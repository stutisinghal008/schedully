package com.example.schedully;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import com.example.schedully.database.AppDatabase;
import com.example.schedully.database.DatabaseClient;
import com.example.schedully.model.Assignment;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AssignmentFragment extends Fragment implements activity_center.OnFabClickListener, AssignmentAdapter.AssignmentInteractionListener {
    private RecyclerView assignmentsRecyclerView;
    private AssignmentsViewModel assignmentsViewModel;
    private AssignmentAdapter adapter;
    private List<Assignment> assignmentList = new ArrayList<>();
    private DashboardViewModel dashboardViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assignment, container, false);
        assignmentsRecyclerView = view.findViewById(R.id.assignmentsRecyclerView);
        assignmentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AssignmentAdapter(assignmentList, this);
        assignmentsRecyclerView.setAdapter(adapter);
        fetchDataFromDatabase(null);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        assignmentsViewModel = new ViewModelProvider(this).get(AssignmentsViewModel.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof activity_center) {
            ((activity_center) getActivity()).setupFilterIcon(true);
        }
        fetchDataFromDatabase(null);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() instanceof activity_center) {
            ((activity_center) getActivity()).setupFilterIcon(false);
        }
    }

    private void fetchDataFromDatabase(final String sortBy) {
        AppDatabase db = DatabaseClient.getInstance(requireContext()).getAppDatabase();
        LiveData<List<Assignment>> assignmentsLiveData;
        if ("date".equals(sortBy)) {
            assignmentsLiveData = db.assignmentDao().getAllAssignmentsSortedByDate();
        } else if ("course".equals(sortBy)) {
            assignmentsLiveData = db.assignmentDao().getAllAssignmentsSortedByCourse();
        } else {
            assignmentsLiveData = db.assignmentDao().getAllAssignments();
        }

        assignmentsLiveData.observe(requireActivity(), assignments -> {
            assignmentList.clear();
            assignmentList.addAll(assignments);
            adapter.notifyDataSetChanged();
        });
    }

    public void handleFilterSelection(int menuItemId) {
        if (menuItemId == R.id.action_sort_by_date) {
            fetchDataFromDatabase("date");
        } else if (menuItemId == R.id.action_sort_by_course) {
            fetchDataFromDatabase("course");
        } else if (menuItemId == R.id.action_no_filter) {
            fetchDataFromDatabase(null);
        } else {
            // Handle any other cases or do nothing
        }
    }

    @Override
    public void onFabClick() {
        showNewAssignmentDialog();
    }

    private void showNewAssignmentDialog() {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.new_assignment, null);
        final EditText editTextAssignmentName = dialogView.findViewById(R.id.editTextAssignmentName);
        final EditText editTextDateTime = dialogView.findViewById(R.id.editTextDate);
        final Spinner spinnerCourseName = dialogView.findViewById(R.id.spinnerCourseName);

        setupCourseSpinner(spinnerCourseName);
        editTextDateTime.setOnClickListener(v -> showDateTimePicker(editTextDateTime, getContext()));

        new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    saveAssignment(editTextAssignmentName, editTextDateTime, spinnerCourseName);
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel())
                .show();
    }

    private void showDateTimePicker(final EditText editText, Context context) {
        final Calendar currentCalendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, R.style.TimePickerTheme, (view, year, monthOfYear, dayOfMonth) -> {
            currentCalendar.set(year, monthOfYear, dayOfMonth);
            TimePickerDialog timePickerDialog = new TimePickerDialog(context, R.style.TimePickerTheme, (view1, hourOfDay, minute) -> {
                currentCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                currentCalendar.set(Calendar.MINUTE, minute);
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US);
                editText.setText(sdf.format(currentCalendar.getTime()));
            }, currentCalendar.get(Calendar.HOUR_OF_DAY), currentCalendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        }, currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void saveAssignment(EditText editTextName, EditText editTextDateTime, Spinner spinnerCourseName) {
        try {
            String assignmentName = editTextName.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US);
            Date dueDate = sdf.parse(editTextDateTime.getText().toString());
            String courseName = spinnerCourseName.getSelectedItem().toString();
            Assignment newAssignment = new Assignment(assignmentName, dueDate, courseName, false);
            assignmentList.add(newAssignment);
            adapter.notifyDataSetChanged();
            Context context = getContext();
            if (context != null) {
                new Thread(() -> {
                    AppDatabase db = DatabaseClient.getInstance(context).getAppDatabase();
                    db.assignmentDao().insert(newAssignment);
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupCourseSpinner(Spinner spinner) {
        dashboardViewModel.getAllCourseNames().observe(this, courseNames -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, courseNames);
            spinner.setAdapter(adapter);
        });
    }

    public void onItemClick(Assignment assignment) {

    }

    public void onEditClick(Assignment assignment) {
        showEditDialog(assignment);
    }

    private void showEditDialog(Assignment assignment) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.new_assignment, null);

        EditText editTextTaskName = view.findViewById(R.id.editTextAssignmentName);
        EditText editTextDate = view.findViewById(R.id.editTextDate);
        Spinner spinnerCourseName = view.findViewById(R.id.spinnerCourseName);

        editTextTaskName.setText(assignment.getAssignmentName());
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US);
        editTextDate.setText(sdf.format(assignment.getDueDate()));

        setupDatePicker(editTextDate, view.getContext());

        dashboardViewModel.getAllCourseNames().observe(getViewLifecycleOwner(), courseNames -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, courseNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCourseName.setAdapter(adapter);

            if (courseNames.contains(assignment.getCourseName())) {
                int position = adapter.getPosition(assignment.getCourseName());
                spinnerCourseName.setSelection(position);
            }
        });
        new AlertDialog.Builder(requireContext())
                .setView(view)
                .setPositiveButton("Update", (dialog, which) -> {
                    String updatedTaskName = editTextTaskName.getText().toString();
                    String updatedDueDateString = editTextDate.getText().toString();
                    Date updatedDueDate;
                    try {
                        updatedDueDate = sdf.parse(updatedDueDateString);
                    } catch (Exception e) {
                        e.printStackTrace(); // Handle error
                        return;
                    }
                    String updatedCourseName = spinnerCourseName.getSelectedItem().toString();

                    assignment.setAssignmentName(updatedTaskName);
                    assignment.setDueDate(updatedDueDate);
                    assignment.setCourseName(updatedCourseName);
                    updateAssignmentInDatabase(assignment);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setupDatePicker(EditText editTextDate, Context context) {
        editTextDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR); // current year
            int month = calendar.get(Calendar.MONTH); // current month
            int day = calendar.get(Calendar.DAY_OF_MONTH); // current day

            DatePickerDialog datePickerDialog = new DatePickerDialog(context, R.style.TimePickerTheme, (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                calendar.set(selectedYear, selectedMonth, selectedDayOfMonth);
                TimePickerDialog timePickerDialog = new TimePickerDialog(context, R.style.TimePickerTheme, (timeView, selectedHour, selectedMinute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                    calendar.set(Calendar.MINUTE, selectedMinute);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
                    String dateTime = dateFormat.format(calendar.getTime());
                    editTextDate.setText(dateTime);
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

                timePickerDialog.show();
            }, year, month, day);

            datePickerDialog.show();
        });
    }


    private void updateAssignmentInDatabase(Assignment assignment) {
        assignmentsViewModel.update(assignment);
    }

    public void onDeleteClick(Assignment assignment) {
        showDeleteConfirmationDialog(assignment);
    }

    private void showDeleteConfirmationDialog(Assignment assignment) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Assignment")
                .setMessage("Are you sure you want to delete this assignment?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    deleteAssignmentFromDatabase(assignment);
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteAssignmentFromDatabase(Assignment assignment) {
        assignmentsViewModel.delete(assignment);
    }
}
