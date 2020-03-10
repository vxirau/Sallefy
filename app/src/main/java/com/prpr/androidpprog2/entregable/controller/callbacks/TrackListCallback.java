package com.prpr.androidpprog2.entregable.controller.callbacks;

import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;

import java.util.ArrayList;

public interface TrackListCallback {
    void onTrackSelected(Track track);
    void onTrackSelected(int index);
    void onTrackAddSelected(int position, ArrayList<Track> tracks, Playlist playlist);
}
