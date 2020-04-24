package com.prpr.androidpprog2.entregable.model.DB;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Playlist retrievePlaylist() throws IOException, ClassNotFoundException {
        byte [] data = Base64.getDecoder().decode(playlist);
        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(  data ) );
        Object o  = ois.readObject();
        ois.close();
        return (Playlist) o;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String savePlaylist(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( o );
        oos.close();
        this.playlist = Base64.getEncoder().encodeToString(baos.toByteArray());
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }


}
