package org.example.registerlogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

public class Setting extends AppCompatActivity implements View.OnClickListener {
    Button logout_btn;
    //언어 설정
    private Button btn_en, btn_ko,btn_ja;

    private Locale myLocale;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        //언어 정보 불러오기
        loadLocale();


        this.btn_en = (Button) findViewById(R.id.eng);
        this.btn_ko = (Button) findViewById(R.id.korea);
        this.btn_ja = (Button) findViewById(R.id.jap);
        this.btn_ja.setOnClickListener(this);
        this.btn_en.setOnClickListener(this);
        this.btn_ko.setOnClickListener(this);

        logout_btn = (Button) findViewById(R.id.logout_btn);



        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences setting = getSharedPreferences("setting", 0);
                SharedPreferences.Editor editor = setting.edit();
                editor.clear();
                editor.commit();
                Toast.makeText(getApplicationContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Setting.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
            }
        });

        //Initialize And Assign Variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        //Set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.Setting);
        //Preform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.History:
                        startActivity(new Intent(getApplicationContext(), History.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.Home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.Setting:
                        return true;
                }
                return false;
            }
        });
    }
    //언어 설정
        @Override
        public void onClick (View v){
            String lang = "ko";
            switch (v.getId()) {
                case R.id.eng:
                    lang = "en";
                    break;
                case R.id.korea:
                    lang = "ko";
                    break;
                case R.id.jap:
                    lang = "ja";
                    break;
                default:
                    break;
            }
            changeLang(lang);
            Intent intent = getIntent();
            finish();//한번 닫고 다시 실행
            startActivity(intent);

        }
        public void loadLocale ()
        {
            Log.e("A", "loadLocale 실행");
            String langPref = "Language";
            SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
            String language = prefs.getString(langPref, "");
            changeLang(language);
        }
        public void saveLocale (String lang)
        {
            Log.e("A", "saveLocale 실행");
            String langPref = "Language";
            SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(langPref, lang);
            editor.commit();
        }
        public void changeLang (String lang)
        {
            if (lang.equalsIgnoreCase(""))
                return;
            Log.e("A", "changeLang 실행");
            myLocale = new Locale(lang);
            saveLocale(lang);
            Locale.setDefault(myLocale);
            android.content.res.Configuration config = new android.content.res.Configuration();
            config.locale = myLocale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
            //  updateTexts();
        }

        @Override
        public void onConfigurationChanged (android.content.res.Configuration newConfig){
            super.onConfigurationChanged(newConfig);
            if (myLocale != null) {
                newConfig.locale = myLocale;
                Locale.setDefault(myLocale);
                getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
            }
        }

    }

