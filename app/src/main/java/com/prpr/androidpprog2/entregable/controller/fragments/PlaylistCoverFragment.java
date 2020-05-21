package com.prpr.androidpprog2.entregable.controller.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.model.Playlist;

public class PlaylistCoverFragment extends Fragment {

    public Playlist playlist;

    public static PlaylistCoverFragment getInstance(Playlist p){
        PlaylistCoverFragment fragment = new PlaylistCoverFragment();

        if(p!=null){
            Bundle bundle = new Bundle();
            //bundle.putParcelable("cover", p);
            fragment.setArguments(bundle);
        }

        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info_track, container, false);
        //LIDIA DIME QUE LO RESIBES
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            playlist = getArguments().getParcelable("cover");
        }
    }
}
