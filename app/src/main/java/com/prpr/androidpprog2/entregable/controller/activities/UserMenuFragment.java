package com.prpr.androidpprog2.entregable.controller.activities;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prpr.androidpprog2.entregable.R;



public class UserMenuFragment extends Fragment{

    private final int[] USERMENUBUTTONS = {R.id.user_playlists_title, R.id.user_my_songs_title, R.id.user_statistics_title};

    public UserMenuFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View userMenu = inflater.inflate(R.layout.fragment_user_menu, container, false);

        TextView tvUserMenu;

        for(int i = 0; i < USERMENUBUTTONS.length; i++){

            tvUserMenu = (TextView) userMenu.findViewById(USERMENUBUTTONS[i]);

            final int whichButton = i;

            tvUserMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Activity currentActivity = getActivity();

                    ((UserCommunicationInterface)currentActivity).userMenu(whichButton);
                }
            });
        }
        return userMenu;
    }
}
