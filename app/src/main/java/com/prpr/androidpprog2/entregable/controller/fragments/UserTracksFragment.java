package com.prpr.androidpprog2.entregable.controller.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.activities.UploadActivity;
import com.prpr.androidpprog2.entregable.controller.adapters.TrackListAdapter;
import com.prpr.androidpprog2.entregable.controller.callbacks.TrackListCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.TrackCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.TrackManager;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.utils.PreferenceUtils;
import com.prpr.androidpprog2.entregable.utils.Session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class UserTracksFragment extends Fragment implements TrackListCallback, TrackCallback {

    private ArrayList<Track> myTracks;
    private ArrayList<Track> genreFilteredTracks;

    private Button btnFilterTracks;

    private Button btnFilterByAscending;
    private Button btnFilterByDescending;
    private Button btnResetFilters;

    Animation fabOpen, fabClose, rotateForward, rotateBackward;
    boolean isOpen = false;

    private FloatingActionButton btnSettingsTracks;
    private Playlist myPlaylist;
    private RecyclerView mRecyclerView;

    static final int TRACK_FILTER_REQUEST= 1;

    private Button btnAddNewTrack;

    private FloatingActionButton btnSettings;


    private TextView tvAddnewTrack;

    private EditText etSearchTracks;

    private TrackManager trackManager;
    public static final String Broadcast_PLAY_NEW_AUDIO = "com.prpr.androidpprog2.entregable.PlayNewAudio";



    public UserTracksFragment() {
        // Required empty public constructor
    }
    @Override
    public void onResume() {
        super.onResume();
        trackManager.getOwnTracks(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_tracks, container, false);

        /*btnSettingsTracks = (FloatingActionButton) view.findViewById(R.id.configTracksButton);
        btnSettingsTracks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });*/

        btnSettings = (FloatingActionButton)getActivity().findViewById(R.id.configButton);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.userTracksRecyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        TrackListAdapter adapter = new TrackListAdapter(this, getContext(), myTracks, myPlaylist);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && btnSettings.getVisibility() == View.VISIBLE) {
                    btnSettings.hide();
                } else if (dy < 0 && btnSettings.getVisibility() != View.VISIBLE) {
                    btnSettings.show();
                }
            }
        });

        trackManager = new TrackManager(getContext());
        trackManager.getOwnTracks(this);
        tvAddnewTrack = (TextView) view.findViewById(R.id.tv_add_new_song);
        tvAddnewTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), UploadActivity.class);
                startActivity(intent);
            }
        });

        btnAddNewTrack = (Button) view.findViewById(R.id.add_new_song);
        btnAddNewTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), UploadActivity.class);
                startActivity(intent);
            }
        });
        btnFilterTracks = (Button) view.findViewById(R.id.filter_user_songs);
        btnFilterTracks.setEnabled(true);
        btnFilterTracks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();

            }
        });



        btnFilterByAscending = view.findViewById(R.id.btn_filter_songs_by_name_ascendent);
        btnFilterByAscending.setEnabled(false);
        btnFilterByAscending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
                sortNameAscendent();
            }
        });

        btnFilterByDescending = view.findViewById(R.id.btn_filter_songs_by_name_descendent);
        btnFilterByDescending.setEnabled(false);
        btnFilterByDescending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
                sortNameDescendent();
            }
        });


        btnResetFilters = view.findViewById(R.id.btn_reset_filters_tracks);
        btnResetFilters.setEnabled(false);
        btnResetFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
                resetFilters();
            }
        });

        etSearchTracks = (EditText) view.findViewById(R.id.search_user_tracks);
        etSearchTracks.addTextChangedListener(new TextWatcher() {
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
        return view;
    }


    private void sortNameAscendent(){

        Collections.sort(myTracks, Track.TrackNameAscendentComparator);

        mRecyclerView.setAdapter(new TrackListAdapter(this, getContext(), myTracks, this.myPlaylist));

    }

    private void sortNameDescendent(){

        Collections.sort(myTracks, Track.TrackNameDescendentComparator);

        mRecyclerView.setAdapter(new TrackListAdapter(this, getContext(), myTracks, this.myPlaylist));

    }

    private void filter(String text){
        ArrayList<Track> filteredTracks = new ArrayList<>();

        for(Track t : myTracks){
            if(t.getName().toLowerCase().contains(text.toLowerCase())){
                filteredTracks.add(t);
            }
        }
        mRecyclerView.setAdapter(new TrackListAdapter(this, getContext(), filteredTracks, this.myPlaylist));
    }

    private void resetFilters(){

        trackManager.getOwnTracks(this);

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

    private void playAudio(int audioIndex) {

        PreferenceUtils.saveAllTracks(getActivity(), myTracks);
        PreferenceUtils.saveTrack(getActivity(), myTracks.get(audioIndex));
        PreferenceUtils.savePlayID(getActivity(), -5);
        Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
        getActivity().sendBroadcast(broadcastIntent);

    }

    @Override
    public void onTrackSelected(int index) {
        playAudio(index);
    }

    @Override
    public void onTrackAddSelected(int position, ArrayList<Track> tracks, Playlist playlist) {
        Playlist pl;
        if(playlist!=null){
            pl =  playlist;
        }else{
            pl = new Playlist("UserPlaylist", Session.getUser());
        }
        InfoTrackFragment bottomSheetDialog = new InfoTrackFragment(tracks.get(position), pl, Session.getUser());
        bottomSheetDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme);
        bottomSheetDialog.show(getFragmentManager(), "track_info");
    }

    @Override
    public void onTrackSelectedLiked(int position) {
        trackManager.likeTrack(myTracks.get(position).getId(), UserTracksFragment.this);
    }

    @Override
    public void onTracksReceived(List<Track> tracks) {

    }

    @Override
    public void onNoTracks(Throwable throwable) {

    }

    @Override
    public void onPersonalTracksReceived(List<Track> tracks) {
        this.myTracks = (ArrayList) tracks;
        TrackListAdapter trackListAdapter = new TrackListAdapter(this, getContext(), this.myTracks, this.myPlaylist);
        mRecyclerView.setAdapter(trackListAdapter);
    }

    @Override
    public void onPersonalLikedTracksReceived(List<Track> tracks) {

    }

    @Override
    public void onUserTracksReceived(List<Track> tracks) {

    }

    @Override
    public void onCreateTrack(Track t) {

    }

    @Override
    public void onTopTracksRecieved(List<Track> tracks) {

    }

    @Override
    public void onNoTopTracks(Throwable throwable) {

    }

    private int trackById(int id){
        int valor = 0;
        for(int i=0; i<myTracks.size() ;i++){
            if(myTracks.get(i).getId()==id){
                valor = i;
            }
        }
        return valor;
    }

    @Override
    public void onTrackLiked(int id) {
        if(myTracks.get(trackById(id)).isLiked()){
            myTracks.get(trackById(id)).setLiked(false);
        }else{
            myTracks.get(trackById(id)).setLiked(true);
        }
    }


    @Override
    public void onTrackNotFound(Throwable throwable) {

    }

    @Override
    public void onTrackUpdated(Track body) {

    }

    @Override
    public void onTrackUpdateFailure(Throwable throwable) {

    }

    @Override
    public void onTrackDeleted(int id) {

    }

    @Override
    public void onTrackReceived(Track track) {

    }

    @Override
    public void onFailure(Throwable throwable) {

    }


}
