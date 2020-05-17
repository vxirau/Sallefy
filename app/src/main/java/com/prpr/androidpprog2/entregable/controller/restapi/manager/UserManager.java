package com.prpr.androidpprog2.entregable.controller.restapi.manager;

import android.content.Context;
import android.util.Log;

import com.prpr.androidpprog2.entregable.controller.activities.InfoArtistaActivity;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.PlaylistCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.UserCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.service.UserService;
import com.prpr.androidpprog2.entregable.controller.restapi.service.UserTokenService;
import com.prpr.androidpprog2.entregable.model.DB.ObjectBox;
import com.prpr.androidpprog2.entregable.model.DB.SavedCache;
import com.prpr.androidpprog2.entregable.model.Follow;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.User;
import com.prpr.androidpprog2.entregable.model.UserLogin;
import com.prpr.androidpprog2.entregable.model.UserRegister;
import com.prpr.androidpprog2.entregable.model.UserToken;
import com.prpr.androidpprog2.entregable.utils.Constants;
import com.prpr.androidpprog2.entregable.utils.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserManager extends MainManager{

    private static final String TAG = "UserManager";

    private static UserManager sUserManager;
    private UserService mService;


    public static UserManager getInstance(Context context) {
        if (sUserManager == null) {
            sUserManager = new UserManager(context);
        }
        return sUserManager;
    }

    public UserManager(Context cntxt) {
        mContext = cntxt;
        mService = mainRetrofit.create(UserService.class);
    }

    public void updateUser(User user, final UserCallback userCallback) {
          

        Call<User> call = mService.updateUser(user);
       call.enqueue(new Callback<User>() {
           @Override
           public void onResponse(Call<User> call, Response<User> response) {
               int code = response.code();
               if (response.isSuccessful()) {
                   userCallback.onUserUpdated(response.body());
                   System.out.println("is successful");
               } else {
                   try{
                       userCallback.onUserUpdateFailure(new Throwable(response.errorBody().string()));
                       System.out.println("it is not successful");
                   }catch (IOException e){
                       e.printStackTrace();
                   }
               }
           }

           @Override
           public void onFailure(Call<User> call, Throwable t) {
               Log.d(TAG, "Error Failure: " + t.getStackTrace());
               userCallback.onUserUpdateFailure(new Throwable("ERROR " + t.getStackTrace()));

           }
       });

    }
    public void saveAccount(User user, final UserCallback userCallback) {
          

        Call<ResponseBody> call = mService.saveAccount(user);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                if (response.isSuccessful()) {
                    userCallback.onAccountSaved(user);
                } else {
                    try{
                        userCallback.onAccountSavedFailure(new Throwable(response.errorBody().string()));
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "Error Failure: " + t.getStackTrace());
                userCallback.onAccountSavedFailure(new Throwable("ERROR " + t.getStackTrace()));
            }
        });

    }

    public synchronized void getTopUsers (final UserCallback userCallback) {
          
        Call<List<User>> call = mService.getTopUsers(   );
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {

                int code = response.code();
                if (response.isSuccessful()) {
                    SavedCache c = ObjectBox.get().boxFor(SavedCache.class).get(1);
                    c.saveTopUsers((ArrayList<User>) response.body());
                    ObjectBox.get().boxFor(SavedCache.class).put(c);
                    userCallback.onTopUsersRecieved(response.body());
                } else {
                    Log.d(TAG, "Error NOT SUCCESSFUL: " + response.toString());
                    userCallback.onFailure(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.d(TAG, "Error: " + t.getMessage());
                userCallback.onTopUsersFailure(new Throwable("ERROR " + t.getStackTrace()));
            }
        });
    }
    public synchronized void getFollowedUsers(final UserCallback userCallback){
          
        Call<List<User>> call = mService.getFollowedUsers(  );

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                int code = response.code();
                if (response.isSuccessful()) {
                    SavedCache c = ObjectBox.get().boxFor(SavedCache.class).get(1);
                    c.saveFollowedUsers((ArrayList<User>) response.body());
                    ObjectBox.get().boxFor(SavedCache.class).put(c);
                    userCallback.onFollowedUsersSuccess(response.body());
                } else {
                    Log.d(TAG, "Error NOT SUCCESSFUL: " + response.toString());
                    userCallback.onFollowedUsersFail(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                userCallback.onFollowedUsersFailure(t);
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
        //  
        Call<User> call = mService.getUserById(login);
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
    public synchronized void getUser (String login, final UserCallback userCallback) {
          
        Call<User> call = mService.getUserById(login);
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

          
        Call<List<User>> call = mService.getAllUsers(  );

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

    public synchronized void startStopFollowing (String login, final UserCallback userCallback){
          
        Call<Follow> call = mService.startStopFollowing(login);
        call.enqueue(new Callback<Follow>() {
            @Override
            public void onResponse(Call<Follow> call, Response<Follow> response) {
                int code = response.code();
                if (response.isSuccessful()) {
                    userCallback.onFollowSuccess(response.body());
                } else {
                    Log.d(TAG, "Error NOT SUCCESSFUL: " + response.toString());
                    userCallback.onFollowFailure(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<Follow> call, Throwable t) {
                Log.d(TAG, "Error: " + t.getMessage());
                userCallback.onFailure(new Throwable("ERROR " + t.getStackTrace()));
            }
        });
    }

    public synchronized void checkFollow(String login, final UserCallback userCallback){
          
        Call<Follow> call = mService.checkFollow(login);
        call.enqueue(new Callback<Follow>() {
            @Override
            public void onResponse(Call<Follow> call, Response<Follow> response) {
                int code = response.code();
                if (response.isSuccessful()) {
                    userCallback.onCheckSuccess(response.body());
                } else {
                    Log.d(TAG, "Error NOT SUCCESSFUL: " + response.toString());
                    userCallback.onCheckFailure(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<Follow> call, Throwable t) {
                Log.d(TAG, "Error: " + t.getMessage());
                userCallback.onFailure(new Throwable("ERROR " + t.getStackTrace()));
            }
        });
    }


    public synchronized void getFollowers(String login, final UserCallback userCallback) {
        Call<List<User>> call = mService.getFollowers(login);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                int code = response.code();
                if (response.isSuccessful()) {
                    userCallback.onFollowersRecieved((ArrayList<User>) response.body());
                } else {
                    Log.d(TAG, "Error NOT SUCCESSFUL: " + response.toString());
                    userCallback.onFollowersFailed(new Throwable("ERROR " + code + ", " + response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.d(TAG, "Error: " + t.getMessage());
                userCallback.onFollowersFailure(new Throwable("ERROR " + t.getStackTrace()));
            }
        });
    }
}
