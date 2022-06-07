package org.example.registerlogin;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

//각 id별로 가지고 있는 데이터를 읽거나 저장할 때 사용하는 용도로 클래스를 생성.데이터를 잠시 담아두는 역
public class historysave {
    private String userId;
    private String date;
    private String diary;


    public historysave(){
        // DataSnapshot.getValue(FirebasePost.class) 호출에 필요한 기본 생성자

    }
    public historysave(String userId,String date,String diary){
        this.userId=userId;
        this.diary=diary;
        this.date=date;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId=userId;
    }


    public String getDiary() {
        return diary;
    }

    public void setDiary(String diary) {
        this.diary=diary;
    }



    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date=date;
    }


}

