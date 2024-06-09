package com.example.schedully;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedully.model.Assignment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder> {

    private List<Assignment> assignmentList;
    private final AssignmentInteractionListener listener;

    public interface AssignmentInteractionListener {
        void onItemClick(Assignment assignment);
        void onEditClick(Assignment assignment);
        void onDeleteClick(Assignment assignment);
    }

    public AssignmentAdapter(List<Assignment> assignmentList, AssignmentInteractionListener listener) {
        this.assignmentList = assignmentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.assignment_layout, parent, false);
        return new AssignmentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        Assignment assignment = assignmentList.get(position);
        holder.bind(assignment, listener);
    }

    @Override
    public int getItemCount() {
        return assignmentList.size();
    }

    public void setAssignments(List<Assignment> assignments) {
        this.assignmentList = assignments;
        notifyDataSetChanged();
    }

    class AssignmentViewHolder extends RecyclerView.ViewHolder {
        CheckBox assignmentCompletedCheckBox;
        TextView assignmentNameTextView, dueDateTextView, courseTextView, timerTextView;
        View assignmentEdit, assignmentDelete;
        View contentContainer;
        boolean iconsVisible = false;
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable updateRunnable;

        AssignmentViewHolder(View itemView) {
            super(itemView);
            assignmentCompletedCheckBox = itemView.findViewById(R.id.assignmentCompletedCheckBox);
            assignmentNameTextView = itemView.findViewById(R.id.assignmentNameTextView);
            dueDateTextView = itemView.findViewById(R.id.dueDateTextView);
            courseTextView = itemView.findViewById(R.id.courseTextView);
            timerTextView = itemView.findViewById(R.id.timerTextView); // Ensure you have this TextView in your layout
            assignmentEdit = itemView.findViewById(R.id.assignmentEdit);
            assignmentDelete = itemView.findViewById(R.id.assignmentDelete);
            contentContainer = itemView.findViewById(R.id.text_container);
        }

        void bind(final Assignment assignment, final AssignmentAdapter.AssignmentInteractionListener listener) {
            assignmentCompletedCheckBox.setChecked(assignment.isCompleted());
            assignmentNameTextView.setText(assignment.getAssignmentName());
            dueDateTextView.setText(new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US).format(assignment.getDueDate()));
            courseTextView.setText(assignment.getCourseName());
            setupAndUpdateTimer(assignment.getDueDate());

            assignmentCompletedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    listener.onDeleteClick(assignment);
                }
            });

            itemView.setOnClickListener(v -> {
                if (!iconsVisible) {
                    showIconsAndReduceOpacity();
                } else {
                    hideIconsAndRestoreOpacity();
                }
                listener.onItemClick(assignment);
            });

            assignmentEdit.setOnClickListener(v ->
            {
                listener.onEditClick(assignment);
                hideIconsAndRestoreOpacity();
            });
            assignmentDelete.setOnClickListener(v -> {
                listener.onDeleteClick(assignment);
                hideIconsAndRestoreOpacity();
            });
        }

        private void setupAndUpdateTimer(Date dueDate) {
            clearTimer();

            updateRunnable = new Runnable() {
                @Override
                public void run() {
                    long currentTimeMillis = System.currentTimeMillis();
                    long dueDateTimeMillis = dueDate.getTime();
                    long timeDiff = dueDateTimeMillis - currentTimeMillis;

                    if (timeDiff > 0) {
                        long hours = timeDiff / (1000 * 60 * 60);
                        long minutes = (timeDiff / (1000 * 60)) % 60;
                        long seconds = (timeDiff / 1000) % 60;
                        timerTextView.setText(String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds));
                        handler.postDelayed(this, 1000);
                    } else {
                        timerTextView.setText("Time's up!");
                    }
                }
            };
            handler.post(updateRunnable);
        }

        void clearTimer() {
            if (updateRunnable != null) {
                handler.removeCallbacks(updateRunnable);
            }
        }

        private void showIconsAndReduceOpacity() {
            contentContainer.animate()
                    .alpha(0.3f)
                    .setDuration(300)
                    .withEndAction(() -> {
                        assignmentEdit.setVisibility(View.VISIBLE);
                        assignmentDelete.setVisibility(View.VISIBLE);
                        iconsVisible = true; // Icons are visible
                    })
                    .start();
        }

        private void hideIconsAndRestoreOpacity() {
            assignmentEdit.setVisibility(View.GONE);
            assignmentDelete.setVisibility(View.GONE);
            contentContainer.animate()
                    .alpha(1.0f)
                    .setDuration(300)
                    .start();
            iconsVisible = false; // Icons are hidden
        }
    }
}
