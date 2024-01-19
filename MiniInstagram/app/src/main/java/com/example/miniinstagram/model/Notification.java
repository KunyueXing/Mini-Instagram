package com.example.miniinstagram.model;

import java.util.Date;

public class Notification {
    private String NotifiID;
    private Date createTime;
    private String content;
    private String userID;
    private String postID;
    private boolean isPost;

    public Notification() {
    }

    public Notification(String notifiID, String userID, String content, boolean isPost) {
        NotifiID = notifiID;
        this.createTime = new Date(System.currentTimeMillis());
        this.content = content;
        this.userID = userID;
        this.isPost = isPost;
    }

    public String getNotifiID() {
        return NotifiID;
    }

    public void setNotifiID(String notifiID) {
        NotifiID = notifiID;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public boolean isPost() {
        return isPost;
    }

    public void setPost(boolean post) {
        isPost = post;
    }
}
