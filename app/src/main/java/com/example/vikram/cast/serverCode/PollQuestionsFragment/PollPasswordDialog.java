package com.example.vikram.cast.serverCode.PollQuestionsFragment;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.Window;
import android.widget.EditText;

import com.example.vikram.cast.R;

public class PollPasswordDialog extends Dialog {

    private Context context;
    private String pollPassword;

    private EditText pollPasswordEditText;

    public PollPasswordDialog(@NonNull Context context) {
        super(context);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_poll_password_dialog);

        Window window = getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT,ActionBar.LayoutParams.WRAP_CONTENT);

        pollPasswordEditText = findViewById(R.id.pollPassword);
    }

    public String getPollPassword() {
        return pollPasswordEditText.getText().toString();
    }
}
