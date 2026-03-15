package ui;

import dao.ExamDAO;
import dao.ResultDAO;
import dao.UserDAO;
import model.Result;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.List;

public class AdminOverviewPanel extends JPanel {
    private ExamDAO examDAO = new ExamDAO();
    private ResultDAO resultDAO = new ResultDAO();
    private UserDAO userDAO = new UserDAO();
    private Timer refreshTimer;

    public AdminOverviewPanel() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_MEDIUM);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        buildUI();

        // Auto-refresh every 30 seconds
        refreshTimer = new Timer(30000, e -> refreshStats());
        refreshTimer.start();
    }

    private void buildUI() {
        removeAll();

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 25, 0));

        JLabel welcomeLabel = new JLabel("Dashboard Overview");
        welcomeLabel.setFont(UITheme.FONT_TITLE);
        welcomeLabel.setForeground(UITheme.TEXT_PRIMARY);
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        JLabel subtitleLabel = new JLabel("Monitor your examination system at a glance");
        subtitleLabel.setFont(UITheme.FONT_BODY);
        subtitleLabel.setForeground(UITheme.TEXT_MUTED);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);

        // Stats cards
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 20, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        int examCount = examDAO.getExamCount();
        int studentCount = userDAO.getStudentCount();
        int resultCount = resultDAO.getTotalResultCount();
        double avgScore = resultDAO.getAverageScore();

        statsRow.add(UITheme.createStatCard("📝", String.valueOf(examCount), "Total Exams", UITheme.PRIMARY));
        statsRow.add(UITheme.createStatCard("👥", String.valueOf(studentCount), "Students", UITheme.INFO));
        statsRow.add(UITheme.createStatCard("📊", String.valueOf(resultCount), "Submissions", UITheme.ACCENT));
        statsRow.add(UITheme.createStatCard("🎯", String.format("%.1f%%", avgScore), "Avg Score", UITheme.SUCCESS));

        // Center content
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        centerPanel.add(statsRow);
        centerPanel.add(Box.createVerticalStrut(30));

        // Recent activity
        JPanel activityPanel = new JPanel(new BorderLayout()) {
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
        activityPanel.setOpaque(false);
        activityPanel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel activityTitle = new JLabel("📋  Recent Submissions");
        activityTitle.setFont(UITheme.FONT_HEADING);
        activityTitle.setForeground(UITheme.TEXT_PRIMARY);
        activityTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        activityPanel.add(activityTitle, BorderLayout.NORTH);

        // Recent results list
        JPanel resultsList = new JPanel();
        resultsList.setLayout(new BoxLayout(resultsList, BoxLayout.Y_AXIS));
        resultsList.setOpaque(false);

        List<Result> recentResults = resultDAO.getAllResults();
        int maxShow = Math.min(recentResults.size(), 8);

        if (maxShow == 0) {
            JLabel emptyLabel = new JLabel("No submissions yet. Students haven't taken any exams.");
            emptyLabel.setFont(UITheme.FONT_BODY);
            emptyLabel.setForeground(UITheme.TEXT_MUTED);
            emptyLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
            resultsList.add(emptyLabel);
        } else {
            for (int i = 0; i < maxShow; i++) {
                Result r = recentResults.get(i);
                resultsList.add(createActivityItem(r));
                if (i < maxShow - 1) {
                    JSeparator sep = new JSeparator();
                    sep.setForeground(new Color(50, 50, 70));
                    sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                    resultsList.add(sep);
                }
            }
        }

        activityPanel.add(UITheme.createScrollPane(resultsList), BorderLayout.CENTER);
        centerPanel.add(activityPanel);

        add(centerPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JPanel createActivityItem(Result r) {
        JPanel item = new JPanel(new BorderLayout(12, 0));
        item.setOpaque(false);
        item.setBorder(new EmptyBorder(10, 4, 10, 4));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        double pct = r.getPercentage();
        String grade = UITheme.getGrade(pct);
        Color gradeColor = UITheme.getGradeColor(pct);

        // Left: student name & exam
        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        leftPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(
                "👤  " + (r.getStudentName() != null ? r.getStudentName() : "Student #" + r.getStudentId()));
        nameLabel.setFont(UITheme.FONT_BODY_BOLD);
        nameLabel.setForeground(UITheme.TEXT_PRIMARY);
        leftPanel.add(nameLabel);

        String examTitle = r.getExamTitle() != null ? r.getExamTitle() : "Exam #" + r.getExamId();
        String dateStr = r.getSubmittedAt() != null
                ? new SimpleDateFormat("MMM dd, yyyy HH:mm").format(r.getSubmittedAt())
                : "";
        JLabel detailLabel = new JLabel(examTitle + "  •  " + dateStr);
        detailLabel.setFont(UITheme.FONT_SMALL);
        detailLabel.setForeground(UITheme.TEXT_MUTED);
        leftPanel.add(detailLabel);

        item.add(leftPanel, BorderLayout.CENTER);

        // Right: score badge
        JPanel scorePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        scorePanel.setOpaque(false);

        JLabel scoreLabel = new JLabel(r.getScore() + "/" + r.getTotalMarks());
        scoreLabel.setFont(UITheme.FONT_BODY);
        scoreLabel.setForeground(UITheme.TEXT_SECONDARY);
        scorePanel.add(scoreLabel);

        JLabel gradeLabel = new JLabel(" " + grade + " ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(gradeColor.getRed(), gradeColor.getGreen(), gradeColor.getBlue(), 30));
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        gradeLabel.setFont(UITheme.FONT_SMALL_BOLD);
        gradeLabel.setForeground(gradeColor);
        gradeLabel.setBorder(new EmptyBorder(3, 8, 3, 8));
        gradeLabel.setOpaque(false);
        scorePanel.add(gradeLabel);

        item.add(scorePanel, BorderLayout.EAST);

        return item;
    }

    private void refreshStats() {
        SwingUtilities.invokeLater(this::buildUI);
    }
}

