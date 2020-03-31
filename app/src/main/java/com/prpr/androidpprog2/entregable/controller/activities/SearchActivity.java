package com.prpr.androidpprog2.entregable.controller.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.adapters.GenereAdapter;
import com.prpr.androidpprog2.entregable.controller.adapters.PlaylistAdapter;
import com.prpr.androidpprog2.entregable.controller.adapters.TrackListAdapter;
import com.prpr.androidpprog2.entregable.controller.adapters.UserAdapter;
import com.prpr.androidpprog2.entregable.controller.callbacks.TrackListCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.GenreCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.PlaylistCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.SearchCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.UserCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.GenreManager;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.SearchManager;
import com.prpr.androidpprog2.entregable.model.Genre;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.model.User;
import com.prpr.androidpprog2.entregable.model.UserToken;
import com.prpr.androidpprog2.entregable.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements  TrackListCallback, PlaylistCallback, UserCallback, GenreCallback, SearchCallback {

    //Llista de songs i playists
    private RecyclerView mRecyclerViewTracks;
    private RecyclerView mRecyclerViewPlaylist;
    private RecyclerView mRecyclerViewUser;

    //Generes
    private RecyclerView getmRecyclerViewGeneres;
    private ArrayList<Genre> mGeneres;
    private Playlist mPlaylistDeGenere;

    //Cerca
    private EditText mSearchText;

    //Possibles layouts en la cerca
    private LinearLayout mPlaylistLayout;
    private LinearLayout mTracksLayout;
    private LinearLayout mUsersLayout;
    private ScrollView mGeneresLayout;
    private ScrollView mBothLayout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        initViews();
    }

    void initViews(){
        //No mostrem res
        mGeneresLayout = (ScrollView) findViewById(R.id.search_genere_layout);
        mGeneresLayout.setVisibility(View.GONE);

        mPlaylistLayout = (LinearLayout) findViewById(R.id.search_recyclerView_playlist);
        //mPlaylistLayout.setVisibility(View.GONE);

        mTracksLayout = (LinearLayout) findViewById(R.id.search_recyclerView_song);
        //mTracksLayout.setVisibility(View.GONE);

        mUsersLayout = (LinearLayout) findViewById(R.id.search_recyclerView_user);
        //mUsersLayout.setVisibility(View.GONE);

        mBothLayout = (ScrollView) findViewById(R.id.search_scroll_all);
        mBothLayout.setVisibility(View.INVISIBLE);

        //Obtenim GENERES LIST
        mGeneres = new ArrayList<>();
        //GenreManager.getInstance(this).getAllGenres(this);

        //Recicle views
        mRecyclerViewPlaylist = (RecyclerView) findViewById(R.id.search_dynamic_recyclerView_playlist);
        LinearLayoutManager managerPlaylist = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        PlaylistAdapter adapterPlaylist = new PlaylistAdapter(this, null);
        adapterPlaylist.setPlaylistCallback(this);
        mRecyclerViewPlaylist.setLayoutManager(managerPlaylist);
        mRecyclerViewPlaylist.setAdapter(adapterPlaylist);

        mRecyclerViewTracks = (RecyclerView) findViewById(R.id.search_dynamic_recyclerView_songs);
        LinearLayoutManager managerTrack = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        TrackListAdapter adapterTrack = new TrackListAdapter(this, this, null, null);
        mRecyclerViewTracks.setLayoutManager(managerTrack);
        mRecyclerViewTracks.setAdapter(adapterTrack);

        mRecyclerViewUser = (RecyclerView) findViewById(R.id.search_dynamic_recyclerView_user);
        LinearLayoutManager managerUser = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        UserAdapter adapterUser = new UserAdapter(this, null);
        adapterUser.setUserCallback(this);
        mRecyclerViewUser.setLayoutManager(managerUser);
        mRecyclerViewUser.setAdapter(adapterUser);

        getmRecyclerViewGeneres = (RecyclerView) findViewById(R.id.search_dynamic_recyclerView_genere);
        LinearLayoutManager managerGenere = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        GenereAdapter adapterGenere = new GenereAdapter(this, this, null);
        mRecyclerViewPlaylist.setLayoutManager(managerGenere);
        mRecyclerViewPlaylist.setAdapter(adapterGenere);

        //Search bar
        mSearchText = (EditText) findViewById(R.id.search_bar);
        mSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                call();
            }
        });

        //XI - BOTTOM NAVBAR
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
                        Intent intent2 = new Intent(getApplicationContext(), UserMainActivity.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent2, Constants.NETWORK.LOGIN_OK);
                        return true;
                }
                return false;
            }
        });
    }
    private void call (){
        if (!mSearchText.getText().toString().equals("")) {
            SearchManager.getInstance(this).getSearch(this, mSearchText.getText().toString());
        }
    }

    @Override
    public void onFailure(Throwable throwable) {

    }

    @Override
    public void onGenresReceive(ArrayList<Genre> genres) {
        mGeneres = genres;

        GenereAdapter adapter = new GenereAdapter(this, this, mGeneres);
        getmRecyclerViewGeneres.setAdapter(adapter);

        //mGeneresLayout.setVisibility(View.VISIBLE);
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
    public void onUsernameUpdated(User user) {

    }

    @Override
    public void onEmailUpdated(User user) {

    }

    @Override
    public void onTopUsersRecieved(List<User> body) {

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
    public void onAllUsersFail(Throwable throwable) {

    }


    @Override
    public void onTrackSearchRecived(ArrayList<Track> tracks) {
        mRecyclerViewTracks.setAdapter(new TrackListAdapter(this, this, tracks, null));

        /*
        //SOUTS DE PLAYLISTS
        System.out.println("TRACKS ON VIEW");
        for (Track playlist: tracks) {
            playlist.print();
        }
        */

        mTracksLayout.setVisibility(View.VISIBLE);
        mBothLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNoTrackSearchRecived() {
        mTracksLayout.setVisibility(View.GONE);
    }

    @Override
    public void onPlaylistSearchRecived(ArrayList<Playlist> playlists) {
        mRecyclerViewPlaylist.setAdapter(new PlaylistAdapter(this, playlists));

        /*
        //SOUTS DE PLAYLISTS
        System.out.println("PLAYLISTS ON VIEW");
        for (Playlist playlist: playlists) {
            playlist.print();
        }
         */

        mPlaylistLayout.setVisibility(View.VISIBLE);
        mBothLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNoPlaylistSearchRecived() {
        mPlaylistLayout.setVisibility(View.GONE);
    }

    @Override
    public void onUserSearchRecived(ArrayList<User> users) {
        mRecyclerViewUser.setAdapter(new UserAdapter(this, users));

        /*
        //SOUTS DE USERS
        System.out.println("USERS ON VIEW");
        for (User playlist: users) {
            playlist.print();
        }
         */

        mUsersLayout.setVisibility(View.VISIBLE);
        mBothLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNoUserSearchRecived() {
        mUsersLayout.setVisibility(View.GONE);
    }

    @Override
    public void onTrackSelected(int index) {

    }

    @Override
    public void onTrackAddSelected(int position, ArrayList<Track> tracks, Playlist playlist) {

    }
}

