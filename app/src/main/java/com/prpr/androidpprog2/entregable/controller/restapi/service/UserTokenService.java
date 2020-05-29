package com.prpr.androidpprog2.entregable.controller.restapi.service;

import com.prpr.androidpprog2.entregable.model.UserLogin;
import com.prpr.androidpprog2.entregable.model.UserRegister;
import com.prpr.androidpprog2.entregable.model.UserToken;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserTokenService {

    @POST("authenticate")
    Call<UserToken> loginUser(@Body UserLogin login);

    @POST("register")
    Call<ResponseBody> registerUser(@Body UserRegister user);

}
