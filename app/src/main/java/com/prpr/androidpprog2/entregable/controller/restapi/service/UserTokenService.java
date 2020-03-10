package com.prpr.androidpprog2.entregable.controller.restapi.service;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import com.prpr.androidpprog2.entregable.model.UserLogin;
import com.prpr.androidpprog2.entregable.model.UserToken;

public interface UserTokenService {

    @POST("authenticate")
    Call<UserToken> loginUser(@Body UserLogin login);

}
