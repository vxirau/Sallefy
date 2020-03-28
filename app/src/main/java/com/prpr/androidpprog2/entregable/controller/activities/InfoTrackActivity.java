package com.prpr.androidpprog2.entregable.controller.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;
import com.squareup.picasso.Picasso;


public class InfoTrackActivity extends AppCompatActivity {

    private ImageView songCover;
    private TextView songName;
    private TextView nomArtista;

    private ImageButton favorites;
    private TextView text_favorites;

    private ImageButton edit;
    private TextView text_edit;

    private ImageButton artist;
    private TextView text_artist;

    private ImageButton playlist;
    private TextView text_playlist;

    private Track trck;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_track);
        trck = (Track) getIntent().getSerializableExtra("Trck");
        initViews();
    }

    private void initViews(){
        songCover = (ImageView) findViewById(R.id.SongCover);
        songName = (TextView) findViewById(R.id.SongName);
        nomArtista = (TextView) findViewById(R.id.ArtistName);

        songName.setText(trck.getName());
        nomArtista.setText(trck.getUserLogin());

        if(trck.getThumbnail()!=null){
            Picasso.get().load(trck.getThumbnail()).into(songCover);
        }else{
            Picasso.get().load("https://community.spotify.com/t5/image/serverpage/image-id/25294i2836BD1C1A31BDF2/image-size/original?v=mpbl-1&px=-1").into(songCover);
        }

        //Fer crida a l'Api, guardar a playlist de favoritos de usuari
        favorites = (ImageButton) findViewById(R.id.favoritos);
        favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        edit= (ImageButton) findViewById(R.id.edit);


        artist = (ImageButton) findViewById(R.id.user);
        artist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), InfoArtistaActivity.class);
                startActivity(intent);
            }
        });


        playlist = (ImageButton) findViewById(R.id.playlist);
        playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Add2PlaylistActivity.class);
                startActivity(intent);
            }
        });

    }
}


