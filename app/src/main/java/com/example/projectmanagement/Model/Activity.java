package com.example.projectmanagement.Model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Activity {
    private String activityTitle;//
    private String scope;//
    private String description;//
    private Long timeCreated;
    private String latestEvent;
    private int progressReading;//
    private String status/*e.g. ongoing, cancelled, completed, waiting for assignee confirmation etc.*/;
    private String statusDetails;
    private String creater;
    private String activityId;//
    private ArrayList<String> assignee = new ArrayList<>();//
    private ArrayList<String> potentialAssignee = new ArrayList<>();//
    private ArrayList<String> tasksList = new ArrayList<>();//
    private ArrayList<String> potentialTasksList = new ArrayList<>();//
    private ArrayList<String> activitiesList = new ArrayList<>();//
    private ArrayList<String> potentialActivitiesList = new ArrayList<>();//
    private ArrayList<String> subActivitiesList = new ArrayList<>();//
    private ArrayList<String> potentialSubActivitiesList = new ArrayList<>();//
    private ArrayList<String> confirmationList = new ArrayList<>();//
    private ArrayList<String> confirmationListRef = new ArrayList<>();//
    private Long activityStartDate, activityEndDate;
    private HashMap<String, ArrayList<String>> potentialAssigneeMap = new HashMap<>();
    private HashMap<String, ArrayList<String>> potentialSubActivitiesMap = new HashMap<>();
    private HashMap<String, ArrayList<String>> potentialSuperiorActivitiesMap = new HashMap<>();
    private HashMap<String, ArrayList<String>> potentialTasksMap = new HashMap<>();
    private HashMap<String, ArrayList<String>> requestAssignee = new HashMap<>();
    private HashMap<String, ArrayList<String>> requestSubActivities = new HashMap<>();
    private HashMap<String, ArrayList<String>> requestSuperiorActivities = new HashMap<>();//
    private HashMap<String, ArrayList<String>> requestTasks = new HashMap<>();//

    public Activity(){}
    public Activity(String activityTitle, String scope, String description, Long timeCreated, String status, ArrayList<String> assignee,
                    ArrayList<String> potentialAssignee, int progressReading, String creater, ArrayList<String> tasksList,
                    ArrayList<String> potentialTasksList, ArrayList<String> activitiesList, ArrayList<String> potentialActivitiesList,
                    ArrayList<String> subActivitiesList, ArrayList<String> potentialSubActivitiesList, ArrayList<String> confirmationList, ArrayList<String> confirmationListRef){
        this.activityTitle = activityTitle;
        this.scope = scope;
        this.description = description;
        this.status = status;
        this.progressReading = progressReading;
        this.creater = creater;
        this.assignee = assignee;
        this.potentialAssignee = potentialAssignee;
        this.tasksList = tasksList;
        this.potentialTasksList = potentialTasksList;
        this.activitiesList = activitiesList;
        this.potentialActivitiesList = potentialActivitiesList;
        this.subActivitiesList = subActivitiesList;
        this.potentialSubActivitiesList = potentialSubActivitiesList;
        this.timeCreated = timeCreated;
        this.confirmationList = confirmationList;
        this.confirmationListRef = confirmationListRef;
    }
    public void setActivityTitle(String activityTitle){this.activityTitle = activityTitle;}
    public void setTimeCreated(Long timeCreated){this.timeCreated = timeCreated;}
    public void setLatestEvent(String latestEvent){this.latestEvent = latestEvent;}
    public void setProgressReading(int progressReading){this.progressReading = progressReading;}
    public void setStatus(String status){this.status = status;}
    private void setStatusDetails(String statusDetails){this.statusDetails = statusDetails;}
    public void setAssignee(ArrayList<String> assignee){this.assignee = assignee;}
    public void setPotentialAssignee(ArrayList<String> potentialAssignee){this.potentialAssignee = potentialAssignee;}
    public void setScope(String scope){this.scope = scope;}
    public  void setDescription(String description){this.description = description;}
    public void setActivityId(String activityId){this.activityId = activityId;}
    public void setTasksList(ArrayList<String> tasksList){this.tasksList = tasksList;}
    public void setPotentialTasksList(ArrayList<String> potentialTasksList){this.potentialTasksList = potentialTasksList;}
    public void setActivitiesList(ArrayList<String> activitiesList){this.activitiesList = activitiesList;}
    public void setSubActivitiesList(ArrayList<String> subActivitiesList){this.subActivitiesList = subActivitiesList;}
    public void setPotentialActivitiesList(ArrayList<String> potentialActivitiesList){this.potentialActivitiesList = potentialActivitiesList;}
    public void setPotentialSubActivitiesList(ArrayList<String> potentialSubActivitiesList){this.potentialSubActivitiesList = potentialSubActivitiesList;}
    public void setCreater(String creater){this.creater = creater;}
    public void setActivityStartDate(Long activityStartDate){this.activityStartDate = activityStartDate;}
    public void setActivityEndDate(Long activityEndDate){this.activityEndDate = activityEndDate;}
    public void setConfirmationList(ArrayList<String> confirmationList){this.confirmationList = confirmationList;}
    public void setConfirmationListRef(ArrayList<String> confirmationListRef){this.confirmationListRef = confirmationListRef;}
    public void setPotentialAssigneeMap(HashMap<String, ArrayList<String>> potentialAssigneeMap){this.potentialAssigneeMap = potentialAssigneeMap;}
    public void setPotentialSubActivitiesMap(HashMap<String, ArrayList<String>> potentialSubActivitiesMap){this.potentialSubActivitiesMap = potentialSubActivitiesMap;}
    public void setPotentialSuperiorActivitiesMap(HashMap<String, ArrayList<String>> potentialSuperiorActivitiesMap){this.potentialSuperiorActivitiesMap = potentialSuperiorActivitiesMap;}
    public void setPotentialTasksMap(HashMap<String, ArrayList<String>> potentialTasksMap){this.potentialTasksMap = potentialTasksMap;}
    public void setRequestAssignee(HashMap<String, ArrayList<String>> requestAssignee){this.requestAssignee = requestAssignee;}
    public void setRequestSubActivities(HashMap<String, ArrayList<String>> requestSubActivities){this.requestSubActivities = requestSubActivities;}
    public void setRequestSuperiorActivities(HashMap<String, ArrayList<String>> requestSuperiorActivities){this.requestSuperiorActivities = requestSuperiorActivities;}
    public void setRequestTasks(HashMap<String, ArrayList<String>> requestTasks){this.requestTasks = requestTasks;}
    public String getActivityTitle(){return activityTitle;}
    public Long getTimeCreated(){return timeCreated;}
    public String getLatestEvent(){return latestEvent;}
    public int getProgressReading(){return progressReading;}
    public String getStatus(){return status;}
    public String getStatusDetails(){return statusDetails;}
    public ArrayList<String> getAssignee(){return assignee;}
    public ArrayList<String> getPotentialAssignee(){return potentialAssignee;}
    public String getScope(){return scope;}
    public String getDescription(){return description;}
    public String getActivityId(){return activityId;}
    public ArrayList<String> getTasksList(){return tasksList;}
    public ArrayList<String> getPotentialTasksList(){return potentialTasksList;}
    public ArrayList<String> getActivitiesList(){return activitiesList;}
    public ArrayList<String> getSubActivitiesList(){return subActivitiesList;}
    public ArrayList<String> getPotentialActivitiesList(){return potentialActivitiesList;}
    public ArrayList<String> getPotentialSubActivitiesList(){return potentialSubActivitiesList;}
    public String getCreater(){return creater;}
    public Long getActivityStartDate(){return activityStartDate;}
    public Long getActivityEndDate(){return activityEndDate;}
    public ArrayList<String> getConfirmationList(){return confirmationList;}
    public ArrayList<String> getConfirmationListRef(){return confirmationListRef;}
    public HashMap<String, ArrayList<String>> getPotentialAssigneeMap(){return potentialAssigneeMap;}
    public HashMap<String, ArrayList<String>> getPotentialSubActivitiesMap(){return potentialSubActivitiesMap;}
    public HashMap<String, ArrayList<String>> getPotentialSuperiorActivitiesMap(){return potentialSuperiorActivitiesMap;}
    public HashMap<String, ArrayList<String>> getPotentialTasksMap(){return potentialTasksMap;}
    public HashMap<String, ArrayList<String>> getRequestAssignee(){return requestAssignee;}
    public HashMap<String, ArrayList<String>> getRequestSubActivities(){return requestSubActivities;}
    public HashMap<String, ArrayList<String>> getRequestSuperiorActivities(){return requestSuperiorActivities;}
    public HashMap<String, ArrayList<String>> getRequestTasks(){return requestTasks;}
}