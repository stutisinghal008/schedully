package com.example.schedully;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.schedully.database.AppDatabase;
import com.example.schedully.database.AssignmentDao;
import com.example.schedully.database.DatabaseClient;
import com.example.schedully.model.Assignment;

import java.util.List;

public class AssignmentsRepository {
    private AssignmentDao assignmentDao;
    public AssignmentsRepository(Application application) {
        AppDatabase db = DatabaseClient.getInstance(application).getAppDatabase();
        assignmentDao = db.assignmentDao();
    }

    public void insert(Assignment assignment) {
        new Thread(() -> assignmentDao.update(assignment)).start();
    }

    public void update(Assignment assignment) {new Thread(() -> assignmentDao.update(assignment)).start();}

    public void delete(Assignment assignment) {new Thread(() -> assignmentDao.delete(assignment)).start();}

    public LiveData<List<Assignment>> getAllAssignments() {
        return assignmentDao.getAllAssignments();
    }
}
