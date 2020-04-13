package com.prpr.androidpprog2.entregable.controller.restapi.service;

import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Position;
import com.prpr.androidpprog2.entregable.model.Track;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface TrackService {

    @GET("tracks")
    Call<List<Track>> getAllTracks(@Header("Authorization") String token);

    @GET("me/tracks")
     Call<List<Track>> getOwnTracks(@Header("Authorization") String token);

    @GET("users/{login}/tracks?popular=true")
    Call<List<Track>> getUserTracks(@Path("login") String login, @Header("Authorization") String token);

    @POST("tracks")
    Call<ResponseBody> createTrack(@Body Track track, @Header("Authorization") String token);

    @PUT("tracks/{id}/like")
    Call<ResponseBody> likeTrack(@Path("id") int id, @Header("Authorization") String token);

    @GET("users/{login}/tracks?popular=true&size=5")
    Call<List<Track>> getTopTracks(@Path("login") String login, @Header("Authorization") String token);

    @PUT("tracks")
    Call<Track> updateTrack(@Body Track track, @Header("Authorization") String token);

    @PUT("tracks/{id}/play")
    Call<Track> playTrack(@Path("id") int id, @Body Position pos, @Header("Authorization") String token);

    @DELETE("tracks/{id}")
    Call <ResponseBody> removeTrack(@Path("id") int id, @Header("Authorization") String token);

    @GET("tracks/{id}")
    Call<Track> getTrack(@Path("id") int id, @Header("Authorization") String token);

}
