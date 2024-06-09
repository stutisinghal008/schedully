package com.example.schedully;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.schedully.model.Dashboard;

import java.util.List;

public class DashboardViewModel extends AndroidViewModel {
    private DashboardRepository repository;
    private LiveData<List<String>> allCourseNames;
    private LiveData<List<Dashboard>> allCourses;

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        repository = new DashboardRepository(application);
        allCourses = repository.getAllCourses();
        allCourseNames = repository.getAllCourseNames();
    }

    public LiveData<List<Dashboard>> getAllCourses() {
        return allCourses;
    }

    public void insert(Dashboard dashboard) {
        repository.insert(dashboard);
    }

    public void update(Dashboard dashboard) {
        repository.update(dashboard);
    }

    public void delete(Dashboard dashboard) {
        repository.delete(dashboard);
    }

    public LiveData<List<String>> getAllCourseNames() {
        return allCourseNames;
    }
}
