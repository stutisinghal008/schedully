package com.example.schedully;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.schedully.database.AppDatabase;
import com.example.schedully.database.DatabaseClient;
import com.example.schedully.database.ExamDao;
import com.example.schedully.model.Exam;

import java.util.List;

public class ExamRepository {
    private ExamDao examDao;
    private LiveData<List<Exam>> allExams;

    public ExamRepository(Application application) {
        AppDatabase db = DatabaseClient.getInstance(application).getAppDatabase();
        examDao = db.examDao();
        allExams = examDao.getAllExams();
    }

    LiveData<List<Exam>> getAllExams() {
        return allExams;
    }

    void insert(Exam exam) {
        new Thread(() -> examDao.insert(exam)).start();
    }

    void update(Exam exam) {
        new Thread(() -> examDao.update(exam)).start();
    }

    void delete(Exam exam) {
        new Thread(() -> examDao.delete(exam)).start();
    }
}
