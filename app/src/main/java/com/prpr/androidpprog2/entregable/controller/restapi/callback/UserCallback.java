package com.prpr.androidpprog2.entregable.controller.restapi.callback;

import com.prpr.androidpprog2.entregable.model.Follow;
import com.prpr.androidpprog2.entregable.model.User;
import com.prpr.androidpprog2.entregable.model.UserToken;
import com.prpr.androidpprog2.entregable.model.passwordChangeDto;

import java.util.ArrayList;
import java.util.List;

public interface UserCallback extends FailureCallback {
    void onLoginSuccess(UserToken userToken);
    void onLoginFailure(Throwable throwable);
    void onRegisterSuccess() ;
    void onRegisterFailure(Throwable throwable);
    void onUserInfoReceived(User userData);
    void onUserUpdated(User body);
    void onAccountSaved(User body);
    void onTopUsersRecieved(List<User> body);
    void onUserUpdateFailure(Throwable throwable);
    void onUserSelected(User user);
    void onAllUsersSuccess(List<User> users);
    void onFollowedUsersSuccess(List<User> users);
    void onAllUsersFail(Throwable throwable);
    void onFollowedUsersFail(Throwable throwable);
    void onFollowSuccess(Follow body);
    void onAccountSavedFailure(Throwable throwable);
    void onFollowFailure(Throwable throwable);
    void onCheckSuccess(Follow body);
    void onCheckFailure(Throwable throwable);
    void onTopUsersFailure(Throwable throwable);
    void onFollowedUsersFailure(Throwable t);
    void onFollowersRecieved(ArrayList<User> body);
    void onFollowersFailed(Throwable throwable);
    void onFollowersFailure(Throwable throwable);
    void onPasswordUpdated(passwordChangeDto pd);
    void onPasswordUpdatedFailure(Throwable throwable);
}
