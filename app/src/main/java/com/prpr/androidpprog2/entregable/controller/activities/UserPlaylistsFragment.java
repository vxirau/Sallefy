package com.prpr.androidpprog2.entregable.controller.activities;


import android.os.Bundle;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.adapters.PlaylistAdapter;
import com.prpr.androidpprog2.entregable.controller.adapters.TrackListAdapter;
import com.prpr.androidpprog2.entregable.controller.adapters.UserPlaylistAdapter;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.PlaylistCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.SearchCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.PlaylistManager;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.SearchManager;
import com.prpr.androidpprog2.entregable.model.Follow;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.model.User;
import com.prpr.androidpprog2.entregable.utils.Constants;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class UserPlaylistsFragment extends Fragment implements PlaylistCallback{

    private ArrayList<Playlist> myPlaylists;
    private Playlist myPlaylist;

    private PlaylistManager playlistManager;

    private Button btnFilterPlaylists;
    private Button btnAddNewPlaylist;

    Animation fabOpen, fabClose, rotateForward, rotateBackward;
    boolean isOpen = false;

    private Button btnFilterByAscending;
    private Button btnFilterByDescending;

    private TextView tvAddnewPlaylist;
    private EditText etSearchPlaylist;

    private FloatingActionButton btnSettingsPlaylists;

    private RecyclerView mRecyclerView;
    public UserPlaylistsFragment() {
        // Required empty public constructor
        this.myPlaylists = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view =  inflater.inflate(R.layout.fragment_user_playlists, container, false);

        BottomNavigationView navigation = (BottomNavigationView) view.findViewById(R.id.menu);
        navigation.setSelectedItemId(R.id.perfil);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
                        return true;
                    case R.id.buscar:
                        Intent intent2 = new Intent(getContext(), SearchActivity.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent2, Constants.NETWORK.LOGIN_OK);
                        return true;
                    case R.id.perfil:
                        return true;
                }
                return false;
            }
        });


       btnSettingsPlaylists = (FloatingActionButton) view.findViewById(R.id.configPlaylistsButton);
       btnSettingsPlaylists.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(getContext(), SettingsActivity.class);
               startActivity(intent);
           }
       });

        btnFilterPlaylists = (Button) view.findViewById(R.id.filter_user_playlists);
        btnFilterPlaylists.setEnabled(true);
        btnFilterPlaylists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();

            }
        });

        btnFilterByAscending = view.findViewById(R.id.btn_filter_playlists_by_name_ascendent);
        btnFilterByAscending.setEnabled(false);
        btnFilterByAscending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
                sortNameAscendent();
            }
        });

        btnFilterByDescending = view.findViewById(R.id.btn_filter_playlists_by_name_descendent);
        btnFilterByDescending.setEnabled(false);
        btnFilterByDescending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
                sortNameDescendent();
            }
        });

       tvAddnewPlaylist = (TextView) view.findViewById(R.id.tv_add_new_playlist);
       tvAddnewPlaylist.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(getContext(), NewPlaylistActivity.class);
               startActivity(intent);
           }
       });

       btnAddNewPlaylist = (Button) view.findViewById(R.id.add_new_playlist);
       btnAddNewPlaylist.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(getContext(), NewPlaylistActivity.class);
               startActivity(intent);
           }
       });

        etSearchPlaylist = (EditText) view.findViewById(R.id.search_user_playlists);
        etSearchPlaylist.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });

        mRecyclerView = (RecyclerView) view.findViewById(R.id.userPlaylistsRecyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        UserPlaylistAdapter adapter = new UserPlaylistAdapter(getContext(), null);
        adapter.setPlaylistCallback(this);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(adapter);

        playlistManager = new PlaylistManager(getContext());
        playlistManager.getAllMyPlaylists(this);



       return view;
    }

    private void filter(String text){
        ArrayList<Playlist> filteredPlaylists = new ArrayList<>();

        for(Playlist p : myPlaylists){
            if(p.getName().toLowerCase().contains(text.toLowerCase())){
                filteredPlaylists.add(p);
            }
        }
        mRecyclerView.setAdapter(new UserPlaylistAdapter(getContext(), filteredPlaylists ));
    }

    private void sortNameAscendent(){

        Collections.sort(myPlaylists, Playlist.PlaylistNameAscendentComparator);

        mRecyclerView.setAdapter(new UserPlaylistAdapter(getContext(), this.myPlaylists));

    }

    private void sortNameDescendent(){

        Collections.sort(myPlaylists, Playlist.PlaylistNameDescendentComparator);

        mRecyclerView.setAdapter(new UserPlaylistAdapter(getContext(), this.myPlaylists));

    }

    private void animateFab(){
        if(isOpen){

            //btnFilterByAscending.startAnimation(fabClose);
            btnFilterByAscending.setClickable(false);
            btnFilterByAscending.setEnabled(false);
            btnFilterByAscending.setVisibility(View.INVISIBLE);
            //btnFilterByRecent.startAnimation(fabClose);
            btnFilterByDescending.setClickable(false);
            btnFilterByDescending.setEnabled(false);
            btnFilterByDescending.setVisibility(View.INVISIBLE);
            isOpen=false;
        }else{

            //btnFilterByPlays.startAnimation(fabOpen);
            btnFilterByAscending.setClickable(true);
            btnFilterByAscending.setEnabled(true);
            btnFilterByAscending.setVisibility(View.VISIBLE);
            //btnFilterByRecent.startAnimation(fabOpen);
            btnFilterByDescending.setClickable(true);
            btnFilterByDescending.setEnabled(true);
            btnFilterByDescending.setVisibility(View.VISIBLE);
            isOpen=true;
        }
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
        playlistManager.getFollowingPlaylists(this);
    }

    @Override
    public void onNoPlaylists(Throwable throwable) {

    }

    @Override
    public void onPlaylistSelected(Playlist playlist) {
        Intent intent = new Intent(getContext(), PlaylistActivity.class);
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
        this.myPlaylists.addAll(body);
        UserPlaylistAdapter userPlaylistAdapter = new UserPlaylistAdapter(getContext(), this.myPlaylists);
        userPlaylistAdapter.setPlaylistCallback(this);
        mRecyclerView.setAdapter(userPlaylistAdapter);
    }

    @Override
    public void onFollowingChecked(Follow body) {

    }

    @Override
    public void onFollowSuccessfull(Follow body) {

    }

}
