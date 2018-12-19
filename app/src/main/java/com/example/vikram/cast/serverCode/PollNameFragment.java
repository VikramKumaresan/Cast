package com.example.vikram.cast.serverCode;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.vikram.cast.R;


public class PollNameFragment extends Fragment {
    static EditText.OnEditorActionListener editTextListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_poll_name, container, false);

        EditText pollNameEditText=view.findViewById(R.id.pollTitle);
        attachListener(pollNameEditText,editTextListener);

        return view;
    }

    String getPollName(){
        EditText titleEditText = getView().findViewById(R.id.pollTitle);
        return titleEditText.getText().toString().trim();
    }
    void setEditTextListener(EditText.OnEditorActionListener editTextListener) {
        PollNameFragment.editTextListener = editTextListener;
    }
    void attachListener(EditText editText, EditText.OnEditorActionListener listener){
        editText.setOnEditorActionListener(listener);
    }
}