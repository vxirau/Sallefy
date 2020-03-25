package com.prpr.androidpprog2.entregable.controller.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    private User user;
    private Button btnUpdateUsername;
    private Button btnUpdateEmail;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_layout);
        getData();
        initInfo();

    }
    void initInfo(){

        etNewUsername = findViewById(R.id.textview_settings_change_username);
        etNewEmail = findViewById(R.id.textview_settings_change_email);

        btnUpdateUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doUpdateUsername(user);
            }
        });

        btnUpdateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doUpdateEmail(user);
            }
        });

    }

    private void doUpdateUsername(User user){
        //TODO -> IMPLEMENT AND CALL updateusername() [UserManager + UserService]
        UserManager.getInstance(getApplicationContext()).updateUsername(user, this);
    }

    private void doUpdateEmail(User user){

        UserManager.getInstance(getApplicationContext()).updateEmail(user, this);
    }

    private void getData(){ //UserManager.getInstance(this).
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
    public void onUsernameUpdated(User user) {

    }

    @Override
    public void onEmailUpdated(User user) {

    }


    @Override
    public void onFailure(Throwable throwable) {

    }
}
