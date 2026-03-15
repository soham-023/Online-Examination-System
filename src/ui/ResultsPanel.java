package ui;

import dao.ExamDAO;
import dao.ResultDAO;
import model.Exam;
import model.Result;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class ResultsPanel extends JPanel {
    private User currentUser;
    private ResultDAO resultDAO;
    private ExamDAO examDAO;
    private JTable resultsTable;
    private DefaultTableModel tableModel;

    public ResultsPanel(User user) {
        this.currentUser = user;
        this.resultDAO = new ResultDAO();
        this.examDAO = new ExamDAO();
        initializeUI();
        loadResults();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_MEDIUM);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel heading = new JLabel("📊  Exam Results");
        heading.setFont(UITheme.FONT_SUBTITLE);
        heading.setForeground(UITheme.TEXT_PRIMARY);
        headerPanel.add(heading, BorderLayout.WEST);

        // Right: filter by exam + export
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        controlsPanel.setOpaque(false);

        // Exam filter
        JComboBox<String> examFilter = new JComboBox<>();
        examFilter.setFont(UITheme.FONT_BODY);
        examFilter.setPreferredSize(new Dimension(200, 36));
        examFilter.addItem("All Exams");
        List<Exam> exams = examDAO.getAllExams();
        for (Exam e : exams) {
            examFilter.addItem(e.getTitle());
        }
        examFilter.addActionListener(e -> {
            int idx = examFilter.getSelectedIndex();
            if (idx == 0) {
                loadResults();
            } else {
                Exam selectedExam = exams.get(idx - 1);
                loadResultsByExam(selectedExam.getId());
            }
        });
        controlsPanel.add(new JLabel("Filter: ") {
            {
                setForeground(UITheme.TEXT_SECONDARY);
                setFont(UITheme.FONT_BODY);
            }
        });
        controlsPanel.add(examFilter);

        // Export button
        JButton exportBtn = UITheme.createOutlineButton("📥 Export CSV");
        exportBtn.setPreferredSize(new Dimension(130, 36));
        exportBtn.addActionListener(e -> UITheme.exportTableToCSV(resultsTable, this));
        controlsPanel.add(exportBtn);

        // Refresh button
        JButton refreshBtn = UITheme.createGhostButton("🔄 Refresh");
        refreshBtn.setPreferredSize(new Dimension(100, 36));
        refreshBtn.addActionListener(e -> loadResults());
        controlsPanel.add(refreshBtn);

        headerPanel.add(controlsPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Stats panel
        add(createStatsPanel(), BorderLayout.SOUTH);

        // Table
        String[] columns = { "Student", "Exam", "Score", "Total", "Percentage", "Grade", "Date" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        resultsTable = new JTable(tableModel);
        UITheme.styleTable(resultsTable);

        // Custom renderer for Percentage column (with progress bar)
        resultsTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JPanel panel = new JPanel(new BorderLayout(6, 0));
                panel.setBorder(new EmptyBorder(8, 8, 8, 8));
                panel.setBackground(isSelected
                        ? new Color(UITheme.PRIMARY.getRed(), UITheme.PRIMARY.getGreen(), UITheme.PRIMARY.getBlue(), 60)
                        : (row % 2 == 0 ? UITheme.BG_LIGHT : new Color(28, 28, 42)));

                String pctStr = value != null ? value.toString() : "0.0%";
                double pct = 0;
                try {
                    pct = Double.parseDouble(pctStr.replace("%", ""));
                } catch (NumberFormatException ignored) {
                }

                JLabel label = new JLabel(pctStr);
                label.setFont(UITheme.FONT_SMALL_BOLD);
                label.setForeground(UITheme.getGradeColor(pct));
                panel.add(label, BorderLayout.WEST);

                JPanel bar = UITheme.createProgressBar(pct / 100.0, UITheme.getGradeColor(pct));
                bar.setPreferredSize(new Dimension(60, 6));
                panel.add(bar, BorderLayout.CENTER);

                return panel;
            }
        });

        // Custom renderer for Grade column (colored badge)
        resultsTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                String grade = value != null ? value.toString() : "";
                // Get percentage from column 4
                String pctStr = table.getValueAt(row, 4) != null ? table.getValueAt(row, 4).toString() : "0";
                double pct = 0;
                try {
                    pct = Double.parseDouble(pctStr.replace("%", ""));
                } catch (NumberFormatException ignored) {
                }
                Color gradeColor = UITheme.getGradeColor(pct);

                JLabel label = new JLabel(grade, SwingConstants.CENTER) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2d = (Graphics2D) g.create();
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2d.setColor(new Color(gradeColor.getRed(), gradeColor.getGreen(), gradeColor.getBlue(), 30));
                        g2d.fill(new RoundRectangle2D.Float(4, 4, getWidth() - 8, getHeight() - 8, 8, 8));
                        g2d.dispose();
                        super.paintComponent(g);
                    }
                };
                label.setFont(UITheme.FONT_BODY_BOLD);
                label.setForeground(gradeColor);
                label.setOpaque(true);
                label.setBackground(isSelected
                        ? new Color(UITheme.PRIMARY.getRed(), UITheme.PRIMARY.getGreen(), UITheme.PRIMARY.getBlue(), 60)
                        : (row % 2 == 0 ? UITheme.BG_LIGHT : new Color(28, 28, 42)));
                return label;
            }
        });

        add(UITheme.createScrollPane(resultsTable), BorderLayout.CENTER);
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 8));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(12, 0, 0, 0));

        // Will be populated after loading results
        statsPanel.setName("statsPanel");
        return statsPanel;
    }

    private void updateStats(List<Result> results) {
        // Find the stats panel
        for (Component c : getComponents()) {
            if (c instanceof JPanel && "statsPanel".equals(c.getName())) {
                JPanel statsPanel = (JPanel) c;
                statsPanel.removeAll();

                int total = results.size();
                double avg = results.stream().mapToDouble(Result::getPercentage).average().orElse(0);
                double max = results.stream().mapToDouble(Result::getPercentage).max().orElse(0);
                double min = results.stream().mapToDouble(Result::getPercentage).min().orElse(0);

                statsPanel.add(createStatLabel("📋 Total: " + total));
                statsPanel.add(createStatLabel(String.format("📊 Avg: %.1f%%", avg)));
                statsPanel.add(createStatLabel(String.format("🏆 Best: %.1f%%", max)));
                statsPanel.add(createStatLabel(String.format("📉 Lowest: %.1f%%", min)));

                statsPanel.revalidate();
                statsPanel.repaint();
                break;
            }
        }
    }

    private JLabel createStatLabel(String text) {
        JLabel label = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(UITheme.BG_LIGHT);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        label.setFont(UITheme.FONT_SMALL_BOLD);
        label.setForeground(UITheme.TEXT_SECONDARY);
        label.setBorder(new EmptyBorder(6, 14, 6, 14));
        label.setOpaque(false);
        return label;
    }

    private void loadResults() {
        tableModel.setRowCount(0);
        List<Result> results;
        if (currentUser.isTeacher()) {
            results = resultDAO.getAllResults();
        } else {
            results = resultDAO.getResultsByStudent(currentUser.getId());
        }
        populateTable(results);
        updateStats(results);
    }

    private void loadResultsByExam(int examId) {
        tableModel.setRowCount(0);
        List<Result> results = resultDAO.getResultsByExam(examId);
        populateTable(results);
        updateStats(results);
    }

    private void populateTable(List<Result> results) {
        tableModel.setRowCount(0);
        for (Result r : results) {
            double pct = r.getPercentage();
            String grade = UITheme.getGrade(pct);
            String date = r.getSubmittedAt() != null
                    ? new java.text.SimpleDateFormat("MMM dd, yyyy HH:mm").format(r.getSubmittedAt())
                    : "";

            tableModel.addRow(new Object[] {
                    r.getStudentName() != null ? r.getStudentName() : "Student #" + r.getStudentId(),
                    r.getExamTitle() != null ? r.getExamTitle() : "Exam #" + r.getExamId(),
                    r.getScore(),
                    r.getTotalMarks(),
                    String.format("%.1f%%", pct),
                    grade,
                    date
            });
        }
    }
}
