package com.example.whatsapp.model;

import com.example.whatsapp.config.FirebaseConfig;
import com.example.whatsapp.helper.UserFirebaseHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {

    private String id;
    private String name;
    private String email;
    private String password;
    private String foto;

    public User() {
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public void save(){
        DatabaseReference firebaseRef = FirebaseConfig.getFirebaseDatabase();
        DatabaseReference user = firebaseRef.child("usuarios").child(getId());

        user.setValue(this);
    }

    public void update(){
        String userId = UserFirebaseHelper.getUserId();
        DatabaseReference database = FirebaseConfig.getFirebaseDatabase();

        DatabaseReference usersRef = database.child("usuarios")
                .child(userId);

        Map<String, Object> userValues = convertToMap();
        usersRef.updateChildren(userValues);
    }
    @Exclude
    public Map<String, Object> convertToMap(){
        HashMap<String,Object> mapUser = new HashMap<>();

        mapUser.put("email", getEmail());
        mapUser.put("nome", getName());
        mapUser.put("foto", getFoto());

        return mapUser;
    }

    @Exclude
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
