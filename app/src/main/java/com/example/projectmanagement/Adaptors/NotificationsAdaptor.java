package com.example.projectmanagement.Adaptors;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectmanagement.Interface.OnListItemSelectedListener;
import com.example.projectmanagement.Model.NotificationsMessage;
import com.example.projectmanagement.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

public class NotificationsAdaptor extends FirestoreAdaptor<NotificationsAdaptor.ViewHolder> {
    private OnListItemSelectedListener mListener;

    public NotificationsAdaptor(Query query, OnListItemSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @NonNull
    @Override
    public NotificationsAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new NotificationsAdaptor.ViewHolder(inflater.inflate(R.layout.item_notifications_message, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsAdaptor.ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private OnListItemSelectedListener mListener;
        private DocumentSnapshot mSnapshot;
        TextView title, activity, task, sender, description, currentMembers;
        Button confirm, cancel;
        RelativeLayout notificationParent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.notificationsTitle);
            sender = itemView.findViewById(R.id.notificationsSender);
            description = itemView.findViewById(R.id.notificationsDescription);
            confirm = itemView.findViewById(R.id.notificationsConfirmButton);
            cancel = itemView.findViewById(R.id.notificationsCancelButton);
            notificationParent = itemView.findViewById(R.id.notificationsParent);
        }
        public void bind(final DocumentSnapshot snapshot, final OnListItemSelectedListener listener){
            mListener = listener;
            mSnapshot = snapshot;
            NotificationsMessage notifications = snapshot.toObject(NotificationsMessage.class);
            title.setText(notifications.getNotificationTitle());
            String senderString = notifications.getSenderUid();
            sender.setText(senderString);

            description.setText(notifications.getDescription());
            confirm.setOnClickListener(this);
            cancel.setOnClickListener(this);
            notificationParent.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onNotificationSelected(mSnapshot, v);
            }
        }
    }
}