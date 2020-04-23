package com.prpr.androidpprog2.entregable.controller.restapi.service;

import com.prpr.androidpprog2.entregable.model.Genre;
import com.prpr.androidpprog2.entregable.model.Track;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface GenreService {

    @GET("genres/{id}")
    Call<Genre> getGenreById(@Path("id") Integer id );

    @GET("genres")
    Call<List<Genre>> getAllGenres();

    @GET("genres/{id}/tracks")
    Call<List<Track>> getTracksByGenre(@Path("id") Integer id );

    @POST("genres")
    Call<Genre> createGenre(@Body Genre g );
}
