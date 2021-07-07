package com.funers.ibmiotplatform_mqtt;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class start_page extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        ActionBar actionBar = getSupportActionBar();

        Handler timer = new Handler(); //Handler 생성
        actionBar.hide();

        timer.postDelayed(new Runnable(){ //2초후 쓰레드를 생성하는 postDelayed 메소드
            public void run(){
                Intent intent = new Intent(start_page.this, btn_page.class);
                startActivity(intent); //다음 액티비티 이동
                finish(); // 이 액티비티를 종료
            }
        }, 2000); //2000은 2초를 의미


    }
}