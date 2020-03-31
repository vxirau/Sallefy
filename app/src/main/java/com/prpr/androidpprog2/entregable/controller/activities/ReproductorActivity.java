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
    private ImageButton shuffle;
    private boolean isShuffle=false;
    private Button atras;
    private SeekBar mSeekBar;
    private CircleLineVisualizer mVisualizer;
    private MediaPlayer mPlayer;

    private ReproductorService serv;
    private boolean servidorVinculat=false;

    @Override
    public void onResume() {
        super.onResume();
        if(!servidorVinculat){
            Intent intent = new Intent(this, ReproductorService.class);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }else{
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
        if(mPlayer!=null){
            int audioSessionId = mPlayer.getAudioSessionId();
            if (audioSessionId != -1)
                mVisualizer.setAudioSessionId(audioSessionId);
        }

    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ReproductorService.LocalBinder binder = (ReproductorService.LocalBinder) service;
            serv = binder.getService();
            servidorVinculat = true;
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
