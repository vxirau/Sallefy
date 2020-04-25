package com.prpr.androidpprog2.entregable.model.DB;

import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;

import java.io.File;
import java.io.IOException;
import java.util.List;

import io.objectbox.query.Query;
import io.objectbox.relation.ToMany;

public class UtilFunctions {


    public static boolean playlistExistsInDatabase(Playlist p){
        List<SavedPlaylist> list = ObjectBox.get().boxFor(SavedPlaylist.class).query().equal(SavedPlaylist_.id, p.getId()).build().find();
        return list.size() ==1;
    }

    public static boolean trackExistsInDatabase(Track t){
        List<SavedTrack> list = ObjectBox.get().boxFor(SavedTrack.class).query().equal(SavedTrack_.id, t.getId()).build().find();
        return list.size() ==1;
    }

    public static int trackInPlaylistTotal(Track t){
        ToMany<SavedPlaylist> list = ObjectBox.get().boxFor(SavedTrack.class).get(t.getId()).playlist;
        return list.size();
    }

    public static void deleteFiles(String path) {
        if(path!=null){
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
        }
    }

}
