package com.prpr.androidpprog2.entregable.controller.restapi.callback;

import java.util.List;

import com.prpr.androidpprog2.entregable.model.Track;


public interface TrackCallback extends FailureCallback {
    void onTracksReceived(List<Track> tracks);
    void onNoTracks(Throwable throwable);
    void onPersonalTracksReceived(List<Track> tracks);
    void onUserTracksReceived(List<Track> tracks);
    void onCreateTrack();
}
