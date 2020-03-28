package com.prpr.androidpprog2.entregable.controller.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.dialogs.ErrorDialog;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.utils.Session;
import com.squareup.picasso.Picasso;


public class InfoTrackActivity extends AppCompatActivity {

    private ImageView songCover;
    private TextView songName;
    private TextView nomArtista;

    private ImageButton favorites;
    private TextView text_favorites;
    private LinearLayout layoutFav;

    private ImageButton edit;
    private TextView text_edit;
    private LinearLayout layoutedit;

    private ImageButton artist;
    private TextView text_artist;
    private LinearLayout layoutArtist;

    private ImageButton playlist;
    private TextView text_playlist;
    private LinearLayout layoutPlaylist;

    private Button cancel;

    private Track trck;

    private ErrorDialog er;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_track);
        trck = (Track) getIntent().getSerializableExtra("Trck");
        initViews();
    }

    private void initViews(){
        er = new ErrorDialog(this);
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
        text_favorites = findViewById(R.id.text_favoritos);
        layoutFav = findViewById(R.id.layoutFavoritos);
        layoutFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        edit= (ImageButton) findViewById(R.id.edit);
        text_edit = findViewById(R.id.text_edit);
        layoutedit = findViewById(R.id.layoutedit);
        layoutedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Session.getInstance(getApplicationContext()).getUser().getLogin().equals(trck.getUserLogin())){

                }else{
                    er.showErrorDialog("This track is not yours to edit");
                }
            }
        });



        artist = (ImageButton) findViewById(R.id.user);
        text_artist = findViewById(R.id.text_user);
        layoutArtist = findViewById(R.id.layoutUser);
        layoutArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), InfoArtistaActivity.class);
                //TODO: Falta putExtra
                startActivity(intent);
            }
        });


        playlist = (ImageButton) findViewById(R.id.playlist);
        text_playlist = findViewById(R.id.text_playlist);
        layoutPlaylist = findViewById(R.id.layoutPlaylist);
        layoutPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Falta putExtra
                Intent intent = new Intent(getApplicationContext(), Add2PlaylistActivity.class);
                startActivity(intent);
            }
        });

        cancel= findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.nothing,R.anim.nothing);
            }
        });

    }
}


