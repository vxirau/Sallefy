package com.prpr.androidpprog2.entregable.controller.restapi.manager;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.prpr.androidpprog2.entregable.controller.activities.PlaylistActivity;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.PlaylistCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.service.PlaylistService;
import com.prpr.androidpprog2.entregable.model.DB.ObjectBox;
import com.prpr.androidpprog2.entregable.model.DB.SavedCache;
import com.prpr.androidpprog2.entregable.model.DB.UtilFunctions;
import com.prpr.androidpprog2.entregable.model.Follow;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.model.UserToken;
import com.prpr.androidpprog2.entregable.utils.Constants;
import com.prpr.androidpprog2.entregable.utils.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlaylistManager extends MainManager{

    private static final String TAG = PlaylistManager.class.getName();
    private PlaylistService mPlaylistService;
    private static PlaylistManager sPlaylistManager;


    public static PlaylistManager getInstance (Context context) {
        if (sPlaylistManager == null) {
            sPlaylistManager = new PlaylistManager(context);
        }
        return sPlaylistManager;
    }

    public PlaylistManager(Context context) {
        mContext = context;
        /*if (isConnected(context)){
            mPlaylistService = mainRetrofit.create(PlaylistService.class);
        }else{
            mPlaylistService = getCachedRetrofit(context).create(PlaylistService.class);
        }*/
        mPlaylistService = mainRetrofit.create(PlaylistService.class);
    }

    public synchronized void createPlaylist(Playlist playlist, final PlaylistCallback playlistCallback) {
          

        Call<Playlist> call = mPlaylistService.createPlaylist(playlist );
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

        Call<List<Playlist>> call = mPlaylistService.getAllMyPlaylists();
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
        Call<List<Playlist>> call = mPlaylistService.getAllPlaylists();
        call.enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(Call<List<Playlist>> call, Response<List<Playlist>> response) {
                int code = response.code();
                if (response.isSuccessful()) {
                    SavedCache c = ObjectBox.get().boxFor(SavedCache.class).get(1);
                    c.saveAllPlaylists((ArrayList<Playlist>) response.body());
                    ObjectBox.get().boxFor(SavedCache.class).put(c);
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
          


        Call<List<Playlist>> call = mPlaylistService.getTopPlaylists(  );
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

    public void updatePlaylist(Playlist playlist, Track trck, final PlaylistCallback playlistCallback) {
        Call<Playlist> call = mPlaylistService.addTrackPlaylist(playlist );
        call.enqueue(new Callback<Playlist>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<Playlist> call, Response<Playlist> response) {
                int code = response.code();
                if (response.isSuccessful()) {
                    try {
                        UtilFunctions.updatePlaylist(response.body(), mContext, trck);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    playlistCallback.onPlaylistToUpdated(response.body());
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


    public void updatePlaylist(Playlist playlist, final PlaylistCallback playlistCallback) {
        Call<Playlist> call = mPlaylistService.addTrackPlaylist(playlist );
        call.enqueue(new Callback<Playlist>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<Playlist> call, Response<Playlist> response) {
                int code = response.code();
                if (response.isSuccessful()) {
                    try {
                        UtilFunctions.updatePlaylist(response.body(), mContext);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    playlistCallback.onPlaylistToUpdated(response.body());
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
          
        Call<List<Playlist>> call = mPlaylistService.getFollowedPlaylists();
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

    public synchronized void showUserPlaylist(String login,final PlaylistCallback playlistCallback) {
          
        Call<List<Playlist>> call = mPlaylistService.showUserPlaylist(login );
        call.enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(Call<List<Playlist>> call, Response<List<Playlist>> response) {

                int code = response.code();
                if (response.isSuccessful()) {
                    playlistCallback.onAllPlaylistRecieved(response.body());
                } else {
                    Log.d(TAG, "Error NOT SUCCESSFUL: " + response.toString());
                    playlistCallback.onAllNoPlaylists(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<List<Playlist>> call, Throwable t) {

            }
        });
    }

    public synchronized void checkFollowing (int id, final PlaylistCallback playlistCallback) {
          
        Call<Follow> call = mPlaylistService.checkFollow(Integer.toString(id));
        call.enqueue(new Callback<Follow>() {
            @Override
            public void onResponse(Call<Follow> call, Response<Follow> response) {

                int code = response.code();
                if (response.isSuccessful()) {
                    playlistCallback.onFollowingChecked(response.body());
                } else {
                    Log.d(TAG, "Error NOT SUCCESSFUL: " + response.toString());
                    playlistCallback.onPlaylistFailure(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<Follow> call, Throwable t) {
                Log.d(TAG, "Error: " + t.getMessage());
                playlistCallback.onPlaylistFailure(new Throwable("ERROR " + t.getStackTrace()));
            }
        });
    }

    public synchronized void followPlaylist(int id, final PlaylistCallback playlistCallback) {
          
        Call<Follow> call = mPlaylistService.followPlaylist(Integer.toString(id));
        call.enqueue(new Callback<Follow>() {
            @Override
            public void onResponse(Call<Follow> call, Response<Follow> response) {

                int code = response.code();
                if (response.isSuccessful()) {
                    playlistCallback.onFollowSuccessfull(response.body());
                } else {
                    Log.d(TAG, "Error NOT SUCCESSFUL: " + response.toString());
                    playlistCallback.onPlaylistFailure(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<Follow> call, Throwable t) {
                Log.d(TAG, "Error: " + t.getMessage());
                playlistCallback.onPlaylistFailure(new Throwable("ERROR " + t.getStackTrace()));
            }
        });
    }

    public synchronized void getPlaylist (int id, final PlaylistCallback playlistCallback) {
          
        Call<Playlist> call = mPlaylistService.getPlaylist(id);
        call.enqueue(new Callback<Playlist>() {
            @Override
            public void onResponse(Call<Playlist> call, Response<Playlist> response) {

                int code = response.code();
                if (response.isSuccessful()) {
                    playlistCallback.onPlaylistRecived(response.body());
                } else {
                    Log.d(TAG, "Error NOT SUCCESSFUL: " + response.toString());
                    playlistCallback.onPlaylistFailure(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<Playlist> call, Throwable t) {
                Log.d(TAG, "Error: " + t.getMessage());
                playlistCallback.onPlaylistFailure(new Throwable("ERROR " + t.getStackTrace()));
            }
        });
    }

    public void deletePlaylist(int id, final PlaylistCallback playlistCallback) {
          
        Call<Playlist> call = mPlaylistService.deletePlaylist(id);
        call.enqueue(new Callback<Playlist>() {
            @Override
            public void onResponse(Call<Playlist> call, Response<Playlist> response) {

                int code = response.code();
                if (response.isSuccessful()) {
                    playlistCallback.onPlaylistDeleted(response.body());
                } else {
                    Log.d(TAG, "Error NOT SUCCESSFUL: " + response.toString());
                    playlistCallback.onPlaylistDeleteFailure(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<Playlist> call, Throwable t) {
                Log.d(TAG, "Error: " + t.getMessage());
                playlistCallback.onPlaylistFailure(new Throwable("ERROR " + t.getStackTrace()));
            }
        });
    }
}

