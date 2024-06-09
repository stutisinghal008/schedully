package com.example.schedully.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Dashboard {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String name;
    public String instructor;
    public String days;
    public String time;
    public String location;

    public void setName(String name) {
        this.name = name;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Dashboard(String name, String instructor, String days, String time, String location) {
        this.name = name;
        this.instructor = instructor;
        this.days = days;
        this.time = time;
        this.location = location;
    }
    public String getName() {
        return name;
    }

    public String getInstructor() {
        return instructor;
    }

    public String getTime() {
        return time;
    }

    public String getDays() {
        return days;
    }

    public String getLocation() {
        return location;
    }
}
