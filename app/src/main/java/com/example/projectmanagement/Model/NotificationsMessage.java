package com.example.projectmanagement.Model;

import java.util.ArrayList;

public class NotificationsMessage {
    private String notificationId, notificationTitle;
    private String senderUid;
    private String activity;
    private String task;
    private String description;
    private String receiver;
    private Long timeCreated;
    private ArrayList<String> superiorActivities = new ArrayList<String>();
    private ArrayList<String> relatedSubTasks = new ArrayList<String>();
    private ArrayList<String> relatedSuperiorTasks = new ArrayList<>();
    public NotificationsMessage(){}
    public NotificationsMessage(String notificationTitle, String senderUid, String receiver, String activity, String task, String description,
                                Long timeCreated){
        this.notificationTitle = notificationTitle;
        this.senderUid = senderUid;
        this.receiver = receiver;
        this.activity = activity;
        this.task = task;
        this.description = description;
        this.timeCreated = timeCreated;
    }

    public void setNotificationTitle(String notificationTitle){this.notificationTitle = notificationTitle;}
    public void setSenderUid(String senderUid){this.senderUid = senderUid;}
    public void setActivity(String activity){this.activity = activity;}
    public void setTask(String task) {this.task = task;}
    public void setDescription(String description){this.description = description;}
    public void setReceiver(String receiver){this.receiver = receiver;}
    public void setTimeCreated(Long timeCreated){this.timeCreated = timeCreated;}
    public void setNotificationId(String notificationId){this.notificationId = notificationId;}
    public void setSuperiorActivities(ArrayList<String> superiorActivities){this.superiorActivities = superiorActivities;}
    public void setRelatedSubTasks(ArrayList<String> relatedSubTasks){this.relatedSubTasks = relatedSubTasks;}
    public void setRelatedSuperiorTasks(ArrayList<String> relatedSuperiorTasks){this.relatedSuperiorTasks = relatedSuperiorTasks;}
    public String getNotificationTitle(){return notificationTitle;}
    public String getSenderUid(){return senderUid;}
    public String getActivity(){return activity;}
    public String getTask(){return task;}
    public String getDescription(){return description;}
    public String getReceiver(){return receiver;}
    public Long getTimeCreated(){return timeCreated;}
    public String getNotificationId(){return notificationId;}
    public ArrayList<String> getSuperiorActivities(){return superiorActivities;}
    public ArrayList<String> getRelatedSubTasks(){return relatedSubTasks;}
    public ArrayList<String> getRelatedSuperiorTasks(){return relatedSuperiorTasks;}
}
