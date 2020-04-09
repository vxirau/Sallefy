package com.prpr.androidpprog2.entregable.controller.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import java.util.*;

import android.os.IBinder;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gauravk.audiovisualizer.visualizer.CircleLineVisualizer;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.adapters.TrackListAdapter;
import com.prpr.androidpprog2.entregable.controller.callbacks.OptionDialogCallback;
import com.prpr.androidpprog2.entregable.controller.callbacks.ServiceCallback;
import com.prpr.androidpprog2.entregable.controller.callbacks.TrackListCallback;
import com.prpr.androidpprog2.entregable.controller.dialogs.ErrorDialog;
import com.prpr.androidpprog2.entregable.controller.dialogs.OptionDialog;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.PlaylistCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.TrackCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.PlaylistManager;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.TrackManager;
import com.prpr.androidpprog2.entregable.controller.restapi.service.ReproductorService;
import com.prpr.androidpprog2.entregable.model.Follow;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.model.User;
import com.prpr.androidpprog2.entregable.utils.Constants;
import com.prpr.androidpprog2.entregable.utils.KeyboardUtils;
import com.prpr.androidpprog2.entregable.utils.PreferenceUtils;
import com.prpr.androidpprog2.entregable.utils.Session;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PlaylistActivity extends AppCompatActivity implements TrackCallback, TrackListCallback, PlaylistCallback, ServiceCallback, OptionDialogCallback {

    private Playlist playlst;
    private TextView plyName;
    private TextView plyAuthor;
    private TextView followers;
    private ImageView plyImg;
    private ImageButton imgEdit;

    private TextView tvTitle;
    private TextView tvAuthor;
    private LinearLayout playing;
    private LinearLayout actionButtons;
    private LinearLayout reproductor;



    private Button back2Main;
    private Button infoPlaylist;
    private Button shuffle;
    private Button acceptEdit;
    private Button follow;
    private Button accessible;
    private Follow followingInfo;
    private boolean isFollowing = false;
    private Button addBunch;
    private Button play;
    private Button pause;

    private EditText newName;

    private RecyclerView mRecyclerView;
    private boolean bunch;
    private ArrayList<Track> mTracks;
    private int currentTrack = 0;
    private PlaylistManager pManager;
    private TrackManager trackManager;

    //Sort
    private FloatingActionButton mSorts;
    private FloatingActionButton mSortAlpha;
    private FloatingActionButton mSortTime;
    private FloatingActionButton mSortArtist;
    private int mSorted = -1;
    private boolean isOpen;
    private boolean asc_dsc;
    private BottomNavigationView navigation;

    private Animation fabOpen, fabClose;
    private OptionDialog dialogEdit;
    private final int SORT_AZ = 0;
    private final int SORT_TIME = 1;
    private final int SORT_ARTIST = 2;


    //----------------------------------------------------------------PART DE SERVICE--------------------------------------------------------------------------------
    private SeekBar mseek;
    private ReproductorService player;
    private ImageView im;
    private boolean serviceBound = false;
    public static final String Broadcast_PLAY_NEW_AUDIO = "com.prpr.androidpprog2.entregable.PlayNewAudio";




    private void playAudio(int audioIndex) {

        PreferenceUtils.saveAllTracks(getApplicationContext(), mTracks);
        PreferenceUtils.saveTrackIndex(getApplicationContext(), audioIndex);
        PreferenceUtils.saveTrack(getApplicationContext(), mTracks.get(audioIndex));
        PreferenceUtils.savePlayID(getApplicationContext(), playlst.getId());

        if (!serviceBound) {
            Intent playerIntent = new Intent(this, ReproductorService.class);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            sendBroadcast(broadcastIntent);
        } else {
            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            sendBroadcast(broadcastIntent);
        }
        tvTitle.setText(mTracks.get(audioIndex).getName());
        tvAuthor.setText(mTracks.get(audioIndex).getUserLogin());
    }


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ReproductorService.LocalBinder binder = (ReproductorService.LocalBinder) service;
            player = binder.getService();
            player.setmSeekBar(mseek);
            player.setSeekCallback(PlaylistActivity.this);
            serviceBound = true;
            player.setUIControls(mseek, tvTitle, tvAuthor, play, pause, im);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            player.stopSelf();
        }
    }

    @Override
    public void onSeekBarUpdate(int progress, int duration, boolean isPlaying, String duracio) {
        if(isPlaying){
            mseek.postDelayed(player.getmProgressRunner(), 1000);
        }
        mseek.setProgress(progress);
    }


    @Override
    public void onStart() {
        super.onStart();
        if(!serviceBound){
            Intent intent = new Intent(this, ReproductorService.class);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }else{
            player.setUIControls(mseek, tvTitle, tvAuthor, play, pause, im);
            player.updateUI();
            player.setSeekCallback(this);
        }
        pManager.checkFollowing(playlst.getId(), this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(serviceBound){
            player.setSeekCallback(this);
        }
        pManager.getPlaylist(playlst.getId(), this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_layout);
        if(getIntent().getSerializableExtra("Playlst")!=null){
            playlst = (Playlist) getIntent().getSerializableExtra("Playlst");
        }
        pManager = new PlaylistManager(this);
        trackManager = new TrackManager(this);
        if(playlst.getId()!=-5){
            pManager.checkFollowing(playlst.getId(), this);
        }

        initViews();
        getData();
    }

    //----------------------------------------------------------------FIN DE LA PART DE SERVICE--------------------------------------------------------------------------------



    private void initViews() {

        navigation = (BottomNavigationView) findViewById(R.id.menu);
        navigation.setSelectedItemId(R.id.none);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        Intent intent0 = new Intent(getApplicationContext(), MainActivity.class);
                        intent0.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent0, Constants.NETWORK.LOGIN_OK);
                        return true;
                    case R.id.buscar:
                        Intent intent1 = new Intent(getApplicationContext(), SearchActivity.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent1, Constants.NETWORK.LOGIN_OK);
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

        followers = findViewById(R.id.followers);
        followers.setText(playlst.getFollowers() +" Followers");

        playing = findViewById(R.id.reproductor);
        playing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReproductorActivity.class);
                startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.dynamic_recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        TrackListAdapter adapter = new TrackListAdapter(this, this, null, playlst);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(adapter);



        follow = findViewById(R.id.playlistSeguirBoto);
        follow.setEnabled(true);
        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pManager.followPlaylist(playlst.getId(), PlaylistActivity.this);
            }
        });

        if(playlst.getId()==-5){
            follow.setVisibility(View.GONE);
        }

        acceptEdit = findViewById(R.id.acceptEdit);
        acceptEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideUIEdit();
                if(!newName.getText().toString().matches("")){
                    playlst.setName(newName.getText().toString());
                }
                pManager.updatePlaylist(playlst, PlaylistActivity.this);
            }
        });
        acceptEdit.setVisibility(View.GONE);

        imgEdit = findViewById(R.id.imgEdit);
        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ErrorDialog.getInstance(PlaylistActivity.this).showErrorDialog("Change playlist cover is not yet available");
            }
        });
        imgEdit.setVisibility(View.GONE);

        reproductor= findViewById(R.id.reproductor);

        accessible= findViewById(R.id.privadaPublica);
        accessible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playlst.setPublicAccessible(!playlst.isPublicAccessible());
                pManager.updatePlaylist(playlst, PlaylistActivity.this);
                if(playlst.isPublicAccessible()){
                    accessible.setText("Public");
                    accessible.setBackgroundResource(R.drawable.rectangle_small_gborder_green);;
                }else{
                    accessible.setText("Private");
                    accessible.setBackgroundResource(R.drawable.rectangle_small_gborder_black);;
                }
            }
        });
        if(playlst.isPublicAccessible()){
            accessible.setText("Public");
            accessible.setBackgroundResource(R.drawable.rectangle_small_gborder_green);;
        }else{
            accessible.setText("Private");
            accessible.setBackgroundResource(R.drawable.rectangle_small_gborder_black);;
        }


        plyName = findViewById(R.id.playlistName);
        plyName.setVisibility(View.VISIBLE);
        plyAuthor = findViewById(R.id.playlistAuthor);
        plyImg = findViewById(R.id.playlistCover);

        newName = findViewById(R.id.nomCanvi);
        newName.setVisibility(View.GONE);

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

        play = findViewById(R.id.playButton);
        play.setEnabled(true);
        play.bringToFront();
        play.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                int index=0;
                player.resumeMedia();
                pause.setVisibility(View.VISIBLE);
                play.setVisibility(View.INVISIBLE);
            }
        });
        pause = findViewById(R.id.playPause);
        pause.setEnabled(true);
        pause.bringToFront();
        pause.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                player.pauseMedia();
                play.setVisibility(View.VISIBLE);
                pause.setVisibility(View.INVISIBLE);
            }
        });

        actionButtons = findViewById(R.id.actionbuttons);

        mseek = findViewById(R.id.dynamic_seekBar);
        mseek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    player.seekToPosition(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        shuffle = findViewById(R.id.playlistRandom);
        shuffle.setEnabled(true);
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAudio(new Random().nextInt(mTracks.size()));
                player.setShuffle(true);
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


        infoPlaylist = findViewById(R.id.infoPlaylist);
        infoPlaylist.setEnabled(true);
        infoPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogEdit.showOptionDialog("Edit", "Delete");
            }
        });

        if(Session.getInstance(getApplicationContext()).getUser().getLogin().equals(playlst.getOwner().getLogin())){
            addBunch.setVisibility(View.VISIBLE);
            infoPlaylist.setVisibility(View.VISIBLE);
            follow.setVisibility(View.GONE);
            accessible.setVisibility(View.VISIBLE);
        }else{
            addBunch.setVisibility(View.INVISIBLE);
            infoPlaylist.setVisibility(View.INVISIBLE);
            follow.setVisibility(View.VISIBLE);
            accessible.setVisibility(View.GONE);
        }
        if(playlst.getId()==-5){
            follow.setVisibility(View.GONE);
        }
        dialogEdit = new OptionDialog(PlaylistActivity.this, PlaylistActivity.this);




        back2Main = findViewById(R.id.back2Main);
        back2Main.setEnabled(true);
        back2Main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //player.savePosition();
                if(bunch){
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
                }else{
                    finish();
                    overridePendingTransition(R.anim.nothing,R.anim.nothing);
                }

            }
        });

        tvAuthor = findViewById(R.id.dynamic_artist);
        tvTitle = findViewById(R.id.dynamic_title);
        tvTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        tvTitle.setSelected(true);
        tvTitle.setSingleLine(true);

        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);

        mSorts = findViewById(R.id.playlistSorts);
        mSorts.setEnabled(true);
        mSorts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
            }
        });

        mSortAlpha = findViewById(R.id.playlistSortAlpha);
        mSortAlpha.setEnabled(false);
        mSortAlpha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
                sortAZ();
            }
        });

        mSortTime = findViewById(R.id.playlistSortTime);
        mSortTime.setEnabled(false);
        mSortTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
                sortTime();
            }
        });

        mSortArtist = findViewById(R.id.playlistSortArtist);
        mSortArtist.setEnabled(false);
        mSortArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
                sortArtist();

            }
        });
    }

    private void sortAZ(){
        if (mSorted != SORT_AZ) {
            Collections.sort(mTracks, Track.TrackNameAscendentComparator);
            asc_dsc = true;
        } else {
            if (asc_dsc) {
                Collections.sort(mTracks, Track.TrackNameDescendentComparator);
                asc_dsc = false;
            } else {
                Collections.sort(mTracks, Track.TrackNameAscendentComparator);
                asc_dsc = true;
            }
        }
        TrackListAdapter adapter = new TrackListAdapter(this, this, mTracks, playlst);
        mRecyclerView.setAdapter(adapter);
        mSorted = SORT_AZ;
    }

    private void sortTime(){
        if (mSorted != SORT_TIME) {
            Collections.sort(mTracks, Track.TrackAscendentDurationComparator);
            asc_dsc = true;
        } else {
            if (asc_dsc) {
                Collections.sort(mTracks, Track.TrackDescendentDurationComparator);
                asc_dsc = false;
            } else {
                Collections.sort(mTracks, Track.TrackAscendentDurationComparator);
                asc_dsc = true;
            }
        }
        TrackListAdapter adapter = new TrackListAdapter(this, this, mTracks, playlst);
        mRecyclerView.setAdapter(adapter);
        mSorted = SORT_TIME;
    }

    private void sortArtist(){
        if (mSorted != SORT_ARTIST) {
            Collections.sort(mTracks, Track.TrackArtistNameAscendentComparator);
            asc_dsc = true;
        } else {
            if (asc_dsc) {
                Collections.sort(mTracks, Track.TrackArtistNameDescendentComparator);
                asc_dsc = false;
            } else {
                Collections.sort(mTracks, Track.TrackArtistNameAscendentComparator);
                asc_dsc = true;
            }
        }
        TrackListAdapter adapter = new TrackListAdapter(this, this, mTracks, playlst);
        mRecyclerView.setAdapter(adapter);
        mSorted = SORT_ARTIST;
    }

    private void animateFab(){
        if(isOpen){
            mSortArtist.startAnimation(fabClose);
            mSortArtist.setClickable(false);
            mSortArtist.setEnabled(false);
            mSortTime.startAnimation(fabClose);
            mSortTime.setClickable(false);
            mSortTime.setEnabled(false);
            mSortAlpha.startAnimation(fabClose);
            mSortAlpha.setClickable(false);
            mSortAlpha.setEnabled(false);
            isOpen=false;
        }else{
            mSortArtist.startAnimation(fabOpen);
            mSortArtist.setClickable(true);
            mSortArtist.setEnabled(true);
            mSortTime.startAnimation(fabOpen);
            mSortTime.setClickable(true);
            mSortTime.setEnabled(true);
            mSortAlpha.startAnimation(fabOpen);
            mSortAlpha.setClickable(true);
            mSortAlpha.setEnabled(true);
            isOpen=true;
        }
    }

    private void getData() {
        mTracks = (ArrayList<Track>) playlst.getTracks();
        TrackListAdapter adapter = new TrackListAdapter(this, this, mTracks, playlst);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onTracksReceived(List<Track> tracks) {
        mTracks = (ArrayList<Track>) tracks;
        PreferenceUtils.saveAllTracks(getApplicationContext(), mTracks);
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
        playAudio(0);
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

    private int trackById(int id){
        int valor = 0;
        for(int i=0; i<mTracks.size() ;i++){
            if(mTracks.get(i).getId()==id){
                valor = i;
            }
        }
        return valor;
    }

    @Override
    public void onTrackLiked(int id) {
        if(mTracks.get(trackById(id)).isLiked()){
            mTracks.get(trackById(id)).setLiked(false);
        }else{
            mTracks.get(trackById(id)).setLiked(true);
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
    public void onFailure(Throwable throwable) {

    }

    @Override
    public void onTrackSelected(int index) {
        pause.setVisibility(View.VISIBLE);
        play.setVisibility(View.INVISIBLE);
        currentTrack = index;
        playAudio(index);
    }

    @Override
    public void onTrackAddSelected(int position, ArrayList<Track> tracks, Playlist p) {
        Intent intent = new Intent(getApplicationContext(), InfoTrackActivity.class);
        intent.putExtra("Trck", tracks.get(position));
        intent.putExtra("Playlst", p);
        startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
    }

    @Override
    public void onTrackSelectedLiked(int position) {
        trackManager.likeTrack(mTracks.get(position).getId(), PlaylistActivity.this);
    }


    @Override
    public void onPlaylistCreated(Playlist playlist) {

    }

    @Override
    public void onPlaylistFailure(Throwable throwable) {

    }

    @Override
    public void onPlaylistRecieved(List<Playlist> playlists) {

    }

    @Override
    public void onNoPlaylists(Throwable throwable) {

    }

    @Override
    public void onPlaylistSelected(Playlist playlist) {

    }

    @Override
    public void onTrackAdded(Playlist body) {

    }

    @Override
    public void onTrackAddFailure(Throwable throwable) {

    }

    @Override
    public void onAllPlaylistRecieved(List<Playlist> body) {

    }

    @Override
    public void onAllNoPlaylists(Throwable throwable) {

    }

    @Override
    public void onAllPlaylistFailure(Throwable throwable) {

    }

    @Override
    public void onTopRecieved(List<Playlist> topPlaylists) {

    }

    @Override
    public void onNoTopPlaylists(Throwable throwable) {

    }

    @Override
    public void onTopPlaylistsFailure(Throwable throwable) {

    }

    @Override
    public void onFollowingRecieved(List<Playlist> body) {

    }

    @Override
    public void onFollowingChecked(Follow body) {
        followingInfo = body;
        if(followingInfo.isFollowing()){
            follow.setText("Following");
            follow.setBackgroundResource(R.drawable.rectangle_small_gborder_green);;
            isFollowing=false;
        }else{
            follow.setText("Follow");
            follow.setBackgroundResource(R.drawable.rectangle_small_gborder_black);;
            isFollowing=true;
        }
    }

    @Override
    public void onFollowSuccessfull(Follow body) {
        followingInfo = body;
        if(followingInfo.isFollowing()){
            follow.setText("Following");
            follow.setBackgroundResource(R.drawable.rectangle_small_gborder_green);;
            isFollowing=false;
        }else{
            follow.setText("Follow");
            follow.setBackgroundResource(R.drawable.rectangle_small_gborder_black);;
            isFollowing=true;
        }
    }

    @Override
    public void onPlaylistRecived(Playlist playlist) {
        playlst = playlist;
        mTracks = (ArrayList<Track>) playlist.getTracks();
        TrackListAdapter adapter = new TrackListAdapter(this, this, mTracks, playlst);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onPlaylistDeleted(Playlist body) {
        finish();
        overridePendingTransition(R.anim.nothing,R.anim.nothing);
    }

    @Override
    public void onPlaylistDeleteFailure(Throwable throwable) {

    }

    @Override
    public void onDelete() {
        dialogEdit.cancelDialog();
        dialogEdit.showConfirmationDialog();
    }

    private void hideUIEdit(){
        newName.setVisibility(View.GONE);
        plyName.setVisibility(View.VISIBLE);
        plyName.setText(playlst.getName());
        mRecyclerView.setVisibility(View.VISIBLE);
        plyImg.setVisibility(View.VISIBLE);
        back2Main.setVisibility(View.VISIBLE);
        infoPlaylist.setVisibility(View.VISIBLE);
        shuffle.setVisibility(View.VISIBLE);
        addBunch.setVisibility(View.VISIBLE);
        acceptEdit.setVisibility(View.GONE);
        imgEdit.setVisibility(View.GONE);
        navigation.setVisibility(View.VISIBLE);
        actionButtons.setVisibility(View.VISIBLE);
        reproductor.setVisibility(View.VISIBLE);
        mseek.setVisibility(View.VISIBLE);
        play.setVisibility(View.VISIBLE);
        pause.setVisibility(View.VISIBLE);
        accessible.setVisibility(View.VISIBLE);
        if(!newName.getText().toString().matches("")){
            plyName.setText(newName.getText().toString());
        }
        KeyboardUtils.hideKeyboard(this);
    }

    private void showUIEdit(){
        newName.setVisibility(View.VISIBLE);
        plyName.setVisibility(View.GONE);
        newName.setText(playlst.getName());
        mRecyclerView.setVisibility(View.INVISIBLE);
        plyImg.setVisibility(View.GONE);
        back2Main.setVisibility(View.INVISIBLE);
        infoPlaylist.setVisibility(View.INVISIBLE);
        shuffle.setVisibility(View.GONE);
        addBunch.setVisibility(View.GONE);
        acceptEdit.setVisibility(View.VISIBLE);
        imgEdit.setVisibility(View.VISIBLE);
        if (playlst.getThumbnail() != null) {
            Picasso.get().load(playlst.getThumbnail()).into(imgEdit);
        }else{
            Picasso.get().load("https://community.spotify.com/t5/image/serverpage/image-id/25294i2836BD1C1A31BDF2/image-size/original?v=mpbl-1&px=-1").into(imgEdit);
        }
        navigation.setVisibility(View.GONE);
        actionButtons.setVisibility(View.GONE);
        reproductor.setVisibility(View.GONE);
        mseek.setVisibility(View.GONE);
        play.setVisibility(View.GONE);
        pause.setVisibility(View.GONE);
        accessible.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onEdit() {
        showUIEdit();
        dialogEdit.cancelDialog();
    }

    @Override
    public void onAccept() {
        dialogEdit.cancelDialog();
        pManager.deletePlaylist(playlst.getId(), this);
    }

    @Override
    public void onCancel() {
        dialogEdit.cancelDialog();
    }
}
