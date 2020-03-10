package com.prpr.androidpprog2.entregable.controller.restapi.service;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import com.prpr.androidpprog2.entregable.model.Track;

public interface TrackService {

    @GET("tracks")
    Call<List<Track>> getAllTracks(@Header("Authorization") String token);

    @GET("me/tracks")
     Call<List<Track>> getOwnTracks(@Header("Authorization") String token);

    @GET("users/{login}/tracks")
    Call<List<Track>> getUserTracks(@Path("login") String login, @Header("Authorization") String token);

    @POST("tracks")
    Call<ResponseBody> createTrack(@Body Track track, @Header("Authorization") String token);

}
