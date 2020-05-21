package com.prpr.androidpprog2.entregable.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

public class Playlist implements Serializable, Comparable<Playlist> {
    @SerializedName("cover")
    public String cover;
    @SerializedName("description")
    public String description;
    @SerializedName("followed")
    public boolean followed;
    @SerializedName("followers")
    public int followers;
    @SerializedName("id")
    public Integer id;
    @SerializedName("name")
    public String name;
    @SerializedName("owner")
    public User owner;
    @SerializedName("publicAccessible")
    public boolean publicAccessible;
    @SerializedName("thumbnail")
    public String thumbnail;
    @SerializedName("tracks")
    public List<Track> tracks = null;


    public Playlist(String name) {
        this.name = name;
        this.cover = null;
        this.id = null;
        this.thumbnail = null;
        this.publicAccessible = false;
        this.tracks = null;
    }

    public Playlist(String name, User user) {
        this.id = -5;
        this.name = name;
        this.owner = user;
        this.cover = null;
        this.thumbnail = null;
        this.publicAccessible = false;
        this.tracks = null;
    }

    public Playlist(String name, User user, List<Track> tracks, String thumbnail){
        this.id = -5;
        this.name = name;
        this.owner = user;
        this.thumbnail = thumbnail;
        this.tracks = tracks;
    }



    public Playlist() {

    }

    public boolean isFollowed() {
        return followed;
    }

    public void setFollowed(boolean followed) {
        this.followed = followed;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
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



    public static Comparator<Playlist> PlaylistNameAscendentComparator
            = new Comparator<Playlist>() {

        @Override
        public int compare(Playlist firstPlaylist, Playlist secondPlaylist) {
            String firstPlaylistReleased = firstPlaylist.getName().toUpperCase();
            String secondPlaylistReleased = secondPlaylist.getName().toUpperCase();

            if(firstPlaylistReleased != null && secondPlaylistReleased != null){
                return firstPlaylistReleased.compareTo(secondPlaylistReleased);
            }else{
                return 0;
            }
        }



    };
    public static Comparator<Playlist> PlaylistNameDescendentComparator
            = new Comparator<Playlist>() {

        public int compare(Playlist firstPlaylist, Playlist secondPlaylist) {

            String firstTrackReleased = firstPlaylist.getName().toUpperCase();
            String secondTrackReleased = secondPlaylist.getName().toUpperCase();

            if(firstTrackReleased != null && secondTrackReleased != null){
                return secondTrackReleased.compareTo(firstTrackReleased);
            }else{
                return 0;
            }


        }

    };
    @Override
    public int compareTo(Playlist playlist) {
        int compareId = ((Playlist) playlist).getId();

        //ascending order
        return this.id - compareId;


    }

}
