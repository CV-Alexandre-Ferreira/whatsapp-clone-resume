package com.example.whatsapp.model;

import com.example.whatsapp.config.FirebaseConfig;
import com.google.firebase.database.DatabaseReference;

public class Chat {
    private String senderId;
    private String receiverId;
    private String lastMessage;
    private User showcaseUser;
    private String isGroup;
    private Group group;


    public Chat() {
        this.setIsGroup("false");
    }

    public void save(){
        DatabaseReference database = FirebaseConfig.getFirebaseDatabase();
        DatabaseReference chatRef = database.child("conversas");
        chatRef.child(this.getSenderId()).child(this.getReceiverId())
                .setValue(this);
    }

    public String getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(String isGroup) {
        this.isGroup = isGroup;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public User getShowcaseUser() {
        return showcaseUser;
    }

    public void setShowcaseUser(User showcaseUser) {
        this.showcaseUser = showcaseUser;
    }
}
