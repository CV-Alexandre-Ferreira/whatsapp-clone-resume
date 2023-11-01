package com.example.whatsapp.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseConfig {

    private static DatabaseReference database;
    private static FirebaseAuth auth;
    private static StorageReference storage;

    //returns instance of FirebaseDatabse
    public static DatabaseReference getFirebaseDatabase(){
        if( database == null ){
            database = FirebaseDatabase.getInstance().getReference();
        }
        return database;
    }


    //return instance of FirebaseAuth

    public static FirebaseAuth getFirebaseAuth(){
        if(auth == null){
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }

    //return instance of Storage
    public static StorageReference getFirebaseStorage(){

        if(storage == null){
            storage = FirebaseStorage.getInstance().getReference();
        }
        return storage;

    }
}
