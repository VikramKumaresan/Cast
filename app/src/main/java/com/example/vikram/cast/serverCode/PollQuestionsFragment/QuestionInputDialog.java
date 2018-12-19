package com.example.vikram.cast.serverCode.PollQuestionsFragment;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.example.vikram.cast.R;

import java.util.ArrayList;
import java.util.Arrays;

public class QuestionInputDialog extends Dialog {
    public int ERROR_IN_QUESTION = -1;
    public int ERROR_IN_ANSWER = -2;

    private Context context;

    private EditText question;
    private EditText answerList;

    private PollQuestionsData newQuestionAnswers;

    public QuestionInputDialog(@NonNull Context context) {
        super(context);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_question_input_dialog);

        Window window = getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT,ActionBar.LayoutParams.WRAP_CONTENT);

        question=findViewById(R.id.inputQuestion);
        answerList=findViewById(R.id.inputAnswers);
    }

    public int isQuestionAnswersValid(){
        if(question.getText().toString().length()==0){
            return ERROR_IN_QUESTION;
        }

        String[] answers=answerList.getText().toString().split(",");

        if(answers.length==0){
            return ERROR_IN_ANSWER;
        }

        for(String answer : answers){
            if(answer.trim().length()==0){
                return ERROR_IN_ANSWER;
            }
        }

        //Validation Passed
        ArrayList<String> answersArrayList = new ArrayList<>(Arrays.asList(answers));
        newQuestionAnswers=new PollQuestionsData(question.getText().toString(),answersArrayList);

        return 1;
    }

    public void addQuestionToList(PollQuestions obj){
        obj.addQuestionAnswers(newQuestionAnswers);
        dismiss();
    }
}
