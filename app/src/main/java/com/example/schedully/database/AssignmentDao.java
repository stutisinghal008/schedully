package com.example.schedully.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.schedully.model.Assignment;

import java.util.List;

@Dao
public interface AssignmentDao {
    @Insert
    void insert(Assignment assignment);

    @Update
    void update(Assignment assignment);

    @Delete
    void delete(Assignment assignment);

    @Query("SELECT * FROM Assignment")
    LiveData<List<Assignment>> getAllAssignments();

    @Query("SELECT * FROM Assignment ORDER BY dueDate ASC")
    LiveData<List<Assignment>> getAllAssignmentsSortedByDate();

    @Query("SELECT * FROM Assignment ORDER BY courseName ASC")
    LiveData<List<Assignment>> getAllAssignmentsSortedByCourse();
}
