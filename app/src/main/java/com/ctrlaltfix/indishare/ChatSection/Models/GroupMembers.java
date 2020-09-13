package com.ctrlaltfix.indishare.ChatSection.Models;

public class GroupMembers {
    String id;
    String groupId;
    boolean isAdmin;

    public GroupMembers() {
    }

    public GroupMembers(String id, String groupId, boolean isAdmin) {
        this.id = id;
        this.groupId = groupId;
        this.isAdmin = isAdmin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
