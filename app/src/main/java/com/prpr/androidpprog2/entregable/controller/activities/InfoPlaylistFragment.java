package com.prpr.androidpprog2.entregable.controller.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.dialogs.ErrorDialog;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.utils.Constants;
import com.prpr.androidpprog2.entregable.utils.Session;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

public class InfoPlaylistFragment extends BottomSheetDialogFragment {

    private Playlist playlist;
    private ImageView playlistCover;
    private TextView playlistName;
    private TextView playlistArtist;
    private LinearLayout layoutArtist;
    private LinearLayout layoutedit;
    private LinearLayout layoutdelete;


    public InfoPlaylistFragment(Playlist p) {
        playlist = p;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_playlist_info, container, false);

        playlistCover = view.findViewById(R.id.playlist_img);
        if (playlist.getThumbnail() != null) {
            Picasso.get().load(playlist.getThumbnail()).into(playlistCover);
        }else{
            Picasso.get().load("https://community.spotify.com/t5/image/serverpage/image-id/25294i2836BD1C1A31BDF2/image-size/original?v=mpbl-1&px=-1").into(playlistCover);
        }
        playlistName = view.findViewById(R.id.playlistName);
        playlistName.setText(playlist.getName());
        playlistArtist = view.findViewById(R.id.ArtistName);
        playlistArtist.setText(playlist.getUserLogin());

        layoutdelete = view.findViewById(R.id.layoutEliminar);
        layoutdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((PlaylistActivity)getActivity()).onDelete();
            }
        });

        layoutedit = view.findViewById(R.id.layoutedit);
        layoutedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Session.getInstance(((PlaylistActivity)getActivity()).getApplicationContext()).getUser().getLogin().equals(playlist.getUserLogin())) {
                    dismiss();
                    ((PlaylistActivity)getActivity()).showUIEdit();
                }else{
                    ErrorDialog.getInstance(((PlaylistActivity)getActivity())).showErrorDialog("This playlist is not yours to edit");
                }
            }
        });

        layoutArtist = view.findViewById(R.id.layoutUser);
        layoutArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Session.getInstance(((PlaylistActivity)getActivity()).getApplicationContext()).getUser().getLogin().equals(playlist.getUserLogin())) {
                    ErrorDialog.getInstance(((PlaylistActivity)getActivity())).showErrorDialog("You cannot check yourself out!");
                }else{
                    Intent intent = new Intent(((PlaylistActivity)getActivity()), InfoArtistaActivity.class);
                    intent.putExtra("User", playlist.getOwner());
                    startActivity(intent);
                }
            }
        });


        return view;
    }
}
