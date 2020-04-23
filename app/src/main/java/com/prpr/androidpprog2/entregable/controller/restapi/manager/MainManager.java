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

    private Retrofit mRetrofit;
    public static final String TAG = "MainManager";
    protected Context mContext;
    private Retrofit mCachedRetrofit;
    private Cache mCache;
    private OkHttpClient client, mCachedOkHttpClient;
    public static final String HEADER_CACHE_CONTROL = "Cache-Control";
    public static final String HEADER_PRAGMA = "Pragma";


    public MainManager(){
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(500, TimeUnit.SECONDS).readTimeout(500, TimeUnit.SECONDS);
        //httpClient.addInterceptor(provideOfflineCacheInterceptor());
        //httpClient.addNetworkInterceptor(provideCacheInterceptor());
        //httpClient.cache(provideCache());
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



    public Retrofit getCachedRetrofit(Context cont) {
        mContext = cont;
        if (mCachedRetrofit == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                    // Add all interceptors you want (headers, URL, logging)
                    .addInterceptor(provideForcedOfflineCacheInterceptor())
                    .cache(provideCache());

            mCachedOkHttpClient = httpClient.build();

            mCachedRetrofit = new Retrofit.Builder()
                    .baseUrl(Constants.NETWORK.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(new Gson()))
                    .client(mCachedOkHttpClient)
                    .build();
        }

        return mCachedRetrofit;
    }

    private Cache provideCache() {
        if (mCache == null) {
            try {
                mCache = new Cache(new File(mContext.getApplicationContext().getCacheDir(), "http-cache"), 10 * 1024 * 1024); // 10 MB
            } catch (Exception e) {
                Log.e(TAG, "Could not create Cache!");
            }
        }

        return mCache;
    }

    private Interceptor provideCacheInterceptor() {
        return chain -> {
            Response response = chain.proceed(chain.request());

            CacheControl cacheControl;

            if (isConnected()) {
                cacheControl = new CacheControl.Builder().maxAge(0, TimeUnit.SECONDS).build();
            } else {
                cacheControl = new CacheControl.Builder().maxStale(7, TimeUnit.DAYS).build();
            }

            return response.newBuilder()
                    .removeHeader(HEADER_PRAGMA)
                    .removeHeader(HEADER_CACHE_CONTROL)
                    .header(HEADER_CACHE_CONTROL, cacheControl.toString())
                    .build();

        };
    }

    private Interceptor provideOfflineCacheInterceptor() {
        return chain -> {
            Request request = chain.request();
            if (!isConnected()) {
                CacheControl cacheControl = new CacheControl.Builder().maxStale(7, TimeUnit.DAYS) .build();
                request = request.newBuilder().removeHeader(HEADER_PRAGMA).removeHeader(HEADER_CACHE_CONTROL).cacheControl(cacheControl).build();
            }
            return chain.proceed(request);
        };
    }

    private Interceptor provideForcedOfflineCacheInterceptor() {
        return chain -> {
            Request request = chain.request();

            CacheControl cacheControl = new CacheControl.Builder()
                    .maxStale(7, TimeUnit.DAYS)
                    .build();

            request = request.newBuilder()
                    .removeHeader(HEADER_PRAGMA)
                    .removeHeader(HEADER_CACHE_CONTROL)
                    .cacheControl(cacheControl)
                    .build();

            return chain.proceed(request);
        };
    }

    public void clean() {
        if (client != null) {
            // Cancel Pending Request
            client.dispatcher().cancelAll();
        }

        if (mCachedOkHttpClient != null) {
            mCachedOkHttpClient.dispatcher().cancelAll();
        }
        mainRetrofit = null;
        mCachedRetrofit = null;

        if (mCache != null) {
            try {
                mCache.evictAll();
            } catch (IOException e) {
                Log.e(TAG, "Error cleaning http cache");
            }
        }

        mCache = null;
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isConnected(Context c) {
        ConnectivityManager connectivityManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
