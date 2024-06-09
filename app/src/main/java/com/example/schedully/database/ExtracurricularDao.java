package com.example.schedully.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.schedully.model.Extracurricular;

import java.util.List;

@Dao
public interface ExtracurricularDao {
    @Insert
    void insert(Extracurricular extracurricular);

    @Update
    void update(Extracurricular extracurricular);

    @Delete
    void delete(Extracurricular extracurricular);

    @Query("SELECT * FROM Extracurricular")
    LiveData<List<Extracurricular>> getAllActivities();
}
