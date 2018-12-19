package com.example.vikram.cast.serverCode;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vikram.cast.R;
import com.example.vikram.cast.serverCode.PollQuestionsFragment.PollQuestions;
import com.example.vikram.cast.serverCode.PollQuestionsFragment.PollQuestionsData;
import com.example.vikram.cast.serverCode.PollQuestionsFragment.QuestionInputDialog;
import com.example.vikram.cast.serverCode.PollQuestionsFragment.QuestionListViewModelStorage;
import com.example.vikram.cast.serverCode.ServerAdvertisementFragment.StartAdvertisementFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ServerActivity extends AppCompatActivity {
    private android.support.v4.app.FragmentManager fragmentManager;
    private Toast message;

    private String pollName;
    private QuestionInputDialog questionInputDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        initializeVariables();
        startPollNameFragment();
    }

    private void initializeVariables(){
        fragmentManager = getSupportFragmentManager();
        message = new Toast(this);
    }

    private void startPollNameFragment(){
        PollNameFragment pollNameFragment = new PollNameFragment();

        //Used to goNext from keyboard
        EditText.OnEditorActionListener listener = new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId==EditorInfo.IME_ACTION_DONE){
                    onNextButtonClicked(null);
                }
                return false;
            }
        };
        pollNameFragment.setEditTextListener(listener);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragmentContainer,pollNameFragment,getString(R.string.pollNameFragment));
        fragmentTransaction.commit();
    }

    public void onNextButtonClicked(View view){
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragmentContainer);

        if(currentFragment.getTag().equals(getString(R.string.pollNameFragment))){
            if(isPollNameValid((PollNameFragment)currentFragment)){
                switchFragment(new PollQuestions(),getString(R.string.setQuestionsFragment),true);
            }
            else{
                showToastMessage("Pls enter a valid name");
            }
        }
        else if(currentFragment.getTag().equals(getString(R.string.setQuestionsFragment))){
            if(isQuestionsSet((PollQuestions)currentFragment)){
                ArrayList<PollQuestionsData> allQuestionAnswers = ((PollQuestions) currentFragment).getAllQuestionAnswers();
                try {
                    String jsonEncodedQuestionAnswers = encodeQuestionAnswersToJson(allQuestionAnswers);
                    StartAdvertisementFragment startAdvertisementFragmentObj= new StartAdvertisementFragment();

                    startAdvertisementFragmentObj.setServerEndpointName(pollName);
                    startAdvertisementFragmentObj.setJsonEncodedQuestionAnswers(jsonEncodedQuestionAnswers);
                    startAdvertisementFragmentObj.prepareVoteStore(allQuestionAnswers);
                    switchFragment(startAdvertisementFragmentObj,getString(R.string.startAdvertisementFragment),true);
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToastMessage("Internal Error - Server JSON Conversion");
                }
            }
            else{
                showToastMessage("Pls enter a question");
            }
        }
        else if(currentFragment.getTag().equals(getString(R.string.startAdvertisementFragment))){
            StartAdvertisementFragment startAdvertisementFragmentObj = (StartAdvertisementFragment)currentFragment;

            ShowResultFragment showResultFragmentObj = new ShowResultFragment();
            showResultFragmentObj.setVoteStore(startAdvertisementFragmentObj.getVoteStore());
            startAdvertisementFragmentObj.stopAdvertisement();
            switchFragment(showResultFragmentObj,getString(R.string.showResultsFragment),false);
        }
        else if(currentFragment.getTag().equals(getString(R.string.showResultsFragment))){
            clearPollQuestionsViewModel();
            finish();
        }
    }

    //PollQuestionsFragment Funcs
    public void onDeleteQuestionClicked(View view){
        //Called from listItem in PollQuestions Fragment
            //Tag set in the adapter for every deleteQuestion ImageView
        PollQuestions fragment =(PollQuestions)fragmentManager.findFragmentById(R.id.fragmentContainer);
        fragment.deleteQuestion((Integer)view.getTag());
    }
    public void onAddQuestionClicked(View view){
        //Show dialog box to input question
        questionInputDialog= new QuestionInputDialog(this);
        questionInputDialog.show();
    }
    public void onAddQuestionDialogButtonClicked(View view){
        //Add Question button in dialog box clicked
        int result = questionInputDialog.isQuestionAnswersValid();

        if(result==questionInputDialog.ERROR_IN_QUESTION){
            showToastMessage("Pls Enter a Valid Question");
        }
        else if(result==questionInputDialog.ERROR_IN_ANSWER){
            showToastMessage("Pls Enter Valid Options");
        }
        else{
            PollQuestions fragment =(PollQuestions)fragmentManager.findFragmentById(R.id.fragmentContainer);
            questionInputDialog.addQuestionToList(fragment);
        }
    }
    private void clearPollQuestionsViewModel(){
        QuestionListViewModelStorage questionListViewModelObj= ViewModelProviders.of(this).get(QuestionListViewModelStorage.class);
        questionListViewModelObj.data.clear();
    }

    //onNextButtonClicked Validation funcs
    private boolean isPollNameValid(PollNameFragment fragmentInstance){
        String proposedTitle = fragmentInstance.getPollName();

        if(proposedTitle.length()>0){
            pollName=proposedTitle;
            return true;
        }
        return false;
    }
    private boolean isQuestionsSet(PollQuestions pollQuestionsInstance){
        if(pollQuestionsInstance.getNumberOfQuestions()==0){
            return false;
        }
        return true;
    }

    //Server Activity Util Funcs
    private void switchFragment(Fragment newFragment,String tag,boolean shouldAddBackToStack){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, newFragment, tag);
        if(shouldAddBackToStack) {
            fragmentTransaction.addToBackStack(null);
        }
        else{
            //Remove current fragment
            fragmentManager.beginTransaction().remove(fragmentManager.findFragmentById(R.id.fragmentContainer)).commit();
        }
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
    }
    private void showToastMessage(String textMessage){
        message.cancel();
        message = Toast.makeText(this,textMessage,Toast.LENGTH_LONG);
        message.show();
    }
    private String encodeQuestionAnswersToJson(ArrayList<PollQuestionsData> questionAnswers) throws JSONException {
        JSONObject jsonObj = new JSONObject();

        for(PollQuestionsData obj : questionAnswers){
            JSONArray answerArray = new JSONArray();

            for(String answer: obj.getAnswerList()){
                answerArray.put(answer);
            }
            jsonObj.put(obj.getQuestion(),answerArray);
        }
        return jsonObj.toString();
    }

    @Override
    public void onBackPressed() {
        if(fragmentManager.getBackStackEntryCount()==0){
            clearPollQuestionsViewModel();
            finish();
            return;
        }

        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragmentContainer);
        if(currentFragment.getTag().equals(getString(R.string.startAdvertisementFragment))){
            StartAdvertisementFragment obj = (StartAdvertisementFragment)currentFragment;
            obj.stopAdvertisement();
        }

        //Remove current fragment
        fragmentManager.beginTransaction().remove(currentFragment).commit();
        fragmentManager.popBackStack();
    }
}