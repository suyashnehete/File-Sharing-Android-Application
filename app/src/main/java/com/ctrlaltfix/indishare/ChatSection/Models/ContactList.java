package com.ctrlaltfix.indishare.ChatSection.Models;

public class ContactList {
    String mobile;
    String name;
    String id;
    String profile;

    public ContactList() {
    }

    public ContactList(String mobile, String name, String id, String profile) {
        this.mobile = mobile;
        this.name = name;
        this.id = id;
        this.profile = profile;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
