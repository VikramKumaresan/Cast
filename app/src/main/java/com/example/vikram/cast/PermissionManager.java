package com.example.vikram.cast;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionManager {
    final static int REQUEST_CODE = 1;
    private Activity requestingActivity;
    private String[] permissionList;

    PermissionManager(Activity activity){
        requestingActivity=activity;
        permissionList= new String[]{
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH_ADMIN
        };
    }

    boolean checkPermission(String permission){
        int result=ContextCompat.checkSelfPermission(requestingActivity,permission);

        if(result== PackageManager.PERMISSION_DENIED){
            return true;
        }
        return false;
    }

    void askPermission(String[] permissionList){
        ActivityCompat.requestPermissions(requestingActivity,permissionList,REQUEST_CODE);
    }

    String[] getPermissionList() {
        return permissionList;
    }
}

