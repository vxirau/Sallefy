package com.prpr.androidpprog2.entregable.controller.activities;


import android.os.Bundle;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.adapters.PlaylistAdapter;
import com.prpr.androidpprog2.entregable.controller.adapters.TrackListAdapter;
import com.prpr.androidpprog2.entregable.controller.adapters.UserPlaylistAdapter;
import com.prpr.androidpprog2.entregable.controller.callbacks.ServiceCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.PlaylistCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.SearchCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.PlaylistManager;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.SearchManager;
import com.prpr.androidpprog2.entregable.controller.restapi.service.ReproductorService;
import com.prpr.androidpprog2.entregable.model.Follow;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.model.User;
import com.prpr.androidpprog2.entregable.utils.Constants;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class UserPlaylistsFragment extends Fragment implements PlaylistCallback {

    private ArrayList<Playlist> myPlaylists;
    private Playlist myPlaylist;

    private PlaylistManager playlistManager;

    private Button btnFilterPlaylists;
    private Button btnAddNewPlaylist;
    private Button btnResetFilters;

    Animation fabOpen, fabClose, rotateForward, rotateBackward;
    boolean isOpen = false;

    private Button btnFilterByAscending;
    private Button btnFilterByDescending;

    private TextView tvAddnewPlaylist;
    private EditText etSearchPlaylist;

    private FloatingActionButton btnSettingsPlaylists;

    private ReproductorService servei;

    private RecyclerView mRecyclerView;
    public UserPlaylistsFragment() {
        // Required empty public constructor
        this.myPlaylists = new ArrayList<>();
    }


    /*
    private TextView trackTitle;
    private TextView followingTxt;
    private TextView trackAuthor;
    private SeekBar mSeekBar;
    private Button play;
    private Button pause;
    private ImageView im;
    private LinearLayout playing;
    private ReproductorService serv;
    private boolean servidorVinculat=false;
    @Override
    public void onSeekBarUpdate(int progress, int duration, boolean isPlaying, String duracio) {
        if(isPlaying){
            mSeekBar.postDelayed(serv.getmProgressRunner(), 1000);
        }
        mSeekBar.setProgress(progress);
    }


    public void setService(ReproductorService serv){
        servei = serv;
    }
    */


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view =  inflater.inflate(R.layout.fragment_user_playlists, container, false);

        /*
        play = view.findViewById(R.id.playButton);
        play.setEnabled(true);
        play.bringToFront();
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serv.resumeMedia();
            }
        });
        pause = view.findViewById(R.id.playPause);
        pause.setEnabled(true);
        pause.bringToFront();
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serv.pauseMedia();
            }
        });

        trackAuthor = view.findViewById(R.id.dynamic_artist);
        trackTitle = view.findViewById(R.id.dynamic_title);
        trackTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        trackTitle.setSelected(true);
        trackTitle.setSingleLine(true);
        mSeekBar = (SeekBar) view.findViewById(R.id.dynamic_seekBar);

        playing = view.findViewById(R.id.reproductor);
        playing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ReproductorActivity.class);
                startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
                //overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
            }
        });

        servei.setSeekCallback(this);
        servei.setmSeekBar(mSeekBar);
        servei.setUIControls(mSeekBar, trackTitle, trackAuthor, play, pause, im);
        servei.setSeekCallback(UserPlaylistsFragment.this);

        */

        // Inflate the layout for this fragment

       /*btnSettingsPlaylists = (FloatingActionButton) view.findViewById(R.id.configPlaylistsButton);
       btnSettingsPlaylists.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(getContext(), SettingsActivity.class);
               intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
               startActivity(intent);
           }
       });*/

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

        btnResetFilters = view.findViewById(R.id.btn_reset_filters_playlists);
        btnResetFilters.setEnabled(false);
        btnResetFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
                resetFilters();
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

    private void resetFilters(){

        playlistManager.getAllMyPlaylists(this);

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

            btnResetFilters.setClickable(false);
            btnResetFilters.setEnabled(false);
            btnResetFilters.setVisibility(View.INVISIBLE);

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

            btnResetFilters.setClickable(true);
            btnResetFilters.setEnabled(true);
            btnResetFilters.setVisibility(View.VISIBLE);
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

    @Override
    public void onPlaylistRecived(Playlist playlist) {

    }


}
