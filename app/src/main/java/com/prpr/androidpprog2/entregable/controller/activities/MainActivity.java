package com.prpr.androidpprog2.entregable.controller.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.adapters.PlaylistAdapter;
import com.prpr.androidpprog2.entregable.controller.adapters.UserAdapter;
import com.prpr.androidpprog2.entregable.controller.callbacks.ServiceCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.PlaylistCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.UserCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.PlaylistManager;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.UserManager;
import com.prpr.androidpprog2.entregable.controller.restapi.service.ReproductorService;
import com.prpr.androidpprog2.entregable.model.Follow;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.model.User;
import com.prpr.androidpprog2.entregable.model.UserToken;
import com.prpr.androidpprog2.entregable.utils.Constants;
import com.prpr.androidpprog2.entregable.utils.PreferenceUtils;
import com.prpr.androidpprog2.entregable.utils.Session;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PlaylistCallback, UserCallback, ServiceCallback {

    private FloatingActionButton mes;
    private FloatingActionButton btnNewPlaylist;
    private FloatingActionButton pujarCanco;
    Animation fabOpen, fabClose, rotateForward, rotateBackward;
    boolean isOpen = false;
    private TextView trackTitle;
    private TextView followingTxt;
    private TextView trackAuthor;
    private SeekBar mSeekBar;
    private Button play;
    private Button pause;
    private ImageView im;
    private boolean sameUser = false;

    private RecyclerView allPlaylistRecycle;
    private RecyclerView topPlaylistsRecycle;
    private RecyclerView topUsersReycle;
    private RecyclerView folloingPlaylistRecycle;


    private ArrayList<Playlist> allPlaylists;
    private ArrayList<Playlist> topPlaylists;
    private ArrayList<User> topUsers;
    private ArrayList<Playlist> followingPlaylists;
    private ArrayList<Playlist> discover;


    private PlaylistManager pManager;
    private UserManager usrManager;


    private LinearLayout playing;

    //----------------------------------------------------------------PART DE SERVICE--------------------------------------------------------------------------------
    public static final String Broadcast_PLAY_NEW_AUDIO = "com.prpr.androidpprog2.entregable.PlayNewAudio";
    private ReproductorService serv;
    private boolean servidorVinculat=false;
    private ArrayList<Track> audioList;
    private int audioIndex = -1;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ReproductorService.LocalBinder binder = (ReproductorService.LocalBinder) service;
            serv = binder.getService();
            //serv.setmSeekBar(mSeekBar);
            servidorVinculat = true;
            serv.setUIControls(mSeekBar, trackTitle, trackAuthor, play, pause, im);
            serv.setSeekCallback(MainActivity.this);
            if(sameUser){
                serv.pauseMedia();
            }
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
    public void onStart() {
        super.onStart();
        if(!servidorVinculat){
            Intent intent = new Intent(this, ReproductorService.class);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }else{
            serv.setUIControls(mSeekBar, trackTitle, trackAuthor, play, pause, im);
            serv.updateUI();
            serv.setSeekCallback(this);
        }
    }


    private void loadPreviousSession() {
        audioList = PreferenceUtils.getAllTracks(getApplicationContext());
        audioIndex = PreferenceUtils.getTrackIndex(getApplicationContext());
        loadAudioPlay(audioIndex);
        Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
        sendBroadcast(broadcastIntent);
    }

    private void loadAudioPlay(int audioIndex) {
        Intent playerIntent = new Intent(this, ReproductorService.class);
        startService(playerIntent);
        bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        trackTitle.setText(audioList.get(audioIndex).getName());
        trackAuthor.setText(audioList.get(audioIndex).getUserLogin());
    }


    @Override
    public void onResume() {
        super.onResume();
        if(servidorVinculat){
            serv.setSeekCallback(this);
        }
        pManager.getFollowingPlaylists(this);
    }


    @Override
    public void onSeekBarUpdate(int progress, int duration, boolean isPlaying, String duracio) {
        if(!sameUser){
            if(isPlaying){
                mSeekBar.postDelayed(serv.getmProgressRunner(), 1000);
            }
            mSeekBar.setProgress(progress);
        }else{
            serv.pauseMedia();
            sameUser=false;
        }

    }

    //----------------------------------------------------------------FIN DE LA PART DE SERVICE--------------------------------------------------------------------------------



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(getIntent().getSerializableExtra("sameUser")!=null){
            sameUser = (boolean) getIntent().getSerializableExtra("sameUser");
        }
        initViews();
        btnNewPlaylist.setEnabled(true);
        UserToken userToken = Session.getInstance(this).getUserToken();
        pManager = new PlaylistManager(this);
        usrManager = new UserManager(this);
        pManager.getAllPlaylists(this);
        pManager.getTopPlaylists(this);
        usrManager.getTopUsers(this);
        pManager.getFollowingPlaylists(this);
        if(sameUser){
            loadPreviousSession();
        }
    }

    private void initViews() {

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.menu);
        navigation.setSelectedItemId(R.id.home);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        return true;
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

        followingTxt= findViewById(R.id.noFollow);

        play = findViewById(R.id.playButton);
        play.setEnabled(true);
        play.bringToFront();
        play.setVisibility(View.VISIBLE);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serv.resumeMedia();
            }
        });
        pause = findViewById(R.id.playPause);
        pause.setEnabled(true);
        pause.setVisibility(View.INVISIBLE);
        pause.bringToFront();
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serv.pauseMedia();
            }
        });

        playing = findViewById(R.id.reproductor);
        playing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReproductorActivity.class);
                startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
            }
        });

        trackAuthor = findViewById(R.id.dynamic_artist);
        trackTitle = findViewById(R.id.dynamic_title);
        trackTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        trackTitle.setSelected(true);
        trackTitle.setSingleLine(true);

        allPlaylistRecycle = (RecyclerView) findViewById(R.id.allplaylists);
        LinearLayoutManager manager2 = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        PlaylistAdapter adapter2 = new PlaylistAdapter(this, null);
        adapter2.setPlaylistCallback(this);
        allPlaylistRecycle.setLayoutManager(manager2);
        allPlaylistRecycle.setAdapter(adapter2);

        topPlaylistsRecycle = (RecyclerView) findViewById(R.id.topPlayedPlaylists);
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        PlaylistAdapter adapter = new PlaylistAdapter(this, null);
        adapter2.setPlaylistCallback(this);
        topPlaylistsRecycle.setLayoutManager(manager);
        topPlaylistsRecycle.setAdapter(adapter);

        topUsersReycle = (RecyclerView) findViewById(R.id.artists_descobrir);
        LinearLayoutManager manager3 = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        UserAdapter adapter3 = new UserAdapter(this, null);
        adapter3.setUserCallback(this);
        topUsersReycle.setLayoutManager(manager3);
        topUsersReycle.setAdapter(adapter3);

        folloingPlaylistRecycle = (RecyclerView) findViewById(R.id.followingPlaylists);
        LinearLayoutManager manager4 = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        PlaylistAdapter adapter4 = new PlaylistAdapter(this, null);
        adapter4.setPlaylistCallback(this);
        folloingPlaylistRecycle.setLayoutManager(manager4);
        folloingPlaylistRecycle.setAdapter(adapter4);


        mes= findViewById(R.id.mesButton);
        mes.setEnabled(true);
        mes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
            }
        });


        btnNewPlaylist = findViewById(R.id.novaPlaylst);
        btnNewPlaylist.setEnabled(false);
        btnNewPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
                Intent intent = new Intent(getApplicationContext(), NewPlaylistActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
            }
        });

        pujarCanco= findViewById(R.id.pujarCanco);
        pujarCanco.setEnabled(false);
        Context c = this;
        pujarCanco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
                Intent intent = new Intent(getApplicationContext(), UploadActivity.class);
                intent.putExtra("agas", false);
                intent.putExtra("User", Session.getInstance(c).getUser());
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
            }
        });


        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        rotateForward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);

        mSeekBar = (SeekBar) findViewById(R.id.dynamic_seekBar);


    }

    private void animateFab(){
        if(isOpen){
            btnNewPlaylist.startAnimation(fabClose);
            pujarCanco.startAnimation(fabClose);
            btnNewPlaylist.setClickable(false);
            pujarCanco.setClickable(false);
            btnNewPlaylist.setEnabled(false);
            pujarCanco.setEnabled(false);
            isOpen=false;
        }else{
            btnNewPlaylist.startAnimation(fabOpen);
            pujarCanco.startAnimation(fabOpen);
            btnNewPlaylist.setClickable(true);
            pujarCanco.setClickable(true);
            btnNewPlaylist.setEnabled(true);
            pujarCanco.setEnabled(true);
            isOpen=true;
        }
    }


    private void enableNetworkButtons() {
        btnNewPlaylist.setEnabled(true);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.NETWORK.LOGIN_OK) {
            enableNetworkButtons();
            if (resultCode == RESULT_OK) {}
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }



    @Override
    public void onPlaylistCreated(Playlist playlist) {

    }

    @Override
    public void onPlaylistFailure(Throwable throwable) {

    }


    @Override
    public void onPlaylistRecieved(List<Playlist> playlists) {
        this.topPlaylists = (ArrayList) playlists;
        PlaylistAdapter p2 = new PlaylistAdapter(this, this.topPlaylists);
        p2.setPlaylistCallback(this);
        topPlaylistsRecycle.setAdapter(p2);
    }

    @Override
    public void onAllPlaylistRecieved(List<Playlist> body) {
        this.allPlaylists = (ArrayList) body;
        PlaylistAdapter p2 = new PlaylistAdapter(this, this.allPlaylists);
        p2.setPlaylistCallback(this);
        allPlaylistRecycle.setAdapter(p2);
    }

    @Override
    public void onNoPlaylists(Throwable throwable) {
        Toast.makeText(this, "No tens playlists", Toast.LENGTH_LONG);
    }

    @Override
    public void onPlaylistSelected(Playlist playlist) {
        Intent intent = new Intent(getApplicationContext(), PlaylistActivity.class);
        intent.putExtra("Playlst", playlist);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
    }

    @Override
    public void onTrackAdded(Playlist body) {

    }

    @Override
    public void onTrackAddFailure(Throwable throwable) {

    }

    @Override
    public void onAllNoPlaylists(Throwable throwable) {

    }

    @Override
    public void onAllPlaylistFailure(Throwable throwable) {

    }

    @Override
    public void onTopRecieved(List<Playlist> topPlaylists) {
        this.topPlaylists = (ArrayList) topPlaylists;
        PlaylistAdapter p2 = new PlaylistAdapter(this, this.topPlaylists);
        p2.setPlaylistCallback(this);
        topPlaylistsRecycle.setAdapter(p2);
    }

    @Override
    public void onNoTopPlaylists(Throwable throwable) {

    }

    @Override
    public void onTopPlaylistsFailure(Throwable throwable) {

    }

    @Override
    public void onFollowingRecieved(List<Playlist> body) {
        if(body.size()==0){
            folloingPlaylistRecycle.setVisibility(View.GONE);
            followingTxt.setVisibility(View.VISIBLE);
        }else{
            this.followingPlaylists = (ArrayList) body;
            PlaylistAdapter p2 = new PlaylistAdapter(this, this.followingPlaylists);
            p2.setPlaylistCallback(this);
            folloingPlaylistRecycle.setAdapter(p2);
        }
    }

    @Override
    public void onFollowingChecked(Follow body) {

    }

    @Override
    public void onFollowSuccessfull(Follow body) {

    }


    @Override
    public void onLoginSuccess(UserToken userToken) {

    }

    @Override
    public void onLoginFailure(Throwable throwable) {

    }

    @Override
    public void onRegisterSuccess() {

    }

    @Override
    public void onRegisterFailure(Throwable throwable) {

    }

    @Override
    public void onUserInfoReceived(User userData) {

    }

    @Override
    public void onUsernameUpdated(User user) {

    }

    @Override
    public void onEmailUpdated(User user) {

    }

    @Override
    public void onTopUsersRecieved(List<User> body) {
        this.topUsers = (ArrayList) body;
        UserAdapter p3 = new UserAdapter(this, this.topUsers);
        p3.setUserCallback(this);
        topUsersReycle.setAdapter(p3);
    }

    @Override
    public void onUserSelected(User user) {
        Intent intent = new Intent(getApplicationContext(), InfoArtistaActivity.class);
        intent.putExtra("User", user);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
    }

    @Override
    public void onAllUsersSuccess(List<User> users) {

    }

    @Override
    public void onFollowedUsersSuccess(List<User> users) {

    }


    @Override
    public void onAllUsersFail(Throwable throwable) {

    }

    @Override
    public void onFollowedUsersFail(Throwable throwable) {

    }


    @Override
    public void onFailure(Throwable throwable) {

    }

}
