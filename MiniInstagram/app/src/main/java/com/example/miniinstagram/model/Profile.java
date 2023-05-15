package com.example.miniinstagram.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Profile {

    private String profilePicUriStr;
    private String bio;
    private String phone;
    private GenderChoice gender;
    private Date birthday;
    private List<Link> links;

    public Profile(String profilePicUriStr, String bio, String phone, GenderChoice gender, Date birthday, List<Link> links) {
        this.profilePicUriStr = profilePicUriStr;
        this.bio = bio;
        this.phone = phone;
        this.gender = gender;
        this.birthday = birthday;
        this.links = links;
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

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
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
}
