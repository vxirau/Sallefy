package com.prpr.androidpprog2.entregable.model.DB;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public static boolean hasCache(){
        List<SavedCache> list = ObjectBox.get().boxFor(SavedCache.class).query().equal(SavedCache_.id, 1).build().find();
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
    public static void updatePlaylist(Playlist playlist, Context c) throws IOException {
        if(playlistExistsInDatabase(playlist)){
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void updatePlaylist(Playlist playlist, Context c, Track track) throws IOException {

        if(playlistExistsInDatabase(playlist)){
            SavedTrack t = ObjectBox.get().boxFor(SavedTrack.class).get(track.getId());
            t.playlist.removeById(playlist.getId());
            ObjectBox.get().boxFor(SavedTrack.class).put(t);

            SavedPlaylist p = ObjectBox.get().boxFor(SavedPlaylist.class).get(playlist.getId());
            p.tracks.removeById(track.getId());
            ObjectBox.get().boxFor(SavedPlaylist.class).put(p);

            if(trackInPlaylistTotal(track)==0){
                UtilFunctions.deleteFiles(ObjectBox.get().boxFor(SavedTrack.class).get(track.getId()).coverPath);
                UtilFunctions.deleteFiles(ObjectBox.get().boxFor(SavedTrack.class).get(track.getId()).trackPath);
                ObjectBox.get().boxFor(SavedTrack.class).remove(track.getId());
            }
        }
    }

    public static void checkForPlaylistUpdate(Playlist playlst) {
        SavedPlaylist p = ObjectBox.get().boxFor(SavedPlaylist.class).get(playlst.getId());
        Playlist saved = p.retrievePlaylist();

        Collections.sort(saved.getTracks(), Track.TrackNameAscendentComparator);
        Collections.sort(playlst.getTracks(), Track.TrackNameAscendentComparator);

        if (!saved.getTracks().equals(playlst.getTracks())) {
            ArrayList<Track> removedFromPlaylist = (ArrayList<Track>) saved.getTracks();
            removedFromPlaylist.removeAll(playlst.getTracks());
            ArrayList<Track> added = (ArrayList<Track>) playlst.getTracks();
            added.removeAll(saved.getTracks());
            for(Track t : removedFromPlaylist){
                saved.getTracks().remove(t);
            }
            for(Track t : added){
                saved.getTracks().add(t);
            }
            p.savePlaylist(saved);
            ObjectBox.get().boxFor(SavedPlaylist.class).put(p);
        }
    }

    private static ArrayList<Track> findRemovedTracks(ArrayList<Track> saved, ArrayList<Track> noves) {

        return new ArrayList<>();
    }

    private static ArrayList<Track> findAddedTracks(ArrayList<Track> saved, ArrayList<Track> noves) {

        return new ArrayList<>();
    }

    public static boolean noInternet(Context c){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return !(activeNetworkInfo != null && activeNetworkInfo.isConnected());
    }


    public static boolean needsSallefyUsers() {
        SavedCache c = ObjectBox.get().boxFor(SavedCache.class).get(1);
        if(c.getSallefyDate() == null || c.getSallefyDate().equals("") || c.getSallefyPlaylists() == null || c.getSallefyPlaylists().equals("") || c.retrieveSallefyPlaylists() == null){
            return true;
        }else{
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = null;
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            Date currentDate = null;
            try {
                date = format.parse(c.getSallefyDate());
                currentDate = format.parse(dateFormat.format(cal.getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long secs = (currentDate.getTime() - date.getTime()) / 1000;
            int hours = (int) (secs / 3600);
            if(hours>=24){
                return true;
            }else{
                return false;
            }
        }
    }
}
