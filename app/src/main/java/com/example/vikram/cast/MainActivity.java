package com.example.vikram.cast;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.vikram.cast.clientCode.ClientActivity;
import com.example.vikram.cast.serverCode.ServerActivity;

import java.security.Permission;

public class MainActivity extends AppCompatActivity {
    private PermissionManager permissionManagerObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();
    }

    public void goToAdvertiseActivity(View view){
        Intent myIntent = new Intent(this,ServerActivity.class);
        startActivity(myIntent);
    }
    public void goToDiscoverActivity(View view){
        Intent myIntent = new Intent(this,ClientActivity.class);
        startActivity(myIntent);
    }

    private void checkPermissions(){
        permissionManagerObj = new PermissionManager(this);

        if(!permissionManagerObj.checkPermission(Manifest.permission.ACCESS_WIFI_STATE) ||
                !permissionManagerObj.checkPermission(Manifest.permission.CHANGE_WIFI_STATE) ||
                !permissionManagerObj.checkPermission(Manifest.permission.INTERNET) ||
                !permissionManagerObj.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)||
                !permissionManagerObj.checkPermission(Manifest.permission.BLUETOOTH_ADMIN)){

            permissionManagerObj.askPermission(permissionManagerObj.getPermissionList());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==PermissionManager.REQUEST_CODE){
            //Location permission
            if(grantResults[3]==PackageManager.PERMISSION_DENIED){
                Toast.makeText(this, "Pls Grant Permissions", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}
