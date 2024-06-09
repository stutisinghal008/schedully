package com.example.schedully.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.schedully.model.Exam;

import java.util.List;

@Dao
public interface ExamDao {
    @Insert
    void insert(Exam exam);

    @Update
    void update(Exam exam);

    @Delete
    void delete(Exam exam);

    @Query("SELECT * FROM Exam")
    LiveData<List<Exam>> getAllExams();
}
