package com.example.vikram.cast.clientCode.ServerDiscoveryFragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ServerListCustomAdapter extends ArrayAdapter<DiscoveredServer> {
    private Context context;
    private ArrayList<DiscoveredServer> dataSet;

    public ServerListCustomAdapter(@NonNull Context context) {
        super(context, android.R.layout.simple_list_item_1);
        this.context=context;
        dataSet = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        DiscoveredServer obj = getItem(position);

        if(view==null){
            view=LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1,null);
        }

        TextView textView = view.findViewById(android.R.id.text1);
        textView.setText(obj.getServerName());
        return view;
    }
}
