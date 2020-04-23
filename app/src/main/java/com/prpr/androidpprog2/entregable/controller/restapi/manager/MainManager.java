package com.prpr.androidpprog2.entregable.controller.restapi.manager;

import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prpr.androidpprog2.entregable.controller.activities.LoginActivity;
import com.prpr.androidpprog2.entregable.model.UserToken;
import com.prpr.androidpprog2.entregable.utils.Constants;
import com.prpr.androidpprog2.entregable.utils.Session;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
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

        /*httpClient.authenticator(new Authenticator() {
            @Override
            public Request authenticate(Route route, Response response) throws IOException {

                Call<UserToken> call = UserTokenManager.getInstance().getNewToken(TokenStoreManager.getInstance().getRefreshToken());

                try{

                    retrofit2.Response<UserToken> tokenResponse = call.execute();

                    if(tokenResponse.code() == 400 || tokenResponse.code() == 401){
                        Intent intentLogin = new Intent(TokenStoreManager.getInstance().getContext(), LoginActivity.class);
                        TokenStoreManager.getInstance().getContext().startActivity(intentLogin);
                        return null;
                    }
                    if(tokenResponse.code() == 200) {
                        UserToken newToken = tokenResponse.body();

                        TokenStoreManager.getInstance().setTokenType(newToken.getTokenType());
                        TokenStoreManager.getInstance().setAccessToken(newToken.getAccessToken());
                        TokenStoreManager.getInstance().setRefreshToken(newToken.getRefreshToken());

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(TokenStoreManager.getInstance().getContext());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("accessToken", newToken.getAccessToken());
                        editor.putString("refreshToken",newToken.getRefreshToken());
                        editor.putString("tokenType", newToken.getTokenType());
                        editor.apply();

                        return response.request().newBuilder()
                                .header("Authorization", newToken.getTokenType() + " " + newToken.getAccessToken())
                                .build();
                    }
                    else {
                        return null;
                    }

                }catch (IOException e){
                    return null;
                }
            }
        });*/

        OkHttpClient client = httpClient.build();

        mainRetrofit = new Retrofit.Builder()
                .baseUrl(Constants.NETWORK.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

    }
}
