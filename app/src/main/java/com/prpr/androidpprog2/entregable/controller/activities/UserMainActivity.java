package com.prpr.androidpprog2.entregable.controller.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.fragment.app.FragmentManager;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.dialogs.ErrorDialog;
import com.prpr.androidpprog2.entregable.controller.fragments.UserFollowedFragment;
import com.prpr.androidpprog2.entregable.controller.fragments.UserPlaylistsFragment;
import com.prpr.androidpprog2.entregable.controller.fragments.UserStatisticsFragment;
import com.prpr.androidpprog2.entregable.controller.fragments.UserTracksFragment;
import com.prpr.androidpprog2.entregable.controller.music.ReproductorService;
import com.prpr.androidpprog2.entregable.model.User;
import com.prpr.androidpprog2.entregable.utils.Constants;

public class UserMainActivity extends AppCompatActivity{

    private TextView tvUserPlaylists;
    private TextView tvUserTracks;
    private TextView tvUserStatistics;
    private TextView tvUserFollowed;
    private User user;
    private FloatingActionButton btnSettingsStatistics;
    private FloatingActionButton btnSettings;
    private View bigView;
    private SpringAnimation springAnimation;
    private int width;
    private int height;
    //----------------------------------------------------------------PART DE SERVICE--------------------------------------------------------------------------------
    private TextView trackTitle;
    private TextView followingTxt;
    private TextView trackAuthor;
    private SeekBar mSeekBar;
    private Button play;
    private Button pause;
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
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!servidorVinculat){
            Intent intent = new Intent(this, ReproductorService.class);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }else{
            serv.setUIControls(mSeekBar, trackTitle, trackAuthor, play, pause, im);
            serv.updateUI();
        }

    }


    //----------------------------------------------------------------FIN DE LA PART DE SERVICE--------------------------------------------------------------------------------



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        getScreenSize();
        initGeneralViews();
        initPlaylistViews();

    }

    void initGeneralViews(){


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
        trackAuthor.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        trackAuthor.setSelected(true);
        trackAuthor.setSingleLine(true);
        trackTitle = findViewById(R.id.dynamic_title);
        trackTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        trackTitle.setSelected(true);
        trackTitle.setSingleLine(true);
        mSeekBar = (SeekBar) findViewById(R.id.dynamic_seekBar);

        ErrorDialog er = new ErrorDialog(this);

        playing = findViewById(R.id.reproductor);
        playing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(serv!=null && !trackTitle.getText().toString().equals("")){
                    Intent intent = new Intent(getApplicationContext(), ReproductorActivity.class);
                    startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
                    overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                }else{
                    er.showErrorDialog("You haven't selected a song yet!");
                }

            }
        });

        bigView = (View) findViewById(R.id.view_big_bar);

        SpringForce springForce = new SpringForce(0).setDampingRatio(SpringForce.DAMPING_RATIO_LOW_BOUNCY)
                .setStiffness(SpringForce.STIFFNESS_MEDIUM);

        springAnimation = new SpringAnimation(bigView, DynamicAnimation.TRANSLATION_X).setSpring(springForce);
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


        tvUserPlaylists = (TextView) findViewById(R.id.user_playlists_title);
        tvUserPlaylists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initPlaylistViews();
            }
        });

        tvUserTracks = (TextView) findViewById(R.id.user_my_songs_title);
        tvUserTracks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initTracksViews();
            }
        });

        tvUserStatistics = (TextView) findViewById(R.id.user_statistics_title);
        tvUserStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initStatisticsViews();
            }
        });

        tvUserFollowed = (TextView) findViewById(R.id.user_my_followed_title);
        tvUserFollowed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initMyFollowedViews();
            }
        });

        btnSettings = (FloatingActionButton) findViewById(R.id.configButton);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

    }

    void getScreenSize(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.width = size.x;
        this.height = size.y;
    }

    void initPlaylistViews(){


        springAnimation.animateToFinalPosition(0);
        UserPlaylistsFragment userPlaylistsFragment = new UserPlaylistsFragment();

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.relativeUserLayout, userPlaylistsFragment, userPlaylistsFragment.getTag())
                .commit();



    }
    void initTracksViews(){

        springAnimation.animateToFinalPosition(((this.width/4))- 30);
        System.out.println(((this.width/4)));


        UserTracksFragment userTracksFragment = new UserTracksFragment();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.relativeUserLayout, userTracksFragment, userTracksFragment.getTag()).commit();

    }

    void initStatisticsViews(){
        springAnimation.animateToFinalPosition(((this.width/4)*2) - 45);
        System.out.println(((this.width/4)*2) - 20);
        UserStatisticsFragment userStatisticsFragment = new UserStatisticsFragment();

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.relativeUserLayout, userStatisticsFragment, userStatisticsFragment.getTag())
                .commit();

    }

    void initMyFollowedViews(){
        springAnimation.animateToFinalPosition(((this.width/4)*3)- 55);
        System.out.println(((this.width/4)*3)- 20);
        UserFollowedFragment userFollowedFragment = new UserFollowedFragment();

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.relativeUserLayout, userFollowedFragment, userFollowedFragment.getTag())
                .commit();

    }


}
