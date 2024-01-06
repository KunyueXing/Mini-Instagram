package com.example.miniinstagram.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Comment {
    private String commentID;
    private String authorID;
    private String content;
    private int likesCount;
    private List<Comment> commentsOnComment;
    private Date createTime;

    public Comment() {
    }

    public Comment(String commentID, String authorID, String content) {
        this.createTime = new Date(System.currentTimeMillis());
        this.commentID = commentID;
        this.authorID = authorID;
        this.content = content;
    }

    public Comment(String commentID, String authorID, String content, int likesCount,
                   List<Comment> commentsOnComment) {
        this.commentID = commentID;
        this.authorID = authorID;
        this.content = content;
        this.likesCount = likesCount;
        this.commentsOnComment = commentsOnComment;
        this.createTime = new Date(System.currentTimeMillis());
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public List<Comment> getCommentsOnComment() {
        return commentsOnComment;
    }

    public void setCommentsOnComment(Comment comment) {
        if (this.commentsOnComment == null) {
            this.commentsOnComment = new ArrayList<>();
        }

        this.commentsOnComment.add(comment);
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void addLikes() {
        likesCount++;
    }

    public void deleteLikes() {
        likesCount--;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();

        result.put("commentID", commentID);
        result.put("authorID", authorID);
        result.put("content", content);
        result.put("createTime", createTime);

        return result;
    }
}
