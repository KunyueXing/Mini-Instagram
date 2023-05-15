package com.example.miniinstagram.model;

import java.util.List;

public class Group {
    private String groupID;
    private String groupName;
    private int numOfmembers;
    private List<String> members;

    public Group() {
    }

    public Group(String groupID, String groupName, int numOfmembers, List<String> members) {
        this.groupID = groupID;
        this.groupName = groupName;
        this.numOfmembers = numOfmembers;
        this.members = members;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getNumOfmembers() {
        return numOfmembers;
    }

    public void setNumOfmembers(int numOfmembers) {
        this.numOfmembers = numOfmembers;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }
}
