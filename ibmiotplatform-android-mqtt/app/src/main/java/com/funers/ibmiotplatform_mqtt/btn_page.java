package com.funers.ibmiotplatform_mqtt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class btn_page extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btn_page);
        setActionBar();

        Button convey_page=(Button)findViewById(R.id.convey_btn);
        convey_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(btn_page.this, MainActivity.class);
                startActivity(intent);
                //  finish();
            }
        });
    }

    private void setActionBar(){
        CustomActionBar ca=new CustomActionBar(this, getSupportActionBar());
        ca.setActionBar();
    }
}