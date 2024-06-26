package com.example.miniinstagram.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Group {
    private String groupID;
    private String groupName;
    private String ownerID;

    private String description;
    private Map<String, Object> members = new HashMap<>();

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public Group() {
    }

    public Group(String groupID, String groupName, String ownerID) {
        this.groupID = groupID;
        this.groupName = groupName;
        this.ownerID = ownerID;
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

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public Map<String, Object> getMembers() {
        return members;
    }

    public void setMembers(String userID, Object obj) {
        if (members == null) {
            members = new HashMap<>();
        }
        this.members.put(userID, obj);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();

        result.put("ownerID", ownerID);
        result.put("groupID", groupID);
        result.put("groupName", groupName);
        result.put("description", description);
        result.put("members", this.getMembers());

        return result;
    }
}
