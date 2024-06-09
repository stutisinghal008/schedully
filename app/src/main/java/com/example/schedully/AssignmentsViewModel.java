package com.example.schedully;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.schedully.model.Assignment;

import java.util.List;

public class AssignmentsViewModel extends AndroidViewModel {
    private AssignmentsRepository assignmentsRepository;
    private LiveData<List<Assignment>> allAssignments;

    public AssignmentsViewModel(@NonNull Application application) {
        super(application);
        assignmentsRepository = new AssignmentsRepository(application);
        allAssignments = assignmentsRepository.getAllAssignments();
    }

    public void insert(Assignment assignment) { assignmentsRepository.insert(assignment); }
    public void update(Assignment assignment) { assignmentsRepository.update(assignment); }
    public void delete(Assignment assignment) { assignmentsRepository.delete(assignment); }
    public LiveData<List<Assignment>> getAllAssignments() { return allAssignments; }
}
