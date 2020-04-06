package com.prpr.androidpprog2.entregable.controller.activities;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;


import android.os.IBinder;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.callbacks.ServiceCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.UserCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.UserManager;
import com.prpr.androidpprog2.entregable.controller.restapi.service.ReproductorService;
import com.prpr.androidpprog2.entregable.model.User;
import com.prpr.androidpprog2.entregable.model.UserToken;
import com.prpr.androidpprog2.entregable.utils.Constants;

import java.util.List;


public class SettingsActivity extends AppCompatActivity implements UserCallback, ServiceCallback {

    private EditText etUsername;
    private EditText etEmail;

    private Button btnUsername;
    private Button btnEmail;

    private User myUser;

    private UserManager userManager;


    //----------------------------------------------------------------PART DE SERVICE--------------------------------------------------------------------------------
    private TextView trackTitle;
    private TextView followingTxt;
    private TextView trackAuthor;
    private SeekBar mSeekBar;
    private Button play;
    private Button pause;
    private Button btnBack;
    private ImageView im;
    private LinearLayout playing;
    private ReproductorService serv;
    private boolean servidorVinculat=false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ReproductorService.LocalBinder binder = (ReproductorService.LocalBinder) service;
            serv = binder.getService();
            //serv.setmSeekBar(mSeekBar);
            servidorVinculat = true;
            serv.setUIControls(mSeekBar, trackTitle, trackAuthor, play, pause, im);
            serv.setSeekCallback(SettingsActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            servidorVinculat = false;
        }
    };

    void doUnbindService() {
        if (servidorVinculat) {
            unbindService(serviceConnection);
            servidorVinculat = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }


    @Override
    public void onStart() {
        super.onStart();
        if(!servidorVinculat){
            Intent intent = new Intent(this, ReproductorService.class);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }else{
            serv.setUIControls(mSeekBar, trackTitle, trackAuthor, play, pause, im);
            serv.updateUI();
            serv.setSeekCallback(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(servidorVinculat){
            serv.setSeekCallback(this);
        }
    }


    @Override
    public void onSeekBarUpdate(int progress, int duration, boolean isPlaying, String duracio) {
        if(isPlaying){
            mSeekBar.postDelayed(serv.getmProgressRunner(), 1000);
        }
        mSeekBar.setProgress(progress);
    }



    //----------------------------------------------------------------FIN DE LA PART DE SERVICE--------------------------------------------------------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initViews();

    }

    void initViews(){

        play = findViewById(R.id.playButton);
        play.setEnabled(true);
        play.bringToFront();
        play.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                serv.resumeMedia();
            }
        });

        pause = findViewById(R.id.playPause);
        pause.setEnabled(true);
        pause.bringToFront();
        pause.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                serv.pauseMedia();
            }
        });

        trackAuthor = findViewById(R.id.dynamic_artist);
        trackTitle = findViewById(R.id.dynamic_title);
        trackTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        trackTitle.setSelected(true);
        trackTitle.setSingleLine(true);
        mSeekBar = (SeekBar) findViewById(R.id.dynamic_seekBar);

        playing = findViewById(R.id.reproductor);
        playing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReproductorActivity.class);
                startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
            }
        });

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

        btnBack = findViewById(R.id.back2User);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UserMainActivity.class);
                startActivity(intent);

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


    @Override
    public void onFailure(Throwable throwable) {

    }
}

