package com.ctrlaltfix.indishare.Models;


import com.google.firebase.storage.UploadTask;

public class TrackUserFileModel {
    String name;
    long size;
    String type;
    String shareCode;
    String uri;
    UploadTask uploadTask;

    public TrackUserFileModel(String name, long size, String type, String shareCode, String uri, UploadTask uploadTask) {
        this.name = name;
        this.size = size;
        this.type = type;
        this.shareCode = shareCode;
        this.uri = uri;
        this.uploadTask = uploadTask;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getShareCode() {
        return shareCode;
    }

    public void setShareCode(String shareCode) {
        this.shareCode = shareCode;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public UploadTask getUploadTask() {
        return uploadTask;
    }

    public void setUploadTask(UploadTask uploadTask) {
        this.uploadTask = uploadTask;
    }
}
