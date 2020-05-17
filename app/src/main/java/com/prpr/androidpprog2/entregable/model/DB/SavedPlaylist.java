package com.prpr.androidpprog2.entregable.model.DB;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.model.User;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Base64;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;

@Entity
public class SavedPlaylist {

    @Id(assignable = true) public long id;
    public String coverPath;
    public String playlist;
    @Backlink(to = "playlist")
    public ToMany<SavedTrack> tracks;

    public ToMany<SavedTrack> getTracks() {
        return tracks;
    }

    public void setTracks(ToMany<SavedTrack> tracks) {
        this.tracks = tracks;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getPlaylist() {
        return playlist;
    }

    public void setPlaylist(String playlist) {
        this.playlist = playlist;
    }

    public Playlist retrievePlaylist(){
        Gson gson = new Gson();
        Type type = new TypeToken<Playlist>() {}.getType();
        if(this.playlist!= null ){
            return gson.fromJson(this.playlist, type);
        }else{
            return new Playlist();
        }
    }

    public void savePlaylist(Playlist user){
        Gson gson = new Gson();
        this.playlist = gson.toJson(user);
    }


}
