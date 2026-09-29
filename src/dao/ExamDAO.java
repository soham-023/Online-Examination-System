package dao;

import db.DatabaseConnection;
import model.Exam;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExamDAO {
    

    public boolean createExam(Exam exam) {
        String sql = "INSERT INTO exams (title, description, duration_minutes, created_by, is_active) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, exam.getTitle());
            stmt.setString(2, exam.getDescription());
            stmt.setInt(3, exam.getDurationMinutes());
            stmt.setInt(4, exam.getCreatedBy());
            stmt.setBoolean(5, exam.isActive());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    exam.setId(generatedKeys.getInt(1));
                }
                
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    

    public boolean updateExam(Exam exam) {
        String sql = "UPDATE exams SET title = ?, description = ?, duration_minutes = ?, is_active = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, exam.getTitle());
            stmt.setString(2, exam.getDescription());
            stmt.setInt(3, exam.getDurationMinutes());
            stmt.setBoolean(4, exam.isActive());
            stmt.setInt(5, exam.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    

    public boolean deleteExam(int id) {
        String sql = "DELETE FROM exams WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Exam> getAllExams() {
        List<Exam> exams = new ArrayList<>();
        String sql = "SELECT * FROM exams ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                exams.add(extractExam(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exams;
    }

    public List<Exam> getActiveExams() {
        List<Exam> exams = new ArrayList<>();
        String sql = "SELECT * FROM exams WHERE is_active = TRUE ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                exams.add(extractExam(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exams;
    }

    public Exam getExamById(int id) {
        String sql = "SELECT * FROM exams WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractExam(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getExamCount() {
        String sql = "SELECT COUNT(*) FROM exams";
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

    private Exam extractExam(ResultSet rs) throws SQLException {
        return new Exam(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getInt("duration_minutes"),
                rs.getInt("created_by"),
                rs.getBoolean("is_active"),
                rs.getTimestamp("created_at"));
    }
}
