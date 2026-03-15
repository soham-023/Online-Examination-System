package ui;

import dao.ExamDAO;
import dao.QuestionDAO;
import model.Exam;
import model.Question;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageQuestionsPanel extends JPanel {
    private ExamDAO examDAO;
    private QuestionDAO questionDAO;
    private JComboBox<ExamItem> examCombo;
    private JTable questionTable;
    private DefaultTableModel tableModel;

    // Wrapper class for exam combo box
    private static class ExamItem {
        Exam exam;

        ExamItem(Exam exam) {
            this.exam = exam;
        }

        @Override
        public String toString() {
            return exam.getTitle() + " (ID: " + exam.getId() + ")";
        }
    }

    public ManageQuestionsPanel() {
        this.examDAO = new ExamDAO();
        this.questionDAO = new QuestionDAO();
        initializeUI();
        loadExams();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_MEDIUM);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout(15, 0));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 18, 0));

        JLabel heading = new JLabel("❓  Manage Questions");
        heading.setFont(UITheme.FONT_SUBTITLE);
        heading.setForeground(UITheme.TEXT_PRIMARY);
        headerPanel.add(heading, BorderLayout.WEST);

        // Exam selector + buttons
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        controlsPanel.setOpaque(false);

        JLabel selectLabel = new JLabel("Exam:");
        selectLabel.setFont(UITheme.FONT_BODY);
        selectLabel.setForeground(UITheme.TEXT_SECONDARY);
        controlsPanel.add(selectLabel);

        examCombo = new JComboBox<>();
        examCombo.setFont(UITheme.FONT_BODY);
        examCombo.setPreferredSize(new Dimension(220, 38));
        examCombo.addActionListener(e -> loadQuestions());
        controlsPanel.add(examCombo);

        JButton addBtn = UITheme.createPrimaryButton("+ Add Question");
        addBtn.setPreferredSize(new Dimension(150, 38));
        addBtn.addActionListener(e -> showQuestionDialog(null));
        controlsPanel.add(addBtn);

        JButton editBtn = UITheme.createOutlineButton("✏ Edit");
        editBtn.setPreferredSize(new Dimension(90, 38));
        editBtn.addActionListener(e -> editSelectedQuestion());
        controlsPanel.add(editBtn);

        JButton deleteBtn = UITheme.createDangerButton("🗑 Delete");
        deleteBtn.setPreferredSize(new Dimension(100, 38));
        deleteBtn.addActionListener(e -> deleteSelectedQuestion());
        controlsPanel.add(deleteBtn);

        headerPanel.add(controlsPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columns = { "ID", "Question", "Option A", "Option B", "Option C", "Option D", "Answer", "Marks" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        questionTable = new JTable(tableModel);
        UITheme.styleTable(questionTable);
        questionTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        questionTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        questionTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        questionTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        questionTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        questionTable.getColumnModel().getColumn(5).setPreferredWidth(120);
        questionTable.getColumnModel().getColumn(6).setPreferredWidth(60);
        questionTable.getColumnModel().getColumn(7).setPreferredWidth(50);

        add(UITheme.createScrollPane(questionTable), BorderLayout.CENTER);
    }

    private void loadExams() {
        examCombo.removeAllItems();
        List<Exam> exams = examDAO.getAllExams();
        for (Exam e : exams) {
            examCombo.addItem(new ExamItem(e));
        }
    }

    private void loadQuestions() {
        tableModel.setRowCount(0);
        ExamItem selected = (ExamItem) examCombo.getSelectedItem();
        if (selected == null)
            return;

        List<Question> questions = questionDAO.getQuestionsByExamId(selected.exam.getId());
        for (Question q : questions) {
            tableModel.addRow(new Object[] {
                    q.getId(),
                    truncate(q.getQuestionText(), 60),
                    truncate(q.getOptionA(), 30),
                    truncate(q.getOptionB(), 30),
                    truncate(q.getOptionC(), 30),
                    truncate(q.getOptionD(), 30),
                    q.getCorrectOption(),
                    q.getMarks()
            });
        }
    }

    private String truncate(String text, int maxLen) {
        if (text == null)
            return "";
        return text.length() > maxLen ? text.substring(0, maxLen) + "..." : text;
    }

    private void showQuestionDialog(Question existingQuestion) {
        ExamItem selectedExam = (ExamItem) examCombo.getSelectedItem();
        if (selectedExam == null) {
            UITheme.showError(this, "Please select an exam first.");
            return;
        }

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.BG_MEDIUM);
        panel.setPreferredSize(new Dimension(550, 420));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JTextArea questionArea = new JTextArea(existingQuestion != null ? existingQuestion.getQuestionText() : "", 3,
                35);
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);

        JTextField optA = new JTextField(existingQuestion != null ? existingQuestion.getOptionA() : "", 35);
        JTextField optB = new JTextField(existingQuestion != null ? existingQuestion.getOptionB() : "", 35);
        JTextField optC = new JTextField(existingQuestion != null ? existingQuestion.getOptionC() : "", 35);
        JTextField optD = new JTextField(existingQuestion != null ? existingQuestion.getOptionD() : "", 35);

        JComboBox<String> correctCombo = new JComboBox<>(new String[] { "A", "B", "C", "D" });
        if (existingQuestion != null) {
            correctCombo.setSelectedItem(existingQuestion.getCorrectOption());
        }

        JSpinner marksSpinner = new JSpinner(new SpinnerNumberModel(
                existingQuestion != null ? existingQuestion.getMarks() : 1, 1, 100, 1));

        String[][] fields = {
                { "Question:", "area" },
                { "Option A:", "a" }, { "Option B:", "b" },
                { "Option C:", "c" }, { "Option D:", "d" },
                { "Correct Answer:", "correct" },
                { "Marks:", "marks" }
        };

        int row = 0;
        for (String[] field : fields) {
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0;
            JLabel lbl = new JLabel(field[0]);
            lbl.setForeground(UITheme.TEXT_PRIMARY);
            lbl.setFont(UITheme.FONT_BODY);
            panel.add(lbl, gbc);

            gbc.gridx = 1;
            gbc.weightx = 1;
            switch (field[1]) {
                case "area":
                    panel.add(new JScrollPane(questionArea), gbc);
                    break;
                case "a":
                    panel.add(optA, gbc);
                    break;
                case "b":
                    panel.add(optB, gbc);
                    break;
                case "c":
                    panel.add(optC, gbc);
                    break;
                case "d":
                    panel.add(optD, gbc);
                    break;
                case "correct":
                    panel.add(correctCombo, gbc);
                    break;
                case "marks":
                    panel.add(marksSpinner, gbc);
                    break;
            }
            row++;
        }

        String dialogTitle = existingQuestion != null ? "✏ Edit Question" : "➕ Add Question";
        int result = JOptionPane.showConfirmDialog(this, panel, dialogTitle,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String qText = questionArea.getText().trim();
            String a = optA.getText().trim();
            String b = optB.getText().trim();
            String c = optC.getText().trim();
            String d = optD.getText().trim();
            String correct = (String) correctCombo.getSelectedItem();
            int marks = (int) marksSpinner.getValue();

            if (qText.isEmpty() || a.isEmpty() || b.isEmpty() || c.isEmpty() || d.isEmpty()) {
                UITheme.showError(this, "All fields are required.");
                return;
            }

            if (existingQuestion != null) {
                existingQuestion.setQuestionText(qText);
                existingQuestion.setOptionA(a);
                existingQuestion.setOptionB(b);
                existingQuestion.setOptionC(c);
                existingQuestion.setOptionD(d);
                existingQuestion.setCorrectOption(correct);
                existingQuestion.setMarks(marks);
                if (questionDAO.updateQuestion(existingQuestion)) {
                    UITheme.showSuccess(this, "Question updated!");
                    loadQuestions();
                } else {
                    UITheme.showError(this, "Failed to update question.");
                }
            } else {
                Question newQ = new Question(selectedExam.exam.getId(), qText, a, b, c, d, correct, marks);
                if (questionDAO.addQuestion(newQ)) {
                    UITheme.showSuccess(this, "Question added!");
                    loadQuestions();
                } else {
                    UITheme.showError(this, "Failed to add question.");
                }
            }
        }
    }

    private void editSelectedQuestion() {
        int selectedRow = questionTable.getSelectedRow();
        if (selectedRow == -1) {
            UITheme.showError(this, "Please select a question to edit.");
            return;
        }

        int qId = (int) tableModel.getValueAt(selectedRow, 0);
        ExamItem selectedExam = (ExamItem) examCombo.getSelectedItem();
        if (selectedExam == null)
            return;

        List<Question> questions = questionDAO.getQuestionsByExamId(selectedExam.exam.getId());
        for (Question q : questions) {
            if (q.getId() == qId) {
                showQuestionDialog(q);
                return;
            }
        }
    }

    private void deleteSelectedQuestion() {
        int selectedRow = questionTable.getSelectedRow();
        if (selectedRow == -1) {
            UITheme.showError(this, "Please select a question to delete.");
            return;
        }

        int qId = (int) tableModel.getValueAt(selectedRow, 0);
        String qText = (String) tableModel.getValueAt(selectedRow, 1);

        if (UITheme.showConfirm(this, "Delete this question?\n\n\"" + qText + "\"")) {
            if (questionDAO.deleteQuestion(qId)) {
                UITheme.showSuccess(this, "Question deleted.");
                loadQuestions();
            } else {
                UITheme.showError(this, "Failed to delete question.");
            }
        }
    }
}
