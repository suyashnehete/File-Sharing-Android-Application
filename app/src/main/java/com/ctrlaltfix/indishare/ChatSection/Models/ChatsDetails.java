package com.ctrlaltfix.indishare.ChatSection.Models;

public class ChatsDetails {
    String chatId;
    String userId;
    long lastMessageCount;
    boolean isGroup;

    public ChatsDetails() {
    }

    public ChatsDetails(String chatId, String userId, long lastMessageCount, boolean isGroup) {
        this.chatId = chatId;
        this.userId = userId;
        this.lastMessageCount = lastMessageCount;
        this.isGroup = isGroup;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getLastMessageCount() {
        return lastMessageCount;
    }

    public void setLastMessageCount(long lastMessageCount) {
        this.lastMessageCount = lastMessageCount;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }
}
