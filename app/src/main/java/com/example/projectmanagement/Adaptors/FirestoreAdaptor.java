package com.example.projectmanagement.Adaptors;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public abstract class FirestoreAdaptor <VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> implements EventListener<QuerySnapshot> {

    private static final String TAG = "Firestore Adapter";
    private Query mQuery;
    private ListenerRegistration mRegistration;
    private ArrayList<DocumentSnapshot> mSnapshots = new ArrayList<>();
    private ArrayList<DocumentSnapshot> mSnapshotsNotifications = new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private String firebaseUid;
    public FirestoreAdaptor(Query query) {
        mQuery = query;
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        firebaseUid = mFirebaseUser.getUid();
    }

    @Override
    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
        Log.d("onEventTriggered?", "Triggered");
        if (error != null){
            return;
        }
        for (DocumentChange change : querySnapshot.getDocumentChanges()){
            DocumentSnapshot doc = change.getDocument();
            switch (change.getType()) {
                case ADDED:
                    onDocumentAdded(change, mSnapshots);
                    break;
                case MODIFIED:
                    onDocumentModified(change, mSnapshots);
                    break;
                case REMOVED:
                    onDocumentRemoved(change, mSnapshots);
                    break;
            }
        }
    }
    protected void onDocumentAdded(DocumentChange change, ArrayList<DocumentSnapshot> snapshots) {
        snapshots.add(change.getNewIndex(), change.getDocument());
        notifyItemInserted(change.getNewIndex());
    }
    protected void onDocumentModified(DocumentChange change, ArrayList<DocumentSnapshot> snapshots) {
        if (change.getOldIndex() == change.getNewIndex()) {
            snapshots.set(change.getOldIndex(), change.getDocument());
            notifyItemChanged(change.getOldIndex());
        } else {
            snapshots.remove(change.getOldIndex());
            snapshots.add(change.getNewIndex(), change.getDocument());
            notifyItemMoved(change.getOldIndex(), change.getNewIndex());
        }
    }
    protected void onDocumentRemoved(DocumentChange change, ArrayList<DocumentSnapshot> snapshots) {
        snapshots.remove(change.getOldIndex());
        notifyItemRemoved(change.getOldIndex());
    }
    public void startListening() {
        if (mQuery != null && mRegistration == null) {
            mRegistration = mQuery.addSnapshotListener(this);
        }
    }

    public void stopListening() {
        if (mRegistration != null) {
            mRegistration.remove();
            mRegistration = null;
        }

        mSnapshots.clear();
        mSnapshotsNotifications.clear();
        notifyDataSetChanged();
    }

    public void setQuery(Query query) {
        // Stop listening
        stopListening();

        // Clear existing data
        mSnapshots.clear();
        mSnapshotsNotifications.clear();
        notifyDataSetChanged();

        // Listen to new query
        mQuery = query;
        startListening();
    }

    @Override
    public int getItemCount() {
        return mSnapshots.size();
    }

    public int getItemCountNotifications(){return mSnapshotsNotifications.size();}

    protected DocumentSnapshot getSnapshot(int index) {
        return mSnapshots.get(index);
    }

    protected DocumentSnapshot getSnapshotNotifications(int index){
        return mSnapshotsNotifications.get(index);
    }

    protected void onError(FirebaseFirestoreException e) {};

    protected void onDataChanged() {}
}
