package com.prpr.androidpprog2.entregable.controller.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
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

import androidx.core.content.ContextCompat;

import com.chibde.visualizer.CircleBarVisualizer;
import com.gauravk.audiovisualizer.visualizer.CircleLineVisualizer;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.restapi.service.ReproductorService;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.utils.Constants;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class ReproductorActivity extends Activity {

    private static final String TAG = "DynamicPlaybackActivity";
    private static final String PLAY_VIEW = "PlayIcon";
    private static final String STOP_VIEW = "StopIcon";

    private TextView trackTitle;
    private TextView trackAuthor;
    private ImageView trackImage;

    private ImageButton btnBackward;
    private Button btnPlay;
    private Button btnPause;

    private ImageButton btnForward;
    private Button atras;
    private SeekBar mSeekBar;
    private Handler mHandler;
    private Runnable mRunnable;
    private Track trck;
    private CircleLineVisualizer mVisualizer;
    private MediaPlayer mPlayer;

    private ReproductorService serv;
    private boolean servidorVinculat=false;

    @Override
    public void onStart() {
        super.onStart();
        if(!servidorVinculat){
            Intent intent = new Intent(this, ReproductorService.class);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }else{
            serv.setUIControls(mSeekBar, trackTitle, trackAuthor, btnPlay, btnPause, trackImage);
            serv.updateUI();

            updateVisualizer();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_playback);
        initViews();
    }

    private void updateVisualizer(){
        mPlayer = serv.getPlayer();
        int audioSessionId = mPlayer.getAudioSessionId();
        if (audioSessionId != -1)
            mVisualizer.setAudioSessionId(audioSessionId);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ReproductorService.LocalBinder binder = (ReproductorService.LocalBinder) service;
            serv = binder.getService();
            servidorVinculat = true;
            serv.setUIControls(mSeekBar, trackTitle, trackAuthor, btnPlay, btnPause, trackImage);
            serv.updateUI();
            updateVisualizer();

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
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
    }



    private void initViews() {


        trackTitle= findViewById(R.id.music_title);
        trackTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        trackTitle.setSelected(true);
        trackTitle.setSingleLine(true);


        trackAuthor = findViewById(R.id.music_artist);
        trackImage = findViewById(R.id.track_img);

        mVisualizer = findViewById(R.id.circleVisualizer);
        mVisualizer.setDrawLine(true);



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
                updateVisualizer();
            }
        });
        btnForward = (ImageButton)findViewById(R.id.music_forward_btn);
        btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serv.skipToNext();
                updateVisualizer();
            }
        });
        btnPlay = findViewById(R.id.play);
        btnPlay.setEnabled(true);
        btnPlay.bringToFront();
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serv.resumeMedia();
                updateVisualizer();
            }
        });
        btnPause = findViewById(R.id.pause);
        btnPause.setEnabled(true);
        btnPause.bringToFront();
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serv.pauseMedia();
                updateVisualizer();
            }
        });

        mSeekBar = (SeekBar) findViewById(R.id.seekBar);


    }



    @Override
    protected void onPause() {
        super.onPause();
    }

}
