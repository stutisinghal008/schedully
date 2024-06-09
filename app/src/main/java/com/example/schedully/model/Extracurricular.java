package com.example.schedully.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class Extracurricular {

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    @PrimaryKey(autoGenerate = true)
    public int activityId;

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }
    private String activityType;

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
    private String organizationName;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    private String taskName;

    public Date getTaskDueDate() {
        return taskDueDate;
    }

    public void setTaskDueDate(Date taskDueDate) {
        this.taskDueDate = taskDueDate;
    }

    public Date taskDueDate;

    public Extracurricular(String activityType, String organizationName, String taskName, Date taskDueDate) {
        this.activityType=activityType;
        this.organizationName=organizationName;
        this.taskName=taskName;
        this.taskDueDate=taskDueDate;
    }

}
