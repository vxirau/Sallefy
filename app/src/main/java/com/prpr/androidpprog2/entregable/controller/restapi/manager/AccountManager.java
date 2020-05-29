package com.prpr.androidpprog2.entregable.controller.restapi.manager;

import android.content.Context;
import android.util.Log;

import com.prpr.androidpprog2.entregable.controller.restapi.callback.AccountCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.UserCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.service.UserService;
import com.prpr.androidpprog2.entregable.controller.restapi.service.UserTokenService;
import com.prpr.androidpprog2.entregable.model.UserLogin;
import com.prpr.androidpprog2.entregable.model.UserRegister;
import com.prpr.androidpprog2.entregable.model.UserToken;
import com.prpr.androidpprog2.entregable.utils.Constants;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AccountManager {
    private static final String TAG = "AccountManager";

    private static AccountManager sUserManager;
    private Context mContext;
    private Retrofit retrofit;
    private UserTokenService mTokenService;


    public static AccountManager getInstance(Context context) {
        if (sUserManager == null) {
            sUserManager = new AccountManager(context);
        }
        return sUserManager;
    }

    public AccountManager(Context cntxt) {
        mContext = cntxt;
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.NETWORK.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mTokenService = retrofit.create(UserTokenService.class);
    }

    public synchronized void registerAttempt (String email, String username, String password, final AccountCallback userCallback) {

        Call<ResponseBody> call = mTokenService.registerUser(new UserRegister(email, username, password));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                int code = response.code();
                if (response.isSuccessful()) {
                    userCallback.onRegisterSuccess();
                } else {
                    userCallback.onRegisterFailure(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                userCallback.onFailure(t);
            }
        });
    }


    public synchronized void loginAttempt (String username, String password, final AccountCallback userCallback) {

        Call<UserToken> call = mTokenService.loginUser(new UserLogin(username, password, true));

        call.enqueue(new Callback<UserToken>() {
            @Override
            public void onResponse(Call<UserToken> call, Response<UserToken> response) {

                int code = response.code();
                UserToken userToken = response.body();

                if (response.isSuccessful()) {
                    userCallback.onLoginSuccess(userToken);
                } else {
                    Log.d(TAG, "Error: " + code);
                    userCallback.onLoginFailure(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<UserToken> call, Throwable t) {
                Log.d(TAG, "Error: " + t.getMessage());
                userCallback.onFailure(t);
            }
        });
    }

}
