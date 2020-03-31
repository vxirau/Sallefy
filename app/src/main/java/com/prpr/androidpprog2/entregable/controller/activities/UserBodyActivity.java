package com.prpr.androidpprog2.entregable.controller.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Fragment;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.adapters.TrackListAdapter;
import com.prpr.androidpprog2.entregable.controller.adapters.UserPlaylistAdapter;
import com.prpr.androidpprog2.entregable.controller.callbacks.TrackListCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.PlaylistCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.TrackCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.PlaylistManager;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.TrackManager;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class UserBodyActivity extends AppCompatActivity implements UserCommunicationInterface, PlaylistCallback, TrackListCallback, TrackCallback{

    Fragment[] myFragments;

    private RecyclerView mRecyclerView;
    private ArrayList<Playlist> myPlaylists;

    private PlaylistManager playlistManager;

    private Button btnCreateNewPlaylist;
    private Button btnFilterPlaylists;
    private FloatingActionButton btnSettings;

    private TextView tvGoToMySongs;
    private TextView tvGoToStatistics;
    private TextView tvCreateNewPlaylist;



    private Button btnCreateNewSong;
    private FloatingActionButton btnSettings2;


    private TextView tvGoToMyPlaylists;
    private TextView tvGoToStatistics2;
    private TextView tvUploadNewSong;
    private Button btnFilterTracks;

    private TrackManager trackManager;

    private RecyclerView mRecyclerView2;

    private Playlist myPlaylist;
    private ArrayList<Track> myTracks;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_user_body);

        myFragments = new Fragment[3];

        myFragments[0] = new UserPlaylistFragment();
        myFragments[1] = new UserTracksFragment();
        myFragments[2] = new UserStatisticsFragment();

        Bundle extras = getIntent().getExtras();

        userMenu(extras.getInt("Button Pressed"));

        /*switch (extras.getByte("Button Pressed")){
            case 0:
                initPlaylistInfo();
                playlistManager = new PlaylistManager(this);
                playlistManager.getAllMyPlaylists(this);
                break;
            case 1:
                initTracksInfo();
                trackManager = new TrackManager(this);
                trackManager.getOwnTracks(this);
                break;
            case 2:
                initStatisticsInfo();

        }*/


    }

    void initPlaylistInfo(){

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.menu);
        navigation.setSelectedItemId(R.id.perfil);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
                        return true;
                    case R.id.buscar:
                        Intent intent2 = new Intent(getApplicationContext(), SearchActivity.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent2, Constants.NETWORK.LOGIN_OK);
                        return true;
                    case R.id.perfil:
                        return true;
                }
                return false;
            }
        });

        btnCreateNewPlaylist = (Button) findViewById(R.id.btn_create_new_playlist);
        btnCreateNewPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewPlaylistActivity.class);
                startActivity(intent);
            }
        });

        tvCreateNewPlaylist = findViewById(R.id.tv_create_new_playlist);
        tvCreateNewPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewPlaylistActivity.class);
                startActivity(intent);
            }
        });





        btnSettings = findViewById(R.id.configButton);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);

            }
        });
        //TODO --> Filter Button Implementation
        btnFilterPlaylists = findViewById(R.id.btn_filter_user_playlists);
        btnFilterPlaylists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //doFilterPlaylists();
            }
        });


        mRecyclerView = (RecyclerView) findViewById(R.id.userPlaylistsRecyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        UserPlaylistAdapter adapter = new UserPlaylistAdapter(this, null);
        adapter.setPlaylistCallback(this);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(adapter);
    }

    void initTracksInfo(){

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.menu);
        navigation.setSelectedItemId(R.id.perfil);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
                        return true;
                    case R.id.buscar:
                        Intent intent2 = new Intent(getApplicationContext(), SearchActivity.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent2, Constants.NETWORK.LOGIN_OK);
                        return true;
                    case R.id.perfil:
                        return true;
                }
                return false;
            }
        });

        btnCreateNewSong = (Button) findViewById(R.id.btn_add_new_song);
        btnCreateNewSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UploadActivity.class);
                startActivity(intent);
            }
        });

        tvUploadNewSong = findViewById(R.id.tv_add_new_song);
        tvUploadNewSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UploadActivity.class);
                startActivity(intent);
            }
        });


        btnSettings2 = findViewById(R.id.configButton);
        btnSettings2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);

            }
        });

        btnFilterTracks = findViewById(R.id.btn_filter_user_songs);
        btnFilterTracks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //doFilterTracks();
            }
        });

        mRecyclerView2 = (RecyclerView) findViewById(R.id.userTracksRecyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        TrackListAdapter adapter = new TrackListAdapter(this, this, myTracks, myPlaylist);
        mRecyclerView2.setLayoutManager(manager);
        mRecyclerView2.setAdapter(adapter);

    }
    void initStatisticsInfo(){}

    @Override
    public void userMenu(int whichButton){

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.userBodyRelativeLayout, myFragments[whichButton]);
        fragmentTransaction.commit();
    }




    @Override
    public void onPlaylistCreated(Playlist playlist) {

    }

    @Override
    public void onPlaylistFailure(Throwable throwable) {

    }

    @Override
    public void onPlaylistRecieved(List<Playlist> playlists) {
        this.myPlaylists = (ArrayList) playlists;
        UserPlaylistAdapter playlistAdapter = new UserPlaylistAdapter(this, this.myPlaylists);
        playlistAdapter.setPlaylistCallback(this);
        mRecyclerView.setAdapter(playlistAdapter);
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
    public void onTrackSelected(int index) {

    }

    @Override
    public void onTrackAddSelected(int position, ArrayList<Track> tracks, Playlist playlist) {

    }

    @Override
    public void onTracksReceived(List<Track> tracks) {

    }

    @Override
    public void onNoTracks(Throwable throwable) {

    }

    @Override
    public void onPersonalTracksReceived(List<Track> tracks) {
        this.myTracks = (ArrayList) tracks;
        TrackListAdapter trackListAdapter = new TrackListAdapter(this, this, this.myTracks, this.myPlaylist);
        mRecyclerView.setAdapter(trackListAdapter);
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
    public void onFailure(Throwable throwable) {

    }
}
