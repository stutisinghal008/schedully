package com.example.schedully;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.schedully.database.AppDatabase;
import com.example.schedully.database.DashboardDao;
import com.example.schedully.database.DatabaseClient;
import com.example.schedully.model.Dashboard;

import java.util.List;

public class DashboardRepository {
    private DashboardDao dashboardDao;
    private LiveData<List<Dashboard>> allCourses;

    public DashboardRepository(Application application) {
        AppDatabase db = DatabaseClient.getInstance(application).getAppDatabase();
        dashboardDao = db.dashboardDao();
        allCourses = dashboardDao.getAllCourses();
    }

    LiveData<List<Dashboard>> getAllCourses() {
        return allCourses;
    }

    void insert(Dashboard dashboard) {
        new Thread(() -> dashboardDao.insert(dashboard)).start();
    }

    void update(Dashboard dashboard) {
        new Thread(() -> dashboardDao.update(dashboard)).start();
    }

    void delete(Dashboard dashboard) {
        new Thread(() -> dashboardDao.delete(dashboard)).start();
    }

    public LiveData<List<String>> getAllCourseNames() {
        return dashboardDao.getAllCourseNames();
    }
}
