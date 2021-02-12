package com.example.projectmanagement;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectmanagement.Adaptors.ActivityAdaptor;
import com.example.projectmanagement.Adaptors.NotificationsAdaptor;
import com.example.projectmanagement.Adaptors.TaskAdaptor;
import com.example.projectmanagement.Adaptors.UsersAdaptor;
import com.example.projectmanagement.Interface.OnListItemSelectedListener;
import com.example.projectmanagement.Model.Activity;
import com.example.projectmanagement.Model.Task;
import com.example.projectmanagement.Model.Users;
import com.example.projectmanagement.Utils.DatePickerFragment;
import com.example.projectmanagement.Utils.TimePickerFragment;
import com.example.projectmanagement.Utils.UtilsMethods;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditTask#newInstance} factory method to
 * create an instance of this fragment.
 */

//Open when create a new task
public class EditTask extends Fragment implements OnListItemSelectedListener, View.OnClickListener {
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private CollectionReference usersRef, tasksRef, notificationsRef, activitiesRef;
    private Query mQueryUsers, mQueryActivities, mQueryTasks, mQueryNotifications;
    private DocumentReference userDoc;
    private ActivityAdaptor mAdaptorActivities;
    private TaskAdaptor mAdaptorTasks;
    private UsersAdaptor mAdaptorUsers;
    private String uid, userDisplayName, activityTitle, activityIdInit,
            taskTitle, taskId, taskScope, taskDescription, taskLevel, dateTimeText, status,
            notificationDescription, taskStartDate, taskDueDate;
    private Long startDateLong, dueDateLong;
    private static int LIMIT = 20;
    private int pageIndex;
    private int dialogFlag = 0;
    private boolean carryActivityDetails;

    private String DateAndTimePickerResultKey = "DateAndTimePickerResultKey";
    private String invitationNotificationDescriptionTemplateTask =
            "The task title is: %s, the task scope is: %s, the task description: %s";
    private int startYear, startMonth, startDay, startHour, startMin, endYear, endMonth, endDay, endHour, endMin;

    private ArrayList<String> activitiesListParticipated = new ArrayList<>();
    private ArrayList<String> assignee = new ArrayList<>();
    private ArrayList<String> subTasks = new ArrayList<>();
    private ArrayList<String> superiorTasks = new ArrayList<>();
    private ArrayList<String> activities = new ArrayList<>();
    private ArrayList<String> activityMembersAllForNotification = new ArrayList<>();
    private ArrayList<String> taskMembersAllForNotification = new ArrayList<>();
    private ArrayList<String> notificationsPeople = new ArrayList<>();
    private ArrayList<String> confirmationList = new ArrayList<>();
    private ArrayList<String> superiorTaskOfThisAll = new ArrayList<>();
    private HashMap<Integer, String> chipsAssigneeId = new HashMap<>();
    private HashMap<Integer, String> chipsSubTasksId = new HashMap<>();
    private HashMap<Integer, String> chipsSuperiorTasksId = new HashMap<>();
    private HashMap<Integer, String> chipsActivitiesId = new HashMap<>();
    private Bundle datePickerArg = new Bundle();
    private Bundle timePickerArg = new Bundle();
    private Bundle initDataBundle = new Bundle();
    private HashMap<String, ArrayList<String>> potentialAssigneeMap = new HashMap<>();
    private HashMap<String, ArrayList<String>> potentialSuperiorActivitiesMap = new HashMap<>();
    private HashMap<String, ArrayList<String>> potentialSuperiorTasksMap = new HashMap<>();
    private HashMap<String, ArrayList<String>> potentialSubTasksMap = new HashMap<>();
    private HashMap<String, ArrayList<String>> requestAssignee = new HashMap<>();
    private HashMap<String, ArrayList<String>> requestSuperiorActivitiesMap = new HashMap<>();
    private HashMap<String, ArrayList<String>> requestSuperiorTasksMap = new HashMap<>();
    private HashMap<String, ArrayList<String>> requestSubTasksMap = new HashMap<>();

    private ChipGroup addTaskAssigneeChipsGroup, taskToWhichActivityChipsGroup,
            taskSubtasksChipsGroup, taskSuperiorTasksChipsGroup;
    private EditText taskTitleEdit, taskScopeEdit, taskDescriptionEdit;
    private ImageButton startDateButton, startTimeButton, endDateButton, endTimeButton,
            addTaskAssigneeButton, taskToWhichActivityButton, taskSubtaskButton, taskSuperiortaskButton;
    private RadioGroup taskDifficultiesChoices;
    private RadioButton easyButton, middleButton, difficultButton;
    private TextView startDate, startTime, endDate, endTime;
    private Button taskConfirmButton;
    private RecyclerView recyclerActivities, recyclerUsers, recyclerTasks;

    private Dialog dialog;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EditTask() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditTask.
     */
    // TODO: Rename and change types and number of parameters
    public static EditTask newInstance(String param1, String param2) {
        EditTask fragment = new EditTask();
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
            if (getArguments() != null){
                initDataBundle = getArguments();
                taskId = initDataBundle.getString("taskId");
                taskTitle = initDataBundle.getString("taskTitle");
                taskScope = initDataBundle.getString("taskScope");
                taskDescription = initDataBundle.getString("taskDescription");
                startDateLong = initDataBundle.getLong("StartDate");
                dueDateLong = initDataBundle.getLong("DueDate");
                Calendar calendar = Calendar.getInstance();
                Date dateStart = new Date(startDateLong);
                calendar.setTime(dateStart);
                taskStartDate = String.format("%04d/%02d/%02d %02d:%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                        calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
                Calendar calendar2 = Calendar.getInstance();
                Date dateDue = new Date(dueDateLong);
                calendar2.setTime(dateDue);
                taskDueDate = String.format("%04d/%02d/%02d %02d:%02d", calendar2.get(Calendar.YEAR), calendar2.get(Calendar.MONTH) + 1,
                        calendar2.get(Calendar.DAY_OF_MONTH), calendar2.get(Calendar.HOUR_OF_DAY), calendar2.get(Calendar.MINUTE));
                assignee = initDataBundle.getStringArrayList("AssigneeTask");
                confirmationList = initDataBundle.getStringArrayList("ConfirmationList");
                subTasks = initDataBundle.getStringArrayList("subtasks");
                superiorTasks = initDataBundle.getStringArrayList("superiorTasks");
                activities = initDataBundle.getStringArrayList("activities");
                taskLevel = initDataBundle.getString("taskDifficulty");
                potentialAssigneeMap = (HashMap<String, ArrayList<String>>) initDataBundle.getSerializable("potentialAssigneeMap");
                potentialSuperiorActivitiesMap = (HashMap<String, ArrayList<String>>) initDataBundle.getSerializable("potentialSuperiorActivitiesMap");
                potentialSuperiorTasksMap = (HashMap<String, ArrayList<String>>) initDataBundle.getSerializable("potentialSuperiorTasksMap");
                potentialSubTasksMap = (HashMap<String, ArrayList<String>>) initDataBundle.getSerializable("potentialSubTasksMap");
                requestAssignee = (HashMap<String, ArrayList<String>>) initDataBundle.getSerializable("requestAssignee");
                requestSuperiorActivitiesMap = (HashMap<String, ArrayList<String>>) initDataBundle.getSerializable("requestSuperiorActivities");
                requestSuperiorTasksMap = (HashMap<String, ArrayList<String>>) initDataBundle.getSerializable("requestSuperiorTasks");
                requestSubTasksMap = (HashMap<String, ArrayList<String>>) initDataBundle.getSerializable("requestSubTasks");
            }
        }

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        uid = mFirebaseUser.getUid();
        iniFireStore();
        iniFragmentData();
        getParentFragmentManager().setFragmentResultListener(DateAndTimePickerResultKey, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                if (result.getString("resultType").equals("hourAndMin")) {
                    int hr = result.getInt("resultHour");
                    int min = result.getInt("resultMin");
                    int viewId = result.getInt("viewId");
                    processTime(hr, min, viewId);
                } else if (result.getString("resultType").equals("date")){
                    int year = result.getInt("resultYear");
                    int month = result.getInt("resultMonth");
                    int dayOfMonth = result.getInt("resultDayOfMonth");
                    int viewId = result.getInt("viewId");
                    processDate(year, month, dayOfMonth, viewId);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit_task, container, false);
        addTaskAssigneeButton = root.findViewById(R.id.addTaskAssigneeButton);
        taskToWhichActivityButton = root.findViewById(R.id.taskToWhichActivityButton);
        taskSubtaskButton = root.findViewById(R.id.taskSubtaskButton);
        taskSuperiortaskButton = root.findViewById(R.id.taskSuperiortaskButton);
        taskConfirmButton = root.findViewById(R.id.taskConfirmButton);
        addTaskAssigneeButton.setOnClickListener(this);
        taskToWhichActivityButton.setOnClickListener(this);
        taskSubtaskButton.setOnClickListener(this);
        taskSuperiortaskButton.setOnClickListener(this);
        taskConfirmButton.setOnClickListener(this);
        addTaskAssigneeChipsGroup = root.findViewById(R.id.addTaskAssigneeChipsGroup);
        taskSubtasksChipsGroup = root.findViewById(R.id.taskSubtasksChipsGroup);
        taskSuperiorTasksChipsGroup = root.findViewById(R.id.taskSuperiorTasksChipsGroup);
        taskToWhichActivityChipsGroup = root.findViewById(R.id.taskToWhichActivityChipsGroup);
        taskTitleEdit = root.findViewById(R.id.taskTitleEdit);
        taskScopeEdit = root.findViewById(R.id.taskScopeEdit);
        taskDescriptionEdit = root.findViewById(R.id.taskDescriptionEdit);
        taskDifficultiesChoices = root.findViewById(R.id.taskDifficultiesChoices);
        taskDifficultiesChoices.setOnCheckedChangeListener((group, checkedId) -> {
            switch(checkedId){
                case R.id.easyButton:
                    taskLevel = "Easy";
                    break;
                case R.id.middleButton:
                    Log.d("userDisplayName", userDisplayName);
                    taskLevel = "Middle";
                    break;
                case R.id.difficultButton:
                    taskLevel = "Difficult";
                    break;
            }
        });
        startDate = root.findViewById(R.id.startDateText);
        startTime = root.findViewById(R.id.startTimeText);
        endDate = root.findViewById(R.id.endDateText);
        endTime = root.findViewById(R.id.endTimeText);
        startDate.setOnClickListener(this);
        startTime.setOnClickListener(this);
        endDate.setOnClickListener(this);
        endTime.setOnClickListener(this);
        easyButton = root.findViewById(R.id.easyButton);
        middleButton = root.findViewById(R.id.middleButton);
        difficultButton = root.findViewById(R.id.difficultButton);
        iniFragmentUI();
        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.startDateText:
                showDatePickerDialog(v);
                break;
            case R.id.startTimeText:
                showTimePickerDialog(v);
                break;
            case R.id.endDateText:
                showDatePickerDialog(v);
                break;
            case R.id.endTimeText:
                showTimePickerDialog(v);
                break;
            case R.id.addTaskAssigneeButton:
                mAdaptorUsers = new UsersAdaptor(mQueryUsers, this);
                mAdaptorUsers.startListening();
                pageIndex = R.layout.item_users;
                dialog = UtilsMethods.createDialog(getContext(), pageIndex, mAdaptorUsers, R.layout.fragment_items_list, null, null, null);
                dialog.show();
                break;
            case R.id.taskToWhichActivityButton:
                pageIndex = R.layout.item_activities;
                mAdaptorActivities = new ActivityAdaptor(mQueryActivities, this);
                mAdaptorActivities.startListening();
                pageIndex = R.layout.item_activities;
                dialog = UtilsMethods.createDialog(getContext(), pageIndex, mAdaptorActivities, R.layout.fragment_items_list, null, null, null);
                dialog.show();
                break;
            case R.id.taskSubtaskButton:
                dialogFlag = R.id.taskSubtaskButton;
                //reset task query here
                pageIndex = R.layout.item_tasks;
                mAdaptorTasks = new TaskAdaptor(mQueryTasks, this);
                mAdaptorTasks.startListening();
                dialog = UtilsMethods.createDialog(getContext(), pageIndex, mAdaptorTasks, R.layout.fragment_items_list, null, null, null);
                dialog.show();
                break;
            case R.id.taskSuperiortaskButton:
                dialogFlag = R.id.taskSuperiortaskButton;
                //reset task query here
                pageIndex = R.layout.item_tasks;
                mAdaptorTasks = new TaskAdaptor(mQueryTasks, this);
                mAdaptorTasks.startListening();
                dialog = UtilsMethods.createDialog(getContext(), pageIndex, mAdaptorTasks, R.layout.fragment_items_list, null, null, null);
                dialog.show();
                break;
            case R.id.taskConfirmButton:
                String taskTitle = taskTitleEdit.getText().toString();
                String taskScope = taskScopeEdit.getText().toString();
                String taskDescription = taskDescriptionEdit.getText().toString();
                status = "Waiting for confirmation";

                Calendar calendar = Calendar.getInstance();
                Long timeCreated = calendar.getTimeInMillis();
                calendar.set(startYear, startMonth, startDay, startHour, startMin);
                Long startDateTime = calendar.getTimeInMillis();
                calendar.set(endYear, endMonth, endDay, endHour, endMin);
                Long endDateTime = calendar.getTimeInMillis();
                Log.d("dateTime", Long.toString(startDateTime));
                String level = taskLevel;

                for(String act : chipsActivitiesId.values()){
                    potentialSuperiorActivitiesMap.put(act, new ArrayList<>());
                }
                for(String person : chipsAssigneeId.values()){
                    potentialAssigneeMap.put(person, new ArrayList<>());
                }
                for(String subtask : chipsSubTasksId.values()){
                    potentialSubTasksMap.put(subtask, new ArrayList<>());
                }
                for(String superiortask : chipsSuperiorTasksId.values()){
                    potentialSuperiorTasksMap.put(superiortask, new ArrayList<>());
                }
                Task dummyTask = new Task();
                tasksRef.add(dummyTask).addOnSuccessListener(documentReference -> {
                    String taskId = documentReference.getId();
                    tasksRef.document(taskId).update("taskId", taskId);
                    if (mAdaptorUsers != null){
                        for (DocumentSnapshot docUser : mAdaptorUsers.retrieveSnapshot()){
                            for (String userid : potentialAssigneeMap.keySet()){
                                HashMap<String, Boolean> requestTaskOfThisUser;
                                String docUid = docUser.toObject(Users.class).getUid();
                                if (docUid.equals(userid)){
                                    requestTaskOfThisUser = docUser.toObject(Users.class).getRequestTasks();
                                    if (userid.equals(uid)){
                                        ArrayList<String> docTasks;
                                        docTasks = docUser.toObject(Users.class).getTasks();
                                        docTasks.add(taskId);
                                        if (potentialAssigneeMap.keySet().size() == 1){
                                            Log.d("size1", "triggered");
                                            potentialAssigneeMap.remove(userid);
                                            assignee.add(userid);
                                            usersRef.document(userid).update("tasks", docTasks);
                                            status = "Confirmed";
                                        } else if (potentialAssigneeMap.keySet().size() > 1){
                                            potentialAssigneeMap.replace(userid, new ArrayList<>(Arrays.asList(uid)));
                                            Log.d("size2", "triggered");
                                        }
                                    } else {
                                        Log.d("size3", "triggered");
                                        potentialAssigneeMap.replace(userid, new ArrayList<>());
                                        requestTaskOfThisUser.put(taskId, false);
                                        usersRef.document(userid).update("requestTasks", requestTaskOfThisUser);
                                    }
                                }
                            }
                        }
                    }
                    if (mAdaptorTasks != null){
                        for (DocumentSnapshot docTasks : mAdaptorTasks.retrieveSnapshot()){
                            String docTaskId = docTasks.toObject(Task.class).getTaskId();
                            //taskArrayAll.put(docTasks.toObject(Task.class).getTaskId(), docTasks.toObject(Task.class));
                            for (String superiorTask : potentialSuperiorTasksMap.keySet()){
                                HashMap<String, ArrayList<String>> requestSubTaskOfThisTask;
                                if (docTaskId != null){
                                    if (docTaskId.equals(superiorTask)){
                                        requestSubTaskOfThisTask = docTasks.toObject(Task.class).getRequestSubTasks();
                                        ArrayList<String> docAssignee;
                                        docAssignee = docTasks.toObject(Task.class).getAssignee();
                                        if (docAssignee.contains(uid) && docAssignee.size() > 1){
                                            requestSubTaskOfThisTask.put(taskId, new ArrayList<>(Arrays.asList(uid)));
                                            tasksRef.document(superiorTask).update("requestSubTasks", requestSubTaskOfThisTask);
                                        } else if (docAssignee.contains(uid) && docAssignee.size() == 1){
                                            ArrayList<String> docSubTask;
                                            docSubTask = docTasks.toObject(Task.class).getSubTasks();
                                            docSubTask.add(taskId);
                                            tasksRef.document(superiorTask).update("subTasks", docSubTask);
                                            potentialSuperiorTasksMap.remove(superiorTask);
                                            superiorTasks.add(superiorTask);
                                        } else if (!docAssignee.contains(uid)) {
                                            requestSubTaskOfThisTask.put(taskId, new ArrayList<>());
                                            tasksRef.document(superiorTask).update("requestSubTasks", requestSubTaskOfThisTask);
                                        }
                                    }
                                }
                            }
                            for (String subTask : potentialSubTasksMap.keySet()){
                                HashMap<String, ArrayList<String>> requestSuperiorTaskOfThisTask;
                                if (docTaskId != null){
                                    if (docTaskId.equals(subTask)){
                                        requestSuperiorTaskOfThisTask = docTasks.toObject(Task.class).getRequestSuperiorActivities();
                                        ArrayList<String> docAssignee;
                                        docAssignee = docTasks.toObject(Task.class).getAssignee();
                                        if (docAssignee.contains(uid) && docAssignee.size() > 1){
                                            requestSuperiorTaskOfThisTask.put(taskId, new ArrayList<>(Arrays.asList(uid)));
                                            tasksRef.document(subTask).update("requestSuperiorTasks", requestSuperiorTaskOfThisTask);
                                        } else if (docAssignee.contains(uid) && docAssignee.size() == 1){
                                            ArrayList<String> docSuperiorTasks;
                                            docSuperiorTasks = docTasks.toObject(Task.class).getSuperiorTasks();
                                            docSuperiorTasks.add(taskId);
                                            tasksRef.document(subTask).update("superiorTasks", docSuperiorTasks);
                                            potentialSubTasksMap.remove(subTask);
                                            subTasks.add(subTask);
                                        } else if (!docAssignee.contains(uid)) {
                                            requestSuperiorTaskOfThisTask.put(taskId, new ArrayList<>());
                                            tasksRef.document(subTask).update("requestSuperiorTasks", requestSuperiorTaskOfThisTask);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (mAdaptorActivities != null){
                        for (DocumentSnapshot docAct : mAdaptorActivities.retrieveSnapshot()){
                            for (String superiorAct : potentialSuperiorActivitiesMap.keySet()){
                                HashMap<String , ArrayList<String>> requestSubTasks;
                                String actId = docAct.toObject(Activity.class).getActivityId();
                                if (actId != null){
                                    if (actId.equals(superiorAct)){
                                        requestSubTasks = docAct.toObject(Activity.class).getRequestTasks();
                                        ArrayList<String> docAssignee;
                                        docAssignee = docAct.toObject(Activity.class).getAssignee();
                                        if (docAssignee.contains(uid) && docAssignee.size() > 1){
                                            requestSubTasks.put(taskId, new ArrayList<>(Arrays.asList(uid)));
                                            activitiesRef.document(superiorAct).update("requestTasks", requestSubTasks);
                                        } else if (docAssignee.contains(uid) && docAssignee.size() == 1){
                                            ArrayList<String> taskList;
                                            taskList = docAct.toObject(Activity.class).getTasksList();
                                            taskList.add(taskId);
                                            activitiesRef.document(superiorAct).update("tasksList", taskList);
                                            potentialSuperiorActivitiesMap.remove(superiorAct);
                                            activities.add(superiorAct);
                                        } else if (!docAssignee.contains(uid)){
                                            requestSubTasks.put(taskId, new ArrayList<>());
                                            activitiesRef.document(superiorAct).update("requestTasks", requestSubTasks);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Task newTask = new Task(taskId, taskTitle, status, timeCreated, activities, potentialSuperiorActivitiesMap,
                            assignee, potentialAssigneeMap, subTasks, potentialSubTasksMap, superiorTasks, potentialSuperiorTasksMap,
                            0, taskScope, taskDescription, taskLevel, startDateTime, endDateTime);
                    tasksRef.document(taskId).set(newTask);
                });



                /*if (potentialAssignee.contains(uid)){
                    assignee.add(uid);
                    potentialAssignee.remove(uid);
                }
                if (activityMembersAllForNotification.contains(uid)){
                    activityMembersAllForNotification.remove(uid);
                }
                if (taskMembersAllForNotification.contains(uid)){
                    taskMembersAllForNotification.remove(uid);
                }
                if ((potentialAssignee.size() > 0) || (activityMembersAllForNotification.size() > 0)
                        || (taskMembersAllForNotification.size() > 0)){
                    status = "Waiting for confirmation";
                } else {
                    status = "Ongoing...";
                }
                Set<String> set = new LinkedHashSet<>(potentialAssignee);
                set.addAll(activityMembersAllForNotification);
                set.addAll(taskMembersAllForNotification);
                notificationsPeople = new ArrayList<String>(set);
                Log.d("notificationsPeople", Integer.toString(notificationsPeople.size()));
                notificationDescription = String.format(invitationNotificationDescriptionTemplateTask,
                        taskTitle, taskScope, taskDescription);

                Task newTask = new Task(taskTitle, getString(R.string.status_waiting_for_confirmation), timeCreated, activities, potentialActivities, assignee, potentialAssignee,
                        subTasks, potentialSubTasks, superiorTasks, potentialSuperiorTasks, 0, taskScope, taskDescription, level,
                        startDateTime, endDateTime, notificationsPeople);

                tasksRef.add(newTask).addOnSuccessListener(documentReference -> {
                    taskId = documentReference.getId();
                    documentReference.update("taskId", taskId);
                    if (assignee.contains(uid)){
                        usersRef.document(uid).get().addOnSuccessListener(snapshot -> {
                            ArrayList<String> userTasks;
                            userTasks = snapshot.toObject(Users.class).getTasks();
                            userTasks.add(taskId);
                            usersRef.document(uid).update("tasks", userTasks);
                        });
                    }
                    UtilsMethods.createNotification(getString(R.string.Invitation_join_task), "", taskId,
                            notificationDescription, uid, timeCreated, notificationsPeople, notificationsRef);
                    updateActivitiesListAtFirstCreation(potentialActivities, documentReference, taskId);
                    updateSubTasksListAtFirstCreation(potentialSubTasks, documentReference, taskId);
                    updateSuperiorTasksListAtFirstCreation(potentialSuperiorTasks, documentReference, taskId);
                });*/

                break;
        }
    }

    @Override
    public void onDocumentSelected(DocumentSnapshot doc) {

    }

    @Override
    public void onMessageSelected(DocumentSnapshot message) {

    }

    @Override
    public void onTaskSelected(DocumentSnapshot taskDoc) {
        Task task = taskDoc.toObject(Task.class);
        String taskId = task.getTaskId();
        String taskTitle = task.getTaskTitle();
        switch(dialogFlag){
            case R.id.taskSubtaskButton:
                //add chips to subtask field
                UtilsMethods.addChips(getContext(), taskSubtasksChipsGroup, chipsSubTasksId, taskId, taskTitle, false);
                dialog.hide();
                break;
            case R.id.taskSuperiortaskButton:
                //add chips to superiortask field
                UtilsMethods.addChips(getContext(), taskSuperiorTasksChipsGroup, chipsSuperiorTasksId, taskId, taskTitle, false);
                dialog.hide();
                break;
        }
        tasksRef.document(taskId).get().addOnSuccessListener(snapshot -> {
            ArrayList<String> taskMembers;
            taskMembers = snapshot.toObject(Task.class).getAssignee();
            Set<String> set = new LinkedHashSet<>(taskMembersAllForNotification);
            set.addAll(taskMembers);
            taskMembersAllForNotification = new ArrayList<String>(set);
        });
        dialogFlag = 0;
        dialog.hide();
    }

    @Override
    public void onActivitySelected(DocumentSnapshot activity) {
        Activity act = activity.toObject(Activity.class);
        String activityTitle = act.getActivityTitle();
        String activityId = act.getActivityId();
        UtilsMethods.addChips(getContext(), taskToWhichActivityChipsGroup, chipsActivitiesId, activityId, activityTitle, false);
        if (activityId != null){
            activitiesRef.document(activityId).get().addOnSuccessListener(snapshot -> {
                ArrayList<String> activityMembers;
                activityMembers = snapshot.toObject(Activity.class).getAssignee();
                Log.d("MembersOnActivitySelected", activityMembers.toString());
                Set<String> set = new LinkedHashSet<>(activityMembersAllForNotification);
                set.addAll(activityMembers);
                activityMembersAllForNotification = new ArrayList<String>(set);
            });
        }
        dialog.hide();
    }

    @Override
    public void onUserSelected(DocumentSnapshot user) {
        Users users = user.toObject(Users.class);
        String userName = users.getDisplayName();
        String uid = users.getUid();
        UtilsMethods.addChips(getContext(), addTaskAssigneeChipsGroup, chipsAssigneeId, uid, userName, false);
        dialog.hide();
    }

    @Override
    public void onNotificationSelected(DocumentSnapshot notification, View v) {

    }
    private void iniFireStore(){
        mFirestore = FirebaseFirestore.getInstance();
        usersRef = mFirestore.collection("Users");
        userDoc = usersRef.document(uid);
        userDoc.addSnapshotListener((value, error) -> {
           Users users = value.toObject(Users.class);
           userDisplayName = users.getDisplayName();
        });
        activitiesRef = mFirestore.collection("Activities");
        tasksRef = mFirestore.collection("Tasks");
        notificationsRef = mFirestore.collection("Notifications");
        mQueryUsers = usersRef.orderBy("timeCreated", Query.Direction.DESCENDING)
                .limit(LIMIT);
        mQueryActivities = activitiesRef.orderBy("timeCreated", Query.Direction.DESCENDING)
                .limit(LIMIT);
        mQueryTasks = tasksRef.orderBy("timeCreated", Query.Direction.DESCENDING)
                .limit(LIMIT);
        mQueryNotifications = notificationsRef.orderBy("timeCreated", Query.Direction.DESCENDING)
                .limit(LIMIT);
        /*usersRef.document(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                userDisplayName = task.getResult().toObject(Users.class).getDisplayName();
                activitiesListParticipated = task.getResult().toObject(Users.class).getActivities();
                if (activitiesListParticipated.size() > 0){
                    mQueryTasks = mFirestore.collection("Tasks")
                            .orderBy("timeCreated").whereArrayContainsAny("activities", activitiesListParticipated)
                            .limit(LIMIT);
                }
            } else {
                Log.d("activitiesList", "failed to get the user's activities");
            }
        });*/
    }
    public void iniFragmentData(){
        if (getArguments() != null){
            activityIdInit = getArguments().getString("activityId");
            activityTitle = getArguments().getString("activityTitle");
            carryActivityDetails = getArguments().getBoolean("carryActivityDetails");
        }
        if (activityIdInit != null){
            activitiesRef.document(activityIdInit).get().addOnSuccessListener(snapshot -> {
                ArrayList<String> activityMembers;
                activityMembers = snapshot.toObject(Activity.class).getAssignee();
                Log.d("MembersOnActivitySelectedInit", activityMembers.toString());
                Set<String> set = new LinkedHashSet<>(activityMembersAllForNotification);
                set.addAll(activityMembers);
                activityMembersAllForNotification = new ArrayList<>(set);
            });
        }
    }
    public void iniFragmentUI(){
        if (activityIdInit != null && activityTitle != null){
            UtilsMethods.addChips(getContext(), taskToWhichActivityChipsGroup, chipsActivitiesId, activityIdInit, activityTitle, carryActivityDetails);
        }
        if (getArguments() != null){
            taskTitleEdit.setText(taskTitle);
            taskScopeEdit.setText(taskScope);
            taskDescriptionEdit.setText(taskDescription);
            startDate.setText(taskStartDate);
            endDate.setText(taskDueDate);
            if (taskLevel != null){
                if (taskLevel.equals("Easy")){
                    taskDifficultiesChoices.check(R.id.easyButton);
                } else if (taskLevel.equals("Middle")){
                    taskDifficultiesChoices.check(R.id.middleButton);
                } else if (taskLevel.equals("Difficult")){
                    taskDifficultiesChoices.check(R.id.difficultButton);
                }
            } else {Toast.makeText(getContext(), "Select a level!", Toast.LENGTH_SHORT).show();}
            for (String member : assignee){
                UtilsMethods.addChips(getContext(), addTaskAssigneeChipsGroup, chipsAssigneeId, member, member, false);
            }
            for (String actId : activities){
                UtilsMethods.addChips(getContext(), taskToWhichActivityChipsGroup, chipsActivitiesId, actId, actId, false);
            }
            for (String taskId : subTasks){
                UtilsMethods.addChips(getContext(), taskSubtasksChipsGroup, chipsSubTasksId, taskId, taskId, false);
            }
            for (String taskId : superiorTasks){
                UtilsMethods.addChips(getContext(), taskSuperiorTasksChipsGroup, chipsSuperiorTasksId, taskId, taskId, false);
            }
        }
    }
    public void showDatePickerDialog(View view){
        int viewId = view.getId();
        DialogFragment datePickerDialog = new DatePickerFragment();
        switch(viewId){
            case R.id.startDateText:
                datePickerArg.putInt("datePickerType", R.id.startDateText);
                break;
            case R.id.endDateText:
                datePickerArg.putInt("datePickerType", R.id.endDateText);
                break;
            default:
                Log.d("datePickerDialog", "Unknown source for the dialog");
        }
        datePickerDialog.setArguments(datePickerArg);
        datePickerDialog.show(getParentFragmentManager(), "datePicker");
    }
    public void processDate(int yr, int mon, int day, int viewId){
        dateTimeText = String.format("%04d/%02d/%02d", yr, mon, day);
        switch (viewId){
            case R.id.startDateText:
                startDate.setText(dateTimeText);
                break;
            case R.id.endDateText:
                endDate.setText(dateTimeText);
                break;
        }
        if (viewId == R.id.startDateText){
            startYear = yr;
            startMonth = mon;
            startDay = day;
        } else {
            endYear = yr;
            endMonth = mon;
            endDay = day;
        }
    }
    public void showTimePickerDialog(View view){
        int viewId = view.getId();
        DialogFragment timePickerDialog = new TimePickerFragment();
        switch(viewId) {
            case R.id.startTimeText:
                timePickerArg.putInt("timePickerType", R.id.startTimeText);
                break;
            case R.id.endTimeText:
                timePickerArg.putInt("timePickerType", R.id.endTimeText);
                break;
            default:
                Log.d("timePickerDialog", "Unknown source for the dialog");
        }
        timePickerDialog.setArguments(timePickerArg);
        timePickerDialog.show(getParentFragmentManager(), "timePicker");
    }
    public void processTime(int hr, int min, int viewId) {
        switch (viewId) {
            case R.id.startTimeText:
                if (min < 15 && min > 0) {
                    min = 0;
                } else if (min < 30 && min > 15) {
                    min = 15;
                } else if (min < 45 && min > 30) {
                    min = 30;
                } else if (min > 45) {
                    min = 45;
                }
                dateTimeText = String.format("%02d:%02d", hr, min);
                startTime.setText(dateTimeText);
                break;
            case R.id.endTimeText:
                if (min < 15 && min > 0) {
                    min = 15;
                } else if (min < 30 && min > 15) {
                    min = 30;
                } else if (min < 45 && min > 30) {
                    min = 45;
                } else if (min > 45) {
                    min = 0;
                    hr = hr + 1;
                }
                dateTimeText = String.format("%02d:%02d", hr, min);
                endTime.setText(dateTimeText);
                break;
        }
        if (viewId == R.id.startTimeText) {
            startHour = hr;
            startMin = min;
        } else {
            endHour = hr;
            endMin = min;
        }
        Toast.makeText(getContext(), "Notes: Resolution of minutes is 15 mins", Toast.LENGTH_SHORT).show();
    }
    public void updateActivitiesListAtFirstCreation(ArrayList<String> potentialActivities, DocumentReference docRef, String newTaskId){
        for (String actId : potentialActivities){
            activitiesRef.document(actId).get().addOnSuccessListener(snapshot -> {
                HashMap<String, ArrayList<String>> requestSubTaskInActivityTemp;
                requestSubTaskInActivityTemp = snapshot.toObject(Activity.class).getRequestTasks();
                if ((snapshot.toObject(Activity.class).getAssignee().contains(uid))
                        && (snapshot.toObject(Activity.class).getAssignee().size() == 1)){
                    docRef.get().addOnSuccessListener(snapshot1 -> {
                        ArrayList<String> activitiesListTemp;
                        HashMap<String, ArrayList<String>> potentialActivitiesListTemp;
                        activitiesListTemp = snapshot1.toObject(Task.class).getActivities();
                        potentialActivitiesListTemp = snapshot1.toObject(Task.class).getPotentialSuperiorActivitiesMap();
                        activitiesListTemp.add(actId);
                        potentialActivitiesListTemp.remove(actId);
                        docRef.update("activities", activitiesListTemp);
                        docRef.update("potentialSuperiorActivitiesMap", potentialActivitiesListTemp);

                        //add to the activities' taskList
                        String taskId = snapshot1.toObject(Task.class).getTaskId();
                        ArrayList<String> activityTaskList = snapshot.toObject(Activity.class).getTasksList();
                        activityTaskList.add(taskId);
                        activitiesRef.document(actId).update("tasksList", activityTaskList).addOnFailureListener(e -> {
                        });
                    });
                } else if ((snapshot.toObject(Activity.class).getAssignee().contains(uid))
                        && (snapshot.toObject(Activity.class).getAssignee().size() > 1)){
                    // go to the potentialActivity document to update requestTasks field
                    requestSubTaskInActivityTemp.put(newTaskId, new ArrayList<>(Arrays.asList(uid)));
                    activitiesRef.document(actId).update("requestTasks", requestSubTaskInActivityTemp);
                } else if (!snapshot.toObject(Activity.class).getAssignee().contains(uid)){
                    requestSubTaskInActivityTemp.put(newTaskId, new ArrayList<>());
                    activitiesRef.document(actId).update("requestTasks", requestSubTaskInActivityTemp);
                }
            });
        }
    }
    public void updateSubTasksListAtFirstCreation(ArrayList<String> potentialSubTasks, DocumentReference docRef, String newTaskId){
        for (String subtaskId : potentialSubTasks){
            tasksRef.document(subtaskId).get().addOnSuccessListener(snapshot -> {
                HashMap<String, ArrayList<String>> requestSuperiorTaskInTaskTemp;
                requestSuperiorTaskInTaskTemp = snapshot.toObject(Task.class).getRequestSuperiorTasks();
                if ((snapshot.toObject(Task.class).getAssignee().contains(uid))
                        && (snapshot.toObject(Task.class).getAssignee().size() == 1)){
                    docRef.get().addOnSuccessListener(snapshot1 -> {
                        ArrayList<String> subtaskListTemp;
                        HashMap<String, ArrayList<String>> potentialSubtaskListTemp;
                        subtaskListTemp = snapshot1.toObject(Task.class).getSubTasks();
                        potentialSubtaskListTemp = snapshot1.toObject(Task.class).getPotentialSubTasksMap();
                        subtaskListTemp.add(subtaskId);
                        potentialSubtaskListTemp.remove(subtaskId);
                        docRef.update("subTasks", subtaskListTemp);
                        docRef.update("potentialSubTasksMap", potentialSubtaskListTemp);

                        String taskId = snapshot1.toObject(Task.class).getTaskId();
                        ArrayList<String> superiorTasksOfSubTasks = snapshot.toObject(Task.class).getSuperiorTasks();
                        superiorTasksOfSubTasks.add(taskId);
                        tasksRef.document(subtaskId).update("superiorTasks", superiorTasksOfSubTasks);
                    });
                } else if ((snapshot.toObject(Task.class).getAssignee().contains(uid))
                        && (snapshot.toObject(Task.class).getAssignee().size() > 1)){
                    requestSuperiorTaskInTaskTemp.put(newTaskId, new ArrayList<>(Arrays.asList(uid)));
                    tasksRef.document(subtaskId).update("requestSuperiorTasks", requestSuperiorTaskInTaskTemp);
                } else if (!snapshot.toObject(Activity.class).getAssignee().contains(uid)){
                    requestSuperiorTaskInTaskTemp.put(newTaskId, new ArrayList<>());
                    tasksRef.document(subtaskId).update("requestSuperiorTasks", requestSuperiorTaskInTaskTemp);
                }
            });
        }
    }
    public void updateSuperiorTasksListAtFirstCreation(ArrayList<String> potentialSuperiorTasks, DocumentReference docRef, String newTaskId){
        for (String superiortaskId : potentialSuperiorTasks){
            tasksRef.document(superiortaskId).get().addOnSuccessListener(snapshot -> {
                HashMap<String, ArrayList<String>> requestSubTaskInTaskTemp;
                requestSubTaskInTaskTemp = snapshot.toObject(Task.class).getRequestSubTasks();
                if ((snapshot.toObject(Task.class).getAssignee().contains(uid))
                        && (snapshot.toObject(Task.class).getAssignee().size() == 1)){
                    docRef.get().addOnSuccessListener(snapshot1 -> {
                        ArrayList<String> superiorTasksListTemp;
                        HashMap<String, ArrayList<String>> potentialSuperiorTasksListTemp;
                        superiorTasksListTemp = snapshot1.toObject(Task.class).getSuperiorTasks();
                        potentialSuperiorTasksListTemp = snapshot1.toObject(Task.class).getPotentialSuperiorTasksMap();
                        superiorTasksListTemp.add(superiortaskId);
                        potentialSuperiorTasksListTemp.remove(superiortaskId);
                        docRef.update("superiorTasks", superiorTasksListTemp);
                        docRef.update("potentialSuperiorTasksMap", potentialSuperiorTasksListTemp);

                        String taskId = snapshot1.toObject(Task.class).getTaskId();
                        ArrayList<String> subTasksOfSuperiorTasks = snapshot.toObject(Task.class).getSubTasks();
                        subTasksOfSuperiorTasks.add(taskId);
                        tasksRef.document(superiortaskId).update("subTasks", subTasksOfSuperiorTasks);
                    });
                } else if ((snapshot.toObject(Task.class).getAssignee().contains(uid))
                        && (snapshot.toObject(Task.class).getAssignee().size() > 1)){
                    requestSubTaskInTaskTemp.put(newTaskId, new ArrayList<>(Arrays.asList(uid)));
                    tasksRef.document(superiortaskId).update("requestSubTasks", requestSubTaskInTaskTemp);
                } else if (!snapshot.toObject(Activity.class).getAssignee().contains(uid)){
                    requestSubTaskInTaskTemp.put(newTaskId, new ArrayList<>());
                    tasksRef.document(superiortaskId).update("requestSubTasks", requestSubTaskInTaskTemp);
                }
            });
        }
    }
    public void updateUserJobsOnHand(){
        //update Users object in firestore database the tasks on hands on i). this week, ii). today etc.
        //If the user is within assignee of the task, add the task directly to to-do list
    }
}
