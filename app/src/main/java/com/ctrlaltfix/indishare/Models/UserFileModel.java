package com.ctrlaltfix.indishare.Models;


public class UserFileModel {
    String name;
    long size;
    String url;
    long uploadTime;
    String type;
    String shareCode;
    String uri;


    public UserFileModel() {
    }

    public UserFileModel(String name, long size, String url, long uploadTime, String type, String shareCode, String uri) {
        this.name = name;
        this.size = size;
        this.url = url;
        this.uploadTime = uploadTime;
        this.type = type;
        this.shareCode = shareCode;
        this.uri = uri;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(long uploadTime) {
        this.uploadTime = uploadTime;
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
}
