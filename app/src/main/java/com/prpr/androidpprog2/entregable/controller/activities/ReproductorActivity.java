package com.prpr.androidpprog2.entregable.controller.activities;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
    private ImageButton btnPlayStop;
    private ImageButton btnForward;
    private Button atras;
    private SeekBar mSeekBar;
    private Handler mHandler;
    private Runnable mRunnable;
    private Track trck;
    private CircleLineVisualizer mVisualizer;
    private MediaPlayer mPlayer;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_playback);
        initViews();
        mPlayer.start();
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
    }

    private void initViews() {

        mVisualizer = findViewById(R.id.circleVisualizer);
        mVisualizer.setDrawLine(true);
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
                try {
                    //mPlayer.setDataSource(trck.getUrl());
                    mPlayer.prepare();
                } catch (IOException e) {
                  Toast.makeText(ReproductorActivity.this,"Error, couldn't play the music\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        trackAuthor = findViewById(R.id.music_artist);
        trackTitle = findViewById(R.id.music_title);
        /*
        trackImage = findViewById(R.id.track_img);
        if (trck.getThumbnail() != null) {
            Picasso.get().load(trck.getThumbnail()).into(trackImage);
        }else{
            Picasso.get().load("https://community.spotify.com/t5/image/serverpage/image-id/25294i2836BD1C1A31BDF2/image-size/original?v=mpbl-1&px=-1").into(trackImage);
        }*/

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
