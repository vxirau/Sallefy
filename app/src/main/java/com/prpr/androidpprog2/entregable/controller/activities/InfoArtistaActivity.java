package com.prpr.androidpprog2.entregable.controller.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.adapters.PlaylistAdapter;
import com.prpr.androidpprog2.entregable.controller.adapters.TrackListAdapter;
import com.prpr.androidpprog2.entregable.controller.callbacks.PlaylistListCallback;
import com.prpr.androidpprog2.entregable.controller.callbacks.ServiceCallback;
import com.prpr.androidpprog2.entregable.controller.callbacks.TrackListCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.PlaylistCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.TrackCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.UserCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.PlaylistManager;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.TrackManager;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class InfoArtistaActivity extends AppCompatActivity implements TrackListCallback, TrackCallback, PlaylistCallback, ServiceCallback, UserCallback {

    private Button back;
    private RecyclerView topSongsRecycle;
    private RecyclerView playlistByArtistRecycle;
    private RecyclerView allSongsRecycle;

    private ArrayList<Track> artTracks;
    private ArrayList<Playlist> artPlaylist;
    private User artist;
    private TextView name;
    private TextView login;
    private TextView topSongs;
    private TextView plists;
    private TextView songs;
    private Button follow;
    private Follow followingInfo;
    private boolean isFollowing = false;
    private UserManager umanager;
    private ImageView profilePic;



    //----------------------------------------------------------------PART DE SERVICE--------------------------------------------------------------------------------
    private TextView trackTitle;
    private TextView followingTxt;
    private TextView trackAuthor;
    private SeekBar mSeekBar;
    private Button play;
    private Button pause;
    private ImageView im;
    private LinearLayout playing;
    private ReproductorService serv;
    private boolean servidorVinculat=false;
    public static final String Broadcast_PLAY_NEW_AUDIO = "com.prpr.androidpprog2.entregable.PlayNewAudio";



    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ReproductorService.LocalBinder binder = (ReproductorService.LocalBinder) service;
            serv = binder.getService();
            //serv.setmSeekBar(mSeekBar);
            servidorVinculat = true;
            serv.setUIControls(mSeekBar, trackTitle, trackAuthor, play, pause, im);
            serv.setSeekCallback(InfoArtistaActivity.this);
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

    @Override
    public void onResume() {
        super.onResume();
        if(servidorVinculat){
            serv.setSeekCallback(this);
        }
    }


    @Override
    public void onSeekBarUpdate(int progress, int duration, boolean isPlaying, String duracio) {
        if(isPlaying){
            mSeekBar.postDelayed(serv.getmProgressRunner(), 1000);
        }
        mSeekBar.setProgress(progress);
    }


    //----------------------------------------------------------------FIN DE LA PART DE SERVICE--------------------------------------------------------------------------------




    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_user);
        artist = (User) getIntent().getSerializableExtra("User");
        umanager = new UserManager(this);
        umanager.checkFollow(artist.getLogin(),this);
        initViews();

        TrackManager topmanager = new TrackManager(this);
        topmanager.getTopTracks(artist.getLogin(), this);

        PlaylistManager pmanager = new PlaylistManager(this);
        pmanager.showUserPlaylist(artist.getLogin(),this);

        TrackManager tmanager = new TrackManager(this);
        tmanager.getUserTracks(artist.getLogin(),this);
    }

    private void initViews(){

        play = findViewById(R.id.playButton);
        play.setEnabled(true);
        play.bringToFront();
        play.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                serv.resumeMedia();
            }
        });
        pause = findViewById(R.id.playPause);
        pause.setEnabled(true);
        pause.bringToFront();
        pause.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
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
        mSeekBar = (SeekBar) findViewById(R.id.dynamic_seekBar);

        playing = findViewById(R.id.reproductor);
        playing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReproductorActivity.class);
                startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
            }
        });


        back = findViewById(R.id.back2Main);
        back.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            finish();
            overridePendingTransition(R.anim.nothing,R.anim.nothing);
        }
        });

        name = findViewById(R.id.userName);
        String nom = artist.getFirstName() + " " + artist.getLastName();
        name.setText(nom);

        login = findViewById(R.id.userLogin);
        login.setText(artist.getLogin());

        profilePic = (ImageView) findViewById(R.id.profilePic);
        if(artist.getImageUrl() != null && !artist.getImageUrl().isEmpty()){
            Picasso.get().load(artist.getImageUrl()).into(profilePic, new Callback() {
                @Override
                public void onSuccess() {
                    Bitmap imageBitmap = ((BitmapDrawable) profilePic.getDrawable()).getBitmap();
                    RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                    imageDrawable.setCircular(true);
                    imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                    profilePic.setImageDrawable(imageDrawable);
                }
                @Override
                public void onError(Exception e) {
                    Picasso.get().load("https://user-images.githubusercontent.com/48185184/77792597-e939a400-7068-11ea-8ade-cd8b4e4ab7c9.png").into(profilePic);
                }
            });
        } else {
            Picasso.get().load("https://user-images.githubusercontent.com/48185184/77792597-e939a400-7068-11ea-8ade-cd8b4e4ab7c9.png").into(profilePic, new Callback() {
                @Override
                public void onSuccess() {
                    Bitmap imageBitmap = ((BitmapDrawable) profilePic.getDrawable()).getBitmap();
                    RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                    imageDrawable.setCircular(true);
                    imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                    profilePic.setImageDrawable(imageDrawable);
                }
                @Override
                public void onError(Exception e) {
                    Picasso.get().load("https://user-images.githubusercontent.com/48185184/77792597-e939a400-7068-11ea-8ade-cd8b4e4ab7c9.png").into(profilePic);
                }
            });
        }



        follow = (Button) findViewById(R.id.followUser);
        follow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                umanager.startStopFollowing(artist.getLogin(),InfoArtistaActivity.this);
            }
        });

        topSongsRecycle = (RecyclerView) findViewById(R.id.topSongsRecycle);
        LinearLayoutManager man = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        TrackListAdapter topadapter = new TrackListAdapter(this,this, artTracks , null);
        topSongsRecycle.setLayoutManager(man);
        topSongsRecycle.setAdapter(topadapter);

        playlistByArtistRecycle = (RecyclerView) findViewById(R.id.playlistByArtistRecycle);
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        PlaylistAdapter pladapter = new PlaylistAdapter(this,artPlaylist);
        playlistByArtistRecycle.setLayoutManager(manager);
        playlistByArtistRecycle.setAdapter(pladapter);

        allSongsRecycle = (RecyclerView) findViewById(R.id.allSongsRecycle);
        LinearLayoutManager manager2 = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        TrackListAdapter adapter = new TrackListAdapter(this,this, artTracks , null);
        allSongsRecycle.setLayoutManager(manager2);
        allSongsRecycle.setAdapter(adapter);

    }



    private void playAudio(int audioIndex) {

        PreferenceUtils.saveAllTracks(getApplicationContext(), artTracks);
        PreferenceUtils.saveTrackIndex(getApplicationContext(), audioIndex);
        PreferenceUtils.saveTrack(getApplicationContext(), artTracks.get(audioIndex));
        PreferenceUtils.savePlayID(getApplicationContext(), -6);

        if (!servidorVinculat) {
            Intent playerIntent = new Intent(this, ReproductorService.class);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            sendBroadcast(broadcastIntent);
        } else {
            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            sendBroadcast(broadcastIntent);
        }
        trackTitle.setText(artTracks.get(audioIndex).getName());
        trackAuthor.setText(artTracks.get(audioIndex).getUserLogin());
    }



    @Override
    public void onTrackSelected(int index) {
        pause.setVisibility(View.VISIBLE);
        play.setVisibility(View.INVISIBLE);
        playAudio(index);
    }

    @Override
    public void onTrackAddSelected(int position, ArrayList<Track> tracks, Playlist playlist) {
        Intent intent = new Intent(getApplicationContext(), InfoTrackActivity.class);
        intent.putExtra("Trck", tracks.get(position));
        intent.putExtra("Playlst", playlist);
        startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
    }

    @Override
    public void onTrackSelectedLiked(int position) {

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
        this.artTracks = (ArrayList) tracks;
        if(tracks.size()==0){
            songs = findViewById(R.id.noSongsAvailable);
            songs.setVisibility(View.VISIBLE);
        } else {
            TrackListAdapter trackListAdapter = new TrackListAdapter(this, this, this.artTracks, null);
            allSongsRecycle.setAdapter(trackListAdapter);
        }
    }

    @Override
    public void onCreateTrack(Track t) {

    }

    @Override
    public void onTopTracksRecieved(List<Track> tracks) {
        this.artTracks = (ArrayList) tracks;
        if(tracks.size() == 0){
            topSongs = findViewById(R.id.noTopAvailable);
            topSongs.setVisibility(View.VISIBLE);
        } else {
            TrackListAdapter trackListAdapter = new TrackListAdapter(this, this, this.artTracks, null);
            topSongsRecycle.setAdapter(trackListAdapter);
        }
    }

    @Override
    public void onNoTopTracks(Throwable throwable) {

    }

    @Override
    public void onTrackLiked(int id) {

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
    public void onAllPlaylistRecieved(List<Playlist> body) {
        this.artPlaylist = (ArrayList) body;
        if(body.size()==0){
            plists = findViewById(R.id.noPlistAvailable);
            plists.setVisibility(View.VISIBLE);
        } else {
            PlaylistAdapter padapt = new PlaylistAdapter(this, this.artPlaylist);
            padapt.setPlaylistCallback(this);
            playlistByArtistRecycle.setAdapter(padapt);
        }
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

    }

    @Override
    public void onFollowSuccessfull(Follow body) {

    }

    @Override
    public void onPlaylistRecived(Playlist playlist) {

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
    public void onUserUpdated(User body) {

    }


    @Override
    public void onTopUsersRecieved(List<User> body) {

    }

    @Override
    public void onUserUpdateFailure(Throwable throwable) {

    }

    @Override
    public void onUserSelected(User user) {

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
    public void onFollowSuccess(Follow body) {
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
    public void onFollowFailure(Throwable throwable) {

    }

    @Override
    public void onCheckSuccess(Follow body) {
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
    public void onCheckFailure(Throwable throwable) {

    }
}
