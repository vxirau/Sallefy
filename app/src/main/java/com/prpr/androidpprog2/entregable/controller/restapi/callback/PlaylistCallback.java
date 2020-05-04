package com.prpr.androidpprog2.entregable.controller.restapi.callback;


import com.prpr.androidpprog2.entregable.model.Follow;
import com.prpr.androidpprog2.entregable.model.Playlist;

import java.util.List;

public interface PlaylistCallback {
    void onPlaylistCreated(Playlist playlist);
    void onPlaylistFailure(Throwable throwable);
    void onPlaylistRecieved(List<Playlist> playlists);
    void onNoPlaylists(Throwable throwable);
    void onPlaylistSelected(Playlist playlist);
    void onPlaylistToUpdated(Playlist body);
    void onTrackAddFailure(Throwable throwable);
    void onAllPlaylistRecieved(List<Playlist> body);
    void onAllNoPlaylists(Throwable throwable);
    void onAllPlaylistFailure(Throwable throwable);
    void onTopRecieved(List<Playlist> topPlaylists);
    void onNoTopPlaylists(Throwable throwable);
    void onTopPlaylistsFailure(Throwable throwable);
    void onFollowingRecieved(List<Playlist> body);
    void onFollowingChecked(Follow body);
    void onFollowSuccessfull(Follow body);
    void onPlaylistRecived(Playlist playlist);
    void onPlaylistDeleted(Playlist body);
    void onPlaylistDeleteFailure(Throwable throwable);
    void onAllMyPlaylistFailure(Throwable throwable);
    void onFollowingPlaylistsFailure(Throwable throwable);
}
