package com.example.projectmanagement.Adaptors;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectmanagement.Adaptors.FirestoreAdaptor;
import com.example.projectmanagement.Model.Task;
import com.example.projectmanagement.R;
import com.example.projectmanagement.Interface.OnListItemSelectedListener;
import com.example.projectmanagement.Utils.UtilsMethods;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TaskAdaptor extends FirestoreAdaptor<TaskAdaptor.ViewHolder> {
    private OnListItemSelectedListener mListener;
    private ArrayList<DocumentSnapshot> snapshotResult = new ArrayList<>();
    public TaskAdaptor(Query query, OnListItemSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @NonNull
    @Override
    public TaskAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new TaskAdaptor.ViewHolder(inflater.inflate(R.layout.item_tasks, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdaptor.ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }
    public ArrayList<DocumentSnapshot> retrieveSnapshot(){
        for (int i=0; i<getItemCount(); i++){
            snapshotResult.add(getSnapshot(i));
        }
        return snapshotResult;
    }
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView taskTitleText, startAndDueDateText, activitiesText,
                assigneeText, taskLevelText, progressReadingText;
        Long startDate, dueDate;
        ProgressBar progressTask;
        public ViewHolder(View itemView){
            super(itemView);
            taskTitleText = itemView.findViewById(R.id.taskList_title);
            startAndDueDateText = itemView.findViewById(R.id.taskList_startEndTime);
            activitiesText = itemView.findViewById(R.id.taskListActivities);
            assigneeText = itemView.findViewById(R.id.taskListAssignee);
            taskLevelText = itemView.findViewById(R.id.taskList_level);
            progressReadingText = itemView.findViewById(R.id.taskList_progressReading);
            progressTask = itemView.findViewById(R.id.taskList_progressBar);
            progressTask.setMax(100);
            progressTask.setMin(0);
        }
        public void bind(final DocumentSnapshot snapshot, final OnListItemSelectedListener listener){
            Task doc = snapshot.toObject(Task.class);
            taskTitleText.setText(doc.getTaskTitle());
            startDate = doc.getStartDate();
            dueDate = doc.getDueDate();
            if (startDate != null && dueDate != null){
                Calendar calendarStart = UtilsMethods.dateConversion(startDate);
                Calendar calendarDue = UtilsMethods.dateConversion(dueDate);
                String startAndDueDate = String.format("%04d/%02d/%02d %02d:%02d  ~  %04d/%02d/%02d %02d:%02d",
                        calendarStart.get(Calendar.YEAR), calendarStart.get(Calendar.MONTH), calendarStart.get(Calendar.DAY_OF_MONTH),
                        calendarStart.get(Calendar.HOUR_OF_DAY), calendarStart.get(Calendar.MINUTE),
                        calendarDue.get(Calendar.YEAR), calendarDue.get(Calendar.MONTH), calendarDue.get(Calendar.DAY_OF_MONTH),
                        calendarDue.get(Calendar.HOUR_OF_DAY), calendarDue.get(Calendar.MINUTE));
                startAndDueDateText.setText(startAndDueDate);
                //convert back the activity code to readable string
            } else {
                startAndDueDateText.setText("No date available yet");
            }
            activitiesText.setText(UtilsMethods.concatArrayListString(doc.getActivities()));
            assigneeText.setText(UtilsMethods.concatArrayListString(doc.getAssigneeName()));
            taskLevelText.setText(doc.getLevel());
            progressReadingText.setText(Integer.toString(doc.getProgressReading()) + "%");
            progressTask.setProgress(doc.getProgressReading());
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTaskSelected(snapshot);
                }
            });
        }


    }
}