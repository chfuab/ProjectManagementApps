package com.example.projectmanagement.Model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Users {
    private String uid;
    private String displayName;
    private Long timeCreated;
    private String status;
    private String email;
    private int numOfJobsOnHand;
    private ArrayList<String> activities = new ArrayList<>();
    private ArrayList<String> tasks = new ArrayList<>();
    private HashMap<String, Boolean> requestActivities = new HashMap<>();
    private HashMap<String, Boolean> requestTasks = new HashMap<>();

    public Users(){}
    public Users(String uid, String displayName, Long timeCreated, String status, String email, ArrayList<String> activities, ArrayList<String> tasks){
        this.uid = uid;
        this.displayName = displayName;
        this.timeCreated = timeCreated;
        this.status = status;
        this.email = email;
        this.activities = activities;
        this.tasks = tasks;
    }

    public void setUid(String uid) { this.uid = uid; }
    public void setDisplayName(String displayName){this.displayName = displayName;}
    public void setTimeCreated(Long timeCreated){this.timeCreated = timeCreated;}
    public void setStatus(String status){this.status = status;}
    public void setEmail(String email){this.email = email;}
    public void setNumOfJobsOnHand(int numOfJobsOnHand){this.numOfJobsOnHand = numOfJobsOnHand;}
    public void setActivities(ArrayList<String> activities){this.activities = activities;}
    public void setTasks(ArrayList<String> tasks){this.tasks = tasks;}
    public void setRequestActivities(HashMap<String, Boolean> requestActivities){this.requestActivities = requestActivities;}
    public void setRequestTasks(HashMap<String, Boolean> requestTasks){this.requestTasks = requestTasks;}
    public String getUid(){return uid;}
    public String getDisplayName(){return displayName;}
    public Long getTimeCreated(){return timeCreated;}
    public String getStatus(){return status;}
    public String getEmail(){return email;}
    public int getNumOfJobsOnHand(){return numOfJobsOnHand;}
    public ArrayList<String> getActivities(){return activities;}
    public ArrayList<String> getTasks(){return tasks;}
    public HashMap<String, Boolean> getRequestActivities(){return requestActivities;}
    public HashMap<String, Boolean> getRequestTasks(){return requestTasks;}
}
