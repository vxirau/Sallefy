package com.prpr.androidpprog2.entregable.controller.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.adapters.PlaylistAdapter;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.PlaylistCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.PlaylistManager;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.UserToken;
import com.prpr.androidpprog2.entregable.utils.Constants;
import com.prpr.androidpprog2.entregable.utils.Session;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PlaylistCallback {

    private ImageButton btnTrackImg;
    private FloatingActionButton btnNewPlaylist;
    private Button samplePlaylist;
    private RecyclerView playlists_descobrir;
    private ArrayList<Playlist> discover;
    private RecyclerView les_teves_playlists;
    private ArrayList<Playlist> teves;
    private PlaylistManager pManager;
    private Context mContext;


    private Handler mHandler;
    private Runnable mRunnable;
    private BarVisualizer mVisualizer;
    private int mDuration;
    private TextView tvTitle;
    private TextView tvAuthor;
    private ImageButton ivPhoto;
    private static final String PLAY_VIEW = "PlayIcon";
    private static final String STOP_VIEW = "StopIcon";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        enableInitialButtons();
        UserToken userToken = Session.getInstance(this).getUserToken();
        String usertkn = userToken.getIdToken();
        pManager = new PlaylistManager(this);
        pManager.getAllPlaylists(this);

    }

    private void initViews() {

        playlists_descobrir = (RecyclerView) findViewById(R.id.playlists_descobrir);
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        PlaylistAdapter adapter = new PlaylistAdapter(this, null);
        adapter.setPlaylistCallback(this);
        playlists_descobrir.setLayoutManager(manager);
        playlists_descobrir.setAdapter(adapter);


        les_teves_playlists = (RecyclerView) findViewById(R.id.les_teves_playlists);
        LinearLayoutManager manager2 = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        PlaylistAdapter adapter2 = new PlaylistAdapter(this, null);
        les_teves_playlists.setLayoutManager(manager2);
        les_teves_playlists.setAdapter(adapter2);

        btnTrackImg = findViewById(R.id.track_img);
        btnTrackImg.setEnabled(false);
        btnTrackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReproductorActivity.class);
                startActivity(intent);
            }
        });

        btnNewPlaylist = findViewById(R.id.crearNovaPlaylist);
        btnNewPlaylist.setEnabled(false);
        btnNewPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewPlaylistActivity.class);
                startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
            }
        });

        samplePlaylist = findViewById(R.id.samplePlaylist);
        samplePlaylist.setEnabled(false);
        samplePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inte = new Intent(getApplicationContext(), PlaylistActivity.class);
                inte.putExtra("Playlst", new Playlist("Sample"));
                startActivityForResult(inte, Constants.NETWORK.LOGIN_OK);
            }
        });

       /* //----------
        mHandler = new Handler();

        tvAuthor = findViewById(R.id.dynamic_artist);
        tvTitle = findViewById(R.id.dynamic_title);
        tvTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        tvTitle.setSelected(true);
        tvTitle.setSingleLine(true);

        ivPhoto = findViewById(R.id.track_img);

        btnPlayStop = (ImageButton)findViewById(R.id.dynamic_play_btn);
        btnPlayStop.setTag(PLAY_VIEW);
        btnPlayStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (btnPlayStop.getTag().equals(PLAY_VIEW)) {
                    playAudio();
                } else {
                    pauseAudio();
                }
            }
        });

        mSeekBar = (SeekBar) findViewById(R.id.dynamic_seekBar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mPlayer.seekTo(progress);
                }
                if (mDuration > 0) {
                    int newProgress = ((progress*100)/mDuration);
                    System.out.println("New progress: " + newProgress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //---------*/

    }

    private void enableInitialButtons() {
        btnNewPlaylist.setEnabled(true);
        btnTrackImg.setEnabled(true);
        samplePlaylist.setEnabled(true);
    }

    private void enableNetworkButtons() {
        btnNewPlaylist.setEnabled(true);
    }

    private void enableAllButtons() {
        btnNewPlaylist.setEnabled(true);
        btnTrackImg.setEnabled(true);
        samplePlaylist.setEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.NETWORK.LOGIN_OK) {
            enableNetworkButtons();
            if (resultCode == RESULT_OK) {}
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
    public void onPlaylistCreated(Playlist playlist) {

    }

    @Override
    public void onPlaylistFailure(Throwable throwable) {

    }

    @Override
    public void onPlaylistRecieved(List<Playlist> playlists) {
        this.discover = (ArrayList) playlists;
        PlaylistAdapter p = new PlaylistAdapter(this, (ArrayList) playlists);
        p.setPlaylistCallback(this);
        playlists_descobrir.setAdapter(p);
    }

    @Override
    public void onNoPlaylists(Throwable throwable) {
        Toast.makeText(this, "No tens playlists", Toast.LENGTH_LONG);
    }

    @Override
    public void onPlaylistSelected(Playlist playlist) {
        Intent intent = new Intent(getApplicationContext(), PlaylistActivity.class);
        intent.putExtra("Playlst", playlist);
        startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
    }

    @Override
    public void onTrackAdded(Playlist body) {

    }

    @Override
    public void onTrackAddFailure(Throwable throwable) {

    }


}
