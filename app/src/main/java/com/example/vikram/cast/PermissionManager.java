package com.example.vikram.cast;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionManager {

    private Activity requestingActivity;

    PermissionManager(Activity activity){
        requestingActivity=activity;
    }

    boolean checkPermission(String permission){
        int result=ContextCompat.checkSelfPermission(requestingActivity,permission);

        if(result== PackageManager.PERMISSION_DENIED){
            return true;
        }
        return false;
    }

    void askPermission(String[] permissionList){
        ActivityCompat.requestPermissions(requestingActivity,permissionList,1);
    }
}

