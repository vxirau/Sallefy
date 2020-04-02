package com.prpr.androidpprog2.entregable.controller.restapi.callback;

import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.model.User;

import java.util.ArrayList;

public interface SearchCallback extends FailureCallback{
    void onTrackSearchRecived(ArrayList<Track> tracks);
    void onNoTrackSearchRecived();
    void onPlaylistSearchRecived(ArrayList<Playlist> playlists);
    void onNoPlaylistSearchRecived();
    void onUserSearchRecived(ArrayList<User> users);
    void onNoUserSearchRecived();
}
