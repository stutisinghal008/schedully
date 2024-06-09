package com.example.schedully;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.schedully.database.AppDatabase;
import com.example.schedully.database.DatabaseClient;
import com.example.schedully.databinding.FragmentExamsBinding;
import com.example.schedully.model.Exam;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class ExamFragment extends Fragment implements activity_center.OnFabClickListener, ExamAdapter.ExamInteractionListener  {

    private List<Exam> examList = new ArrayList<>();
    private ExamAdapter adapter;

    private FragmentExamsBinding binding;
    private ExamViewModel examViewModel;
    private DashboardViewModel dashboardViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentExamsBinding.inflate(inflater, container, false);
        examViewModel = new ViewModelProvider(this).get(ExamViewModel.class);
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        MaterialCalendarView materialCalendarView = binding.materialCalendarView;
        materialCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_NONE);
        observeExamsAndHighlightDates();
        setupRecyclerView();
        observeExams();
        return binding.getRoot();
    }

    private void observeExamsAndHighlightDates() {
        examViewModel.getAllExams().observe(getViewLifecycleOwner(), exams -> {
            HashSet<CalendarDay> dates = new HashSet<>();
            for (Exam exam : exams) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(exam.getExamDate());
                CalendarDay day = CalendarDay.from(calendar);
                dates.add(day);
            }
            binding.materialCalendarView.removeDecorators();
            binding.materialCalendarView.addDecorator(new ExamDateDecorator(getContext(), dates));
        });
    }


    private void setupRecyclerView() {
        adapter = new ExamAdapter(examList, this);
        binding.examDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.examDetailsRecyclerView.setAdapter(adapter);
    }

    private void observeExams() {
        examViewModel.getAllExams().observe(getViewLifecycleOwner(), exams -> {
            examList.clear();
            examList.addAll(exams);
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
        db.examDao().getAllExams().observe(requireActivity(), exams -> {
            examList.clear();
            examList.addAll(exams);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onItemClick(Exam exam) {
    }

    @Override
    public void onEditClick(Exam exam) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.new_exam, null);
        Spinner spinnerCourseName = view.findViewById(R.id.spinnerCourseName);
        EditText examLocation = view.findViewById(R.id.editExamLocation);
        EditText examDate = view.findViewById(R.id.editExamDate);
        dashboardViewModel.getAllCourseNames().observe(this, courseNames -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, courseNames);
            spinnerCourseName.setAdapter(adapter);
            if (courseNames.contains(exam.getCourseName())) {
                int position = adapter.getPosition(exam.getCourseName());
                spinnerCourseName.setSelection(position);
            }
        });
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US);
        examDate.setText(sdf.format(exam.getExamDate()));
        setupDatePicker(examDate, view.getContext());
        examLocation.setText(exam.getLocation());

        new AlertDialog.Builder(requireContext())
                .setView(view)
                .setPositiveButton("Update", (dialog, which) -> {
                    String updatedLocation = examLocation.getText().toString();
                    String updatedDateString = examDate.getText().toString();
                    Date updatedDate;
                    try {
                        updatedDate = sdf.parse(updatedDateString);
                    } catch (Exception e) {
                        e.printStackTrace(); // Handle error
                        return;
                    }
                    String updatedCourseName = spinnerCourseName.getSelectedItem().toString();

                    exam.setCourseName(updatedCourseName);
                    exam.setLocation(updatedLocation);
                    exam.setExamDate(updatedDate);
                    updateExamInDatabase(exam);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setupDatePicker(EditText examDate, Context context) {
        examDate.setOnClickListener(v -> {
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
                    examDate.setText(dateTime);
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

                timePickerDialog.show();
            }, year, month, day);

            datePickerDialog.show();
        });
    }

    private void updateExamInDatabase(Exam exam) {
        examViewModel.update(exam);
        fetchDataFromDatabase();
    }

    @Override
    public void onDeleteClick(Exam exam) {
        showDeleteConfirmationDialog(exam);
    }

    private void showDeleteConfirmationDialog(Exam exam) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Exam")
                .setMessage("Are you sure you want to delete this exam?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    deleteExamFromDatabase(exam);
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteExamFromDatabase(Exam exam) {
        examViewModel.delete(exam);
        fetchDataFromDatabase();
    }

    @Override
    public void onFabClick() {
        openAddExamDialog();
    }

    private void openAddExamDialog() {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.new_exam, null);
        final Spinner spinnerCourseName = view.findViewById(R.id.spinnerCourseName);
        setupCourseSpinner(spinnerCourseName);
        EditText examLocation = view.findViewById(R.id.editExamLocation);
        EditText examDate = view.findViewById(R.id.editExamDate);
        examDate.setOnClickListener(v -> showDateTimePicker(examDate, getContext()));

        new android.app.AlertDialog.Builder(requireContext())
                .setView(view)
                .setPositiveButton("Add", (dialog, which) -> {
                    saveActivity(examLocation, examDate, spinnerCourseName);
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel())
                .show();
    }

    private void showDateTimePicker(EditText examDate, Context context) {
        final Calendar currentCalendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, R.style.TimePickerTheme, (view, year, monthOfYear, dayOfMonth) -> {
            currentCalendar.set(year, monthOfYear, dayOfMonth);
            TimePickerDialog timePickerDialog = new TimePickerDialog(context, R.style.TimePickerTheme, (view1, hourOfDay, minute) -> {
                currentCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                currentCalendar.set(Calendar.MINUTE, minute);
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US);
                examDate.setText(sdf.format(currentCalendar.getTime()));
            }, currentCalendar.get(Calendar.HOUR_OF_DAY), currentCalendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        }, currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void setupCourseSpinner(Spinner spinnerCourseName) {
        dashboardViewModel.getAllCourseNames().observe(this, courseNames -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, courseNames);
            spinnerCourseName.setAdapter(adapter);
        });
    }

    private void saveActivity(EditText examLocation, EditText examDate, Spinner spinnerCourseName) {
        try {
            String location = examLocation.getText().toString();
            String courseName = spinnerCourseName.getSelectedItem().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US);
            Date date = sdf.parse(examDate.getText().toString());
            Exam newExam = new Exam(courseName, location, date);
            examList.add(newExam);
            adapter.notifyDataSetChanged();
            Context context = getContext();
            if (context != null) {
                new Thread(() -> {
                    AppDatabase db = DatabaseClient.getInstance(context).getAppDatabase();
                    db.examDao().insert(newExam);
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}