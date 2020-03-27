package com.prpr.androidpprog2.entregable.controller.activities;


import android.content.Intent;
import android.os.Bundle;


import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.UserCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.UserManager;
import com.prpr.androidpprog2.entregable.model.User;
import com.prpr.androidpprog2.entregable.model.UserToken;
import com.prpr.androidpprog2.entregable.utils.Constants;

import java.util.List;


public class SettingsActivity extends AppCompatActivity implements UserCallback {

    private EditText etUsername;
    private EditText etEmail;

    private Button btnUsername;
    private Button btnEmail;

    private User myUser;

    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initViews();

    }

    void initViews(){

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.menu);
        navigation.setSelectedItemId(R.id.perfil);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
                        return true;
                    case R.id.buscar:
                        Intent intent2 = new Intent(getApplicationContext(), SearchActivity.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent2, Constants.NETWORK.LOGIN_OK);
                        return true;
                    case R.id.perfil:
                        return true;
                }
                return false;
            }
        });



        etUsername = (EditText) findViewById(R.id.textview_settings_change_username);
        etEmail = (EditText) findViewById(R.id.textview_settings_change_email);

        btnUsername =  (Button) findViewById(R.id.update_username_button);
        btnUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doUpdateUsername(myUser);

            }
        });

        btnEmail =  (Button) findViewById(R.id.update_email_button);


    }

    private void doUpdateUsername(User user){
        userManager = new UserManager(this);
        userManager.updateUsername(user, this);
    }

    private void doUpdateEmail(User user){
        userManager = new UserManager(this);
        userManager.updateEmail(user, this);
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
        this.myUser.setLogin(user.getLogin());
    }

    @Override
    public void onEmailUpdated(User user) {
        this.myUser.setEmail(user.getEmail());
    }

    @Override
    public void onTopUsersRecieved(List<User> body) {

    }

    @Override
    public void onUserSelected(User user) {

    }


    @Override
    public void onFailure(Throwable throwable) {

    }
}

