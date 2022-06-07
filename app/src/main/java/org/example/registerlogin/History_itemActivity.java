package org.example.registerlogin;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.jar.Attributes;

public class History_itemActivity extends LinearLayout {
    TextView DIARY,DATE;
    public History_itemActivity(Context context) {
        super(context);
        init(context);
    }
    public History_itemActivity(Context context, AttributeSet attrs){
        super(context,attrs);
        init(context);

    }

    private void init(Context context) {
        LayoutInflater inflater=(LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.history_item,this,true);

        DIARY=(TextView)findViewById(R.id.DIARY);
        DATE=(TextView)findViewById(R.id.DATE);


    }
    public void setDIARY(String diary){
        DIARY.setText(diary);

    }
    public void setDATE(String date){
        DATE.setText(date);

    }

}
