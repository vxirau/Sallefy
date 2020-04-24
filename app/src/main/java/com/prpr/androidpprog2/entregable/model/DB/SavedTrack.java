package com.prpr.androidpprog2.entregable.model.DB;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.prpr.androidpprog2.entregable.model.Track;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;

@Entity
public class SavedTrack {
    @Id(assignable = true) public long id;
    public String trackPath;
    public String coverPath;
    public String track;
    public ToMany<SavedPlaylist> playlist;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTrackPath() {
        return trackPath;
    }

    public void setTrackPath(String trackPath) {
        this.trackPath = trackPath;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Track retrieveTrack() throws IOException, ClassNotFoundException {
        byte [] data = Base64.getDecoder().decode(track);
        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(  data ) );
        Object o  = ois.readObject();
        ois.close();
        return (Track) o;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String saveTrack(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( o );
        oos.close();
        this.track = Base64.getEncoder().encodeToString(baos.toByteArray());
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }


    public ToMany<SavedPlaylist> getPlaylist() {
        return playlist;
    }

    public void setPlaylist(ToMany<SavedPlaylist> playlists) {
        this.playlist = playlists;
    }
}
