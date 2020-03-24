package com.prpr.androidpprog2.entregable.controller.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.UserCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.UserManager;
import com.prpr.androidpprog2.entregable.model.User;
import com.prpr.androidpprog2.entregable.model.UserToken;

public class SettingsActivity extends AppCompatActivity implements UserCallback {

    private EditText etNewUsername;
    private EditText etNewEmail;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_layout);
        initInfo();

    }
    void initInfo(){


        etNewUsername = findViewById(R.id.textview_settings_change_username);
        etNewUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUsername(etNewUsername.getText().toString());
            }
        });


    }

    private void updateUsername(String username){
        //TODO -> IMPLEMENT AND CALL updateusername() [UserManager + UserService]
        UserManager.getInstance(getApplicationContext()).
    }


    @Override
    public void onLoginSuccess(UserToken userToken) {

    }

    @Override
    public void onLoginFailure(Throwable throwable) {

    }

    @Override
    public void onRegisterSuccess() {

    }

    @Override
    public void onRegisterFailure(Throwable throwable) {

    }

    @Override
    public void onUserInfoReceived(User userData) {

    }

    @Override
    public void onFailure(Throwable throwable) {

    }
}
