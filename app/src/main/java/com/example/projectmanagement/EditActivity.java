package com.example.projectmanagement;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.projectmanagement.Adaptors.ActivityAdaptor;
import com.example.projectmanagement.Adaptors.TaskAdaptor;
import com.example.projectmanagement.Adaptors.UsersAdaptor;
import com.example.projectmanagement.Interface.OnListItemSelectedListener;
import com.example.projectmanagement.Model.Activity;
import com.example.projectmanagement.Model.Task;
import com.example.projectmanagement.Model.Users;
import com.example.projectmanagement.Utils.UtilsMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditActivity#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditActivity extends Fragment implements View.OnClickListener, OnListItemSelectedListener{

    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private CollectionReference usersRef, activityRef, notificationRef, taskRef;
    private Query mQueryUsers, mQueryActivities, mQuerySubActivities, mQuerySubTasks, mQueryNotifications;
    private UsersAdaptor mAdaptorUsers;
    private ActivityAdaptor mAdaptorActivities;
    private TaskAdaptor mAdaptorSubTasks;

    private ChipGroup setActivityAssignee, setActivityToWhichActivity, setIncludeWhichActivities, setIncludeWhichTasks;
    private EditText setActivityTitle, setActivityScope, setActivityDescription;
    private ImageButton addAssignee, addActivity, addSubActivities, addSubTasks;
    private Button confirmButton;

    private String uid, userDisplayName, newActivityId, taskId, status, notificationId,
            activityIdFromActivityDetails, activityTitleFromActivityDetails, activityScopeFromActivityDetails, activityDescriptionFromActivityDetails;
    private String invitationNotificationDescriptionTemplateActivity =
            "The activity title is: %s, the activity scope is: %s, the activity description: %s";
    private String notificationDescription;
    private int pageIndex;
    private static final int LIMIT = 20;

    private ArrayList<String> potentialAssignee = new ArrayList<>();
    private ArrayList<String> assignee = new ArrayList<>();
    private ArrayList<String> potentialActivities = new ArrayList<>();
    private ArrayList<String> activities = new ArrayList<>();
    private ArrayList<String> potentialSubActivities = new ArrayList<>();
    private ArrayList<String> subActivities = new ArrayList<>();
    private ArrayList<String> potentialTasks = new ArrayList<>();
    private ArrayList<String> tasks = new ArrayList<>();
    private ArrayList<String> activityMembersAllForNotification = new ArrayList<>();
    private ArrayList<String> tasksMembersAllForNotification = new ArrayList<>();
    private HashMap<Integer, String> chipUsersId = new HashMap<>();
    private HashMap<Integer, String> chipsActivitiesId = new HashMap<>();
    private HashMap<Integer, String> chipsSubActivitiesId = new HashMap<>();
    private HashMap<Integer, String> chipsSubTasksId = new HashMap<>();
    private ArrayList<String> notificationsPeople = new ArrayList<>();
    private Bundle initData = new Bundle();
    private Dialog dialog;
    private int dialogFlag = 0;
    private boolean fromActivityDetails = false;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EditActivity() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditActivity.
     */
    // TODO: Rename and change types and number of parameters
    public static EditActivity newInstance(String param1, String param2) {
        EditActivity fragment = new EditActivity();
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
            //get all init data here
            initData = getArguments();
            fromActivityDetails = initData.getBoolean("carryActivityDetails");
        }
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        uid = mFirebaseUser.getUid();
        iniFireStore();
        initFragmentData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit_activity, container, false);
        setActivityTitle = root.findViewById(R.id.setActivityTitle);
        setActivityScope = root.findViewById(R.id.setActivityScope);
        setActivityDescription = root.findViewById(R.id.setActivityDescription);
        addAssignee = root.findViewById(R.id.addAssignee);
        addActivity = root.findViewById(R.id.addActivity);
        addSubActivities = root.findViewById(R.id.addSubActivities);
        addSubTasks = root.findViewById(R.id.addSubTasks);
        setActivityAssignee = root.findViewById(R.id.setActivityAssignee);
        setActivityToWhichActivity = root.findViewById(R.id.setActivityToWhichActivity);
        setIncludeWhichActivities = root.findViewById(R.id.setIncludeWhichActivities);
        setIncludeWhichTasks = root.findViewById(R.id.setIncludeWhichTasks);
        confirmButton = root.findViewById(R.id.confirmActivity);
        addActivity.setOnClickListener(this);
        addAssignee.setOnClickListener(this);
        addSubActivities.setOnClickListener(this);
        addSubTasks.setOnClickListener(this);
        confirmButton.setOnClickListener(this);
        initFragmentUI();
        return root;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.addActivity:
                dialogFlag = R.id.addActivity;
                mAdaptorActivities = new ActivityAdaptor(mQueryActivities, this);
                mAdaptorActivities.startListening();
                pageIndex = R.layout.item_activities;
                dialog = UtilsMethods.createDialog(getContext(), pageIndex, mAdaptorActivities, R.layout.fragment_items_list, null, null, null);
                dialog.show();
                break;
            case R.id.addAssignee:
                dialogFlag = R.id.addAssignee;
                mAdaptorUsers = new UsersAdaptor(mQueryUsers, this);
                mAdaptorUsers.startListening();
                pageIndex = R.layout.item_users;
                dialog = UtilsMethods.createDialog(getContext(), pageIndex, mAdaptorUsers, R.layout.fragment_items_list, null, null, null);
                dialog.show();
                break;
            case R.id.addSubActivities:
                dialogFlag = R.id.addSubActivities;
                mAdaptorActivities = new ActivityAdaptor(mQuerySubActivities, this);
                mAdaptorActivities.startListening();
                pageIndex = R.layout.item_activities;
                dialog = UtilsMethods.createDialog(getContext(), pageIndex, mAdaptorActivities, R.layout.fragment_items_list, null, null, null);
                dialog.show();
                break;
            case R.id.addSubTasks:
                dialogFlag = R.id.addSubTasks;
                mAdaptorSubTasks = new TaskAdaptor(mQuerySubTasks, this);
                mAdaptorSubTasks.startListening();
                pageIndex = R.layout.item_tasks;
                dialog = UtilsMethods.createDialog(getContext(), pageIndex, mAdaptorSubTasks, R.layout.fragment_items_list, null, null, null);
                dialog.show();
                break;
            case R.id.confirmActivity:
                if (fromActivityDetails){
                    //just update the field in firestore database
                    //update the arraylists from hashmaps here first
                    //
                    //activityRef.document(activityIdFromActivityDetails).update("assignee", assignee);
                    //                    activityRef.document(activityIdFromActivityDetails).update("potentialAssignee", potentialAssignee);
                    //                    activityRef.document(activityIdFromActivityDetails).update("tasksList", tasks);
                    //                    activityRef.document(activityIdFromActivityDetails).update("potentialTasksList", potentialTasks);
                    //                    activityRef.document(activityIdFromActivityDetails).update("activitiesList", activities);
                    //                    activityRef.document(activityIdFromActivityDetails).update("potentialActivitiesList", potentialActivities);
                    //                    activityRef.document(activityIdFromActivityDetails).update("potentialSubActivitiesList", potentialSubActivities);
                    //                    activityRef.document(activityIdFromActivityDetails).update("subActivitiesList", subActivities);
                    //                    activityRef.document(activityIdFromActivityDetails).update("activityTitle", setActivityTitle.getText().toString());
                    //                    activityRef.document(activityIdFromActivityDetails).update("scope", setActivityScope.getText().toString());
                    //                    activityRef.document(activityIdFromActivityDetails).update("description", setActivityDescription.getText().toString());
                } else {
                    String activityTitle = setActivityTitle.getText().toString();
                    String activityScope = setActivityScope.getText().toString();
                    String activityDescription = setActivityDescription.getText().toString();
                    notificationDescription = String.format(invitationNotificationDescriptionTemplateActivity,
                            activityTitle, activityScope, activityDescription);
                    Calendar calendar = Calendar.getInstance();
                    Long timeCreated = calendar.getTimeInMillis();
                    if (potentialAssignee.contains(uid)){
                        assignee.add(uid);
                        potentialAssignee.remove(uid);
                    }
                    if (activityMembersAllForNotification.contains(uid)){
                        activityMembersAllForNotification.remove(uid);
                    }
                    if (tasksMembersAllForNotification.contains(uid)){
                        tasksMembersAllForNotification.remove(uid);
                    }
                    if ((potentialAssignee.size() > 0) || (activityMembersAllForNotification.size() > 0) || (tasksMembersAllForNotification.size() > 0)){
                        status = getString(R.string.status_waiting_for_confirmation);
                    } else {
                        status = getString(R.string.status_completed_confirmation);
                    }
                    Set<String> set = new LinkedHashSet<>(potentialAssignee);
                    set.addAll(activityMembersAllForNotification);
                    set.addAll(tasksMembersAllForNotification);
                    notificationsPeople = new ArrayList<String>(set);

                    for (String subAct : chipsSubActivitiesId.values()){potentialSubActivities.add(subAct);}
                    for (String subTask : chipsSubTasksId.values()){potentialTasks.add(subTask);}
                    //for loop for potentialActivities here
                    for (String superiorAct : chipsActivitiesId.values()){potentialActivities.add(superiorAct);}

                    Activity newActivity = new Activity(activityTitle, activityScope, activityDescription, timeCreated, status,
                            assignee, potentialAssignee, 0, uid, tasks, potentialTasks, activities, potentialActivities,
                            subActivities, potentialSubActivities, new ArrayList<String>(set), new ArrayList<String>(set));

                    activityRef.add(newActivity).addOnSuccessListener(documentReference -> {
                        newActivityId = documentReference.getId();
                        documentReference.update("activityId", newActivityId);
                        if (assignee.contains(uid)){
                            usersRef.document(uid).get().addOnSuccessListener(snapshot -> {
                                ArrayList<String> userActivities;
                                userActivities = snapshot.toObject(Users.class).getActivities();
                                userActivities.add(newActivityId);
                                usersRef.document(uid).update("activities", userActivities);
                            });
                        }
                        UtilsMethods.createNotification(getString(R.string.Invitation_join_activity), newActivityId, taskId,
                                notificationDescription, uid, timeCreated, notificationsPeople, notificationRef);
                        //remove the activity with uid as only member from potentialActivities
                        for (String actId : potentialActivities){
                            activityRef.document(actId).get().addOnCompleteListener(task -> {
                                HashMap<String, ArrayList<String>> requestSubActivitiesTemp;
                                requestSubActivitiesTemp = task.getResult().toObject(Activity.class).getRequestSubActivities();
                                if (task.isSuccessful()){
                                    if ((task.getResult().toObject(Activity.class).getAssignee().contains(uid))
                                            && (task.getResult().toObject(Activity.class).getAssignee().size() == 1)){
                                        documentReference.get().addOnSuccessListener(snapshot -> {
                                            ArrayList<String> activitiesListTemp;
                                            ArrayList<String> potentialActivitiesListTemp;
                                            activitiesListTemp = snapshot.toObject(Activity.class).getActivitiesList();
                                            potentialActivitiesListTemp = snapshot.toObject(Activity.class).getPotentialActivitiesList();
                                            activitiesListTemp.add(actId);
                                            potentialActivitiesListTemp.remove(actId);
                                            documentReference.update("activitiesList", activitiesListTemp);
                                            documentReference.update("potentialActivitiesList", potentialActivitiesListTemp);

                                            ArrayList<String> subActivitiesOfSuperiorActivity;
                                            subActivitiesOfSuperiorActivity = task.getResult().toObject(Activity.class).getSubActivitiesList();
                                            subActivitiesOfSuperiorActivity.add(newActivityId);
                                            activityRef.document(actId).update("subActivitiesList", subActivitiesOfSuperiorActivity);
                                        });
                                    } else if ((task.getResult().toObject(Activity.class).getAssignee().contains(uid))
                                            && (task.getResult().toObject(Activity.class).getAssignee().size() > 1)){
                                        //go to the "potentialActivity" document to update the potentialSubActivity field.
                                        requestSubActivitiesTemp.put(newActivityId, new ArrayList<>(Arrays.asList(uid)));
                                        activityRef.document(actId).update("requestSubActivities", requestSubActivitiesTemp);
                                    } else if ((!task.getResult().toObject(Activity.class).getAssignee().contains(uid))
                                            && (task.getResult().toObject(Activity.class).getAssignee().size() > 0)){
                                        requestSubActivitiesTemp.put(newActivityId, new ArrayList<>());
                                        activityRef.document(actId).update("requestSubActivities", requestSubActivitiesTemp);
                                    }
                                } else{ Log.d("retrievingPotentialActivities", "failed");}
                            });
                        }
                        for (String subAct : potentialSubActivities){
                            activityRef.document(subAct).get().addOnSuccessListener(snapshot -> {
                                Activity activity = snapshot.toObject(Activity.class);
                                HashMap<String, ArrayList<String>> requestSuperiorActivitiesTemp;
                                requestSuperiorActivitiesTemp = activity.getRequestSuperiorActivities();
                                if ((activity.getAssignee().contains(uid)) && (activity.getAssignee().size() == 1)){
                                    documentReference.get().addOnSuccessListener(snapshot12 -> {
                                        ArrayList<String> subActivitiesListTemp;
                                        ArrayList<String> potentialSubActivitiesListTemp;
                                        subActivitiesListTemp = snapshot.toObject(Activity.class).getSubActivitiesList();
                                        potentialSubActivitiesListTemp = snapshot.toObject(Activity.class).getPotentialSubActivitiesList();
                                        subActivitiesListTemp.add(subAct);
                                        potentialSubActivitiesListTemp.remove(subAct);
                                        documentReference.update("subActivitiesList", subActivitiesListTemp);
                                        documentReference.update("potentialSubActivitiesList", potentialSubActivitiesListTemp);

                                        ArrayList<String> superiorActivitiesOfSubActivity;
                                        superiorActivitiesOfSubActivity = activity.getActivitiesList();
                                        superiorActivitiesOfSubActivity.add(newActivityId);
                                        activityRef.document(subAct).update("activitiesList", superiorActivitiesOfSubActivity);
                                    });
                                } else if ((activity.getAssignee().contains(uid)) && (activity.getAssignee().size() > 1)){
                                    requestSuperiorActivitiesTemp.put(newActivityId, new ArrayList<>(Arrays.asList(uid)));
                                    activityRef.document(subAct).update("requestSuperiorActivities", requestSuperiorActivitiesTemp);
                                } else if (!activity.getAssignee().contains(uid)){
                                    requestSuperiorActivitiesTemp.put(newActivityId, new ArrayList<>());
                                    activityRef.document(subAct).update("requestSuperiorActivities", requestSuperiorActivitiesTemp);
                                }
                            });
                        }
                        for (String taskId : potentialTasks){
                            taskRef.document(taskId).get().addOnSuccessListener(snapshot -> {
                                Task task = snapshot.toObject(Task.class);
                                HashMap<String, ArrayList<String>> requestSuperiorActivityTemp;
                                requestSuperiorActivityTemp = task.getRequestSuperiorActivities();
                                if ((task.getAssignee().contains(uid)) && (task.getAssignee().size() == 1)){
                                    documentReference.get().addOnSuccessListener(snapshot1 -> {
                                        Activity act = snapshot1.toObject(Activity.class);
                                        ArrayList<String> tasksTemp;
                                        ArrayList<String> potentialTasksTemp;
                                        tasksTemp = act.getTasksList();
                                        potentialTasksTemp = act.getPotentialTasksList();
                                        tasksTemp.add(taskId);
                                        potentialTasksTemp.remove(taskId);
                                        documentReference.update("tasksList", tasksTemp);
                                        documentReference.update("potentialTasksList", potentialTasksTemp);
                                    });
                                } else if ((task.getAssignee().contains(uid)) && (task.getAssignee().size() > 1)){
                                    requestSuperiorActivityTemp.put(newActivityId, new ArrayList<>(Arrays.asList(uid)));
                                    taskRef.document(taskId).update("requestSuperiorActivities", requestSuperiorActivityTemp);
                                } else if ((!task.getAssignee().contains(uid)) && (task.getAssignee().size() > 0)){
                                    requestSuperiorActivityTemp.put(newActivityId, new ArrayList<>());
                                    taskRef.document(taskId).update("requestSuperiorActivities", requestSuperiorActivityTemp);
                                }
                            });
                        }

                    });
                }
                break;
        }
    }
    private void iniFireStore(){
        mFirestore = FirebaseFirestore.getInstance();
        usersRef = mFirestore.collection("Users");
        activityRef = mFirestore.collection("Activities");
        notificationRef = mFirestore.collection("Notifications");
        taskRef = mFirestore.collection("Tasks");
        mQueryUsers = usersRef.orderBy("timeCreated", Query.Direction.DESCENDING)
                .limit(LIMIT);
        mQueryActivities = activityRef.orderBy("timeCreated", Query.Direction.DESCENDING)
                .limit(LIMIT);
        mQuerySubActivities = activityRef.orderBy("timeCreated", Query.Direction.DESCENDING)
                .limit(LIMIT);
        mQuerySubTasks = taskRef.orderBy("timeCreated", Query.Direction.DESCENDING)
                .limit(LIMIT);
        mQueryNotifications = notificationRef.orderBy("timeCreated", Query.Direction.DESCENDING)
                .whereEqualTo("receiver", uid)
                .limit(LIMIT);
        usersRef.document(uid).get().addOnCompleteListener(task -> {
           if (task.isSuccessful()){
               userDisplayName = task.getResult().toObject(Users.class).getDisplayName();
           }
        });
    }

    @Override
    public void onDocumentSelected(DocumentSnapshot doc) {

    }

    @Override
    public void onMessageSelected(DocumentSnapshot message) {

    }

    @Override
    public void onTaskSelected(DocumentSnapshot data) {
        Task task = data.toObject(Task.class);
        String taskId = task.getTaskId();
        String taskTitle = task.getTaskTitle();
        switch(dialogFlag){
            case R.id.addSubTasks:
                UtilsMethods.addChips(getContext(), setIncludeWhichTasks, chipsSubTasksId, taskId, taskTitle, false);
                dialog.hide();
                break;
            }
        taskRef.document(taskId).get().addOnSuccessListener(snapshot -> {
            ArrayList<String> taskMembers;
            taskMembers = snapshot.toObject(Task.class).getAssignee();
            Log.d("taskMembers", taskMembers.toString());

            Set<String> set = new LinkedHashSet<>(tasksMembersAllForNotification);
            set.addAll(taskMembers);
            tasksMembersAllForNotification = new ArrayList<String>(set);
        });
        dialogFlag = 0;
        dialog.hide();
        if (mAdaptorSubTasks != null){
            mAdaptorSubTasks.stopListening();
        }
    }

    @Override
    public void onActivitySelected(DocumentSnapshot activity) {
        Activity act = activity.toObject(Activity.class);
        String activityTitle = act.getActivityTitle();
        String activityId = act.getActivityId();
        switch(dialogFlag){
            case R.id.addActivity:
                UtilsMethods.addChips(getContext(), setActivityToWhichActivity, chipsActivitiesId, activityId, activityTitle, false);
                break;
            case R.id.addSubActivities:
                UtilsMethods.addChips(getContext(), setIncludeWhichActivities, chipsSubActivitiesId, activityId, activityTitle, false);
                break;
        }
        activityRef.document(activityId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                ArrayList<String> activityMembers;
                activityMembers = task.getResult().toObject(Activity.class).getAssignee();
                Log.d("activityMembers", activityMembers.toString());

                Set<String> set = new LinkedHashSet<>(activityMembersAllForNotification);
                set.addAll(activityMembers);
                activityMembersAllForNotification = new ArrayList<String>(set);
            }
        });
        dialogFlag = 0;
        dialog.hide();
        if (mAdaptorActivities != null){
            mAdaptorActivities.stopListening();
        }
    }

    @Override
    public void onUserSelected(DocumentSnapshot user) {
        Users users = user.toObject(Users.class);
        String userName = users.getDisplayName();
        String uid = users.getUid();
        if (!potentialAssignee.contains(uid)){
            Chip chip = new Chip(getContext());
            chip.setText(userName);
            chip.setChipIconResource(R.drawable.ic_clear);
            chip.setOnClickListener(v -> {
                setActivityAssignee.removeView(v);
                potentialAssignee.remove(chipUsersId.get(v.getId()));
            });
            setActivityAssignee.addView(chip);
            chipUsersId.put(chip.getId(), uid);
            potentialAssignee.add(uid);
        }
        dialog.hide();
    }
    @Override
    public void onNotificationSelected(DocumentSnapshot notification, View v) {

    }
    private void initFragmentData(){
        /*bundle.putString("activityTitle", act.getActivityTitle());
        bundle.putString("activityScope", act.getScope());
        bundle.putString("activityDescription", act.getDescription());
        bundle.putInt("progressReading", act.getProgressReading());
        bundle.putString("activityId", act.getActivityId());
        bundle.putLong("activityStartDate", act.getActivityStartDate());
        bundle.putLong("activityEndDate", act.getActivityEndDate());
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
        bundle.putSerializable("requestSubActivities", requestSubActivities);
        bundle.putSerializable("requestSuperiorActivities", requestSuperiorActivities);
        bundle.putSerializable("requestTasks", requestTasks);*/
        if (initData != null && fromActivityDetails){
            assignee = initData.getStringArrayList("assignee");
            potentialAssignee = initData.getStringArrayList("potentialAssignee");
            activities = initData.getStringArrayList("activitiesList");
            potentialActivities = initData.getStringArrayList("potentialActivitiesList");
            subActivities = initData.getStringArrayList("subActivitiesList");
            potentialSubActivities = initData.getStringArrayList("potentialSubActivitiesList");
            tasks = initData.getStringArrayList("TasksList");
            potentialTasks = initData.getStringArrayList("potentialTasksList");
            activityIdFromActivityDetails = initData.getString("activityId");
            activityScopeFromActivityDetails = initData.getString("activityScope");
            activityTitleFromActivityDetails = initData.getString("activityTitle");
            activityDescriptionFromActivityDetails = initData.getString("activityDescription");
        }
    }
    private void initFragmentUI(){
        if (initData != null){
            setActivityTitle.setText(activityTitleFromActivityDetails);
            setActivityScope.setText(activityScopeFromActivityDetails);
            setActivityDescription.setText(activityDescriptionFromActivityDetails);
            //for loop to add chip by chip to chipsgroup
            //up to now it is not convenient to get the item's displayName, use their id instead first...
            for (String member : assignee){
                UtilsMethods.addChips(getContext(), setActivityAssignee, chipUsersId, member, member, false);
            }
            for (String actId : activities){
                UtilsMethods.addChips(getContext(), setActivityToWhichActivity, chipsActivitiesId, actId, actId, false);
            }
            for (String subActId : subActivities){
                UtilsMethods.addChips(getContext(), setIncludeWhichActivities, chipsSubActivitiesId, subActId, subActId, false);
            }
            for (String taskId : tasks){
                UtilsMethods.addChips(getContext(), setIncludeWhichTasks, chipsSubTasksId, taskId, taskId, false);
            }
        }
    }

}