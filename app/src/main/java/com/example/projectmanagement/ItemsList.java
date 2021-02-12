package com.example.projectmanagement;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.compose.ui.Alignment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.example.projectmanagement.Adaptors.ActivityAdaptor;
import com.example.projectmanagement.Adaptors.NotificationsAdaptor;
import com.example.projectmanagement.Adaptors.TaskAdaptor;
import com.example.projectmanagement.Adaptors.UsersAdaptor;
import com.example.projectmanagement.Interface.OnListItemSelectedListener;
import com.example.projectmanagement.Model.Activity;
import com.example.projectmanagement.Model.NotificationsMessage;
import com.example.projectmanagement.Model.Task;
import com.example.projectmanagement.Model.Users;
import com.example.projectmanagement.Utils.UtilsMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ItemsList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemsList extends Fragment implements OnListItemSelectedListener, View.OnClickListener {
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String firebaseUid;
    private CardView filterBar, searchBar;
    private RecyclerView mRecyclerViewActivity, mRecyclerViewNotification, mRecyclerViewTask, mRecyclerViewUser;
    private LinearLayout taskListRecyclerViewTitleBar;
    private TaskAdaptor mAdaptorTasks;
    private ActivityAdaptor mAdaptorActivities;
    private NotificationsAdaptor mAdaptorNotifications;
    private UsersAdaptor mAdaptorUsers;
    private Query mQueryActivities, mQueryTasks, mQueryNotifications, mQueryUsers;
    private FloatingActionButton fabActivities, fabNotifications, fabTasks, fabUsers;
    private int pageIndex;
    private CollectionReference activitiesRef, notificationsRef, tasksRef, usersRef;
    private static final int LIMIT = 20;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ItemsList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ItemsList.
     */
    // TODO: Rename and change types and number of parameters
    public static ItemsList newInstance(String param1, String param2) {
        ItemsList fragment = new ItemsList();
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
            pageIndex = getArguments().getInt("index");
        }
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        firebaseUid = mFirebaseUser.getUid();
        iniFireStore();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_items_list, container, false);
        filterBar = root.findViewById(R.id.filterBar);
        searchBar = root.findViewById(R.id.searchBar);
        mRecyclerViewActivity = root.findViewById(R.id.recycler_activities);
        mRecyclerViewNotification = root.findViewById(R.id.recycler_notificationsMessage);
        taskListRecyclerViewTitleBar = root.findViewById(R.id.taskListRecyclerViewTitleBar);
        mRecyclerViewTask = root.findViewById(R.id.recycler_tasks);
        mRecyclerViewTask.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        mRecyclerViewUser = root.findViewById(R.id.recycler_user);
        fabActivities = root.findViewById(R.id.fabItemListActivities);
        fabNotifications = root.findViewById(R.id.fabItemListNotifications);
        fabTasks = root.findViewById(R.id.fabItemListTasks);
        fabUsers = root.findViewById(R.id.fabItemListUsers);
        fabActivities.setOnClickListener(this);
        fabNotifications.setOnClickListener(this);
        fabTasks.setOnClickListener(this);
        fabUsers.setOnClickListener(this);
        switch (pageIndex){
            case R.layout.item_activities:
                mRecyclerViewActivity.setVisibility(View.VISIBLE);
                mRecyclerViewNotification.setVisibility(View.GONE);
                taskListRecyclerViewTitleBar.setVisibility(View.GONE);
                mRecyclerViewTask.setVisibility(View.GONE);
                mRecyclerViewUser.setVisibility(View.GONE);
                fabActivities.setVisibility(View.VISIBLE);
                fabNotifications.setVisibility(View.GONE);
                fabTasks.setVisibility(View.GONE);
                fabUsers.setVisibility(View.GONE);
                mAdaptorActivities = new ActivityAdaptor(mQueryActivities, this);
                mAdaptorActivities.startListening();
                mRecyclerViewActivity.setLayoutManager(new LinearLayoutManager(getActivity()));
                mRecyclerViewActivity.setAdapter(mAdaptorActivities);

                break;
            case R.layout.item_notifications_message:
                mRecyclerViewActivity.setVisibility(View.GONE);
                mRecyclerViewNotification.setVisibility(View.VISIBLE);
                taskListRecyclerViewTitleBar.setVisibility(View.GONE);
                mRecyclerViewTask.setVisibility(View.GONE);
                mRecyclerViewUser.setVisibility(View.GONE);
                fabActivities.setVisibility(View.GONE);
                fabNotifications.setVisibility(View.VISIBLE);
                fabTasks.setVisibility(View.GONE);
                fabUsers.setVisibility(View.GONE);
                mAdaptorNotifications = new NotificationsAdaptor(mQueryNotifications, this);
                mRecyclerViewNotification.setLayoutManager(new LinearLayoutManager(getActivity()));
                mRecyclerViewNotification.setAdapter(mAdaptorNotifications);
                break;
            case R.layout.item_tasks:
                mRecyclerViewActivity.setVisibility(View.GONE);
                mRecyclerViewNotification.setVisibility(View.GONE);
                taskListRecyclerViewTitleBar.setVisibility(View.VISIBLE);
                mRecyclerViewTask.setVisibility(View.VISIBLE);
                mRecyclerViewUser.setVisibility(View.GONE);
                fabActivities.setVisibility(View.GONE);
                fabNotifications.setVisibility(View.GONE);
                fabTasks.setVisibility(View.VISIBLE);
                fabUsers.setVisibility(View.GONE);
                mAdaptorTasks = new TaskAdaptor(mQueryTasks, this);
                mRecyclerViewTask.setLayoutManager(new LinearLayoutManager(getActivity()));
                mRecyclerViewTask.setAdapter(mAdaptorTasks);
            break;
            case R.layout.item_users:
                mRecyclerViewActivity.setVisibility(View.GONE);
                mRecyclerViewNotification.setVisibility(View.GONE);
                taskListRecyclerViewTitleBar.setVisibility(View.GONE);
                mRecyclerViewTask.setVisibility(View.GONE);
                mRecyclerViewUser.setVisibility(View.VISIBLE);
                fabActivities.setVisibility(View.GONE);
                fabNotifications.setVisibility(View.GONE);
                fabTasks.setVisibility(View.GONE);
                fabUsers.setVisibility(View.VISIBLE);
                mAdaptorUsers = new UsersAdaptor(mQueryUsers, this);
                mRecyclerViewTask.setLayoutManager(new LinearLayoutManager(getActivity()));
                mRecyclerViewTask.setAdapter(mAdaptorUsers);
                break;
        }
        return root;
    }

    @Override
    public void onDocumentSelected(DocumentSnapshot doc) {

    }

    @Override
    public void onMessageSelected(DocumentSnapshot message) {

    }

    @Override
    public void onTaskSelected(DocumentSnapshot event) {
        Task task = event.toObject(Task.class);
        Bundle bundle = new Bundle();
        bundle = UtilsMethods.generateBundleToTaskDetailsPage(task);
        Navigation.findNavController(mRecyclerViewTask).navigate(R.id.taskDetailsAll, bundle);
    }

    @Override
    public void onActivitySelected(DocumentSnapshot activity) {
        Activity act = activity.toObject(Activity.class);
        Bundle bundle = new Bundle();
        bundle.putString("activityTitle", act.getActivityTitle());
        bundle.putString("activityScope", act.getScope());
        bundle.putString("activityDescription", act.getDescription());
        bundle.putInt("progressReading", act.getProgressReading());
        bundle.putString("activityId", act.getActivityId());
        //bundle.putLong("activityStartDate", act.getActivityStartDate());
        //bundle.putLong("activityEndDate", act.getActivityEndDate());
        ArrayList<String> assigneeList = act.getAssignee();
        ArrayList<String> potentialAssigneeList = act.getPotentialAssignee();
        ArrayList<String> activitiesList = act.getActivitiesList();
        ArrayList<String> potentialActivitiesList = act.getPotentialActivitiesList();
        ArrayList<String> subActivitiesList = act.getSubActivitiesList();
        ArrayList<String> potentialSubActivitiesList = act.getPotentialSubActivitiesList();
        ArrayList<String> TasksList = act.getTasksList();
        ArrayList<String> potentialTasksList = act.getPotentialTasksList();
        ArrayList<String> confirmationList = act.getConfirmationList();
        ArrayList<String> confirmationListRef = act.getConfirmationListRef();
        HashMap<String, ArrayList<String>> requestSubActivities = act.getRequestSubActivities();
        HashMap<String, ArrayList<String>> requestSuperiorActivities = act.getRequestSuperiorActivities();
        HashMap<String, ArrayList<String>> requestTasks = act.getRequestTasks();
        bundle.putStringArrayList("assignee", assigneeList);
        bundle.putStringArrayList("potentialAssignee", potentialAssigneeList);
        bundle.putStringArrayList("activitiesList", activitiesList);
        bundle.putStringArrayList("potentialActivitiesList", potentialActivitiesList);
        bundle.putStringArrayList("subActivitiesList", subActivitiesList);
        bundle.putStringArrayList("potentialSubActivitiesList", potentialSubActivitiesList);
        bundle.putStringArrayList("TasksList", TasksList);
        bundle.putStringArrayList("potentialTasksList", potentialTasksList);
        bundle.putStringArrayList("confirmationList", confirmationList);
        bundle.putStringArrayList("confirmationListRef", confirmationListRef);
        bundle.putSerializable("potentialAssigneeMap", act.getPotentialAssigneeMap());
        bundle.putSerializable("potentialSubActivitiesMap", act.getPotentialSubActivitiesMap());
        bundle.putSerializable("potentialSuperiorActivitiesMap", act.getPotentialSuperiorActivitiesMap());
        bundle.putSerializable("potentialTasksMap", act.getPotentialTasksMap());
        bundle.putSerializable("requestAssignee", act.getRequestAssignee());
        bundle.putSerializable("requestSubActivities", requestSubActivities);
        bundle.putSerializable("requestSuperiorActivities", requestSuperiorActivities);
        bundle.putSerializable("requestTasks", requestTasks);
        NavController navController = Navigation.findNavController(mRecyclerViewActivity);
        navController.navigate(R.id.activityDetails, bundle);
    }

    @Override
    public void onUserSelected(DocumentSnapshot user) {

    }

    @Override
    public void onNotificationSelected(DocumentSnapshot notification, View v) {
        NotificationsMessage note = notification.toObject(NotificationsMessage.class);
        String activityId = note.getActivity();
        String taskId = note.getTask();
        String sender = note.getSenderUid();
        String receiver = note.getReceiver();
        String notificationTitle = note.getNotificationTitle();
        String notificationId = note.getNotificationId();
        switch(v.getId()){
            case R.id.notificationsConfirmButton:
                //depending on receiver role, either add assignee, or just got acknowledgement
                if (notificationTitle.equals(getString(R.string.Invitation_join_activity))){
                    UtilsMethods.confirmActivity(getContext(), activitiesRef, usersRef,
                            activityId, receiver);
                    notificationsRef.document(notificationId).delete();
                } else if (notificationTitle.equals(getString(R.string.Invitation_join_task))){
                    tasksRef.document(taskId).get().addOnSuccessListener(snapshot -> {
                        Task task = snapshot.toObject(Task.class);
                        ArrayList<String> confirmationList = task.getConfirmationListTasks();
                        ArrayList<String> assignee = task.getAssignee();
                        ArrayList<String> potentialAssignee = task.getPotentialAssignee();
                        ArrayList<String> potentialActivities = task.getPotentialActivities();
                        ArrayList<String> potentialSubTasks = task.getPotentialSubTasks();
                        ArrayList<String> potentialSuperiorTasks = task.getPotentialSuperiorTasks();
                        if (potentialAssignee.contains(receiver)){
                            potentialAssignee.remove(receiver);
                            assignee.add(receiver);
                            tasksRef.document(taskId).update("assignee", assignee);
                            tasksRef.document(taskId).update("potentialAssignee", potentialAssignee);
                            usersRef.document(receiver).get().addOnSuccessListener(snapshot13 -> {
                                ArrayList<String> tasksUser = snapshot13.toObject(Users.class).getTasks();
                                tasksUser.add(taskId);
                                usersRef.document(receiver).update("tasks", tasksUser); // this is the current tasks the user is participating in
                            }).addOnFailureListener(e -> {
                                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                                Log.d("Get User Data", e.toString());
                            });
                        }
                        if (confirmationList.contains(receiver)){
                            confirmationList.remove(receiver);
                            tasksRef.document(taskId).update("confirmationListTasks", confirmationList);
                        }
                        for (String potentialAct : potentialActivities){
                            activitiesRef.document(potentialAct).get().addOnSuccessListener(snapshot1 -> {
                                int potentialActAssigneeConfirmCount = 0;
                                ArrayList<String> potentialActAssignee = snapshot1.toObject(Activity.class).getAssignee();
                                for (String member : potentialActAssignee){
                                    if (!confirmationList.contains(member)){
                                        potentialActAssigneeConfirmCount = potentialActAssigneeConfirmCount + 1;
                                    }
                                    if (potentialActAssigneeConfirmCount == potentialActAssignee.size()){
                                        //the potentialActivity is approved
                                        ArrayList<String> potentialActivitiesList1 = task.getPotentialActivities(); //superiorActivities
                                        ArrayList<String> activitiesList1 = task.getActivities(); //superiorActivities
                                        potentialActivitiesList1.remove(potentialAct);
                                        activitiesList1.add(potentialAct);
                                        tasksRef.document(taskId).update("activities", activitiesList1);
                                        tasksRef.document(taskId).update("potentialActivities", potentialActivitiesList1);
                                        activitiesRef.document(potentialAct).get().addOnSuccessListener(snapshot2 -> {
                                            ArrayList<String> activityTaskList = snapshot2.toObject(Activity.class).getTasksList();
                                            ArrayList<String> activityPotentialTaskList = snapshot2.toObject(Activity.class).getPotentialTasksList();
                                            String taskId1 = task.getTaskId();
                                            activityTaskList.add(taskId1);
                                            activityPotentialTaskList.remove(taskId1);
                                            activitiesRef.document(potentialAct).update("tasksList", activityTaskList);
                                            activitiesRef.document(potentialAct).update("potentialTasksList", activityPotentialTaskList);
                                        });
                                    }
                                }
                            });
                        }
                        //
                        for (String subtask : potentialSubTasks){
                            tasksRef.document(subtask).get().addOnSuccessListener(snapshot12 -> {
                                int confirmCount = 0;
                                ArrayList<String> potentialsubtaskAssignee = snapshot12.toObject(Task.class).getAssignee();
                                for (String member : potentialsubtaskAssignee){
                                    if (!confirmationList.contains(member)){
                                        confirmCount = confirmCount + 1;
                                    }
                                    if (confirmCount == potentialsubtaskAssignee.size()){
                                        //the potentialActivity is approved
                                        ArrayList<String> potentialsubtasksList1 = task.getPotentialSubTasks(); //superiorActivities
                                        ArrayList<String> subtasksList1 = task.getSubTasks(); //superiorActivities
                                        potentialsubtasksList1.remove(subtask);
                                        subtasksList1.add(subtask);
                                        tasksRef.document(taskId).update("subTasks", subtasksList1);
                                        tasksRef.document(taskId).update("potentialSubTasks", potentialsubtasksList1);
                                        tasksRef.document(subtask).get().addOnSuccessListener(snapshot15 -> {
                                            ArrayList<String> superiorTaskOfSubTask = snapshot15.toObject(Task.class).getSuperiorTasks();
                                            ArrayList<String> potentialSuperiorTaskOfSubTask = snapshot15.toObject(Task.class).getPotentialSuperiorTasks();
                                            superiorTaskOfSubTask.add(taskId);
                                            potentialSuperiorTaskOfSubTask.remove(taskId);
                                            tasksRef.document(subtask).update("superiorTasks", superiorTaskOfSubTask);
                                            tasksRef.document(subtask).update("potentialSuperiorTasks", potentialSuperiorTaskOfSubTask);
                                        });
                                    }
                                }
                            });
                        }
                        for (String superiortask : potentialSuperiorTasks){
                            tasksRef.document(superiortask).get().addOnSuccessListener(snapshot14 -> {
                                int confirmCount = 0;
                                ArrayList<String> potentialsuperiortaskAssignee = snapshot14.toObject(Task.class).getAssignee();
                                for (String member : potentialsuperiortaskAssignee){
                                    if (!confirmationList.contains(member)){
                                        confirmCount = confirmCount + 1;
                                    }
                                    if (confirmCount == potentialsuperiortaskAssignee.size()){
                                        //the potentialActivity is approved
                                        ArrayList<String> potentialsuperiortasksList1 = task.getPotentialSuperiorTasks(); //superiorActivities
                                        ArrayList<String> superiortasksList1 = task.getSuperiorTasks(); //superiorActivities
                                        potentialsuperiortasksList1.remove(superiortask);
                                        superiortasksList1.add(superiortask);
                                        tasksRef.document(taskId).update("superiorTasks", superiortasksList1);
                                        tasksRef.document(taskId).update("potentialSuperiorTasks", potentialsuperiortasksList1);
                                        tasksRef.document(superiortask).get().addOnSuccessListener(snapshot15 -> {
                                            ArrayList<String> subTasksOfSuperiorTask = snapshot15.toObject(Task.class).getSubTasks();
                                            ArrayList<String> potentialSubTasksOfSuperiorTask = snapshot15.toObject(Task.class).getPotentialSubTasks();
                                            subTasksOfSuperiorTask.add(taskId);
                                            potentialSubTasksOfSuperiorTask.remove(taskId);
                                            tasksRef.document(superiortask).update("subTasks", subTasksOfSuperiorTask);
                                            tasksRef.document(superiortask).update("potentialSubTasks", potentialSubTasksOfSuperiorTask);
                                        });
                                    }
                                }
                            });
                        }
                        //
                        if (confirmationList.size() == 0){
                            if (potentialAssignee.size() == 0){
                                tasksRef.document(taskId).update("status", "Ongoing..");
                            }
                        }
                    });
                    notificationsRef.document(notificationId).delete();
                }
                break;
            case R.id.notificationsCancelButton:
                notificationsRef.document(notificationId).delete();
                //Reply the sender you reject the invitation
                //reply with reason
                break;
            case R.id.notificationsParent:
                Bundle bundle = new Bundle();
                bundle.putString("notificationId", notificationId);
                bundle.putString("activityId", activityId);
                bundle.putString("taskId", taskId);
                bundle.putString("notificationTitle", notificationTitle);
                Navigation.findNavController(mRecyclerViewNotification).navigate(R.id.notificationDetails, bundle);
        }
        mAdaptorNotifications.stopListening();
        mAdaptorNotifications.startListening();
    }
    private void iniFireStore(){
        mFirestore = FirebaseFirestore.getInstance();
        activitiesRef = mFirestore.collection("Activities");
        notificationsRef = mFirestore.collection("Notifications");
        tasksRef = mFirestore.collection("Tasks");
        usersRef = mFirestore.collection("Users");
        mQueryActivities = activitiesRef.orderBy("timeCreated", Query.Direction.DESCENDING)
                .limit(LIMIT);
        mQueryNotifications = notificationsRef.orderBy("timeCreated", Query.Direction.DESCENDING)
                .whereEqualTo("receiver", firebaseUid)
                .limit(LIMIT);
        mQueryTasks = tasksRef.orderBy("timeCreated", Query.Direction.DESCENDING)
                .limit(LIMIT);
        mQueryUsers = usersRef.orderBy("timeCreated", Query.Direction.DESCENDING)
                .limit(LIMIT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fabItemListActivities:
                Navigation.findNavController(fabActivities).navigate(R.id.editActivity);
                break;
            case R.id.fabItemListNotifications:
                //To-do
                break;
            case R.id.fabItemListTasks:
                Navigation.findNavController(fabActivities).navigate(R.id.editTask);
                break;
        }
    }
    @Override
    public void onStop() {
        switch(pageIndex){
            case R.layout.item_activities:
                if (mAdaptorActivities != null){
                    mAdaptorActivities.stopListening();
                }
                break;
            case R.layout.item_notifications_message:
                if (mAdaptorNotifications != null){
                    mAdaptorNotifications.stopListening();
                }
                break;
            case R.layout.item_tasks:
                if (mAdaptorTasks != null){
                    mAdaptorTasks.stopListening();
                }
                break;
        }
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        switch(pageIndex){
            case R.layout.item_activities:
                if (mAdaptorActivities != null){
                    mAdaptorActivities.startListening();
                }
                break;
            case R.layout.item_notifications_message:
                if (mAdaptorNotifications != null){
                    mAdaptorNotifications.startListening();
                }
                break;
            case R.layout.item_tasks:
                if (mAdaptorTasks != null){
                    mAdaptorTasks.startListening();
                }
                break;
        }

    }
}