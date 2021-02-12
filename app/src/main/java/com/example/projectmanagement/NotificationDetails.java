package com.example.projectmanagement;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.projectmanagement.Adaptors.ActivityAdaptor;
import com.example.projectmanagement.Adaptors.NotificationsAdaptor;
import com.example.projectmanagement.Adaptors.TaskAdaptor;
import com.example.projectmanagement.Adaptors.UsersAdaptor;
import com.example.projectmanagement.Interface.OnListItemSelectedListener;
import com.example.projectmanagement.Model.Activity;
import com.example.projectmanagement.Model.Task;
import com.example.projectmanagement.Model.Users;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationDetails#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationDetails extends Fragment implements EventListener<DocumentSnapshot>, OnListItemSelectedListener {
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private TaskAdaptor mAdaptorTasks;
    private ActivityAdaptor mAdaptorActivities;
    private ActivityAdaptor mAdaptorActivitiesReceiver;
    private TaskAdaptor mAdaptorTasksReceiver;
    private CollectionReference activitiesRef, notificationsRef, tasksRef, usersRef;
    private DocumentReference mActivityDocRef, mTaskDocRef, mUserDocRef;
    private ListenerRegistration mRegistration;
    String notificationId, activityId, taskId, firebaseUid, notificationTitle;
    private Query mSenderActivityQuery, mSenderTaskQuery, mReceiverActivitiesQuery, mReceiverTasksQuery;
    private TextView notificationTitleNotificationDetails, mainTextToBeAddedNewNotificationDetails,
            mainTextToBeIncludedNewNotificationDetails, mainTextToBeInvitedActivityNotificationDetails,
            mainTextToBeInvitedTaskNotificationDetails, mainTextToBeAddedSubTaskNotificationDetails,
            mainTextToBeAddedSuperiorTaskNotificationDetails, receiverMainTextActivityNotificationDetails,
            receiverMainTextTaskNotificationDetails, mainTextToBeAddedInActivitiesNotificationDetails;
    private RecyclerView recycler_senderActivitySummaryNotificationDetails, recycler_senderTaskSummaryNotificationDetails,
            recycler_receiverActivitySummaryNotificationDetails, recycler_receiverTaskSummaryNotificationDetails;
    private ArrayList<String> senderActivityDisplayed = new ArrayList<>();
    private ArrayList<String> senderTaskDisplayed = new ArrayList<>();
    private ArrayList<String> receiverActivitiesDisplayed = new ArrayList<>();
    private ArrayList<String> receiverTasksDisplayed = new ArrayList<>();
    private static final int LIMIT = 20;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NotificationDetails() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationDetails.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationDetails newInstance(String param1, String param2) {
        NotificationDetails fragment = new NotificationDetails();
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
            activityId = getArguments().getString("activityId");
            taskId = getArguments().getString("taskId");
            notificationTitle = getArguments().getString("notificationTitle");
            notificationId = getArguments().getString("notificationId");
        }
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        firebaseUid = mFirebaseUser.getUid();
        iniFireStore();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notification_details, container, false);
        notificationTitleNotificationDetails = root.findViewById(R.id.notificationTitleNotificationDetails);
        notificationTitleNotificationDetails.setText(notificationTitle);
        mainTextToBeAddedNewNotificationDetails = root.findViewById(R.id.mainTextToBeAddedNewNotificationDetails);
        mainTextToBeIncludedNewNotificationDetails = root.findViewById(R.id.mainTextToBeIncludedNewNotificationDetails);
        mainTextToBeInvitedActivityNotificationDetails = root.findViewById(R.id.mainTextToBeInvitedActivityNotificationDetails);
        mainTextToBeInvitedTaskNotificationDetails = root.findViewById(R.id.mainTextToBeInvitedTaskNotificationDetails);
        mainTextToBeAddedSubTaskNotificationDetails = root.findViewById(R.id.mainTextToBeAddedSubTaskNotificationDetails);
        mainTextToBeAddedSuperiorTaskNotificationDetails = root.findViewById(R.id.mainTextToBeAddedSuperiorTaskNotificationDetails);
        mainTextToBeAddedInActivitiesNotificationDetails = root.findViewById(R.id.mainTextToBeAddedInActivitiesNotificationDetails);
        receiverMainTextActivityNotificationDetails = root.findViewById(R.id.receiverMainTextActivityNotificationDetails);
        receiverMainTextTaskNotificationDetails = root.findViewById(R.id.receiverMainTextTaskNotificationDetails);
        recycler_senderActivitySummaryNotificationDetails = root.findViewById(R.id.recycler_senderActivitySummaryNotificationDetails);
        recycler_senderActivitySummaryNotificationDetails.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdaptorActivities = new ActivityAdaptor(mSenderActivityQuery, this);
        recycler_senderActivitySummaryNotificationDetails.setAdapter(mAdaptorActivities);
        recycler_senderTaskSummaryNotificationDetails = root.findViewById(R.id.recycler_senderTaskSummaryNotificationDetails);
        recycler_senderTaskSummaryNotificationDetails.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdaptorTasks = new TaskAdaptor(mSenderTaskQuery, this);
        recycler_senderTaskSummaryNotificationDetails.setAdapter(mAdaptorTasks);
        recycler_receiverActivitySummaryNotificationDetails = root.findViewById(R.id.recycler_receiverActivitySummaryNotificationDetails);
        recycler_receiverTaskSummaryNotificationDetails = root.findViewById(R.id.recycler_receiverTaskSummaryNotificationDetails);
        getActivitiesAndTasksDetails();
        return root;
    }
    private void iniFireStore(){
        mFirestore = FirebaseFirestore.getInstance();
        activitiesRef = mFirestore.collection("Activities");
        notificationsRef = mFirestore.collection("Notifications");
        tasksRef = mFirestore.collection("Tasks");
        usersRef = mFirestore.collection("Users");
        if (firebaseUid != null){
            mUserDocRef = usersRef.document(firebaseUid);
        }
        mSenderActivityQuery = activitiesRef.orderBy("timeCreated", Query.Direction.DESCENDING)
                .whereEqualTo("activityId", activityId)
                .limit(LIMIT);
        mSenderTaskQuery = tasksRef.orderBy("timeCreated", Query.Direction.DESCENDING)
                .whereEqualTo("taskId", taskId)
                .limit(LIMIT);
    }
    @Override
    public void onStop() {
        if (mAdaptorActivities != null){
            mAdaptorActivities.stopListening();
        }
        if (mAdaptorTasks != null){
            mAdaptorTasks.stopListening();
        }
        super.onStop();
    }
    @Override
    public void onStart() {
        super.onStart();
        if (mAdaptorActivities != null){
            mAdaptorActivities.startListening();
        }
        if (mAdaptorTasks != null){
            mAdaptorTasks.startListening();
        }
        if (firebaseUid != null){
            mUserDocRef.addSnapshotListener(this);
        }
    }

    @Override
    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
        receiverActivitiesDisplayed = value.toObject(Users.class).getActivities();
        receiverTasksDisplayed = value.toObject(Users.class).getTasks();
        if (receiverActivitiesDisplayed.size() > 0){
            mReceiverActivitiesQuery =  activitiesRef.orderBy("timeCreated", Query.Direction.DESCENDING)
                    .whereIn("activityId", receiverActivitiesDisplayed)
                    .limit(LIMIT);
        }
        if (receiverTasksDisplayed.size() > 0){
            mReceiverTasksQuery =  tasksRef.orderBy("timeCreated", Query.Direction.DESCENDING)
                    .whereIn("taskId", receiverTasksDisplayed)
                    .limit(LIMIT);
        }
        mAdaptorActivitiesReceiver = new ActivityAdaptor(mReceiverActivitiesQuery, this);
        mAdaptorTasksReceiver = new TaskAdaptor(mReceiverTasksQuery, this);
        mAdaptorActivitiesReceiver.startListening();
        mAdaptorTasksReceiver.startListening();
        recycler_receiverActivitySummaryNotificationDetails.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler_receiverActivitySummaryNotificationDetails.setAdapter(mAdaptorActivitiesReceiver);
        recycler_receiverTaskSummaryNotificationDetails.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler_receiverTaskSummaryNotificationDetails.setAdapter(mAdaptorTasksReceiver);
    }
    @Override
    public void onDocumentSelected(DocumentSnapshot doc) { }
    @Override
    public void onMessageSelected(DocumentSnapshot message) { }
    @Override
    public void onTaskSelected(DocumentSnapshot event) {

    }
    @Override
    public void onActivitySelected(DocumentSnapshot activity) {

    }
    @Override
    public void onUserSelected(DocumentSnapshot user) { }
    @Override
    public void onNotificationSelected(DocumentSnapshot notification, View v) { }
    private void getActivitiesAndTasksDetails() {
        if (!activityId.isEmpty()){
            recycler_senderActivitySummaryNotificationDetails.setVisibility(View.VISIBLE);
            activitiesRef.document(activityId).addSnapshotListener((value, error) -> {
                Activity act = value.toObject(Activity.class);
                ArrayList<String> assignee = act.getPotentialAssignee();
                ArrayList<String> superiorActivitiesOfRequestActivity = act.getPotentialActivitiesList();
                if (assignee.contains(firebaseUid)){
                    mainTextToBeInvitedActivityNotificationDetails.setVisibility(View.VISIBLE);
                    mainTextToBeInvitedActivityNotificationDetails.setText("You are invited to participated in the new activity as follows: ");
                }
                usersRef.document(firebaseUid).addSnapshotListener((value1, error1) -> {
                    receiverActivitiesDisplayed = value1.toObject(Users.class).getActivities();
                    for (String receiverActivity: receiverActivitiesDisplayed){
                        if (superiorActivitiesOfRequestActivity.contains(receiverActivity)){
                            mainTextToBeAddedNewNotificationDetails.setVisibility(View.VISIBLE);
                            mainTextToBeAddedNewNotificationDetails.setText("The user also wants to add this new activity under your activity.");
                        }
                    }
                });
            });
        }
        if (!taskId.isEmpty()){
            recycler_senderTaskSummaryNotificationDetails.setVisibility(View.VISIBLE);
            tasksRef.document(taskId).addSnapshotListener((value, error) -> {
                Task task = value.toObject(Task.class);
                ArrayList<String> assignee = task.getPotentialAssignee();
                ArrayList<String> superiorActivitiesOfRequestTask = task.getPotentialActivities();
                ArrayList<String> superiorTasksOfRequestTask = task.getPotentialSuperiorTasks();
                ArrayList<String> subTasksOfRequestTask = task.getPotentialSubTasks();
                if (assignee.contains(firebaseUid)){
                    mainTextToBeInvitedTaskNotificationDetails.setVisibility(View.VISIBLE);
                    mainTextToBeInvitedTaskNotificationDetails.setText("You are invited to participated in the new task.");
                }
                usersRef.document(firebaseUid).addSnapshotListener((value12, error12) -> {
                    receiverActivitiesDisplayed = value12.toObject(Users.class).getActivities();
                    receiverTasksDisplayed = value12.toObject(Users.class).getTasks();
                    for (String receiverActivity : receiverActivitiesDisplayed){
                        if (superiorActivitiesOfRequestTask.contains(receiverActivity)){
                            mainTextToBeAddedInActivitiesNotificationDetails.setVisibility(View.VISIBLE);
                            mainTextToBeAddedInActivitiesNotificationDetails.setText("The user wants to add a new task to your activity");
                        }
                    }
                    for (String receiverTask : receiverTasksDisplayed){
                        if (superiorTasksOfRequestTask.contains(receiverTask)){
                            mainTextToBeAddedSubTaskNotificationDetails.setVisibility(View.VISIBLE);
                            mainTextToBeAddedSubTaskNotificationDetails.setText("The user wants to add one of your current tasks as superior task.");
                        }
                    }
                    for (String receiverTask : receiverTasksDisplayed){
                        if (subTasksOfRequestTask.contains(receiverTask)){
                            mainTextToBeAddedSuperiorTaskNotificationDetails.setVisibility(View.VISIBLE);
                            mainTextToBeAddedSuperiorTaskNotificationDetails.setText("The user wants to add one of your current task as sub task.");
                        }
                    }
                });
            });
        }
    }
}