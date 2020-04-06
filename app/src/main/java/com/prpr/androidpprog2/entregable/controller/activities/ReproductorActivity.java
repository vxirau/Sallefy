package com.prpr.androidpprog2.entregable.controller.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.chibde.visualizer.CircleBarVisualizer;
import com.gauravk.audiovisualizer.visualizer.CircleLineVisualizer;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.callbacks.ServiceCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.TrackCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.TrackManager;
import com.prpr.androidpprog2.entregable.controller.restapi.service.ReproductorService;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.utils.Constants;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

public class ReproductorActivity extends Activity implements ServiceCallback, TrackCallback {

    private static final String TAG = "DynamicPlaybackActivity";
    private static final String PLAY_VIEW = "PlayIcon";
    private static final String STOP_VIEW = "StopIcon";

    private TextView trackTitle;
    private TextView trackAuthor;
    private ImageView trackImage;

    private TextView duracioTotal;
    private TextView duracioActual;

    private ImageButton btnBackward;
    private Button btnPlay;
    private Button btnPause;

    private Button likeTrack;
    private boolean liked=false;

    private ImageButton btnForward;
    private ImageButton shuffle;
    private boolean isShuffle=false;
    private Button atras;
    private SeekBar mSeekBar;
    private CircleLineVisualizer mVisualizer;
    private MediaPlayer mPlayer;

    private TrackManager tManager;

    private ReproductorService serv;
    private boolean servidorVinculat=false;



    //----------------------------------------------------------------PART DE SERVICE--------------------------------------------------------------------------------


    @Override
    public void onStart() {
        super.onStart();
        if(!servidorVinculat){
            Intent intent = new Intent(this, ReproductorService.class);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }else{
            serv.setUIControls(mSeekBar, trackTitle, trackAuthor, btnPlay, btnPause, trackImage);
            //serv.setmVisualizer(mVisualizer);
            serv.setDuracioTotal(duracioTotal);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_playback);
        initViews();
        tManager = new TrackManager(this);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ReproductorService.LocalBinder binder = (ReproductorService.LocalBinder) service;
            serv = binder.getService();
            servidorVinculat = true;
            serv.setUIControls(mSeekBar, trackTitle, trackAuthor, btnPlay, btnPause, trackImage);
            //serv.setmVisualizer(mVisualizer);
            serv.setDuracioTotal(duracioTotal);
            serv.setSeekCallback(ReproductorActivity.this);
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
    public void onSeekBarUpdate(int progress, int duration, boolean isPlaying, String duracio) {
        if(isPlaying){
            mSeekBar.postDelayed(serv.getmProgressRunner(), 1000);
        }
        mSeekBar.setProgress(progress);
        duracioActual.setText(duracio);
    }

    //----------------------------------------------------------------FIN DE LA PART DE SERVICE--------------------------------------------------------------------------------


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
    }



    private void initViews() {

        likeTrack= findViewById(R.id.addFavorite);
        likeTrack.setEnabled(true);
        likeTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tManager.likeTrack(serv.getCurrentTrack().getId(), ReproductorActivity.this);
            }
        });



        trackTitle= findViewById(R.id.music_title);
        trackTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        trackTitle.setSelected(true);
        trackTitle.setSingleLine(true);

        duracioTotal = findViewById(R.id.totalTime);
        duracioActual = findViewById(R.id.currentTime);


        trackAuthor = findViewById(R.id.music_artist);
        trackImage = findViewById(R.id.track_img);

        mVisualizer = (CircleLineVisualizer) findViewById(R.id.visualizerC);
        mVisualizer.setDrawLine(true);


        shuffle = (ImageButton) findViewById(R.id.botoShuffle);
        shuffle.setEnabled(true);
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isShuffle){
                    shuffle.setBackgroundResource(R.drawable.no_shuffle);;
                    isShuffle=false;
                }else{
                    shuffle.setBackgroundResource(R.drawable.si_shuffle);;
                    isShuffle=true;
                }
            }
        });


        atras = findViewById(R.id.buttonAtras);
        atras.setEnabled(true);
        atras.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                  finishAfterTransition();
              }else finish();
           }
        });
        btnBackward = (ImageButton)findViewById(R.id.music_backward_btn);
        btnBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serv.skipToPrevious();
            }
        });
        btnForward = (ImageButton)findViewById(R.id.music_forward_btn);
        btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serv.skipToNext();
            }
        });
        btnPlay = findViewById(R.id.play);
        btnPlay.setEnabled(true);
        btnPlay.bringToFront();
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                serv.resumeMedia();
            }
        });
        btnPause = findViewById(R.id.pause);
        btnPause.setEnabled(true);
        btnPause.setVisibility(View.VISIBLE);
        btnPause.bringToFront();
        btnPause.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                serv.pauseMedia();
            }
        });

        mSeekBar = (SeekBar) findViewById(R.id.seekBar);


    }



    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onTracksReceived(List<Track> tracks) {

    }

    @Override
    public void onNoTracks(Throwable throwable) {

    }

    @Override
    public void onPersonalTracksReceived(List<Track> tracks) {

    }

    @Override
    public void onUserTracksReceived(List<Track> tracks) {

    }

    @Override
    public void onCreateTrack(Track t) {

    }

    @Override
    public void onTopTracksRecieved(List<Track> tracks) {

    }

    @Override
    public void onNoTopTracks(Throwable throwable) {

    }

    @Override
    public void onTrackLiked() {

        if(liked){
            likeTrack.setBackgroundResource(R.drawable.ic_favorite_track);;
            liked=false;
        }else{
            likeTrack.setBackgroundResource(R.drawable.ic_favorite_true);;
            liked=true;
        }
    }

    @Override
    public void onTrackNotFound(Throwable throwable) {

    }

    @Override
    public void onFailure(Throwable throwable) {

    }
}
