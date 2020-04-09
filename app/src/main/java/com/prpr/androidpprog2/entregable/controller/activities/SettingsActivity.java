package com.prpr.androidpprog2.entregable.controller.activities;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;


import android.os.IBinder;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.CursorAnchorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.adapters.TrackListAdapter;
import com.prpr.androidpprog2.entregable.controller.callbacks.ServiceCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.UserCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.UserManager;
import com.prpr.androidpprog2.entregable.controller.restapi.service.ReproductorService;
import com.prpr.androidpprog2.entregable.model.Follow;
import com.prpr.androidpprog2.entregable.model.User;
import com.prpr.androidpprog2.entregable.model.UserToken;
import com.prpr.androidpprog2.entregable.utils.Constants;
import com.prpr.androidpprog2.entregable.utils.Session;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class SettingsActivity extends AppCompatActivity implements UserCallback, ServiceCallback {


    private static final int UPLOAD_IMAGE = 1;



    private EditText etFirstName;
    private EditText etLastName;
    private EditText etEmail;

    private ImageButton imgBtnUserPic;

    private Button btnUpdate;
    private Button btnLogOut;

    private Boolean pictureUpdated;

    private ScrollView settingsScrollView;
    private User myUser;

    private UserManager userManager;

    private LoginActivity LoginActivity;
    private Context context;
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
            context = getApplicationContext();
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
        myUser = Session.getUser();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        initViews();

    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case UPLOAD_IMAGE:
                    Uri selectedImage = data.getData();
                    imgBtnUserPic.setImageURI(selectedImage);
                    pictureUpdated = true;
                    break;
            }
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
        pictureUpdated = false;
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
                intent.putExtra("UserInfo", myUser);

            }
        });


        etFirstName = (EditText) findViewById(R.id.textview_settings_change_first_name);

        etLastName = (EditText) findViewById(R.id.textview_settings_change_last_name);

        etEmail = (EditText) findViewById(R.id.textview_settings_change_email);

        btnUpdate = findViewById(R.id.update_button);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    doUpdateUser();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });


        btnLogOut =  (Button) findViewById(R.id.log_out_button);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                new AlertDialog.Builder(SettingsActivity.this)
                        .setMessage("Do you really want to log out?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                doLogOut();

                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });





        imgBtnUserPic = findViewById(R.id.userImage);
        if(myUser.getImageUrl()!=null){
            Picasso.get().load(myUser.getImageUrl()).into(imgBtnUserPic);
        }else{
            Picasso.get().load("https://community.spotify.com/t5/image/serverpage/image-id/25294i2836BD1C1A31BDF2/image-size/original?v=mpbl-1&px=-1").into(imgBtnUserPic);
        }
        imgBtnUserPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseFile();

            }
        });

        settingsScrollView = findViewById(R.id.settings_scrollview);

    }

    private void chooseFile(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, UPLOAD_IMAGE);
    }

    private void doLogOut(){

        Toast.makeText(SettingsActivity.this, "You logged out succesfully", Toast.LENGTH_SHORT).show();
        Session.getInstance(this).resetValues();
        SharedPreferences preferences = getSharedPreferences("RememberMe",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        finish();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.putExtra("LoggedOut", true);
        startActivity(intent);

    }
    private void doUpdateUser() throws MalformedURLException {


        System.out.println("viejo" + myUser.getFirstName());
        if(etFirstName.getText().length() > 0 || etFirstName.getText() != null){
            this.myUser.setFirstName(etFirstName.getText().toString());
        }
        if(etLastName.getText().length() > 0 || etLastName.getText() != null){
            this.myUser.setLastName(etLastName.getText().toString());
        }
        if(etEmail.getText().length() > 0 || etEmail.getText() != null){
            this.myUser.setEmail(etEmail.getText().toString());
        }
        System.out.println("nuevo" + myUser.getId());
        if(pictureUpdated){
            this.myUser.setImageUrl(imgBtnUserPic.toString());
        }

        userManager = new UserManager(this);
        userManager.updateUser(myUser, this);
        System.out.println("after update" + myUser.getId());

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

        this.myUser = userData;


    }

    @Override
    public void onUserUpdated(User body) {
        finish();
    }



    @Override
    public void onTopUsersRecieved(List<User> body) {

    }

    @Override
    public void onUserUpdateFailure(Throwable throwable) {

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
    public void onFollowSuccess(Follow body) {

    }

    @Override
    public void onFollowFailure(Throwable throwable) {

    }

    @Override
    public void onCheckSuccess(Follow body) {

    }

    @Override
    public void onCheckFailure(Throwable throwable) {

    }


    @Override
    public void onFailure(Throwable throwable) {

    }
}

