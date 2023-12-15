package com.example.miniinstagram.model;

import static com.example.miniinstagram.model.PrivacySetting.PRIVACY_SETTING_PUBLIC;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Post {
    private String postID;
    private String description;
    private String postImageUrl;
    private String authorID;
    private PrivacySetting postPrivacySetting;
    private Date createTime;
    private Map<String, Comment> listOfComments;

    public Post() {
    }

    // create Post with postID, content, imageUrl, authorID, the default privacy setting is public
    public Post(String postID, String description, String postImageUrl, String authorID) {
        this.postID = postID;
        this.description = description;
        this.postImageUrl = postImageUrl;
        this.authorID = authorID;

        this.postPrivacySetting = PRIVACY_SETTING_PUBLIC;
        this.createTime = new Date(System.currentTimeMillis());
        this.listOfComments = new HashMap<>();
    }

    // create Post with additional privacy setting
    public Post(String postID, String description, String postImageUrl, int likesCount,
                String authorID, PrivacySetting postPrivacySetting) {
        this.postID = postID;
        this.description = description;
        this.postImageUrl = postImageUrl;
        this.authorID = authorID;
        this.postPrivacySetting = postPrivacySetting;

        this.createTime = new Date(System.currentTimeMillis());
        this.listOfComments = new HashMap<>();
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

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public PrivacySetting getPostPrivacySetting() {
        return postPrivacySetting;
    }

    public void setPostPrivacySetting(PrivacySetting postPrivacySetting) {
        this.postPrivacySetting = postPrivacySetting;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Map<String, Comment> getListOfComments() {
        return listOfComments;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();

        result.put("postID", postID);
        result.put("authorID", authorID);
        result.put("description", description);
        result.put("postImageUrl", postImageUrl);
        result.put("postPrivacySetting", postPrivacySetting);
        result.put("createTime", createTime);

        return result;
    }
}
