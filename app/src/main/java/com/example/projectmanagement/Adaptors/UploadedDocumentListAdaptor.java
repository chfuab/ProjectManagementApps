package com.example.projectmanagement.Adaptors;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectmanagement.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.SimpleTimeZone;

public class UploadedDocumentListAdaptor extends RecyclerView.Adapter<UploadedDocumentListAdaptor.WordViewHolder>  {
    private ArrayList<String> documentList;
    private final LayoutInflater mInflater;
    private ArrayList<String> documentURIString;
    private String taskId, creatorUid;
    private HashMap<Integer, String> viewIdUriStringMap = new HashMap<>();
    public UploadedDocumentListAdaptor(Context context, ArrayList<String> docList, ArrayList<String> documentURIString,
                                       String taskId, String creatorUid){
        mInflater = LayoutInflater.from(context);
        this.documentList = docList;
        this.documentURIString = documentURIString;
        this.taskId = taskId;
        this.creatorUid = creatorUid;
    }
    @NonNull
    @Override
    public UploadedDocumentListAdaptor.WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.item_uploaded_documents, parent, false);
        return new UploadedDocumentListAdaptor.WordViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull UploadedDocumentListAdaptor.WordViewHolder holder, int position) {
        holder.uploadedDocumentsTitle.setText(documentList.get(position));
    }

    @Override
    public int getItemCount() {
        return documentList.size();
    }

    public class WordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private FirebaseStorage storage;
        private StorageReference storageReference, docStorageRef;
        CardView uploadedDocumentsBodyAll;
        TextView uploadedDocumentsTitle;
        UploadedDocumentListAdaptor mAdaptor;
        public WordViewHolder(@NonNull View itemView, UploadedDocumentListAdaptor adaptor) {
            super(itemView);
            uploadedDocumentsBodyAll = itemView.findViewById(R.id.uploadedDocumentsBodyAll);
            uploadedDocumentsTitle = itemView.findViewById(R.id.uploadedDocumentsTitle);
            uploadedDocumentsBodyAll.setOnClickListener(this);
            this.mAdaptor = adaptor;
            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.uploadedDocumentsBodyAll:
                    int mPosition = getLayoutPosition();
                    String URIString = documentURIString.get(mPosition);
                    Uri uri = Uri.parse(URIString);
                    docStorageRef = storageReference.child(creatorUid + "/" + taskId + "/" + "documents/" + uri.getLastPathSegment());
                    docStorageRef.getDownloadUrl().addOnSuccessListener(uri1 -> {
                        Log.d("getDownloadURL", uri1.toString());
                    }).addOnFailureListener(e -> {
                        Log.d("getDownloadURL", "failed!");
                    });
                    break;
            }
        }
    }
}
