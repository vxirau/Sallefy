package com.prpr.androidpprog2.entregable.controller.restapi.manager;

import android.content.Context;
import android.util.Log;

import com.prpr.androidpprog2.entregable.controller.restapi.callback.PlaylistCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.service.PlaylistService;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.UserToken;
import com.prpr.androidpprog2.entregable.utils.Constants;
import com.prpr.androidpprog2.entregable.utils.Session;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlaylistManager {

    private static final String TAG = PlaylistManager.class.getName();
    private Context mContext;
    private PlaylistService mPlaylistService;
    private Retrofit mRetrofit;
    private static PlaylistManager sPlaylistManager;


    public static PlaylistManager getInstance (Context context) {
        if (sPlaylistManager == null) {
            sPlaylistManager = new PlaylistManager(context);
        }

        return sPlaylistManager;
    }

    public PlaylistManager(Context context) {
        mContext = context;

        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constants.NETWORK.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mPlaylistService = mRetrofit.create(PlaylistService.class);
    }

    public synchronized void createPlaylist(Playlist playlist, final PlaylistCallback playlistCallback) {
        UserToken userToken = Session.getInstance(mContext).getUserToken();

        Call<Playlist> call = mPlaylistService.createPlaylist(playlist, "Bearer " + userToken.getIdToken());
        call.enqueue(new Callback<Playlist>() {
            @Override
            public void onResponse(Call<Playlist> call, Response<Playlist> response) {
                int code = response.code();
                if (response.isSuccessful()) {
                    playlistCallback.onPlaylistCreated(response.body());
                } else {
                   try{
                       playlistCallback.onPlaylistFailure(new Throwable(response.errorBody().string()));
                   }catch (IOException e){
                       e.printStackTrace();
                   }
                }
            }

            @Override
            public void onFailure(Call<Playlist> call, Throwable t) {
                Log.d(TAG, "Error Failure: " + t.getStackTrace());
                playlistCallback.onPlaylistFailure(new Throwable("ERROR " + t.getStackTrace()));
            }

        });
    }



    public synchronized void getAllMyPlaylists(final PlaylistCallback playlistCallback) {
        UserToken userToken = Session.getInstance(mContext).getUserToken();
        String usertkn = userToken.getIdToken();

        Call<List<Playlist>> call = mPlaylistService.getAllMyPlaylists("Bearer " + usertkn);
        call.enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(Call<List<Playlist>> call, Response<List<Playlist>> response) {
                int code = response.code();

                if (response.isSuccessful()) {
                    playlistCallback.onPlaylistRecieved(response.body());
                } else {
                    Log.d(TAG, "Error Not Successful: " + code);
                    playlistCallback.onNoPlaylists(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<List<Playlist>> call, Throwable t) {
                Log.d(TAG, "Error Failure: " + t.getStackTrace());
                playlistCallback.onPlaylistFailure(new Throwable("ERROR " + t.getStackTrace()));
            }
        });
    }

    public synchronized void getAllPlaylists(final PlaylistCallback playlistCallback) {
        UserToken userToken = Session.getInstance(mContext).getUserToken();
        String usertkn = userToken.getIdToken();

        Call<List<Playlist>> call = mPlaylistService.getAllPlaylists("Bearer " + usertkn);
        call.enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(Call<List<Playlist>> call, Response<List<Playlist>> response) {
                int code = response.code();

                if (response.isSuccessful()) {
                    playlistCallback.onAllPlaylistRecieved(response.body());
                } else {
                    Log.d(TAG, "Error Not Successful: " + code);
                    playlistCallback.onAllNoPlaylists(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<List<Playlist>> call, Throwable t) {
                Log.d(TAG, "Error Failure: " + t.getStackTrace());
                playlistCallback.onAllPlaylistFailure(new Throwable("ERROR " + t.getStackTrace()));
            }
        });
    }

    public synchronized void getTopPlaylists(final PlaylistCallback playlistCallback) {
        UserToken userToken = Session.getInstance(mContext).getUserToken();
        String usertkn = userToken.getIdToken();

        Call<List<Playlist>> call = mPlaylistService.getTopPlaylists("Bearer " + usertkn);
        call.enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(Call<List<Playlist>> call, Response<List<Playlist>> response) {
                int code = response.code();

                if (response.isSuccessful()) {
                    playlistCallback.onTopRecieved(response.body());
                } else {
                    Log.d(TAG, "Error Not Successful: " + code);
                    playlistCallback.onNoTopPlaylists(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<List<Playlist>> call, Throwable t) {
                Log.d(TAG, "Error Failure: " + t.getStackTrace());
                playlistCallback.onTopPlaylistsFailure(new Throwable("ERROR " + t.getStackTrace()));
            }
        });
    }


    public void updatePlaylist(Playlist playlist, final PlaylistCallback playlistCallback) {
        UserToken userToken = Session.getInstance(mContext).getUserToken();

        Call<Playlist> call = mPlaylistService.addTrackPlaylist(playlist, "Bearer " + userToken.getIdToken());
        call.enqueue(new Callback<Playlist>() {
            @Override
            public void onResponse(Call<Playlist> call, Response<Playlist> response) {
                int code = response.code();
                if (response.isSuccessful()) {
                    playlistCallback.onTrackAdded(response.body());
                } else {
                    try{
                        playlistCallback.onTrackAddFailure(new Throwable(response.errorBody().string()));
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Playlist> call, Throwable t) {
                Log.d(TAG, "Error Failure: " + t.getStackTrace());
                playlistCallback.onTrackAddFailure(new Throwable("ERROR " + t.getStackTrace()));
            }

        });

    }



    public synchronized void getFollowingPlaylists (final PlaylistCallback playlistCallback) {
        UserToken userToken = Session.getInstance(mContext).getUserToken();
        Call<List<Playlist>> call = mPlaylistService.getFollowedPlaylists( "Bearer " + userToken.getIdToken());
        call.enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(Call<List<Playlist>> call, Response<List<Playlist>> response) {

                int code = response.code();
                if (response.isSuccessful()) {
                    playlistCallback.onFollowingRecieved(response.body());
                } else {
                    Log.d(TAG, "Error NOT SUCCESSFUL: " + response.toString());
                    playlistCallback.onNoTopPlaylists(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<List<Playlist>> call, Throwable t) {
                Log.d(TAG, "Error: " + t.getMessage());
                playlistCallback.onNoTopPlaylists(new Throwable("ERROR " + t.getStackTrace()));
            }
        });
    }


}
