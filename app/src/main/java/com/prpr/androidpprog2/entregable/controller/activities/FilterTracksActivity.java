package com.prpr.androidpprog2.entregable.controller.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.utils.Constants;

import java.util.ArrayList;

public class FilterTracksActivity extends AppCompatActivity {


    private ArrayList<Track> myTracks;

    private EditText etFilterGenre;
    private Button btnFilterGenre;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_tracks);
        myTracks = new ArrayList<>();
        myTracks =  (ArrayList<Track>) getIntent().getSerializableExtra("Tracks");

        initViews();
    }

    void initViews(){

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.menu);
        navigation.setSelectedItemId(R.id.perfil);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
                        return true;
                    case R.id.buscar:
                        Intent intent2 = new Intent(getApplicationContext(), SearchActivity.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent2, Constants.NETWORK.LOGIN_OK);
                        return true;
                    case R.id.perfil:
                        return true;
                }
                return false;
            }
        });

        etFilterGenre = (EditText) findViewById(R.id.et_tracks_genre_filter);
        String genre = etFilterGenre.getText().toString();

        btnFilterGenre = (Button) findViewById(R.id.button_filter_tracks_by_genre);
        btnFilterGenre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchTracksGenre(genre);
                Intent intent = new Intent();
                intent.putExtra("TracksFiltered", myTracks);
                setResult(RESULT_OK, intent);
                finish();
            }
        });



    }

    void searchTracksGenre(String genre){
        for(int i = 0; i < myTracks.size(); i++){
            if(!myTracks.get(i).getGenres().toString().equalsIgnoreCase(genre)){
                myTracks.remove(i);
            }
        }
    }
}
