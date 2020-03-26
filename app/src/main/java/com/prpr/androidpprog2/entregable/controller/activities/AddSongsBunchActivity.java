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
import com.prpr.androidpprog2.entregable.controller.restapi.callback.TrackCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.PlaylistManager;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.TrackManager;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class AddSongsBunchActivity extends AppCompatActivity implements TrackCallback, BunchTrackListCallback {

    private Button upload;
    private Button accept;
    private Button cancel;
    private ArrayList<Track> myTracks;
    private RecyclerView llistaCancons;
    private TrackManager tManager;
    private Playlist ply;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addsongs_bunch);
        initComponents();
        getData();
        tManager = new TrackManager(this);
        tManager.getOwnTracks(this);
        ply = (Playlist) getIntent().getSerializableExtra("Playlst");
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



}
