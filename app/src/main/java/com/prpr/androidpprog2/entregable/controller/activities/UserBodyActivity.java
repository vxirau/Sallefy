package com.prpr.androidpprog2.entregable.controller.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Fragment;


import com.prpr.androidpprog2.entregable.R;

public class UserBodyActivity extends AppCompatActivity implements UserCommunicationInterface{

    Fragment[] myFragments;


    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_user_body);

        myFragments = new Fragment[3];

        myFragments[0] = new UserPlaylistFragment();
        myFragments[1] = new UserTracksFragment();
        myFragments[2] = new UserStatisticsFragment();

        Bundle extras = getIntent().getExtras();

        userMenu(extras.getInt("Button Pressed"));




    }

    public void userMenu(int whichButton){

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.userBodyRelativeLayout, myFragments[whichButton]);
        fragmentTransaction.commit();
    }


}
