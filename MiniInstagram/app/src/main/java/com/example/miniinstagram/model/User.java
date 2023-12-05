package com.example.miniinstagram.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User extends Person{
    private String userID;
    private String email;
    private String username;
    private String name;
    private String profilePicUriStr;
    private String bio;
    private GenderChoice gender;
    private Date birthday;
    private AccountStatus status;

    private List<String> listOfFollowers;
    private List<String> listOfFollowing;
    private List<String> listOfPosts;
    private Profile profile;
    private List<String> followingGroups;

    public User() {

    }

    public User(String userID, List<String> listOfFollowers, List<String> listOfFollowing,
                List<String> listOfPosts, Profile profile, List<String> followingGroups) {
        this.userID = userID;
        this.listOfFollowers = listOfFollowers;
        this.listOfFollowing = listOfFollowing;
        this.listOfPosts = listOfPosts;
        this.profile = profile;
        this.followingGroups = followingGroups;
    }

    public User(String email, String username, String userID) {
        this.email = email;
        this.username = username;
        this.userID = userID;

        this.profilePicUriStr = "default";
        this.gender = GenderChoice.GENDER_CHOICE_DEFAULT;

        status = AccountStatus.ACCOUNT_STATUS_PUBLIC;
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

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String getName() {
        return name;
    }

    public AccountStatus getAccountStatus() {
        return status;
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

    public String getProfilePicUriStr() {
        return profilePicUriStr;
    }

    public void setProfilePicUriStr(String profilePicUriStr) {
        this.profilePicUriStr = profilePicUriStr;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public GenderChoice getGender() {
        return gender;
    }

    public void setGender(GenderChoice gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(int year, int month, int day) {
        initBirthday(year, month, day);
    }

    private void initBirthday(int year, int month, int day) {
        if (year > 0 && month > 0 && day > 0) {
            Calendar calendar = Calendar.getInstance();
            // year, month, day of month, hour, minute, second.
            // january is 0!
            calendar.set(year, month - 1, day, 0, 0, 0);
            this.birthday = calendar.getTime();
        }
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();

        result.put("email", email);
        result.put("username", username);
        result.put("userID", userID);
        result.put("status", status);
        result.put("name", name);

        result.put("profilePicUriStr", profilePicUriStr);
        result.put("bio", bio);
        result.put("gender", gender);
        result.put("birthday", birthday);

        return result;
    }
}
