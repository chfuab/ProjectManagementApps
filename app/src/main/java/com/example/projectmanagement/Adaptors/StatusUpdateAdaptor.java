package com.example.projectmanagement.Adaptors;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectmanagement.Interface.DialogDataListener;
import com.example.projectmanagement.Interface.OnListItemSelectedListener;
import com.example.projectmanagement.Model.CommentOnStatusUpdate;
import com.example.projectmanagement.Model.StatusUpdate;
import com.example.projectmanagement.Model.Users;
import com.example.projectmanagement.R;
import com.example.projectmanagement.TaskDetails;
import com.example.projectmanagement.Utils.UtilsMethods;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.Distribution;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Comment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class StatusUpdateAdaptor extends FirestoreAdaptor<StatusUpdateAdaptor.ViewHolder> {
    private OnListItemSelectedListener mListener;
    public Context context;
    public Context mContext;
    private ArrayList<DocumentSnapshot> snapshotResult = new ArrayList<>();
    public StatusUpdateAdaptor(Query query, OnListItemSelectedListener listener, Context mContext) {
        super(query);
        this.mListener = listener;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public StatusUpdateAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new StatusUpdateAdaptor.ViewHolder(inflater.inflate(R.layout.item_status_updates, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StatusUpdateAdaptor.ViewHolder holder, int position) {
        try {
            holder.bind(getSnapshot(position), mListener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public ArrayList<DocumentSnapshot> retrieveSnapshot(){
        for (int i=0; i<getItemCount(); i++){
            snapshotResult.add(getSnapshot(i));
        }
        return snapshotResult;
    }
    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, DialogDataListener {
        private FirebaseUser mFirebaseUser;
        private FirebaseAuth mAuth;
        private FirebaseStorage storage;
        private StorageReference storageReference, docStorageRef;
        private FirebaseFirestore mFirestore;
        private CollectionReference tasksRef, updatesRef, commentsRef;
        private DocumentReference updateDocRef;
        private UploadedDocumentListAdaptor mAdaptor;
        private CommentAdaptor mAdaptorComment;
        private Query commentQuery;
        Context context;
        TextView statusUpdateInfoTextName, statusUpdateInfoTextDate,
                statusUpdateContentText, statusUpdateActionBarNumComment, statusUpdateActionBarNumLike,
                statueUpdateContentDocumentTitle, statusUpdateShowAllCommentText;
        ImageView statusUpdateContentImage;
        ImageButton statusUpdateActionBarLike, statusUpdateActionBarShare, statusUpdateActionBarReply;
        LinearLayout statusUpdateShowAllCommentLayout, statusUpdateShowLessCommentLayout;
        RecyclerView statueUpdateContentDocumentTitleRecycler, statusUpdateShowAllCommentRecyclerView;
        ArrayList<String> fileNames = new ArrayList<>();
        ArrayList<String> docURIString = new ArrayList<>();
        HashMap<String, ArrayList<String>> documentURI = new HashMap<>();
        HashMap<Uri, ArrayList<String>> documentURIUri = new HashMap<>();
        HashMap<String, ArrayList<String>> imageURI = new HashMap<>();
        int numOfFiles, numOfLikes, numOfComment;
        String textDocuments = "";
        String taskId, creatorUid, statusUpdateId, creatorName;
        String firebaseUid;
        Boolean documentTitleClick = false;
        Boolean commentClick = false;
        Boolean likeClicked = false;
        HashMap<String, Boolean> likesFromDatabase = new HashMap<>();
        Boolean likeFromDatabase;
        Dialog dialog;
        private static final int LIMIT = 20;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            statusUpdateInfoTextName = itemView.findViewById(R.id.statusUpdateInfoTextName);
            statusUpdateInfoTextDate = itemView.findViewById(R.id.statusUpdateInfoTextDate);
            statusUpdateContentText = itemView.findViewById(R.id.statueUpdateContentText);
            statueUpdateContentDocumentTitle = itemView.findViewById(R.id.statueUpdateContentDocumentTitle);
            statusUpdateActionBarNumLike = itemView.findViewById(R.id.statusUpdateActionBarNumLike);
            statusUpdateActionBarNumComment = itemView.findViewById(R.id.statusUpdateActionBarNumComment);
            statusUpdateContentImage = itemView.findViewById(R.id.statueUpdateContentImage);
            statusUpdateActionBarLike = itemView.findViewById(R.id.statusUpdateActionBarLike);
            statusUpdateActionBarShare = itemView.findViewById(R.id.statusUpdateActionBarShare);
            statusUpdateActionBarReply = itemView.findViewById(R.id.statusUpdateActionBarReply);
            statusUpdateShowAllCommentLayout = itemView.findViewById(R.id.statusUpdateShowAllCommentLayout);
            statusUpdateShowAllCommentText = itemView.findViewById(R.id.statusUpdateShowAllCommentText);
            statusUpdateShowLessCommentLayout = itemView.findViewById(R.id.statusUpdateShowLessCommentLayout);
            statueUpdateContentDocumentTitleRecycler = itemView.findViewById(R.id.statueUpdateContentDocumentTitleRecycler);
            statusUpdateShowAllCommentRecyclerView = itemView.findViewById(R.id.statusUpdateShowAllCommentRecyclerView);
            statusUpdateActionBarLike.setOnClickListener(this);
            statusUpdateActionBarShare.setOnClickListener(this);
            statusUpdateActionBarReply.setOnClickListener(this);
            statusUpdateShowAllCommentLayout.setOnClickListener(this);
            statusUpdateShowAllCommentText.setOnClickListener(this);
            statusUpdateShowLessCommentLayout.setOnClickListener(this);
            statueUpdateContentDocumentTitle.setOnClickListener(this);
            mAuth = FirebaseAuth.getInstance();
            mFirebaseUser = mAuth.getCurrentUser();
            firebaseUid = mFirebaseUser.getUid();
            context = itemView.getContext();
            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();
            mFirestore = FirebaseFirestore.getInstance();
            tasksRef = mFirestore.collection("Tasks");
            Log.d("ViewHolderConstructor", "trig");

        }
        public void bind(final DocumentSnapshot snapshot, final OnListItemSelectedListener listener) throws IOException {
            StatusUpdate update = snapshot.toObject(StatusUpdate.class);
            taskId = update.getOfWhichTaskId();
            creatorUid = update.getCreaterUid();
            statusUpdateId = update.getStatusUpdateId();
            updatesRef = tasksRef.document(taskId).collection("Updates");
            updateDocRef = updatesRef.document(statusUpdateId);
            commentQuery = updateDocRef.collection("Comments").orderBy("timeCreated", Query.Direction.DESCENDING)
                    .limit(LIMIT);
            documentURI = update.getDocumentURI();
            imageURI = update.getImageURI();
            fileNames = update.getDocumentDisplayName();
            numOfLikes = update.getNumOfLike();
            numOfComment = update.getNumOfComment();
            likesFromDatabase = update.getLike();
            if (likesFromDatabase.get(firebaseUid) != null){
                likeFromDatabase = likesFromDatabase.get(firebaseUid);
            } else {
                likesFromDatabase.put(firebaseUid, false);
                likeFromDatabase = false;
            }
            for (String URIString : documentURI.keySet()){
                docURIString.add(URIString);
            }
            numOfFiles = fileNames.size();
            if (numOfFiles > 1){
                textDocuments = fileNames.get(0) + "and more...";
            } else {
                textDocuments = fileNames.get(0);
            }
            statueUpdateContentDocumentTitle.setText(textDocuments);
            creatorName = update.getCreatorName();
            statusUpdateInfoTextName.setText(creatorName);
            Calendar calendarStart = UtilsMethods.dateConversion(update.getTimeCreated());
            String timeCreatedText = String.format("%04d/%02d/%02d %02d:%02d",
                    calendarStart.get(Calendar.YEAR), calendarStart.get(Calendar.MONTH), calendarStart.get(Calendar.DAY_OF_MONTH),
                    calendarStart.get(Calendar.HOUR_OF_DAY), calendarStart.get(Calendar.MINUTE));
            statusUpdateInfoTextDate.setText(timeCreatedText);
            statusUpdateContentText.setText(update.getStatusUpdateDescription());
            if (!imageURI.isEmpty()){
                statusUpdateContentImage.setVisibility(View.VISIBLE);
            }
            //statusUpdateContentDocumentTitle
            statusUpdateActionBarNumComment.setText(Integer.toString(numOfComment));
            //statusUpdateContentImage
            statusUpdateActionBarNumLike.setText(Integer.toString(numOfLikes));
            if (!likeFromDatabase){
                statusUpdateActionBarLike.setImageDrawable(context.getDrawable(R.drawable.ic_star_28dp));
            } else {
                statusUpdateActionBarLike.setImageDrawable(context.getDrawable(R.drawable.ic_star_filled_28dp));
            }
            mAdaptorComment = new CommentAdaptor(commentQuery, null);
            statusUpdateShowAllCommentRecyclerView.setAdapter(mAdaptorComment);
            statusUpdateShowAllCommentRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        }
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.statueUpdateContentDocumentTitle:
                    if (!documentTitleClick){
                        statueUpdateContentDocumentTitleRecycler.setVisibility(View.VISIBLE);
                        mAdaptor = new UploadedDocumentListAdaptor(context, fileNames, docURIString, taskId, creatorUid);
                        statueUpdateContentDocumentTitleRecycler.setAdapter(mAdaptor);
                        statueUpdateContentDocumentTitleRecycler.setLayoutManager(new LinearLayoutManager(context));
                        documentTitleClick = true;
                    } else {
                        statueUpdateContentDocumentTitleRecycler.setVisibility(View.GONE);
                        documentTitleClick = false;
                    }
                    break;
                case R.id.statusUpdateActionBarLike:
                    if (!likeFromDatabase){
                        updateDocRef.update("numOfLike", numOfLikes + 1);
                        likesFromDatabase.replace(firebaseUid, true);
                        updateDocRef.update("like", likesFromDatabase);
                    } else {
                        updateDocRef.update("numOfLike", numOfLikes - 1);
                        likesFromDatabase.replace(firebaseUid, false);
                        updateDocRef.update("like", likesFromDatabase);
                    }
                    break;
                case R.id.statusUpdateShowAllCommentLayout:
                    mAdaptorComment.startListening();
                    statusUpdateShowAllCommentRecyclerView.setVisibility(View.VISIBLE);
                    statusUpdateShowLessCommentLayout.setVisibility(View.VISIBLE);
                    statusUpdateShowAllCommentLayout.setVisibility(View.GONE);
                    break;
                case R.id.statusUpdateShowLessCommentLayout:
                    mAdaptorComment.stopListening();
                    statusUpdateShowAllCommentRecyclerView.setVisibility(View.GONE);
                    statusUpdateShowLessCommentLayout.setVisibility(View.GONE);
                    statusUpdateShowAllCommentLayout.setVisibility(View.VISIBLE);
                    break;
                case R.id.statusUpdateActionBarReply:
                    Bundle bundleTemp = new Bundle();
                    bundleTemp.putString("firebaseDisplayName", creatorName);
                    dialog = UtilsMethods.createDialog(context, 0, null, R.layout.fragment_create_comment, null, this, bundleTemp);
                    dialog.show();
                    break;
            }
        }
        @Override
        public void getDialogData(Bundle bundle) {
            Log.d("getDialogData", "triggered");
            Log.d("statusUpdateId", statusUpdateId);

            String commentText = bundle.getString("commentEdit");
            Calendar calendar = Calendar.getInstance();
            Long timeCreated = calendar.getTimeInMillis();
            CommentOnStatusUpdate newComment = new CommentOnStatusUpdate(firebaseUid, creatorName, timeCreated, commentText);
            commentsRef = updatesRef.document(statusUpdateId).collection("Comments");
            commentsRef.add(newComment).addOnSuccessListener(documentReference -> {
                String commentId = documentReference.getId();
                commentsRef.document(commentId).update("commentId", commentId);
            });
        }
    }
}
