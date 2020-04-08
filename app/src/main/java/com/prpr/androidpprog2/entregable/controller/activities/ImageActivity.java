package com.prpr.androidpprog2.entregable.controller.activities;

import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.adapters.ImageAdapter;
import com.prpr.androidpprog2.entregable.model.Upload;
import com.prpr.androidpprog2.entregable.utils.Session;

import java.util.ArrayList;
import java.util.List;


public class ImageActivity  extends AppCompatActivity {
    private RecyclerView rView;
    private ImageAdapter iAdapt;

    private ProgressBar pBar;


    //private FirebaseStorage mStorage;
    private DatabaseReference mDataBase;
    private List<Upload> iUploads;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        pBar = findViewById(R.id.progressLoad);
        rView = findViewById(R.id.recyclerImages);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(new LinearLayoutManager(this));

        iUploads = new ArrayList<>();


        mDataBase = FirebaseDatabase.getInstance().getReference(Session.getUser().getLogin());
        //mStorage = FirebaseStorage.getInstance();

        mDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //iUploads.clear();

                for(DataSnapshot d : dataSnapshot.getChildren()){
                    Upload u = d.getValue(Upload.class);
                    //u.setmKey(d.getKey());
                    iUploads.add(u);
                }

                iAdapt = new ImageAdapter(ImageActivity.this, iUploads);
                rView.setAdapter(iAdapt);

                pBar.setVisibility(View.INVISIBLE);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ImageActivity.this, databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                pBar.setVisibility(View.INVISIBLE);
            }
        });
    }


}
