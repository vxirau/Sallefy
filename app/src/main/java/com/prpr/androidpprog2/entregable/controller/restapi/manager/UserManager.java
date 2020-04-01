package com.prpr.androidpprog2.entregable.controller.restapi.manager;

import android.content.Context;
import android.util.Log;

import com.prpr.androidpprog2.entregable.controller.activities.MainActivity;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.UserCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.service.UserService;
import com.prpr.androidpprog2.entregable.controller.restapi.service.UserTokenService;
import com.prpr.androidpprog2.entregable.model.User;
import com.prpr.androidpprog2.entregable.model.UserLogin;
import com.prpr.androidpprog2.entregable.model.UserRegister;
import com.prpr.androidpprog2.entregable.model.UserToken;
import com.prpr.androidpprog2.entregable.utils.Constants;
import com.prpr.androidpprog2.entregable.utils.Session;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserManager {

    private static final String TAG = "UserManager";

    private static UserManager sUserManager;
    private Retrofit mRetrofit;
    private Context mContext;

    private UserService mService;
    private UserTokenService mTokenService;

    public UserManager() {

    }


    public static UserManager getInstance(Context context) {
        if (sUserManager == null) {
            sUserManager = new UserManager(context);
        }
        return sUserManager;
    }

    public UserManager(Context cntxt) {
        mContext = cntxt;
        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constants.NETWORK.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mService = mRetrofit.create(UserService.class);
        mTokenService = mRetrofit.create(UserTokenService.class);
    }


    public synchronized void loginAttempt (String username, String password, final UserCallback userCallback) {

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


    public synchronized void updateUsername(User user, final UserCallback userCallback){
        UserToken userToken = Session.getInstance(mContext).getUserToken();

        Call<User> call = mService.updateUsername(user, "Bearer " + userToken.getIdToken());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                int code = response.code();
                if (response.isSuccessful()) {
                    userCallback.onUsernameUpdated(response.body());
                } else {
                    try{
                        userCallback.onFailure(new Throwable(response.errorBody().string()));
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d(TAG, "Error Failure: " + t.getStackTrace());
                userCallback.onFailure(new Throwable("ERROR " + t.getStackTrace()));
            }
        });

    }

    public synchronized void updateEmail(User user, final UserCallback userCallback){

        UserToken userToken = Session.getInstance(mContext).getUserToken();

        Call<User> call = mService.updateEmail(user, "Bearer " + userToken.getIdToken());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                int code = response.code();
                if (response.isSuccessful()) {
                    userCallback.onEmailUpdated(response.body());
                } else {
                    try{
                        userCallback.onFailure(new Throwable(response.errorBody().string()));
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d(TAG, "Error Failure: " + t.getStackTrace());
                userCallback.onFailure(new Throwable("ERROR " + t.getStackTrace()));
            }
        });

    }

    public synchronized void getTopUsers (final UserCallback userCallback) {
        UserToken userToken = Session.getInstance(mContext).getUserToken();
        Call<List<User>> call = mService.getTopUsers( "Bearer " + userToken.getIdToken());
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {

                int code = response.code();
                if (response.isSuccessful()) {
                    userCallback.onTopUsersRecieved(response.body());
                } else {
                    Log.d(TAG, "Error NOT SUCCESSFUL: " + response.toString());
                    userCallback.onFailure(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.d(TAG, "Error: " + t.getMessage());
                userCallback.onFailure(new Throwable("ERROR " + t.getStackTrace()));
            }
        });
    }
    public synchronized void getFollowedUsers(final UserCallback userCallback){
        UserToken userToken = Session.getInstance(mContext).getUserToken();
        Call<List<User>> call = mService.getFollowedUsers("Bearer " + userToken.getIdToken());

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                int code = response.code();
                if (response.isSuccessful()) {
                    userCallback.onFollowedUsersSuccess(response.body());
                } else {
                    Log.d(TAG, "Error NOT SUCCESSFUL: " + response.toString());
                    userCallback.onFollowedUsersFail(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                userCallback.onFailure(t);
            }
        });


    }

    public synchronized void registerAttempt (String email, String username, String password, final UserCallback userCallback) {

        Call<ResponseBody> call = mService.registerUser(new UserRegister(email, username, password));

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

    public synchronized void getUserData (String login, final UserCallback userCallback, UserToken userToken) {
        //UserToken userToken = Session.getInstance(mContext).getUserToken();
        Call<User> call = mService.getUserById(login, "Bearer " + userToken.getIdToken());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                int code = response.code();
                if (response.isSuccessful()) {
                    userCallback.onUserInfoReceived(response.body());
                } else {
                    Log.d(TAG, "Error NOT SUCCESSFUL: " + response.toString());
                    userCallback.onFailure(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d(TAG, "Error: " + t.getMessage());
                userCallback.onFailure(new Throwable("ERROR " + t.getStackTrace()));
            }
        });
    }

    public synchronized void getAllUsers (final UserCallback userCallback) {

        UserToken userToken = Session.getInstance(mContext).getUserToken();
        Call<List<User>> call = mService.getAllUsers("Bearer " + userToken.getIdToken());

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {

                int code = response.code();
                if (response.isSuccessful()) {
                    userCallback.onAllUsersSuccess(response.body());
                } else {
                    userCallback.onAllUsersFail(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                userCallback.onFailure(t);
            }
        });
    }


}
