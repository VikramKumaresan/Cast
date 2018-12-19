package com.example.vikram.cast;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.vikram.cast.clientCode.ClientActivity;
import com.example.vikram.cast.serverCode.ServerActivity;

public class MainActivity extends AppCompatActivity {

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
        PermissionManager permissionManagerObj = new PermissionManager(this);

        if(!permissionManagerObj.checkPermission(Manifest.permission.ACCESS_WIFI_STATE) ||
                !permissionManagerObj.checkPermission(Manifest.permission.CHANGE_WIFI_STATE) ||
                !permissionManagerObj.checkPermission(Manifest.permission.INTERNET) ||
                !permissionManagerObj.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)||
                !permissionManagerObj.checkPermission(Manifest.permission.BLUETOOTH_ADMIN)){

            permissionManagerObj.askPermission(new String[] {
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.BLUETOOTH_ADMIN
            });
        }
    }
}
