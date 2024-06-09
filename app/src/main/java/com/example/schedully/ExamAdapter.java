package com.example.schedully;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedully.model.Exam;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.ExamViewHolder> {
    private List<Exam> examList;
    private final ExamInteractionListener listener;

    public interface ExamInteractionListener {
        void onItemClick(Exam exam);
        void onEditClick(Exam exam);
        void onDeleteClick(Exam exam);
    }

    public ExamAdapter(List<Exam> examList, ExamAdapter.ExamInteractionListener listener) {
        this.examList = examList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_item, parent, false);
        return new ExamViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamViewHolder holder, int position) {
        Exam exam = examList.get(position);
        holder.bind(exam, listener);
    }

    @Override
    public int getItemCount() {
        return examList.size();
    }

    public void setExams(List<Exam> exams) {
        this.examList = exams;
        notifyDataSetChanged();
    }

    class ExamViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseName, tvExamDate, tvExamLocation;
        View examEdit, examDelete;

        ExamViewHolder(View itemView) {
            super(itemView);
            tvCourseName = itemView.findViewById(R.id.tvCourseName);
            tvExamDate = itemView.findViewById(R.id.tvExamDate);
            tvExamLocation = itemView.findViewById(R.id.tvExamLocation);
            examEdit = itemView.findViewById(R.id.examEdit);
            examDelete = itemView.findViewById(R.id.examDelete);
        }

        void bind(final Exam exam, final ExamInteractionListener listener) {
            tvCourseName.setText(exam.getCourseName());
            tvExamLocation.setText(exam.getLocation());
            tvExamDate.setText(new SimpleDateFormat("MM/dd/yyyy HH:MM", Locale.US).format(exam.getExamDate()));

            itemView.setOnClickListener(v -> {
                listener.onItemClick(exam);
            });

            examEdit.setOnClickListener(v ->
            {
                listener.onEditClick(exam);
            });
            examDelete.setOnClickListener(v -> {
                listener.onDeleteClick(exam);
            });
        }
    }
}
