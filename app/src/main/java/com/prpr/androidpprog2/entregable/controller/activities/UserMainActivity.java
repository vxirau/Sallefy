package com.prpr.androidpprog2.entregable.controller.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
import androidx.appcompat.app.AppCompatActivity;


import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.adapters.PlaylistAdapter;
import com.prpr.androidpprog2.entregable.controller.adapters.TrackListAdapter;
import com.prpr.androidpprog2.entregable.controller.adapters.UserAdapter;
import com.prpr.androidpprog2.entregable.controller.adapters.UserPlaylistAdapter;
import com.prpr.androidpprog2.entregable.controller.callbacks.ServiceCallback;
import com.prpr.androidpprog2.entregable.controller.callbacks.TrackListCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.PlaylistCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.TrackCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.PlaylistManager;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.TrackManager;
import com.prpr.androidpprog2.entregable.controller.restapi.service.ReproductorService;
import com.prpr.androidpprog2.entregable.model.Follow;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.model.User;
import com.prpr.androidpprog2.entregable.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class UserMainActivity extends AppCompatActivity implements ServiceCallback {

    private TextView tvUserPlaylists;
    private TextView tvUserTracks;
    private TextView tvUserStatistics;
    private TextView tvUserFollowed;





    private FloatingActionButton btnSettingsStatistics;
    private FloatingActionButton btnSettingsFollowed;

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
           /* ReproductorService.LocalBinder binder = (ReproductorService.LocalBinder) service;
            serv = binder.getService();
            //serv.setmSeekBar(mSeekBar);
            servidorVinculat = true;
            serv.setUIControls(mSeekBar, trackTitle, trackAuthor, play, pause, im);
            serv.setSeekCallback(UserMainActivity.this);*/
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //servidorVinculat = false;
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
        /*if(!servidorVinculat){
            Intent intent = new Intent(this, ReproductorService.class);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }else{
            serv.setUIControls(mSeekBar, trackTitle, trackAuthor, play, pause, im);
            serv.updateUI();
            serv.setSeekCallback(this);
        }*/
    }

    @Override
    public void onResume() {
        super.onResume();
        /*if(servidorVinculat){
            serv.setSeekCallback(this);
        }*/
    }


    @Override
    public void onSeekBarUpdate(int progress, int duration, boolean isPlaying) {
        /*if(isPlaying){
            mSeekBar.postDelayed(serv.getmProgressRunner(), 1000);
        }
        mSeekBar.setProgress(progress);*/
    }

    //----------------------------------------------------------------FIN DE LA PART DE SERVICE--------------------------------------------------------------------------------



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        initGeneralViews();
        initPlaylistViews();

    }

    void initGeneralViews(){




        playing = findViewById(R.id.reproductor);
        playing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReproductorActivity.class);
                startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
            }
        });*/


       /*BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.menu);
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
        });*/


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




    }

    void initPlaylistViews(){

        UserPlaylistsFragment userPlaylistsFragment = new UserPlaylistsFragment();

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.relativeUserLayout, userPlaylistsFragment, userPlaylistsFragment.getTag())
                .commit();





    }
    void initTracksViews(){

        UserTracksFragment userTracksFragment = new UserTracksFragment();

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.relativeUserLayout, userTracksFragment, userTracksFragment.getTag())
                .commit();



    }

    void initStatisticsViews(){

        UserStatisticsFragment userStatisticsFragment = new UserStatisticsFragment();

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.relativeUserLayout, userStatisticsFragment, userStatisticsFragment.getTag())
                .commit();

    }

    void initMyFollowedViews(){

        UserFollowedFragment userFollowedFragment = new UserFollowedFragment();

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.relativeUserLayout, userFollowedFragment, userFollowedFragment.getTag())
                .commit();

    }


}
