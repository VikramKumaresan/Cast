package com.example.vikram.cast.serverCode.ServerAdvertisementFragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vikram.cast.R;
import com.example.vikram.cast.serverCode.PollQuestionsFragment.PollQuestionsData;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class StartAdvertisementFragment extends Fragment {
    private String serverEndpointName;
    private byte[] successMessage;
    private byte[] jsonEncodedQuestionAnswers;
    private ConnectionLifecycleCallback connectionManagementCallback;
    private HashMap<String, HashMap<String,Integer>> voteStore;
    private HashMap<String,Boolean> hasVoted;

    TextView numberOfEntriesTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeVariables();
        startAdvertisement();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_start_advertisement, container, false);
        numberOfEntriesTextView = view.findViewById(R.id.numberOfEntriesTextView);
        return view;
    }

    private void startAdvertisement(){
        Nearby.getConnectionsClient(getContext()).startAdvertising(
                serverEndpointName,
                getString(R.string.serviceId),
                connectionManagementCallback,
                new AdvertisingOptions(Strategy.P2P_STAR)
        ).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("nearbyConnection","Advertisment Started!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("nearbyConnection","Advertisment Failed = "+e.toString());
                Toast.makeText(getContext(),"Internal Error - Advertisement Failed",Toast.LENGTH_LONG).show();
                getActivity().onBackPressed();
            }
        });
    }
    private void initializeVariables(){

        hasVoted = new HashMap<>();
        successMessage=getString(R.string.successMessage).getBytes();

        final Context context=getContext();

        final PayloadCallback payloadCallback = new PayloadCallback() {
            @Override
            public void onPayloadReceived(@NonNull String endpointId, @NonNull Payload payload) {
                Log.d("nearbyConnection","Answers Received from "+endpointId);
                if(!hasVoted.containsKey(endpointId)){
                    hasVoted.put(endpointId,true);
                    try {
                        updateVoteStore(payload);
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.d("nearbyConnection","Error in votesStore update = "+e.toString());
                    }
                    updateNumberOfEntriesTextView();
                }
                Nearby.getConnectionsClient(context).sendPayload(endpointId,Payload.fromBytes(successMessage));
            }

            @Override
            public void onPayloadTransferUpdate(@NonNull String endpointId, @NonNull PayloadTransferUpdate payloadTransferUpdate) {
                if(payloadTransferUpdate.getStatus()==PayloadTransferUpdate.Status.SUCCESS){
                    Log.d("nearbyConnection","Payload Status = SUCCESS from "+endpointId);
                }
            }
        };

        connectionManagementCallback = new ConnectionLifecycleCallback() {
            @Override
            public void onConnectionInitiated(@NonNull String endPointId, @NonNull ConnectionInfo connectionInfo) {
                if(hasVoted.containsKey(endPointId)){
                    Log.d("nearbyConnection","Connection Request From [Rejected] = "+connectionInfo.getEndpointName());
                    Nearby.getConnectionsClient(context).rejectConnection(endPointId);
                }
                else{
                    Log.d("nearbyConnection","Connection Request From [Accepted] = "+connectionInfo.getEndpointName());
                    Nearby.getConnectionsClient(context).acceptConnection(endPointId, payloadCallback);
                }
            }

            @Override
            public void onConnectionResult(@NonNull String endPointId, @NonNull ConnectionResolution result) {
                int statusCode = result.getStatus().getStatusCode();

                if(statusCode==ConnectionsStatusCodes.STATUS_OK){
                    Log.d("nearbyConnection","Connected = "+endPointId);

                    //Send Questions to client
                    Payload questionPayload = Payload.fromBytes(jsonEncodedQuestionAnswers);
                    Nearby.getConnectionsClient(context).sendPayload(endPointId,questionPayload);
                }
                else{
                    Log.d("nearbyConnection","Rejected  = "+endPointId);
                }
            }

            @Override
            public void onDisconnected(@NonNull String endPointId) {
                Log.d("nearbyConnection","Disconnected = "+endPointId);
            }
        };
    }

    private void updateVoteStore(Payload answerPayload) throws Exception {
        byte[] buffer = answerPayload.asBytes();
        JSONObject questionAnswer = new JSONObject(new String(buffer));

        Iterator<String> questions = questionAnswer.keys();

        while(questions.hasNext()){
            String question = questions.next().trim();
            String answer = questionAnswer.getString(question).trim();

            HashMap<String,Integer> votesForAnswer = voteStore.get(question);
            int currentVotes = votesForAnswer.get(answer);
            votesForAnswer.put(answer,++currentVotes);
        }

    }
    private synchronized void updateNumberOfEntriesTextView(){
        int currentNumber = Integer.parseInt(numberOfEntriesTextView.getText(). toString());
        numberOfEntriesTextView.setText(String.valueOf(++currentNumber));
    }

    public void stopAdvertisement(){
        Log.d("nearbyConnection","Stopped Advertising + Disconnected From Clients");
        Nearby.getConnectionsClient(getContext()).stopAdvertising();
        Nearby.getConnectionsClient(getContext()).stopAllEndpoints();
    }

    //Getter Funcs
    public HashMap<String,HashMap<String,Integer>> getVoteStore(){
        return voteStore;
    }

    //Setter Funcs
    public void setJsonEncodedQuestionAnswers(String string){
        jsonEncodedQuestionAnswers=string.getBytes();
    }
    public void setServerEndpointName(String string){
        serverEndpointName=string;
    }
    public void prepareVoteStore( ArrayList<PollQuestionsData> allQuestionAnswers){
        voteStore = new HashMap<>();

        for(PollQuestionsData questionAnswer : allQuestionAnswers){
            HashMap<String,Integer> innnerHashMap = new HashMap<>();
            for(String answer : questionAnswer.getAnswerList()){
                innnerHashMap.put(answer,0);
            }
            voteStore.put(questionAnswer.getQuestion(),innnerHashMap);
        }

    }
}