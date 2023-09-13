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
    private int likesCount;
    private String authorID;
    private PrivacySetting postPrivacySetting;
    private Date createTime;
    private List<Comment> listOfComments;

    public Post() {
    }

    // create Post with postID, content, imageUrl, authorID, the default privacy setting is public
    public Post(String postID, String description, String postImageUrl, String authorID) {
        this.postID = postID;
        this.description = description;
        this.postImageUrl = postImageUrl;
        this.authorID = authorID;

        this.likesCount = 0;
        this.postPrivacySetting = PRIVACY_SETTING_PUBLIC;
        this.createTime = new Date(System.currentTimeMillis());
        this.listOfComments = new ArrayList<>();
    }

    // create Post with additional privacy setting
    public Post(String postID, String description, String postImageUrl, int likesCount,
                String authorID, PrivacySetting postPrivacySetting) {
        this.postID = postID;
        this.description = description;
        this.postImageUrl = postImageUrl;
        this.authorID = authorID;
        this.postPrivacySetting = postPrivacySetting;

        this.likesCount = 0;
        this.createTime = new Date(System.currentTimeMillis());
        this.listOfComments = new ArrayList<>();
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

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();

        result.put("postID", postID);
        result.put("authorID", authorID);
        result.put("content", description);
        result.put("imageUrl", postImageUrl);
        result.put("likes", likesCount);
        result.put("post privacy", postPrivacySetting);
        result.put("createTime", createTime);
        result.put("comments on post", listOfComments);

        return result;
    }
}
