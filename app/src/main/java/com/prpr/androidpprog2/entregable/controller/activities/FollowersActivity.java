package com.prpr.androidpprog2.entregable.controller.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.model.User;

import java.util.ArrayList;

public class FollowersActivity extends AppCompatActivity {

    private ArrayList<User> followers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        followers = (ArrayList<User>) getIntent().getSerializableExtra("followers");



    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
    }

}
