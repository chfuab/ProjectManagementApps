package com.example.projectmanagement.Utils;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectmanagement.Adaptors.FirestoreAdaptor;
import com.example.projectmanagement.Interface.DialogDataListener;
import com.example.projectmanagement.Interface.OnListItemSelectedListener;
import com.example.projectmanagement.Model.Activity;
import com.example.projectmanagement.Model.NotificationsMessage;
import com.example.projectmanagement.Model.Task;
import com.example.projectmanagement.Model.Users;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.example.projectmanagement.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class UtilsMethods {
    public UtilsMethods(){}
    public static void addChips(Context context, ChipGroup chipgroup, HashMap<Integer, String> hashmap, String contentId,
                                String contentDisplay, Boolean carryPreviousFragmentDetails){
        if (hashmap != null){
            if (!hashmap.values().contains(contentId)){
                Chip chip = new Chip(context);
                chip.setText(contentDisplay);
                if (!carryPreviousFragmentDetails){
                    chip.setChipIconResource(R.drawable.ic_clear);
                    chip.setOnClickListener(v -> {
                        chipgroup.removeView(v);
                        hashmap.remove(v.getId());
                    });
                }
                chipgroup.setSingleLine(false);
                chipgroup.addView(chip);
                hashmap.put(chip.getId(), contentId);
            }
        }
    }
    public static void addChipsConfirm(Context context, ChipGroup chipgroup, HashMap<String, Boolean> statusMap, HashMap<Integer, String> chipIdMap, String contentId,
                                String contentDisplay){
        Log.d("FuckYourMother", "GetFuckedYouAsshole");
        Chip chip = new Chip(context);
        chip.setText(contentDisplay);
        chip.setChipBackgroundColor(AppCompatResources.getColorStateList(context, R.color.green));
        chip.setOnClickListener(v -> {
            String textId = chipIdMap.get(v.getId());
            Boolean statusBeforeClick = statusMap.get(textId);
            if (statusBeforeClick){
                statusMap.replace(textId, false);
                chip.setChipBackgroundColor(AppCompatResources.getColorStateList(context, R.color.grey));
            } else {
                statusMap.replace(textId, true);
                chip.setChipBackgroundColor(AppCompatResources.getColorStateList(context, R.color.green));
            }
        });
        chipgroup.setSingleLine(false);
        chipgroup.addView(chip);
        statusMap.put(contentDisplay, true);
        chipIdMap.put(chip.getId(), contentDisplay);
    }
    public static Dialog createDialog(Context context, int pageIndex, FirestoreAdaptor adaptor, int layout,
                                      View.OnClickListener listener, DialogDataListener dialogListener, Bundle dialogDataIn){
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        switch (layout){
            case R.layout.fragment_items_list:
                dialog.setContentView(R.layout.fragment_items_list);
                dialog.setCancelable(true);
                RecyclerView mRecyclerViewActivity = dialog.findViewById(R.id.recycler_activities);
                RecyclerView mRecyclerViewNotification = dialog.findViewById(R.id.recycler_notificationsMessage);
                RecyclerView mRecyclerViewTask = dialog.findViewById(R.id.recycler_tasks);
                RecyclerView mRecyclerViewUser = dialog.findViewById(R.id.recycler_user);
                FloatingActionButton fabActivities = dialog.findViewById(R.id.fabItemListActivities);
                FloatingActionButton fabNotifications = dialog.findViewById(R.id.fabItemListNotifications);
                FloatingActionButton fabTasks = dialog.findViewById(R.id.fabItemListTasks);
                FloatingActionButton fabUsers = dialog.findViewById(R.id.fabItemListUsers);
                fabActivities.setVisibility(View.GONE);
                fabNotifications.setVisibility(View.GONE);
                fabTasks.setVisibility(View.GONE);
                fabUsers.setVisibility(View.GONE);
                switch(pageIndex){
                    case R.layout.item_activities:
                        dialog.setTitle("Please choose an activity");
                        mRecyclerViewActivity.setVisibility(View.VISIBLE);
                        mRecyclerViewActivity.setLayoutManager(new LinearLayoutManager(context));
                        mRecyclerViewActivity.setAdapter(adaptor);
                        mRecyclerViewNotification.setVisibility(View.GONE);
                        mRecyclerViewTask.setVisibility(View.GONE);
                        mRecyclerViewUser.setVisibility(View.GONE);
                        break;
                    case R.layout.item_notifications_message:
                        dialog.setTitle("Please choose a notification message");
                        mRecyclerViewActivity.setVisibility(View.GONE);
                        mRecyclerViewNotification.setVisibility(View.VISIBLE);
                        mRecyclerViewNotification.setLayoutManager(new LinearLayoutManager(context));
                        mRecyclerViewNotification.setAdapter(adaptor);
                        mRecyclerViewTask.setVisibility(View.GONE);
                        mRecyclerViewUser.setVisibility(View.GONE);
                        break;
                    case R.layout.item_tasks:
                        dialog.setTitle("Please choose a task");
                        mRecyclerViewActivity.setVisibility(View.GONE);
                        mRecyclerViewNotification.setVisibility(View.GONE);
                        mRecyclerViewTask.setVisibility(View.VISIBLE);
                        mRecyclerViewTask.setLayoutManager(new LinearLayoutManager(context));
                        mRecyclerViewTask.setAdapter(adaptor);
                        mRecyclerViewUser.setVisibility(View.GONE);
                        break;
                    case R.layout.item_users:
                        dialog.setTitle("Please choose an user");
                        mRecyclerViewActivity.setVisibility(View.GONE);
                        mRecyclerViewNotification.setVisibility(View.GONE);
                        mRecyclerViewTask.setVisibility(View.GONE);
                        mRecyclerViewUser.setVisibility(View.VISIBLE);
                        mRecyclerViewUser.setLayoutManager(new LinearLayoutManager(context));
                        mRecyclerViewUser.setAdapter(adaptor);
                        break;
                }
                break;
            case R.layout.fragment_confirmation_dialog:
                HashMap<String, ArrayList<String>> potentialAssigneeMap = (HashMap<String, ArrayList<String>>) dialogDataIn.getSerializable("potentialAssigneeMap");
                HashMap<String, ArrayList<String>> potentialSuperiorActivitiesMap = (HashMap<String, ArrayList<String>>) dialogDataIn.getSerializable("potentialSuperiorActivitiesMap");
                HashMap<String, ArrayList<String>> potentialSuperiorTasksMap = (HashMap<String, ArrayList<String>>) dialogDataIn.getSerializable("potentialSuperiorTasksMap");
                HashMap<String, ArrayList<String>> potentialSubTasksMap = (HashMap<String, ArrayList<String>>) dialogDataIn.getSerializable("potentialSubTasksMap");
                HashMap<String, ArrayList<String>> requestAssignee = (HashMap<String, ArrayList<String>>) dialogDataIn.getSerializable("requestAssignee");
                HashMap<String, ArrayList<String>> requestSuperiorActivitiesMap = (HashMap<String, ArrayList<String>>) dialogDataIn.getSerializable("requestSuperiorActivities");
                HashMap<String, ArrayList<String>> requestSuperiorTasksMap = (HashMap<String, ArrayList<String>>) dialogDataIn.getSerializable("requestSuperiorTasks");
                HashMap<String, ArrayList<String>> requestSubTasksMap = (HashMap<String, ArrayList<String>>) dialogDataIn.getSerializable("requestSubTasks");
                String firebaseUid = dialogDataIn.getString("firebaseUid");
                Log.d("requestSubTasksMap", requestSubTasksMap.toString());
                //foe tasks
                HashMap<String, Boolean> potentialAssigneeMapForChipsStatus = new HashMap<>();
                HashMap<String, Boolean> potentialSuperiorActivitiesMapForChipsStatus = new HashMap<>();
                HashMap<String, Boolean> potentialSuperiorTasksMapForChipsStatus = new HashMap<>();
                HashMap<String, Boolean> potentialSubTasksMapForChipsStatus = new HashMap<>();
                HashMap<String, Boolean> requestAssigneeForChipsStatus = new HashMap<>();
                HashMap<String, Boolean> requestSuperiorActivitiesMapForChipsStatus = new HashMap<>();
                HashMap<String, Boolean> requestSuperiorTasksMapForChipsStatus = new HashMap<>();
                HashMap<String, Boolean> requestSubTasksMapForChipsStatus = new HashMap<>();
                HashMap<Integer, String> potentialAssigneeMapForChips = new HashMap<>();
                HashMap<Integer, String> potentialSuperiorActivitiesMapForChips = new HashMap<>();
                HashMap<Integer, String> potentialSuperiorTasksMapForChips = new HashMap<>();
                HashMap<Integer, String> potentialSubTasksMapForChips = new HashMap<>();
                HashMap<Integer, String> requestAssigneeForChips = new HashMap<>();
                HashMap<Integer, String> requestSuperiorActivitiesMapForChips = new HashMap<>();
                HashMap<Integer, String> requestSuperiorTasksMapForChips = new HashMap<>();
                HashMap<Integer, String> requestSubTasksMapForChips = new HashMap<>();
                dialog.setContentView(R.layout.fragment_confirmation_dialog);
                dialog.setCancelable(true);
                CheckBox ConfirmAssigneeInTaskCheckbox = dialog.findViewById(R.id.ConfirmAssigneeInTaskCheckbox);
                CheckBox superiorTasksConfirmCheckBox = dialog.findViewById(R.id.superiorTasksConfirmCheckBox);
                CheckBox subTasksConfirmCheckBox = dialog.findViewById(R.id.subTasksConfirmCheckBox);
                CheckBox activitiesConfirmForTasksCheckBox = dialog.findViewById(R.id.activitiesConfirmForTasksCheckBox);
                CheckBox confirmRequestTaskAssigneeCheckBox = dialog.findViewById(R.id.confirmRequestTaskAssigneeCheckBox);
                CheckBox confirmRequestSubTaskCheckBox = dialog.findViewById(R.id.confirmRequestSubTaskCheckBox);
                CheckBox confirmRequestSuperiorTaskCheckBox = dialog.findViewById(R.id.confirmRequestSuperiorTaskCheckBox);
                CheckBox confirmRequestSuperiorActivitiesForTaskCheckBox = dialog.findViewById(R.id.confirmRequestSuperiorActivitiesForTaskCheckBox);
                LinearLayout ConfirmAssigneeInTaskLayout = dialog.findViewById(R.id.ConfirmAssigneeInTaskLayout);
                LinearLayout superiorTasksConfirmLayout = dialog.findViewById(R.id.superiorTasksConfirmLayout);
                LinearLayout subTasksConfirmLayout = dialog.findViewById(R.id.subTasksConfirmLayout);
                LinearLayout activitiesConfirmForTasksLayout = dialog.findViewById(R.id.activitiesConfirmForTasksLayout);
                LinearLayout confirmRequestTaskAssigneeLayout = dialog.findViewById(R.id.confirmRequestTaskAssigneeLayout);
                LinearLayout confirmRequestSubTaskLayout = dialog.findViewById(R.id.confirmRequestSubTaskLayout);
                LinearLayout confirmRequestSuperiorTaskLayout = dialog.findViewById(R.id.confirmRequestSuperiorTaskLayout);
                LinearLayout confirmRequestSuperiorActivitiesForTaskLayout = dialog.findViewById(R.id.confirmRequestSuperiorActivitiesForTaskLayout);
                ChipGroup ConfirmAssigneeInTask = dialog.findViewById(R.id.ConfirmAssigneeInTask);
                ChipGroup superiorTasksConfirm = dialog.findViewById(R.id.superiorTasksConfirm);
                ChipGroup subTasksConfirm = dialog.findViewById(R.id.subTasksConfirm);
                ChipGroup activitiesConfirmForTasks = dialog.findViewById(R.id.activitiesConfirmForTasks);
                ChipGroup confirmRequestTaskAssignee = dialog.findViewById(R.id.confirmRequestTaskAssignee);
                ChipGroup confirmRequestSubTask = dialog.findViewById(R.id.confirmRequestSubTask);
                ChipGroup confirmRequestSuperiorTask = dialog.findViewById(R.id.confirmRequestSuperiorTask);
                ChipGroup confirmRequestSuperiorActivitiesForTask = dialog.findViewById(R.id.confirmRequestSuperiorActivitiesForTask);
                Button confirmDialogButton = dialog.findViewById(R.id.confirmDialogButton);
                if (potentialAssigneeMap.isEmpty()){
                    ConfirmAssigneeInTaskLayout.setVisibility(View.GONE);
                }
                if (potentialSuperiorActivitiesMap.isEmpty()){
                    activitiesConfirmForTasksLayout.setVisibility(View.GONE);
                }
                if (potentialSuperiorTasksMap.isEmpty()){
                    superiorTasksConfirmLayout.setVisibility(View.GONE);
                }
                if (potentialSubTasksMap.isEmpty()){
                    subTasksConfirmLayout.setVisibility(View.GONE);
                }
                if (requestAssignee.isEmpty()){
                    confirmRequestTaskAssigneeLayout.setVisibility(View.GONE);
                }
                if (requestSuperiorActivitiesMap.isEmpty()){
                    confirmRequestSuperiorActivitiesForTaskLayout.setVisibility(View.GONE);
                }
                if (requestSuperiorTasksMap.isEmpty()){
                    confirmRequestSuperiorTaskLayout.setVisibility(View.GONE);
                }
                if (requestSubTasksMap.keySet().isEmpty()){
                    confirmRequestSubTaskLayout.setVisibility(View.GONE);
                }
                for (String assignee: potentialAssigneeMap.keySet()){
                    addChipsConfirm(context, ConfirmAssigneeInTask, potentialAssigneeMapForChipsStatus, potentialAssigneeMapForChips, assignee, assignee);
                }
                for (String activities: potentialSuperiorActivitiesMap.keySet()){
                    addChipsConfirm(context, activitiesConfirmForTasks, potentialSuperiorActivitiesMapForChipsStatus, potentialSuperiorActivitiesMapForChips, activities, activities);
                }
                for (String superiorTask: potentialSuperiorTasksMap.keySet()){
                    addChipsConfirm(context, superiorTasksConfirm, potentialSuperiorTasksMapForChipsStatus, potentialSuperiorTasksMapForChips, superiorTask, superiorTask);
                }
                for (String subtask: potentialSubTasksMap.keySet()){
                    addChipsConfirm(context, subTasksConfirm, potentialSubTasksMapForChipsStatus, potentialSubTasksMapForChips, subtask, subtask);
                }
                for (String rAssignee: requestAssignee.keySet()){
                    addChipsConfirm(context, confirmRequestTaskAssignee, requestAssigneeForChipsStatus, requestAssigneeForChips, rAssignee, rAssignee);
                }
                for (String rSubTask: requestSubTasksMap.keySet()){
                    addChipsConfirm(context, confirmRequestSubTask, requestSubTasksMapForChipsStatus, requestSubTasksMapForChips, rSubTask, rSubTask);
                }
                Log.d("requestSubTasksMap.keySet()", requestSubTasksMap.keySet().toString());

                for (String rSuperiorTask: requestSuperiorTasksMap.keySet()){
                    addChipsConfirm(context, confirmRequestSuperiorTask, requestSuperiorTasksMapForChipsStatus, requestSuperiorTasksMapForChips, rSuperiorTask, rSuperiorTask);
                }
                for (String rSuperiorAct: requestSuperiorActivitiesMap.keySet()){
                    addChipsConfirm(context, confirmRequestSuperiorActivitiesForTask, requestSuperiorActivitiesMapForChipsStatus, requestSuperiorActivitiesMapForChips, rSuperiorAct, rSuperiorAct);
                }
                confirmDialogButton.setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    votingItems(potentialAssigneeMapForChipsStatus, potentialAssigneeMap, firebaseUid);
                    votingItems(potentialSuperiorActivitiesMapForChipsStatus, potentialSuperiorActivitiesMap, firebaseUid);
                    votingItems(potentialSuperiorTasksMapForChipsStatus, potentialSuperiorTasksMap, firebaseUid);
                    votingItems(potentialSubTasksMapForChipsStatus, potentialSubTasksMap, firebaseUid);
                    votingItems(requestAssigneeForChipsStatus, requestAssignee, firebaseUid);
                    votingItems(requestSuperiorActivitiesMapForChipsStatus, requestSuperiorActivitiesMap, firebaseUid);
                    votingItems(requestSuperiorTasksMapForChipsStatus, requestSuperiorTasksMap, firebaseUid);
                    votingItems(requestSubTasksMapForChipsStatus, requestSubTasksMap, firebaseUid);
                    bundle.putSerializable("potentialAssigneeMap", potentialAssigneeMap);
                    bundle.putSerializable("potentialSuperiorActivitiesMap", potentialSuperiorActivitiesMap);
                    bundle.putSerializable("potentialSuperiorTasksMap", potentialSuperiorTasksMap);
                    bundle.putSerializable("potentialSubTasksMap", potentialSubTasksMap);
                    bundle.putSerializable("requestAssignee", requestAssignee);
                    bundle.putSerializable("requestSuperiorActivitiesMap", requestSuperiorActivitiesMap);
                    bundle.putSerializable("requestSuperiorTasksMap", requestSuperiorTasksMap);
                    bundle.putSerializable("requestSubTasksMap", requestSubTasksMap);
                    bundle.putString("dialogFlag", "confirmations");
                    dialogListener.getDialogData(bundle);
                    dialog.hide();
                });
                break;
            case R.layout.fragment_confirmation_dialog_activity:
                HashMap<String, ArrayList<String>> potentialAssigneeMapActivity = (HashMap<String, ArrayList<String>>) dialogDataIn.getSerializable("potentialAssigneeMap");
                HashMap<String, ArrayList<String>> potentialSuperiorActivitiesMapActivity = (HashMap<String, ArrayList<String>>) dialogDataIn.getSerializable("potentialSuperiorActivitiesMap");
                HashMap<String, ArrayList<String>> potentialSubActivitiesActivity = (HashMap<String, ArrayList<String>>) dialogDataIn.getSerializable("potentialSubActivitiesMap");
                HashMap<String, ArrayList<String>> potentialTasksMapActivity = (HashMap<String, ArrayList<String>>) dialogDataIn.getSerializable("potentialTasksMap");
                HashMap<String, ArrayList<String>> requestAssigneeActivity = (HashMap<String, ArrayList<String>>) dialogDataIn.getSerializable("requestAssignee");
                HashMap<String, ArrayList<String>> requestSuperiorActivitiesMapActivity = (HashMap<String, ArrayList<String>>) dialogDataIn.getSerializable("requestSuperiorActivities");
                HashMap<String, ArrayList<String>> requestSubActivitiesActivity = (HashMap<String, ArrayList<String>>) dialogDataIn.getSerializable("requestSubActivities");
                HashMap<String, ArrayList<String>> requestTasksMapActivity = (HashMap<String, ArrayList<String>>) dialogDataIn.getSerializable("requestTasks");
                String firebaseUidActivity = dialogDataIn.getString("firebaseUid");
                HashMap<String, Boolean> potentialAssigneeMapActivityForChipsStatus = new HashMap<>();
                HashMap<String, Boolean> potentialSuperiorActivitiesMapActivityForChipsStatus = new HashMap<>();
                HashMap<String, Boolean> potentialSubActivitiesActivityForChipsStatus = new HashMap<>();
                HashMap<String, Boolean> potentialTasksMapActivityForChipsStatus = new HashMap<>();
                HashMap<String, Boolean> requestAssigneeActivityForChipsStatus = new HashMap<>();
                HashMap<String, Boolean> requestSuperiorActivitiesMapActivityForChipsStatus = new HashMap<>();
                HashMap<String, Boolean> requestSubActivitiesActivityForChipsStatus = new HashMap<>();
                HashMap<String, Boolean> requestTasksMapActivityForChipsStatus = new HashMap<>();
                HashMap<Integer, String> potentialAssigneeMapActivityForChips = new HashMap<>();
                HashMap<Integer, String> potentialSuperiorActivitiesMapActivityForChips = new HashMap<>();
                HashMap<Integer, String> potentialSubActivitiesActivityForChips = new HashMap<>();
                HashMap<Integer, String> potentialTasksMapActivityForChips = new HashMap<>();
                HashMap<Integer, String> requestAssigneeActivityForChips = new HashMap<>();
                HashMap<Integer, String> requestSuperiorActivitiesMapActivityForChips = new HashMap<>();
                HashMap<Integer, String> requestSubActivitiesActivityForChips = new HashMap<>();
                HashMap<Integer, String> requestTasksMapActivityForChips = new HashMap<>();
                dialog.setContentView(R.layout.fragment_confirmation_dialog_activity);
                dialog.setCancelable(true);
                CheckBox ConfirmAssigneeInActivityCheckbox = dialog.findViewById(R.id.ConfirmAssigneeInActivityCheckbox);
                CheckBox ConfirmSubActivitiesInActivityCheckbox = dialog.findViewById(R.id.ConfirmSubActivitiesInActivityCheckbox);
                CheckBox ConfirmSuperiorActivitiesInActivityCheckbox = dialog.findViewById(R.id.ConfirmSuperiorActivitiesInActivityCheckbox);
                CheckBox ConfirmTasksInActivityCheckbox = dialog.findViewById(R.id.ConfirmTasksInActivityCheckbox);
                CheckBox ConfirmAssigneeInActivityRequestCheckbox = dialog.findViewById(R.id.ConfirmAssigneeInActivityRequestCheckbox);
                CheckBox ConfirmSubActivitiesInActivityRequestCheckbox = dialog.findViewById(R.id.ConfirmSubActivitiesInActivityRequestCheckbox);
                CheckBox ConfirmSuperiorActivitiesInActivityRequestCheckbox = dialog.findViewById(R.id.ConfirmSuperiorActivitiesInActivityRequestCheckbox);
                CheckBox ConfirmTasksInActivityRequestCheckbox = dialog.findViewById(R.id.ConfirmTasksInActivityRequestCheckbox);
                LinearLayout ConfirmAssigneeInActivityLayout = dialog.findViewById(R.id.ConfirmAssigneeInActivityLayoutt);
                LinearLayout ConfirmSubActivitiesInActivityLayout = dialog.findViewById(R.id.ConfirmSubActivitiesInActivityLayout);
                LinearLayout ConfirmSuperiorActivitiesInActivityLayout = dialog.findViewById(R.id.ConfirmSuperiorActivitiesInActivityLayout);
                LinearLayout ConfirmTasksInActivityLayout = dialog.findViewById(R.id.ConfirmTasksInActivityLayout);
                LinearLayout ConfirmAssigneeInActivityRequestLayout = dialog.findViewById(R.id.ConfirmAssigneeInActivityRequestLayout);
                LinearLayout ConfirmSubActivitiesInActivityRequestLayout = dialog.findViewById(R.id.ConfirmSubActivitiesInActivityRequestLayout);
                LinearLayout ConfirmSuperiorActivitiesInActivityRequestLayout = dialog.findViewById(R.id.ConfirmSuperiorActivitiesInActivityRequestLayout);
                LinearLayout ConfirmTasksInActivityRequestLayout = dialog.findViewById(R.id.ConfirmTasksInActivityRequestLayout);
                ChipGroup ConfirmAssigneeInActivity = dialog.findViewById(R.id.ConfirmAssigneeInActivity);
                ChipGroup ConfirmSubActivitiesInActivity = dialog.findViewById(R.id.ConfirmSubActivitiesInActivity);
                ChipGroup ConfirmSuperiorActivitiesInActivity = dialog.findViewById(R.id.ConfirmSuperiorActivitiesInActivity);
                ChipGroup ConfirmTasksInActivity = dialog.findViewById(R.id.ConfirmTasksInActivity);
                ChipGroup ConfirmAssigneeInActivityRequest = dialog.findViewById(R.id.ConfirmAssigneeInActivityRequest);
                ChipGroup ConfirmSubActivitiesInActivityRequest = dialog.findViewById(R.id.ConfirmSubActivitiesInActivityRequest);
                ChipGroup ConfirmSuperiorActivitiesInActivityRequest = dialog.findViewById(R.id.ConfirmSuperiorActivitiesInActivityRequest);
                ChipGroup ConfirmTasksInActivityRequest = dialog.findViewById(R.id.ConfirmTasksInActivityRequest);
                Button confirmButtonActivityDialog = dialog.findViewById(R.id.confirmButtonActivityDialog);
                if (potentialAssigneeMapActivity.isEmpty()){
                    ConfirmAssigneeInActivityLayout.setVisibility(View.GONE);
                }
                if (potentialSuperiorActivitiesMapActivity.isEmpty()){
                    ConfirmSuperiorActivitiesInActivityLayout.setVisibility(View.GONE);

                }
                if (potentialSubActivitiesActivity.isEmpty()){
                    ConfirmSubActivitiesInActivityLayout.setVisibility(View.GONE);
                }
                if (potentialTasksMapActivity.isEmpty()){
                    ConfirmTasksInActivityLayout.setVisibility(View.GONE);
                }
                if (requestAssigneeActivity.isEmpty()){
                    ConfirmAssigneeInActivityRequestLayout.setVisibility(View.GONE);
                }
                if (requestSuperiorActivitiesMapActivity.isEmpty()){
                    ConfirmSuperiorActivitiesInActivityRequestLayout.setVisibility(View.GONE);
                }
                if (requestSubActivitiesActivity.isEmpty()){
                    ConfirmSubActivitiesInActivityRequestLayout.setVisibility(View.GONE);
                }
                if (requestTasksMapActivity.keySet().isEmpty()){
                    ConfirmTasksInActivityRequestLayout.setVisibility(View.GONE);
                }
                for (String assignee: potentialAssigneeMapActivity.keySet()){
                    addChipsConfirm(context, ConfirmAssigneeInActivity, potentialAssigneeMapActivityForChipsStatus, potentialAssigneeMapActivityForChips, assignee, assignee);
                }
                for (String activitiesSup: potentialSuperiorActivitiesMapActivity.keySet()){
                    addChipsConfirm(context, ConfirmSuperiorActivitiesInActivity, potentialSuperiorActivitiesMapActivityForChipsStatus, potentialSuperiorActivitiesMapActivityForChips, activitiesSup, activitiesSup);
                }
                for (String subAct: potentialSubActivitiesActivity.keySet()){
                    addChipsConfirm(context, ConfirmSubActivitiesInActivity, potentialSubActivitiesActivityForChipsStatus, potentialSubActivitiesActivityForChips, subAct, subAct);
                }
                for (String subtask: potentialTasksMapActivity.keySet()){
                    addChipsConfirm(context, ConfirmTasksInActivity, potentialTasksMapActivityForChipsStatus, potentialTasksMapActivityForChips, subtask, subtask);
                }
                for (String rAssignee: requestAssigneeActivity.keySet()){
                    addChipsConfirm(context, ConfirmAssigneeInActivityRequest, requestAssigneeActivityForChipsStatus, requestAssigneeActivityForChips, rAssignee, rAssignee);
                }
                for (String rSupAct: requestSuperiorActivitiesMapActivity.keySet()){
                    addChipsConfirm(context, ConfirmSuperiorActivitiesInActivityRequest, requestSuperiorActivitiesMapActivityForChipsStatus, requestSuperiorActivitiesMapActivityForChips, rSupAct, rSupAct);
                }
                for (String rSubAct: requestSubActivitiesActivity.keySet()){
                    addChipsConfirm(context, ConfirmSubActivitiesInActivityRequest, requestSubActivitiesActivityForChipsStatus, requestSubActivitiesActivityForChips, rSubAct, rSubAct);
                }
                for (String rTask: requestTasksMapActivity.keySet()){
                    addChipsConfirm(context, ConfirmTasksInActivityRequest, requestTasksMapActivityForChipsStatus, requestTasksMapActivityForChips, rTask, rTask);
                }
                confirmButtonActivityDialog.setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    votingItems(potentialAssigneeMapActivityForChipsStatus, potentialAssigneeMapActivity, firebaseUidActivity);
                    votingItems(potentialSuperiorActivitiesMapActivityForChipsStatus, potentialSuperiorActivitiesMapActivity, firebaseUidActivity);
                    votingItems(potentialSubActivitiesActivityForChipsStatus, potentialSubActivitiesActivity, firebaseUidActivity);
                    votingItems(potentialTasksMapActivityForChipsStatus, potentialTasksMapActivity, firebaseUidActivity);
                    votingItems(requestAssigneeActivityForChipsStatus, requestAssigneeActivity, firebaseUidActivity);
                    votingItems(requestSuperiorActivitiesMapActivityForChipsStatus, requestSuperiorActivitiesMapActivity, firebaseUidActivity);
                    votingItems(requestSubActivitiesActivityForChipsStatus, requestSubActivitiesActivity, firebaseUidActivity);
                    votingItems(requestTasksMapActivityForChipsStatus, requestTasksMapActivity, firebaseUidActivity);
                    bundle.putSerializable("potentialAssigneeMap", potentialAssigneeMapActivity);
                    bundle.putSerializable("potentialSuperiorActivitiesMap", potentialSuperiorActivitiesMapActivity);
                    bundle.putSerializable("potentialSubActivitiesMap", potentialSubActivitiesActivity);
                    bundle.putSerializable("potentialTasksMap", potentialTasksMapActivity);
                    bundle.putSerializable("requestAssignee", requestAssigneeActivity);
                    bundle.putSerializable("requestSuperiorActivities", requestSuperiorActivitiesMapActivity);
                    bundle.putSerializable("requestSubActivities", requestSubActivitiesActivity);
                    bundle.putSerializable("requestTasks", requestTasksMapActivity);
                    bundle.putString("dialogFlag", "confirmations");
                    dialogListener.getDialogData(bundle);
                    dialog.hide();
                });
                break;
            case R.layout.fragment_create_comment:
                dialog.setContentView(R.layout.fragment_create_comment);
                dialog.setCancelable(true);
                TextView commentUserName = dialog.findViewById(R.id.commentUserName);
                EditText commentEdit = dialog.findViewById(R.id.commentEdit);
                AppCompatButton commentButtonCancel = dialog.findViewById(R.id.commentButtonCancel);
                AppCompatButton commentButtonSummit = dialog.findViewById(R.id.commentButtonSummit);

                String userName = dialogDataIn.getString("firebaseDisplayName");
                Log.d("creatorName", dialogDataIn.getString("firebaseDisplayName"));
                commentUserName.setText(userName);
                commentButtonCancel.setOnClickListener(v -> {
                    dialog.hide();
                });
                commentButtonSummit.setOnClickListener(v -> {
                    Bundle bundleTemp = new Bundle();
                    bundleTemp.putString("dialogFlag", "commentDialog");
                    bundleTemp.putString("commentEdit", commentEdit.getText().toString());
                    dialogListener.getDialogData(bundleTemp);
                    dialog.hide();
                });
                break;
        }
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        return dialog;
    }
    private static void votingItems(HashMap<String, Boolean> potentialAssigneeMapForChipsStatus,
                                    HashMap<String, ArrayList<String>> potentialAssigneeMap, String firebaseUid){
        for (String potentialAssigneeStatus : potentialAssigneeMapForChipsStatus.keySet()){
            ArrayList<String> voting;
            voting = potentialAssigneeMap.get(potentialAssigneeStatus);
            if (potentialAssigneeMapForChipsStatus.get(potentialAssigneeStatus)){
                if (!voting.contains(firebaseUid)){
                    voting.add(firebaseUid);
                    potentialAssigneeMap.replace(potentialAssigneeStatus, voting);
                }
            } else {
                if (voting.contains(firebaseUid)){
                    voting.remove(firebaseUid);
                    potentialAssigneeMap.replace(potentialAssigneeStatus, voting);
                }
            }
        }
    }
    public static void createNotification(String notificationTitle, String newActivityId, String taskId, String notificationDescription, String senderId, Long timeCreated,
                                                       ArrayList<String> notificationsPeople, CollectionReference notificationRef){
        //determine people to be receiving the notifications
        for (String notificationReceiver : notificationsPeople){
            NotificationsMessage notifymessage = new NotificationsMessage(notificationTitle, senderId,
                    notificationReceiver, newActivityId, taskId, notificationDescription, timeCreated);
            notificationRef.add(notifymessage).addOnSuccessListener(documentReference -> {
                String notificationId = documentReference.getId();
                Log.d("notificationId", notificationId);
                documentReference.update("notificationId", notificationId);
            });
        }
    }
    public static void populateChipsGroup(Context context, ChipGroup group, ArrayList<String> list){
        group.removeAllViews();
        for (String text : list){
            Chip chip = new Chip(context);
            chip.setText(text);
            group.addView(chip);
        }
    }
    public static void confirmActivity(Context context, CollectionReference activitiesRef, CollectionReference usersRef,
                                       String activityId, String receiver){
        activitiesRef.document(activityId).get().addOnSuccessListener(snapshot -> {
            Activity act = snapshot.toObject(Activity.class);
            ArrayList<String> confirmationList = act.getConfirmationList();
            ArrayList<String> assignee = act.getAssignee();
            ArrayList<String> potentialAssignee = act.getPotentialAssignee();
            ArrayList<String> potentialActivitiesList = act.getPotentialActivitiesList(); //superiorActivities
            ArrayList<String> potentialTasksList = act.getPotentialTasksList();
            ArrayList<String> tasksList = act.getTasksList();
            if (act.getPotentialAssignee().contains(receiver)){
                //the receiveer is one of the potential assign, press confirm to accept the assignment
                potentialAssignee.remove(receiver);
                assignee.add(receiver);
                activitiesRef.document(activityId).update("assignee", assignee);
                activitiesRef.document(activityId).update("potentialAssignee", potentialAssignee);
                // update teh Users' activities
                usersRef.document(receiver).get().addOnSuccessListener(snapshot12 -> {
                    ArrayList<String> activitiesUser = snapshot12.toObject(Users.class).getActivities();
                    activitiesUser.add(activityId);
                    usersRef.document(receiver).update("activities", activitiesUser); // this is the current activities the user is participating in
                }).addOnFailureListener(e -> {
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                    Log.d("Get User Data", e.toString());
                });
            }
            if (confirmationList.contains(receiver)){
                confirmationList.remove(receiver);
                activitiesRef.document(activityId).update("confirmationList", confirmationList);
            }
            //develop the detailed confirmation list here..
            for (String potentialAct : potentialActivitiesList){
                activitiesRef.document(potentialAct).get().addOnSuccessListener(snapshot1 -> {
                    int potentialActAssigneeConfirmCount = 0;
                    ArrayList<String> potentialActAssignee = snapshot1.toObject(Activity.class).getAssignee();
                    for (String member : potentialActAssignee){
                        if (!confirmationList.contains(member)){
                            potentialActAssigneeConfirmCount = potentialActAssigneeConfirmCount + 1;
                        }
                        if (potentialActAssigneeConfirmCount == potentialActAssignee.size()){
                            //the potentialActivity is approved
                            ArrayList<String> potentialActivitiesList1 = act.getPotentialActivitiesList(); //superiorActivities
                            ArrayList<String> activitiesList1 = act.getActivitiesList(); //superiorActivities
                            potentialActivitiesList1.remove(potentialAct);
                            activitiesList1.add(potentialAct);
                            activitiesRef.document(activityId).update("activitiesList", activitiesList1);
                            activitiesRef.document(activityId).update("potentialActivitiesList", potentialActivitiesList1);
                        }
                    }
                });
            }
            // develope the detailed confirmation for subtasks and superior tasks here..

            if (confirmationList.size() == 0){
                tasksList = potentialTasksList;
                potentialTasksList = new ArrayList<>();
                activitiesRef.document(activityId).update("tasksList", tasksList);
                activitiesRef.document(activityId).update("potentialTasksList", potentialTasksList);
                if (potentialAssignee.size() == 0){
                    activitiesRef.document(activityId).update("status", "Ongoing..");
                }
            }
        });
    }
    public static void createConfirmationDialog(){

    }
    //generateTaskGraph() has assumption that each task in a specific activity MUST ONLY have one superior task.

    public static Bundle generateTaskGraph(ArrayList<Task> taskArray, ArrayList<String> subtasks){
        int taskArraySize = taskArray.size();
        HashMap<String, String> superiorOfSubMap = new HashMap<>();
        ArrayList<String> subTaskIds = new ArrayList<>();
        float subTaskProgressReadingsAll[] = new float[taskArraySize];
        ArrayList<Integer> subTasksLevelsArray = new ArrayList<>();
        HashMap<String, ArrayList<String>> superiorTasksOfThisAllMap = new HashMap<>();
        HashMap<String, Float> subTaskProgressReadingsAllMap = new HashMap<>();
        int count = 0;
        for (Task task : taskArray){
            String subTaskId = task.getTaskId();
            float subTaskProgressReading = task.getProgressReading();
            ArrayList<String> superiorTaskIds = task.getSuperiorTasks();
            for (String superiorTaskId : superiorTaskIds){
                //!!Apply only for single superiorTask!!
                if (subtasks.contains(superiorTaskId)) {
                    superiorOfSubMap.put(subTaskId, superiorTaskId);
                }
            }
            subTaskIds.add(subTaskId);
            subTaskProgressReadingsAll[count] = subTaskProgressReading;
            subTaskProgressReadingsAllMap.put(subTaskId, subTaskProgressReading);
            count = count + 1;
        }
        for (String subtask : subTaskIds){
            int level = 0;
            ArrayList<String> superiorTasksOfThisAll = new ArrayList<>();
            String valueOfHashMap = superiorOfSubMap.get(subtask);
            String keyOfHashMap;
            while(valueOfHashMap != null){
                superiorTasksOfThisAll.add(valueOfHashMap);
                keyOfHashMap = valueOfHashMap;
                valueOfHashMap = superiorOfSubMap.get(keyOfHashMap);
                level = level + 1;
            }
            superiorTasksOfThisAllMap.put(subtask, superiorTasksOfThisAll);
            subTasksLevelsArray.add(level);
        }
        Bundle result = new Bundle();
        result.putFloatArray("subTaskProgressReadingsAll", subTaskProgressReadingsAll);
        result.putSerializable("subTaskProgressReadingsAllMap", subTaskProgressReadingsAllMap);
        result.putStringArrayList("subTaskIds", subTaskIds);
        result.putIntegerArrayList("subTasksLevelsArray", subTasksLevelsArray);
        result.putSerializable("superiorOfSubMap", superiorOfSubMap);
        result.putSerializable("superiorTasksOfThisAllMap", superiorTasksOfThisAllMap);
        return result;
    }
    public static Calendar dateConversion(Long time){
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(time);
        calendar.setTime(date);
        return calendar;
    }
    public static String concatArrayListString(ArrayList<String> list){
        String finalString = "";
        for (String item : list){
            finalString = finalString + item + ", ";
        }
        return finalString;
    }
    public static Bundle generateBundleToTaskDetailsPage(Task task){
        Bundle bundle = new Bundle();
        bundle.putInt("completionSelected", task.getCompletionSelected());
        bundle.putString("taskId", task.getTaskId());
        bundle.putString("taskTitle", task.getTaskTitle());
        bundle.putString("taskScope", task.getScope());
        bundle.putString("taskDescription", task.getDescription());
        bundle.putString("completion", task.getCompletion());
        bundle.putStringArrayList("subtasks", task.getSubTasks());
        bundle.putStringArrayList("superiorTasks", task.getSuperiorTasks());
        bundle.putStringArrayList("activities", task.getActivities());
        bundle.putStringArrayList("ConfirmationList", task.getConfirmationListTasks());
        bundle.putStringArrayList("AssigneeTask", task.getAssignee());
        bundle.putLong("StartDate", task.getStartDate());
        bundle.putLong("DueDate", task.getDueDate());
        bundle.putInt("ProgressReading", task.getProgressReading());
        bundle.putString("taskDifficulty", task.getLevel());
        bundle.putSerializable("requestAssignee", task.getRequestAssignee());
        bundle.putSerializable("requestSuperiorActivities", task.getRequestSuperiorActivities());
        bundle.putSerializable("requestSuperiorTasks", task.getRequestSuperiorTasks());
        bundle.putSerializable("requestSubTasks", task.getRequestSubTasks());
        bundle.putSerializable("potentialAssigneeMap", task.getPotentialAssigneeMap());
        bundle.putSerializable("potentialSuperiorActivitiesMap", task.getPotentialSuperiorActivitiesMap());
        bundle.putSerializable("potentialSuperiorTasksMap", task.getPotentialSuperiorTasksMap());
        bundle.putSerializable("potentialSubTasksMap", task.getPotentialSubTasksMap());
        bundle.putSerializable("assigneeExistence", task.getAssigneeExistence());
        bundle.putSerializable("assigneeInTime", task.getAssigneeInTime());
        bundle.putSerializable("assigneeOutTime", task.getAssigneeOutTime());
        return bundle;
    }
}
