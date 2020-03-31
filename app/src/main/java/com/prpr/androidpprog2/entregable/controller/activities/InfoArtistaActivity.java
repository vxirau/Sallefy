package com.prpr.androidpprog2.entregable.controller.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.adapters.PlaylistAdapter;
import com.prpr.androidpprog2.entregable.controller.adapters.TrackListAdapter;
import com.prpr.androidpprog2.entregable.controller.callbacks.PlaylistListCallback;
import com.prpr.androidpprog2.entregable.controller.callbacks.TrackListCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.PlaylistCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.TrackCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.PlaylistManager;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.TrackManager;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.model.User;
import com.prpr.androidpprog2.entregable.model.UserToken;
import com.prpr.androidpprog2.entregable.utils.Constants;
import com.prpr.androidpprog2.entregable.utils.Session;

import java.util.ArrayList;
import java.util.List;

public class InfoArtistaActivity extends AppCompatActivity implements TrackListCallback, TrackCallback , PlaylistCallback{

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

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_user);
        artist = (User) getIntent().getSerializableExtra("User");
        initViews();

        TrackManager topmanager = new TrackManager(this);
        topmanager.getTopTracks(artist.getLogin(), this);

        PlaylistManager pmanager = new PlaylistManager(this);
        pmanager.showUserPlaylist(artist.getLogin(),this);

        TrackManager tmanager = new TrackManager(this);
        tmanager.getUserTracks(artist.getLogin(),this);
    }

    private void initViews(){

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

    @Override
    public void onTrackSelected(int index) {

    }

    @Override
    public void onTrackAddSelected(int position, ArrayList<Track> tracks, Playlist playlist) {
        Intent intent = new Intent(getApplicationContext(), InfoTrackActivity.class);
        intent.putExtra("Trck", tracks.get(position));
        intent.putExtra("Playlst", playlist);
        startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
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
}
