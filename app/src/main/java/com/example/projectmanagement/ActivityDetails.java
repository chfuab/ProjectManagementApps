package com.example.projectmanagement;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectmanagement.Adaptors.ActivityAdaptor;
import com.example.projectmanagement.Adaptors.TaskAdaptor;
import com.example.projectmanagement.Interface.DialogDataListener;
import com.example.projectmanagement.Interface.OnListItemSelectedListener;
import com.example.projectmanagement.Model.Activity;
import com.example.projectmanagement.Model.Task;
import com.example.projectmanagement.Utils.UtilsMethods;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ActivityDetails#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActivityDetails extends Fragment implements View.OnClickListener, EventListener<QuerySnapshot>, DialogDataListener, OnListItemSelectedListener {
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private CollectionReference activitiesRef, notificationsRef, tasksRef, usersRef;
    private ListenerRegistration mRegistration;
    private DocumentReference mActivityDocRef;
    private Query queryTasksUnderThisActivity, queryActivityThisOnly, queryTasks, queryActivities;
    private ActivityAdaptor mAdaptorActivityThis, mAdaptorActivity;
    private TaskAdaptor mAdaptorTasks;
    private TextView activityDetailsTitle, activityDetailsScope,
            activityDetailsDescription, activityDetailsAssignee, progressReading;
    private FloatingActionButton fabTaskList, fabActivityConfirm, fabActivityEdit;
    private ChipGroup mchipsActivities, mchipsAssignee, mchipsConfirmation;
    private ProgressBar progressBar;
    private String title, scope, description, activityId, firebaseUid;
    private int progressReadingValue;
    private boolean carryActivityDetails = false;
    private ArrayList<String> assignee = new ArrayList<>();
    private ArrayList<String> potentialAssignee = new ArrayList<>();
    private ArrayList<String> activities = new ArrayList<>();
    private ArrayList<String> subtasks = new ArrayList<>();
    private ArrayList<String> confirmationList = new ArrayList<>();
    private ArrayList<Integer> progressReadingOfSubtasks = new ArrayList<>();
    private static final int LIMIT = 20;
    private String confirmationType;
    private ArrayList<String> requestSubActivitiesToBeConfirmed = new ArrayList<>();
    private ArrayList<String> requestTasksToBeConfirmed = new ArrayList<>();
    private HashMap<String, ArrayList<String>> potentialAssigneeMap = new HashMap<>();
    private HashMap<String, ArrayList<String>> potentialSubActivitiesMap = new HashMap<>();
    private HashMap<String, ArrayList<String>> potentialSuperiorActivitiesMap = new HashMap<>();
    private HashMap<String, ArrayList<String>> potentialTasksMap = new HashMap<>();
    private HashMap<String, ArrayList<String>> requestAssignee = new HashMap<>();
    private HashMap<String, ArrayList<String>> requestSubActivities = new HashMap<>();
    private HashMap<String, ArrayList<String>> requestSuperiorActivities = new HashMap<>();
    private HashMap<String, ArrayList<String>> requestTasks = new HashMap<>();
    private Dialog dialog;
    Bundle initData = new Bundle();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ActivityDetails() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ActivityDetails.
     */
    // TODO: Rename and change types and number of parameters
    public static ActivityDetails newInstance(String param1, String param2) {
        ActivityDetails fragment = new ActivityDetails();
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
            initData = getArguments();
        }
        title = getArguments().getString("activityTitle");
        scope = getArguments().getString("activityScope");
        description = getArguments().getString("activityDescription");
        assignee = getArguments().getStringArrayList("assignee");
        potentialAssignee = getArguments().getStringArrayList("potentialAssignee");
        activities = getArguments().getStringArrayList("activitiesList");
        subtasks = getArguments().getStringArrayList("TasksList");
        confirmationList = getArguments().getStringArrayList("confirmationList");
        activityId = getArguments().getString("activityId");

        progressReadingValue = getArguments().getInt("progressReading");
        potentialAssigneeMap = (HashMap<String, ArrayList<String>>) getArguments().getSerializable("potentialAssigneeMap");
        potentialSubActivitiesMap = (HashMap<String, ArrayList<String>>) getArguments().getSerializable("potentialSubActivitiesMap");
        potentialSuperiorActivitiesMap = (HashMap<String, ArrayList<String>>) getArguments().getSerializable("potentialSuperiorActivitiesMap");
        potentialTasksMap = (HashMap<String, ArrayList<String>>) getArguments().getSerializable("potentialTasksMap");
        requestAssignee = (HashMap<String, ArrayList<String>>) getArguments().getSerializable("requestAssignee");
        requestSubActivities = (HashMap<String, ArrayList<String>>) getArguments().getSerializable("requestSubActivities");
        requestSuperiorActivities = (HashMap<String, ArrayList<String>>) getArguments().getSerializable("requestSuperiorActivities");
        requestTasks = (HashMap<String, ArrayList<String>>) getArguments().getSerializable("requestTasks");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        firebaseUid = mFirebaseUser.getUid();
        iniFireStore();
        if (potentialAssignee.contains(firebaseUid)){
            confirmationType = "Assignee";
        }

        //ArrayList<String> keySetOfRequestSubActivities = new ArrayList<>(requestSubActivities.keySet());
        int count = 0;
        ArrayList<String> keySetArrayOfRequestSubActivities = new ArrayList<>(requestSubActivities.keySet());
        for (ArrayList<String> arrayList : requestSubActivities.values()){
            if (arrayList.contains(firebaseUid)){
                String requestSubActivityAtCount = keySetArrayOfRequestSubActivities.get(count);
                requestSubActivitiesToBeConfirmed.add(requestSubActivityAtCount);
            }
            count = count + 1;
        }
        count = 0;
        ArrayList<String> keySetArrayOfRequestSubTasks = new ArrayList<>(requestTasks.keySet());
        for (ArrayList<String> arrayList : requestTasks.values()){
            if (arrayList.contains(firebaseUid)){
                String requestSubTaskAtCount = keySetArrayOfRequestSubTasks.get(count);
                requestTasksToBeConfirmed.add(requestSubTaskAtCount);
            }
            count = count + 1;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_activity_details, container, false);
        activityDetailsTitle = root.findViewById(R.id.activityDetailsTitle);
        activityDetailsScope = root.findViewById(R.id.activityDetailsScope);
        mchipsActivities = root.findViewById(R.id.activityChipsGroup);
        mchipsActivities.setSingleLine(false);
        mchipsAssignee = root.findViewById(R.id.assigneeChipsGroup);
        mchipsAssignee.setSingleLine(false);
        mchipsConfirmation = root.findViewById(R.id.confirmationListChipsGroup);
        mchipsConfirmation.setSingleLine(false);
        UtilsMethods.populateChipsGroup(getContext(), mchipsActivities, activities);
        UtilsMethods.populateChipsGroup(getContext(), mchipsAssignee, assignee);
        UtilsMethods.populateChipsGroup(getContext(), mchipsConfirmation, confirmationList);
        progressReading = root.findViewById(R.id.progressReading);
        progressReading.setText(Integer.toString(progressReadingValue) + "%");
        progressBar = root.findViewById(R.id.progressBar);
        progressBar.setProgress(progressReadingValue);
        activityDetailsDescription = root.findViewById(R.id.activityDetailsDescription);
        activityDetailsTitle.setText(title);
        activityDetailsScope.setText(scope);
        activityDetailsDescription.setText(description);
        fabTaskList = root.findViewById(R.id.fabTaskList);
        fabTaskList.setOnClickListener(this);
        fabActivityConfirm = root.findViewById(R.id.fabActivityConfirm);
        if (!assignee.contains(firebaseUid) && !potentialAssigneeMap.keySet().contains(firebaseUid)){
            fabActivityConfirm.setVisibility(View.GONE);
        }
        fabActivityConfirm.setOnClickListener(this);
        fabActivityEdit = root.findViewById(R.id.fabActivityEdit);
        if (assignee.contains(firebaseUid)){
            fabActivityEdit.setVisibility(View.VISIBLE);
            fabActivityEdit.setOnClickListener(this);
        }
        return root;
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fabTaskList:
                Bundle editTaskBundle = new Bundle();
                editTaskBundle.putString("activityId", activityId);
                editTaskBundle.putString("activityTitle", title);
                carryActivityDetails = true;
                editTaskBundle.putBoolean("carryActivityDetails", carryActivityDetails);
                Navigation.findNavController(getActivity(), R.id.fabTaskList).navigate(R.id.editTask, editTaskBundle);
                break;
            case R.id.fabActivityConfirm:
                //fabActivityConfirm.setVisibility(View.GONE);
                initData.putString("firebaseUid", firebaseUid);
                dialog = UtilsMethods.createDialog(getContext(), 0, null, R.layout.fragment_confirmation_dialog_activity, null, this, initData);
                dialog.show();
                break;
            case R.id.fabActivityEdit:
                // put all data in bundle then open editActivity page.
                carryActivityDetails = true;
                initData.putBoolean("carryActivityDetails", carryActivityDetails);
                Navigation.findNavController(getActivity(), R.id.fabActivityEdit).navigate(R.id.editActivity, initData);
                break;
        }
    }

    @Override
    public void onStop() {
        if (mRegistration != null) {
            mRegistration.remove();
            mRegistration = null;
        }
        mAdaptorActivityThis.stopListening();
        mAdaptorActivity.stopListening();
        mAdaptorTasks.stopListening();
        super.onStop();
    }
    @Override
    public void onStart() {
        super.onStart();
        mAdaptorActivityThis.startListening();
        mAdaptorActivity.startListening();
        mAdaptorTasks.startListening();
    }

    private void iniFireStore(){
        mFirestore = FirebaseFirestore.getInstance();
        activitiesRef = mFirestore.collection("Activities");
        notificationsRef = mFirestore.collection("Notifications");
        tasksRef = mFirestore.collection("Tasks");
        usersRef = mFirestore.collection("Users");
        mActivityDocRef = activitiesRef.document(activityId);
        queryActivities = activitiesRef.orderBy("timeCreated", Query.Direction.DESCENDING)
                .limit(LIMIT);
        queryActivityThisOnly = activitiesRef.whereEqualTo("activityId", activityId);
        queryTasks = tasksRef.orderBy("timeCreated", Query.Direction.DESCENDING)
                .limit(LIMIT);
        mAdaptorActivityThis = new ActivityAdaptor(queryActivityThisOnly, this);
        mAdaptorActivity = new ActivityAdaptor(queryActivities, this);
        mAdaptorTasks = new TaskAdaptor(queryTasks, this);
        if (subtasks.size() > 0){
            queryTasksUnderThisActivity = tasksRef
                    .whereIn("taskId", subtasks)
                    .limit(LIMIT);
            mRegistration = queryTasksUnderThisActivity.addSnapshotListener(this);
            Log.d("QuerySubtasksOfThisActivity", subtasks.toString());
        } else {
            Log.d("QuerySubtasksOfThisActivity", "No subtasks");
        }
    }
    @Override
    public void getDialogData(Bundle bundle) {
        HashMap<String, ArrayList<String>> potentialAssigneeMapActivity;
        HashMap<String, ArrayList<String>> potentialSuperiorActivitiesMapActivity;
        HashMap<String, ArrayList<String>> potentialSubActivitiesActivity;
        HashMap<String, ArrayList<String>> potentialTasksMapActivity;
        HashMap<String, ArrayList<String>> requestAssigneeActivity;
        HashMap<String, ArrayList<String>> requestSuperiorActivitiesMapActivity;
        HashMap<String, ArrayList<String>> requestSubActivitiesActivity;
        HashMap<String, ArrayList<String>> requestTasksMapActivity;
        potentialAssigneeMapActivity = (HashMap<String, ArrayList<String>>) bundle.getSerializable("potentialAssigneeMap");
        potentialSuperiorActivitiesMapActivity = (HashMap<String, ArrayList<String>>) bundle.getSerializable("potentialSuperiorActivitiesMap");
        potentialSubActivitiesActivity = (HashMap<String, ArrayList<String>>) bundle.getSerializable("potentialSubActivitiesMap");
        potentialTasksMapActivity = (HashMap<String, ArrayList<String>>) bundle.getSerializable("potentialTasksMap");
        requestAssigneeActivity = (HashMap<String, ArrayList<String>>) bundle.getSerializable("requestAssignee");
        requestSuperiorActivitiesMapActivity = (HashMap<String, ArrayList<String>>) bundle.getSerializable("requestSuperiorActivities");
        requestSubActivitiesActivity = (HashMap<String, ArrayList<String>>) bundle.getSerializable("requestSubActivities");
        requestTasksMapActivity = (HashMap<String, ArrayList<String>>) bundle.getSerializable("requestTasks");
        activitiesRef.document(activityId).update("potentialAssigneeMap", potentialAssigneeMapActivity);
        activitiesRef.document(activityId).update("potentialSubActivitiesMap", potentialSubActivitiesActivity);
        activitiesRef.document(activityId).update("potentialSuperiorActivitiesMap", potentialSuperiorActivitiesMapActivity);
        activitiesRef.document(activityId).update("potentialTasksMap", potentialTasksMapActivity);
        activitiesRef.document(activityId).update("requestAssignee", requestAssigneeActivity);
        activitiesRef.document(activityId).update("requestSubActivities", requestSubActivitiesActivity);
        activitiesRef.document(activityId).update("requestSuperiorActivities", requestSuperiorActivitiesMapActivity);
        activitiesRef.document(activityId).update("requestTasks", requestTasksMapActivity);
        ArrayList<String> assignee = new ArrayList<>();
        ArrayList<String> subActivities = new ArrayList<>();
        ArrayList<String> superiorActivities = new ArrayList<>();
        ArrayList<String> taskList = new ArrayList<>();
        for (DocumentSnapshot doc : mAdaptorActivityThis.retrieveSnapshot()){
            Activity act = doc.toObject(Activity.class);
            taskList = act.getTasksList();
            assignee = act.getAssignee();
        }
        //checkVotingAndUpdate(potentialSuperiorTasksMap, "potentialSuperiorTasksMap", assignee, mAdaptorTasks, null,
        //                superiortasks, "superiorTasks", tasksRef, null, taskId, "requestSubTasks", "subTasks");
        //
        checkVotingAndUpdate(requestTasksMapActivity, "requestTasks", assignee, mAdaptorTasks, mAdaptorActivity,
                taskList, "tasksList", tasksRef, activitiesRef, activityId,
                "potentialSuperiorActivitiesMap", "activities");

    }
    private void checkVotingAndUpdate(HashMap<String, ArrayList<String>> potentialOrRequestSubOrSuperiorTasksOrActivitiesMap, String potentialSubOrSuperiorTasksOrActivitiesMapStringThisTask,
                                      ArrayList<String> assignee, TaskAdaptor mAdaptorTasks, ActivityAdaptor mAdaptorActivity,
                                      ArrayList<String> subOrSuperiorTasksOrActivities, String subOrSuperiorTasksOrActivitiesStringThisTask, CollectionReference tasksRef, CollectionReference activitiesRef,
                                      String activityId, String requestOrPotentialSubOrSuperiorTasksOrActivitiesStringTask, String subOrSuperiorTasksStringRequestOrPotentialTaskOrActivity){

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
                            HashMap<String, ArrayList<String>> requestActs = activity.getRequestSubActivities();
                            ArrayList<String> subActivitiesList = activity.getSubActivitiesList();
                            ArrayList<String> requestActsConfirmList = requestActs.get(activityId);
                            ArrayList<String> beingRequestPeople = activity.getAssignee();
                            Set<String> beingRequestPeopleSet = new HashSet<>(beingRequestPeople);
                            Set<String> requestActsConfirmationListSet = new HashSet<>(requestActsConfirmList);
                            if (beingRequestPeopleSet.equals(requestActsConfirmationListSet)){
                                potentialOrRequestSubOrSuperiorTasksOrActivitiesMap.remove(confirmationItemActivity);
                                subOrSuperiorTasksOrActivities.add(confirmationItemActivity);
                                requestActs.remove(activityId);
                                subActivitiesList.add(activityId);
                                activitiesRef.document(activityId).update(potentialSubOrSuperiorTasksOrActivitiesMapStringThisTask, potentialOrRequestSubOrSuperiorTasksOrActivitiesMap);
                                activitiesRef.document(activityId).update(subOrSuperiorTasksOrActivitiesStringThisTask, subOrSuperiorTasksOrActivities);
                                activitiesRef.document(confirmationItemActivity).update(requestOrPotentialSubOrSuperiorTasksOrActivitiesStringTask, requestActs);
                                activitiesRef.document(confirmationItemActivity).update(subOrSuperiorTasksStringRequestOrPotentialTaskOrActivity, subActivitiesList);
                            }
                        } else if (potentialSubOrSuperiorTasksOrActivitiesMapStringThisTask.equals("potentialSubActivitiesMap")){
                            HashMap<String, ArrayList<String>> requestActs = activity.getRequestSuperiorActivities();
                            ArrayList<String> activitiesList = activity.getActivitiesList();
                            ArrayList<String> requestActsConfirmList = requestActs.get(activityId);
                            ArrayList<String> beingRequestPeople = activity.getAssignee();
                            Set<String> beingRequestPeopleSet = new HashSet<>(beingRequestPeople);
                            Set<String> requestActsConfirmationListSet = new HashSet<>(requestActsConfirmList);
                            if (beingRequestPeopleSet.equals(requestActsConfirmationListSet)){
                                potentialOrRequestSubOrSuperiorTasksOrActivitiesMap.remove(confirmationItemActivity);
                                subOrSuperiorTasksOrActivities.add(confirmationItemActivity);
                                requestActs.remove(activityId);
                                activitiesList.add(activityId);
                                activitiesRef.document(activityId).update(potentialSubOrSuperiorTasksOrActivitiesMapStringThisTask, potentialOrRequestSubOrSuperiorTasksOrActivitiesMap);
                                activitiesRef.document(activityId).update(subOrSuperiorTasksOrActivitiesStringThisTask, subOrSuperiorTasksOrActivities);
                                activitiesRef.document(confirmationItemActivity).update(requestOrPotentialSubOrSuperiorTasksOrActivitiesStringTask, requestActs);
                                activitiesRef.document(confirmationItemActivity).update(subOrSuperiorTasksStringRequestOrPotentialTaskOrActivity, activitiesList);
                            }
                        } else if (potentialSubOrSuperiorTasksOrActivitiesMapStringThisTask.equals("requestSuperiorActivities")){
                            HashMap<String, ArrayList<String>> potentialActs = activity.getPotentialSubActivitiesMap();
                            ArrayList<String> subActivitiesList = activity.getSubActivitiesList();
                            ArrayList<String> requestActsConfirmList = potentialActs.get(activityId);
                            ArrayList<String> beingRequestPeople = activity.getAssignee();
                            Set<String> beingRequestPeopleSet = new HashSet<>(beingRequestPeople);
                            Set<String> requestActsConfirmationListSet = new HashSet<>(requestActsConfirmList);
                            if (beingRequestPeopleSet.equals(requestActsConfirmationListSet)){
                                potentialOrRequestSubOrSuperiorTasksOrActivitiesMap.remove(confirmationItemActivity);
                                subOrSuperiorTasksOrActivities.add(confirmationItemActivity);
                                potentialActs.remove(activityId);
                                subActivitiesList.add(activityId);
                                activitiesRef.document(activityId).update(potentialSubOrSuperiorTasksOrActivitiesMapStringThisTask, potentialOrRequestSubOrSuperiorTasksOrActivitiesMap);
                                activitiesRef.document(activityId).update(subOrSuperiorTasksOrActivitiesStringThisTask, subOrSuperiorTasksOrActivities);
                                activitiesRef.document(confirmationItemActivity).update(requestOrPotentialSubOrSuperiorTasksOrActivitiesStringTask, potentialActs);
                                activitiesRef.document(confirmationItemActivity).update(subOrSuperiorTasksStringRequestOrPotentialTaskOrActivity, subActivitiesList);
                            }
                        } else if (potentialSubOrSuperiorTasksOrActivitiesMapStringThisTask.equals("requestSubActivities")){
                            HashMap<String, ArrayList<String>> potentialActs = activity.getPotentialSuperiorActivitiesMap();
                            ArrayList<String> activitiesList = activity.getActivitiesList();
                            ArrayList<String> requestActsConfirmList = potentialActs.get(activityId);
                            ArrayList<String> beingRequestPeople = activity.getAssignee();
                            Set<String> beingRequestPeopleSet = new HashSet<>(beingRequestPeople);
                            Set<String> requestActsConfirmationListSet = new HashSet<>(requestActsConfirmList);
                            if (beingRequestPeopleSet.equals(requestActsConfirmationListSet)){
                                potentialOrRequestSubOrSuperiorTasksOrActivitiesMap.remove(confirmationItemActivity);
                                subOrSuperiorTasksOrActivities.add(confirmationItemActivity);
                                potentialActs.remove(activityId);
                                activitiesList.add(activityId);
                                activitiesRef.document(activityId).update(potentialSubOrSuperiorTasksOrActivitiesMapStringThisTask, potentialOrRequestSubOrSuperiorTasksOrActivitiesMap);
                                activitiesRef.document(activityId).update(subOrSuperiorTasksOrActivitiesStringThisTask, subOrSuperiorTasksOrActivities);
                                activitiesRef.document(confirmationItemActivity).update(requestOrPotentialSubOrSuperiorTasksOrActivitiesStringTask, potentialActs);
                                activitiesRef.document(confirmationItemActivity).update(subOrSuperiorTasksStringRequestOrPotentialTaskOrActivity, activitiesList);
                            }
                        }
                    }
                }
            }
        }
        for (String confirmationItemTask : potentialOrRequestSubOrSuperiorTasksOrActivitiesMap.keySet()){
            ArrayList<String> confirmationTasksList = potentialOrRequestSubOrSuperiorTasksOrActivitiesMap.get(confirmationItemTask);
            Set<String> confirmationTaskSet = new HashSet<>(confirmationTasksList);
            Set<String> assigneeSet = new HashSet<>(assignee);
            if (assigneeSet.equals(confirmationTaskSet)){
                for (DocumentSnapshot taskDoc : mAdaptorTasks.retrieveSnapshot()){
                    Task task = taskDoc.toObject(Task.class);
                    String taskId1 = task.getTaskId();
                    if (taskId1.equals(confirmationItemTask)){
                        if (potentialSubOrSuperiorTasksOrActivitiesMapStringThisTask.equals("potentialTasksMap")){
                            HashMap<String, ArrayList<String>> requestActs = task.getRequestSuperiorActivities();
                            ArrayList<String> superiorAct = task.getActivities();
                            ArrayList<String> requestItemConfirmListActs = requestActs.get(activityId);
                            ArrayList<String> beingRequestPeople = task.getAssignee();
                            Set<String> beingRequestPeopleSet = new HashSet<>(beingRequestPeople);
                            Set<String> requestItemConfirmationListActsSet = new HashSet<>(requestItemConfirmListActs);
                            if (beingRequestPeopleSet.equals(requestItemConfirmationListActsSet)){
                                potentialOrRequestSubOrSuperiorTasksOrActivitiesMap.remove(confirmationItemTask);
                                subOrSuperiorTasksOrActivities.add(confirmationItemTask);
                                requestActs.remove(activityId);
                                superiorAct.add(activityId);
                                activitiesRef.document(activityId).update(potentialSubOrSuperiorTasksOrActivitiesMapStringThisTask, potentialOrRequestSubOrSuperiorTasksOrActivitiesMap);
                                activitiesRef.document(activityId).update(subOrSuperiorTasksOrActivitiesStringThisTask, subOrSuperiorTasksOrActivities);
                                tasksRef.document(confirmationItemTask).update(requestOrPotentialSubOrSuperiorTasksOrActivitiesStringTask, requestActs);
                                tasksRef.document(confirmationItemTask).update(subOrSuperiorTasksStringRequestOrPotentialTaskOrActivity, superiorAct);
                            }
                        } else if (potentialSubOrSuperiorTasksOrActivitiesMapStringThisTask.equals("requestTasks")){
                            HashMap<String, ArrayList<String>> requestTasks = task.getPotentialSuperiorActivitiesMap();
                            ArrayList<String> superiorAct = task.getActivities();
                            ArrayList<String> requestItemConfirmListTasks = requestTasks.get(activityId);
                            ArrayList<String> beingRequestPeople = task.getAssignee();
                            Set<String> beingRequestPeopleSet = new HashSet<>(beingRequestPeople);
                            Set<String> requestItemConfirmationListActsSet = new HashSet<>(requestItemConfirmListTasks);
                            if (beingRequestPeopleSet.equals(requestItemConfirmationListActsSet)){
                                potentialOrRequestSubOrSuperiorTasksOrActivitiesMap.remove(confirmationItemTask);
                                subOrSuperiorTasksOrActivities.add(confirmationItemTask);
                                requestTasks.remove(activityId);
                                superiorAct.add(activityId);
                                activitiesRef.document(activityId).update(potentialSubOrSuperiorTasksOrActivitiesMapStringThisTask, potentialOrRequestSubOrSuperiorTasksOrActivitiesMap);
                                activitiesRef.document(activityId).update(subOrSuperiorTasksOrActivitiesStringThisTask, subOrSuperiorTasksOrActivities);
                                tasksRef.document(confirmationItemTask).update(requestOrPotentialSubOrSuperiorTasksOrActivitiesStringTask, requestTasks);
                                tasksRef.document(confirmationItemTask).update(subOrSuperiorTasksStringRequestOrPotentialTaskOrActivity, superiorAct);
                            }
                        }
                    }
                }
            }
        }
    }
    @Override
    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

    }

    @Override
    public void onDocumentSelected(DocumentSnapshot doc) {

    }

    @Override
    public void onMessageSelected(DocumentSnapshot message) {

    }

    @Override
    public void onTaskSelected(DocumentSnapshot event) {

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
}