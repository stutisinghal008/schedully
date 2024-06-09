package com.example.schedully;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedully.model.Dashboard;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<Dashboard> courseList;
    private final OnItemClickListener listener;

    public void setCourses(List<Dashboard> courses) {
        this.courseList = courses;
    }

    public interface OnItemClickListener {
        void onItemClick(Dashboard course);
        void onEditClick(Dashboard course);
        void onDeleteClick(Dashboard course);
    }

    public CourseAdapter(List<Dashboard> courseList, OnItemClickListener listener) {
        this.courseList = courseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Dashboard course = courseList.get(position);
        holder.bind(course, listener, position);
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView textViewCourseName, textViewInstructor, textViewTime, textViewDays, textViewLocation;
        View imageViewEdit, imageViewDelete;
        View contentContainer;
        ImageView beeIcon;
        boolean iconsVisible = false;

        public CourseViewHolder(View itemView) {
            super(itemView);
            textViewCourseName = itemView.findViewById(R.id.textViewCourseName);
            textViewInstructor = itemView.findViewById(R.id.textViewInstructor);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewDays = itemView.findViewById(R.id.textViewDays);
            textViewLocation = itemView.findViewById(R.id.textViewLocation);
            imageViewEdit = itemView.findViewById(R.id.courseEdit);
            imageViewDelete = itemView.findViewById(R.id.courseDelete);
            contentContainer = itemView.findViewById(R.id.text_container);
            beeIcon = itemView.findViewById(R.id.beeIcon);
        }

        public void bind(final Dashboard course, final OnItemClickListener listener, int position) {
            textViewCourseName.setText(course.getName());
            textViewInstructor.setText(course.getInstructor());
            textViewTime.setText(course.getTime());
            textViewDays.setText(course.getDays());
            textViewLocation.setText(course.getLocation());

            if ((position + 1) % 3 == 0) {
                beeIcon.setImageResource(R.drawable.bee2);
            } else if ((position + 1) % 2 == 0) {
                beeIcon.setImageResource(R.drawable.bee3);
            } else {
                beeIcon.setImageResource(R.drawable.finalb);
            }

            itemView.setOnClickListener(v -> {
                if (!iconsVisible) {
                    showIconsAndReduceOpacity();
                } else {
                    hideIconsAndRestoreOpacity();
                }
                listener.onItemClick(course);
            });

            imageViewEdit.setOnClickListener(v -> {
                listener.onEditClick(course);
                hideIconsAndRestoreOpacity();
            });

            imageViewDelete.setOnClickListener(v -> {
                listener.onDeleteClick(course);
                hideIconsAndRestoreOpacity();
            });
        }

        private void showIconsAndReduceOpacity() {
            contentContainer.animate()
                    .alpha(0.3f)
                    .setDuration(300)
                    .withEndAction(() -> {
                        imageViewDelete.setVisibility(View.VISIBLE);
                        imageViewEdit.setVisibility(View.VISIBLE);
                        iconsVisible = true; // Icons are visible
                    })
                    .start();
        }

        private void hideIconsAndRestoreOpacity() {
            imageViewDelete.setVisibility(View.GONE);
            imageViewEdit.setVisibility(View.GONE);
            contentContainer.animate()
                    .alpha(1.0f)
                    .setDuration(300)
                    .start();
            iconsVisible = false; // Icons are hidden
        }
    }
}