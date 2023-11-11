package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Message handler (i.e., observer) for LoginTask
 */
public class LoginHandler extends MainHandler<UserService.LoginObserver> {

    public LoginHandler(UserService.LoginObserver observer) {
        super(observer);
    }

    @Override
    protected void handleException(Exception ex) {
        getObserver().loginFailed("Failed to login because of exception: " + ex.getMessage());
    }

    @Override
    protected String getExceptionKey() {
        return LoginTask.EXCEPTION_KEY;
    }

    @Override
    protected void handleError(String message) {
        getObserver().loginFailed("Failed to login: " + message);
    }

    @Override
    protected String getMessageKey() {
        return LoginTask.MESSAGE_KEY;
    }

    @Override
    protected void handleSuccess(Message msg) {
        User loggedInUser = (User) msg.getData().getSerializable(LoginTask.USER_KEY);
        AuthToken authToken = (AuthToken) msg.getData().getSerializable(LoginTask.AUTH_TOKEN_KEY);

        Cache.getInstance().setCurrUser(loggedInUser);
        Cache.getInstance().setCurrUserAuthToken(authToken);

        getObserver().loginSucceeded(loggedInUser);
    }

    @Override
    protected String getSuccessKey() {
        return LoginTask.SUCCESS_KEY;
    }
}
