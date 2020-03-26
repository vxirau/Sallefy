package com.prpr.androidpprog2.entregable.controller.activities;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.renderscript.ScriptGroup;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.adapters.PlaylistAdapter;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.PlaylistCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.PlaylistManager;
import com.prpr.androidpprog2.entregable.controller.restapi.service.ReproductorService;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.UserToken;
import com.prpr.androidpprog2.entregable.utils.Constants;
import com.prpr.androidpprog2.entregable.utils.Session;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PlaylistCallback {

    private FloatingActionButton mes;
    private FloatingActionButton btnNewPlaylist;
    private FloatingActionButton pujarCanco;
    Animation fabOpen, fabClose, rotateForward, rotateBackward;
    boolean isOpen = false;
    private TextView trackTitle;
    private TextView trackAuthor;
    private SeekBar mSeekBar;
    private Button play;
    private Button pause;
    private ImageView im;


    private RecyclerView playlists_descobrir;
    private ArrayList<Playlist> discover;

    private RecyclerView allPlaylistRecycle;
    private ArrayList<Playlist> allPlaylists;

    private PlaylistManager pManager;
    private PlaylistManager pManager2;

    private ReproductorService serv;
    private boolean servidorVinculat=false;




    @Override
    public void onStart() {
        super.onStart();
        if(!servidorVinculat){
            Intent intent = new Intent(this, ReproductorService.class);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }else{
            serv.setUIControls(mSeekBar, trackTitle, trackAuthor, play, pause, im);
            serv.updateUI();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        enableInitialButtons();
        UserToken userToken = Session.getInstance(this).getUserToken();
        String usertkn = userToken.getIdToken();
        pManager = new PlaylistManager(this);
        pManager.getAllMyPlaylists(this);
        //pManager2 = new PlaylistManager(this);
        pManager.getAllPlaylists(this);


    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ReproductorService.LocalBinder binder = (ReproductorService.LocalBinder) service;
            serv = binder.getService();
            servidorVinculat = true;
            serv.setUIControls(mSeekBar, trackTitle, trackAuthor, play, pause, im);
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
                        Intent intent2 = new Intent(getApplicationContext(), UserPlaylistActivity.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent2, Constants.NETWORK.LOGIN_OK);
                        return true;
                }
                return false;
            }
        });

        play = findViewById(R.id.playButton);
        play.setEnabled(true);
        play.bringToFront();
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serv.resumeMedia();
            }
        });
        pause = findViewById(R.id.playPause);
        pause.setEnabled(true);
        pause.bringToFront();
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serv.pauseMedia();
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
                intent.putExtra("User", Session.getInstance(c).getUser());

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


    private void enableInitialButtons() {
        btnNewPlaylist.setEnabled(true);
        //btnTrackImg.setEnabled(true);
        //samplePlaylist.setEnabled(true);
    }

    private void enableNetworkButtons() {
        btnNewPlaylist.setEnabled(true);
    }

    private void enableAllButtons() {
        btnNewPlaylist.setEnabled(true);
        //btnTrackImg.setEnabled(true);
        //samplePlaylist.setEnabled(true);
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
        this.discover = (ArrayList) playlists;
        PlaylistAdapter p = new PlaylistAdapter(this, this.discover);
        p.setPlaylistCallback(this);
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


}
