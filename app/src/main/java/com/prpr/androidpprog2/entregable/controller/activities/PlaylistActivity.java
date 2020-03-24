package com.prpr.androidpprog2.entregable.controller.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import java.util.*;

import android.os.IBinder;
import android.os.StrictMode;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.adapters.TrackListAdapter;
import com.prpr.androidpprog2.entregable.controller.callbacks.TrackListCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.TrackCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.TrackManager;
import com.prpr.androidpprog2.entregable.controller.restapi.service.ReproductorService;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.utils.Constants;
import com.prpr.androidpprog2.entregable.utils.PreferenceUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlaylistActivity extends AppCompatActivity implements TrackCallback, TrackListCallback {

    private Playlist playlst;
    private TextView plyName;
    private TextView plyAuthor;
    private ImageView plyImg;

    private TextView tvTitle;
    private TextView tvAuthor;
    private LinearLayout playing;
    private Button back2Main;
    private Button shuffle;
    private Button addBunch;
    private SeekBar mseek;


    private RecyclerView mRecyclerView;

    private ArrayList<Track> mTracks;
    private int currentTrack = 0;

    private ReproductorService player;
    boolean serviceBound = false;
    public static final String Broadcast_PLAY_NEW_AUDIO = "com.prpr.androidpprog2.entregable.PlayNewAudio";


    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_layout);
        if(getIntent().getSerializableExtra("Playlst")!=null){
            playlst = (Playlist) getIntent().getSerializableExtra("Playlst");
        }
        initViews();
        getData();

        playAudio(new Random().nextInt(mTracks.size()));
    }

    private void initViews() {
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

        back2Main = findViewById(R.id.back2Main);
        back2Main.setEnabled(true);
        back2Main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
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
            player.setUIControls(mseek, tvTitle, tvAuthor);
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
        currentTrack = index;
        playAudio(index);
    }

    @Override
    public void onTrackAddSelected(int position, ArrayList<Track> tracks, Playlist p) {
        Intent intent = new Intent(getApplicationContext(), InfoTrackActivity.class);
        intent.putExtra("Trck", tracks.get(position));
        intent.putExtra("Playlst", p);
        startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
    }
}
