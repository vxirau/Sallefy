package com.prpr.androidpprog2.entregable.utils;

import android.app.Activity;
import android.content.Context;

import com.prpr.androidpprog2.entregable.model.User;
import com.prpr.androidpprog2.entregable.model.UserRegister;
import com.prpr.androidpprog2.entregable.model.UserToken;

import java.lang.reflect.Field;
import java.util.Map;

public class Session {

    public static Session sSession;
    private static Object mutex = new Object();

    private Context mContext;

    private UserRegister mUserRegister;
    private static User mUser;
    private  UserToken mUserToken;

    public static Session getInstance() {
        Session result = sSession;
        if (result == null) {
            synchronized (mutex) {
                result = sSession;
                if (result == null)
                    sSession = result = new Session();
            }
        }
        return result;
    }

    public static Session getInstance(Context context) {
        Session result = sSession;
        if (result == null) {
            synchronized (mutex) {
                result = sSession;
                if (result == null)
                    sSession = result = new Session();
            }
        }
        return result;
    }

    private Session() {}

    public Session(Context context) {
        this.mContext = context;
        this.mUserRegister = null;
        this.mUserToken = null;
    }

    public void resetValues() {
        mUserRegister = null;
        mUserToken = null;
    }

    public static String changeLogin(String mUser){
        return mUser.replace("[","").replace("]","").replace(".","")
                .replace("$","").replace("#","");
    }

    public UserRegister getUserRegister() {
        return mUserRegister;
    }

    public void setUserRegister(UserRegister userRegister) {
        mUserRegister = userRegister;
    }

    public static User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }

    public  UserToken getUserToken() {
        return mUserToken;
    }

    public void setUserToken(UserToken userToken) {
        this.mUserToken = userToken;
    }


    public static Activity quinaActivityEsta() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);

            Map<Object, Object> activities = (Map<Object, Object>) activitiesField.get(activityThread);
            if (activities == null)
                return null;

            for (Object activityRecord : activities.values()) {
                Class activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    return (Activity) activityField.get(activityRecord);
                }
            }

            return null;
        }
        catch (Exception e)
        {
            return null;
        }
    }

}
