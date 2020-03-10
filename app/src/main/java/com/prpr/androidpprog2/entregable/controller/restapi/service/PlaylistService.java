package com.prpr.androidpprog2.entregable.controller.restapi.service;

import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.User;
import com.prpr.androidpprog2.entregable.model.UserRegister;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PlaylistService {

    @POST("playlists")
    Call<Playlist> createPlaylist(@Body Playlist playlist, @Header("Authorization") String token);
    @GET("me/playlists")
    Call<List<Playlist>> getAllPlaylists(@Header("Authorization") String token);

    @PUT("playlists")
    Call<Playlist> addTrackPlaylist(@Body Playlist playlist, @Header("Authorization") String token);
}
