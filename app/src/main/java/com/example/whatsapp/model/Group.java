package com.example.whatsapp.model;

import com.example.whatsapp.config.FirebaseConfig;
import com.example.whatsapp.helper.Base64Custom;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.List;

public class Group implements Serializable {
    private String id;
    private String name;
    private String picture;
    private List<User> members;

    public Group() {
        DatabaseReference database = FirebaseConfig.getFirebaseDatabase();
        DatabaseReference grupoRef = database.child("grupos");

        //Recover push code
        String firebaseGroupId = grupoRef.push().getKey();
        setId(firebaseGroupId);
    }

    public void save(){

        DatabaseReference database = FirebaseConfig.getFirebaseDatabase();
        DatabaseReference groupRef = database.child("grupos");

        groupRef.child(getId()).setValue(this);

        //save chat for group members
        for( User member: getMembers() ){

            String senderId = Base64Custom.encodeBase64(member.getEmail());
            String receiverId = getId();

            Chat chat = new Chat();
            chat.setSenderId(senderId);
            chat.setReceiverId(receiverId);
            chat.setLastMessage("");
            chat.setIsGroup("true");
            chat.setGroup(this);

            chat.save();

        }

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }
}
