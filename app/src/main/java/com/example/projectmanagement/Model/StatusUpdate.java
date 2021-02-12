package com.example.projectmanagement.Model;

import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;

public class StatusUpdate {
    private String statusUpdateId;
    private String ofWhichTaskId;
    private HashMap<String, ArrayList<String>> documentURI = new HashMap<>();
    private HashMap<String, ArrayList<String>> imageURI = new HashMap<>();
    private String createrUid;
    private String creatorName;
    private String statusUpdateDescription;
    private Long timeCreated;
    private HashMap<String, Boolean> like = new HashMap<>();
    private int numOfLike;
    private int numOfComment;
    private String followFromWhichUpdate;
    private ArrayList<String> documentDisplayName;
    public StatusUpdate(){}
    public StatusUpdate(String ofWhichTaskId, HashMap<String, ArrayList<String>> documentURI, HashMap<String, ArrayList<String>> imageURI, String createrUid,
                        String creatorName, String statusUpdateDescription, Long timeCreated, HashMap<String, Boolean> like,
                        String followFromWhichUpdate, ArrayList<String> documentDisplayName, int numOfLike, int numOfComment){
        this.ofWhichTaskId = ofWhichTaskId;
        this.documentURI = documentURI;
        this.imageURI = imageURI;
        this.createrUid = createrUid;
        this.creatorName = creatorName;
        this.statusUpdateDescription = statusUpdateDescription;
        this.timeCreated = timeCreated;
        this.like = like;
        this.followFromWhichUpdate = followFromWhichUpdate;
        this.documentDisplayName = documentDisplayName;
        this.numOfLike = numOfLike;
        this.numOfComment = numOfComment;
    }
    public void setOfWhichTaskId(String ofWhichTaskId){this.ofWhichTaskId = ofWhichTaskId;}
    public void setStatusUpdateId(String statusUpdateId){this.statusUpdateId = statusUpdateId;}
    public void setDocumentURI(HashMap<String, ArrayList<String>> documentURI){this.documentURI = documentURI;}
    public void setImageURI(HashMap<String, ArrayList<String>> imageURI){this.imageURI = imageURI;}
    public void setCreaterUid(String createrUid){this.createrUid = createrUid;}
    public void setCreatorName(String creatorName){this.creatorName = creatorName;}
    public void setStatusUpdateDescription(String statusUpdateDescription){this.statusUpdateDescription = statusUpdateDescription;}
    public void setTimeCreated(Long timeCreated){this.timeCreated = timeCreated;}
    public void setLike(HashMap<String, Boolean> like){this.like = like;}
    public void setNumOfLike(int numOfLike){this.numOfLike = numOfLike;}
    public void setNumOfComment(int numOfComment){this.numOfComment = numOfComment;}
    public void setFollowFromWhichUpdate(String followFromWhichUpdate){this.followFromWhichUpdate = followFromWhichUpdate;}
    public void setDocumentDisplayName(ArrayList<String> documentDisplayName){this.documentDisplayName = documentDisplayName;}
    public String getOfWhichTaskId(){return ofWhichTaskId;}
    public String getStatusUpdateId(){return statusUpdateId;}
    public HashMap<String, ArrayList<String>> getDocumentURI(){return documentURI;}
    public HashMap<String, ArrayList<String>> getImageURI(){return imageURI;}
    public String getCreaterUid(){return createrUid;}
    public String getCreatorName(){return creatorName;}
    public String getStatusUpdateDescription(){return statusUpdateDescription;}
    public Long getTimeCreated(){return timeCreated;}
    public HashMap<String, Boolean> getLike(){return like;}
    public int getNumOfLike(){return numOfLike;}
    public int getNumOfComment(){return numOfComment;}
    public String getFollowFromWhichUpdate(){return followFromWhichUpdate;}
    public ArrayList<String> getDocumentDisplayName(){return documentDisplayName;}
}
