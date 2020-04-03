package com.prpr.androidpprog2.entregable.controller.restapi.manager;

import android.content.Context;
import android.util.Log;

import com.prpr.androidpprog2.entregable.controller.restapi.callback.FailureCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.SearchCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.service.SearchService;
import com.prpr.androidpprog2.entregable.model.Search;
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

public class SearchManager {
    private static final String TAG = "genreManager";
    private static SearchManager sSearchManager;
    private Retrofit mRetrofit;
    private Context mContext;

    private SearchService mService;

    public static SearchManager getInstance(Context context) {
        if (sSearchManager == null) {
            sSearchManager = new SearchManager(context);
        }
        return sSearchManager;
    }

    private SearchManager(Context cntxt) {
        mContext = cntxt;
        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constants.NETWORK.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mService = mRetrofit.create(SearchService.class);
    }

    public synchronized void getSearch (final SearchCallback searchCallback, String text){
        UserToken userToken = Session.getInstance(mContext).getUserToken();

        Call<Search> call = mService.getSearch("Bearer " + userToken.getIdToken(), text);
        call.enqueue(new Callback<Search>() {
            @Override
            public void onResponse(Call<Search> call, Response<Search> response) {
                int code = response.code();

                if (response.isSuccessful()) {
                    Search data = (Search) response.body();
                    assert data != null;
                    //Playlists
                    if (data.getPlaylists() != null && data.getPlaylists().size() != 0) {
                        searchCallback.onPlaylistSearchRecived(data.getPlaylists());
                    } else {
                        searchCallback.onNoPlaylistSearchRecived();
                    }

                    //Tracks
                    if (data.getTracks() != null && data.getTracks().size() != 0) {
                        searchCallback.onTrackSearchRecived(data.getTracks());
                    } else {
                        searchCallback.onNoTrackSearchRecived();
                    }

                    //Users
                    if (data.getUsers() != null && data.getUsers().size() != 0) {
                        searchCallback.onUserSearchRecived(data.getUsers());
                    } else {
                        searchCallback.onNoUserSearchRecived();
                    }

                    if ((data.getPlaylists() == null || data.getPlaylists().size() == 0) && (data.getTracks() == null || data.getTracks().size() != 0) && (data.getUsers() == null || data.getUsers().size() == 0)){
                        searchCallback.onEmptySearch();
                    }

                } else {
                    Log.d(TAG, "Error: " + code);
                    searchCallback.onFailure(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<Search> call, Throwable t) {
                Log.d(TAG, "Error: " + t);
                searchCallback.onFailure(new Throwable("ERROR " + t.getMessage() ));
            }
        });
    }
}

