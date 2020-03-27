package com.prpr.androidpprog2.entregable.controller.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.adapters.PlaylistAdapter;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.PlaylistManager;
import com.prpr.androidpprog2.entregable.model.UserToken;
import com.prpr.androidpprog2.entregable.utils.Constants;
import com.prpr.androidpprog2.entregable.utils.Session;

public class InfoArtistaActivity extends AppCompatActivity {

    private Button back;
    private RecyclerView topSongsRecycle;
    private RecyclerView playlistByArtistRecycle;
    private RecyclerView allSongsRecycle;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_user);
        initViews();

    }

    private void initViews(){

        back = findViewById(R.id.back2Main);
        back.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
            }
        });

        topSongsRecycle = (RecyclerView) findViewById(R.id.allplaylists);
        LinearLayoutManager man = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        //adapter
        topSongsRecycle.setLayoutManager(man);
        //topSongsRecycle.setAdapter();

        playlistByArtistRecycle = (RecyclerView) findViewById(R.id.topPlayedPlaylists);
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        //adapter
        playlistByArtistRecycle.setLayoutManager(manager);
        //playlistByArtistRecycle.setAdapter();

        allSongsRecycle = (RecyclerView) findViewById(R.id.allSongsRecycle);
        LinearLayoutManager manager2 = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        //adapter
        allSongsRecycle.setLayoutManager(manager2);
        //allSongsRecycle.setAdapter();



    }

}
