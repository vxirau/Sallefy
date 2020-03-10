package com.prpr.androidpprog2.entregable.controller.music;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.prpr.androidpprog2.entregable.utils.Constants;

public class MusicService extends Service {

    private MediaPlayer mediaPlayer;
    private final IBinder mBinder = new MusicBinder();
    private AudioManager audioManager;
    private boolean playingBeforeInterruption = false;

    public class MusicBinder extends Binder {
        public MusicService getService(){
            return MusicService.this;
        }
    }

    public MusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getStringExtra(Constants.URL) != null)
            playStream(intent.getStringExtra(Constants.URL));

        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void stopService() {
        pausePlayer();
        stopSelf();
        onDestroy();
    }

    public void playStream(String url) {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
            } catch(Exception e) {
            }
            mediaPlayer = null;
        }

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                updateTrack(1);
            }
        });

        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });
        } catch(Exception e) {

        }

    }
    public void updateSessionMusicData(int offset) {
        /*int oldIndex = Session.getInstance(getApplicationContext()).getIndex();
        int size = Session.getInstance(getApplicationContext()).getTracks().size();
        int newIndex = (oldIndex + offset)%size;
        Session.getInstance(getApplicationContext()).setIndex(newIndex);
        Track newTrack = Session.getInstance(getApplicationContext()).getTracks().get(newIndex);
        Session.getInstance(getApplicationContext()).setTrack(newTrack);*/
    }

    public void updateTrack(int offset) {
        updateSessionMusicData(offset);
        String newUrl = "";//Session.getInstance(getApplicationContext()).getTrack().getUrl();
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(newUrl);
            //mediaPlayer.pause();
            mediaPlayer.prepare();
        } catch(Exception e) {
        }
    }


    private void pausePlayer() {
        try {
            mediaPlayer.pause();
           // showNotification();
        } catch (Exception e) {
            Log.d(" EXCEPTION", "failed to ic_pause media player.");
        }
    }

    public void playPlayer() {
        try {
            getAudioFocusAndPlay();
            //showNotification();
        } catch (Exception e) {
            Log.d("EXCEPTION", "failed to start media player.");
        }
    }

    public void togglePlayer() {
        try {
            if (mediaPlayer.isPlaying()) {
                pausePlayer();
            } else {
                playPlayer();
            }
        }catch(Exception e) {
            Log.d("EXCEPTION", "failed to toggle media player.");
        }
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }
    // audio focus section
    public void getAudioFocusAndPlay () {
        audioManager = (AudioManager) this.getBaseContext().getSystemService(Context.AUDIO_SERVICE);

        int result = audioManager.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mediaPlayer.start();
        }
    }

    public void setCurrentDuration(int time) {
        try {
            mediaPlayer.seekTo(time);
        } catch (Exception e) {
            Log.d("EXCEPTION", "Failed to set the duration");
        }
    }

    public int getCurrrentDuration() {
        try {
            if (mediaPlayer != null) {
                return mediaPlayer.getCurrentPosition();
            } else {
                return 0;
            }
        }catch(Exception e) {
            Log.d("EXCEPTION", "Failed to get the duration");
        }
        return 0;
    }

    public int getMaxDuration() {
        try {
            if (mediaPlayer != null) {
                return mediaPlayer.getDuration();
            } else {
                return 0;
            }
        }catch(Exception e) {
            Log.d("EXCEPTION", "Failed to get the duration");
        }
        return 0;
    }

    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                if (mediaPlayer.isPlaying()) {
                    playingBeforeInterruption = true;
                } else {
                    playingBeforeInterruption = false;
                }
                pausePlayer();
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                if (playingBeforeInterruption) {
                    playPlayer();
                }
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                pausePlayer();
                audioManager.abandonAudioFocus(afChangeListener);
            }
        }
    };
}
