package com.prpr.androidpprog2.entregable.controller.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.adapters.GenereAdapter;
import com.prpr.androidpprog2.entregable.controller.adapters.PlaylistAdapter;
import com.prpr.androidpprog2.entregable.controller.adapters.TrackListAdapter;
import com.prpr.androidpprog2.entregable.controller.callbacks.PlaylistListCallback;
import com.prpr.androidpprog2.entregable.controller.callbacks.TrackListCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.GenreCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.GenreManager;
import com.prpr.androidpprog2.entregable.model.Genre;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.model.User;
import com.prpr.androidpprog2.entregable.utils.Constants;

import java.sql.SQLOutput;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements TrackListCallback, PlaylistListCallback, GenreCallback {

    //Arraylist de cançons i playlists
    private ArrayList<Track> mTracks;
    private ArrayList<Playlist> mPlaylist;

    //Llista de songs i playists
    private RecyclerView mRecyclerViewTracks;
    private RecyclerView mRecyclerViewPlaylist;

    //Generes
    private RecyclerView getmRecyclerViewGeneres;
    private ArrayList<Genre> mGeneres;
    private Playlist mPlaylistDeGenere;

    //Possibles layouts en la cerca
    private LinearLayout mGeneresLayout;
    private LinearLayout mPlaylistLayout;
    private LinearLayout mTracksLayout;
    private LinearLayout mBothLayout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initViews();
    }

    void initViews(){
        //No mostrem res
        mGeneresLayout = (LinearLayout) findViewById(R.id.search_genere_layout);
        mGeneresLayout.setVisibility(View.GONE);

        mPlaylistLayout = (LinearLayout) findViewById(R.id.search_recyclerView_playlist);
        mPlaylistLayout.setVisibility(View.GONE);

        mTracksLayout = (LinearLayout) findViewById(R.id.search_recyclerView_song);
        mTracksLayout.setVisibility(View.GONE);

        mBothLayout = (LinearLayout) findViewById(R.id.search_recyclerView_both);
        mBothLayout.setVisibility(View.GONE);

        //GENERE LIST
        mGeneres = new ArrayList<>();
        GenreManager.getInstance(this).getAllGenres(this);

        //Recicle views

        mRecyclerViewTracks = (RecyclerView) findViewById(R.id.search_dynamic_recyclerView_songs);
        LinearLayoutManager managerTrack = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        TrackListAdapter adapterTrack = new TrackListAdapter(this, this, null, null);
        mRecyclerViewTracks.setLayoutManager(managerTrack);
        mRecyclerViewTracks.setAdapter(adapterTrack);

        mRecyclerViewPlaylist = (RecyclerView) findViewById(R.id.search_dynamic_recyclerView_playlist);
        LinearLayoutManager managerPlaylist = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        PlaylistAdapter adapterPlaylist = new PlaylistAdapter(this, null);
        mRecyclerViewPlaylist.setLayoutManager(managerPlaylist);
        mRecyclerViewPlaylist.setAdapter(adapterPlaylist);

        getmRecyclerViewGeneres = (RecyclerView) findViewById(R.id.search_dynamic_recyclerView_genere);
        LinearLayoutManager managerGenere = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        GenereAdapter adapterGenere = new GenereAdapter(this, null);
        mRecyclerViewPlaylist.setLayoutManager(managerGenere);
        mRecyclerViewPlaylist.setAdapter(adapterGenere);

        //XI
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.menu);
        navigation.setSelectedItemId(R.id.buscar);
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
                        return true;
                    case R.id.perfil:
                        Intent intent2 = new Intent(getApplicationContext(), UserPlaylistActivity.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent2, Constants.NETWORK.LOGIN_OK);
                        return true;
                }
                return false;
            }
        });
    }

    private Genre getGenereFromName(String name){
        for (Genre genre: mGeneres)
            if (genre.getName().equals(name))
                return genre;
        return null;
    }

    @Override
    public void onTrackSelected(int index) {

    }

    @Override
    public void onTrackAddSelected(int position, ArrayList<Track> tracks, Playlist playlist) {

    }

    @Override
    public void onGenresReceive(ArrayList<Genre> genres) {
        mGeneres = genres;

        GenereAdapter adapter = new GenereAdapter(this, mGeneres);
        getmRecyclerViewGeneres.setAdapter(adapter);

        mGeneresLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTracksByGenre(ArrayList<Track> tracks) {
        mPlaylistDeGenere.setTracks(tracks);

        Intent intent = new Intent(getApplicationContext(), PlaylistActivity.class);
        intent.putExtra("Playlst", mPlaylistDeGenere);
        startActivity(intent);
    }

    @Override
    public void onGenreSelected(Genre genere) {
        mPlaylistDeGenere = new Playlist(genere.getName(), new User("Sallefy"));
        GenreManager.getInstance(this).getTracksByGenre(genere.getId(), this);
    }

    @Override
    public void onFailure(Throwable throwable) {

    }
}

