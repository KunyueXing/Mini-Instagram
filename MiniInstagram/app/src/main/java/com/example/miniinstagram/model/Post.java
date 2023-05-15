package com.example.miniinstagram.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Post {
    private String postID;
    private String description;
    private String postImageUrl;
    private int likesCount;
    private String authorID;
    private PrivacySetting PostPrivacySetting;
    private Date createTime;
    private List<Comment> listOfComments;

    public Post() {
    }

    public Post(String postID, String description, String postImageUrl, int likesCount,
                String authorID, PrivacySetting postPrivacySetting, List<Comment> listOfComments) {
        this.postID = postID;
        this.description = description;
        this.postImageUrl = postImageUrl;
        this.likesCount = likesCount;
        this.authorID = authorID;
        PostPrivacySetting = postPrivacySetting;
        this.createTime = new Date(System.currentTimeMillis());
        this.listOfComments = listOfComments;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public void setPostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public PrivacySetting getPostPrivacySetting() {
        return PostPrivacySetting;
    }

    public void setPostPrivacySetting(PrivacySetting postPrivacySetting) {
        PostPrivacySetting = postPrivacySetting;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public List<Comment> getListOfComments() {
        return listOfComments;
    }

    public void addComments(Comment comment) {
        if (this.listOfComments == null) {
            this.listOfComments = new ArrayList<Comment>();
        }

        this.listOfComments.add(comment);
    }

    public void addLikes() {
        likesCount++;
    }

    public void deleteLikes() {
        likesCount--;
    }
}
