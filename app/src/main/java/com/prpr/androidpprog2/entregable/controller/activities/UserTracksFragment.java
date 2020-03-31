package com.prpr.androidpprog2.entregable.controller.activities;

import android.content.Intent;
import android.os.Bundle;

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
import com.prpr.androidpprog2.entregable.controller.adapters.TrackListAdapter;
import com.prpr.androidpprog2.entregable.controller.adapters.UserPlaylistAdapter;
import com.prpr.androidpprog2.entregable.controller.callbacks.TrackListCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.TrackCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.PlaylistManager;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.TrackManager;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class UserTracksFragment extends Fragment implements TrackListCallback, TrackCallback {

    private ArrayList<Track> myTracks;
    private Button btnFilterTracks;
    private FloatingActionButton btnSettingsTracks;
    private Playlist myPlaylist;
    private RecyclerView mRecyclerView;

    static final int TRACK_FILTER_REQUEST= 1;

    private Button btnAddNewTrack;

    private TextView tvAddnewTrack;

    private TrackManager trackManager;

    public UserTracksFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_tracks, container, false);

        btnSettingsTracks = (FloatingActionButton) view.findViewById(R.id.configTracksButton);
        btnSettingsTracks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });


        mRecyclerView = (RecyclerView) view.findViewById(R.id.userTracksRecyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        TrackListAdapter adapter = new TrackListAdapter(this, getContext(), myTracks, myPlaylist);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(adapter);

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
        btnFilterTracks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), FilterTracksActivity.class);
                intent.putExtra("Tracks", myTracks);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, TRACK_FILTER_REQUEST);
                //myTracks.clear();
                //myTracks = (ArrayList<Track>) Objects.requireNonNull(getActivity().getIntent().getExtras()).getSerializable("Tracks filtered");
            }
        });

        return view;
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
        this.myTracks = (ArrayList) tracks;
        TrackListAdapter trackListAdapter = new TrackListAdapter(this, getContext(), this.myTracks, this.myPlaylist);
        mRecyclerView.setAdapter(trackListAdapter);
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

    @Override
    public void onFailure(Throwable throwable) {

    }
}
