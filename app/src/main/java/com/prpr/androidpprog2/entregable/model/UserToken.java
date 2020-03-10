package com.prpr.androidpprog2.entregable.model;

import com.google.gson.annotations.SerializedName;

public class UserToken {

    @SerializedName("id_token")
    private String idToken;

    public UserToken(String idToken) {
        this.idToken = idToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    @Override
    public String toString() {
        return "UserTokenService{" +
                "idToken='" + idToken + '\'' +
                '}';
    }
}
