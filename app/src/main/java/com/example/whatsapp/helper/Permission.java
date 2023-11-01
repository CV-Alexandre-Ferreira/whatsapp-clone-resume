package com.example.whatsapp.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permission {

    public static boolean validatePermissions(String[] permissions, Activity activity, int requestCode){

        //Verify if version is bigger than marshmallow
        if(Build.VERSION.SDK_INT >= 23){

            List<String> permissionsList  = new ArrayList<>();

            //Verifies permissions

            for(String permission: permissions){
                Boolean havePermission = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
                if( !havePermission ) permissionsList.add(permission);

            }
            //If the list is empty it's not required to ask for permission
            if(permissionsList.isEmpty()) return true;

            String[] newPermissions = new String[permissionsList.size()];
            permissionsList.toArray(newPermissions);

            //request permission
            ActivityCompat.requestPermissions(activity, newPermissions, requestCode);
        }

        return true;
    }
}
