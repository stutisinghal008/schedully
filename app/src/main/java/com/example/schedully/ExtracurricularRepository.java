package com.example.schedully;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.schedully.database.AppDatabase;
import com.example.schedully.database.DashboardDao;
import com.example.schedully.database.DatabaseClient;
import com.example.schedully.database.ExtracurricularDao;
import com.example.schedully.model.Dashboard;
import com.example.schedully.model.Extracurricular;

import java.util.List;

public class ExtracurricularRepository {
    private ExtracurricularDao extracurricularDao;
    private LiveData<List<Extracurricular>> allActivities;

    public ExtracurricularRepository(Application application) {
        AppDatabase db = DatabaseClient.getInstance(application).getAppDatabase();
        extracurricularDao = db.extracurricularDao();
        allActivities = extracurricularDao.getAllActivities();
    }

    LiveData<List<Extracurricular>> getAllActivities() {
        return allActivities;
    }

    void insert(Extracurricular extracurricular) {
        new Thread(() -> extracurricularDao.insert(extracurricular)).start();
    }

    void update(Extracurricular extracurricular) {
        new Thread(() -> extracurricularDao.update(extracurricular)).start();
    }

    void delete(Extracurricular extracurricular) {
        new Thread(() -> extracurricularDao.delete(extracurricular)).start();
    }
}
