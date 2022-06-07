package org.example.registerlogin;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.util.Log.*;
//TextVeiw 값을 date에 빨리 지정해즈ㅓ야함 아님 계속 중복되어 저장이됨
public class History extends AppCompatActivity {
    EditText contextEditText;
    Button save_Btn, cha_Btn, del_Btn,his_btn;
    TextView diaryTextView, textView2;
    CalendarView calendarView;
    List<historysave> historysaveList;
    DatabaseReference databaseData;
    boolean Boolean;
    String day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        databaseData = FirebaseDatabase.getInstance().getReference("Data");
        historysaveList = new ArrayList<>();
        contextEditText = (EditText) findViewById(R.id.contextEditText);
        diaryTextView = (TextView) findViewById(R.id.diaryTextView);
       // textView2 = (TextView) findViewById(R.id.textView2);
        save_Btn = (Button) findViewById(R.id.save_Btn);
        //cha_Btn = (Button) findViewById(R.id.cha_Btn);
       // del_Btn = (Button) findViewById(R.id.del_Btn);
        calendarView = (CalendarView) findViewById(R.id.calendarView);
         his_btn = (Button) findViewById(R.id.his_btn);


        //Date curDate = new Date(calendarView.getDate());


        // 오늘 날짜를 받게해주는 CalenderInstance
        Calendar c = Calendar.getInstance();
        int cYear = c.get(Calendar.YEAR);
        int cMonth = c.get(Calendar.MONTH);
        int cDay = c.get(Calendar.DAY_OF_MONTH);
        //첫화면에 오늘 날짜 체크된 거 보여줌
       checkedDay(cYear, cMonth, cDay);

      his_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                       historyActivity2.class); // 다음 넘어갈 클래스 지정
                startActivity(intent); // 다음 화면으로 넘어간다
            }
        });


        //Initialize And Assign Variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        //Set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.History);
        //Preform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.Home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.Setting:
                        startActivity(new Intent(getApplicationContext(), Setting.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.History:
                        return true;
                }
                return false;
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarview, int cYear, int cMonth, int cDay) {
                //day = "" + cYear + "-" + (cMonth + 1) + "" + "-" + cDay ;///저장 파일 이름 만들어줌
                //diaryTextView.setText(day);
                diaryTextView.setText(cYear + "년" + (cMonth + 1) + "월" + cDay + "일");
                contextEditText.setText("");//EditText에 공백값 넣
                checkedDay(cYear, cMonth, cDay);
            }
        });

//이름 저장 tv 저장이 안됨
        save_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDiary();
            }
        });
    }

    private void saveDiary() {
        String diary = contextEditText.getText().toString().trim();
        String date = diaryTextView.getText().toString().trim();
        //  save_Btn.setText(selectedDate);
        if (!TextUtils.isEmpty(diary)) {
            String userId = databaseData.push().getKey();
            historysave historysave = new historysave(userId, date, diary);
            databaseData.child(userId).setValue(historysave);
            contextEditText.setText("");
            Toast.makeText(this, date + "내용이 입력되었습니다.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "내용을 입력하세요", Toast.LENGTH_LONG).show();
        }

    }

    private void checkedDay(int cYear, int cMonth, int cDay) {
         diaryTextView.setText(cYear + "-" + (cMonth + 1) + "-" + cDay);//받은 날짜 보여주기
        String date = "" + cYear + "-" + (cMonth + 1) + "" + "-" + cDay ;///저장 파일 이름 만들어줌

            }




            }


