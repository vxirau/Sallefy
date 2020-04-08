package com.prpr.androidpprog2.entregable.controller.restapi.callback;

import com.prpr.androidpprog2.entregable.model.User;
import com.prpr.androidpprog2.entregable.model.UserToken;

import java.util.List;

public interface UserCallback extends FailureCallback {
    void onLoginSuccess(UserToken userToken);
    void onLoginFailure(Throwable throwable);
    void onRegisterSuccess() ;
    void onRegisterFailure(Throwable throwable);
    void onUserInfoReceived(User userData);
    void onUserUpdated();
    void onTopUsersRecieved(List<User> body);
    void onUserUpdateFailure(Throwable throwable);
    void onUserSelected(User user);
    void onAllUsersSuccess(List<User> users);
    void onFollowedUsersSuccess(List<User> users);
    void onAllUsersFail(Throwable throwable);
    void onFollowedUsersFail(Throwable throwable);

}
