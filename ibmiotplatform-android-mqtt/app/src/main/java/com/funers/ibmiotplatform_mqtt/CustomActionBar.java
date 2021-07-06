package com.funers.ibmiotplatform_mqtt;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.ActionBar;


public class CustomActionBar {
    private Activity activity;
    private ActionBar actionBar;

    public CustomActionBar(Activity _activity, ActionBar _actionBar){
        this.activity = _activity;
        this.actionBar=_actionBar;
    }


    public void setActionBar(){
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);

        View mCustomView=LayoutInflater.from(activity).inflate(R.layout.actionbar_actions,null);
        actionBar.setCustomView(mCustomView);
    }
}
