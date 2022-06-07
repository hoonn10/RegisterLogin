package org.example.registerlogin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class MyArrayAdapter extends ArrayAdapter<historysave> {
     Context context;
     List<historysave> historysaveList;


    public MyArrayAdapter(@NonNull Activity context, List<historysave> historysaveList) {
        super(context,R.layout.activity_history2 ,historysaveList);
        this.context=context;
        this.historysaveList=historysaveList;



    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View listViewItem = inflater.inflate(R.layout.history_item, null, true);

        TextView DIARY = (TextView) listViewItem.findViewById(R.id.DIARY);
        TextView DATE = (TextView) listViewItem.findViewById(R.id.DATE);

        historysave historysave=historysaveList.get(position);

        DIARY.setText(historysave.getDiary());
        DATE.setText(historysave.getDate());

        return listViewItem;
    }

}
