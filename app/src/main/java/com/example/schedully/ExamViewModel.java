package com.example.schedully;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.schedully.model.Exam;

import java.util.List;

public class ExamViewModel extends AndroidViewModel {
    private ExamRepository repository;
    private LiveData<List<Exam>> allExams;

    public ExamViewModel(@NonNull Application application) {
        super(application);
        repository = new ExamRepository(application);
        allExams = repository.getAllExams();
    }

    public void insert(Exam exam) { repository.insert(exam); }
    public void update(Exam exam) { repository.update(exam); }
    public void delete(Exam exam) { repository.delete(exam); }
    public LiveData<List<Exam>> getAllExams() { return allExams; }
}
