package com.prpr.androidpprog2.entregable.controller.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.adapters.BunchTrackListAdapter;
import com.prpr.androidpprog2.entregable.controller.adapters.TrackListAdapter;
import com.prpr.androidpprog2.entregable.controller.callbacks.BunchTrackListCallback;
import com.prpr.androidpprog2.entregable.controller.callbacks.TrackListCallback;
import com.prpr.androidpprog2.entregable.controller.dialogs.ErrorDialog;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.PlaylistCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.TrackCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.PlaylistManager;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.TrackManager;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class AddSongsBunchActivity extends AppCompatActivity implements TrackCallback, BunchTrackListCallback, PlaylistCallback {

    private Button upload;
    private Button accept;
    private Button cancel;
    private ArrayList<Track> myTracks;
    private ArrayList<Track> bunch = new ArrayList<>();
    private RecyclerView llistaCancons;
    private TrackManager tManager;
    private Playlist ply;
    private PlaylistManager pManager;
    private PlaylistCallback pCall;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addsongs_bunch);
        initComponents();
        getData();
        pCall = this;
        pManager = new PlaylistManager(this);
        tManager = new TrackManager(this);
        tManager.getOwnTracks(this);
        ply = (Playlist) getIntent().getSerializableExtra("Playlst");
    }

    private boolean existsInPlaylist(List<Track> t, Track track){
        boolean exists = false;
        for(Track tk : t){
            if(tk.getName().equals(track.getName())){
                exists=true;
            }
        }
        return exists;
    }

    private void initComponents() {

        llistaCancons = (RecyclerView) findViewById(R.id.llistaCancons);
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        BunchTrackListAdapter adapter = new BunchTrackListAdapter(this, this, null);
        llistaCancons.setLayoutManager(manager);
        llistaCancons.setAdapter(adapter);
        upload= findViewById(R.id.UploadButton);
        upload.setEnabled(true);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UploadActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("agas", true);
                intent.putExtra("playlist", ply);
                startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
            }
        });
        accept= findViewById(R.id.AcceptarButton);
        accept.setEnabled(true);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ply.getTracks().addAll(bunch);
                pManager.updatePlaylist(ply, pCall);

            }
        });
        cancel= findViewById(R.id.CancelButton);
        cancel.setEnabled(true);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        llistaCancons= findViewById(R.id.llistaCancons);


    }

    private void getData() {



    }


    @Override
    public void onTracksReceived(List<Track> tracks) {
    }

    @Override
    public void onNoTracks(Throwable throwable) {

    }

    @Override
    public void onPersonalTracksReceived(List<Track> tracks) {
        myTracks = (ArrayList)tracks;
        BunchTrackListAdapter adapter = new BunchTrackListAdapter(this, this, myTracks);
        llistaCancons.setAdapter(adapter);
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

    @Override
    public void onTrackSelected(int index) {

    }

    @Override
    public void onRadioSelected(Track t) {
        if(existsInPlaylist(ply.getTracks(), t)){
            ErrorDialog.getInstance(getApplicationContext()).showErrorDialog("Aquesta canço ja està en aquesta playlist!");
        }else{
            bunch.add(t);
        }

    }

    @Override
    public void onRadioRemove(Track t) {
        bunch.remove(t);
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
        Intent intent = new Intent(getApplicationContext(), PlaylistActivity.class);
        intent.putExtra("Playlst", ply);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
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
}
