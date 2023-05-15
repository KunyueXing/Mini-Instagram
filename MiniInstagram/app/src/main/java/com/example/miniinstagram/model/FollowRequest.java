package com.example.miniinstagram.model;

import java.util.Date;

public class FollowRequest {
    private String receiptUID;
    private String senderUID;
    private FollowStatus status;
    private Date createTime;
    private Date modifiedTime;

    public FollowRequest() {
    }

    public FollowRequest(String receiptUID, String senderUID, FollowStatus status,
                         Date modifiedTime) {
        this.receiptUID = receiptUID;
        this.senderUID = senderUID;
        this.status = status;
        this.createTime = new Date(System.currentTimeMillis());
        this.modifiedTime = modifiedTime;
    }

    public String getReceiptUID() {
        return receiptUID;
    }

    public void setReceiptUID(String receiptUID) {
        this.receiptUID = receiptUID;
    }

    public String getSenderUID() {
        return senderUID;
    }

    public void setSenderUID(String senderUID) {
        this.senderUID = senderUID;
    }

    public FollowStatus getStatus() {
        return status;
    }

    public void setStatus(FollowStatus status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = new Date(System.currentTimeMillis());
    }
}
