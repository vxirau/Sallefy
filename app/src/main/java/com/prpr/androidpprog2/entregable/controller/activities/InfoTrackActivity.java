package com.prpr.androidpprog2.entregable.controller.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.dialogs.ErrorDialog;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.TrackCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.TrackManager;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.utils.Constants;
import com.prpr.androidpprog2.entregable.utils.Session;
import com.squareup.picasso.Picasso;

import java.util.List;


public class InfoTrackActivity extends AppCompatActivity implements TrackCallback {

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

    private TrackManager tManager;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_track);
        trck = (Track) getIntent().getSerializableExtra("Trck");
        initViews();
        tManager = new TrackManager(this);
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

        favorites = (ImageButton) findViewById(R.id.favoritos);
        text_favorites = findViewById(R.id.text_favoritos);
        layoutFav = findViewById(R.id.layoutFavoritos);
        layoutFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tManager.likeTrack(trck.getId(), InfoTrackActivity.this);
            }
        });


        edit= (ImageButton) findViewById(R.id.edit);
        text_edit = findViewById(R.id.text_edit);
        layoutedit = findViewById(R.id.layoutedit);
        layoutedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Session.getInstance(getApplicationContext()).getUser().getLogin().equals(trck.getUserLogin())){
                    Intent intent = new Intent(getApplicationContext(), EditSongActivity.class);
                    intent.putExtra("Trck", trck);
                    startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);

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
                intent.putExtra("User", trck.getUserLogin());
                startActivity(intent);
            }
        });


        playlist = (ImageButton) findViewById(R.id.playlist);
        text_playlist = findViewById(R.id.text_playlist);
        layoutPlaylist = findViewById(R.id.layoutPlaylist);
        layoutPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Add2PlaylistActivity.class);
                intent.putExtra("Trck", trck);

                startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
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

    @Override
    public void onTracksReceived(List<Track> tracks) {

    }

    @Override
    public void onNoTracks(Throwable throwable) {

    }

    @Override
    public void onPersonalTracksReceived(List<Track> tracks) {

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
    public void onTrackLiked(int id) {
        if(trck.isLiked()){
            Toast.makeText(getApplicationContext(), "Afegit correctament", Toast.LENGTH_SHORT).show();
            trck.setLiked(false);
            System.out.println("hooaoofodwfoiehfowehif");
        }else{
            Toast.makeText(getApplicationContext(), "Afegit correctament", Toast.LENGTH_SHORT).show();
            trck.setLiked(true);
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
    public void onFailure(Throwable throwable) {

    }
}


