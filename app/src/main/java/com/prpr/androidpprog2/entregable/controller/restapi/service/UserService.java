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

public interface UserService {

    @GET("users/{login}")
    Call<User> getUserById(@Path("login") String login, @Header("Authorization") String token);

    @GET("users")
    Call<List<User>> getAllUsers(@Header("Authorization") String token);

    @GET("users?notFollowing=true&popular=true&size=10")
    Call<List<User>> getTopUsers(@Header("Authorization") String token);

    @GET("me/followings")
    Call<List<User>> getFollowedUsers(@Header("Authorization") String token);

    @POST("register")
    Call<ResponseBody> registerUser(@Body UserRegister user);

    @PUT("users")
    Call<ResponseBody> updateUser(@Body User user, @Header("Authorization") String token);



}

