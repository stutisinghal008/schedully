package com.example.schedully;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.schedully.model.Extracurricular;

import java.util.List;

public class ExtracurricularViewModel extends AndroidViewModel {
    private ExtracurricularRepository repository;
    private LiveData<List<Extracurricular>> allActivites;
    public ExtracurricularViewModel(@NonNull Application application) {
        super(application);
        repository = new ExtracurricularRepository(application);
        allActivites = repository.getAllActivities();
    }

    public LiveData<List<Extracurricular>> getAllActivities() {
        return allActivites;
    }

    public void insert(Extracurricular extracurricular) {
        repository.insert(extracurricular);
    }

    public void update(Extracurricular extracurricular) {
        repository.update(extracurricular);
    }

    public void delete(Extracurricular extracurricular) {
        repository.delete(extracurricular);
    }

}
