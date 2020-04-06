package com.prpr.androidpprog2.entregable.controller.activities;

import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.adapters.ImageAdapter;
import com.prpr.androidpprog2.entregable.utils.Session;

import java.util.ArrayList;
import java.util.List;


public class ImageActivity  extends AppCompatActivity implements ImageAdapter.OnClickListener {
    private RecyclerView rView;
    private ImageAdapter iAdapt;

    private ProgressBar pBar;

    private DatabaseReference mDataBase;
    private List<String> uri;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        pBar = findViewById(R.id.progressLoad);
        rView = findViewById(R.id.recyclerImages);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(new LinearLayoutManager(this));

        uri = new ArrayList<>();

        mDataBase = FirebaseDatabase.getInstance().getReference(Session.getUser().getLogin());

        mDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d : dataSnapshot.getChildren()){
                    String file = d.getValue(String.class);
                    uri.add(file);
                }


                iAdapt = new ImageAdapter(ImageActivity.this, uri);
                rView.setAdapter(iAdapt);

                iAdapt.onClickedItem(ImageActivity.this);
                pBar.setVisibility(View.INVISIBLE);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ImageActivity.this, databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                pBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void OnSelectedItem(int position) {
        Toast.makeText(ImageActivity.this, "Select item", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteItem(int position) {
        Toast.makeText(ImageActivity.this, "Delete item", Toast.LENGTH_SHORT).show();
    }
}
