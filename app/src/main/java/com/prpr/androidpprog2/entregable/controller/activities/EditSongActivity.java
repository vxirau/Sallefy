package com.prpr.androidpprog2.entregable.controller.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.dialogs.ErrorDialog;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.TrackCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.CloudinaryManager;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.PlaylistManager;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.TrackManager;
import com.prpr.androidpprog2.entregable.model.Genre;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.utils.Session;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EditSongActivity extends Activity implements TrackCallback {
    //Portada
    private LinearLayout portada1;
    private LinearLayout portada2;
    private ImageView image_upload;
    private Button choose_file;
    private Button upload_file;
    private Button canvi_portada;
    private ImageView song_cover;
    private Button guardar_portada;
    private Button cancel_portada;

    //Nom
    private TextView text_song; //Titol: Nom de la canco
    private TextView song_name; //Nom canco
    private LinearLayout song1; //Primera linearlayout abans de canvi
    private LinearLayout song2; //Segona linearlayout despres de canvi
    private Button canvi_song; //Boto: canviar nom
    private EditText song_canviada; //Nou nom de la canco
    private Button guardar_song;
    private Button cancel_song;


    //Genere
    private TextView text_genere;
    private TextView genere_name;
    private LinearLayout genere1;
    private LinearLayout genere2;
    private Button canviar_genere;
    private Spinner genere_canviat;
    private Button guardar_genere;
    private Button cancel_genere;


    //Durada
    private TextView text_durada;
    private TextView durada_name;
    private LinearLayout durada1;
    private LinearLayout durada2;
    private Button canvi_durada;
    private EditText durada_canviada;
    private Button guardar_durada;
    private Button cancel_durada;

    //Canco a editar
    private Track trck;

    //Botons generals
    private Button guardar;
    private Button cancel;

    private Context context;
    private TrackManager tManager;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_song);
        trck = (Track) getIntent().getSerializableExtra("Trck");
        initViews();
        tManager = new TrackManager(this);
        omplir();
    }

    private void omplir() {
        song_name.setText(trck.getName());
        song_canviada.setText(trck.getName());
        String segons = "";
        if(trck.getDuration()!=null || trck.getDuration()==0){
            if(trck.getDuration()%60<10){
                segons = "0" + trck.getDuration()%60;
            }else{
                segons = String.valueOf(trck.getDuration()%60);
            }
            durada_name.setText(trck.getDuration()/60 + ":" + segons);
            durada_canviada.setText(trck.getDuration()/60 + ":" + segons);
        }else{
            durada_name.setText("00:00");
            durada_canviada.setText("00:00");

        }
    }


    private void initViews()  {

        //Portada
        song_cover = findViewById(R.id.SongCover);
        portada1 = findViewById(R.id.portada);
        canvi_portada= findViewById(R.id.cp);
        portada2 = findViewById(R.id.canvi_portada);
        image_upload = findViewById(R.id.image_upload);
        guardar_portada = findViewById(R.id.guardar_portada);
        cancel_portada = findViewById(R.id.cancel_portada);
        choose_file = findViewById(R.id.choose_file);
        upload_file=findViewById(R.id.upload_file);
        song_cover=findViewById(R.id.SongCover);

        if(trck.getThumbnail()!=null){
            Picasso.get().load(trck.getThumbnail()).into(song_cover);
        }else{
            Picasso.get().load("https://community.spotify.com/t5/image/serverpage/image-id/25294i2836BD1C1A31BDF2/image-size/original?v=mpbl-1&px=-1").into(song_cover);
        }

        canvi_portada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                portada1.setVisibility(View.INVISIBLE);
                portada2.setVisibility(View.VISIBLE);
            }
        });

        choose_file.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //chooseFile();
            }
        });

        cancel_portada.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                portada1.setVisibility(View.VISIBLE);
                portada2.setVisibility(View.INVISIBLE);
            }
        });


        //Song
        upload_file = findViewById(R.id.upload_file);
        song_canviada = findViewById(R.id.nom_canviat);
        guardar_song=findViewById(R.id.guardar_song);
        text_song = findViewById(R.id.text_canco);
        song_name = findViewById(R.id.nom_canco);
        cancel_song = findViewById(R.id.cancel_song);

        song1 = findViewById(R.id.song1);
        song2 = findViewById(R.id.song2);

        canvi_song= findViewById(R.id.boto_canviar_nom);
        canvi_song.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                song1.setVisibility(View.INVISIBLE);
                song2.setVisibility(View.VISIBLE);
                text_song.setVisibility(View.VISIBLE);
            }
        });

        guardar_song.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                song1.setVisibility(View.VISIBLE);
                song2.setVisibility(View.INVISIBLE);
                if(song_canviada.getText().length()!=0) {
                    song_name.setText(song_canviada.getText());
                }
                text_song.setVisibility(View.INVISIBLE);
            }
        });

        cancel_song.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                song1.setVisibility(View.VISIBLE);
                song2.setVisibility(View.INVISIBLE);
                text_song.setVisibility(View.INVISIBLE);
            }
        });

        //Durada
        durada_name= findViewById(R.id.durada);
        text_durada = findViewById(R.id.text_durada);
        durada1 = findViewById(R.id.durada1);
        durada2 = findViewById(R.id.durada2);
        canvi_durada = findViewById(R.id.boto_canviar_durada);
        durada_canviada = findViewById(R.id.durada_canviada);
        guardar_durada = findViewById(R.id.guardar_durada);
        cancel_durada = findViewById(R.id.cancel_durada);

        canvi_durada.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                durada1.setVisibility(View.INVISIBLE);
                durada2.setVisibility(View.VISIBLE);
                text_durada.setVisibility(View.VISIBLE);
            }
        });

        guardar_durada.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                durada1.setVisibility(View.VISIBLE);
                durada2.setVisibility(View.INVISIBLE);
                if(durada_canviada.getText().length()!=0) {
                    durada_name.setText(durada_canviada.getText());
                }
                text_durada.setVisibility(View.INVISIBLE);
            }
        });

        cancel_durada.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                durada1.setVisibility(View.VISIBLE);
                durada2.setVisibility(View.INVISIBLE);
                text_durada.setVisibility(View.INVISIBLE);
            }
        });

        //Guardar general
        guardar = findViewById(R.id.guardar);
        guardar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                trck.setName(song_name.getText().toString());
                trck.setDuration(convertSeconds(durada_name.getText().toString()));
                //trck.setThumbnail("");
                //Genre g = new Genre((String) genere_name.getText());
                //trck.getGenres().add(g);
                tManager.updateTrack(trck, EditSongActivity.this);
            }
        });

        //Cancel general
        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.nothing,R.anim.nothing);
            }
        });

    }

    private int convertSeconds(String d){
        String[] s;
        s = d.split(":");
        int minuts = Integer.parseInt(s[0]);
        int segons = Integer.parseInt(s[1]);
        return segons + (minuts*60);
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

    }

    @Override
    public void onTrackNotFound(Throwable throwable) {

    }

    @Override
    public void onTrackUpdated(Track body) {
        finish();
    }

    @Override
    public void onTrackUpdateFailure(Throwable throwable) {
        ErrorDialog e = new ErrorDialog(this);
        e.showErrorDialog("Track couldn't be updated");
    }

    @Override
    public void onFailure(Throwable throwable) {

    }
}
