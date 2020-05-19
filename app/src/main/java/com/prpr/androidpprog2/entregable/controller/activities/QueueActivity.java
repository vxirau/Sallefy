package com.prpr.androidpprog2.entregable.controller.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.adapters.TrackListAdapter;
import com.prpr.androidpprog2.entregable.controller.adapters.UserFollowedAdapter;
import com.prpr.androidpprog2.entregable.controller.callbacks.QueueCallback;
import com.prpr.androidpprog2.entregable.controller.dialogs.ErrorDialog;
import com.prpr.androidpprog2.entregable.controller.music.ReproductorService;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.model.User;
import com.prpr.androidpprog2.entregable.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

public class QueueActivity extends AppCompatActivity implements QueueCallback {


    private Button atras;
    private ArrayList<Track> trackList;
    private RecyclerView mRecyclerView;
    private Track current;

    //----------------------------------------------------------------PART DE SERVICE--------------------------------------------------------------------------------
    private ReproductorService serv;
    private boolean servidorVinculat=false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @RequiresApi(api = Build.VERSION_CODES.O)
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

    @Override
    public void onResume() {
        super.onResume();

    }

    //----------------------------------------------------------------FIN DE LA PART DE SERVICE--------------------------------------------------------------------------------




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);


        trackList = (ArrayList<Track>) getIntent().getSerializableExtra("queue");
        current = (Track) getIntent().getSerializableExtra("currentTrack");

        sortTrackList();

        atras = findViewById(R.id.back);
        atras.setEnabled(true);
        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serv.updateAudioList(trackList);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                }else finish();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recycle);
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        TrackListAdapter adapter = new TrackListAdapter(0, this, this, trackList, current);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(adapter);

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {

                int positionDragged = dragged.getAdapterPosition();
                int positionTarget = target.getAdapterPosition();

                Collections.swap(trackList, positionDragged, positionTarget);

                adapter.notifyItemMoved(positionDragged, positionTarget);

                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });

        helper.attachToRecyclerView(mRecyclerView);



    }

    private void sortTrackList() {
        ArrayList<Track> tmp = new ArrayList<>();
        for(Track t : trackList){
            if(!t.getId().equals(current.getId())){
                tmp.add(t);
            }else{
                break;
            }
        }
        for(Track t : tmp){
            trackList.remove(t);
        }
        for(Track t : tmp){
            trackList.add(t);
        }
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
    }

    @Override
    public void dragNdropClicked() {

    }
}
