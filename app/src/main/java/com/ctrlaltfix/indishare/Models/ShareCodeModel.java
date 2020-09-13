package com.ctrlaltfix.indishare.Models;

public class ShareCodeModel {

    String userId;
    String fileId;

    public ShareCodeModel(String userId, String fileId) {
        this.userId = userId;
        this.fileId = fileId;
    }

    public ShareCodeModel() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
