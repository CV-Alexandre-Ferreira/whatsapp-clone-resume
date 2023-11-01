package com.example.whatsapp.helper;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.whatsapp.config.FirebaseConfig;
import com.example.whatsapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UserFirebaseHelper {

    public static String getUserId(){
        FirebaseAuth user = FirebaseConfig.getFirebaseAuth();
        String email = user.getCurrentUser().getEmail();
        String userId = Base64Custom.encodeBase64(email);

        return userId;
    }

    public static FirebaseUser getCurrentUser(){
        FirebaseAuth user = FirebaseConfig.getFirebaseAuth();
        return user.getCurrentUser();
    }

    public static Boolean updateUserName(String name){

        try{
            FirebaseUser user = getCurrentUser();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name).build();
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        Log.d("Perfil", "Erro ao atualizar nome de perfil");
                    }
                }
            });
            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    public static User getDataFromLoggedUser(){
        FirebaseUser firebaseUser = getCurrentUser();
        User user = new User();
        user.setEmail(firebaseUser.getEmail());
        user.setName(firebaseUser.getDisplayName());

        if(firebaseUser.getPhotoUrl() == null){
            user.setFoto("");
        }else {
            user.setFoto(firebaseUser.getPhotoUrl().toString());
        }

        return user;

    }

    public static Boolean updateUserPicture(Uri url){

        try{
            FirebaseUser user = getCurrentUser();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(url).build();
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        Log.d("Perfil", "Erro ao atualizar foto de perfil");
                    }
                }
            });
            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

}
