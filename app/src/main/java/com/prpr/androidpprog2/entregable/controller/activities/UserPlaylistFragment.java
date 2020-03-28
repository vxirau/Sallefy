package com.prpr.androidpprog2.entregable.controller.activities;

import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



import androidx.appcompat.app.AppCompatActivity;

import com.prpr.androidpprog2.entregable.R;


public class UserPlaylistFragment extends Fragment {




    public UserPlaylistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_playlist, container, false);
    }


}
