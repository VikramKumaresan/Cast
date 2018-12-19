package com.example.vikram.cast.clientCode.ConnectAndShowQuestionsFragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vikram.cast.R;
import com.example.vikram.cast.serverCode.PollQuestionsFragment.PollQuestionsData;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.tasks.OnFailureListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ConnectAndShowQuestionsFragment extends Fragment {
    private String serverEndpointConnectionId;
    private String clientEndpointName;

    private ConnectionLifecycleCallback connectionManagementCallback;
    private ListView questionList;
    private ConnectAndShowQuestionsCustomAdapter adapter;
    private LoadingDialogBox dialogBoxObj;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeVariables();
        connectToServer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_show_questions, container, false);
        questionList=view.findViewById(R.id.questionListClient);
        return view;
    }

    private void initializeVariables(){
        dialogBoxObj=new LoadingDialogBox(getContext());
        clientEndpointName=Build.MODEL;

        final PayloadCallback payloadCallback = new PayloadCallback() {
            @Override
            public void onPayloadReceived(@NonNull String endpointId, @NonNull Payload payload) {
                Log.d("nearbyConnection","Payload Received from "+endpointId);

                byte[] buffer=payload.asBytes();
                String payloadToString = new String(buffer);

                if(payloadToString.equals(getString(R.string.successMessage))){
                    disconnectFromServer();
                    getActivity().finish();
                    Toast.makeText(getContext(), "Votes Casted Successfully!", Toast.LENGTH_SHORT).show();
                    dialogBoxObj.dismiss();
                }
                else{
                    //Questions received
                    ArrayList<PollQuestionsData> data;
                    try {
                        data = decodeJsonEncodedStringToArrayList(payloadToString);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("nearbyConnection","Decode JSON onReceiveQuestion = "+e.toString());
                        finishActivity("Internal Error - Decode JSON");
                        return;
                    }
                    adapter = new ConnectAndShowQuestionsCustomAdapter(data,getContext());
                    questionList.setAdapter(adapter);
                    dialogBoxObj.dismiss();
                }
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
                Log.d("nearbyConnection","Connection Request From = "+connectionInfo.getEndpointName());
                Nearby.getConnectionsClient(getContext()).acceptConnection(endPointId, payloadCallback);
            }

            @Override
            public void onConnectionResult(@NonNull String endPointId, @NonNull ConnectionResolution result) {
                int statusCode = result.getStatus().getStatusCode();

                if(statusCode==ConnectionsStatusCodes.STATUS_OK){
                    Log.d("nearbyConnection","Connected = "+endPointId);
                }
                else if(statusCode==ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED){
                    Log.d("nearbyConnection","Rejected = "+endPointId);
                    finishActivity("You Have Already Voted");
                }
                else{
                    Log.d("nearbyConnection","Other Code  = "+ConnectionsStatusCodes.getStatusCodeString(statusCode));
                    finishActivity("Server Busy - Try Again Later");
                }
            }

            @Override
            public void onDisconnected(@NonNull String endPointId) {
                Log.d("nearbyConnection","Disconnected = "+endPointId);
                finishActivity("Server Shutdown");
            }
        };
    }

    private void connectToServer(){
        dialogBoxObj.setDialogMessage("Connecting...");
        dialogBoxObj.show();

        Nearby.getConnectionsClient(getContext()).requestConnection(
            clientEndpointName,
            serverEndpointConnectionId,
            connectionManagementCallback).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("nearbyConnection","requestConnection Failed = "+e.toString());
                if(e.getMessage().split(" ")[1].trim().equals("STATUS_ENDPOINT_IO_ERROR")){
                    finishActivity("Server Busy - Try Again Later");
                }
                else{
                    finishActivity("Internal Error - Connection Request Failed");
                }
            }
        });
    }
    public void disconnectFromServer(){
        //Called from onBackPressed in ClientActivity
        Nearby.getConnectionsClient(getContext()).stopAllEndpoints();
        Log.d("nearbyConnection","Disconnected from Server");
    }
    public void submitVotesToServer(){
        String jsonEncodedAnswerString;
        try {
            jsonEncodedAnswerString = encodeAnswersToJsonString();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("nearbyConnection","JSON Encoding  Client Side = "+e.toString());
            finishActivity("Internal Error - JSON Encoding Failed");
            return;
        }
        Nearby.getConnectionsClient(getContext()).sendPayload(serverEndpointConnectionId,Payload.fromBytes(jsonEncodedAnswerString.getBytes()));
        dialogBoxObj.setDialogMessage("Casting Votes...");
        dialogBoxObj.show();
    }

    private void finishActivity(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        dialogBoxObj.dismiss();
        getActivity().onBackPressed();
    }

    private ArrayList<PollQuestionsData> decodeJsonEncodedStringToArrayList(String jsonEncodedString) throws JSONException {
        JSONObject jsonObject =new JSONObject(jsonEncodedString.trim());
        ArrayList<PollQuestionsData> questionAnswersList=new ArrayList<>();
        Iterator<String> keys = jsonObject.keys();

        while(keys.hasNext()){
            String question = keys.next();
            JSONArray answersFromJson = jsonObject.getJSONArray(question);
            List<String> answers = new ArrayList<>();

            for(int i=0;i<answersFromJson.length();i++){
                answers.add(answersFromJson.get(i).toString());
            }

            PollQuestionsData obj = new PollQuestionsData(question,answers);
            questionAnswersList.add(obj);
        }
        return questionAnswersList;
    }
    private String encodeAnswersToJsonString() throws JSONException{

        JSONObject jsonObject = new JSONObject();

        for(int i=0;i<questionList.getChildCount();i++){
            String question= ((TextView)questionList.getChildAt(i).findViewById(R.id.questionClient)).getText().toString();
            String answer= ((Spinner)questionList.getChildAt(i).findViewById(R.id.answersClient)).getSelectedItem().toString();
            jsonObject.put(question,answer);
        }
        return jsonObject.toString();
    }

    public void setServerEndpointConnectionId(String string){serverEndpointConnectionId=string;}
}
