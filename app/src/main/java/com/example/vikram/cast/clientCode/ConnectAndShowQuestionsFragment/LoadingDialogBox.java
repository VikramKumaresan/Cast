package com.example.vikram.cast.clientCode.ConnectAndShowQuestionsFragment;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.example.vikram.cast.R;

public class LoadingDialogBox extends Dialog {
    private Context context;
    private String dialogMessage;

    public LoadingDialogBox(@NonNull Context context) {
        super(context);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_loading_dialog_box);
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        TextView dialogTextView = findViewById(R.id.clientLoadingDialogTextView);
        dialogTextView.setText(dialogMessage);
    }

    void setDialogMessage(String message){
        dialogMessage=message;
    }
}
