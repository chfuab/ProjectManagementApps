package com.example.projectmanagement;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.ArraySet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.projectmanagement.Adaptors.ActivityAdaptor;
import com.example.projectmanagement.Adaptors.CommentAdaptor;
import com.example.projectmanagement.Adaptors.PagerAdaptor;
import com.example.projectmanagement.Adaptors.StatusUpdateAdaptor;
import com.example.projectmanagement.Adaptors.TaskAdaptor;
import com.example.projectmanagement.Adaptors.UsersAdaptor;
import com.example.projectmanagement.Interface.DialogDataListener;
import com.example.projectmanagement.Interface.OnListItemSelectedListener;
import com.example.projectmanagement.Model.Activity;
import com.example.projectmanagement.Model.CommentOnStatusUpdate;
import com.example.projectmanagement.Model.StatusUpdate;
import com.example.projectmanagement.Model.Task;
import com.example.projectmanagement.Model.Users;
import com.example.projectmanagement.Utils.UtilsMethods;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TaskDetails#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskDetails extends Fragment implements RadioGroup.OnCheckedChangeListener, View.OnClickListener, EventListener<QuerySnapshot>, OnListItemSelectedListener, DialogDataListener {
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private CollectionReference activitiesRef, notificationsRef, tasksRef, usersRef, updatesRef;
    private DocumentReference userDocRef;
    private ListenerRegistration mRegistration;
    private Query querySubTasks, queryTasks, queryTasksThisTaskOnly,
            queryTasksWithSameActivities, queryActivities, queryUserName, queryUpdates, querySuperiorTaskIsThis, querySubTaskIsThis;
    private ArrayList<TaskAdaptor> mAdaptorListTaskObj = new ArrayList<>();
    private TaskAdaptor mAdaptorTasks, mAdaptorTasksCurrentTaskOnly, mAdaptorSuperiorTaskIsThis, mAdaptorSubTaskIsThis;
    private ActivityAdaptor mAdaptorActivity;
    private UsersAdaptor mUserAdaptor;
    private StatusUpdateAdaptor mAdaptorStatus;
    private ArrayList<String> mAdaptorListTaskId = new ArrayList<>();
    private int pageIndexForTabs;
    private Bundle initDataBundle = new Bundle();
    private View root;
    private TextView taskDetailsTitle, taskDetailsTitleBody, taskDetailsScope,
            taskDetailsScopeBody, taskDetailsDescription, taskDetailsDescriptionBody,
            taskDetailsAssignee, progressBarTitleTaskDetails, progressBarReadingTaskDetails,
            taskDetailsSuperiorTask, taskDetailsSubTask, taskDetailsConfirmationList, taskDetailsTimeLineText;
    private ChipGroup assigneeChipsGroupTaskDetails, confirmationListChipsGroupTaskDetails;
    private RecyclerView recycler_superiorTasks, recycler_subTasks, taskUpdatesListRecyclerView;
    private ImageButton taskSuperiortaskButtonInSuperiorTask, taskSubtaskButtonInSubTask;
    private ProgressBar progressTask;
    private RadioGroup completionStatus;
    private RadioButton radioButtonDoneTaskDetails, radioButtonNotDoneTaskDetails, radioButtonTaskCancelledDetails;
    private LinearLayout taskAssigneeLayout, taskDetailsConfirmationListLayout;
    private Button confirmTaskDetails;
    private FloatingActionButton fabTaskDetailsConfirm, fabTaskDetailsEdit, fabCreateStatusUpdate, fabTaskDetailsNowIn;
    private String firebaseUid, taskId, taskTitle, taskScope, taskDescription, taskStartDate, taskDueDate, completion, userDisplayName;
    private ArrayList<Integer> progressReadingSubtasks = new ArrayList<>();
    private int progressReading, oldProgressReadingOfThisTask;
    private int reviewReading = 0;
    private int completionSelected = 0;
    private ArrayList<String> activities = new ArrayList<>();
    private ArrayList<String> assignee = new ArrayList<>();
    private HashMap<String, ArrayList<String>> potentialAssigneeMap = new HashMap<>();
    private ArrayList<String> confirmationList = new ArrayList<>();
    private ArrayList<String> subtasks = new ArrayList<>();
    private ArrayList<String> superiortasks = new ArrayList<>();
    private HashMap<String, ArrayList<Task>> tasksObjOfActs = new HashMap<>();
    private HashMap<String, ArrayList<String>> tasksIdOfActs = new HashMap<>();
    private HashMap<String, Bundle> bundlesOfActs = new HashMap<>();
    private HashMap<String, ArrayList<String>> superiorTasksOfAllTaskThisActivity = new HashMap<>();
    private HashMap<String, Float> progressReadingAllTasksThisActivity = new HashMap<>();
    private HashMap<String, Integer> numOFSubTasksOfEachSuperiorTask = new HashMap<>();
    private HashMap<String, String> assigneeExistence = new HashMap<>();
    private HashMap<String, ArrayList<Long>> assigneeInTime = new HashMap<>();
    private HashMap<String, ArrayList<Long>> assigneeOutTime = new HashMap<>();
    private Dialog dialog;
    private static final int LIMIT = 20;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public TaskDetails() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TaskDetails.
     */
    // TODO: Rename and change types and number of parameters
    public static TaskDetails newInstance(String param1, String param2) {
        TaskDetails fragment = new TaskDetails();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        firebaseUid = mFirebaseUser.getUid();
        if (getArguments() != null) {
            pageIndexForTabs = getArguments().getInt("pageIndexForTabs");
            initDataBundle = getArguments().getBundle("pageInitData");
            taskId = initDataBundle.getString("taskId");
            taskTitle = initDataBundle.getString("taskTitle");
            taskScope = initDataBundle.getString("taskScope");
            taskDescription = initDataBundle.getString("taskDescription");
            progressReading = initDataBundle.getInt("ProgressReading");
            oldProgressReadingOfThisTask = progressReading;
            completionSelected = initDataBundle.getInt("completionSelected");
            Long startDate = initDataBundle.getLong("StartDate");
            Long dueDate = initDataBundle.getLong("DueDate");
            Calendar calendar = Calendar.getInstance();
            Date dateStart = new Date(startDate);
            calendar.setTime(dateStart);
            taskStartDate = String.format("%04d/%02d/%02d %02d:%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            Calendar calendar2 = Calendar.getInstance();
            Date dateDue = new Date(dueDate);
            calendar2.setTime(dateDue);
            taskDueDate = String.format("%04d/%02d/%02d %02d:%02d", calendar2.get(Calendar.YEAR), calendar2.get(Calendar.MONTH) + 1,
                    calendar2.get(Calendar.DAY_OF_MONTH), calendar2.get(Calendar.HOUR_OF_DAY), calendar2.get(Calendar.MINUTE));
            assignee = initDataBundle.getStringArrayList("AssigneeTask");
            confirmationList = initDataBundle.getStringArrayList("ConfirmationList");
            subtasks = initDataBundle.getStringArrayList("subtasks");
            superiortasks = initDataBundle.getStringArrayList("superiorTasks");
            activities = initDataBundle.getStringArrayList("activities");
            potentialAssigneeMap = (HashMap<String, ArrayList<String>>) initDataBundle.getSerializable("potentialAssigneeMap");
            assigneeExistence = (HashMap<String, String>) initDataBundle.getSerializable("assigneeExistence");
            assigneeInTime = (HashMap<String, ArrayList<Long>>) initDataBundle.getSerializable("assigneeInTime");
            assigneeOutTime = (HashMap<String, ArrayList<Long>>) initDataBundle.getSerializable("assigneeOutTime");
            iniFireStore();
            userDocRef = usersRef.document(firebaseUid);
            userDocRef.addSnapshotListener((value, error) -> {
                Users users = value.toObject(Users.class);
                userDisplayName = users.getDisplayName();
            });
            if (subtasks.size() > 0){
                querySubTasks = tasksRef
                        .whereIn("taskId", subtasks)
                        .limit(LIMIT);
            }
            for (String actId : activities){
                queryTasksWithSameActivities = tasksRef
                        .whereArrayContains("activities", actId)
                        .limit(LIMIT);
                mAdaptorListTaskObj.add(new TaskAdaptor(queryTasksWithSameActivities, this));
            }
            queryTasks = tasksRef.orderBy("timeCreated", Query.Direction.DESCENDING)
                    .limit(LIMIT);
            queryTasksThisTaskOnly = tasksRef
                    .whereEqualTo("taskId", taskId);
            querySuperiorTaskIsThis = tasksRef.whereArrayContains("superiorTasks", taskId);
            querySubTaskIsThis = tasksRef.whereArrayContains("subTasks", taskId);
            Log.d("FuckYourMotherTaskId", taskId);
            queryActivities = activitiesRef.orderBy("timeCreated", Query.Direction.DESCENDING)
                    .limit(LIMIT);
            queryUserName = usersRef.whereEqualTo("uid", firebaseUid);
            queryUpdates = updatesRef.whereEqualTo("ofWhichTaskId", taskId);
            mAdaptorActivity = new ActivityAdaptor(queryActivities, this);
            mAdaptorTasks = new TaskAdaptor(queryTasks, this);
            mAdaptorSuperiorTaskIsThis = new TaskAdaptor(querySuperiorTaskIsThis, this);
            mAdaptorSubTaskIsThis = new TaskAdaptor(querySubTaskIsThis, this);
            mAdaptorTasksCurrentTaskOnly = new TaskAdaptor(queryTasksThisTaskOnly, this);
            mUserAdaptor = new UsersAdaptor(queryUserName, this);
            mAdaptorStatus = new StatusUpdateAdaptor(queryUpdates, this, getContext());
            switch (pageIndexForTabs){
                case R.layout.fragment_task_details:

                    break;
                case R.layout.fragment_task_details_status_update:

                    break;
                case R.layout.fragment_task_details_review_history:

                    break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        switch (pageIndexForTabs){
            case R.layout.fragment_task_details:
                root = inflater.inflate(R.layout.fragment_task_details, container, false);
                taskDetailsTitle = root.findViewById(R.id.taskDetailsTitle);
                taskDetailsTitleBody = root.findViewById(R.id.taskDetailsTitleBody);
                taskDetailsTitleBody.setText(taskTitle);
                taskDetailsScope = root.findViewById(R.id.taskDetailsScope);
                taskDetailsScopeBody = root.findViewById(R.id.taskDetailsScopeBody);
                taskDetailsScopeBody.setText(taskScope);
                taskDetailsDescription = root.findViewById(R.id.taskDetailsDescription);
                taskDetailsDescriptionBody = root.findViewById(R.id.taskDetailsDescriptionBody);
                taskDetailsDescriptionBody.setText(taskDescription);
                taskDetailsTimeLineText = root.findViewById(R.id.taskDetailsTimeLineText);
                taskDetailsTimeLineText.setText("Start time: " + taskStartDate + "\n" + "End time: " + taskDueDate);
                taskDetailsTimeLineText.setTextSize(20);
                taskDetailsAssignee = root.findViewById(R.id.taskDetailsAssignee);
                progressBarTitleTaskDetails = root.findViewById(R.id.progressBarTitleTaskDetails);
                progressBarReadingTaskDetails = root.findViewById(R.id.progressBarReadingTaskDetails);
                progressBarReadingTaskDetails.setText(progressReading + "%");
                taskDetailsSuperiorTask = root.findViewById(R.id.taskDetailsSuperiorTask);
                taskDetailsSubTask = root.findViewById(R.id.taskDetailsSubTask);
                taskAssigneeLayout = root.findViewById(R.id.taskAssigneeLayout);
                taskDetailsConfirmationListLayout = root.findViewById(R.id.taskDetailsConfirmationListLayout);
                assigneeChipsGroupTaskDetails = root.findViewById(R.id.assigneeChipsGroupTaskDetails);
                if (!assignee.isEmpty()){
                    UtilsMethods.populateChipsGroup(getContext(), assigneeChipsGroupTaskDetails, assignee);
                } else {
                    taskAssigneeLayout.setVisibility(View.GONE);
                }
                recycler_superiorTasks = root.findViewById(R.id.recycler_superiorTasksTaskDetails);
                recycler_subTasks = root.findViewById(R.id.recycler_subTasksTaskDetails);
                recycler_superiorTasks.setAdapter(mAdaptorSubTaskIsThis);
                recycler_subTasks.setAdapter(mAdaptorSuperiorTaskIsThis);
                recycler_superiorTasks.setLayoutManager(new LinearLayoutManager(getContext()));
                recycler_subTasks.setLayoutManager(new LinearLayoutManager(getContext()));
                taskSuperiortaskButtonInSuperiorTask = root.findViewById(R.id.taskSuperiortaskButtonInSuperiorTask);
                taskSubtaskButtonInSubTask = root.findViewById(R.id.taskSubtaskButtonInSubTask);
                progressTask = root.findViewById(R.id.progressTask);
                progressTask.setProgress(progressReading);
                completionStatus = root.findViewById(R.id.completionRadioGroupTaskDetails);
                completionStatus.setOnCheckedChangeListener(this);
                radioButtonDoneTaskDetails = root.findViewById(R.id.radioButtonDoneTaskDetails);
                radioButtonNotDoneTaskDetails = root.findViewById(R.id.radioButtonNotDoneTaskDetails);
                radioButtonTaskCancelledDetails = root.findViewById(R.id.radioButtonTaskCancelledDetails);
                if (completionSelected == 0){
                    radioButtonDoneTaskDetails.setChecked(true);
                } else if (completionSelected == 1){
                    radioButtonNotDoneTaskDetails.setChecked(true);
                } else if (completionSelected == 2){
                    radioButtonTaskCancelledDetails.setChecked(true);
                }
                taskDetailsConfirmationList = root.findViewById(R.id.taskDetailsConfirmationList);
                confirmationListChipsGroupTaskDetails = root.findViewById(R.id.confirmationListChipsGroupTaskDetails);
                confirmTaskDetails = root.findViewById(R.id.confirmTaskDetails);
                confirmTaskDetails.setOnClickListener(this);
                if (!confirmationList.isEmpty()){
                    UtilsMethods.populateChipsGroup(getContext(), confirmationListChipsGroupTaskDetails, confirmationList);
                } else{
                    taskDetailsConfirmationListLayout.setVisibility(View.GONE);
                }
                fabTaskDetailsConfirm = root.findViewById(R.id.fabTaskDetailsConfirm);
                if (!assignee.contains(firebaseUid) && !potentialAssigneeMap.keySet().contains(firebaseUid)){
                    fabTaskDetailsConfirm.setVisibility(View.GONE);
                }
                fabTaskDetailsEdit = root.findViewById(R.id.fabTaskDetailsEdit);
                fabTaskDetailsNowIn = root.findViewById(R.id.fabTaskDetailsNowIn);
                if (!assigneeExistence.keySet().contains(firebaseUid)){
                    fabTaskDetailsNowIn.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_48dp));
                } else {
                    fabTaskDetailsNowIn.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_48dp));
                }
                fabTaskDetailsConfirm.setOnClickListener(this);
                fabTaskDetailsEdit.setOnClickListener(this);
                fabTaskDetailsNowIn.setOnClickListener(this);
                break;
            case R.layout.fragment_task_details_status_update:
                root = inflater.inflate(R.layout.fragment_task_details_status_update, container, false);
                taskUpdatesListRecyclerView = root.findViewById(R.id.taskUpdatesListRecyclerView);
                taskUpdatesListRecyclerView.setAdapter(mAdaptorStatus);
                taskUpdatesListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                fabCreateStatusUpdate = root.findViewById(R.id.fabCreateStatusUpdate);
                fabCreateStatusUpdate.setOnClickListener(this);
                break;
            case R.layout.fragment_task_details_review_history:
                root = inflater.inflate(R.layout.fragment_task_details_review_history, container, false);

                break;
        }
        return root;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.radioButtonDoneTaskDetails:
                reviewReading = 100;
                completionSelected = 0;
                break;
            case R.id.radioButtonNotDoneTaskDetails:
                reviewReading = 0;
                completionSelected = 1;
                break;
            case R.id.radioButtonTaskCancelledDetails:
                completionSelected = 2;
                break;
        }
    }
    @Override
    public void onStop() {
        if (mRegistration != null) {
            mRegistration.remove();
            mRegistration = null;
        }
        for (TaskAdaptor taskAdaptor : mAdaptorListTaskObj){
            taskAdaptor.stopListening();
        }
        if (mAdaptorActivity != null){
            mAdaptorActivity.stopListening();
        }
        if (mAdaptorTasks != null){
            mAdaptorTasks.stopListening();
        }
        if (mAdaptorSuperiorTaskIsThis != null){
            mAdaptorSuperiorTaskIsThis.stopListening();
        }
        if (mAdaptorSubTaskIsThis != null){
            mAdaptorSubTaskIsThis.stopListening();
        }
        if (mAdaptorTasksCurrentTaskOnly != null){
            mAdaptorTasksCurrentTaskOnly.stopListening();
        }
        if (mUserAdaptor != null){
            mUserAdaptor.stopListening();
        }
        if (mAdaptorStatus != null){
            mAdaptorStatus.stopListening();
        }
        super.onStop();
    }
    @Override
    public void onStart() {
        super.onStart();
        switch(pageIndexForTabs){
            case R.layout.fragment_task_details:
                if (subtasks.size() > 0){
                    mRegistration = querySubTasks.addSnapshotListener(this);
                }
                for (TaskAdaptor taskAdaptor : mAdaptorListTaskObj){
                    taskAdaptor.startListening();
                }
                mAdaptorActivity.startListening();
                mAdaptorTasks.startListening();
                mAdaptorSuperiorTaskIsThis.startListening();
                mAdaptorSubTaskIsThis.startListening();
                mAdaptorTasksCurrentTaskOnly.startListening();
                mUserAdaptor.startListening();
                break;
            case R.layout.fragment_task_details_status_update:
                mUserAdaptor.startListening();
                mAdaptorStatus.startListening();
                break;
            case R.layout.fragment_task_details_review_history:
                break;
        }
    }

    private void iniFireStore(){
        mFirestore = FirebaseFirestore.getInstance();
        activitiesRef = mFirestore.collection("Activities");
        notificationsRef = mFirestore.collection("Notifications");
        tasksRef = mFirestore.collection("Tasks");
        usersRef = mFirestore.collection("Users");
        updatesRef = tasksRef.document(taskId).collection("Updates");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.confirmTaskDetails:
                //first update the current task progressReading:
                int progressReadingSum = 0;
                for (int reading : progressReadingSubtasks){
                    progressReadingSum = progressReadingSum + reading;
                }
                progressReading = (progressReadingSum + reviewReading)/ (progressReadingSubtasks.size() + 1);// the "1" here reserves for review (i.e. status of completion) in the task.
                tasksRef.document(taskId).update("progressReading", progressReading);
                if (reviewReading == 100 && progressReading == 100){ completion = "reviewed & all sub tasks completed"; }
                else if (reviewReading == 100 && progressReading < 100){ completion = "reviewed completed, sub tasks not yet completed"; }
                else if (reviewReading < 100 && progressReading == 100){ completion = "All sub tasks completed, not yet reviewed"; }
                else if (reviewReading < 100 && progressReading < 100){ completion = "Not yet completed, not yet reviewed"; }
                tasksRef.document(taskId).update("completion", completion);
                //update the superiorTasks progressReading if it exists
                for (int i=0 ; i<activities.size(); i++){
                    String actId = activities.get(i);
                    ArrayList<Task> taskArrayInActId = new ArrayList<>();
                    ArrayList<String> taskIdArrayInActId = new ArrayList<>();
                    for (DocumentSnapshot doc : mAdaptorListTaskObj.get(i).retrieveSnapshot()){
                        taskArrayInActId.add(doc.toObject(Task.class));
                        taskIdArrayInActId.add(doc.toObject(Task.class).getTaskId());
                    }
                    tasksObjOfActs.put(actId, taskArrayInActId);
                    tasksIdOfActs.put(actId, taskIdArrayInActId);
                }
                for (String actId : activities) {
                    bundlesOfActs.put(actId, UtilsMethods.generateTaskGraph(tasksObjOfActs.get(actId), tasksIdOfActs.get(actId)));
                    superiorTasksOfAllTaskThisActivity = (HashMap<String, ArrayList<String>>) UtilsMethods.
                            generateTaskGraph(tasksObjOfActs.get(actId), tasksIdOfActs.get(actId)).
                            getSerializable("superiorTasksOfThisAllMap");
                    progressReadingAllTasksThisActivity = (HashMap<String, Float>) UtilsMethods.
                            generateTaskGraph(tasksObjOfActs.get(actId), tasksIdOfActs.get(actId)).
                            getSerializable("subTaskProgressReadingsAllMap");
                    ArrayList<Integer> subTasksLevelsArray;
                    subTasksLevelsArray = UtilsMethods.generateTaskGraph(tasksObjOfActs.get(actId), tasksIdOfActs.get(actId))
                            .getIntegerArrayList("subTasksLevelsArray");
                    ArrayList<String> subTaskList;
                    subTaskList = UtilsMethods.generateTaskGraph(tasksObjOfActs.get(actId), tasksIdOfActs.get(actId))
                            .getStringArrayList("subTaskIds");
                    float subTaskProgressReadingsAll[] = new float[tasksIdOfActs.get(actId).size()];
                    subTaskProgressReadingsAll = UtilsMethods.generateTaskGraph(tasksObjOfActs.get(actId), tasksIdOfActs.get(actId))
                            .getFloatArray("subTaskProgressReadingsAll");
                    ArrayList<String> superiorTasksOfThisTaskThisActivity = superiorTasksOfAllTaskThisActivity.get(taskId);
                    //update teh superiorTasks in each activity the current task belongs to:
                    for (String superiorTask : superiorTasksOfThisTaskThisActivity) {
                        int numOfSubTasks = 0;
                        for (ArrayList<String> superiorTasks : superiorTasksOfAllTaskThisActivity.values()) {
                            if (superiorTasks.size() > 0) {
                                if (superiorTasks.get(0).equals(superiorTask)) {
                                    numOfSubTasks = numOfSubTasks + 1;
                                }
                            }
                        }
                        numOFSubTasksOfEachSuperiorTask.put(superiorTask, numOfSubTasks);
                    }
                    //update the superiorTasks progressReading here
                    HashMap<String, Float> updatedProgressReading = new HashMap<>();
                    float lastTaskNewProgressReading = progressReading;
                    float lastTaskOldProgressReading = oldProgressReadingOfThisTask;
                    updatedProgressReading.put(taskId, (float) progressReading);
                    for (String superiorTask : superiorTasksOfThisTaskThisActivity) {
                        float superiorTaskNewProgressReading;
                        float superiorTaskOldProgressReading = progressReadingAllTasksThisActivity.get(superiorTask);
                        float superiorTaskSubTasksSize = numOFSubTasksOfEachSuperiorTask.get(superiorTask);
                        superiorTaskNewProgressReading = nextTaskNewProgressReading(superiorTaskSubTasksSize, lastTaskNewProgressReading,
                                lastTaskOldProgressReading, superiorTaskOldProgressReading);
                        lastTaskNewProgressReading = superiorTaskNewProgressReading;
                        lastTaskOldProgressReading = progressReadingAllTasksThisActivity.get(superiorTask);
                        tasksRef.document(superiorTask).update("progressReading", superiorTaskNewProgressReading);
                        updatedProgressReading.put(superiorTask, superiorTaskNewProgressReading);
                    }
                    //update the activity progressReading here
                    ArrayList<Float> subTaskProgressReadingsSelected = new ArrayList<>();
                    int count = 0;
                    int progressReadingSize = 0;
                    for (String subTaskId : subTaskList){
                        if (subTasksLevelsArray.get(count) == 0){
                            progressReadingSize = progressReadingSize + 1;
                            for (String updateTask : updatedProgressReading.keySet()){
                                if (subTaskId.equals(updateTask)){
                                    subTaskProgressReadingsAll[count] = updatedProgressReading.get(updateTask);
                                }
                            }
                            subTaskProgressReadingsSelected.add(subTaskProgressReadingsAll[count]);
                        }
                        count = count + 1;
                    }
                    float readingSum = 0;
                    float progressOfThisActivity;
                    for (float reading : subTaskProgressReadingsSelected){readingSum = readingSum + reading;}
                    if (progressReadingSize > 0){
                        progressOfThisActivity = readingSum / progressReadingSize;
                        activitiesRef.document(actId).update("progressReading", progressOfThisActivity);
                    }
                }
                tasksRef.document(taskId).update("completionSelected", completionSelected);
                break;
            case R.id.fabTaskDetailsEdit:
                Navigation.findNavController(getActivity(), R.id.fabTaskDetailsEdit).navigate(R.id.editTask, initDataBundle);
                break;
            case R.id.fabTaskDetailsConfirm:
                initDataBundle.putString("firebaseUid", firebaseUid);
                dialog = UtilsMethods.createDialog(getContext(), 0, null, R.layout.fragment_confirmation_dialog, null, this, initDataBundle);
                dialog.show();
                break;
            case R.id.fabCreateStatusUpdate:
                ArrayList<String> userName = new ArrayList<>();
                for (DocumentSnapshot snapshot : mUserAdaptor.retrieveSnapshot()){
                    Users user = snapshot.toObject(Users.class);
                    String username = user.getDisplayName();
                    userName.add(username);
                }
                Log.d("mUserAdaptor", Integer.toString(mUserAdaptor.retrieveSnapshot().size()));
                Log.d("userName", userName.toString());
                Bundle bundleTemp = new Bundle();
                bundleTemp.putString("taskId", taskId);
                bundleTemp.putStringArrayList("UsersName", userName);
                bundleTemp.putString("taskTitle", taskTitle);
                bundleTemp.putString("taskScope", taskScope);
                bundleTemp.putString("taskDescription", taskDescription);
                Navigation.findNavController(getActivity(), R.id.fabCreateStatusUpdate).navigate(R.id.taskDetailsStatusUpdate, bundleTemp);
                break;
            case R.id.fabTaskDetailsNowIn:
                if (!assigneeExistence.keySet().contains(firebaseUid)){
                    assigneeExistence.put(firebaseUid, userDisplayName);
                    tasksRef.document(taskId).update("assigneeExistence", assigneeExistence);
                    ArrayList<Long> timeArray;
                    Calendar calendar = Calendar.getInstance();
                    if (assigneeInTime.get(firebaseUid) != null){
                        timeArray = assigneeInTime.get(firebaseUid);
                        timeArray.add(calendar.getTimeInMillis());
                        assigneeInTime.put(firebaseUid, timeArray);
                        Log.d("assigneeInTime", assigneeInTime.toString());
                    } else {
                        timeArray = new ArrayList<>();
                        timeArray.add(calendar.getTimeInMillis());
                        assigneeInTime.put(firebaseUid, timeArray);
                    }
                    tasksRef.document(taskId).update("assigneeInTime", assigneeInTime);
                    fabTaskDetailsNowIn.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_48dp));
                } else {
                    assigneeExistence.remove(firebaseUid, userDisplayName);
                    tasksRef.document(taskId).update("assigneeExistence", assigneeExistence);
                    ArrayList<Long> timeArray;
                    Calendar calendar = Calendar.getInstance();
                    if (assigneeOutTime.get(firebaseUid) != null){
                        timeArray = assigneeOutTime.get(firebaseUid);
                        timeArray.add(calendar.getTimeInMillis());
                        assigneeOutTime.put(firebaseUid, timeArray);
                    } else {
                        timeArray = new ArrayList<>();
                        timeArray.add(calendar.getTimeInMillis());
                        assigneeOutTime.put(firebaseUid, timeArray);
                    }
                    tasksRef.document(taskId).update("assigneeOutTime", assigneeOutTime);
                    fabTaskDetailsNowIn.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_48dp));
                }
                break;
        }
    }

    @Override
    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
        for (Task task : value.toObjects(Task.class)){
            progressReadingSubtasks.add(task.getProgressReading());
        }
    }

    @Override
    public void onDocumentSelected(DocumentSnapshot doc) { }

    @Override
    public void onMessageSelected(DocumentSnapshot message) { }

    @Override
    public void onTaskSelected(DocumentSnapshot event) {
        Task task = event.toObject(Task.class);
        Bundle bundle = new Bundle();
        bundle = UtilsMethods.generateBundleToTaskDetailsPage(task);
        Navigation.findNavController(recycler_superiorTasks).navigate(R.id.taskDetailsAll, bundle);
    }

    @Override
    public void onActivitySelected(DocumentSnapshot activity) {

    }

    @Override
    public void onUserSelected(DocumentSnapshot user) {

    }

    @Override
    public void onNotificationSelected(DocumentSnapshot notification, View v) {

    }

    private float nextTaskNewProgressReading(float nextTaskSubTasksSize, float thisTaskNewProgressReading,
                                             float thisTaskOldProgressReading, float nextTaskOldProgressReading){
        float nextTaskNewProgressReading = nextTaskOldProgressReading
                + (thisTaskNewProgressReading - thisTaskOldProgressReading) / (nextTaskSubTasksSize + 1);
        return nextTaskNewProgressReading;
    }

    @Override
    public void getDialogData(Bundle bundle) {
        String dialogFlag = bundle.getString("dialogFlag");
        if (dialogFlag.equals("commentDialog")){

        } else if (dialogFlag.equals("confirmations")){
            HashMap<String, ArrayList<String>> potentialSuperiorTasksMap;
            HashMap<String, ArrayList<String>> potentialSubTasksMap;
            HashMap<String, ArrayList<String>> potentialAssigneeMap;
            HashMap<String, ArrayList<String>> potentialSuperiorActivitiesMap;
            HashMap<String, ArrayList<String>> requestAssignee;
            HashMap<String, ArrayList<String>> requestSuperiorActivitiesMap;
            HashMap<String, ArrayList<String>> requestSuperiorTasksMap;
            HashMap<String, ArrayList<String>> requestSubTasksMap;
            potentialSuperiorTasksMap = (HashMap<String, ArrayList<String>>) bundle.getSerializable("potentialSuperiorTasksMap");
            potentialSubTasksMap = (HashMap<String, ArrayList<String>>) bundle.getSerializable("potentialSubTasksMap");
            potentialAssigneeMap = (HashMap<String, ArrayList<String>>) bundle.getSerializable("potentialAssigneeMap");
            potentialSuperiorActivitiesMap = (HashMap<String, ArrayList<String>>) bundle.getSerializable("potentialSuperiorActivitiesMap");
            requestAssignee = (HashMap<String, ArrayList<String>>) bundle.getSerializable("requestAssignee");
            requestSuperiorActivitiesMap = (HashMap<String, ArrayList<String>>) bundle.getSerializable("requestSuperiorActivitiesMap");
            requestSuperiorTasksMap = (HashMap<String, ArrayList<String>>) bundle.getSerializable("requestSuperiorTasksMap");
            requestSubTasksMap = (HashMap<String, ArrayList<String>>) bundle.getSerializable("requestSubTasksMap");
            tasksRef.document(taskId).update("potentialSuperiorTasksMap", potentialSuperiorTasksMap);
            tasksRef.document(taskId).update("potentialSubTasksMap", potentialSubTasksMap);
            tasksRef.document(taskId).update("potentialAssigneeMap", potentialAssigneeMap);
            tasksRef.document(taskId).update("potentialSuperiorActivitiesMap", potentialSuperiorActivitiesMap);
            tasksRef.document(taskId).update("requestAssignee", requestAssignee);
            tasksRef.document(taskId).update("requestSuperiorActivities", requestSuperiorActivitiesMap);
            tasksRef.document(taskId).update("requestSuperiorTasks", requestSuperiorTasksMap);
            tasksRef.document(taskId).update("requestSubTasks", requestSubTasksMap);
            ArrayList<String> assignee = new ArrayList<>();
            ArrayList<String> subtasks = new ArrayList<>();
            ArrayList<String> superiortasks = new ArrayList<>();
            for (DocumentSnapshot doc : mAdaptorTasksCurrentTaskOnly.retrieveSnapshot()){
                Task thisTask = doc.toObject(Task.class);
                assignee = thisTask.getAssignee();
                subtasks = thisTask.getSubTasks();
                superiortasks = thisTask.getSuperiorTasks();
            }
            checkVotingAndUpdate(potentialSuperiorTasksMap, "potentialSuperiorTasksMap", assignee, mAdaptorTasks, null,
                    superiortasks, "superiorTasks", tasksRef, null, taskId, "requestSubTasks", "subTasks");
            checkVotingAndUpdate(requestSubTasksMap, "requestSubTasks", assignee, mAdaptorTasks, null,
                    subtasks, "subTasks", tasksRef, null, taskId, "potentialSuperiorTasksMap", "superiorTasks");
        }
    }
    private void checkVotingAndUpdate(HashMap<String, ArrayList<String>> potentialOrRequestSubOrSuperiorTasksOrActivitiesMap, String potentialSubOrSuperiorTasksOrActivitiesMapStringThisTask,
                                      ArrayList<String> assignee, TaskAdaptor mAdaptorTasks, ActivityAdaptor mAdaptorActivity,
                                      ArrayList<String> subOrSuperiorTasksOrActivities, String subOrSuperiorTasksOrActivitiesStringThisTask, CollectionReference tasksRef, CollectionReference activitiesRef,
                                      String taskId, String requestOrPotentialSubOrSuperiorTasksOrActivitiesStringTask, String subOrSuperiorTasksStringRequestOrPotentialTaskOrActivity){
        for (String confirmationItem : potentialOrRequestSubOrSuperiorTasksOrActivitiesMap.keySet()) {
            ArrayList<String> confirmationList = potentialOrRequestSubOrSuperiorTasksOrActivitiesMap.get(confirmationItem);
            Set<String> confirmationSet = new HashSet<>(confirmationList);
            Set<String> taskAssigneeSet = new HashSet<>(assignee);
            if (taskAssigneeSet.equals(confirmationSet)) {

                Log.d("FuckYourMotherCunt1", "triggered");//potentialSubTask accepted by current task members, then check if requestSubOrSuperiorTask is accepted
                for (DocumentSnapshot taskDoc : mAdaptorTasks.retrieveSnapshot()) {
                    Task task = taskDoc.toObject(Task.class);
                    String taskId1 = task.getTaskId();
                    if (taskId1.equals(confirmationItem)) {
                        Log.d("FuckYourMotherCunt2", "triggered");
                        if (potentialSubOrSuperiorTasksOrActivitiesMapStringThisTask.equals("potentialSubTasksMap")) {
                            Log.d("FuckYourMotherCunt3", "triggered");
                            HashMap<String, ArrayList<String>> requestItem = task.getRequestSuperiorTasks();
                            ArrayList<String> superiorTask = task.getSuperiorTasks();
                            ArrayList<String> requestItemConfirmList = requestItem.get(taskId);
                            ArrayList<String> beingRequestPeople = task.getAssignee();
                            Set<String> beingRequestPeopleSet = new HashSet<>(beingRequestPeople);
                            Set<String> requestItemConfirmationListSet = new HashSet<>(requestItemConfirmList);
                            if (beingRequestPeopleSet.equals(requestItemConfirmationListSet)) {
                                //both potentialSubTask & requestSuperiorTask are accepted.
                                Log.d("FuckYourMotherCunt4", "triggered");
                                potentialOrRequestSubOrSuperiorTasksOrActivitiesMap.remove(confirmationItem);
                                subOrSuperiorTasksOrActivities.add(confirmationItem);
                                requestItem.remove(taskId);
                                superiorTask.add(taskId);
                                tasksRef.document(taskId).update(potentialSubOrSuperiorTasksOrActivitiesMapStringThisTask, potentialOrRequestSubOrSuperiorTasksOrActivitiesMap);
                                tasksRef.document(taskId).update(subOrSuperiorTasksOrActivitiesStringThisTask, subOrSuperiorTasksOrActivities);
                                tasksRef.document(confirmationItem).update(requestOrPotentialSubOrSuperiorTasksOrActivitiesStringTask, requestItem);
                                tasksRef.document(confirmationItem).update(subOrSuperiorTasksStringRequestOrPotentialTaskOrActivity, superiorTask);
                            }
                        } else if (potentialSubOrSuperiorTasksOrActivitiesMapStringThisTask.equals("potentialSuperiorTasksMap")) {
                            Log.d("FuckYourMotherCunt5", "triggered");
                            HashMap<String, ArrayList<String>> requestItem2 = task.getRequestSubTasks();
                            ArrayList<String> subTask = task.getSubTasks();
                            ArrayList<String> requestItemConfirmList2 = requestItem2.get(taskId);
                            ArrayList<String> beingRequestPeople2 = task.getAssignee();
                            Set<String> beingRequestPeopleSet2 = new HashSet<>(beingRequestPeople2);
                            Set<String> requestItemConfirmationListSet2 = new HashSet<>(requestItemConfirmList2);
                            Log.d("beingRequestPeopleSet2", beingRequestPeopleSet2.toString());
                            Log.d("requestItem2", requestItem2.toString());
                            if (beingRequestPeopleSet2.equals(requestItemConfirmationListSet2)) {
                                //both potentialSubTask & requestSuperiorTask are accepted.
                                Log.d("FuckYourMotherCunt6", "triggered");
                                potentialOrRequestSubOrSuperiorTasksOrActivitiesMap.remove(confirmationItem);
                                subOrSuperiorTasksOrActivities.add(confirmationItem);
                                requestItem2.remove(taskId);
                                subTask.add(taskId);
                                tasksRef.document(taskId).update(potentialSubOrSuperiorTasksOrActivitiesMapStringThisTask, potentialOrRequestSubOrSuperiorTasksOrActivitiesMap);
                                tasksRef.document(taskId).update(subOrSuperiorTasksOrActivitiesStringThisTask, subOrSuperiorTasksOrActivities);
                                tasksRef.document(confirmationItem).update(requestOrPotentialSubOrSuperiorTasksOrActivitiesStringTask, requestItem2);
                                tasksRef.document(confirmationItem).update(subOrSuperiorTasksStringRequestOrPotentialTaskOrActivity, subTask);
                            }
                        } else if (potentialSubOrSuperiorTasksOrActivitiesMapStringThisTask.equals("requestSuperiorTasks")){
                            ////////////////////////////////////////
                            HashMap<String, ArrayList<String>> potentialItem2 = task.getPotentialSubTasksMap();
                            ArrayList<String> subTask = task.getSubTasks();
                            ArrayList<String> potentialItemConfirmList2 = potentialItem2.get(taskId);
                            ArrayList<String> requestingPeople2 = task.getAssignee();
                            Set<String> requestingPeopleSet2 = new HashSet<>(requestingPeople2);
                            Set<String> potentialItemConfirmationListSet2 = new HashSet<>(potentialItemConfirmList2);
                            Log.d("requestingPeople2", requestingPeopleSet2.toString());
                            Log.d("potentialItem2", potentialItem2.toString());
                            if (requestingPeopleSet2.equals(potentialItemConfirmationListSet2)) {
                                //both potentialSubTask & requestSuperiorTask are accepted.
                                Log.d("FuckYourMotherCunt7", "triggered");
                                potentialOrRequestSubOrSuperiorTasksOrActivitiesMap.remove(confirmationItem);
                                subOrSuperiorTasksOrActivities.add(confirmationItem);
                                potentialItem2.remove(taskId);
                                subTask.add(taskId);
                                tasksRef.document(taskId).update(potentialSubOrSuperiorTasksOrActivitiesMapStringThisTask, potentialOrRequestSubOrSuperiorTasksOrActivitiesMap);
                                tasksRef.document(taskId).update(subOrSuperiorTasksOrActivitiesStringThisTask, subOrSuperiorTasksOrActivities);
                                tasksRef.document(confirmationItem).update(requestOrPotentialSubOrSuperiorTasksOrActivitiesStringTask, potentialItem2);
                                tasksRef.document(confirmationItem).update(subOrSuperiorTasksStringRequestOrPotentialTaskOrActivity, subTask);
                            }
                        } else if (potentialSubOrSuperiorTasksOrActivitiesMapStringThisTask.equals("requestSubTasks")){
                            HashMap<String, ArrayList<String>> potentialItem2 = task.getPotentialSuperiorTasksMap();
                            ArrayList<String> superiorTask = task.getSuperiorTasks();
                            ArrayList<String> potentialItemConfirmList2 = potentialItem2.get(taskId);
                            ArrayList<String> requestingPeople2 = task.getAssignee();
                            Set<String> requestingPeopleSet2 = new HashSet<>(requestingPeople2);
                            Set<String> potentialItemConfirmationListSet2 = new HashSet<>(potentialItemConfirmList2);
                            Log.d("requestingPeople2", requestingPeopleSet2.toString());
                            Log.d("potentialItem2", potentialItem2.toString());
                            if (requestingPeopleSet2.equals(potentialItemConfirmationListSet2)) {
                                //both potentialSubTask & requestSuperiorTask are accepted.
                                Log.d("FuckYourMotherCunt7", "triggered");
                                potentialOrRequestSubOrSuperiorTasksOrActivitiesMap.remove(confirmationItem);
                                subOrSuperiorTasksOrActivities.add(confirmationItem);
                                potentialItem2.remove(taskId);
                                superiorTask.add(taskId);
                                tasksRef.document(taskId).update(potentialSubOrSuperiorTasksOrActivitiesMapStringThisTask, potentialOrRequestSubOrSuperiorTasksOrActivitiesMap);
                                tasksRef.document(taskId).update(subOrSuperiorTasksOrActivitiesStringThisTask, subOrSuperiorTasksOrActivities);
                                tasksRef.document(confirmationItem).update(requestOrPotentialSubOrSuperiorTasksOrActivitiesStringTask, potentialItem2);
                                tasksRef.document(confirmationItem).update(subOrSuperiorTasksStringRequestOrPotentialTaskOrActivity, superiorTask);
                            }
                        }
                    }
                }
            }
        }
        for (String confirmationItemActivity : potentialOrRequestSubOrSuperiorTasksOrActivitiesMap.keySet()){
            ArrayList<String> confirmationListActivity = potentialOrRequestSubOrSuperiorTasksOrActivitiesMap.get(confirmationItemActivity);
            Set<String> confirmationActivitySet = new HashSet<>(confirmationListActivity);
            Set<String> assigneeSet = new HashSet<>(assignee);
            if (assigneeSet.equals(confirmationActivitySet)){
                for (DocumentSnapshot activityDoc : mAdaptorActivity.retrieveSnapshot()){
                    Activity activity = activityDoc.toObject(Activity.class);
                    String actId = activity.getActivityId();
                    if (actId.equals(confirmationItemActivity)){
                        if (potentialSubOrSuperiorTasksOrActivitiesMapStringThisTask.equals("potentialSuperiorActivitiesMap")){
                            HashMap<String, ArrayList<String>> requestTasks = activity.getRequestTasks();
                            ArrayList<String> tasksList = activity.getTasksList();
                            ArrayList<String> requestTasksConfirmList = requestTasks.get(taskId);
                            ArrayList<String> beingRequestPeople = activity.getAssignee();
                            Set<String> beingRequestPeopleSet = new HashSet<>(beingRequestPeople);
                            Set<String> requestTasksConfirmationListSet = new HashSet<>(requestTasksConfirmList);
                            if (beingRequestPeopleSet.equals(requestTasksConfirmationListSet)){
                                potentialOrRequestSubOrSuperiorTasksOrActivitiesMap.remove(confirmationItemActivity);
                                subOrSuperiorTasksOrActivities.add(confirmationItemActivity);
                                requestTasks.remove(taskId);
                                tasksList.add(taskId);
                                tasksRef.document(taskId).update(potentialSubOrSuperiorTasksOrActivitiesMapStringThisTask, potentialOrRequestSubOrSuperiorTasksOrActivitiesMap);
                                tasksRef.document(taskId).update(subOrSuperiorTasksOrActivitiesStringThisTask, subOrSuperiorTasksOrActivities);
                                activitiesRef.document(confirmationItemActivity).update(requestOrPotentialSubOrSuperiorTasksOrActivitiesStringTask, requestTasks);
                                activitiesRef.document(confirmationItemActivity).update(subOrSuperiorTasksStringRequestOrPotentialTaskOrActivity, tasksList);
                            }
                        } else if (potentialSubOrSuperiorTasksOrActivitiesMapStringThisTask.equals("requestSuperiorActivities")){
                            HashMap<String, ArrayList<String>> potentialTasks = activity.getPotentialTasksMap();
                            ArrayList<String> tasksList = activity.getTasksList();
                            ArrayList<String> requestTasksConfirmList = potentialTasks.get(taskId);
                            ArrayList<String> beingRequestPeople = activity.getAssignee();
                            Set<String> beingRequestPeopleSet = new HashSet<>(beingRequestPeople);
                            Set<String> requestTasksConfirmationListSet = new HashSet<>(requestTasksConfirmList);
                            if (beingRequestPeopleSet.equals(requestTasksConfirmationListSet)){
                                potentialOrRequestSubOrSuperiorTasksOrActivitiesMap.remove(confirmationItemActivity);
                                subOrSuperiorTasksOrActivities.add(confirmationItemActivity);
                                potentialTasks.remove(taskId);
                                tasksList.add(taskId);
                                tasksRef.document(taskId).update(potentialSubOrSuperiorTasksOrActivitiesMapStringThisTask, potentialOrRequestSubOrSuperiorTasksOrActivitiesMap);
                                tasksRef.document(taskId).update(subOrSuperiorTasksOrActivitiesStringThisTask, subOrSuperiorTasksOrActivities);
                                activitiesRef.document(confirmationItemActivity).update(requestOrPotentialSubOrSuperiorTasksOrActivitiesStringTask, potentialTasks);
                                activitiesRef.document(confirmationItemActivity).update(subOrSuperiorTasksStringRequestOrPotentialTaskOrActivity, tasksList);
                            }
                        }
                    }
                }
            }
        }
    }
    private void showBottomSheetDialog(){

    }
}