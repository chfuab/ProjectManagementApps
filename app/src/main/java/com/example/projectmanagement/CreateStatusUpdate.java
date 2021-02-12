package com.example.projectmanagement;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.DocumentsContract;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectmanagement.Adaptors.AddDocumentListAdaptor;
import com.example.projectmanagement.Model.StatusUpdate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.Distribution;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import javax.net.ssl.SSLEngineResult;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateStatusUpdate#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateStatusUpdate extends Fragment implements View.OnClickListener {
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private CollectionReference activitiesRef, notificationsRef, tasksRef, usersRef, updatesRef;
    private DocumentReference taskDocRef;
    private FirebaseStorage storage;
    private StorageReference storageReference, docStorageRef;
    private UploadTask uploadTask;
    private String firebaseUid;
    private String taskId, taskScope, taskDescription, taskTitle;
    private String fromWhichUpdate;
    private HashMap<String, Uri> fileNameAndURI = new HashMap<>();
    private ArrayList<String> fileNameList = new ArrayList<>();
    private ArrayList<String> userNames = new ArrayList<>();
    private AddDocumentListAdaptor mAdaptor;
    LinearLayout taskDetailsForUpdateLayout;
    EditText contentBodyEdit;
    TextView taskDetailsForUpdateTitle, taskDetailsForUpdateScope, taskDetailsForUpdateDescription;
    CardView addDocuments;
    RecyclerView addDocumentsRecyclerView;
    FloatingActionButton fabTaskUpdateConfirm;
    private static final int CREATE_FILE = 1;
    private static final int PICK_PDF_FILE = 2;
    private static final int OPEN_FILE = 3;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CreateStatusUpdate() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateStatusUpdate.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateStatusUpdate newInstance(String param1, String param2) {
        CreateStatusUpdate fragment = new CreateStatusUpdate();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            taskId = getArguments().getString("taskId");
            taskScope = getArguments().getString("taskScope");
            taskDescription = getArguments().getString("taskDescription");
            taskTitle = getArguments().getString("taskTitle");
            userNames = getArguments().getStringArrayList("UsersName");
            if (getArguments().getString("fromWhichUpdate") != null){
                fromWhichUpdate = getArguments().getString("fromWhichUpdate");
            }
        }
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        firebaseUid = mFirebaseUser.getUid();
        iniFireStore();
        iniStorage();
        mAdaptor = new AddDocumentListAdaptor(getContext(), fileNameList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_create_status_update, container, false);
        contentBodyEdit = root.findViewById(R.id.contentBodyEdit);
        addDocuments = root.findViewById(R.id.addDocuments);
        addDocuments.setOnClickListener(this);
        addDocumentsRecyclerView = root.findViewById(R.id.addDocumentsRecyclerView);
        addDocumentsRecyclerView.setAdapter(mAdaptor);
        addDocumentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fabTaskUpdateConfirm = root.findViewById(R.id.fabTaskUpdateConfirm);
        taskDetailsForUpdateTitle = root.findViewById(R.id.taskDetailsForUpdateTitle);
        taskDetailsForUpdateScope = root.findViewById(R.id.taskDetailsForUpdateScope);
        taskDetailsForUpdateDescription = root.findViewById(R.id.taskDetailsForUpdateDescription);
        fabTaskUpdateConfirm.setOnClickListener(this);
        taskDetailsForUpdateTitle.setText(taskTitle);
        taskDetailsForUpdateScope.setText(taskScope);
        taskDetailsForUpdateDescription.setText(taskDescription);
        return root;
    }
    private void iniFireStore(){
        mFirestore = FirebaseFirestore.getInstance();
        activitiesRef = mFirestore.collection("Activities");
        notificationsRef = mFirestore.collection("Notifications");
        tasksRef = mFirestore.collection("Tasks");
        usersRef = mFirestore.collection("Users");
        updatesRef = mFirestore.collection("Tasks").document(taskId).collection("Updates");
    }
    private void iniStorage(){
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addDocuments:
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/pdf");
                startActivityForResult(intent, PICK_PDF_FILE);
                break;
            case R.id.fabTaskUpdateConfirm:
                String updateContent = contentBodyEdit.getText().toString();
                HashMap<String, ArrayList<String>> documentsUri = new HashMap<>();
                HashMap<String, ArrayList<String>> imagesUri = new HashMap<>();
                String userName = userNames.get(0);
                Long timeCreated = Calendar.getInstance().getTimeInMillis();
                HashMap<String, Boolean> like = new HashMap<>();
                like.put(firebaseUid, false);
                String followFromWhichUpdate = fromWhichUpdate;
                for (String parseFileName : fileNameAndURI.keySet()){
                    if (!fileNameList.contains(parseFileName)){
                        fileNameAndURI.remove(parseFileName);
                    }
                }
                for (Uri uri : fileNameAndURI.values()){
                    String pathString = firebaseUid + "/" + taskId + "/" + "documents/" + uri.getLastPathSegment();
                    docStorageRef = storageReference.child(pathString);
                    uploadTask = docStorageRef.putFile(uri);
                    uploadTask.addOnSuccessListener(taskSnapshot -> {
                        Log.d("uploadTask", "Successful");
                    }).addOnFailureListener(e -> {
                        Log.d("uploadTask", "Failed!!");
                    });
                    documentsUri.put(uri.toString(), new ArrayList<String>());
                }
                StatusUpdate statusUpdate = new StatusUpdate(taskId, documentsUri, imagesUri, firebaseUid, userName, updateContent, timeCreated,
                        like, followFromWhichUpdate, fileNameList, 0, 0);
                updatesRef.add(statusUpdate).addOnSuccessListener(documentReference -> {
                    String updateId = documentReference.getId();
                    updatesRef.document(updateId).update("statusUpdateId", updateId);
                });
                break;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        if (requestCode == PICK_PDF_FILE
                && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                //openFile(uri);
                String parseFileName = queryName(getContext().getContentResolver(), uri);
                Long fileSize = querySize(getContext().getContentResolver(), uri);
                //fileNameAndSize.put(parseFileName, fileSize);
                fileNameAndURI.put(parseFileName, uri);
                int listSize = fileNameList.size();
                fileNameList.add(parseFileName);
                Log.d("parseFileName", parseFileName);
                addDocumentsRecyclerView.getAdapter().notifyItemInserted(listSize);
                addDocumentsRecyclerView.smoothScrollToPosition(listSize);
            }
        }
    }
    public void openFile(Uri uriToLoad) {
        Intent intent = new Intent();
        intent.setDataAndType(uriToLoad, "application/pdf");
        Intent chooserIntent = Intent.createChooser(intent, "Open Report");
        startActivity(chooserIntent);
    }
    private String queryName(ContentResolver resolver, Uri uri) {
        Cursor returnCursor = resolver.query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        Long size = returnCursor.getLong(sizeIndex);
        returnCursor.close();
        return name;
    }
    private Long querySize(ContentResolver resolver, Uri uri) {
        Cursor returnCursor = resolver.query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        Long size = returnCursor.getLong(sizeIndex);
        returnCursor.close();
        return size;
    }
}