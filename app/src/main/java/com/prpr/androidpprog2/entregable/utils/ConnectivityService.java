package com.prpr.androidpprog2.entregable.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import com.prpr.androidpprog2.entregable.controller.activities.LoginActivity;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.UserCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.AccountManager;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.UserManager;
import com.prpr.androidpprog2.entregable.model.DB.ObjectBox;
import com.prpr.androidpprog2.entregable.model.DB.SavedCache;
import com.prpr.androidpprog2.entregable.model.DB.UtilFunctions;
import com.prpr.androidpprog2.entregable.model.Follow;
import com.prpr.androidpprog2.entregable.model.User;
import com.prpr.androidpprog2.entregable.model.UserToken;
import com.prpr.androidpprog2.entregable.model.passwordChangeDto;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ConnectivityService extends Service implements UserCallback {

    public static String TAG_INTERVAL = "interval";
    private int interval;
    private boolean hadInternet=true;
    private Timer mTimer = null;
    public static final String Broadcast_CONNECTION_REGAINED = "com.prpr.androidpprog2.entregable.CONNECTION_REGAINED";
    public static final String Broadcast_CONNECTION_LOST = "com.prpr.androidpprog2.entregable.CONNECTION_LOST";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    void hasInternetConnection(){
        if(!hadInternet){
            hadInternet = true;
            if (ObjectBox.get().boxFor(SavedCache.class).get(1)==null){
                SavedCache s = new SavedCache();
                s.saveUser(Session.getUser());
                s.oldToken = Session.getInstance().getUserToken().getIdToken();
                ObjectBox.get().boxFor(SavedCache.class).put(s);
            }
            //Intent broadcastIntent = new Intent(Broadcast_CONNECTION_REGAINED);
            //sendBroadcast(broadcastIntent);
            AccountManager.getInstance(getApplicationContext()).loginAttempt(ObjectBox.get().boxFor(SavedCache.class).get(1).username, ObjectBox.get().boxFor(SavedCache.class).get(1).password, ConnectivityService.this);
        }
    }

    void hasNoInternetConnection(){
        if(hadInternet){
            hadInternet=false;
            if (UtilFunctions.hasCache()){
                SavedCache s = ObjectBox.get().boxFor(SavedCache.class).get(1);
                s.saveUser(Session.getUser());
                s.oldToken = Session.getInstance().getUserToken().getIdToken();
                ObjectBox.get().boxFor(SavedCache.class).put(s);
            }else{
                SharedPreferences prefs = getSharedPreferences("RememberMe", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("nickname","");
                editor.putString("password", "");
                editor.putBoolean("stateSwitch", false);
                editor.commit();
                Intent dialogIntent = new Intent(this, LoginActivity.class);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(dialogIntent);
            }
            //Intent broadcastIntent = new Intent(Broadcast_CONNECTION_LOST);
            //sendBroadcast(broadcastIntent);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        interval = intent.getIntExtra(TAG_INTERVAL, 10);

        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new CheckForConnection(), 0, interval * 1000);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onLoginSuccess(UserToken userToken) {
        Session.getInstance(getApplicationContext()).setUserToken(userToken);
        SavedCache c =  ObjectBox.get().boxFor(SavedCache.class).get(1);
        c.setOldToken(userToken.getIdToken());
        ObjectBox.get().boxFor(SavedCache.class).put(c);
        UserManager.getInstance(getApplicationContext()).getUserData(ObjectBox.get().boxFor(SavedCache.class).get(1).username, ConnectivityService.this, userToken);

    }

    @Override
    public void onLoginFailure(Throwable throwable) {

    }

    @Override
    public void onRegisterSuccess() {

    }

    @Override
    public void onRegisterFailure(Throwable throwable) {

    }

    @Override
    public void onUserInfoReceived(User userData) {
        Session.getInstance(getApplicationContext()).setUser(userData);
        SavedCache c =  ObjectBox.get().boxFor(SavedCache.class).get(1);
        c.saveUser(userData);
        ObjectBox.get().boxFor(SavedCache.class).put(c);
    }

    @Override
    public void onUserUpdated(User body) {

    }

    @Override
    public void onAccountSaved(User body) {

    }

    @Override
    public void onTopUsersRecieved(List<User> body) {

    }

    @Override
    public void onUserUpdateFailure(Throwable throwable) {

    }

    @Override
    public void onUserSelected(User user) {

    }

    @Override
    public void onAllUsersSuccess(List<User> users) {

    }

    @Override
    public void onFollowedUsersSuccess(List<User> users) {

    }

    @Override
    public void onAllUsersFail(Throwable throwable) {

    }

    @Override
    public void onFollowedUsersFail(Throwable throwable) {

    }

    @Override
    public void onFollowSuccess(Follow body) {

    }

    @Override
    public void onAccountSavedFailure(Throwable throwable) {

    }

    @Override
    public void onFollowFailure(Throwable throwable) {

    }

    @Override
    public void onCheckSuccess(Follow body) {

    }

    @Override
    public void onCheckFailure(Throwable throwable) {

    }

    @Override
    public void onTopUsersFailure(Throwable throwable) {

    }

    @Override
    public void onFollowedUsersFailure(Throwable t) {

    }

    @Override
    public void onFollowersRecieved(ArrayList<User> body) {

    }

    @Override
    public void onFollowersFailed(Throwable throwable) {

    }

    @Override
    public void onFollowersFailure(Throwable throwable) {

    }

    @Override
    public void onPasswordUpdated(passwordChangeDto pd) {

    }

    @Override
    public void onPasswordUpdatedFailure(Throwable throwable) {

    }

    @Override
    public void onFailure(Throwable throwable) {

    }

    class CheckForConnection extends TimerTask{
        @Override
        public void run() {
            isNetworkAvailable();
        }
    }

    @Override
    public void onDestroy() {
        mTimer.cancel();
        super.onDestroy();
    }

    private boolean isNetworkAvailable(){
        boolean internet = false;
        if (!UtilFunctions.noInternet(getApplication())) {
            try {
                HttpURLConnection urlc = (HttpURLConnection)
                        (new URL("http://sallefy.eu-west-3.elasticbeanstalk.com/api/").openConnection());
                urlc.setRequestProperty("User-Agent", "Android");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                internet =  (urlc.getResponseCode() == 204 && urlc.getContentLength() == 0);
                if(!internet){
                    hasInternetConnection();
                }
                return internet;
            } catch (IOException e) {
                hasNoInternetConnection();
            }
        } else {
           hasNoInternetConnection();
        }
        return false;
    }

}

