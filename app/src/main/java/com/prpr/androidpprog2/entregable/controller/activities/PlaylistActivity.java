package com.prpr.androidpprog2.entregable.controller.activities;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import java.util.*;
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
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.utils.Constants;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlaylistActivity extends AppCompatActivity implements TrackCallback, TrackListCallback {

    private static final String TAG = "PlaylistActivity";
    private static final String PLAY_VIEW = "PlayIcon";
    private static final String STOP_VIEW = "StopIcon";

    private Playlist playlst;
    private TextView plyName;
    private TextView plyAuthor;
    private ImageView plyImg;

    private TextView tvTitle;
    private TextView tvAuthor;
    private LinearLayout playing;
    private SeekBar mSeekBar;
    private Button back2Main;
    private Button shuffle;
    private Button addBunch;

    private Handler mHandler;
    private Runnable mRunnable;

    private int mDuration;

    private RecyclerView mRecyclerView;

    private MediaPlayer mPlayer;
    private ArrayList<Track> mTracks;
    private Track nowPlaying;
    private int currentTrack = 0;


    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_layout);
        if(getIntent().getSerializableExtra("Playlst")!=null){
            playlst = (Playlist) getIntent().getSerializableExtra("Playlst");
        }
        initViews();
        getData();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initViews() {
        playing = findViewById(R.id.reproductor);
        playing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReproductorActivity.class);
                intent.putExtra("Trck", nowPlaying);
                mPlayer.stop();
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

        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mSeekBar.setMax(mPlayer.getDuration());
                mDuration =  mPlayer.getDuration();
                mPlayer.start();
                updateSeekBar();
            }
        });

        shuffle = findViewById(R.id.playlistRandom);
        shuffle.setEnabled(true);
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTrack(mTracks.get(new Random().nextInt(mTracks.size())));
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

        mHandler = new Handler();
        tvAuthor = findViewById(R.id.dynamic_artist);
        tvTitle = findViewById(R.id.dynamic_title);
        tvTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        tvTitle.setSelected(true);
        tvTitle.setSingleLine(true);

        mSeekBar = (SeekBar) findViewById(R.id.dynamic_seekBar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mPlayer.seekTo(progress);
                }
                if (mDuration > 0) {
                    int newProgress = ((progress*100)/mDuration);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //---------
    }

    private void prepareMediaPlayer(final String url) {
        Thread connection = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mPlayer.setDataSource(url);
                    mPlayer.prepare();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(),"Error, couldn't play the music\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        connection.start();
    }

    public void updateSeekBar() {
        mSeekBar.setProgress(mPlayer.getCurrentPosition());

        if(mPlayer.isPlaying()) {
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    updateSeekBar();
                }
            };
            mHandler.postDelayed(mRunnable, 1000);
        }
    }

    public void updateTrack(Track track) {
        nowPlaying = track;
        tvAuthor.setText(track.getUserLogin());
        tvTitle.setText(track.getName());

        try {
            mPlayer.reset();
            mPlayer.setDataSource(track.getUrl());
            mPlayer.prepare();
        } catch(Exception e) {

        }
    }


    private void getData() {
        mTracks = (ArrayList) playlst.getTracks();
        TrackListAdapter adapter = new TrackListAdapter(this, this, mTracks, playlst);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onTracksReceived(List<Track> tracks) {
        mTracks = (ArrayList) tracks;
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
        updateTrack(tracks.get(0));
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
        updateTrack(mTracks.get(currentTrack));
    }

    @Override
    public void onTrackAddSelected(int position, ArrayList<Track> tracks, Playlist p) {
        Intent intent = new Intent(getApplicationContext(), InfoTrackActivity.class);
        intent.putExtra("Trck", tracks.get(position));
        intent.putExtra("Playlst", p);
        startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
    }
}
