package com.prpr.androidpprog2.entregable.controller.activities;


import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.adapters.UserPlaylistAdapter;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.PlaylistCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.PlaylistManager;
import com.prpr.androidpprog2.entregable.model.Follow;
import com.prpr.androidpprog2.entregable.model.Playlist;

import java.util.ArrayList;
import java.util.List;


public class UserPlaylistsFragment extends Fragment implements PlaylistCallback {

    private ArrayList<Playlist> myPlaylists;
    private ArrayList<Playlist> followingPlaylists;
    private Playlist myPlaylist;

    private PlaylistManager playlistManager;

    private Button btnFilterPlaylists;
    private Button btnAddNewPlaylist;

    private TextView tvAddnewPlaylist;

    private FloatingActionButton btnSettingsPlaylists;

    private RecyclerView mRecyclerView;
    public UserPlaylistsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view =  inflater.inflate(R.layout.fragment_user_playlists, container, false);


       followingPlaylists = new ArrayList<>();


       btnSettingsPlaylists = (FloatingActionButton) view.findViewById(R.id.configPlaylistsButton);
       btnSettingsPlaylists.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(getContext(), SettingsActivity.class);
               startActivity(intent);
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

        mRecyclerView = (RecyclerView) view.findViewById(R.id.userPlaylistsRecyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        UserPlaylistAdapter adapter = new UserPlaylistAdapter(getContext(), null);
        adapter.setPlaylistCallback(this);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(adapter);

        playlistManager = new PlaylistManager(getContext());
        playlistManager.getAllMyPlaylists(this);
        playlistManager.getFollowingPlaylists(this);


       return view;
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
        this.followingPlaylists = (ArrayList) body;
        this.followingPlaylists.addAll(myPlaylists);
        UserPlaylistAdapter playlistAdapter = new UserPlaylistAdapter(getContext(), this.followingPlaylists);
        playlistAdapter.setPlaylistCallback(this);
        mRecyclerView.setAdapter(playlistAdapter);
    }

    @Override
    public void onFollowingChecked(Follow body) {

    }

    @Override
    public void onFollowSuccessfull(Follow body) {

    }
}
