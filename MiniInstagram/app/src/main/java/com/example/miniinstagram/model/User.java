package com.example.miniinstagram.model;

import java.util.ArrayList;
import java.util.List;

public class User extends Person{
    private String userID;
    private List<String> listOfFollowers;
    private List<String> listOfFollowing;
    private List<String> listOfPosts;
    private Profile profile;
    private List<String> followingGroups;

    public User(String userID, List<String> listOfFollowers, List<String> listOfFollowing,
                List<String> listOfPosts, Profile profile, List<String> followingGroups) {
        this.userID = userID;
        this.listOfFollowers = listOfFollowers;
        this.listOfFollowing = listOfFollowing;
        this.listOfPosts = listOfPosts;
        this.profile = profile;
        this.followingGroups = followingGroups;
    }

    public String getUserID() {
        return userID;
    }

    public List<String> getListOfFollowers() {
        return listOfFollowers;
    }

    public List<String> getListOfFollowing() {
        return listOfFollowing;
    }

    public List<String> getListOfPosts() {
        return listOfPosts;
    }

    public Profile getProfile() {
        return profile;
    }

    public List<String> getFollowingGroups() {
        return followingGroups;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public void addFollowers(String userID) {
        if (this.listOfFollowers == null) {
            this.listOfFollowers = new ArrayList<>();
        }

        this.listOfFollowers.add(userID);
    }

    public void addFollowing(String userID) {
        if (this.listOfFollowing == null) {
            this.listOfFollowing = new ArrayList<>();
        }

        this.listOfFollowing.add(userID);
    }

    public void addPost(String postID) {
        if (this.listOfPosts == null) {
            this.listOfPosts = new ArrayList<>();
        }

        this.listOfPosts.add(postID);
    }

    public void addGroup(String groupID) {
        if (this.followingGroups == null) {
            this.followingGroups = new ArrayList<>();
        }

        this.followingGroups.add(groupID);
    }
}
