package com.example.miniinstagram.model;

import java.util.Date;

public class SystemNotification {
    private String NotifiID;
    private Date createTime;
    private String content;

    public SystemNotification() {
    }

    public SystemNotification(String notifiID, Date createTime, String content) {
        NotifiID = notifiID;
        this.createTime = new Date(System.currentTimeMillis());
        this.content = content;
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
}
