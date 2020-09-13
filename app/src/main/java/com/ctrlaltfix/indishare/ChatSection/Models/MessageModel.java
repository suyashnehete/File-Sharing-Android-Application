package com.ctrlaltfix.indishare.ChatSection.Models;

public class MessageModel {

    String id;
    String senderId;
    String senderMob;
    String message;
    String time;
    String date;
    String url;
    boolean isDeletedBySender;
    boolean isDeletedByReceiver;
    boolean isDeleted;
    boolean isSeen;
    String type;
    String uri;
    String name;

    public MessageModel() {
    }

    public MessageModel(String id, String senderId, String senderMob, String message, String time, String date, String url, boolean isDeletedBySender, boolean isDeletedByReceiver, boolean isDeleted, boolean isSeen, String type, String uri, String name) {
        this.id = id;
        this.senderId = senderId;
        this.senderMob = senderMob;
        this.message = message;
        this.time = time;
        this.date = date;
        this.url = url;
        this.isDeletedBySender = isDeletedBySender;
        this.isDeletedByReceiver = isDeletedByReceiver;
        this.isDeleted = isDeleted;
        this.isSeen = isSeen;
        this.type = type;
        this.uri = uri;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderMob() {
        return senderMob;
    }

    public void setSenderMob(String senderMob) {
        this.senderMob = senderMob;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isDeletedBySender() {
        return isDeletedBySender;
    }

    public void setDeletedBySender(boolean deletedBySender) {
        isDeletedBySender = deletedBySender;
    }

    public boolean isDeletedByReceiver() {
        return isDeletedByReceiver;
    }

    public void setDeletedByReceiver(boolean deletedByReceiver) {
        isDeletedByReceiver = deletedByReceiver;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
