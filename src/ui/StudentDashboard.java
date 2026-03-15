package ui;

import dao.ExamDAO;
import dao.QuestionDAO;
import dao.ResultDAO;
import model.Exam;
import model.Result;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.List;

public class StudentDashboard extends JFrame {
    private User currentUser;
    private JPanel examCardsPanel;
    private ExamDAO examDAO;
    private QuestionDAO questionDAO;
    private ResultDAO resultDAO;

    public StudentDashboard(User user) {
        this.currentUser = user;
        this.examDAO = new ExamDAO();
        this.questionDAO = new QuestionDAO();
        this.resultDAO = new ResultDAO();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("TestNova — Student Dashboard");
        setSize(1200, 780);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 650));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BG_MEDIUM);

        mainPanel.add(createTopBar(), BorderLayout.NORTH);

        // Tabbed content
        JTabbedPane tabbedPane = new JTabbedPane() {
            @Override
            public void updateUI() {
                super.updateUI();
                setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
                    @Override
                    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                                      int x, int y, int w, int h, boolean isSelected) {
                        Graphics2D g2d = (Graphics2D) g.create();
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        if (isSelected) {
                            g2d.setColor(UITheme.PRIMARY_DARK);
                        } else {
                            g2d.setColor(UITheme.BG_DARK);
                        }
                        g2d.fill(new RoundRectangle2D.Float(x + 2, y + 2, w - 4, h - 2, 10, 10));
                        g2d.dispose();
                    }

                    @Override
                    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                                  int x, int y, int w, int h, boolean isSelected) {
                        if (isSelected) {
                            Graphics2D g2d = (Graphics2D) g.create();
                            g2d.setColor(UITheme.PRIMARY);
                            g2d.fillRect(x + 8, y + h - 3, w - 16, 3);
                            g2d.dispose();
                        }
                    }

                    @Override
                    protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects,
                                                       int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
                    }

                    @Override
                    protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
                    }
                });
            }
        };
        tabbedPane.setFont(UITheme.FONT_BODY_BOLD);
        tabbedPane.setForeground(UITheme.TEXT_PRIMARY);
        tabbedPane.setBackground(UITheme.BG_MEDIUM);

        tabbedPane.addTab("📝  Available Exams", createExamsPanel());
        tabbedPane.addTab("📊  My Results", createResultsPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(8, 80, 72),
                        getWidth(), 0, UITheme.PRIMARY_DARK);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(new Color(255, 255, 255, 10));
                g2d.fillRect(0, getHeight() - 1, getWidth(), 1);
                g2d.dispose();
            }
        };
        topBar.setPreferredSize(new Dimension(0, 64));
        topBar.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));

        JLabel titleLabel = new JLabel("📋  TestNova — Student Portal");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        topBar.add(titleLabel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 0));
        rightPanel.setOpaque(false);

        // Student stats in top bar
        List<Result> myResults = resultDAO.getResultsByStudent(currentUser.getId());
        double avgScore = 0;
        if (!myResults.isEmpty()) {
            avgScore = myResults.stream().mapToDouble(Result::getPercentage).average().orElse(0);
        }
        JLabel statsLabel = new JLabel(String.format("📈 %d exams taken  •  %.0f%% avg", myResults.size(), avgScore));
        statsLabel.setFont(UITheme.FONT_SMALL_BOLD);
        statsLabel.setForeground(new Color(180, 230, 220));
        rightPanel.add(statsLabel);

        JLabel userLabel = new JLabel("👤 " + currentUser.getFullName());
        userLabel.setFont(UITheme.FONT_BODY_BOLD);
        userLabel.setForeground(UITheme.TEXT_PRIMARY);
        rightPanel.add(userLabel);

        JButton logoutBtn = UITheme.createDangerButton("Logout");
        logoutBtn.setPreferredSize(new Dimension(100, 34));
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginScreen().setVisible(true);
        });
        rightPanel.add(logoutBtn);

        topBar.add(rightPanel, BorderLayout.EAST);
        return topBar;
    }

    private JPanel createExamsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_MEDIUM);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel heading = new JLabel("Available Exams");
        heading.setFont(UITheme.FONT_SUBTITLE);
        heading.setForeground(UITheme.TEXT_PRIMARY);
        headerPanel.add(heading, BorderLayout.WEST);

        JButton refreshBtn = UITheme.createGhostButton("🔄 Refresh");
        refreshBtn.addActionListener(e -> refreshExams());
        headerPanel.add(refreshBtn, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);

        examCardsPanel = new JPanel();
        examCardsPanel.setLayout(new BoxLayout(examCardsPanel, BoxLayout.Y_AXIS));
        examCardsPanel.setOpaque(false);

        JScrollPane scrollPane = UITheme.createScrollPane(examCardsPanel);
        scrollPane.getViewport().setBackground(UITheme.BG_MEDIUM);
        panel.add(scrollPane, BorderLayout.CENTER);

        refreshExams();
        return panel;
    }

    private void refreshExams() {
        examCardsPanel.removeAll();
        List<Exam> exams = examDAO.getActiveExams();

        if (exams.isEmpty()) {
            JLabel emptyLabel = new JLabel("No exams available at the moment.");
            emptyLabel.setFont(UITheme.FONT_BODY);
            emptyLabel.setForeground(UITheme.TEXT_MUTED);
            emptyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            emptyLabel.setBorder(new EmptyBorder(40, 0, 0, 0));
            examCardsPanel.add(emptyLabel);
        } else {
            for (Exam exam : exams) {
                examCardsPanel.add(createExamCard(exam));
                examCardsPanel.add(Box.createVerticalStrut(14));
            }
        }
        examCardsPanel.revalidate();
        examCardsPanel.repaint();
    }

    private JPanel createExamCard(Exam exam) {
        boolean taken = resultDAO.hasStudentTakenExam(currentUser.getId(), exam.getId());
        int questionCount = questionDAO.getQuestionsByExam(exam.getId()).size();

        JPanel card = new JPanel(new BorderLayout(16, 0)) {
            private float hoverProg = 0;
            private Timer hoverTimer;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        if (hoverTimer != null)
                            hoverTimer.stop();
                        hoverTimer = new Timer(16, ev -> {
                            hoverProg = Math.min(1, hoverProg + 0.1f);
                            repaint();
                            if (hoverProg >= 1)
                                ((Timer) ev.getSource()).stop();
                        });
                        hoverTimer.start();
                    }

                    public void mouseExited(MouseEvent e) {
                        if (hoverTimer != null)
                            hoverTimer.stop();
                        hoverTimer = new Timer(16, ev -> {
                            hoverProg = Math.max(0, hoverProg - 0.1f);
                            repaint();
                            if (hoverProg <= 0)
                                ((Timer) ev.getSource()).stop();
                        });
                        hoverTimer.start();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow grows on hover
                int shadowSpread = (int) (6 + 6 * hoverProg);
                UITheme.paintShadow(g2d, 0, 2, getWidth(), getHeight(), 16, shadowSpread);

                // Card background — slightly lighter on hover
                int bgR = UITheme.BG_LIGHT.getRed() + (int) (8 * hoverProg);
                int bgG = UITheme.BG_LIGHT.getGreen() + (int) (8 * hoverProg);
                int bgB = UITheme.BG_LIGHT.getBlue() + (int) (8 * hoverProg);
                g2d.setColor(new Color(bgR, bgG, bgB));
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));

                // Left accent bar
                Color barColor = taken ? UITheme.TEXT_MUTED : UITheme.PRIMARY;
                g2d.setColor(barColor);
                g2d.fill(new RoundRectangle2D.Float(0, 0, 5, getHeight(), 4, 4));

                g2d.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(18, 22, 18, 22));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Left info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(exam.getTitle());
        titleLabel.setFont(UITheme.FONT_HEADING);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(4));

        if (exam.getDescription() != null && !exam.getDescription().isEmpty()) {
            String desc = exam.getDescription().length() > 80
                    ? exam.getDescription().substring(0, 80) + "..."
                    : exam.getDescription();
            JLabel descLabel = new JLabel(desc);
            descLabel.setFont(UITheme.FONT_BODY);
            descLabel.setForeground(UITheme.TEXT_MUTED);
            descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            infoPanel.add(descLabel);
            infoPanel.add(Box.createVerticalStrut(6));
        }

        JLabel metaLabel = new JLabel(String.format("⏱ %d min  •  📝 %d questions",
                exam.getDurationMinutes(), questionCount));
        metaLabel.setFont(UITheme.FONT_SMALL);
        metaLabel.setForeground(UITheme.TEXT_SECONDARY);
        metaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(metaLabel);

        card.add(infoPanel, BorderLayout.CENTER);

        // Right button
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
        actionPanel.setOpaque(false);

        if (taken) {
            JLabel doneLabel = new JLabel("✅ Completed") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(new Color(34, 197, 94, 25));
                    g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                    g2d.dispose();
                    super.paintComponent(g);
                }
            };
            doneLabel.setFont(UITheme.FONT_SMALL_BOLD);
            doneLabel.setForeground(UITheme.SUCCESS);
            doneLabel.setBorder(new EmptyBorder(6, 14, 6, 14));
            doneLabel.setOpaque(false);
            actionPanel.add(doneLabel);
        } else if (questionCount == 0) {
            JLabel noQLabel = new JLabel("No Questions");
            noQLabel.setFont(UITheme.FONT_SMALL);
            noQLabel.setForeground(UITheme.TEXT_MUTED);
            actionPanel.add(noQLabel);
        } else {
            JButton startBtn = UITheme.createAccentButton("▶ Start Exam");
            startBtn.setPreferredSize(new Dimension(140, 40));
            startBtn.addActionListener(e -> {
                if (UITheme.showConfirm(this,
                        "Start \"" + exam.getTitle() + "\"?\n\n" +
                                "⏱ Duration: " + exam.getDurationMinutes() + " minutes\n" +
                                "📝 Questions: " + questionCount + "\n\n" +
                                "The timer will start immediately.")) {
                    dispose();
                    new ExamScreen(currentUser, exam).setVisible(true);
                }
            });
            actionPanel.add(startBtn);
        }

        card.add(actionPanel, BorderLayout.EAST);
        return card;
    }

    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_MEDIUM);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel heading = new JLabel("📊  My Results");
        heading.setFont(UITheme.FONT_SUBTITLE);
        heading.setForeground(UITheme.TEXT_PRIMARY);
        heading.setBorder(new EmptyBorder(0, 0, 20, 0));
        panel.add(heading, BorderLayout.NORTH);

        List<Result> results = resultDAO.getResultsByStudent(currentUser.getId());

        if (results.isEmpty()) {
            JLabel emptyLabel = new JLabel("No results yet. Take an exam to see your scores!");
            emptyLabel.setFont(UITheme.FONT_BODY);
            emptyLabel.setForeground(UITheme.TEXT_MUTED);
            emptyLabel.setBorder(new EmptyBorder(40, 0, 0, 0));
            panel.add(emptyLabel, BorderLayout.CENTER);
        } else {
            JPanel resultsList = new JPanel();
            resultsList.setLayout(new BoxLayout(resultsList, BoxLayout.Y_AXIS));
            resultsList.setOpaque(false);

            for (Result r : results) {
                resultsList.add(createResultCard(r));
                resultsList.add(Box.createVerticalStrut(12));
            }

            JScrollPane scrollPane = UITheme.createScrollPane(resultsList);
            scrollPane.getViewport().setBackground(UITheme.BG_MEDIUM);
            panel.add(scrollPane, BorderLayout.CENTER);
        }

        return panel;
    }

    private JPanel createResultCard(Result r) {
        double pct = r.getPercentage();
        Color gradeColor = UITheme.getGradeColor(pct);
        String grade = UITheme.getGrade(pct);

        JPanel card = new JPanel(new BorderLayout(16, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                UITheme.paintShadow(g2d, 0, 2, getWidth(), getHeight(), 14, 5);
                g2d.setColor(UITheme.BG_LIGHT);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                // Grade color accent
                g2d.setColor(gradeColor);
                g2d.fill(new RoundRectangle2D.Float(0, 0, 4, getHeight(), 4, 4));
                g2d.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(14, 20, 14, 20));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Left: exam info
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(r.getExamTitle() != null ? r.getExamTitle() : "Exam #" + r.getExamId());
        titleLabel.setFont(UITheme.FONT_BODY_BOLD);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(titleLabel);

        String dateStr = r.getSubmittedAt() != null
                ? new SimpleDateFormat("MMM dd, yyyy  •  hh:mm a").format(r.getSubmittedAt())
                : "";
        JLabel dateLabel = new JLabel(dateStr);
        dateLabel.setFont(UITheme.FONT_SMALL);
        dateLabel.setForeground(UITheme.TEXT_MUTED);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(dateLabel);

        card.add(leftPanel, BorderLayout.CENTER);

        // Right: score + progress bar + grade
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(180, 60));

        JPanel scoreRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        scoreRow.setOpaque(false);

        JLabel scoreLabel = new JLabel(String.format("%d/%d (%.0f%%)", r.getScore(), r.getTotalMarks(), pct));
        scoreLabel.setFont(UITheme.FONT_BODY_BOLD);
        scoreLabel.setForeground(UITheme.TEXT_PRIMARY);
        scoreRow.add(scoreLabel);

        JLabel gradeLabel = new JLabel(" " + grade + " ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(gradeColor.getRed(), gradeColor.getGreen(), gradeColor.getBlue(), 35));
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        gradeLabel.setFont(UITheme.FONT_SMALL_BOLD);
        gradeLabel.setForeground(gradeColor);
        gradeLabel.setBorder(new EmptyBorder(2, 8, 2, 8));
        gradeLabel.setOpaque(false);
        scoreRow.add(gradeLabel);

        rightPanel.add(scoreRow);
        rightPanel.add(Box.createVerticalStrut(6));

        JPanel progressBar = UITheme.createProgressBar(pct / 100.0, gradeColor);
        progressBar.setAlignmentX(Component.RIGHT_ALIGNMENT);
        rightPanel.add(progressBar);

        card.add(rightPanel, BorderLayout.EAST);

        return card;
    }
}
