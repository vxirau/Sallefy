package com.prpr.androidpprog2.entregable.controller.activities;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PlaylistCallback {

    private ImageButton btnTrackImg;
    private FloatingActionButton mes;
    private FloatingActionButton btnNewPlaylist;
    private FloatingActionButton pujarCanco;
    Animation fabOpen, fabClose, rotateForward, rotateBackward;
    boolean isOpen = false;

    private Button samplePlaylist;
    private RecyclerView playlists_descobrir;
    private ArrayList<Playlist> discover;
    private RecyclerView allPlaylistRecycle;
    private ArrayList<Playlist> allPlaylists;
    private PlaylistManager pManager;
    private PlaylistManager pManager2;
    private Context mContext;
    private Playlist fav;
    private TextView favNom;
    private TextView favTotal;
    private TextView misCanciones;
    private ImageButton favCover;
    private MediaPlayer mPlayer;


    private Handler mHandler;
    private Runnable mRunnable;
    private BarVisualizer mVisualizer;
    private int mDuration;
    private TextView tvTitle;
    private TextView tvAuthor;
    private ImageButton ivPhoto;
    private ImageButton btnPlayStop;
    private SeekBar mSeekBar;
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
        pManager.getAllMyPlaylists(this);
        pManager2 = new PlaylistManager(this);
        pManager2.getAllPlaylists(this);

    }

    private void initViews() {

        allPlaylistRecycle = (RecyclerView) findViewById(R.id.allplaylists);
        LinearLayoutManager manager2 = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        PlaylistAdapter adapter2 = new PlaylistAdapter(this, null);
        adapter2.setPlaylistCallback(this);
        allPlaylistRecycle.setLayoutManager(manager2);
        allPlaylistRecycle.setAdapter(adapter2);

        playlists_descobrir = (RecyclerView) findViewById(R.id.playlists_descobrir);
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        PlaylistAdapter adapter = new PlaylistAdapter(this, null);
        adapter.setPlaylistCallback(this);
        playlists_descobrir.setLayoutManager(manager);
        playlists_descobrir.setAdapter(adapter);

        mes= findViewById(R.id.mesButton);
        mes.setEnabled(true);
        mes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
            }
        });


        btnNewPlaylist = findViewById(R.id.novaPlaylst);
        btnNewPlaylist.setEnabled(false);
        btnNewPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
                Intent intent = new Intent(getApplicationContext(), NewPlaylistActivity.class);
                startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
            }
        });

        favNom  = findViewById(R.id.favoritosTitol);
        favTotal  = findViewById(R.id.favoritosTotalSongs);
        favCover  = findViewById(R.id.favoritosImg);
        misCanciones = findViewById(R.id.misCanciones);

        favCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlaylistSelected(fav);
            }
        });


        pujarCanco= findViewById(R.id.pujarCanco);
        pujarCanco.setEnabled(false);
        Context c = this;
        pujarCanco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
                Intent intent = new Intent(getApplicationContext(), UploadActivity.class);
                intent.putExtra("Upload", fav);
                intent.putExtra("User", Session.getInstance(c).getUser());

                startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
            }
        });


        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        rotateForward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);




        btnTrackImg = findViewById(R.id.track_img);
        btnTrackImg.setEnabled(false);
        btnTrackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReproductorActivity.class);
                startActivity(intent);
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


    }

    private void animateFab(){
        if(isOpen){
            btnNewPlaylist.startAnimation(fabClose);
            pujarCanco.startAnimation(fabClose);
            btnNewPlaylist.setClickable(false);
            pujarCanco.setClickable(false);
            btnNewPlaylist.setEnabled(false);
            pujarCanco.setEnabled(false);
            isOpen=false;
        }else{
            btnNewPlaylist.startAnimation(fabOpen);
            pujarCanco.startAnimation(fabOpen);
            btnNewPlaylist.setClickable(true);
            pujarCanco.setClickable(true);
            btnNewPlaylist.setEnabled(true);
            pujarCanco.setEnabled(true);
            isOpen=true;
        }
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

    private void setMyUploads(){
        int index=0;
        boolean found=false;
        for (int i=0 ; i<discover.size() ;i++){
            if(discover.get(i).getName().equals("My Uploads")){
                index=i;
                found=true;
            }
        }
        if(found){
            favNom.setText(discover.get(index).getName());
            int size = discover.get(index).getTracks() != null ? discover.get(index).getTracks().size() : 0 ;
            favTotal.setText( size + " cançons");
            if (discover.get(index).getThumbnail() != null) {
                Picasso.get().load(discover.get(index).getThumbnail()).into(favCover);
            }else{
                Picasso.get().load("https://community.spotify.com/t5/image/serverpage/image-id/25294i2836BD1C1A31BDF2/image-size/original?v=mpbl-1&px=-1").into(favCover);
            }
            fav = discover.get(index);
            discover.remove(index);
        }else{
            misCanciones.setVisibility(View.INVISIBLE);
            favCover.setVisibility(View.INVISIBLE);
            favNom.setVisibility(View.INVISIBLE);
            favTotal.setVisibility(View.INVISIBLE);
        }

    }
    public void updateSeekBar() {
        mSeekBar.setProgress(mPlayer.getCurrentPosition());

        if(mPlayer.isPlaying()) {
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    updateSeekBar();
                }
            };
            mHandler.postDelayed(mRunnable, 1000);
        }
    }

    private void playAudio() {
        mPlayer.start();
        updateSeekBar();
        btnPlayStop.setImageResource(R.drawable.ic_pause);
        btnPlayStop.setTag(STOP_VIEW);
    }

    private void pauseAudio() {
        mPlayer.pause();
        btnPlayStop.setImageResource(R.drawable.ic_play);
        btnPlayStop.setTag(PLAY_VIEW);
    }


    @Override
    public void onPlaylistRecieved(List<Playlist> playlists) {
        this.discover = (ArrayList) playlists;
        setMyUploads();
        PlaylistAdapter p = new PlaylistAdapter(this, this.discover);
        p.setPlaylistCallback(this);
        playlists_descobrir.setAdapter(p);


    }

    @Override
    public void onAllPlaylistRecieved(List<Playlist> body) {
        this.allPlaylists = (ArrayList) body;
        PlaylistAdapter p2 = new PlaylistAdapter(this, this.allPlaylists);
        p2.setPlaylistCallback(this);
        allPlaylistRecycle.setAdapter(p2);
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



    @Override
    public void onAllNoPlaylists(Throwable throwable) {

    }

    @Override
    public void onAllPlaylistFailure(Throwable throwable) {

    }


}
