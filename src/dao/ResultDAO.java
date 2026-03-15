package dao;

import db.DatabaseConnection;
import model.Result;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResultDAO {

    public boolean saveResult(Result result) {
        String sql = "INSERT INTO results (student_id, exam_id, score, total_marks) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, result.getStudentId());
            stmt.setInt(2, result.getExamId());
            stmt.setInt(3, result.getScore());
            stmt.setInt(4, result.getTotalMarks());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Result> getResultsByStudent(int studentId) {
        List<Result> results = new ArrayList<>();
        String sql = "SELECT r.*, u.full_name as student_name, e.title as exam_title " +
                "FROM results r " +
                "JOIN users u ON r.student_id = u.id " +
                "JOIN exams e ON r.exam_id = e.id " +
                "WHERE r.student_id = ? ORDER BY r.submitted_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(extractResult(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public List<Result> getResultsByExam(int examId) {
        List<Result> results = new ArrayList<>();
        String sql = "SELECT r.*, u.full_name as student_name, e.title as exam_title " +
                "FROM results r " +
                "JOIN users u ON r.student_id = u.id " +
                "JOIN exams e ON r.exam_id = e.id " +
                "WHERE r.exam_id = ? ORDER BY r.score DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, examId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(extractResult(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public List<Result> getAllResults() {
        List<Result> results = new ArrayList<>();
        String sql = "SELECT r.*, u.full_name as student_name, e.title as exam_title " +
                "FROM results r " +
                "JOIN users u ON r.student_id = u.id " +
                "JOIN exams e ON r.exam_id = e.id " +
                "ORDER BY r.submitted_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                results.add(extractResult(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public boolean hasStudentTakenExam(int studentId, int examId) {
        String sql = "SELECT COUNT(*) FROM results WHERE student_id = ? AND exam_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, examId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public double getAverageScore() {
        String sql = "SELECT AVG(CASE WHEN total_marks > 0 THEN (score * 100.0 / total_marks) ELSE 0 END) FROM results";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next())
                return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalResultCount() {
        String sql = "SELECT COUNT(*) FROM results";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Result extractResult(ResultSet rs) throws SQLException {
        Result result = new Result(
                rs.getInt("id"),
                rs.getInt("student_id"),
                rs.getInt("exam_id"),
                rs.getInt("score"),
                rs.getInt("total_marks"),
                rs.getTimestamp("submitted_at"));
        result.setStudentName(rs.getString("student_name"));
        result.setExamTitle(rs.getString("exam_title"));
        return result;
    }
}
