package com.example.vikram.cast.clientCode.ServerDiscoveryFragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.vikram.cast.R;
import com.example.vikram.cast.clientCode.ClientActivity;
import com.google.android.gms.nearby.Nearby;

import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class ServerDiscoveryFragment extends Fragment {
    private ListView serverList;
    private ServerListCustomAdapter adapter;

    private String pollPassword;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("fragment","onCreate Called!");

        if(pollPassword.equals("")){
            pollPassword= getString(R.string.serviceId);
        }
        startDiscovery();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_server_discovery, container, false);

        serverList=view.findViewById(R.id.serverList);
        adapter=new ServerListCustomAdapter(getContext());
        serverList.setAdapter(adapter);
        ClientActivity.attachItemClickListenerToList(serverList);

        return view;
    }

    private void startDiscovery(){
        EndpointDiscoveryCallback discoveryCallback = new EndpointDiscoveryCallback() {
            @Override
            public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
                Log.d("nearbyConnection","Discovered "+discoveredEndpointInfo.getEndpointName());
                changeServerList(true,discoveredEndpointInfo.getEndpointName(),endpointId);
            }

            @Override
            public void onEndpointLost(@NonNull String endpointId) {
                Log.d("nearbyConnection","Lost Discovery of "+endpointId);
                changeServerList(false,null,endpointId);
            }
        };

        Nearby.getConnectionsClient(getContext()).startDiscovery(
                pollPassword,
                discoveryCallback,
                new DiscoveryOptions(Strategy.P2P_STAR)
        ).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("nearbyConnection","Discovery Started!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Internal Error - Discovery Failed",Toast.LENGTH_LONG).show();
                Log.d("nearbyConnection","Discovery Failed = "+e.toString());
                getActivity().onBackPressed();
            }
        });
    }
    public void stopDiscovery(){
        Log.d("nearbyConnection","Discovery Stopped!");
        Nearby.getConnectionsClient(getContext()).stopDiscovery();
    }

    private synchronized void changeServerList(boolean shouldAddServer,String serverName,String endpointId){
        if(shouldAddServer){
            addServer(serverName,endpointId);
        }
        else{
            removeServer(endpointId);
        }
    }
    private void addServer(String serverName,String endpointId){
        DiscoveredServer server = new DiscoveredServer(serverName,endpointId);
        adapter.add(server);
        adapter.notifyDataSetChanged();
    }
    private void removeServer(String endpointId){
        for(int i=0;i<adapter.getCount();i++){
            if(adapter.getItem(i).getEndpointId().equals(endpointId)){
                adapter.remove(adapter.getItem(i));
                adapter.notifyDataSetChanged();
                return;
            }
        }
    }

    public String getEndpointIdAtPosition(int position){
        return adapter.getItem(position).getEndpointId();
    }

    public void setPollPassword(String pollPassword) {
        this.pollPassword = pollPassword;
    }
}