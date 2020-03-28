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
import com.prpr.androidpprog2.entregable.controller.callbacks.TrackListCallback;
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

public class InfoArtistaActivity extends AppCompatActivity implements TrackListCallback, TrackCallback {

    private Button back;
    private RecyclerView topSongsRecycle;
    private RecyclerView playlistByArtistRecycle;
    private RecyclerView allSongsRecycle;

    private ArrayList<Track> artTracks;
    private Playlist artPlaylist;
    private User artist;
    private TextView name;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_user);
        artist = (User) getIntent().getSerializableExtra("User");
        initViews();
        TrackManager tmanager = new TrackManager(this);
        tmanager.getUserTracks(artist.getLogin(),this);

    }

    private void initViews(){

        back = findViewById(R.id.back2Main);
        back.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            //controlar des d'on vens
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
            }
        });

        name = findViewById(R.id.userName);
        String nom = artist.getFirstName() + " " + artist.getLastName();
        name.setText(nom);

        /*topSongsRecycle = (RecyclerView) findViewById(R.id.allplaylists);
        LinearLayoutManager man = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        //adapter
        topSongsRecycle.setLayoutManager(man);
        //topSongsRecycle.setAdapter();

        playlistByArtistRecycle = (RecyclerView) findViewById(R.id.topPlayedPlaylists);
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        //adapter
        playlistByArtistRecycle.setLayoutManager(manager);
        //playlistByArtistRecycle.setAdapter();

         */

        allSongsRecycle = (RecyclerView) findViewById(R.id.allSongsRecycle);
        LinearLayoutManager manager2 = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        TrackListAdapter adapter = new TrackListAdapter(this,this, artTracks , artPlaylist);
        allSongsRecycle.setLayoutManager(manager2);
        allSongsRecycle.setAdapter(adapter);

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
        this.artTracks = (ArrayList) tracks;
        TrackListAdapter trackListAdapter = new TrackListAdapter(this, this, this.artTracks, this.artPlaylist);
        allSongsRecycle.setAdapter(trackListAdapter);
    }

    @Override
    public void onUserTracksReceived(List<Track> tracks) {

    }

    @Override
    public void onCreateTrack(Track t) {

    }

    @Override
    public void onFailure(Throwable throwable) {

    }
}
