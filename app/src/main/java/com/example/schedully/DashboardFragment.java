package com.example.schedully;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.schedully.database.AppDatabase;
import com.example.schedully.database.DatabaseClient;
import com.example.schedully.databinding.FragmentDashboardBinding;
import com.example.schedully.model.Dashboard;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

public class DashboardFragment extends Fragment implements activity_center.OnFabClickListener, CourseAdapter.OnItemClickListener {

    private List<Dashboard> courseList = new ArrayList<>();
    private CourseAdapter adapter;

    private FragmentDashboardBinding binding;
    private DashboardViewModel dashboardViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        setupRecyclerView();
        observeCourses();
        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new CourseAdapter(courseList, this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);
    }

    private void observeCourses() {
        dashboardViewModel.getAllCourses().observe(getViewLifecycleOwner(), courses -> {
            courseList.clear();
            courseList.addAll(courses);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchDataFromDatabase();
    }

    private void fetchDataFromDatabase() {
        AppDatabase db = DatabaseClient.getInstance(requireContext()).getAppDatabase();
        db.dashboardDao().getAllCourses().observe(requireActivity(), courses -> {
            courseList.clear();
            courseList.addAll(courses);
            adapter.notifyDataSetChanged();
        });
    }

    private void openAddCourseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialogue_add_course, null);

        EditText editTextCourseName = dialogView.findViewById(R.id.courseName);
        EditText editTextInstructor = dialogView.findViewById(R.id.instructor);
        EditText editTextLocation = dialogView.findViewById(R.id.location);

        CheckBox checkBoxSunday = dialogView.findViewById(R.id.checkBoxSunday);
        CheckBox checkBoxMonday = dialogView.findViewById(R.id.checkBoxMonday);
        CheckBox checkBoxTuesday = dialogView.findViewById(R.id.checkBoxTuesday);
        CheckBox checkBoxWednesday = dialogView.findViewById(R.id.checkBoxWednesday);
        CheckBox checkBoxThursday = dialogView.findViewById(R.id.checkBoxThursday);
        CheckBox checkBoxFriday = dialogView.findViewById(R.id.checkBoxFriday);
        CheckBox checkBoxSaturday = dialogView.findViewById(R.id.checkBoxSaturday);

        Button buttonSelectTime = dialogView.findViewById(R.id.selectTime);

        final int[] hour = new int[1];
        final int[] minute = new int[1];

        buttonSelectTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                    R.style.TimePickerTheme,
                    (view, hourOfDay, minutes) -> {
                        hour[0] = hourOfDay;
                        minute[0] = minutes;
                        buttonSelectTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour[0], minute[0]));
                    }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), true);
            timePickerDialog.show();
        });
        builder.setView(dialogView)
                .setPositiveButton("Add", (dialog, id) -> {
                    // Extract info
                    String courseName = editTextCourseName.getText().toString();
                    String instructor = editTextInstructor.getText().toString();
                    String location = editTextLocation.getText().toString();
                    String time = buttonSelectTime.getText().toString();
                    String days = getSelectedDays(checkBoxSunday, checkBoxMonday, checkBoxTuesday,
                            checkBoxWednesday, checkBoxThursday, checkBoxFriday,
                            checkBoxSaturday);

                    Dashboard newCourse = new Dashboard(courseName, instructor, days, time, location);
                    courseList.add(newCourse);
                    adapter.notifyDataSetChanged();
                    Context context = getContext();
                    if (context != null) {
                        new Thread(() -> {
                            AppDatabase db = DatabaseClient.getInstance(context).getAppDatabase();
                            db.dashboardDao().insert(newCourse);
                        }).start();
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String getSelectedDays(CheckBox... checkBoxes) {
        StringBuilder selectedDays = new StringBuilder();
        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isChecked()) {
                if (selectedDays.length() > 0) selectedDays.append(", ");
                selectedDays.append(checkBox.getText().toString());
            }
        }
        return selectedDays.toString();
    }

    @Override
    public void onItemClick(Dashboard course) {

    }

    @Override
    public void onEditClick(Dashboard course) {
        showEditDialog(course);
    }

    private void showEditDialog(Dashboard course) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialogue_add_course, null);

        EditText editTextCourseName = dialogView.findViewById(R.id.courseName);
        EditText editTextInstructor = dialogView.findViewById(R.id.instructor);
        EditText editTextLocation = dialogView.findViewById(R.id.location);

        CheckBox checkBoxSunday = dialogView.findViewById(R.id.checkBoxSunday);
        CheckBox checkBoxMonday = dialogView.findViewById(R.id.checkBoxMonday);
        CheckBox checkBoxTuesday = dialogView.findViewById(R.id.checkBoxTuesday);
        CheckBox checkBoxWednesday = dialogView.findViewById(R.id.checkBoxWednesday);
        CheckBox checkBoxThursday = dialogView.findViewById(R.id.checkBoxThursday);
        CheckBox checkBoxFriday = dialogView.findViewById(R.id.checkBoxFriday);
        CheckBox checkBoxSaturday = dialogView.findViewById(R.id.checkBoxSaturday);

        Button buttonSelectTime = dialogView.findViewById(R.id.selectTime);

        final int[] hour = new int[1];
        final int[] minute = new int[1];

        buttonSelectTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                    R.style.TimePickerTheme,
                    (view, hourOfDay, minutes) -> {
                        hour[0] = hourOfDay;
                        minute[0] = minutes;
                        buttonSelectTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour[0], minute[0]));
                    }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), true);
            timePickerDialog.show();
        });
        editTextCourseName.setText(course.getName());
        editTextInstructor.setText(course.getInstructor());
        editTextLocation.setText(course.getLocation());
        buttonSelectTime.setText(course.getTime());
        List<String> courseDays = Arrays.asList(course.getDays().split(", "));
        checkBoxSunday.setChecked(courseDays.contains("Sun"));
        checkBoxMonday.setChecked(courseDays.contains("Mon"));
        checkBoxTuesday.setChecked(courseDays.contains("Tues"));
        checkBoxWednesday.setChecked(courseDays.contains("Wed"));
        checkBoxThursday.setChecked(courseDays.contains("Thurs"));
        checkBoxFriday.setChecked(courseDays.contains("Fri"));
        checkBoxSaturday.setChecked(courseDays.contains("Sat"));
        builder.setView(dialogView)
                .setPositiveButton("Update", (dialog, id) -> {
                    course.setName(editTextCourseName.getText().toString());
                    course.setInstructor(editTextInstructor.getText().toString());
                    course.setLocation(editTextLocation.getText().toString());
                    course.setTime(buttonSelectTime.getText().toString());
                    course.setDays(getSelectedDays(checkBoxSunday, checkBoxMonday, checkBoxTuesday,
                            checkBoxWednesday, checkBoxThursday, checkBoxFriday,
                            checkBoxSaturday));

                    updateCourseInDatabase(course);
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateCourseInDatabase(Dashboard course) {
        dashboardViewModel.update(course);

    }

    @Override
    public void onDeleteClick(Dashboard course) {
        showDeleteConfirmationDialog(course);
    }

    private void showDeleteConfirmationDialog(Dashboard course) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Course")
                .setMessage("Are you sure you want to delete this course?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    deleteCourseFromDatabase(course);
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteCourseFromDatabase(Dashboard course) {
        dashboardViewModel.delete(course);
    }

    @Override
    public void onFabClick() {
        openAddCourseDialog();
    }
}
