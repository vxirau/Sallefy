package com.prpr.androidpprog2.entregable.controller.restapi.manager;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prpr.androidpprog2.entregable.controller.activities.LoginActivity;
import com.prpr.androidpprog2.entregable.model.UserToken;
import com.prpr.androidpprog2.entregable.utils.Constants;
import com.prpr.androidpprog2.entregable.utils.Session;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainManager {
    protected Retrofit mainRetrofit;

    public static final String TAG = "MainManager";
    protected Context mContext;
    private OkHttpClient client, mCachedOkHttpClient;



    public MainManager(){
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(500, TimeUnit.SECONDS).readTimeout(500, TimeUnit.SECONDS);
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                String bearerToken = Session.getInstance().getUserToken().getIdToken();

                if(bearerToken == null || bearerToken.equals("")){
                    System.out.println("No token");
                }

                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", "Bearer "+bearerToken);

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        client = httpClient.build();

        mainRetrofit = new Retrofit.Builder()
                .baseUrl(Constants.NETWORK.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

}
