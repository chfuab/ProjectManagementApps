package com.example.projectmanagement.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectmanagement.Interface.OnListItemSelectedListener;
import com.example.projectmanagement.Model.CommentOnStatusUpdate;
import com.example.projectmanagement.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Comment;

import java.util.Calendar;
import java.util.Date;

public class CommentAdaptor extends FirestoreAdaptor<CommentAdaptor.ViewHolder> {
    private OnListItemSelectedListener mListener;
    public Context context;
    public CommentAdaptor(Query query, OnListItemSelectedListener listener) {
        super(query);
        this.mListener = listener;
    }

    @NonNull
    @Override
    public CommentAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new CommentAdaptor.ViewHolder(inflater.inflate(R.layout.item_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdaptor.ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView commentWhole;
        TextView commentBodyName, commentBodyDate, commentBodyContent;
        String commentName, commentDate, commentContent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            commentBodyName = itemView.findViewById(R.id.CommentBodyName);
            commentBodyDate = itemView.findViewById(R.id.CommentBodyDate);
            commentBodyContent = itemView.findViewById(R.id.CommentBodyContent);
        }
        public void bind(final DocumentSnapshot snapshot, final OnListItemSelectedListener listener){
            CommentOnStatusUpdate comment = snapshot.toObject(CommentOnStatusUpdate.class);
            commentName = comment.getCreatorDisplayName();
            commentBodyName.setText(commentName);
            Long commentDateValue = comment.getTimeCreated();
            Calendar calendar = Calendar.getInstance();
            Date date = new Date(commentDateValue);
            calendar.setTime(date);
            commentDate = String.format("%04d/%02d/%02d %02d:%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            commentBodyDate.setText(commentDate);
            commentContent = comment.getContent();
            commentBodyContent.setText(commentContent);
        }
    }
}
