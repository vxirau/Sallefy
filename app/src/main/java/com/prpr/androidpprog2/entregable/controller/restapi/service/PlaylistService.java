package com.prpr.androidpprog2.entregable.controller.restapi.service;

import com.prpr.androidpprog2.entregable.model.Follow;
import com.prpr.androidpprog2.entregable.model.Playlist;

import java.util.List;

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
    Call<List<Playlist>> getAllMyPlaylists(@Header("Authorization") String token);

    @GET("playlists")
    Call<List<Playlist>> getAllPlaylists(@Header("Authorization") String token);

    @GET("playlists?popular=true&size=10")
    Call<List<Playlist>> getTopPlaylists(@Header("Authorization") String token);

    @PUT("playlists")
    Call<Playlist> addTrackPlaylist(@Body Playlist playlist, @Header("Authorization") String token);

    @GET("me/playlists/following")
    Call<List<Playlist>> getFollowedPlaylists(@Header("Authorization") String token);

    @GET("users/{login}/playlists")
    Call<List<Playlist>> showUserPlaylist(@Path("login") String login, @Header("Authorization") String token);

    @GET("playlists/{id}/follow")
    Call<Follow> checkFollow(@Path("id") String id, @Header("Authorization") String token);

    @PUT("playlists/{id}/follow")
    Call<Follow> followPlaylist(@Path("id") String id, @Header("Authorization") String token);

    @GET("playlists/{id}")
    Call<Playlist> getPlaylist(@Header("Authorization") String token, @Path("id") int id);


}
