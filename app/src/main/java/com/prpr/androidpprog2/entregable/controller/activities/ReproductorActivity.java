package com.prpr.androidpprog2.entregable.controller.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.framework.CastContext;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.formatter.IFillFormatter;


import com.google.android.gms.cast.framework.CastOptions;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.CastState;
import com.google.android.gms.cast.framework.CastStateListener;
import com.google.android.gms.cast.framework.IntroductoryOverlay;
import com.google.android.gms.cast.framework.OptionsProvider;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.SessionProvider;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.TrackCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.TrackManager;
import com.prpr.androidpprog2.entregable.controller.music.ReproductorService;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.utils.ConnectivityService;
import com.prpr.androidpprog2.entregable.utils.Constants;
import com.prpr.androidpprog2.entregable.utils.Session;

import java.util.List;

public class ReproductorActivity extends AppCompatActivity implements TrackCallback,OptionsProvider {

    private static final String TAG = "DynamicPlaybackActivity";
    private static final String PLAY_VIEW = "PlayIcon";
    private static final String STOP_VIEW = "StopIcon";

    private TextView trackTitle;
    private TextView trackAuthor;
    private ImageView trackImage;

    private TextView duracioTotal;
    private TextView duracioActual;

    private Track active;

    private ImageButton btnBackward;
    private Button btnPlay;
    private Button btnPause;
    private Button queueButton;

    private Button likeTrack;
    private boolean liked=false;
    private RelativeLayout relativeLayoutLikeButton;

    private ImageButton btnForward;
    private ImageButton shuffle;
    private Toolbar chromeBar;
    private LinearLayout shuffleLayout;
    private Button atras;
    private SeekBar mSeekBar;

    private TrackManager tManager;

    private ReproductorService serv;
    private boolean servidorVinculat=false;

    private CastContext mCastContext;
    private MenuItem mediaRouteMenuItem;
    private CastStateListener mCastStateListener;
    private IntroductoryOverlay mIntroductoryOverlay;
    private CastSession mCastSession;
    private SessionManager mSessionManager;
    private SessionManagerListener<CastSession> mSessionManagerListener;

    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;

    private RelativeLayout relativeLayoutImage;
    private RelativeLayout relativeLayoutVideo;

    private BroadcastReceiver songChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            active = serv.getActiveAudio();
            surfaceHolder.addCallback(serv);
            ReproductorActivity.this.videoShow();
        }
    };

    //----------------------------------------------------------------PART DE SERVICE--------------------------------------------------------------------------------


    @Override
    public void onStart() {
        super.onStart();
        if(!servidorVinculat){
            Intent intent = new Intent(this, ReproductorService.class);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        }else{
            serv.setUIControls(mSeekBar, trackTitle, trackAuthor, btnPlay, btnPause, trackImage);
            serv.setRandomButton(shuffle);
            serv.setDuracioTotal(duracioTotal, duracioActual);
            serv.updateUI();
            serv.setShuffleButtonUI();
            serv.setMainActivity(ReproductorActivity.this);

        }
    }


    @Override
    public void onResume() {
        Log.d(TAG, "onResume() was called");
        mCastContext.getSessionManager().addSessionManagerListener(
                mSessionManagerListener, CastSession.class);
        super.onResume();

        if(servidorVinculat){
            serv.setShuffleButtonUI();
            active = serv.getActiveAudio();
            updateLiked();
        }
    }

    private void updateLiked() {
        if(!active.isLiked()){
            likeTrack.setBackgroundResource(R.drawable.ic_favorite_track);;
            liked=true;
        }else{
            likeTrack.setBackgroundResource(R.drawable.ic_favorite_true);;
            liked=false;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_playback);
        setupActionBar();


        /*mCastStateListener = new CastStateListener() {
            @Override
            public void onCastStateChanged(int newState) {
                if (newState != CastState.NO_DEVICES_AVAILABLE) {
                    showIntroductoryOverlay();
                }
            }
        };*/

        initViews();
        tManager = new TrackManager(this);
        setupCastListener();
        mCastContext = CastContext.getSharedInstance(this);
        mSessionManager = mCastContext.getSessionManager();
        mCastSession = mSessionManager.getCurrentCastSession();

    }

    private void setupActionBar() {
        chromeBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(chromeBar);
    }

    private void showIntroductoryOverlay() {
        if (mIntroductoryOverlay != null) {
            mIntroductoryOverlay.remove();
        }
        if ((mediaRouteMenuItem != null) && mediaRouteMenuItem.isVisible()) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mIntroductoryOverlay = new IntroductoryOverlay.Builder(
                            ReproductorActivity.this, mediaRouteMenuItem)
                            .setTitleText("Introducing Cast")
                            .setSingleTime()
                            .setOnOverlayDismissedListener(
                                    new IntroductoryOverlay.OnOverlayDismissedListener() {
                                        @Override
                                        public void onOverlayDismissed() {
                                            mIntroductoryOverlay = null;
                                        }
                                    })
                            .build();
                    mIntroductoryOverlay.show();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.chromecast, menu);
        mediaRouteMenuItem = CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), menu,
                R.id.media_route_menu_item);

        return true;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ReproductorService.LocalBinder binder = (ReproductorService.LocalBinder) service;
            serv = binder.getService();
            servidorVinculat = true;
            serv.setUIControls(mSeekBar, trackTitle, trackAuthor, btnPlay, btnPause, trackImage);
            active = serv.getActiveAudio();
            serv.setMainActivity(ReproductorActivity.this);

            updateLiked();
            serv.setRandomButton(shuffle);
            serv.setShuffleButtonUI();
            serv.setDuracioTotal(duracioTotal, duracioActual);

            surfaceHolder.addCallback(serv);
            ReproductorActivity.this.videoShow();
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
        unregisterReceiver(songChanged);
    }

    private void registerSongChanged() {
        IntentFilter filter = new IntentFilter(ReproductorService.Broadcast_SONG_CHANGED);
        registerReceiver(songChanged, filter);
    }

    //----------------------------------------------------------------FIN DE LA PART DE SERVICE--------------------------------------------------------------------------------


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
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
        duracioActual.setText("--:--");


        trackAuthor = findViewById(R.id.music_artist);
        trackImage = findViewById(R.id.track_img);

        shuffleLayout = findViewById(R.id.shuffleLayout);
        shuffleLayout.setEnabled(true);
        shuffleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serv.toggleShuffle();
            }
        });

        shuffle = (ImageButton) findViewById(R.id.botoShuffle);
        shuffle.setEnabled(true);
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serv.toggleShuffle();

            }
        });

        queueButton = findViewById(R.id.queueButton);
        queueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), QueueActivity.class);
                intent.putExtra("queue", serv.getAudioList());
                intent.putExtra("currentTrack", serv.getCurrentTrack());
                startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
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
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                serv.skipToPrevious();
                serv.loadMedia(mSeekBar.getProgress(), mCastSession,true);
                active = serv.getActiveAudio();
                videoShow();
            }
        });
        btnForward = (ImageButton)findViewById(R.id.music_forward_btn);
        btnForward.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                serv.skipToNext();
                serv.loadMedia(mSeekBar.getProgress(), mCastSession,true);
                active = serv.getActiveAudio();
                videoShow();
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
                serv.loadMedia(mSeekBar.getProgress(), mCastSession,true);
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
                serv.loadMedia(mSeekBar.getProgress(), mCastSession,false);
            }
        });

        mSeekBar = (SeekBar) findViewById(R.id.seekBar);

        relativeLayoutLikeButton = (RelativeLayout) findViewById(R.id.relativeLayoutLikeButton);

        relativeLayoutImage = (RelativeLayout) findViewById(R.id.layoutPlayPicture);
        relativeLayoutVideo = (RelativeLayout) findViewById(R.id.layoutPlayVideo);

        relativeLayoutImage.setVisibility(View.VISIBLE);
        relativeLayoutVideo.setVisibility(View.GONE);

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);

        surfaceHolder = surfaceView.getHolder();

        registerSongChanged();
    }

    private void videoShow() {

        ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(relativeLayoutLikeButton.getLayoutParams());
        marginParams.setMargins(0, 0 ,160, 0);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
        relativeLayoutLikeButton.setLayoutParams(layoutParams);

        relativeLayoutImage.setVisibility(View.VISIBLE);
        relativeLayoutVideo.setVisibility(View.GONE);

        if (!serv.isOffline() && active.getUrl().contains("mp4")){
            ViewGroup.MarginLayoutParams marginParamsOnVideo = new ViewGroup.MarginLayoutParams(relativeLayoutLikeButton.getLayoutParams());
            marginParamsOnVideo.setMargins(0, 20 ,20, 0);
            RelativeLayout.LayoutParams layoutParamsOnVideo = new RelativeLayout.LayoutParams(marginParamsOnVideo);
            relativeLayoutLikeButton.setLayoutParams(layoutParamsOnVideo);

            relativeLayoutImage.setVisibility(View.GONE);
            relativeLayoutVideo.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        mCastContext.getSessionManager().removeSessionManagerListener(
                mSessionManagerListener, CastSession.class);
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
    public void onPersonalLikedTracksReceived(List<Track> tracks) {

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
    public void onTrackLiked(int id) {
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
    public void onTrackUpdated(Track body) {

    }

    @Override
    public void onTrackUpdateFailure(Throwable throwable) {

    }

    @Override
    public void onTrackDeleted(int id) {

    }

    @Override
    public void onTrackReceived(Track track) {

    }

    @Override
    public void onMyTracksFailure(Throwable throwable) {

    }

    @Override
    public void onFailure(Throwable throwable) {

    }


    @Override
    public CastOptions getCastOptions(Context appContext) {
        CastOptions castOptions = new CastOptions.Builder()
                .setReceiverApplicationId(appContext.getString(R.string.app_id))
                .build();
        return castOptions;
    }
    @Override
    public List<SessionProvider> getAdditionalSessionProviders(Context context) {
        return null;
    }

    private void setupCastListener() {
        mSessionManagerListener = new SessionManagerListener<CastSession>() {

            @Override
            public void onSessionEnded(CastSession session, int error) {
                onApplicationDisconnected();
            }

            @Override
            public void onSessionResumed(CastSession session, boolean wasSuspended) {
                //onApplicationConnected(session);
            }

            @Override
            public void onSessionResumeFailed(CastSession session, int error) {
                onApplicationDisconnected();
            }

            @Override
            public void onSessionStarted(CastSession session, String sessionId) {
                onApplicationConnected(session);
            }

            @Override
            public void onSessionStartFailed(CastSession session, int error) {
                onApplicationDisconnected();
            }

            @Override
            public void onSessionStarting(CastSession session) {
            }

            @Override
            public void onSessionEnding(CastSession session) {
            }

            @Override
            public void onSessionResuming(CastSession session, String sessionId) {
            }

            @Override
            public void onSessionSuspended(CastSession session, int reason) {
            }

            private void onApplicationConnected(CastSession castSession) {
                serv.loadMedia(mSeekBar.getProgress(), castSession,false);
                mCastSession = castSession;
            }

            private void onApplicationDisconnected() {
                serv.upVolume();
            }
        };
    }

}
