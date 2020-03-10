package com.prpr.androidpprog2.entregable.controller.callbacks;

import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;

import java.util.ArrayList;

public interface Add2PlaylistListCallback {
    void onPlaylistAddSelected(int position, ArrayList<Playlist> playlist, Track track);
}
