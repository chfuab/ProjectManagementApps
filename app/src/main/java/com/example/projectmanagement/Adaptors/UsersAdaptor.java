package com.example.projectmanagement.Adaptors;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectmanagement.Interface.OnListItemSelectedListener;
import com.example.projectmanagement.Model.Users;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.example.projectmanagement.R;

import java.util.ArrayList;

public class UsersAdaptor extends FirestoreAdaptor<UsersAdaptor.ViewHolder> {
    private OnListItemSelectedListener mListener;
    private ArrayList<DocumentSnapshot> snapshotResult = new ArrayList<>();
    public UsersAdaptor(Query query, OnListItemSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @NonNull
    @Override
    public UsersAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new UsersAdaptor.ViewHolder(inflater.inflate(R.layout.item_users, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdaptor.ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }
    public ArrayList<DocumentSnapshot> retrieveSnapshot(){
        for (int i=0; i<getItemCount(); i++){
            snapshotResult.add(getSnapshot(i));
        }
        return snapshotResult;
    }
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView userDisplayName, userEmail, numOfJobsOnHand;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userDisplayName = itemView.findViewById(R.id.userDisplayName);
            userEmail = itemView.findViewById(R.id.userEmail);
            numOfJobsOnHand = itemView.findViewById(R.id.numOfJobsOnHand);
        }
        public void bind(final DocumentSnapshot snapshot, final OnListItemSelectedListener listener){
            Users user = snapshot.toObject(Users.class);
            userDisplayName.setText(user.getDisplayName());
            userEmail.setText(user.getEmail());
            numOfJobsOnHand.setText(Integer.toString(user.getNumOfJobsOnHand()));
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUserSelected(snapshot);
                }
            });
        }
    }
}