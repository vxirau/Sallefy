package com.prpr.androidpprog2.entregable.controller.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.adapters.PlaylistAdapter;
import com.prpr.androidpprog2.entregable.controller.adapters.TrackListAdapter;
import com.prpr.androidpprog2.entregable.controller.adapters.UserAdapter;
import com.prpr.androidpprog2.entregable.controller.adapters.UserPlaylistAdapter;
import com.prpr.androidpprog2.entregable.controller.callbacks.TrackListCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.PlaylistCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.TrackCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.PlaylistManager;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.TrackManager;
import com.prpr.androidpprog2.entregable.model.Follow;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.model.User;
import com.prpr.androidpprog2.entregable.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class UserMainActivity extends AppCompatActivity implements PlaylistCallback, TrackCallback, TrackListCallback {

    private TextView tvUserPlaylists;
    private TextView tvUserTracks;
    private TextView tvUserStatistics;
    private TextView tvUserFollowed;

    private Playlist myPlaylist;
    private ArrayList<Track> myTracks;
    private ArrayList<Playlist> myPlaylists;

    private RecyclerView mRecyclerView;
    private RecyclerView mRecyclerView2;

    private PlaylistManager playlistManager;
    private TrackManager trackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        initGeneralViews();
        initPlaylistViews();



        playlistManager = new PlaylistManager(this);
        playlistManager.getAllMyPlaylists(this);




    }

    void initGeneralViews(){

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


        tvUserPlaylists = (TextView) findViewById(R.id.user_playlists_title);
        tvUserPlaylists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initPlaylistViews();
            }
        });

        tvUserTracks = (TextView) findViewById(R.id.user_my_songs_title);
        tvUserTracks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initTracksViews();
            }
        });

        tvUserStatistics = (TextView) findViewById(R.id.user_statistics_title);
        tvUserStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initStatisticsViews();
            }
        });

        tvUserFollowed = (TextView) findViewById(R.id.user_my_followed_title);
        tvUserFollowed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initMyFollowedViews();
            }
        });


        mRecyclerView = (RecyclerView) findViewById(R.id.userPlaylistsRecyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        UserPlaylistAdapter adapter = new UserPlaylistAdapter(this, null);
        adapter.setPlaylistCallback(this);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(adapter);
    }

    void initPlaylistViews(){

        UserPlaylistsFragment userPlaylistsFragment = new UserPlaylistsFragment();

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.relativeUserLayout, userPlaylistsFragment, userPlaylistsFragment.getTag())
                .commit();




    }
    void initTracksViews(){

        UserTracksFragment userTracksFragment = new UserTracksFragment();

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.relativeUserLayout, userTracksFragment, userTracksFragment.getTag())
                .commit();



    }

    void initStatisticsViews(){

        UserStatisticsFragment userStatisticsFragment = new UserStatisticsFragment();

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.relativeUserLayout, userStatisticsFragment, userStatisticsFragment.getTag())
                .commit();

    }

    void initMyFollowedViews(){

        UserFollowedFragment userFollowedFragment = new UserFollowedFragment();

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.relativeUserLayout, userFollowedFragment, userFollowedFragment.getTag())
                .commit();

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
        this.myPlaylists = (ArrayList) playlists;
        UserPlaylistAdapter playlistAdapter = new UserPlaylistAdapter(this, this.myPlaylists);
        playlistAdapter.setPlaylistCallback(this);
        //mRecyclerView.setAdapter(playlistAdapter);
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

    }

    @Override
    public void onFollowSuccessfull(Follow body) {

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
    public void onTrackSelected(int index) {

    }

    @Override
    public void onTrackAddSelected(int position, ArrayList<Track> tracks, Playlist playlist) {

    }
}
