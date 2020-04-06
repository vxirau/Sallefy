package com.prpr.androidpprog2.entregable.controller.activities;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.UserCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.CloudinaryManager;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.UserManager;
import com.prpr.androidpprog2.entregable.model.User;
import com.prpr.androidpprog2.entregable.model.UserToken;
import com.prpr.androidpprog2.entregable.utils.Session;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements UserCallback {

    private EditText etLogin;
    private EditText etPassword;
    private Button btnLogin;
    private CheckBox btnRemember;
    private TextView tvToRegister;
    private UserToken usTkn;
    private String username="";
    //private StorageReference mStorage;


    @Override
    public void onCreate(Bundle savedInstanceSate) {
        super.onCreate(savedInstanceSate);
        setContentView(R.layout.activity_login);
        initViews();
    }

    private void initViews () {

        etLogin = (EditText) findViewById(R.id.login_user);
        etPassword = (EditText) findViewById(R.id.login_password);
        tvToRegister = (TextView) findViewById(R.id.register_btn_action);
        tvToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
            }
        });

        btnRemember = (CheckBox) findViewById(R.id.checkBox);

        final SharedPreferences prefs = getSharedPreferences("RememberMe", Context.MODE_PRIVATE);
        String nickname = prefs.getString("nickname", "");
        String pass = prefs.getString("password", "");
        boolean stateSwitch = prefs.getBoolean("stateSwitch", false);
        btnRemember.setChecked(stateSwitch);
        etLogin.setText(nickname);
        etPassword.setText(pass);

        btnLogin = (Button) findViewById(R.id.login_btn_action);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(btnRemember.isChecked()){

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("nickname", etLogin.getText().toString());
                    editor.putString("password", etPassword.getText().toString());
                    editor.putBoolean("stateSwitch", btnRemember.isChecked());
                    editor.commit();

                } else {

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("nickname","");
                    editor.putString("password", "");
                    editor.putBoolean("stateSwitch", btnRemember.isChecked());
                    editor.commit();

                }

                doLogin(etLogin.getText().toString(), etPassword.getText().toString());
            }
        });
        btnLogin.setEnabled(true);
    }

    private void doLogin(String username, String userpassword) {
        this.username = username;
        UserManager.getInstance(getApplicationContext()).loginAttempt(username, userpassword, LoginActivity.this);
    }

    @Override
    public void onLoginSuccess(UserToken userToken) {

        /*Uri imageUri = (new Uri.Builder())
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(getResources().getResourcePackageName(R.drawable.boto_canviar))
                .appendPath(getResources().getResourceTypeName(R.drawable.boto_canviar))
                .appendPath(getResources().getResourceEntryName(R.drawable.boto_canviar))
                .build();*/

        Uri imageUri = Uri.parse("android.resource://"+getApplicationContext().getPackageName()+"/drawable/cancel_edit");

        /*mStorage = FirebaseStorage.getInstance().getReference();
        StorageReference filePath = mStorage.child(etLogin.getText().toString()).child(imageUri.getLastPathSegment());

        filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(LoginActivity.this,"exito pelotudo",Toast.LENGTH_SHORT).show();
            }
        });*/
        usTkn = userToken;
        UserManager.getInstance(getApplicationContext()).getUserData(username, LoginActivity.this, userToken);
    }

    @Override
    public void onLoginFailure(Throwable throwable) {
        Toast.makeText(getApplicationContext(), "Login failed " + throwable.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRegisterSuccess() {

    }

    @Override
    public void onRegisterFailure(Throwable throwable) {

    }

    @Override
    public void onUserInfoReceived(User userData) {
        Session.getInstance(getApplicationContext()).setUserToken(usTkn);
        Session.getInstance(getApplicationContext()).setUser(userData);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        //prova
        intent.putExtra("sameUser", false);
        //es pot esborrar, es per veure si la meva part esta bé
        startActivity(intent);
    }

    @Override
    public void onUsernameUpdated(User user) {

    }

    @Override
    public void onEmailUpdated(User user) {

    }

    @Override
    public void onTopUsersRecieved(List<User> body) {

    }

    @Override
    public void onUserSelected(User user) {

    }

    @Override
    public void onAllUsersSuccess(List<User> users) {

    }

    @Override
    public void onFollowedUsersSuccess(List<User> users) {

    }


    @Override
    public void onAllUsersFail(Throwable throwable) {

    }

    @Override
    public void onFollowedUsersFail(Throwable throwable) {

    }


    /*@Override
    public void onUsernameUpdated(User user) {

    }

    @Override
    public void onEmailUpdated(User user) {

    }*/

    @Override
    public void onFailure(Throwable throwable) {

    }

}

