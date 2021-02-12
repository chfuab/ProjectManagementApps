package com.example.projectmanagement.Model;

import java.util.ArrayList;
import java.util.HashMap;

public class Task {
    private String taskTitle, scope, description, taskId, level,
            status/*To be due, overdue, cancelled, waiting assignee confirmation etc.*/, completion;
    private Long timeCreated, startDate, dueDate;
    private int progressReading, completionSelected;
    private ArrayList<String> activities = new ArrayList<>();
    private ArrayList<String> activitiesName = new ArrayList<>();
    private ArrayList<String> potentialActivities = new ArrayList<>();
    private ArrayList<String> assignee = new ArrayList<>();
    private ArrayList<String> assigneeName = new ArrayList<>();
    private ArrayList<String> potentialAssignee = new ArrayList<>();
    private ArrayList<String> subTasks = new ArrayList<>();
    private ArrayList<String> subTasksName = new ArrayList<>();
    private ArrayList<String> potentialSubTasks = new ArrayList<>();
    private ArrayList<String> superiorTasks = new ArrayList<>();
    private ArrayList<String> superiorTasksName = new ArrayList<>();
    private ArrayList<String> potentialSuperiorTasks = new ArrayList<>();
    private ArrayList<String> confirmationListTasksName = new ArrayList<>();
    private ArrayList<String> confirmationListTasks = new ArrayList<>();
    private HashMap<String, ArrayList<String>> requestAssignee  = new HashMap<>();
    private HashMap<String, ArrayList<String>> requestSuperiorActivities = new HashMap<>();
    private HashMap<String, ArrayList<String>> requestSuperiorTasks = new HashMap<>();
    private HashMap<String, ArrayList<String>> requestSubTasks = new HashMap<>();
    private HashMap<String, ArrayList<String>> potentialAssigneeMap = new HashMap<>();
    private HashMap<String, ArrayList<String>> potentialSuperiorActivitiesMap = new HashMap<>();
    private HashMap<String, ArrayList<String>> potentialSuperiorTasksMap = new HashMap<>();
    private HashMap<String, ArrayList<String>> potentialSubTasksMap = new HashMap<>();
    private HashMap<String, String> assigneeExistence = new HashMap<>();
    private HashMap<String, ArrayList<Long>> assigneeInTime = new HashMap<>();
    private HashMap<String, ArrayList<Long>> assigneeOutTime = new HashMap<>();

    public Task (){}
    public Task (String taskId, String taskTitle, String status, Long timeCreated, ArrayList<String> activities, HashMap<String, ArrayList<String>> potentialSuperiorActivitiesMap,
                 ArrayList<String> assignee, HashMap<String, ArrayList<String>> potentialAssigneeMap, ArrayList<String> subTasks, HashMap<String, ArrayList<String>> potentialSubTasksMap,
                 ArrayList<String> superiorTasks, HashMap<String, ArrayList<String>> potentialSuperiorTasksMap, int progressReading, String scope,
                 String description, String level, Long startDate, Long dueDate){
        this.taskId = taskId;
        this.taskTitle = taskTitle;//
        this.status = status;
        this.timeCreated = timeCreated;//
        this.activities = activities;
        this.potentialSuperiorActivitiesMap = potentialSuperiorActivitiesMap;
        this.assignee = assignee;//
        this.potentialAssigneeMap = potentialAssigneeMap;//
        this.subTasks = subTasks;
        this.potentialSubTasksMap = potentialSubTasksMap;
        this.superiorTasks = superiorTasks;
        this.potentialSuperiorTasksMap = potentialSuperiorTasksMap;
        this.progressReading = progressReading;
        this.scope = scope;//
        this.description = description;//
        this.level = level;
        this.startDate = startDate;//
        this.dueDate = dueDate;//

    }
    public void setTaskTitle(String taskTitle){this.taskTitle = taskTitle;}
    public void setTimeCreated(Long timeCreated){this.timeCreated = timeCreated;}
    public void setProgressReading(int progressReading){this.progressReading = progressReading;}
    public void setCompletionSelected(int completionSelected){this.completionSelected = completionSelected;}
    public void setStatus(String status){this.status = status;}
    public void setCompletion(String completion){this.completion = completion;}
    public void setPotentialActivities(ArrayList<String> potentialActivities){this.potentialActivities = potentialActivities;}
    public void setActivities(ArrayList<String> activities){this.activities = activities;}
    public void setActivitiesName(ArrayList<String> activitiesName){this.activitiesName = activitiesName;}
    public void setAssignee(ArrayList<String> assignee){this.assignee = assignee;}
    public void setAssigneeName(ArrayList<String> assigneeName){this.assigneeName = assigneeName;}
    public void setPotentialAssignee(ArrayList<String> potentialAssignee){this.potentialAssignee = potentialAssignee;}
    public void setSubTasks(ArrayList<String> subTasks){this.subTasks = subTasks;}
    public void setSubTasksName(ArrayList<String> subTasksName){this.subTasksName = subTasksName;}
    public void setPotentialSubTasks(ArrayList<String> potentialSubTasks){this.potentialSubTasks = potentialSubTasks;}
    public void setSuperiorTasks(ArrayList<String> superiorTasks){this.superiorTasks = superiorTasks;}
    public void setSuperiorTasksName(ArrayList<String> superiorTasksName){this.superiorTasksName = superiorTasksName;}
    public void setPotentialSuperiorTasks(ArrayList<String> potentialSuperiorTasks){this.potentialSuperiorTasks = potentialSuperiorTasks;}
    public void setScope(String scope){this.scope = scope;}
    public void setDescription(String description){this.description = description;}
    public void setTaskId(String taskId){this.taskId = taskId;}
    public void setLevel(String level){this.level = level;}
    public void setStartDate(Long startDate){this.startDate = startDate;}
    public void setDueDate(Long dueDate){this.dueDate = dueDate;}
    public void setPotentialAssigneeMap(HashMap<String, ArrayList<String>> potentialAssigneeMap){this.potentialAssigneeMap = potentialAssigneeMap;};
    public void setPotentialSuperiorActivitiesMap(HashMap<String, ArrayList<String>> potentialSuperiorActivitiesMap){this.potentialSuperiorActivitiesMap = potentialSuperiorActivitiesMap;}
    public void setPotentialSuperiorTasksMap(HashMap<String, ArrayList<String>> potentialSuperiorTasksMap){this.potentialSuperiorTasksMap = potentialSuperiorTasksMap;};
    public void setPotentialSubTasksMap(HashMap<String, ArrayList<String>> potentialSubTasksMap){this.potentialSubTasksMap = potentialSubTasksMap;};
    public void setConfirmationListTasks(ArrayList<String> confirmationListTasks){this.confirmationListTasks = confirmationListTasks;}
    public void setRequestAssignee(HashMap<String, ArrayList<String>> requestAssignee){this.requestAssignee = requestAssignee;}
    public void setRequestSuperiorActivities(HashMap<String, ArrayList<String>> requestSuperiorActivities){this.requestSuperiorActivities = requestSuperiorActivities;}
    public void setRequestSuperiorTasks(HashMap<String, ArrayList<String>> requestSuperiorTasks){this.requestSuperiorTasks = requestSuperiorTasks;}
    public void setRequestSubTasks(HashMap<String, ArrayList<String>> requestSubTasks){this.requestSubTasks = requestSubTasks;}
    public void setAssigneeExistence(HashMap<String, String> assigneeExistence){this.assigneeExistence = assigneeExistence;}
    public void setAssigneeInTime(HashMap<String, ArrayList<Long>> assigneeInTime){this.assigneeInTime = assigneeInTime;}
    public void setAssigneeOutTime(HashMap<String, ArrayList<Long>> assigneeOutTime){this.assigneeOutTime = assigneeOutTime;}
    public String getTaskTitle(){return taskTitle;}
    public Long getTimeCreated(){return timeCreated;}
    public int getProgressReading(){return progressReading;}
    public int getCompletionSelected(){return completionSelected;}
    public String getStatus(){return status;}
    public String getCompletion(){return completion;}
    public ArrayList<String> getAssignee(){return assignee;}
    public ArrayList<String> getAssigneeName(){return assigneeName;}
    public ArrayList<String> getPotentialAssignee(){return potentialAssignee;}
    public ArrayList<String> getSubTasks(){return subTasks;}
    public ArrayList<String> getSubTasksName(){return subTasksName;}
    public ArrayList<String> getPotentialSubTasks(){return potentialSubTasks;}
    public ArrayList<String> getSuperiorTasks(){return superiorTasks;}
    public ArrayList<String> getSuperiorTasksName(){return superiorTasksName;}
    public ArrayList<String> getPotentialSuperiorTasks(){return potentialSuperiorTasks;}
    public ArrayList<String> getActivities(){return activities;}
    public ArrayList<String> getActivitiesName(){return activitiesName;}
    public ArrayList<String> getPotentialActivities(){return potentialActivities;}
    public String getScope(){return scope;}
    public String getDescription(){return description;}
    public String getTaskId(){return taskId;}
    public String getLevel(){return level;}
    public Long getStartDate(){return startDate;}
    public Long getDueDate(){return dueDate;}
    public ArrayList<String> getConfirmationListTasks(){return confirmationListTasks;}
    public HashMap<String, ArrayList<String>> getPotentialAssigneeMap(){return potentialAssigneeMap;}
    public HashMap<String, ArrayList<String>> getPotentialSuperiorActivitiesMap(){return potentialSuperiorActivitiesMap;}
    public HashMap<String, ArrayList<String>> getPotentialSuperiorTasksMap(){return potentialSuperiorTasksMap;}
    public HashMap<String, ArrayList<String>> getPotentialSubTasksMap(){return potentialSubTasksMap;}
    public HashMap<String, ArrayList<String>> getRequestAssignee(){return requestAssignee;}
    public HashMap<String, ArrayList<String>> getRequestSuperiorActivities(){return requestSuperiorActivities;}
    public HashMap<String, ArrayList<String>> getRequestSuperiorTasks(){return requestSuperiorTasks;}
    public HashMap<String, ArrayList<String>> getRequestSubTasks(){return requestSubTasks;}
    public HashMap<String, String> getAssigneeExistence(){return assigneeExistence;}
    public HashMap<String, ArrayList<Long>> getAssigneeInTime(){return assigneeInTime;}
    public HashMap<String, ArrayList<Long>> getAssigneeOutTime(){return assigneeOutTime;}
}
