package com.prpr.androidpprog2.entregable.model.DB;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.prpr.androidpprog2.entregable.controller.activities.InfoPlaylistFragment;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;

import java.io.File;
import java.io.IOException;
import java.util.List;

import io.objectbox.query.Query;
import io.objectbox.relation.ToMany;

public class UtilFunctions {

    public static void deletePlaylist(Playlist playlist){
        PRDownloader.cancelAll();
        UtilFunctions.deleteFiles(ObjectBox.get().boxFor(SavedPlaylist.class).get(playlist.getId()).coverPath);
        ObjectBox.get().boxFor(SavedPlaylist.class).remove(playlist.getId());

        for(int i=0; i<playlist.getTracks().size() ;i++){
            if(UtilFunctions.trackInPlaylistTotal(playlist.getTracks().get(i))==0){
                UtilFunctions.deleteFiles(ObjectBox.get().boxFor(SavedTrack.class).get(playlist.getTracks().get(i).getId()).coverPath);
                UtilFunctions.deleteFiles(ObjectBox.get().boxFor(SavedTrack.class).get(playlist.getTracks().get(i).getId()).trackPath);
                ObjectBox.get().boxFor(SavedTrack.class).remove(playlist.getTracks().get(i).getId());}
        }
    }


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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void updatePlaylist(Playlist playlist, Context c, boolean borrar) throws IOException {
        if(playlistExistsInDatabase(playlist)){
            if(borrar){

            }else{
                for (int i=0; i<playlist.getTracks().size() ;i++) {
                    if (!trackExistsInDatabase(playlist.getTracks().get(i))) {
                        SavedPlaylist play = ObjectBox.get().boxFor(SavedPlaylist.class).get(playlist.getId());
                        File path = c.getApplicationContext().getFilesDir();
                        String a = path.getAbsolutePath();

                        SavedTrack t = new SavedTrack();
                        t.setId(playlist.getTracks().get(i).getId());
                        t.setTrackPath(path.toString() + "/Sallefy/tracks/" + playlist.getTracks().get(i).getName() + "--" + playlist.getTracks().get(i).getUserLogin());
                        t.setTrack(t.saveTrack(playlist.getTracks().get(i)));


                        int downloadId = PRDownloader.download(playlist.getTracks().get(i).getUrl(), path.toString() + "/Sallefy/tracks/", playlist.getTracks().get(i).getName() + "--" + playlist.getTracks().get(i).getUserLogin())
                                .build()
                                .start(new OnDownloadListener() {
                                    @Override
                                    public void onDownloadComplete() {
                                        ObjectBox.get().boxFor(SavedTrack.class).put(t);
                                    }

                                    @Override
                                    public void onError(Error error) {
                                        System.out.println("Error en descarrega");
                                        System.out.println(error.getServerErrorMessage());
                                    }
                                });
                        if (playlist.getTracks().get(i).getThumbnail() != null) {
                            int downloadIdCover = PRDownloader.download(playlist.getTracks().get(i).getThumbnail(), path.toString() + "/Sallefy/covers/tracks/", playlist.getTracks().get(i).getName() + "--" + playlist.getTracks().get(i).getUserLogin() + ".jpeg")
                                    .build()
                                    .start(new OnDownloadListener() {
                                        @Override
                                        public void onDownloadComplete() {
                                        }

                                        @Override
                                        public void onError(Error error) {
                                            System.out.println("Error en descarrega");
                                            System.out.println(error.getServerErrorMessage());
                                        }
                                    });
                            t.setCoverPath(path.toString() + "/Sallefy/covers/tracks/" + playlist.getTracks().get(i).getName() + "--" + playlist.getTracks().get(i).getUserLogin() + ".jpeg");
                        } else {
                            t.setCoverPath(null);
                        }
                        ObjectBox.get().boxFor(SavedTrack.class).attach(t);
                        t.playlist.add(play);
                        ObjectBox.get().boxFor(SavedPlaylist.class).attach(play);
                        play.tracks.add(t);
                    }
                }
            }

        }
    }

}
