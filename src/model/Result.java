package model;

import java.sql.Timestamp;

public class Result {
    private int id;
    private int studentId;
    private int examId;
    private int score;
    private int totalMarks;
    private Timestamp submittedAt;

   
    // Extra fields for display purposes
    private String studentName;
    private String examTitle;

    public Result() {
    }

    public Result(int id, int studentId, int examId, int score, int totalMarks, Timestamp submittedAt) 
    
    {
        this.id = id;
        
        this.studentId = studentId;
        
        this.examId = examId;
        this.score = score;
        this.totalMarks = totalMarks;
        this.submittedAt = submittedAt;
    }

    public Result(int studentId, int examId, int score, int totalMarks) {
        this.studentId = studentId;
        this.examId = examId;
        this.score = score;
        this.totalMarks = totalMarks;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public int getScore() {
        return score;
        
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }

    public Timestamp getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Timestamp submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getExamTitle() {
        return examTitle;
    }

    public void setExamTitle(String examTitle) {
        this.examTitle = examTitle;
    }

    public double getPercentage() {
        if (totalMarks == 0)
            return 0;
        return (score * 100.0) / totalMarks;
    }
}
