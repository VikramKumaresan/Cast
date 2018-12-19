package com.example.vikram.cast.serverCode;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.vikram.cast.R;
import com.example.vikram.cast.clientCode.ConnectAndShowQuestionsFragment.ConnectAndShowQuestionsCustomAdapter;
import com.example.vikram.cast.serverCode.PollQuestionsFragment.PollQuestionsData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ShowResultFragment extends Fragment {
    private HashMap<String, HashMap<String,Integer>> voteStore;
    private ArrayList<PollQuestionsData> resultsToShow;
    private ConnectAndShowQuestionsCustomAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prepareResultsToShowArrayList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_show_result, container, false);

        ListView resultList = view.findViewById(R.id.resultList);
        adapter = new ConnectAndShowQuestionsCustomAdapter(resultsToShow,getContext());
        resultList.setAdapter(adapter);

        return view;
    }

    private void prepareResultsToShowArrayList(){
        resultsToShow=new ArrayList<>();

        for(String question: voteStore.keySet()){
            HashMap<String,Integer> answerVotes = voteStore.get(question);
            List<String> answers = new ArrayList<>();

            for(String answer : answerVotes.keySet()){
                answers.add(answer+" - "+answerVotes.get(answer));
            }

            Collections.sort(answers, new Comparator<String>() {
                @Override
                public int compare(String answer1, String answer2) {
                    int votesForAnswer1 = Integer.parseInt(answer1.split("-")[1].trim());
                    int votesForAnswer2 = Integer.parseInt(answer2.split("-")[1].trim());
                    if(votesForAnswer1>votesForAnswer2){
                        return -1;
                    }
                    else if(votesForAnswer1==votesForAnswer2){
                        return 0;
                    }
                    else{
                        return 1;
                    }
                }
            });

            PollQuestionsData obj = new PollQuestionsData(question,answers);
            resultsToShow.add(obj);
        }
    }

    //Setter Funcs
    public void setVoteStore(HashMap<String, HashMap<String,Integer>> voteStore){
        this.voteStore=voteStore;
    }
}