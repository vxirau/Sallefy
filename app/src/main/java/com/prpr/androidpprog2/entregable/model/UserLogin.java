package com.prpr.androidpprog2.entregable.model;

import com.google.gson.annotations.SerializedName;

public class UserLogin {

    @SerializedName("username")
    private String userName;
    @SerializedName("password")
    private String password;
    @SerializedName("rememberMe")
    private boolean rememberMe;

    public UserLogin(String userName, String password, boolean rememberMe) {
        this.userName = userName;
        this.password = password;
        this.rememberMe = rememberMe;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }
}
