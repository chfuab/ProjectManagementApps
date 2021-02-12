package com.example.projectmanagement.Interface;

import android.view.View;

import com.google.firebase.firestore.DocumentSnapshot;

public interface OnListItemSelectedListener {
    void onDocumentSelected(DocumentSnapshot doc);
    void onMessageSelected(DocumentSnapshot message);
    void onTaskSelected(DocumentSnapshot event);
    void onActivitySelected (DocumentSnapshot activity);
    void onUserSelected(DocumentSnapshot user);
    void onNotificationSelected(DocumentSnapshot notification, View v);
}
