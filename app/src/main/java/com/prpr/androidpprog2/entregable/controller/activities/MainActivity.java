package com.prpr.androidpprog2.entregable.controller.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.telecom.ConnectionService;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.adapters.PlaylistAdapter;
import com.prpr.androidpprog2.entregable.controller.adapters.UserAdapter;
import com.prpr.androidpprog2.entregable.controller.dialogs.ErrorDialog;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.PlaylistCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.TrackCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.UserCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.PlaylistManager;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.TrackManager;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.UserManager;
import com.prpr.androidpprog2.entregable.controller.music.ReproductorService;
import com.prpr.androidpprog2.entregable.model.DB.ObjectBox;
import com.prpr.androidpprog2.entregable.model.DB.SavedCache;
import com.prpr.androidpprog2.entregable.model.DB.SavedCache_;
import com.prpr.androidpprog2.entregable.model.DB.UtilFunctions;
import com.prpr.androidpprog2.entregable.model.Follow;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.model.User;
import com.prpr.androidpprog2.entregable.model.UserToken;
import com.prpr.androidpprog2.entregable.model.passwordChangeDto;
import com.prpr.androidpprog2.entregable.utils.ConnectivityService;
import com.prpr.androidpprog2.entregable.utils.Constants;
import com.prpr.androidpprog2.entregable.utils.PreferenceUtils;
import com.prpr.androidpprog2.entregable.utils.Session;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import io.objectbox.BoxStore;
import io.objectbox.android.AndroidObjectBrowser;
import io.objectbox.android.BuildConfig;


public class MainActivity extends AppCompatActivity implements PlaylistCallback, UserCallback, TrackCallback {

    private FloatingActionButton mes;
    private FloatingActionButton btnNewPlaylist;
    private FloatingActionButton pujarCanco;
    private FloatingActionButton nouGenere;
    Animation fabOpen, fabClose, rotateForward, rotateBackward;
    boolean isOpen = false;
    private TextView trackTitle;
    private TextView followingTxt;
    private TextView trackAuthor;

    private TextView titol;

    private SeekBar mSeekBar;
    private Button play;
    private Button pause;
    private ImageView im;
    private boolean sameUser = true;

    private RecyclerView allPlaylistRecycle;
    private RecyclerView topPlaylistsRecycle;
    private RecyclerView topUsersReycle;
    private RecyclerView folloingPlaylistRecycle;


    private ArrayList<Playlist> allPlaylists;
    private ArrayList<Playlist> topPlaylists;
    private ArrayList<User> topUsers;
    private ArrayList<Playlist> followingPlaylists;
    private ArrayList<Playlist> discover;

    private ArrayList<User> top4;

    private PlaylistManager pManager;
    private UserManager usrManager;
    private LinearLayout playing;


    //---------BY SALLEFY---------
    private LinearLayout topLeft;
    private ImageView topLeftImg;
    private TextView topLeftText;

    private LinearLayout topRight;
    private ImageView topRightImg;
    private TextView topRightText;

    private LinearLayout bottomLeft;
    private ImageView bottomLeftImg;
    private TextView bottomLeftText;

    private ArrayList<Playlist> top4Playlists;
    private int sallefyIndex=0;
    private boolean done = false;
    private boolean isCache = false;

    private LinearLayout bottomRight;
    private ImageView bottomRightImg;
    private TextView bottomRightText;

    private String urlRecieved;

    //---------------------------

    //----------------------------------------------------------------PART DE SERVICE--------------------------------------------------------------------------------
    public static final String Broadcast_PLAY_NEW_AUDIO = "com.prpr.androidpprog2.entregable.PlayNewAudio";
    private ReproductorService serv;
    private boolean servidorVinculat=false;
    private ArrayList<Track> audioList;
    private int audioIndex = -1;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ReproductorService.LocalBinder binder = (ReproductorService.LocalBinder) service;
            serv = binder.getService();
            serv.setmSeekBar(mSeekBar);
            servidorVinculat = true;
            serv.setMainActivity(MainActivity.this);
            serv.setUIControls(mSeekBar, trackTitle, trackAuthor, play, pause, im);
            boolean shuf = PreferenceUtils.getShuffle(getApplicationContext());
            serv.setShuffle(shuf);
            if(sameUser){
                serv.stopOnStart();
                sameUser=false;
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
            serv.setMainActivity(MainActivity.this);
        }
    }

    private int findIndex(Track t){
        int index=-1;
        for(int i=0; i<audioList.size() ;i++){
            if(t.getId().equals(audioList.get(i).getId()) && t.getName().equals(audioList.get(i).getName()) && t.getUrl().equals(audioList.get(i).getUrl())){
                index = i;
            }
        }
        return index;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadPreviousSession() {
        audioList = PreferenceUtils.getAllTracks(getApplicationContext());
        Track t = PreferenceUtils.getTrack(getApplicationContext());
        if(t!=null){
            audioIndex = findIndex(t);
            if(audioIndex==-1){
                ErrorDialog.getInstance(this).showErrorDialog("Error loading previous session");
            }else{
                start();
                Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
                sendBroadcast(broadcastIntent);
            }
        }else{
            ErrorDialog.getInstance(this).showErrorDialog("Error loading previous session");
        }
    }

    private void start() {
        Intent playerIntent = new Intent(this, ReproductorService.class);
        startService(playerIntent);
        bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    public void onResume() {
        super.onResume();
        if(servidorVinculat && serv!=null){
            serv.setUIControls(mSeekBar, trackTitle, trackAuthor, play, pause, im);
            serv.updateUI();
        }
        pManager.getAllPlaylists(this);
        pManager.getTopPlaylists(this);
        pManager.getFollowingPlaylists(this);
    }


    //----------------------------------------------------------------FIN DE LA PART DE SERVICE--------------------------------------------------------------------------------

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent intent = new Intent(this, ConnectivityService.class);
        intent.putExtra(ConnectivityService.TAG_INTERVAL, 3);
        startService(intent);

        if (getIntent().getSerializableExtra("sameUser") != null) {
            sameUser = (boolean) getIntent().getSerializableExtra("sameUser");
        } else {
            sameUser = false;
        }
        if(getIntent().getSerializableExtra("url") != null){
            urlRecieved = (String) getIntent().getSerializableExtra("url");

        }

        UserToken userToken = Session.getInstance(this).getUserToken();
        pManager = new PlaylistManager(this);
        usrManager = new UserManager(this);
        pManager.getAllPlaylists(this);
        pManager.getTopPlaylists(this);
        usrManager.getTopUsers(this);

        if (UtilFunctions.needsSallefyUsers() && !UtilFunctions.noInternet(this)) {
            usrManager.getSallefyUsers(sallefyIndex, this, false);
        } else {
            isCache = true;
            top4Playlists = ObjectBox.get().boxFor(SavedCache.class).get(1).retrieveSallefyPlaylists();
        }
        pManager.getFollowingPlaylists(this);
        initViews();
        btnNewPlaylist.setEnabled(true);

        if (sameUser) {
            loadPreviousSession();
        }

    }

    @Override
    public void onBackPressed() {
        Log.d("Sallefy", "backPressed cancelled");
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

        loadBySallefySection();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        followingTxt= findViewById(R.id.noFollow);
        String buenas = "";

        Calendar calendar = Calendar.getInstance();
        long hours= calendar.get(Calendar.HOUR_OF_DAY);
        if(hours>5 && hours<12){
            buenas = "Good Morning ";
        }else if(hours>=12 && hours<19){
            buenas = "Good Evening ";
        }else if((hours >=19 && hours <=24) || (hours > 0 && hours <= 5)){
            buenas = "Good Night ";
        }

        String name = "";
        if(Session.getInstance().getUser().getFirstName()!=null){
            name = Session.getInstance().getUser().getFirstName();
        }else{
            name = Session.getInstance().getUser().getLogin();;
        }

        titol = findViewById(R.id.titolActivitat);
        titol.setText(buenas + name + "!");

        play = findViewById(R.id.playButton);
        play.setEnabled(true);
        play.bringToFront();
        play.setVisibility(View.VISIBLE);
        play.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
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
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                serv.pauseMedia();
            }
        });

        playing = findViewById(R.id.reproductor);
        playing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(serv!=null&& !trackTitle.getText().toString().equals("")){
                    Intent intent = new Intent(getApplicationContext(), ReproductorActivity.class);
                    startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
                    overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                }else{
                    ErrorDialog.getInstance(MainActivity.this).showErrorDialog("You haven't selected a song yet!");
                }
            }
        });

        trackAuthor = findViewById(R.id.dynamic_artist);
        trackAuthor.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        trackAuthor.setSelected(true);
        trackAuthor.setSingleLine(true);
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
        if(UtilFunctions.noInternet(getApplicationContext())){
            mes.setVisibility(View.GONE);
        }
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

        nouGenere= findViewById(R.id.creaGenere);
        nouGenere.setEnabled(false);
        nouGenere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
                Intent intent = new Intent(getApplicationContext(), NewGenreActivity.class);
                startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
            }
        });


        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        rotateForward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);

        mSeekBar = (SeekBar) findViewById(R.id.dynamic_seekBar);


    }

    private void loadBySallefySection() {
        topLeft = findViewById(R.id.id11);
        topRight = findViewById(R.id.id12);
        bottomLeft = findViewById(R.id.id21);
        bottomRight = findViewById(R.id.id22);

        topLeftImg = findViewById(R.id.image11);
        topRightImg = findViewById(R.id.image12);
        bottomLeftImg = findViewById(R.id.image21);
        bottomRightImg = findViewById(R.id.image22);

        topLeftText = findViewById(R.id.playlist11);
        topRightText = findViewById(R.id.playlist12);
        bottomLeftText = findViewById(R.id.playlist21);
        bottomRightText = findViewById(R.id.playlist22);

        topLeftText.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        topLeftText.setSelected(true);
        topLeftText.setSingleLine(true);

        topRightText.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        topRightText.setSelected(true);
        topRightText.setSingleLine(true);

        bottomLeftText.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        bottomLeftText.setSelected(true);
        bottomLeftText.setSingleLine(true);

        bottomRightText.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        bottomRightText.setSelected(true);
        bottomRightText.setSingleLine(true);

        topLeft.setVisibility(View.INVISIBLE);
        topRight.setVisibility(View.INVISIBLE);
        bottomLeft.setVisibility(View.INVISIBLE);
        bottomRight.setVisibility(View.INVISIBLE);

        if(top4Playlists !=null && top4Playlists.size() > 0){
            getUsersFromPlaylists();
            isCache = true;
            saQuedaoCorto();
        }

    }

    private void getUsersFromPlaylists() {
        top4 = new ArrayList<>();
        for(Playlist p : top4Playlists){
            String[] arr = p.getName().split(" ");
            User u = new User(p.getThumbnail(), p.getUserLogin(), arr[2]);
            top4.add(u);
        }
    }

    private void animateFab(){
        if(isOpen){
            btnNewPlaylist.startAnimation(fabClose);
            pujarCanco.startAnimation(fabClose);
            nouGenere.startAnimation(fabClose);
            btnNewPlaylist.setClickable(false);
            pujarCanco.setClickable(false);
            btnNewPlaylist.setEnabled(false);
            pujarCanco.setEnabled(false);
            nouGenere.setEnabled(false);
            isOpen=false;
        }else{
            btnNewPlaylist.startAnimation(fabOpen);
            pujarCanco.startAnimation(fabOpen);
            nouGenere.startAnimation(fabOpen);
            btnNewPlaylist.setClickable(true);
            pujarCanco.setClickable(true);
            btnNewPlaylist.setEnabled(true);
            pujarCanco.setEnabled(true);
            nouGenere.setEnabled(true);
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


    private void removePrivate(){
        for(int i=0; i<this.allPlaylists.size() ;i++){
            if(!this.allPlaylists.get(i).isPublicAccessible()){
                this.allPlaylists.remove(i);
            }
        }
    }

    @Override
    public void onAllPlaylistRecieved(List<Playlist> body) {
        this.allPlaylists = (ArrayList) body;
        removePrivate();
        Collections.shuffle(this.allPlaylists);
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
    public void onPlaylistToUpdated(Playlist body) {

    }


    @Override
    public void onTrackAddFailure(Throwable throwable) {

    }

    @Override
    public void onAllNoPlaylists(Throwable throwable) {

    }

    @Override
    public void onAllPlaylistFailure(Throwable throwable) {
        if(UtilFunctions.noInternet(getApplicationContext())){
            onAllPlaylistRecieved(ObjectBox.get().boxFor(SavedCache.class).get(1).retrieveAllPlaylists());
        }
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
        if(UtilFunctions.noInternet(getApplicationContext())){
            onTopRecieved(ObjectBox.get().boxFor(SavedCache.class).get(1).retrievetopPlaylists());
        }
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
    public void onPlaylistRecived(Playlist playlist) {

    }

    @Override
    public void onPlaylistDeleted(Playlist body) {

    }

    @Override
    public void onPlaylistDeleteFailure(Throwable throwable) {

    }

    @Override
    public void onAllMyPlaylistFailure(Throwable throwable) {

    }

    @Override
    public void onFollowingPlaylistsFailure(Throwable throwable) {
        if(UtilFunctions.noInternet(getApplicationContext())){
            onFollowingRecieved(ObjectBox.get().boxFor(SavedCache.class).get(1).retrieveFollowingPlaylists());
        }
    }


    @Override
    public void onUserInfoReceived(User userData) {

    }

    @Override
    public void onUserUpdated(User body) {

    }

    @Override
    public void onAccountSaved(User body) {

    }


    @Override
    public void onTopUsersRecieved(List<User> body) {
        this.topUsers = (ArrayList) body;
        UserAdapter p3 = new UserAdapter(this, this.topUsers);
        p3.setUserCallback(this);
        topUsersReycle.setAdapter(p3);
    }

    @Override
    public void onUserUpdateFailure(Throwable throwable) {

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
    public void onFollowSuccess(Follow body) {

    }

    @Override
    public void onAccountSavedFailure(Throwable throwable) {

    }

    @Override
    public void onFollowFailure(Throwable throwable) {

    }

    @Override
    public void onCheckSuccess(Follow body) {

    }

    @Override
    public void onCheckFailure(Throwable throwable) {

    }

    @Override
    public void onTopUsersFailure(Throwable throwable) {
        if(UtilFunctions.noInternet(getApplicationContext())){
            onTopUsersRecieved(ObjectBox.get().boxFor(SavedCache.class).get(1).retreiveTopUsers());
        }
    }

    @Override
    public void onFollowedUsersFailure(Throwable t) {

    }

    @Override
    public void onFollowersRecieved(ArrayList<User> body) {

    }

    @Override
    public void onFollowersFailed(Throwable throwable) {

    }

    @Override
    public void onFollowersFailure(Throwable throwable) {

    }

    @Override
    public void onPasswordUpdated(passwordChangeDto pd) {

    }

    @Override
    public void onPasswordUpdatedFailure(Throwable throwable) {

    }

    @Override
    public void onSallefySectionRecieved(List<User> body, boolean recieved) {
        if (recieved && body.size()>0) {
            ArrayList<User> r = clearArray(body);
            for(User h : r){
                if(!existsInTop(top4, h)){
                    top4.add(h);
                }
            }
            if (top4.size() < 4) {
                sallefyIndex++;
                UserManager.getInstance(this).getSallefyUsers(sallefyIndex, this, true);
            }else{
                boolean noInternet = UtilFunctions.noInternet(this);
                for(int i=0; i<top4.size() ;i++){
                    switch (i){
                        case 0:
                            topLeftText.setText("This is " + top4.get(i).getFirstName());
                            if(noInternet && isCache){
                                Bitmap myBitmap = BitmapFactory.decodeFile(top4Playlists.get(i).getThumbnail());
                                topLeftImg.setImageBitmap(myBitmap);
                            }else{
                                Picasso.get().load(top4.get(i).getImageUrl()).into(topLeftImg);
                            }
                            topLeft.setVisibility(View.VISIBLE);
                            break;
                        case 1:
                            topRightText.setText("This is " + top4.get(i).getFirstName());
                            if(noInternet && isCache){
                                Bitmap myBitmap = BitmapFactory.decodeFile(top4Playlists.get(i).getThumbnail());
                                topRightImg.setImageBitmap(myBitmap);
                            }else{
                                Picasso.get().load(top4.get(i).getImageUrl()).into(topRightImg);
                            }
                            topRight.setVisibility(View.VISIBLE);
                            break;
                        case 2:
                            bottomLeftText.setText("This is " + top4.get(i).getFirstName());
                            if(noInternet && isCache){
                                Bitmap myBitmap = BitmapFactory.decodeFile(top4Playlists.get(i).getThumbnail());
                                bottomLeftImg.setImageBitmap(myBitmap);
                            }else{
                                Picasso.get().load(top4.get(i).getImageUrl()).into(bottomLeftImg);
                            }
                            bottomLeft.setVisibility(View.VISIBLE);
                            break;
                        case 3:
                            bottomRightText.setText("This is " + top4.get(i).getFirstName());
                            if(noInternet && isCache){
                                Bitmap myBitmap = BitmapFactory.decodeFile(top4Playlists.get(i).getThumbnail());
                                bottomRightImg.setImageBitmap(myBitmap);
                            }else{
                                Picasso.get().load(top4.get(i).getImageUrl()).into(bottomRightImg);
                            }
                            bottomRight.setVisibility(View.VISIBLE);
                            break;
                        default:
                            break;
                    }
                    if(isCache){
                        onUserTracksReceived(top4Playlists.get(i).getTracks());
                    }else{
                        TrackManager.getInstance(this).getUserTracks(top4.get(i).getLogin(), this);
                    }
                }
            }
        } else if(body.size()==0){
            done = true;
            isCache = false;
            saQuedaoCorto();
        }else{
            top4 = clearArray(body);
            top4Playlists = new ArrayList<>();
            if (top4.size() < 4) {
                sallefyIndex++;
                UserManager.getInstance(this).getSallefyUsers(sallefyIndex, this, true);
            }
        }
    }

    private void saQuedaoCorto() {
        topLeft.setVisibility(View.INVISIBLE);
        topRight.setVisibility(View.INVISIBLE);
        bottomLeft.setVisibility(View.INVISIBLE);
        bottomRight.setVisibility(View.INVISIBLE);
        boolean noInternet = UtilFunctions.noInternet(this);
        for(int i=0; i<top4.size() ;i++){
            switch (i){
                case 0:
                    topLeftText.setText("This is " + top4.get(i).getFirstName());
                    if(noInternet && isCache){
                        Picasso.get().load(R.drawable.default_cover).into(topLeftImg);
                    }else{
                        Picasso.get().load(top4.get(i).getImageUrl()).into(topLeftImg);
                    }
                    topLeft.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    topRightText.setText("This is " + top4.get(i).getFirstName());
                    if(noInternet && isCache){
                        Picasso.get().load(R.drawable.default_cover).into(topLeftImg);
                    }else{
                        Picasso.get().load(top4.get(i).getImageUrl()).into(topRightImg);
                    }
                    topRight.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    bottomLeftText.setText("This is " + top4.get(i).getFirstName());
                    if(noInternet && isCache){
                        Picasso.get().load(R.drawable.default_cover).into(topLeftImg);
                    }else{
                        Picasso.get().load(top4.get(i).getImageUrl()).into(bottomLeftImg);
                    }
                    bottomLeft.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    bottomRightText.setText("This is " + top4.get(i).getFirstName());
                    if(noInternet && isCache){
                        Picasso.get().load(R.drawable.default_cover).into(topLeftImg);
                    }else{
                        Picasso.get().load(top4.get(i).getImageUrl()).into(bottomRightImg);
                    }
                    bottomRight.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
            if(isCache){
                onUserTracksReceived(top4Playlists.get(i).getTracks());
            }else{
                TrackManager.getInstance(this).getUserTracks(top4.get(i).getLogin(), this);
            }
        }

    }

    private ArrayList<User> clearArray(List<User> body) {
        ArrayList<User> top = new ArrayList<>();
        boolean ok = false;
        for(int i=0; i<body.size() && !ok ;i++){
            if(!existsInTop(top, body.get(i)) && body.get(i).getFirstName()!=null && (body.get(i).getImageUrl()!=null && !body.get(i).getImageUrl().equals(""))){
                if(isValidUrl(body.get(i).getImageUrl())){
                    top.add(body.get(i));
                }
            }
            if(body.get(i).getId().equals(83)){
                System.out.println("martin");
            }
            if(top.size()==4){
                ok = true;
            }
        }

        return top;
    }

    private boolean isValidUrl(String imageUrl) {
        return URLUtil.isValidUrl(imageUrl);
    }

    private boolean existsInTop(ArrayList<User> top, User u) {
        boolean exist = false;
        for(User usr : top){
            if(usr.getId().equals(u.getId())){
                exist = true;
            }
        }
        return exist;
    }

    @Override
    public void onSallefySectionFailure(Throwable throwable) {

    }


    @Override
    public void onFailure(Throwable throwable) {

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
        int trackUser = getUserFromTracks(tracks);
        Playlist p = new Playlist("This is " + top4.get(trackUser).getFirstName(), new User("Sallefy"), tracks, top4.get(trackUser).getImageUrl());
        top4Playlists.add(p);
        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PlaylistActivity.class);
                intent.putExtra("Playlst", p);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        };
        switch (trackUser){
            case 0:
                topLeft.setOnClickListener(click);
                break;
            case 1:
                topRight.setOnClickListener(click);
                break;
            case 2:
                bottomLeft.setOnClickListener(click);
                break;
            case 3:
                bottomRight.setOnClickListener(click);
                break;
            default:
                break;

        }
        if((top4Playlists.size()==4 || done) && !isCache/*&& UtilFunctions.needsSallefyUsers()*/){
            storeSallefyCache();
        }
    }

    private void storeSallefyCache() {
        SavedCache c = ObjectBox.get().boxFor(SavedCache.class).get(1);
        c.saveSallefyPlaylists(top4Playlists);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        c.setSallefyDate(dateFormat.format(cal.getTime()));
        ObjectBox.get().boxFor(SavedCache.class).put(c);
    }

    private int getUserFromTracks(List<Track> tracks) {
        int index = 0;
        for(int i=0; i<top4.size() ;i++){
            if(top4.get(i).getImageUrl().equals(tracks.get(0).getUser().getImageUrl())){
                index = i;
            }
        }
        return index;
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
}
