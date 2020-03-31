package com.prpr.androidpprog2.entregable.controller.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.PlaylistCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.PlaylistManager;
import com.prpr.androidpprog2.entregable.model.Follow;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.utils.Constants;

import java.util.List;

public class NewPlaylistActivity extends Activity implements PlaylistCallback {

    private Button tornarEnrere;
    private Button newPlaylist;
    private EditText nomPlaylist;
    private PlaylistCallback pCallback;
    private PlaylistManager pManager;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_playlist);
        initViews();
        enableInitialButtons();
    }

    private void initViews() {
        pManager = new PlaylistManager(this);
        nomPlaylist = findViewById(R.id.nomNovaPlaylist);
        tornarEnrere = findViewById(R.id.amagarNovaPlaylist);
        tornarEnrere.setEnabled(false);
        tornarEnrere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.nothing,R.anim.nothing);
            }
        });
        newPlaylist = findViewById(R.id.AfegirAPlaylist);
        newPlaylist.setEnabled(true);
        newPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCreatePlaylist(new Playlist(nomPlaylist.getText().toString()));
            }
        });
    }


    private void doCreatePlaylist(Playlist playlist){
        pManager.createPlaylist(playlist, this);
    }

    private void enableInitialButtons() {
        tornarEnrere.setEnabled(true);
    }


    @Override
    public void onPlaylistCreated(Playlist playlist) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
    }

    @Override
    public void onPlaylistFailure(Throwable throwable) {
        Toast.makeText(this, "Error", Toast.LENGTH_LONG);
    }

    @Override
    public void onPlaylistRecieved(List<Playlist> playlists) {

    }

    @Override
    public void onNoPlaylists(Throwable throwable) {

    }

    @Override
    public void onPlaylistSelected(Playlist p) {

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

}
