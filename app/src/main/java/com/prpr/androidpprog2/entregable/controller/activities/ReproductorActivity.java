package com.prpr.androidpprog2.entregable.controller.activities;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gauravk.audiovisualizer.visualizer.CircleLineVisualizer;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.utils.Constants;

import java.io.IOException;

public class ReproductorActivity extends Activity {

    private static final String TAG = "DynamicPlaybackActivity";
    private static final String PLAY_VIEW = "PlayIcon";
    private static final String STOP_VIEW = "StopIcon";

    private TextView tvTitle;
    private TextView tvAuthor;
    private ImageView ivPhoto;

    private ImageButton btnBackward;
    private ImageButton btnPlayStop;
    private ImageButton btnForward;
    private Button atras;
    private SeekBar mSeekBar;
    private Handler mHandler;
    private Runnable mRunnable;

    private CircleLineVisualizer mVisualizer;

    private MediaPlayer mPlayer;
    private final static String url = "https://res.cloudinary.com/jcarri/video/upload/v1568737044/simon-garfunkel-the-boxer-audio_whwaox.mp3";
    //private final static String url = "https://soundcloud.com/lionelrichieofficial/all-night-long-all-night-album";



    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_playback);
        initViews();


    }

    private void initViews() {

        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mSeekBar.setMax(mPlayer.getDuration());

                int audioSessionId = mPlayer.getAudioSessionId();
                if (audioSessionId != -1)
                    mVisualizer.setAudioSessionId(audioSessionId);
            }
        });

        mHandler = new Handler();
        Thread connection = new Thread(new Runnable() {
            @Override
            public void run() {
                //try {
                    //mPlayer.setDataSource(url);
                    //mPlayer.prepare(); // might take long! (for buffering, etc)
                //} catch (IOException e) {
                  //  Toast.makeText(ReproductorActivity.this,"Error, couldn't play the music\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                //}
            }
        });

        tvAuthor = findViewById(R.id.music_artist);
        tvTitle = findViewById(R.id.music_title);

        atras = findViewById(R.id.buttonAtras);
        atras.setEnabled(true);
        atras.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
              /* Intent intent = new Intent(getApplicationContext(), MainActivity.class);
               startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);*/
               finish();
           }
        });
        btnBackward = (ImageButton)findViewById(R.id.music_backward_btn);
        btnForward = (ImageButton)findViewById(R.id.music_forward_btn);

        btnPlayStop = (ImageButton)findViewById(R.id.music_play_btn);
        btnPlayStop.setTag(PLAY_VIEW);
        btnPlayStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (btnPlayStop.getTag().equals(PLAY_VIEW)) {
                    mPlayer.start();
                    updateSeekBar();
                    btnPlayStop.setImageResource(R.drawable.ic_pause);
                    btnPlayStop.setTag(STOP_VIEW);
                    Toast.makeText(getApplicationContext(), "Playing Audio", Toast.LENGTH_SHORT).show();

                } else {
                    mPlayer.pause();
                    btnPlayStop.setImageResource(R.drawable.ic_play);
                    btnPlayStop.setTag(PLAY_VIEW);
                    Toast.makeText(getApplicationContext(), "Pausing Audio", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVisualizer != null)
            mVisualizer.release();
    }
}
