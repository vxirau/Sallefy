package com.prpr.androidpprog2.entregable.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Search {

    @SerializedName("playlists")
    @Expose
    private ArrayList<Playlist> playlists;
    @SerializedName("tracks")
    @Expose
    private ArrayList<Track> tracks;
    @SerializedName("users")
    @Expose
    private ArrayList<User> users;

    public Search(ArrayList<Playlist> playlists, ArrayList<Track> tracks, ArrayList<User> users) {
        super();
        this.playlists = playlists;
        this.tracks = tracks;
        this.users = users;
    }

    public ArrayList<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
    }

    public ArrayList<Track> getTracks() {
        return tracks;
    }

    public void setTracks(ArrayList<Track> tracks) {
        this.tracks = tracks;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }
}