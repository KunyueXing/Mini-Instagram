package com.example.miniinstagram.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Profile {

    private String profilePicUriStr;
    private String bio;
    private String phone;
    private GenderChoice gender;
    private Date birthday;
    private List<Link> links;

    public Profile(String Default, String i_like_swimming, GenderChoice genderChoiceNoneBinary, int i, int i1, int i2, ArrayList<Link> links) {
    }

    public Profile(String profilePicUriStr) {
        this.profilePicUriStr = profilePicUriStr;

        this.gender = GenderChoice.GENDER_CHOICE_DEFAULT;
        this.links = new ArrayList<>();
    }

    public Profile(String profilePicUriStr, String bio, String phone) {
        this.profilePicUriStr = profilePicUriStr;
        this.bio = bio;
        this.phone = phone;

        this.gender = GenderChoice.GENDER_CHOICE_DEFAULT;
        this.links = new ArrayList<>();
    }

    public Profile(String profilePicUriStr, String bio, String phone, GenderChoice gender,
                   int year, int month, int day, List<Link> links) {
        this.profilePicUriStr = profilePicUriStr;
        this.bio = bio;
        this.phone = phone;
        this.gender = gender;
        this.links = links;

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public List<Link> getLinks() {
        return links;
    }

    public void addLink(Link link) {
        if (this.links == null) {
            this.links = new ArrayList<Link>();
        }

        this.links.add(link);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();

        result.put("profile pic", profilePicUriStr);
        result.put("bio", bio);
        result.put("phone", phone);
        result.put("gender", gender);
        result.put("birthday", birthday);
        result.put("links", links);

        return result;
    }
}
