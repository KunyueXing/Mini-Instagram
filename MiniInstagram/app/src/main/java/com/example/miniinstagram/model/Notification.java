package com.example.miniinstagram.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Notification {
    private String notifiID;
    private Date createTime;
    private String content;
    private String userID;
    private String postID;
    private NotificationType notificationType;

    public Notification() {
    }

    public Notification(String notifiID, String userID, NotificationType notificationType) {
        this.notifiID = notifiID;
        this.createTime = new Date(System.currentTimeMillis());
        this.userID = userID;
        this.notificationType = notificationType;

        generateContent(notificationType);
    }

    private void generateContent(NotificationType type) {

        switch (type) {
            case NOTIFICATION_TYPE_FOLLOWERS:
                this.content = "";
                break;
            case NOTIFICATION_TYPE_COMMENTS:
                this.content = "";
                break;
            case NOTIFICATION_TYPE_LIKES:
                this.content = "liked your post";
                break;
        }

    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();

        result.put("postID", postID);
        result.put("notifiID", notifiID);
        result.put("content", content);
        result.put("userID", userID);
        result.put("notificationType", notificationType);
        result.put("createTime", createTime);

        return result;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotifiID() {
        return notifiID;
    }

    public void setNotifiID(String notifiID) {
        this.notifiID = notifiID;
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
        if (notificationType == NotificationType.NOTIFICATION_TYPE_FOLLOWERS) {
            return false;
        }

        return true;
    }
}
