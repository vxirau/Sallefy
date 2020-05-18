package com.prpr.androidpprog2.entregable.controller.restapi.callback;

import com.prpr.androidpprog2.entregable.model.User;
import com.prpr.androidpprog2.entregable.model.UserToken;

public interface AccountCallback {
    void onLoginSuccess(UserToken userToken);
    void onLoginFailure(Throwable throwable);
    void onRegisterSuccess() ;
    void onRegisterFailure(Throwable throwable);
    void onUserInfoReceived(User userData);
    void onAccountSaved(User body);
    void onAccountSavedFailure(Throwable throwable);
    void onFailure(Throwable t);
}
