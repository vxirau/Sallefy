package com.prpr.androidpprog2.entregable.controller.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.PlaylistManager;
import com.prpr.androidpprog2.entregable.model.UserToken;
import com.prpr.androidpprog2.entregable.utils.Session;

public class InfoArtistaActivity extends AppCompatActivity {

    private static PlaylistManager pManager;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

    }

    private void initViews(){

    }

}
