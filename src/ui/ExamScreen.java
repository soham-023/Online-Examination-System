package ui;

import dao.QuestionDAO;
import dao.ResultDAO;
import model.Exam;
import model.Question;
import model.Result;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExamScreen extends JFrame {
    private User currentUser;
    private Exam exam;
    private List<Question> questions;
    private Map<Integer, String> answers;
    private int currentQuestionIndex = 0;
    private Timer timer;
    private int remainingSeconds;

    // UI components
    private JLabel timerLabel;
    private JLabel questionNumberLabel;
    private JLabel questionTextLabel;
    private JPanel optionsPanel;
    private ButtonGroup optionGroup;
    private JRadioButton[] optionButtons;
    private JPanel questionNavPanel;
    private JPanel progressBarPanel;
    private JButton prevBtn, nextBtn, submitBtn;

    public ExamScreen(User user, Exam exam) {
        this.currentUser = user;
        this.exam = exam;
        this.questions = new QuestionDAO().getQuestionsByExam(exam.getId());
        this.answers = new HashMap<>();
        this.remainingSeconds = exam.getDurationMinutes() * 60;
        initializeUI();
        displayQuestion(0);
        startTimer();
    }

    private void initializeUI() {
        setTitle("TestNova — " + exam.getTitle());
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (UITheme.showConfirm(ExamScreen.this,
                        "Are you sure you want to leave?\nYour progress will be submitted.")) {
                    submitExam();
                }
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BG_MEDIUM);

        mainPanel.add(createExamTopBar(), BorderLayout.NORTH);

        // Center: question panel
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBackground(UITheme.BG_MEDIUM);
        centerWrapper.setBorder(new EmptyBorder(20, 30, 20, 30));

        centerWrapper.add(createQuestionPanel(), BorderLayout.CENTER);
        centerWrapper.add(createNavigationPanel(), BorderLayout.SOUTH);

        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        // Right: question navigator
        mainPanel.add(createQuestionNavPanel(), BorderLayout.EAST);

        setContentPane(mainPanel);
    }

    private JPanel createExamTopBar() {
        JPanel topBar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(
                        0, 0, UITheme.PRIMARY_DARK,
                        getWidth(), 0, new Color(8, 80, 72));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        topBar.setPreferredSize(new Dimension(0, 70));
        topBar.setBorder(new EmptyBorder(10, 24, 10, 24));

        // Left: exam title
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        JLabel examTitle = new JLabel("📝  " + exam.getTitle());
        examTitle.setFont(UITheme.FONT_SUBTITLE);
        examTitle.setForeground(UITheme.TEXT_PRIMARY);
        examTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(examTitle);

        // Progress bar
        progressBarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 20));
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 6, 6));

                int answered = (int) answers.values().stream().filter(v -> v != null).count();
                double progress = questions.isEmpty() ? 0 : (double) answered / questions.size();
                int fillWidth = (int) (getWidth() * progress);
                if (fillWidth > 0) {
                    g2d.setColor(UITheme.ACCENT);
                    g2d.fill(new RoundRectangle2D.Float(0, 0, fillWidth, getHeight(), 6, 6));
                }
                g2d.dispose();
            }
        };
        progressBarPanel.setOpaque(false);
        progressBarPanel.setPreferredSize(new Dimension(300, 6));
        progressBarPanel.setMaximumSize(new Dimension(300, 6));
        progressBarPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(Box.createVerticalStrut(6));
        leftPanel.add(progressBarPanel);

        topBar.add(leftPanel, BorderLayout.WEST);

        // Right: timer
        JPanel timerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0, 0, 0, 40));
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2d.dispose();
            }
        };
        timerPanel.setOpaque(false);
        timerPanel.setBorder(new EmptyBorder(6, 16, 6, 16));

        JLabel clockIcon = new JLabel("⏱ ");
        clockIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        timerPanel.add(clockIcon);

        timerLabel = new JLabel();
        timerLabel.setFont(UITheme.FONT_TIMER);
        timerLabel.setForeground(UITheme.TEXT_PRIMARY);
        timerPanel.add(timerLabel);

        topBar.add(timerPanel, BorderLayout.EAST);
        return topBar;
    }

    private JPanel createQuestionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // Question number
        questionNumberLabel = new JLabel();
        questionNumberLabel.setFont(UITheme.FONT_BODY_BOLD);
        questionNumberLabel.setForeground(UITheme.PRIMARY_LIGHT);
        questionNumberLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(questionNumberLabel, BorderLayout.NORTH);

        // Question text in a card
        JPanel questionCard = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                UITheme.paintShadow(g2d, 0, 2, getWidth(), getHeight(), 16, 6);
                g2d.setColor(UITheme.BG_LIGHT);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2d.dispose();
            }
        };
        questionCard.setOpaque(false);
        questionCard.setBorder(new EmptyBorder(24, 24, 24, 24));

        questionTextLabel = new JLabel();
        questionTextLabel.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        questionTextLabel.setForeground(UITheme.TEXT_PRIMARY);
        questionCard.add(questionTextLabel, BorderLayout.NORTH);

        // Options
        optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setOpaque(false);
        optionsPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        optionGroup = new ButtonGroup();
        optionButtons = new JRadioButton[4];
        String[] labels = { "A", "B", "C", "D" };

        for (int i = 0; i < 4; i++) {
            final int idx = i;
            final String label = labels[i];
            optionButtons[i] = new JRadioButton() {
                private float hoverProg = 0;
                private Timer hoverTimer;
                {
                    addMouseListener(new MouseAdapter() {
                        public void mouseEntered(MouseEvent e) {
                            if (hoverTimer != null)
                                hoverTimer.stop();
                            hoverTimer = new Timer(16, ev -> {
                                hoverProg = Math.min(1, hoverProg + 0.12f);
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
                                hoverProg = Math.max(0, hoverProg - 0.12f);
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

                    Color bg;
                    Color border;
                    if (isSelected()) {
                        bg = new Color(UITheme.PRIMARY.getRed(), UITheme.PRIMARY.getGreen(),
                                UITheme.PRIMARY.getBlue(), 30);
                        border = UITheme.PRIMARY;
                    } else {
                        int alpha = (int) (20 * hoverProg);
                        bg = new Color(UITheme.BG_HOVER.getRed(), UITheme.BG_HOVER.getGreen(),
                                UITheme.BG_HOVER.getBlue(), alpha + 10);
                        border = new Color(UITheme.BG_CARD.getRed(), UITheme.BG_CARD.getGreen(),
                                UITheme.BG_CARD.getBlue(),
                                (int) (200 + 55 * hoverProg));
                    }

                    g2d.setColor(bg);
                    g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                    g2d.setColor(border);
                    g2d.setStroke(new BasicStroke(isSelected() ? 2 : 1));
                    g2d.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 12, 12));

                    // Label badge
                    g2d.setColor(isSelected() ? UITheme.PRIMARY : UITheme.TEXT_MUTED);
                    g2d.setFont(UITheme.FONT_BODY_BOLD);
                    g2d.drawString(label + ".", 16, getHeight() / 2 + 5);

                    // Option text
                    g2d.setColor(isSelected() ? UITheme.TEXT_PRIMARY : UITheme.TEXT_SECONDARY);
                    g2d.setFont(UITheme.FONT_BODY);
                    g2d.drawString(getText(), 44, getHeight() / 2 + 5);
                    g2d.dispose();
                }
            };
            optionButtons[i].setFont(UITheme.FONT_BODY);
            optionButtons[i].setPreferredSize(new Dimension(600, 50));
            optionButtons[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            optionButtons[i].setOpaque(false);
            optionButtons[i].setBorderPainted(false);
            optionButtons[i].setFocusPainted(false);
            optionButtons[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            optionButtons[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            optionButtons[i].addActionListener(e -> {
                answers.put(questions.get(currentQuestionIndex).getId(), label);
                updateQuestionNav();
                progressBarPanel.repaint();
            });
            optionGroup.add(optionButtons[i]);
            optionsPanel.add(optionButtons[i]);
            if (i < 3)
                optionsPanel.add(Box.createVerticalStrut(8));
        }

        questionCard.add(optionsPanel, BorderLayout.CENTER);
        panel.add(questionCard, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                // transparent
            }
        };
        navPanel.setOpaque(false);
        navPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        prevBtn = UITheme.createOutlineButton("← Previous");
        prevBtn.setPreferredSize(new Dimension(140, 42));
        prevBtn.addActionListener(e -> navigateQuestion(-1));
        navPanel.add(prevBtn, BorderLayout.WEST);

        // Center: question counter
        JLabel counterLabel = new JLabel("", SwingConstants.CENTER);
        counterLabel.setFont(UITheme.FONT_BODY);
        counterLabel.setForeground(UITheme.TEXT_MUTED);
        navPanel.add(counterLabel, BorderLayout.CENTER);

        JPanel rightBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightBtns.setOpaque(false);

        nextBtn = UITheme.createPrimaryButton("Next →");
        nextBtn.setPreferredSize(new Dimension(140, 42));
        nextBtn.addActionListener(e -> navigateQuestion(1));
        rightBtns.add(nextBtn);

        submitBtn = UITheme.createAccentButton("📤 Submit");
        submitBtn.setPreferredSize(new Dimension(140, 42));
        submitBtn.addActionListener(e -> {
            int answered = (int) answers.values().stream().filter(v -> v != null).count();
            int unanswered = questions.size() - answered;
            String msg = "Submit this exam?\n\n" +
                    "✅ Answered: " + answered + "\n" +
                    "❌ Unanswered: " + unanswered;
            if (UITheme.showConfirm(this, msg)) {
                submitExam();
            }
        });
        rightBtns.add(submitBtn);

        navPanel.add(rightBtns, BorderLayout.EAST);
        return navPanel;
    }

    private JPanel createQuestionNavPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UITheme.BG_DARK);
        wrapper.setPreferredSize(new Dimension(220, 0));
        wrapper.setBorder(new EmptyBorder(20, 16, 20, 16));

        JLabel navTitle = new JLabel("Question Navigator");
        navTitle.setFont(UITheme.FONT_BODY_BOLD);
        navTitle.setForeground(UITheme.TEXT_PRIMARY);
        navTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        wrapper.add(navTitle, BorderLayout.NORTH);

        questionNavPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        questionNavPanel.setOpaque(false);

        for (int i = 0; i < questions.size(); i++) {
            final int idx = i;
            JButton qBtn = new JButton(String.valueOf(i + 1)) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    Color bg;
                    if (idx == currentQuestionIndex) {
                        bg = UITheme.PRIMARY;
                    } else if (answers.containsKey(questions.get(idx).getId())) {
                        bg = UITheme.SUCCESS;
                    } else {
                        bg = UITheme.BG_CARD;
                    }
                    g2d.setColor(bg);
                    g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));

                    g2d.setColor(UITheme.TEXT_PRIMARY);
                    g2d.setFont(UITheme.FONT_SMALL_BOLD);
                    FontMetrics fm = g2d.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(getText())) / 2;
                    int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                    g2d.drawString(getText(), x, y);
                    g2d.dispose();
                }
            };
            qBtn.setPreferredSize(new Dimension(38, 38));
            qBtn.setBorderPainted(false);
            qBtn.setContentAreaFilled(false);
            qBtn.setFocusPainted(false);
            qBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            qBtn.addActionListener(e -> displayQuestion(idx));
            questionNavPanel.add(qBtn);
        }

        JScrollPane navScroll = UITheme.createScrollPane(questionNavPanel);
        navScroll.setBackground(UITheme.BG_DARK);
        navScroll.getViewport().setBackground(UITheme.BG_DARK);
        wrapper.add(navScroll, BorderLayout.CENTER);

        // Legend
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        legendPanel.setOpaque(false);
        legendPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        legendPanel.add(createLegendItem(UITheme.PRIMARY, "Current"));
        legendPanel.add(createLegendItem(UITheme.SUCCESS, "Answered"));
        legendPanel.add(createLegendItem(UITheme.BG_CARD, "Unanswered"));
        wrapper.add(legendPanel, BorderLayout.SOUTH);

        return wrapper;
    }

    private JPanel createLegendItem(Color color, String text) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        item.setOpaque(false);
        JLabel dot = new JLabel("●");
        dot.setFont(UITheme.FONT_SMALL);
        dot.setForeground(color);
        item.add(dot);
        JLabel label = new JLabel(text);
        label.setFont(UITheme.FONT_SMALL);
        label.setForeground(UITheme.TEXT_MUTED);
        item.add(label);
        return item;
    }

    private void updateQuestionNav() {
        for (int i = 0; i < questionNavPanel.getComponentCount(); i++) {
            questionNavPanel.getComponent(i).repaint();
        }
    }

    private void displayQuestion(int index) {
        if (index < 0 || index >= questions.size())
            return;
        currentQuestionIndex = index;
        Question q = questions.get(index);

        questionNumberLabel.setText("Question " + (index + 1) + " of " + questions.size() +
                "    |    " + q.getMarks() + " mark" + (q.getMarks() > 1 ? "s" : ""));

        questionTextLabel.setText("<html><div style='width:500px'>" + q.getQuestionText() + "</div></html>");

        String[] options = { q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD() };
        String[] labels = { "A", "B", "C", "D" };
        optionGroup.clearSelection();

        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText(options[i]);
            if (answers.containsKey(q.getId()) && answers.get(q.getId()).equals(labels[i])) {
                optionButtons[i].setSelected(true);
            }
        }

        prevBtn.setEnabled(index > 0);
        nextBtn.setEnabled(index < questions.size() - 1);

        updateQuestionNav();
    }

    private void navigateQuestion(int direction) {
        displayQuestion(currentQuestionIndex + direction);
    }

    private void startTimer() {
        updateTimerDisplay();
        timer = new Timer(1000, e -> {
            remainingSeconds--;
            updateTimerDisplay();

            if (remainingSeconds <= 0) {
                timer.stop();
                JOptionPane.showMessageDialog(this,
                        "⏰ Time's up! Your exam will be submitted automatically.",
                        "Time Up", JOptionPane.WARNING_MESSAGE);
                submitExam();
            }
        });
        timer.start();
    }

    private void updateTimerDisplay() {
        int mins = remainingSeconds / 60;
        int secs = remainingSeconds % 60;
        timerLabel.setText(String.format("%02d:%02d", mins, secs));

        // Color changes based on remaining time
        double totalSeconds = exam.getDurationMinutes() * 60.0;
        double pct = remainingSeconds / totalSeconds;

        if (pct > 0.5) {
            timerLabel.setForeground(UITheme.SUCCESS);
        } else if (pct > 0.2) {
            timerLabel.setForeground(UITheme.WARNING);
        } else {
            timerLabel.setForeground(UITheme.CLR_ERROR);
            // Pulse effect for last 20%
            if (remainingSeconds % 2 == 0) {
                timerLabel.setForeground(UITheme.CLR_ERROR);
            } else {
                timerLabel.setForeground(UITheme.CLR_ERROR_LIGHT);
            }
        }
    }

    private void submitExam() {
        if (timer != null)
            timer.stop();

        int score = 0;
        int totalMarks = 0;

        for (Question q : questions) {
            totalMarks += q.getMarks();
            String answer = answers.get(q.getId());
            if (answer != null && answer.equals(q.getCorrectOption())) {
                score += q.getMarks();
            }
        }

        Result result = new Result(currentUser.getId(), exam.getId(), score, totalMarks);
        ResultDAO resultDAO = new ResultDAO();
        resultDAO.saveResult(result);

        double pct = totalMarks > 0 ? (score * 100.0) / totalMarks : 0;
        String grade = UITheme.getGrade(pct);

        int answered = (int) answers.values().stream().filter(v -> v != null).count();

        // Rich result dialog
        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        resultPanel.add(new JLabel("📊 Exam Completed!") {
            {
                setFont(UITheme.FONT_SUBTITLE);
            }
        });
        resultPanel.add(Box.createVerticalStrut(15));
        resultPanel.add(new JLabel(String.format("Score: %d / %d  (%.1f%%)", score, totalMarks, pct)));
        resultPanel.add(Box.createVerticalStrut(5));
        resultPanel.add(new JLabel("Grade: " + grade));
        resultPanel.add(Box.createVerticalStrut(5));
        resultPanel.add(new JLabel("Questions answered: " + answered + " / " + questions.size()));

        JOptionPane.showMessageDialog(this, resultPanel,
                "Exam Results", JOptionPane.INFORMATION_MESSAGE);

        dispose();
        new StudentDashboard(currentUser).setVisible(true);
    }
}
