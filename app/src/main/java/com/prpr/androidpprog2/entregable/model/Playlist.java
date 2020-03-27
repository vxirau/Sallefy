package com.prpr.androidpprog2.entregable.model;

import android.text.Editable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Playlist implements Serializable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("cover")
    @Expose
    private String cover;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
    @SerializedName("publicAccessible")
    @Expose
    private boolean publicAccessible;
    @SerializedName("owner")
    @Expose
    private User owner;
    @SerializedName("tracks")
    @Expose
    private List<Track> tracks;


    public Playlist(String name) {
        this.name = name;
        //this.thumbnail = "https://community.spotify.com/t5/image/serverpage/image-id/25294i2836BD1C1A31BDF2/image-size/original?v=mpbl-1&px=-1";
        //this.cover = "https://community.spotify.com/t5/image/serverpage/image-id/25294i2836BD1C1A31BDF2/image-size/original?v=mpbl-1&px=-1" ;
        this.cover = null;
        this.thumbnail = null;
        this.publicAccessible = false;
        this.tracks = null;
    }

    public Playlist(String name, User user) {
        this.name = name;
        this.owner = user;
        this.cover = null;
        this.thumbnail = null;
        this.publicAccessible = false;
        this.tracks = null;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getUserLogin() {
        return owner.getLogin();
    }
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public boolean isPublicAccessible() {
        return publicAccessible;
    }

    public void setPublicAccessible(boolean publicAccessible) {
        this.publicAccessible = publicAccessible;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    public void print(){
        System.out.println("ID: " + this.id + "\nName: " + this.name + "\nUser: " + this.getUserLogin());
    }
}
