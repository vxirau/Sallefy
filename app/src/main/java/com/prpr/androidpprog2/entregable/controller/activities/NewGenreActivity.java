package com.prpr.androidpprog2.entregable.controller.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.dialogs.ErrorDialog;
import com.prpr.androidpprog2.entregable.controller.dialogs.LoadingDialog;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.GenreCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.GenreCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.GenreManager;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.GenreManager;
import com.prpr.androidpprog2.entregable.model.Follow;
import com.prpr.androidpprog2.entregable.model.Genre;
import com.prpr.androidpprog2.entregable.model.Genre;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class NewGenreActivity extends Activity implements GenreCallback {

    private Button tornarEnrere;
    private Button newGenre;
    private EditText nomGenre;
    private GenreCallback pCallback;
    private GenreManager gManager;
    private Context mContext;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_playlist);
        initViews();
        enableInitialButtons();
        loadingDialog = new LoadingDialog(this);
    }

    private void initViews() {
        gManager = new GenreManager(this);
        nomGenre = findViewById(R.id.nomNovaPlaylist);
        nomGenre.setHint("Name for the Genre");
        tornarEnrere = findViewById(R.id.amagarNovaPlaylist);
        tornarEnrere.setEnabled(false);
        tornarEnrere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.nothing,R.anim.nothing);
            }
        });
        newGenre = findViewById(R.id.AfegirAPlaylist);
        newGenre.setEnabled(true);
        newGenre.setText("Create New Genre");
        newGenre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.showLoadingDialog("Creating genre");
                doCreateGenre(new Genre(nomGenre.getText().toString().toUpperCase()));
            }
        });
    }


    private void doCreateGenre(Genre genre){
        gManager.createNewGenre(genre, this);
    }

    private void enableInitialButtons() {
        tornarEnrere.setEnabled(true);
    }


    @Override
    public void onGenresReceive(ArrayList<Genre> genres) {

    }

    @Override
    public void onTracksByGenre(ArrayList<Track> tracks) {

    }

    @Override
    public void onGenreSelected(Genre genere) {

    }

    @Override
    public void onGenreCreated(Genre data) {
        loadingDialog.cancelLoadingDialog();
        finish();
        overridePendingTransition(R.anim.nothing,R.anim.nothing);
    }

    @Override
    public void onGenreCreateFailure(Throwable throwable) {
        loadingDialog.cancelLoadingDialog();
        ErrorDialog.getInstance(this).showErrorDialog("The creation of the genre \""+ nomGenre.getText().toString() + "\" was not successful");
    }

    @Override
    public void onFailure(Throwable throwable) {

    }
}
