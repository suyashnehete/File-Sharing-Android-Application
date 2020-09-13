package com.ctrlaltfix.indishare.ChatSection.Models;

import java.util.ArrayList;

public class UsersModel {
    String id;
    String mobile;
    String name;
    boolean isOnline;
    String status;
    String url;
    String email;

    public UsersModel() {
    }

    public UsersModel(String id, String mobile, String name, boolean isOnline, String status, String url, String email) {
        this.id = id;
        this.mobile = mobile;
        this.name = name;
        this.isOnline = isOnline;
        this.status = status;
        this.url = url;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
