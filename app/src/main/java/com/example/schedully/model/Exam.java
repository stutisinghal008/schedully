package com.example.schedully.model;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class Exam {

    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getExamDate() {
        return examDate;
    }

    public void setExamDate(Date examDate) {
        this.examDate = examDate;
    }

    @PrimaryKey(autoGenerate = true)
    public int examId;
    public String courseName;
    private String location;
    private Date examDate;

    public Exam(String courseName, String location, Date examDate) {
        this.courseName = courseName;
        this.location = location;
        this.examDate = examDate;
    }
}