package com.example.schedully.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity
public class Assignment {

    @PrimaryKey(autoGenerate = true)
    public int assignmentId;
    public String assignmentName;
    public Date dueDate;
    public String courseName;
    public boolean completed;

    // Constructor
    public Assignment(String assignmentName, Date dueDate, String courseName, boolean completed) {
        this.assignmentName = assignmentName;
        this.dueDate = dueDate;
        this.courseName = courseName;
        this.completed = completed;
    }

    // Getters
    public int getAssignmentId() {
        return assignmentId;
    }

    public String getAssignmentName() {
        return assignmentName;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public String getCourseName() {
        return courseName;
    }

    public boolean isCompleted() {
        return completed;
    }

    // Setters
    public void setAssignmentId(int assignmentId) {
        this.assignmentId = assignmentId;
    }

    public void setAssignmentName(String assignmentName) {
        this.assignmentName = assignmentName;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
