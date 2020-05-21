package com.prpr.androidpprog2.entregable.controller.activities;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.adapters.ImageAdapter;
import com.prpr.androidpprog2.entregable.controller.dialogs.StateDialog;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.GenreCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.PlaylistCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.TrackCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.CloudinaryManager;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.GenreManager;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.PlaylistManager;
import com.prpr.androidpprog2.entregable.model.Follow;
import com.prpr.androidpprog2.entregable.model.Genre;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.model.Upload;
import com.prpr.androidpprog2.entregable.utils.Constants;
import com.prpr.androidpprog2.entregable.utils.Session;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


public class UploadActivity extends AppCompatActivity implements GenreCallback, TrackCallback, PlaylistCallback {

    private static final int chooseRequest = 1;

    private EditText etTitle;
    private Spinner mSpinner;
    private TextView mFilename, txtShow, txtSave;
    private Button btnFind, btnCancel, btnAccept, btnChoose, btnUpload;
    private PlaylistManager pManager;
    private RecyclerView uRecyclerView;
    private String username;
    private ImageView thumbnail;
    private Uri mFileUri,mPhotoUri;

    private Playlist uploadPlylst;

    private ArrayList<String> mGenres;
    private ArrayList<Genre> mGenresObjs;
    private Context mContext;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private Task<Uri> mUploadTask;
    private String downloadUri, coverPas;


    //--------
    private RecyclerView rView;
    private ImageAdapter iAdapt;

    private FirebaseStorage mFireStorage;
    private DatabaseReference mDataBase;
    private List<Upload> iUploads;

    //--------


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_song);
        uploadPlylst = (Playlist) getIntent().getSerializableExtra("Upload");
        mContext = getApplicationContext();
        initViews();
        getData();

    }
    private void initViews() {


        txtSave = findViewById(R.id.text_save);
        txtSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                saveCover();
            }
        });


        pManager = new PlaylistManager(mContext);

        mStorage = FirebaseStorage.getInstance().getReference(Session.changeLogin(Session.getUser().getLogin()));
        mDatabase = FirebaseDatabase.getInstance().getReference(Session.changeLogin(Session.getUser().getLogin()));

        etTitle = (EditText) findViewById(R.id.create_song_title);
        mFilename = (TextView) findViewById(R.id.create_song_file_name);

        mSpinner = (Spinner) findViewById(R.id.create_song_genre);

        btnFind = (Button) findViewById(R.id.create_song_file);
        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAudioFromStorage();
            }
        });

        btnCancel = (Button) findViewById(R.id.create_song_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.nothing,R.anim.nothing);
            }
        });

        btnAccept = (Button) findViewById(R.id.create_song_accept);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkParameters()) {
                    etTitle.setFocusable(false);
                    showStateDialog(false);
                    uploadToCloudinary();
                }
            }
        });

        btnChoose = (Button) findViewById(R.id.button_choose_image);
        btnChoose.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                chooseFile();
            }
        });

        btnUpload = (Button) findViewById(R.id.button_upload);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mUploadTask != null){
                    Toast.makeText(UploadActivity.this, "Upload already in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });

        txtShow = (TextView) findViewById(R.id.text_view_show_uploads);
        txtShow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                openImages();
            }
        });

        thumbnail = (ImageView) findViewById(R.id.image_upload);

        if(ImageAdapter.upload != null){
            Picasso.get().load(ImageAdapter.upload.getImageUrl()).fit().centerCrop().into(thumbnail);
            coverPas = ImageAdapter.upload.getImageUrl();
        }

        //--------------------------------------------------------------------------------------------

        rView = findViewById(R.id.coverRecycle);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        iUploads = new ArrayList<>();
        mDataBase = FirebaseDatabase.getInstance().getReference(Session.changeLogin(Session.getUser().getLogin()));

        mDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                iUploads.clear();

                for(DataSnapshot d : dataSnapshot.getChildren()){
                    Upload u = d.getValue(Upload.class);
                    u.setKey(d.getKey());
                    iUploads.add(u);
                }

                iAdapt = new ImageAdapter(UploadActivity.this, iUploads);
                rView.setAdapter(iAdapt);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UploadActivity.this, databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });


        //--------------------------------------------------------------------------------------------

    }

    private void saveCover() {
        rView.setVisibility(View.GONE);
        thumbnail.setVisibility(View.VISIBLE);
        txtShow.setVisibility(View.VISIBLE);
        txtSave.setVisibility(View.GONE);
        if (ImageAdapter.upload != null) {
            Picasso.get().load(ImageAdapter.upload.getImageUrl()).fit().centerCrop().into(thumbnail);
            coverPas = ImageAdapter.upload.getImageUrl();
        }
    }

    private void openImages(){
        rView.setVisibility(View.VISIBLE);
        thumbnail.setVisibility(View.GONE);
        txtShow.setVisibility(View.GONE);
        txtSave.setVisibility(View.VISIBLE);
    }

    private void chooseFile(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,chooseRequest);
    }

    private String getExtension(Uri uri){
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
                        Toast.makeText(UploadActivity.this,"Upload Successful", Toast.LENGTH_SHORT).show();
                        downloadUri = task.getResult().toString();
                        Upload upload = new Upload(downloadUri);
                        String id = mDatabase.push().getKey();
                        mDatabase.child(id).setValue(upload);
                    } else {
                        Toast.makeText(UploadActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "You have to choose a file", Toast.LENGTH_SHORT).show();
        }
    }

    private void getData() {
        GenreManager.getInstance(this).getAllGenres(this);
    }

    private boolean checkParameters() {
        if (!etTitle.getText().toString().equals("")) {
            if (mFileUri != null) {
                return true;
            }
        }
        return false;
    }

    private void showStateDialog(boolean completed) {
        StateDialog.getInstance(this).showStateDialog(completed);
    }

    private void getAudioFromStorage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/mpeg");
        startActivityForResult(Intent.createChooser(intent, "Choose a song"), Constants.STORAGE.SONG_SELECTED);
    }

    private void uploadToCloudinary() {
        Genre genre = new Genre();
        for (Genre g: mGenresObjs) {
            if (g.getName().equals(mSpinner.getSelectedItem().toString())) {
                genre = g;
            }
        }
        CloudinaryManager.getInstance(this, this).uploadAudioFile(mFileUri, etTitle.getText().toString(), genre, coverPas);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.STORAGE.SONG_SELECTED && resultCode == RESULT_OK) {
            mFileUri = data.getData();
            mFilename.setText(mFileUri.toString());
        } else {
            if (requestCode == chooseRequest && resultCode == RESULT_OK && data != null && data.getData() != null) {
                mPhotoUri = data.getData();
                Upload u = new Upload(mPhotoUri.toString());
                coverPas = u.getImageUrl();
                Picasso.get().load(mPhotoUri).fit().centerCrop().into(thumbnail);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onGenresReceive(ArrayList<Genre> genres) {
        mGenresObjs = genres;
        mGenres = (ArrayList<String>) genres.stream().map(Genre -> Genre.getName()).collect(Collectors.toList());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, mGenres);
        mSpinner.setAdapter(adapter);
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
    public void onAllGenreFailure(Throwable throwable) {

    }

    @Override
    public void onGenreCreateFailure(Throwable throwable) {

    }

    @Override
    public void onFailure(Throwable throwable) {

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
        if(uploadPlylst != null) {
            uploadPlylst.getTracks().add(t);
            pManager.updatePlaylist(uploadPlylst, this);
        } else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
        }
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
    public void onMyTracksFailure(Throwable throwable) {

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
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
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

    @Override
    public void onAllMyPlaylistFailure(Throwable throwable) {

    }

    @Override
    public void onFollowingPlaylistsFailure(Throwable throwable) {

    }
}
