package com.prpr.androidpprog2.entregable.controller.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import com.prpr.androidpprog2.entregable.controller.fragments.InfoTrackFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.activities.Add2PlaylistActivity;
import com.prpr.androidpprog2.entregable.controller.activities.EditSongActivity;
import com.prpr.androidpprog2.entregable.controller.activities.InfoArtistaActivity;
import com.prpr.androidpprog2.entregable.controller.activities.PlaylistActivity;
import com.prpr.androidpprog2.entregable.controller.dialogs.ErrorDialog;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.PlaylistCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.TrackCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.PlaylistManager;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.TrackManager;
import com.prpr.androidpprog2.entregable.model.DB.UtilFunctions;
import com.prpr.androidpprog2.entregable.model.Follow;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.model.User;
import com.prpr.androidpprog2.entregable.utils.Constants;
import com.prpr.androidpprog2.entregable.utils.Session;
import com.squareup.picasso.Picasso;

import java.util.List;


public class InfoTrackFragment extends BottomSheetDialogFragment implements TrackCallback, PlaylistCallback {

    private ImageView songCover;
    private TextView songName;
    private TextView nomArtista;

    private ImageButton favorites;
    private TextView text_favorites;
    private LinearLayout layoutFav;

    private ImageButton edit;
    private TextView text_edit;
    private LinearLayout layoutedit;

    private ImageButton artist;
    private TextView text_artist;
    private LinearLayout layoutArtist;

    private ImageButton playlist;
    private TextView text_playlist;
    private LinearLayout layoutPlaylist;

    private ImageButton eliminar_icono;
    private TextView eliminar_text;
    private LinearLayout layouteliminar;

    private ImageButton share_icono;
    private TextView share_text;
    private LinearLayout layoutShare;

    private Button cancel;

    private Track trck;
//3.37
    private ErrorDialog er;

    private User user;

    private TrackManager tManager;

    private PlaylistManager pManager;

    private Playlist playl;

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if(getActivity() instanceof  PlaylistActivity){
            ((PlaylistActivity)getActivity()).onResume();
        }
    }

    public InfoTrackFragment(Track track, Playlist p, User u) {
        playl = p;
        trck = track;
        user = u;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_info_track, container, false);
        initViews(view);
        tManager = new TrackManager(getContext());
        pManager = new PlaylistManager(getContext());
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        tManager.getTrack(trck.id, this);
    }

    private void initViews(View view){
        er = new ErrorDialog(getContext());
        songCover = (ImageView) view.findViewById(R.id.SongCover);
        songName = (TextView) view.findViewById(R.id.SongName);
        nomArtista = (TextView) view.findViewById(R.id.ArtistName);

        songName.setText(trck.getName());
        nomArtista.setText(trck.getUserLogin());

        if(trck.getThumbnail()!=null){
            Picasso.get().load(trck.getThumbnail()).into(songCover);
        }else{
            Picasso.get().load(R.drawable.default_cover).into(songCover);
        }

        favorites = (ImageButton) view.findViewById(R.id.favoritos);
        text_favorites = view.findViewById(R.id.text_favoritos);
        layoutFav = view.findViewById(R.id.layoutFavoritos);
        layoutFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tManager.likeTrack(trck.getId(), InfoTrackFragment.this);
            }
        });


        edit= (ImageButton) view.findViewById(R.id.edit);
        text_edit = view.findViewById(R.id.text_edit);
        layoutedit = view.findViewById(R.id.layoutedit);
        layoutedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Session.getInstance(getContext().getApplicationContext()).getUser().getLogin().equals(trck.getUserLogin())){
                    Intent intent = new Intent(getContext().getApplicationContext(), EditSongActivity.class);
                    intent.putExtra("Trck", trck);
                    startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);

                }else{
                    er.showErrorDialog("This track is not yours to edit");
                }
            }
        });



        artist = (ImageButton) view.findViewById(R.id.user);
        text_artist = view.findViewById(R.id.text_user);
        layoutArtist = view.findViewById(R.id.layoutUser);
        layoutArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Session.getInstance(getContext().getApplicationContext()).getUser().getLogin().equals(trck.getUserLogin())) {
                    er.showErrorDialog("You cannot check yourself out!");
                }else{
                    Intent intent = new Intent(getContext().getApplicationContext(), InfoArtistaActivity.class);
                    intent.putExtra("User", trck.getUser());
                    startActivity(intent);
                }
            }
        });


        playlist = (ImageButton) view.findViewById(R.id.playlist);
        text_playlist = view.findViewById(R.id.text_playlist);
        layoutPlaylist = view.findViewById(R.id.layoutPlaylist);
        layoutPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext().getApplicationContext(), Add2PlaylistActivity.class);
                intent.putExtra("Trck", trck);
                startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
            }
        });

        eliminar_icono = (ImageButton) view.findViewById(R.id.eliminar);
        eliminar_text = view.findViewById(R.id.text_eliminar);
        layouteliminar = view.findViewById(R.id.layoutEliminar);
        layouteliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Session.getInstance(getContext().getApplicationContext()).getUser().getLogin().equals(playl.getUserLogin())) {
                    playl.getTracks().remove(trck);
                    pManager.updatePlaylist(playl,trck, InfoTrackFragment.this);
                    Toast.makeText(getContext(), "Eliminada correctament", Toast.LENGTH_SHORT).show();
                }else{
                    er.showErrorDialog("This playlist is not yours to edit");
                }
            }
        });

        if(Session.getInstance(getContext().getApplicationContext()).getUser().getLogin().equals(playl.getUserLogin())) {
            layouteliminar.setVisibility(View.VISIBLE);
            layoutedit.setAlpha((float) 1.0);
        }else{
            layouteliminar.setVisibility(View.GONE);
            layoutedit.setAlpha((float) 0.60);
        }

        if(playl==null){
            layouteliminar.setVisibility(View.GONE);
        }

        share_icono = view.findViewById(R.id.button_share);
        share_text = view.findViewById(R.id.text_share);
        layoutShare = view.findViewById(R.id.layoutShare);
        layoutShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                ShareTrackFragment bottomSheetDialog = new ShareTrackFragment(trck);
                bottomSheetDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme);
                bottomSheetDialog.show(getActivity().getSupportFragmentManager(), "track_info");
            }
        });

        if(UtilFunctions.noInternet(getActivity().getApplicationContext())){
            layouteliminar.setAlpha((float) 0.30);
            layouteliminar.setEnabled(false);
            layoutShare.setAlpha((float) 0.30);
            layoutShare.setEnabled(false);
            layoutPlaylist.setAlpha((float) 0.30);
            layoutPlaylist.setEnabled(false);
            layoutArtist.setAlpha((float) 0.30);
            layoutArtist.setEnabled(false);
            layoutedit.setAlpha((float) 0.30);
            layoutedit.setEnabled(false);
            layoutFav.setAlpha((float) 0.30);
            layoutFav.setEnabled(false);
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
        if(trck.isLiked()){
            Toast.makeText(getContext().getApplicationContext(), "Afegit correctament", Toast.LENGTH_SHORT).show();
            trck.setLiked(false);
        }else{
            Toast.makeText(getContext().getApplicationContext(), "Afegit correctament", Toast.LENGTH_SHORT).show();
            trck.setLiked(true);
        }
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
        if(track.getThumbnail()!=null){
            Picasso.get().load(track.getThumbnail()).into(songCover);
        }else{
            Picasso.get().load(R.drawable.default_cover).into(songCover);
        }
        nomArtista.setText(track.getUserLogin());
        songName.setText(track.getName());
    }

    @Override
    public void onMyTracksFailure(Throwable throwable) {

    }

    @Override
    public void onFailure(Throwable throwable) {

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
        dismiss();
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


