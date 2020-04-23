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
import com.prpr.androidpprog2.entregable.controller.restapi.callback.PlaylistCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.TrackCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.PlaylistManager;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.TrackManager;
import com.prpr.androidpprog2.entregable.model.Follow;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.model.User;
import com.prpr.androidpprog2.entregable.utils.Constants;
import com.prpr.androidpprog2.entregable.utils.Session;
import com.squareup.picasso.Picasso;

import java.util.List;


public class InfoTrackActivity extends AppCompatActivity implements TrackCallback, PlaylistCallback {

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

    private ImageButton eliminar_icono;
    private TextView eliminar_text;
    private LinearLayout layouteliminar;

    private Button cancel;

    private Track trck;
//3.37
    private ErrorDialog er;

    private User user;

    private TrackManager tManager;

    private PlaylistManager pManager;

    private Playlist playl;

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_track);
        trck = (Track) getIntent().getSerializableExtra("Trck");
        playl=(Playlist) getIntent().getSerializableExtra("Playlst");
        if(getIntent().getSerializableExtra("UserInfo")!=null){
            user = (User) getIntent().getSerializableExtra("UserInfo");
        }
        initViews();
        tManager = new TrackManager(this);
        pManager = new PlaylistManager(this);
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
                if(Session.getInstance(getApplicationContext()).getUser().getLogin().equals(trck.getUserLogin())) {
                    er.showErrorDialog("You cannot check yourself out!");
                }else{
                    Intent intent = new Intent(getApplicationContext(), InfoArtistaActivity.class);
                    intent.putExtra("User", trck.getUser());
                    startActivity(intent);
                }
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

        eliminar_icono = (ImageButton) findViewById(R.id.eliminar);
        eliminar_text = findViewById(R.id.text_eliminar);
        layouteliminar = findViewById(R.id.layoutEliminar);
        layouteliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Session.getInstance(getApplicationContext()).getUser().getLogin().equals(playl.getUserLogin())) {
                    playl.getTracks().remove(trck);
                    pManager.updatePlaylist(playl, InfoTrackActivity.this);
                    Toast.makeText(InfoTrackActivity.this, "Eliminada correctament", Toast.LENGTH_SHORT).show();
                }else{
                    er.showErrorDialog("This playlist is not yours to edit");
                }
            }
        });

        if(Session.getInstance(getApplicationContext()).getUser().getLogin().equals(playl.getUserLogin())) {
            layouteliminar.setVisibility(View.VISIBLE);
            layoutedit.setAlpha((float) 1.0);
        }else{
            layouteliminar.setVisibility(View.INVISIBLE);
            layoutedit.setAlpha((float) 0.60);
        }

        if(playl==null){
            layouteliminar.setVisibility(View.INVISIBLE);
        }

        cancel= findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getApplicationContext(), UserMainActivity.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent2, Constants.NETWORK.LOGIN_OK);
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
    public void onPersonalLikedTracksReceived(List<Track> tracks) {

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
    public void onTrackDeleted(int id) {

    }

    @Override
    public void onTrackReceived(Track track) {

    }

    @Override
    public void onFailure(Throwable throwable) {

    }

    @Override
    public void onPlaylistCreated(Playlist playlist) {

    }

    @Override
    public void onPlaylistFailure(Throwable throwable) {

    }

    @Override
    public void onPlaylistRecieved(List<Playlist> playlists) {

    }

    @Override
    public void onNoPlaylists(Throwable throwable) {

    }

    @Override
    public void onPlaylistSelected(Playlist playlist) {

    }

    @Override
    public void onPlaylistToUpdated(Playlist body) {

    }

    @Override
    public void onTrackAddFailure(Throwable throwable) {

    }

    @Override
    public void onAllPlaylistRecieved(List<Playlist> body) {

    }

    @Override
    public void onAllNoPlaylists(Throwable throwable) {

    }

    @Override
    public void onAllPlaylistFailure(Throwable throwable) {

    }

    @Override
    public void onTopRecieved(List<Playlist> topPlaylists) {

    }

    @Override
    public void onNoTopPlaylists(Throwable throwable) {

    }

    @Override
    public void onTopPlaylistsFailure(Throwable throwable) {

    }

    @Override
    public void onFollowingRecieved(List<Playlist> body) {

    }

    @Override
    public void onFollowingChecked(Follow body) {

    }

    @Override
    public void onFollowSuccessfull(Follow body) {

    }

    @Override
    public void onPlaylistRecived(Playlist playlist) {

    }

    @Override
    public void onPlaylistDeleted(Playlist body) {

    }

    @Override
    public void onPlaylistDeleteFailure(Throwable throwable) {

    }
}


