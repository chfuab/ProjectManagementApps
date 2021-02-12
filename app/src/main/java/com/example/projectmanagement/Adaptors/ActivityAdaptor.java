package com.example.projectmanagement.Adaptors;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;


import com.example.projectmanagement.Adaptors.FirestoreAdaptor;
import com.example.projectmanagement.Interface.OnListItemSelectedListener;
import com.example.projectmanagement.Model.Activity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projectmanagement.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ActivityAdaptor extends FirestoreAdaptor<ActivityAdaptor.ViewHolder> {
    private OnListItemSelectedListener mListener;
    private ArrayList<DocumentSnapshot> snapshotResult = new ArrayList<>();
    public ActivityAdaptor(Query query, OnListItemSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_activities, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityAdaptor.ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    public ArrayList<DocumentSnapshot> retrieveSnapshot(){
        for (int i=0; i<getItemCount(); i++){
            snapshotResult.add(getSnapshot(i));
        }
        return snapshotResult;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView title, timeCreated, latestEvent, status, progressReading, assignee;
        ProgressBar progressActivity;
        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.activityList_title);
            timeCreated = itemView.findViewById(R.id.activityList_timeCreated);
            latestEvent = itemView.findViewById(R.id.activityList_latestEvent);
            status = itemView.findViewById(R.id.activityList_statusContent);
            progressReading = itemView.findViewById(R.id.activityList_progressReading);
            assignee = itemView.findViewById(R.id.activityList_assignee);
            progressActivity = itemView.findViewById(R.id.activityList_progress);
            progressActivity.setMax(100);
            progressActivity.setMin(0);
        }
        public void bind(final DocumentSnapshot snapshot, final OnListItemSelectedListener listener){
            Activity doc = snapshot.toObject(Activity.class);
            title.setText(doc.getActivityTitle());
            Date date = new Date(doc.getTimeCreated());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            String updatedAt = String.format("%04d/%02d/%02d %02d:%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            timeCreated.setText(updatedAt);
            latestEvent.setText(doc.getLatestEvent());
            status.setText(doc.getStatus());
            progressReading.setText(Integer.toString(doc.getProgressReading()));
            progressActivity.setProgress(doc.getProgressReading());
            ArrayList<String> assigneeList = doc.getAssignee();
            StringBuilder assigneeText = new StringBuilder();
            for (String name : assigneeList){
                assigneeText.append(name).append(", ");
            }
            assignee.setText(assigneeText);
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onActivitySelected(snapshot);
                }
            });
        }
    }
}