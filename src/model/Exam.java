package model;

import java.sql.Timestamp;

public class Exam {
    private int id;
    private String title;
    private String description;
    private int durationMinutes;
    private int createdBy;
    private boolean isActive;
    private Timestamp createdAt;

    public Exam() {
    }

    public Exam(int id, String title, String description, int durationMinutes, int createdBy, boolean isActive,
                Timestamp createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.createdBy = createdBy;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    public Exam(String title, String description, int durationMinutes, int createdBy) {
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.createdBy = createdBy;
        this.isActive = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return title;
    }
}
