package com.example.vikram.cast.serverCode.PollQuestionsFragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vikram.cast.R;

import java.util.ArrayList;

public class QuestionListCustomAdapter extends ArrayAdapter<PollQuestionsData> {
    private Context context;
    private ArrayList<PollQuestionsData> dataSet;

    public QuestionListCustomAdapter(ArrayList<PollQuestionsData> data, @NonNull Context context) {
        super(context, R.layout.questions_list_row, data);
        dataSet=data;
        this.context=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        PollQuestionsData currentDataObj = getItem(position);
        View view = convertView;

        if(view==null){
            view=LayoutInflater.from(context).inflate(R.layout.questions_list_row,null);
        }

        TextView questionTextView = view.findViewById(R.id.question);
        Spinner answerList = view.findViewById(R.id.answers);
        ImageView deleteQuestionButton = view.findViewById(R.id.deleteQuestion);
        deleteQuestionButton.setTag(position);

        questionTextView.setText(currentDataObj.getQuestion());

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_item,
                currentDataObj.getAnswerList()
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        answerList.setAdapter(spinnerAdapter);

        return view;
    }
}
