package com.prpr.androidpprog2.entregable.controller.activities;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import java.util.*;

import android.os.IBinder;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gauravk.audiovisualizer.visualizer.CircleLineVisualizer;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.adapters.TrackListAdapter;
import com.prpr.androidpprog2.entregable.controller.callbacks.TrackListCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.PlaylistCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.TrackCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.PlaylistManager;
import com.prpr.androidpprog2.entregable.controller.restapi.service.ReproductorService;
import com.prpr.androidpprog2.entregable.model.Follow;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.utils.Constants;
import com.prpr.androidpprog2.entregable.utils.PreferenceUtils;
import com.prpr.androidpprog2.entregable.utils.Session;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PlaylistActivity extends AppCompatActivity implements TrackCallback, TrackListCallback, PlaylistCallback {

    private Playlist playlst;
    private TextView plyName;
    private TextView plyAuthor;
    private ImageView plyImg;
    private ImageView im;


    private TextView tvTitle;
    private TextView tvAuthor;
    private LinearLayout playing;
    private Button back2Main;
    private Button shuffle;
    private Button follow;
    private Follow followingInfo;
    private boolean isFollowing = false;
    private Button addBunch;
    private SeekBar mseek;


    private Button play;
    private Button pause;

    private RecyclerView mRecyclerView;
    private boolean bunch;
    private ArrayList<Track> mTracks;
    private int currentTrack = 0;
    private CircleLineVisualizer mVisualizer;
    private PlaylistManager pManager;
    private ReproductorService player;
    private boolean trackAssigned = false;
    boolean serviceBound = false;
    public static final String Broadcast_PLAY_NEW_AUDIO = "com.prpr.androidpprog2.entregable.PlayNewAudio";


    @Override
    public void onStart() {
        super.onStart();
        if(serviceBound){
            player.setUIControls(mseek, tvTitle, tvAuthor, play, pause, im);
            player.updateUI();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(serviceBound){
            player.setUIControls(mseek, tvTitle, tvAuthor, play, pause, im);
            player.updateUI();
        }
        pManager.checkFollowing(playlst.getId(), this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_layout);
        if(getIntent().getSerializableExtra("Playlst")!=null){
            playlst = (Playlist) getIntent().getSerializableExtra("Playlst");
        }
        if(getIntent().getSerializableExtra("bunch")!=null){
            bunch = (Boolean) getIntent().getSerializableExtra("bunch");
        }
        initViews();
        pManager = new PlaylistManager(this);
        pManager.checkFollowing(playlst.getId(), this);
        getData();
    }

    private void initViews() {

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.menu);
        navigation.setSelectedItemId(R.id.none);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        finish();
                        overridePendingTransition(R.anim.nothing,R.anim.nothing);
                    case R.id.buscar:
                        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
                        return true;
                    case R.id.perfil:
                        Intent intent2 = new Intent(getApplicationContext(), UserMainActivity.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent2, Constants.NETWORK.LOGIN_OK);
                        return true;
                }
                return false;
            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        playing = findViewById(R.id.reproductor);
        playing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReproductorActivity.class);
                startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
                overridePendingTransition( R.anim.slide_up, R.anim.slide_down );
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.dynamic_recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        TrackListAdapter adapter = new TrackListAdapter(this, this, null, playlst);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(adapter);

        follow = findViewById(R.id.playlistSeguirBoto);
        follow.setEnabled(true);
        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pManager.followPlaylist(playlst.getId(), PlaylistActivity.this);
            }
        });

        plyName = findViewById(R.id.playlistName);
        plyAuthor = findViewById(R.id.playlistAuthor);
        plyImg = findViewById(R.id.playlistCover);

        plyName.setText(playlst.getName());
        if(playlst.getOwner()!=null){
            plyAuthor.setText("Created by " + playlst.getUserLogin());
        }else{
            plyAuthor.setText("Created by admin");
        }

        if (playlst.getThumbnail() != null) {
            Picasso.get().load(playlst.getThumbnail()).into(plyImg);
        }else{
            Picasso.get().load("https://community.spotify.com/t5/image/serverpage/image-id/25294i2836BD1C1A31BDF2/image-size/original?v=mpbl-1&px=-1").into(plyImg);
        }

        play = findViewById(R.id.playButton);
        play.setEnabled(true);
        play.bringToFront();
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index=0;
                player.resumeMedia();
                pause.setVisibility(View.VISIBLE);
                play.setVisibility(View.INVISIBLE);
                trackAssigned=true;
            }
        });
        pause = findViewById(R.id.playPause);
        pause.setEnabled(true);
        pause.bringToFront();
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.pauseMedia();
                play.setVisibility(v.VISIBLE);
                pause.setVisibility(v.INVISIBLE);
            }
        });

        mseek = findViewById(R.id.dynamic_seekBar);

        shuffle = findViewById(R.id.playlistRandom);
        shuffle.setEnabled(true);
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAudio(new Random().nextInt(mTracks.size()));
            }
        });

        addBunch = findViewById(R.id.PlaylistAddSongs);
        addBunch.setEnabled(true);
        addBunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddSongsBunchActivity.class);
                intent.putExtra("Playlst", playlst);
                startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
            }
        });

        if(Session.getInstance(getApplicationContext()).getUser().getLogin().equals(playlst.getOwner().getLogin())){
            addBunch.setVisibility(View.VISIBLE);
            follow.setVisibility(View.GONE);
        }else{
            addBunch.setVisibility(View.INVISIBLE);
            follow.setVisibility(View.VISIBLE);
        }


        back2Main = findViewById(R.id.back2Main);
        back2Main.setEnabled(true);
        back2Main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bunch){
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
                }else{
                    finish();
                    overridePendingTransition(R.anim.nothing,R.anim.nothing);
                }

            }
        });

        tvAuthor = findViewById(R.id.dynamic_artist);
        tvTitle = findViewById(R.id.dynamic_title);
        tvTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        tvTitle.setSelected(true);
        tvTitle.setSingleLine(true);

    }

    private void playAudio(int audioIndex) {
        if (!serviceBound) {
            PreferenceUtils.saveAllTracks(getApplicationContext(), mTracks);
            PreferenceUtils.saveTrackIndex(getApplicationContext(), audioIndex);
            Intent playerIntent = new Intent(this, ReproductorService.class);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            PreferenceUtils.saveTrackIndex(getApplicationContext(), audioIndex);
            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            sendBroadcast(broadcastIntent);
        }
        tvTitle.setText(mTracks.get(audioIndex).getName());
        tvAuthor.setText(mTracks.get(audioIndex).getUserLogin());
    }


    private void getData() {
        mTracks = (ArrayList) playlst.getTracks();
        TrackListAdapter adapter = new TrackListAdapter(this, this, mTracks, playlst);
        mRecyclerView.setAdapter(adapter);
    }


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ReproductorService.LocalBinder binder = (ReproductorService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;
            player.setUIControls(mseek, tvTitle, tvAuthor, play, pause, im);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };



    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("Sallefy", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("Sallefy");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            player.stopSelf();
        }
    }


    @Override
    public void onTracksReceived(List<Track> tracks) {
        mTracks = (ArrayList) tracks;
        PreferenceUtils.saveAllTracks(getApplicationContext(), mTracks);
        TrackListAdapter adapter = new TrackListAdapter(this, this, mTracks, playlst);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onNoTracks(Throwable throwable) {

    }

    @Override
    public void onPersonalTracksReceived(List<Track> tracks) {

    }

    @Override
    public void onUserTracksReceived(List<Track> tracks) {
        playAudio(0);
    }

    @Override
    public void onCreateTrack(Track t) {

    }


    @Override
    public void onFailure(Throwable throwable) {

    }

    @Override
    public void onTrackSelected(int index) {
        pause.setVisibility(View.VISIBLE);
        play.setVisibility(View.INVISIBLE);
        currentTrack = index;
        trackAssigned=true;
        playAudio(index);
    }

    @Override
    public void onTrackAddSelected(int position, ArrayList<Track> tracks, Playlist p) {
        Intent intent = new Intent(getApplicationContext(), InfoTrackActivity.class);
        intent.putExtra("Trck", tracks.get(position));
        intent.putExtra("Playlst", p);
        startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
    }

    @Override
    public void onPlaylistCreated(Playlist playlist) {

    }

    @Override
    public void onPlaylistFailure(Throwable throwable) {

    }

    @Override
    public void onPlaylistRecieved(List<Playlist> playlists) {

    }

    @Override
    public void onNoPlaylists(Throwable throwable) {

    }

    @Override
    public void onPlaylistSelected(Playlist playlist) {

    }

    @Override
    public void onTrackAdded(Playlist body) {

    }

    @Override
    public void onTrackAddFailure(Throwable throwable) {

    }

    @Override
    public void onAllPlaylistRecieved(List<Playlist> body) {

    }

    @Override
    public void onAllNoPlaylists(Throwable throwable) {

    }

    @Override
    public void onAllPlaylistFailure(Throwable throwable) {

    }

    @Override
    public void onTopRecieved(List<Playlist> topPlaylists) {

    }

    @Override
    public void onNoTopPlaylists(Throwable throwable) {

    }

    @Override
    public void onTopPlaylistsFailure(Throwable throwable) {

    }

    @Override
    public void onFollowingRecieved(List<Playlist> body) {

    }

    @Override
    public void onFollowingChecked(Follow body) {
        followingInfo = body;
        if(followingInfo.isFollowing()){
            follow.setText("Following");
            follow.setBackgroundResource(R.drawable.rectangle_small_gborder_green);;
            isFollowing=false;
        }else{
            follow.setText("Follow");
            follow.setBackgroundResource(R.drawable.rectangle_small_gborder_black);;
            isFollowing=true;
        }
    }

    @Override
    public void onFollowSuccessfull(Follow body) {
        followingInfo = body;
        if(followingInfo.isFollowing()){
            follow.setText("Following");
            follow.setBackgroundResource(R.drawable.rectangle_small_gborder_green);;
            isFollowing=false;
        }else{
            follow.setText("Follow");
            follow.setBackgroundResource(R.drawable.rectangle_small_gborder_black);;
            isFollowing=true;
        }
    }
}
