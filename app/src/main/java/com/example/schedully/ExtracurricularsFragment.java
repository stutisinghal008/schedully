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
import com.example.schedully.databinding.FragmentExtracurricularsBinding;
import com.example.schedully.model.Extracurricular;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExtracurricularsFragment extends Fragment implements activity_center.OnFabClickListener, ExtracurricularAdapter.OnItemClickListener {

    private List<Extracurricular> activityList = new ArrayList<>();
    private ExtracurricularAdapter adapter;

    private FragmentExtracurricularsBinding binding;
    private ExtracurricularViewModel extracurricularViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentExtracurricularsBinding.inflate(inflater, container, false);
        extracurricularViewModel = new ViewModelProvider(this).get(ExtracurricularViewModel.class);
        setupRecyclerView();
        observeActivities();
        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new ExtracurricularAdapter(activityList, this);
        binding.extraRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.extraRecyclerView.setAdapter(adapter);
    }

    private void observeActivities() {
        extracurricularViewModel.getAllActivities().observe(getViewLifecycleOwner(), activities -> {
            activityList.clear();
            activityList.addAll(activities);
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
        db.extracurricularDao().getAllActivities().observe(requireActivity(), activities -> {
            activityList.clear();
            activityList.addAll(activities);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onItemClick(Extracurricular extracurricular) {

    }

    @Override
    public void onEditClick(Extracurricular extracurricular) {
        showEditActivityDialog(extracurricular);
    }

    private void showEditActivityDialog(Extracurricular extracurricular) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.new_extracurricular, null);

        Spinner spinner = view.findViewById(R.id.spinnerActivityType);
        EditText organizationName = view.findViewById(R.id.editOrganizationName);
        EditText activityName = view.findViewById(R.id.editActivityName);
        EditText editECDate = view.findViewById(R.id.ECDate);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, new String[]{"Club", "Internship", "Research", "Part-Time Employment", "Other"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        int spinnerPosition = adapter.getPosition(extracurricular.getActivityType().toString());
        spinner.setSelection(spinnerPosition);
        organizationName.setText(extracurricular.getOrganizationName());
        activityName.setText(extracurricular.getTaskName());
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US);
        editECDate.setText(sdf.format(extracurricular.getTaskDueDate()));

        setupDatePicker(editECDate, view.getContext());

        new AlertDialog.Builder(requireContext())
                .setView(view)
                .setPositiveButton("Update", (dialog, which) -> {
                    String updatedOrgName = organizationName.getText().toString();
                    String updatedActivityName = activityName.getText().toString();
                    String updatedDueDateString = editECDate.getText().toString();
                    Date updatedDueDate;
                    try {
                        updatedDueDate = sdf.parse(updatedDueDateString);
                    } catch (Exception e) {
                        e.printStackTrace(); // Handle error
                        return;
                    }
                    String updatedActivityType = spinner.getSelectedItem().toString();

                    extracurricular.setTaskDueDate(updatedDueDate);
                    extracurricular.setTaskName(updatedActivityName);
                    extracurricular.setOrganizationName(updatedOrgName);
                    extracurricular.setActivityType(updatedActivityType);
                    updateAssignmentInDatabase(extracurricular);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setupDatePicker(EditText editECDate, Context context) {
        editECDate.setOnClickListener(v -> {
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
                    editECDate.setText(dateTime);
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

                timePickerDialog.show();
            }, year, month, day);

            datePickerDialog.show();
        });
    }

    private void updateAssignmentInDatabase(Extracurricular extracurricular) {
        extracurricularViewModel.update(extracurricular);
    }


    @Override
    public void onDeleteClick(Extracurricular extracurricular) {
        showDeleteConfirmationDialog(extracurricular);
    }

    private void showDeleteConfirmationDialog(Extracurricular extracurricular) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Activity")
                .setMessage("Are you sure you want to delete this activity?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    deleteAssignmentFromDatabase(extracurricular);
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteAssignmentFromDatabase(Extracurricular extracurricular) {
        extracurricularViewModel.delete(extracurricular);
    }

    @Override
    public void onFabClick() {
        openAddActivityDialog();
    }

    private void openAddActivityDialog() {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.new_extracurricular, null);

        Spinner spinner = view.findViewById(R.id.spinnerActivityType);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, new String[]{"Club", "Internship", "Research", "Part-Time Employment", "Other"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        EditText organizationName = view.findViewById(R.id.editOrganizationName);
        EditText activityName = view.findViewById(R.id.editActivityName);
        EditText editECDate = view.findViewById(R.id.ECDate);
        editECDate.setOnClickListener(v -> showDateTimePicker(editECDate, getContext()));

        new android.app.AlertDialog.Builder(requireContext())
                .setView(view)
                .setPositiveButton("Add", (dialog, which) -> {
                    saveActivity(activityName, organizationName, spinner, editECDate);
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel())
                .show();
    }

    private void saveActivity(EditText activityName, EditText organizationName, Spinner spinner, EditText editECDate) {
        try {
            String acName = activityName.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US);
            Date taskDueDate = sdf.parse(editECDate.getText().toString());
            String orgName = organizationName.getText().toString();
            String acType = spinner.getSelectedItem().toString();
            Extracurricular newExtracurricular = new Extracurricular(acType, orgName, acName, taskDueDate);
            activityList.add(newExtracurricular);
            adapter.notifyDataSetChanged();
            Context context = getContext();
            if (context != null) {
                new Thread(() -> {
                    AppDatabase db = DatabaseClient.getInstance(context).getAppDatabase();
                    db.extracurricularDao().insert(newExtracurricular);
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showDateTimePicker(final EditText editECDate, Context context) {
        final Calendar currentCalendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, R.style.TimePickerTheme, (view, year, monthOfYear, dayOfMonth) -> {
            currentCalendar.set(year, monthOfYear, dayOfMonth);
            TimePickerDialog timePickerDialog = new TimePickerDialog(context, R.style.TimePickerTheme, (view1, hourOfDay, minute) -> {
                currentCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                currentCalendar.set(Calendar.MINUTE, minute);
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US);
                editECDate.setText(sdf.format(currentCalendar.getTime()));
            }, currentCalendar.get(Calendar.HOUR_OF_DAY), currentCalendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        }, currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}