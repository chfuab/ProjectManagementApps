package com.example.projectmanagement.Model;

public class CommentOnStatusUpdate {
    private String commentId;
    private String creatorUid;
    private String creatorDisplayName;
    private Long timeCreated;
    private String content;
    public CommentOnStatusUpdate(){}
    public CommentOnStatusUpdate(String creatorUid, String creatorDisplayName, Long timeCreated, String content){
        this.creatorUid = creatorUid;
        this.creatorDisplayName = creatorDisplayName;
        this.timeCreated = timeCreated;
        this.content = content;
    }
    public void setCommentId(String commentId){this.commentId = commentId;}
    public void setCreatorUid(String creatorUid){this.creatorUid = creatorUid;}
    public void setCreatorDisplayName(String creatorDisplayName){this.creatorDisplayName = creatorDisplayName;}
    public void setTimeCreated(Long timeCreated){this.timeCreated = timeCreated;}
    public void setContent(String content){this.content = content;}
    public String getCommentId(){return commentId;}
    public String getCreatorUid(){return creatorUid;}
    public String getCreatorDisplayName(){return creatorDisplayName;}
    public Long getTimeCreated(){return timeCreated;}
    public String getContent(){return content;}
}
