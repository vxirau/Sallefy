package com.prpr.androidpprog2.entregable.controller.restapi.manager;

import android.content.Context;
import android.util.Log;

import com.prpr.androidpprog2.entregable.controller.restapi.callback.GenreCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.service.GenreService;
import com.prpr.androidpprog2.entregable.model.DB.ObjectBox;
import com.prpr.androidpprog2.entregable.model.DB.SavedCache;
import com.prpr.androidpprog2.entregable.model.Genre;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.model.UserToken;
import com.prpr.androidpprog2.entregable.utils.Constants;
import com.prpr.androidpprog2.entregable.utils.Session;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GenreManager extends MainManager{

    private static final String TAG = "genreManager";
    private static GenreManager sGenreManager;
    private Context mContext;

    private GenreService mService;

    public static GenreManager getInstance(Context context) {
        if (sGenreManager == null) {
            sGenreManager = new GenreManager(context);
        }
        return sGenreManager;
    }

    public GenreManager(Context cntxt) {
        mContext = cntxt;
        mService = mainRetrofit.create(GenreService.class);
    }

    public synchronized void getAllGenres(final GenreCallback genreCallback) {
       

        Call<List<Genre>> call = mService.getAllGenres();
        call.enqueue(new Callback<List<Genre>>() {
            @Override
            public void onResponse(Call<List<Genre>> call, Response<List<Genre>> response) {
                int code = response.code();
                ArrayList<Genre> data = (ArrayList<Genre>) response.body();

                if (response.isSuccessful()) {
                    SavedCache c = ObjectBox.get().boxFor(SavedCache.class).get(1);
                    c.saveAllGenres((ArrayList<Genre>) response.body());
                    ObjectBox.get().boxFor(SavedCache.class).put(c);
                    genreCallback.onGenresReceive(data);
                } else {
                    Log.d(TAG, "Error: " + code);
                    genreCallback.onFailure(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<List<Genre>> call, Throwable t) {
                Log.d(TAG, "Error: " + t);
                genreCallback.onAllGenreFailure(new Throwable("ERROR " + t.getMessage() ));
            }
        });
    }

    public synchronized void getTracksByGenre(int genreId, final GenreCallback genreCallback) {
       

        Call<List<Track>> call = mService.getTracksByGenre( genreId);
        call.enqueue(new Callback<List<Track>>() {
            @Override
            public void onResponse(Call<List<Track>> call, Response<List<Track>> response) {
                int code = response.code();
                ArrayList<Track> data = (ArrayList<Track>) response.body();

                if (response.isSuccessful()) {
                    genreCallback.onTracksByGenre(data);

                } else {
                    Log.d(TAG, "Error: " + code);
                    genreCallback.onFailure(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<List<Track>> call, Throwable t) {
                Log.d(TAG, "Error: " + t);
                genreCallback.onFailure(new Throwable("ERROR " + t.getMessage() ));
            }
        });

    }

    public synchronized void createNewGenre(Genre g, final GenreCallback genreCallback) {
       

        Call<Genre> call = mService.createGenre( g);
        call.enqueue(new Callback<Genre>() {
            @Override
            public void onResponse(Call<Genre> call, Response<Genre> response) {
                int code = response.code();
                Genre data = (Genre) response.body();

                if (response.isSuccessful()) {
                    genreCallback.onGenreCreated(data);

                } else {
                    Log.d(TAG, "Error: " + code);
                    genreCallback.onGenreCreateFailure(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<Genre> call, Throwable t) {
                Log.d(TAG, "Error: " + t);
                genreCallback.onFailure(new Throwable("ERROR " + t.getMessage() ));
            }
        });

    }

}
