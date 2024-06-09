package com.example.schedully.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.schedully.model.Dashboard;

import java.util.List;

@Dao
public interface DashboardDao {
    @Insert
    void insert(Dashboard dashboard);

    @Update
    void update(Dashboard dashboard);

    @Delete
    void delete(Dashboard dashboard);

    @Query("SELECT * FROM Dashboard")
    LiveData<List<Dashboard>> getAllCourses();

    @Query("SELECT name FROM Dashboard")
    LiveData<List<String>> getAllCourseNames();
}
