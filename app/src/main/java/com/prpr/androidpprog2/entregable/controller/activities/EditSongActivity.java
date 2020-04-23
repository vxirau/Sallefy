package com.prpr.androidpprog2.entregable.controller.activities;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.adapters.ImageAdapter;
import com.prpr.androidpprog2.entregable.controller.dialogs.ErrorDialog;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.GenreCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.TrackCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.GenreManager;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.TrackManager;
import com.prpr.androidpprog2.entregable.controller.music.ReproductorService;
import com.prpr.androidpprog2.entregable.model.Genre;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.model.Upload;
import com.prpr.androidpprog2.entregable.utils.Constants;
import com.prpr.androidpprog2.entregable.utils.Session;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EditSongActivity extends AppCompatActivity implements TrackCallback, GenreCallback {

    private static final int chooseRequest = 1;
    private Uri mPhotoUri;
    private String coverPas, downloadUri;
    private Task<Uri> mUploadTask;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;

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
    private Button show_uploads;

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
    private Button genere1;
    private LinearLayout genere2;
    private Button canviar_genere;
    private Spinner genere_canviat;
    private Spinner genere_total;
    private ArrayList<Genre> mGenresObjs;
    private ArrayList<String> mGenres;
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> adapter2;

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
    private Playlist plyl;

    //Botons generals
    private Button guardar;
    private Button cancel;

    private Context context;
    private TrackManager tManager;

    private Button eliminar;

    private boolean spinner_in;
    private boolean spinner_in2;


    //----------------------------------------------------------------PART DE SERVICE--------------------------------------------------------------------------------
    private ReproductorService serv;
    private boolean servidorVinculat=false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ReproductorService.LocalBinder binder = (ReproductorService.LocalBinder) service;
            serv = binder.getService();
            servidorVinculat = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            servidorVinculat = false;
        }
    };

    void doUnbindService() {
        if (servidorVinculat) {
            unbindService(serviceConnection);
            servidorVinculat = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!servidorVinculat){
            Intent intent = new Intent(this, ReproductorService.class);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    //----------------------------------------------------------------FIN DE LA PART DE SERVICE--------------------------------------------------------------------------------





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_song);
        trck = (Track) getIntent().getSerializableExtra("Trck");
        plyl = (Playlist) getIntent().getSerializableExtra("Playlst");
        initViews();
        getData();
        getTrackData();
        tManager = new TrackManager(this);
        omplir();
    }

    private void omplir() {
        song_name.setText(trck.getName());
        song_canviada.setText(trck.getName());
        if (trck.getThumbnail() != null && !trck.getThumbnail().equals("")) {
            Picasso.get().load(trck.getThumbnail()).into(song_cover);
            Picasso.get().load(trck.getThumbnail()).into(image_upload);
        }else{
            Picasso.get().load("https://user-images.githubusercontent.com/48185184/77687559-e3778c00-6f9e-11ea-8e14-fa8ee4de5b4d.png").into(song_cover);
            Picasso.get().load("https://user-images.githubusercontent.com/48185184/77687559-e3778c00-6f9e-11ea-8e14-fa8ee4de5b4d.png").into(image_upload);
        }

        String segons = "";
        if (trck.getDuration() != null || trck.getDuration() == 0) {
            if (trck.getDuration() % 60 < 10) {
                segons = "0" + trck.getDuration() % 60;
            } else {
                segons = String.valueOf(trck.getDuration() % 60);
            }
            durada_name.setText(trck.getDuration() / 60 + ":" + segons);
            durada_canviada.setText(trck.getDuration() / 60 + ":" + segons);
        } else {
            durada_name.setText("00:00");
            durada_canviada.setText("00:00");

        }
    }


    private void initViews() {

        mStorage = FirebaseStorage.getInstance().getReference(Session.changeLogin(Session.getUser().getLogin()));
        mDatabase = FirebaseDatabase.getInstance().getReference(Session.changeLogin(Session.getUser().getLogin()));


        //Portada
        song_cover = findViewById(R.id.SongCover);
        portada1 = findViewById(R.id.portada);
        canvi_portada = findViewById(R.id.cp);
        portada2 = findViewById(R.id.canvi_portada);
        image_upload = findViewById(R.id.image_upload);
        guardar_portada = findViewById(R.id.guardar_portada);
        cancel_portada = findViewById(R.id.cancel_portada);
        choose_file = findViewById(R.id.choose_file);
        upload_file = findViewById(R.id.upload_file);
        song_cover = findViewById(R.id.SongCover);
        show_uploads = findViewById(R.id.show_results);

        if(ImageAdapter.upload != null){
            Picasso.get().load(ImageAdapter.upload.getImageUrl()).fit().centerCrop().into(song_cover);
            coverPas = ImageAdapter.upload.getImageUrl();
        }


        canvi_portada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                portada1.setVisibility(View.INVISIBLE);
                portada2.setVisibility(View.VISIBLE);
            }
        });

        choose_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseFile();
            }
        });

        upload_file = findViewById(R.id.upload_file);
        upload_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUploadTask != null) {
                    Toast.makeText(EditSongActivity.this, "Upload already in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });

        show_uploads.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                openImages();
            }
        });


        guardar_portada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                portada1.setVisibility(View.VISIBLE);
                portada2.setVisibility(View.INVISIBLE);
                Picasso.get().load(coverPas).fit().centerCrop().into(song_cover);
            }
        });

        cancel_portada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                portada1.setVisibility(View.VISIBLE);
                portada2.setVisibility(View.INVISIBLE);
            }
        });


        //Song
        upload_file = findViewById(R.id.upload_file);
        song_canviada = findViewById(R.id.nom_canviat);
        guardar_song = findViewById(R.id.guardar_song);
        text_song = findViewById(R.id.text_canco);
        song_name = findViewById(R.id.nom_canco);
        cancel_song = findViewById(R.id.cancel_song);

        song1 = findViewById(R.id.song1);
        song2 = findViewById(R.id.song2);

        canvi_song = findViewById(R.id.boto_canviar_nom);
        canvi_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                song1.setVisibility(View.INVISIBLE);
                song2.setVisibility(View.VISIBLE);
                text_song.setVisibility(View.VISIBLE);
            }
        });

        guardar_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                song1.setVisibility(View.VISIBLE);
                song2.setVisibility(View.INVISIBLE);
                if (song_canviada.getText().length() != 0) {
                    song_name.setText(song_canviada.getText());
                }
                text_song.setVisibility(View.INVISIBLE);
            }
        });

        cancel_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                song1.setVisibility(View.VISIBLE);
                song2.setVisibility(View.INVISIBLE);
                text_song.setVisibility(View.INVISIBLE);
            }
        });

        //Durada
        durada_name = findViewById(R.id.durada);
        text_durada = findViewById(R.id.text_durada);
        durada1 = findViewById(R.id.durada1);
        durada2 = findViewById(R.id.durada2);
        canvi_durada = findViewById(R.id.boto_canviar_durada);
        durada_canviada = findViewById(R.id.durada_canviada);
        guardar_durada = findViewById(R.id.guardar_durada);
        cancel_durada = findViewById(R.id.cancel_durada);

        canvi_durada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                durada1.setVisibility(View.INVISIBLE);
                durada2.setVisibility(View.VISIBLE);
                text_durada.setVisibility(View.VISIBLE);
            }
        });

        guardar_durada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                durada1.setVisibility(View.VISIBLE);
                durada2.setVisibility(View.INVISIBLE);
                if (durada_canviada.getText().length() != 0) {
                    durada_name.setText(durada_canviada.getText());
                }
                text_durada.setVisibility(View.INVISIBLE);
            }
        });

        cancel_durada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                durada1.setVisibility(View.VISIBLE);
                durada2.setVisibility(View.INVISIBLE);
                text_durada.setVisibility(View.INVISIBLE);
            }
        });

        //Genere
        genere1 = findViewById(R.id.genere1);
        genere2 = findViewById(R.id.genere2);
        genere_canviat = findViewById(R.id.genere_canviat);
        genere_total = findViewById(R.id.genere_total);

        genere1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                genere1.setVisibility(View.INVISIBLE);
                genere2.setVisibility(View.VISIBLE);

            }
        });

        spinner_in = false;
        genere_total.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(!spinner_in){
                    spinner_in = true;
                    return;
                } else {
                    String item = adapterView.getItemAtPosition(i).toString();
                    adapter2.add(item);
                    adapter2.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner_in2 = false;
        genere_canviat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(!spinner_in2){
                    spinner_in2 = true;
                    return;
                } else {
                    if(adapter2.getCount()>1) {
                        String item = adapterView.getItemAtPosition(i).toString();
                        adapter2.remove(item);
                        adapter2.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //ELiminar canco
        eliminar = findViewById(R.id.eliminar);
        eliminar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Track playing = serv.getActiveAudio();
                if(playing.getName().equals(trck.getName()) && playing.getUserLogin().equals(trck.getUserLogin())){
                    serv.removeTrack();
                }
                tManager.removeTrack(trck.getId(), EditSongActivity.this);
            }
        });

        //Guardar general
        guardar = findViewById(R.id.guardar);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trck.setName(song_name.getText().toString());
                trck.setDuration(convertSeconds(durada_name.getText().toString()));
                trck.setThumbnail(coverPas);
                afegirGenere();
                tManager.updateTrack(trck, EditSongActivity.this);
                //
            }
        });

        //Cancel general
        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.nothing, R.anim.nothing);
            }
        });

    }

    private void openImages(){
        Intent intent = new Intent(this, ImageActivity.class);
        startActivityForResult(intent,2);
    }

    private void getData() {
        GenreManager.getInstance(this).getAllGenres(EditSongActivity.this);
    }

    private void getTrackData() {
        TrackManager.getInstance(this).getTrack(trck.getId(), EditSongActivity.this);
    }

    private int convertSeconds(String d) {
        String[] s;
        s = d.split(":");
        int minuts = Integer.parseInt(s[0]);
        int segons = Integer.parseInt(s[1]);
        return segons + (minuts * 60);
    }

    private void chooseFile() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, chooseRequest);
    }



    private Genre getGenre(String nom){
        Genre ger = null;
        for (Genre g: mGenresObjs){
            if(g.getName().equals(nom)){
                ger = g;
            }
        }
        return ger;
    }

    private void afegirGenere(){
        int c = adapter2.getCount();
        List<Genre> gen = new ArrayList<>();
        for(int i=0; i<c; i++){
            gen.add(getGenre(adapter2.getItem(i)));
        }
        trck.setGenres(gen);
    }

    private String getExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mTm = MimeTypeMap.getSingleton();
        return mTm.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (mPhotoUri != null) {
            StorageReference fileRef = mStorage.child("file" + System.currentTimeMillis() + "." + getExtension(mPhotoUri));
            mUploadTask = fileRef.putFile(mPhotoUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(EditSongActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                        downloadUri = task.getResult().toString();
                        Upload upload = new Upload(downloadUri);
                        String id = mDatabase.push().getKey();
                        mDatabase.child(id).setValue(upload);
                    } else {
                        Toast.makeText(EditSongActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "You have to choose a file", Toast.LENGTH_SHORT).show();
        }
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

    }

    @Override
    public void onTrackNotFound(Throwable throwable) {

    }


    @Override
    public void onTrackUpdated(Track body) {
        Intent intent = new Intent(getApplicationContext(), InfoTrackActivity.class);
        intent.putExtra("Trck", body);
        intent.putExtra("Playlst", plyl);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
    }

    @Override
    public void onTrackUpdateFailure(Throwable throwable) {
        ErrorDialog e = new ErrorDialog(this);
        e.showErrorDialog("Track couldn't be updated");
    }

    @Override
    public void onTrackDeleted(int id) {
        Toast.makeText(this, "Eliminat correctament", Toast.LENGTH_SHORT).show();

        if (plyl == null) {
            Intent intent = new Intent(getApplicationContext(), UserMainActivity.class);
            startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
        } else {
            Intent intent2 = new Intent(getApplicationContext(), PlaylistActivity.class);
            intent2.putExtra("Playlst", plyl);
            startActivityForResult(intent2, Constants.NETWORK.LOGIN_OK);
        }
    }

    @Override
    public void onTrackReceived(Track track) {
        mGenres = (ArrayList<String>) track.getGenres().stream().map(Genre -> Genre.getName()).collect(Collectors.toList());
        adapter2 = new ArrayAdapter<>(EditSongActivity.this, R.layout.support_simple_spinner_dropdown_item, mGenres);
        genere_canviat.setAdapter(adapter2);
    }

    @Override
    public void onFailure(Throwable throwable) {

    }

    //Generes tots
    @Override
    public void onGenresReceive(ArrayList<Genre> genres) {
        mGenresObjs = genres;
        mGenres = (ArrayList<String>) genres.stream().map(Genre -> Genre.getName()).collect(Collectors.toList());
        adapter = new ArrayAdapter<>(EditSongActivity.this, R.layout.support_simple_spinner_dropdown_item, mGenres);
        genere_total.setAdapter(adapter);
    }


    @Override
    public void onTracksByGenre(ArrayList<Track> tracks) {

    }

    @Override
    public void onGenreSelected(Genre genere) {

    }

    @Override
    public void onGenreCreated(Genre data) {

    }

    @Override
    public void onGenreCreateFailure(Throwable throwable) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == chooseRequest && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mPhotoUri = data.getData();
            Upload u = new Upload(mPhotoUri.toString());
            coverPas = u.getImageUrl();
            Picasso.get().load(mPhotoUri).fit().centerCrop().into(image_upload);
        } else {
            if(requestCode == 2 && ImageAdapter.upload != null){
                Picasso.get().load(ImageAdapter.upload.getImageUrl()).fit().centerCrop().into(image_upload);
                coverPas = ImageAdapter.upload.getImageUrl();
            }
        }
    }
}




