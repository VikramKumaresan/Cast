package com.example.vikram.cast.serverCode.PollQuestionsFragment;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.vikram.cast.R;

import java.util.ArrayList;
import java.util.List;


public class PollQuestions extends Fragment {
    ListView questionList;
    QuestionListCustomAdapter adapter;
    QuestionListViewModelStorage questionListViewModelObj;
    ArrayList<PollQuestionsData> data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_poll_questions, container, false);
        questionList = view.findViewById(R.id.questionList);

        questionListViewModelObj= ViewModelProviders.of(this).get(QuestionListViewModelStorage.class);
        data = questionListViewModelObj.data;
        if(data.size()==0) {
            //Sample Question
            ArrayList<String> sampleAnswers = new ArrayList<String>();
            sampleAnswers.add("Option 1");
            sampleAnswers.add("Option 2");
            sampleAnswers.add("Option 3");
            sampleAnswers.add("Option 4");
            PollQuestionsData sampleQuestionObj = new PollQuestionsData("This is a sample question?", sampleAnswers);
            data.add(sampleQuestionObj);
        }

        adapter = new QuestionListCustomAdapter(data,getContext());
        questionList.setAdapter(adapter);

        return view;
    }

    public void deleteQuestion(int position){
        adapter.remove(adapter.getItem(position));
        adapter.notifyDataSetChanged();
    }

    public void addQuestionAnswers(PollQuestionsData newData){
        adapter.add(newData);
        adapter.notifyDataSetChanged();
    }

    public int getNumberOfQuestions(){
        return adapter.getCount();
    }

    public ArrayList<PollQuestionsData> getAllQuestionAnswers(){
        ArrayList<PollQuestionsData> allQuestionsAnswers=new ArrayList<>();

        for(int i=0;i<adapter.getCount();i++){
            PollQuestionsData questionAnswers = adapter.getItem(i);
            allQuestionsAnswers.add(questionAnswers);
        }
        return allQuestionsAnswers;
    }

}