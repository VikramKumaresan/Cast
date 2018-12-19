package com.example.vikram.cast.serverCode.PollQuestionsFragment;

import android.support.annotation.NonNull;

import java.util.List;

public class PollQuestionsData {
    private List<String> answerList;
    private String question;

    public PollQuestionsData(String question,List<String> answerList) {
        this.answerList = answerList;
        this.question = question;
    }

    public String getQuestion(){
        return question;
    }

    public List<String> getAnswerList(){
        return answerList;
    }
}
