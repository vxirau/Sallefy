package com.prpr.androidpprog2.entregable.controller.restapi.manager;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.cloudinary.ArchiveParams;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.cloudinary.utils.ObjectUtils;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.TrackCallback;
import com.prpr.androidpprog2.entregable.model.Genre;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.model.User;
import com.prpr.androidpprog2.entregable.utils.CloudinaryConfigs;
import com.prpr.androidpprog2.entregable.utils.Session;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CloudinaryManager extends AppCompatActivity {

    private static CloudinaryManager sManager;
    private Context mContext;
    private String mFileName;
    private Genre mGenre;
    private TrackCallback mCallback;


    public static CloudinaryManager getInstance(Context context, TrackCallback callback) {
        if (sManager == null) {
            sManager = new CloudinaryManager(context, callback);
        }
        return sManager;
    }


    public CloudinaryManager(Context context, TrackCallback callback) {
        mContext = context;
        mCallback = callback;
        MediaManager.init(mContext, CloudinaryConfigs.getConfigurations());
    }

    public synchronized void uploadAudioFile(Uri fileUri, String fileName, Genre genre) {
        mGenre = genre;
        mFileName = fileName;
        Map<String, Object> options = new HashMap<>();
        options.put("public_id", fileName);
        options.put("folder", "sallefy/songs/mobile");
        options.put("resource_type", "video");

        MediaManager.get().upload(fileUri)
                .unsigned(fileName)
                .options(options)
                .callback(new CloudinaryCallback())
                .dispatch();
    }

    public synchronized void uploadCoverFile(Uri imageUri){

        Map<String, Object> options = new HashMap<>();
        options.put("public_id", "cover");
        //falta posar el user
        options.put("folder", "sallefy/covers/victorxirau");

        MediaManager.get().upload(imageUri)
                .options(options)
                .unsigned("New")
                .dispatch();
    }

    public synchronized Map getThumbnails(String username) throws Exception {

        Map m = MediaManager.get().getCloudinary().search().expression(("folder=sallefy/covers/"+username)).execute();
        System.out.println("he entrat a thumbnails");
        return m;
    }

    public synchronized void createFolder(String username) {

        Map<String, Object> options = new HashMap<>();
        options.put("public_id", "upload");
        options.put("folder", "sallefy/covers/" + username);
        options.put("resource_type", "image");

        String m = MediaManager.get().upload(R.drawable.add_green_button)
                .unsigned("upload")
                .options(options)
                .dispatch();

        System.out.println(m);
    }

    private class CloudinaryCallback implements UploadCallback {

        @Override
        public void onStart(String requestId) {
        }
        @Override
        public void onProgress(String requestId, long bytes, long totalBytes) {
            Double progress = (double) bytes/totalBytes;
        }
        @Override
        public void onSuccess(String requestId, Map resultData) {
            Track track = new Track();
            track.setId(null);
            track.setName(mFileName);
            track.setUrl((String) resultData.get("url"));
            ArrayList<Genre> genres = new ArrayList<>();
            genres.add(mGenre);
            track.setGenres(genres);
            TrackManager.getInstance(mContext).createTrack(track, mCallback);

        }
        @Override
        public void onError(String requestId, ErrorInfo error) {
        }
        @Override
        public void onReschedule(String requestId, ErrorInfo error) {
        }
    }
}
