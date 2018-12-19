package com.example.vikram.cast.clientCode;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vikram.cast.R;
import com.example.vikram.cast.clientCode.ConnectAndShowQuestionsFragment.ConnectAndShowQuestionsFragment;
import com.example.vikram.cast.clientCode.ServerDiscoveryFragment.ServerDiscoveryFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class ClientActivity extends AppCompatActivity {
    private android.support.v4.app.FragmentManager fragmentManager;
    private Toast message;
    public static AdapterView.OnItemClickListener serverListItemClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        initializeVariables();
        goToServerDiscoveryFragment();
    }

    void initializeVariables(){
        fragmentManager = getSupportFragmentManager();
        message = new Toast(this);

        serverListItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ServerDiscoveryFragment serverDiscoveryFragmentObj = (ServerDiscoveryFragment) fragmentManager.findFragmentById(R.id.fragmentContainerClient);
                serverDiscoveryFragmentObj.stopDiscovery();

                ConnectAndShowQuestionsFragment connectionFragmentObj = new ConnectAndShowQuestionsFragment();
                connectionFragmentObj.setServerEndpointConnectionId(serverDiscoveryFragmentObj.getEndpointIdAtPosition(position));
                switchFragment(connectionFragmentObj,getString(R.string.connectAndShowQuestionsFragment));
            }
        };
    }

    //ServerDiscoveryFragment funcs
    void goToServerDiscoveryFragment(){
        fragmentManager.beginTransaction()
                .add(R.id.fragmentContainerClient,new ServerDiscoveryFragment(),getString(R.string.serverDiscoveryFragment))
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }
    public static void attachItemClickListenerToList(ListView list){
        //Called from ServerDicoveryFragment
        list.setOnItemClickListener(serverListItemClickListener);
    }

    //Show questions Fragment funcs
    public void onSendButtonClicked(View view){
        ConnectAndShowQuestionsFragment obj = (ConnectAndShowQuestionsFragment)fragmentManager.findFragmentById(R.id.fragmentContainerClient);
        obj.submitVotesToServer();
    }

    //Server Activity Util Funcs
    private void switchFragment(Fragment newFragment, String tag){
        fragmentManager.beginTransaction().remove(fragmentManager.findFragmentById(R.id.fragmentContainerClient)).commit();
        fragmentManager.beginTransaction()
                .add(R.id.fragmentContainerClient,newFragment,tag)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragmentContainerClient);

        if(currentFragment.getTag().equals(getString(R.string.serverDiscoveryFragment))){
            ServerDiscoveryFragment obj = (ServerDiscoveryFragment) currentFragment;
            obj.stopDiscovery();
            finish();
        }
        else if(currentFragment.getTag().equals(getString(R.string.connectAndShowQuestionsFragment))){
            ConnectAndShowQuestionsFragment obj = (ConnectAndShowQuestionsFragment)currentFragment;
            obj.disconnectFromServer();
            fragmentManager.beginTransaction().remove(currentFragment).commit();
            fragmentManager.beginTransaction().add(R.id.fragmentContainerClient,new ServerDiscoveryFragment(),getString(R.string.serverDiscoveryFragment))
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
        }
    }
}