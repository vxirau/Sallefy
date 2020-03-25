package com.prpr.androidpprog2.entregable.controller.activities;


import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.utils.Constants;


public class SettingsActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etEmail;
    private Button btnUsername;
    private Button btnEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initViews();
    }

    void initViews(){

        etUsername = (EditText) findViewById(R.id.textview_settings_change_username);
        etEmail = (EditText) findViewById(R.id.textview_settings_change_email);

        btnUsername =  (Button) findViewById(R.id.update_username_button);
        btnEmail =  (Button) findViewById(R.id.update_email_button);


    }
}

