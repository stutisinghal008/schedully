package com.example.schedully;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedully.model.Extracurricular;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ExtracurricularAdapter extends RecyclerView.Adapter<ExtracurricularAdapter.ExtracurricularViewHolder> {

    private List<Extracurricular> activityList;
    private final ExtracurricularAdapter.OnItemClickListener listener;

    public void setActivities(List<Extracurricular> activities) {
        this.activityList = activities;
    }


    public ExtracurricularAdapter(List<Extracurricular> activityList, OnItemClickListener listener) {
        this.activityList = activityList;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Extracurricular extracurricular);
        void onEditClick(Extracurricular extracurricular);
        void onDeleteClick(Extracurricular extracurricular);
    }

    @NonNull
    @Override
    public ExtracurricularViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.extracurricular_layout, parent, false);
        return new ExtracurricularViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExtracurricularAdapter.ExtracurricularViewHolder holder, int position) {
        Extracurricular activity = activityList.get(position);
        holder.bind(activity, listener);
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    public static class ExtracurricularViewHolder extends RecyclerView.ViewHolder {
        TextView activityType, organizationName, activityName, activityDate;
        ImageView activityIcon;

        View extracurricularEdit, extracurricularDelete;
        View contentContainer;
        boolean iconsVisible = false;

        public ExtracurricularViewHolder(@NonNull View itemView) {
            super(itemView);
            activityType = itemView.findViewById(R.id.activityType);
            organizationName = itemView.findViewById(R.id.organizationName);
            activityName = itemView.findViewById(R.id.activityName);
            activityDate = itemView.findViewById(R.id.activityDate);
            activityIcon = itemView.findViewById(R.id.image);
            extracurricularEdit = itemView.findViewById(R.id.extracurricularEdit);
            extracurricularDelete = itemView.findViewById(R.id.extracurricularDelete);
            contentContainer = itemView.findViewById(R.id.extracurricular_content);
        }
        public void bind(final Extracurricular activity, final ExtracurricularAdapter.OnItemClickListener listener) {
            switch (activity.getActivityType()) {
                case "Club":
                    activityIcon.setImageResource(R.drawable.club_option2);
                    break;
                case "Internship":
                    activityIcon.setImageResource(R.drawable.internship);
                    break;
                case "Research":
                    activityIcon.setImageResource(R.drawable.research);
                    break;
                case "Employment":
                    activityIcon.setImageResource(R.drawable.employment);
                    break;
                case "Part-Time Employment":
                    activityIcon.setImageResource(R.drawable.parttime);
                    break;
                default:
                    activityIcon.setImageResource(R.drawable.default_activity);
                    break;
            }
            activityType.setText(activity.getActivityType().toString());
            organizationName.setText(activity.getOrganizationName());
            activityName.setText(activity.getTaskName());
            activityDate.setText(new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US).format(activity.getTaskDueDate()));

            itemView.setOnClickListener(v -> {
                if (!iconsVisible) {
                    showIconsAndReduceOpacity();
                } else {
                    hideIconsAndRestoreOpacity();
                }
                listener.onItemClick(activity);
            });

            extracurricularEdit.setOnClickListener(v -> {
                listener.onEditClick(activity);
                hideIconsAndRestoreOpacity();
            });

            extracurricularDelete.setOnClickListener(v -> {
                listener.onDeleteClick(activity);
                hideIconsAndRestoreOpacity();
            });
        }

        private void showIconsAndReduceOpacity() {
            contentContainer.animate()
                    .alpha(0.3f)
                    .setDuration(300)
                    .withEndAction(() -> {
                        extracurricularEdit.setVisibility(View.VISIBLE);
                        extracurricularDelete.setVisibility(View.VISIBLE);
                        iconsVisible = true; // Icons are visible
                    })
                    .start();
        }

        private void hideIconsAndRestoreOpacity() {
            extracurricularDelete.setVisibility(View.GONE);
            extracurricularEdit.setVisibility(View.GONE);
            contentContainer.animate()
                    .alpha(1.0f)
                    .setDuration(300)
                    .start();
            iconsVisible = false; // Icons are hidden
        }
    }
}
