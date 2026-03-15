package ui;

import dao.ExamDAO;
import model.Exam;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class ManageExamsPanel extends JPanel {
    private User currentUser;
    private ExamDAO examDAO;
    private JTable examTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    public ManageExamsPanel(User user) {
        this.currentUser = user;
        this.examDAO = new ExamDAO();
        initializeUI();
        loadExams();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_MEDIUM);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // Header with title + search + buttons
        JPanel headerPanel = new JPanel(new BorderLayout(15, 0));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel heading = new JLabel("📝  Manage Exams");
        heading.setFont(UITheme.FONT_SUBTITLE);
        heading.setForeground(UITheme.TEXT_PRIMARY);
        headerPanel.add(heading, BorderLayout.WEST);

        // Center: search field
        JTextField searchField = UITheme.createSearchField("Search exams...");
        searchField.setPreferredSize(new Dimension(250, 38));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                filterTable(searchField.getText());
            }

            public void insertUpdate(DocumentEvent e) {
                filterTable(searchField.getText());
            }

            public void removeUpdate(DocumentEvent e) {
                filterTable(searchField.getText());
            }
        });

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setOpaque(false);
        centerPanel.add(searchField);
        headerPanel.add(centerPanel, BorderLayout.CENTER);

        // Right: action buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setOpaque(false);

        JButton addBtn = UITheme.createPrimaryButton("+ New Exam");
        addBtn.setPreferredSize(new Dimension(140, 38));
        addBtn.addActionListener(e -> showExamDialog(null));
        buttonsPanel.add(addBtn);

        JButton editBtn = UITheme.createOutlineButton("✏ Edit");
        editBtn.setPreferredSize(new Dimension(100, 38));
        editBtn.addActionListener(e -> editSelectedExam());
        buttonsPanel.add(editBtn);

        JButton toggleBtn = UITheme.createAccentButton("⏸ Toggle");
        toggleBtn.setPreferredSize(new Dimension(110, 38));
        toggleBtn.addActionListener(e -> toggleSelectedExamStatus());
        buttonsPanel.add(toggleBtn);

        JButton deleteBtn = UITheme.createDangerButton("🗑 Delete");
        deleteBtn.setPreferredSize(new Dimension(110, 38));
        deleteBtn.addActionListener(e -> deleteSelectedExam());
        buttonsPanel.add(deleteBtn);

        headerPanel.add(buttonsPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columns = { "ID", "Title", "Description", "Duration (min)", "Status", "Created" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        examTable = new JTable(tableModel);
        UITheme.styleTable(examTable);
        examTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        examTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        examTable.getColumnModel().getColumn(2).setPreferredWidth(250);
        examTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        examTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        examTable.getColumnModel().getColumn(5).setPreferredWidth(140);

        sorter = new TableRowSorter<>(tableModel);
        examTable.setRowSorter(sorter);

        add(UITheme.createScrollPane(examTable), BorderLayout.CENTER);
    }

    private void filterTable(String query) {
        if (query == null || query.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query.trim()));
        }
    }

    private void loadExams() {
        tableModel.setRowCount(0);
        List<Exam> exams = examDAO.getAllExams();
        for (Exam e : exams) {
            String desc = e.getDescription() != null ? e.getDescription() : "";
            if (desc.length() > 50)
                desc = desc.substring(0, 50) + "...";
            tableModel.addRow(new Object[] {
                    e.getId(),
                    e.getTitle(),
                    desc,
                    e.getDurationMinutes(),
                    e.isActive() ? "✅ Active" : "⏸ Inactive",
                    e.getCreatedAt() != null ? new java.text.SimpleDateFormat("MMM dd, yyyy").format(e.getCreatedAt())
                            : ""
            });
        }
    }

    private void showExamDialog(Exam existingExam) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.BG_MEDIUM);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        JTextField titleField = new JTextField(existingExam != null ? existingExam.getTitle() : "", 25);
        JTextArea descArea = new JTextArea(existingExam != null ? existingExam.getDescription() : "", 3, 25);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JSpinner durationSpinner = new JSpinner(new SpinnerNumberModel(
                existingExam != null ? existingExam.getDurationMinutes() : 30, 5, 300, 5));
        JCheckBox activeCheck = new JCheckBox("Active", existingExam == null || existingExam.isActive());

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lbl1 = new JLabel("Title:");
        lbl1.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(lbl1, gbc);
        gbc.gridx = 1;
        panel.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lbl2 = new JLabel("Description:");
        lbl2.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(lbl2, gbc);
        gbc.gridx = 1;
        panel.add(new JScrollPane(descArea), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lbl3 = new JLabel("Duration (min):");
        lbl3.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(lbl3, gbc);
        gbc.gridx = 1;
        panel.add(durationSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel lbl4 = new JLabel("Status:");
        lbl4.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(lbl4, gbc);
        gbc.gridx = 1;
        activeCheck.setForeground(UITheme.TEXT_PRIMARY);
        activeCheck.setOpaque(false);
        panel.add(activeCheck, gbc);

        String dialogTitle = existingExam != null ? "✏ Edit Exam" : "📝 Create New Exam";
        int result = JOptionPane.showConfirmDialog(this, panel, dialogTitle,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            String desc = descArea.getText().trim();
            int duration = (int) durationSpinner.getValue();

            if (title.isEmpty()) {
                UITheme.showError(this, "Exam title is required.");
                return;
            }

            if (existingExam != null) {
                existingExam.setTitle(title);
                existingExam.setDescription(desc);
                existingExam.setDurationMinutes(duration);
                existingExam.setActive(activeCheck.isSelected());
                if (examDAO.updateExam(existingExam)) {
                    UITheme.showSuccess(this, "Exam updated successfully!");
                    loadExams();
                } else {
                    UITheme.showError(this, "Failed to update exam.");
                }
            } else {
                Exam newExam = new Exam(title, desc, duration, currentUser.getId());
                newExam.setActive(activeCheck.isSelected());
                if (examDAO.createExam(newExam)) {
                    UITheme.showSuccess(this, "Exam created successfully!");
                    loadExams();
                } else {
                    UITheme.showError(this, "Failed to create exam.");
                }
            }
        }
    }

    private void editSelectedExam() {
        int selectedRow = examTable.getSelectedRow();
        if (selectedRow == -1) {
            UITheme.showError(this, "Please select an exam to edit.");
            return;
        }
        int modelRow = examTable.convertRowIndexToModel(selectedRow);
        int examId = (int) tableModel.getValueAt(modelRow, 0);
        Exam exam = examDAO.getExamById(examId);
        if (exam != null)
            showExamDialog(exam);
    }

    private void deleteSelectedExam() {
        int selectedRow = examTable.getSelectedRow();
        if (selectedRow == -1) {
            UITheme.showError(this, "Please select an exam to delete.");
            return;
        }
        int modelRow = examTable.convertRowIndexToModel(selectedRow);
        int examId = (int) tableModel.getValueAt(modelRow, 0);
        String title = (String) tableModel.getValueAt(modelRow, 1);
        if (UITheme.showConfirm(this,
                "Delete exam \"" + title + "\"?\n\nThis will also delete all questions and results.")) {
            if (examDAO.deleteExam(examId)) {
                UITheme.showSuccess(this, "Exam deleted.");
                loadExams();
            } else {
                UITheme.showError(this, "Failed to delete exam.");
            }
        }
    }

    private void toggleSelectedExamStatus() {
        int selectedRow = examTable.getSelectedRow();
        if (selectedRow == -1) {
            UITheme.showError(this, "Please select an exam.");
            return;
        }
        int modelRow = examTable.convertRowIndexToModel(selectedRow);
        int examId = (int) tableModel.getValueAt(modelRow, 0);
        Exam exam = examDAO.getExamById(examId);
        if (exam != null) {
            exam.setActive(!exam.isActive());
            if (examDAO.updateExam(exam)) {
                loadExams();
            }
        }
    }
}
