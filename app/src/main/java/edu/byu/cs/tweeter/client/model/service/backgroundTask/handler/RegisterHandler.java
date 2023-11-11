package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

// RegisterHandler
public class RegisterHandler extends MainHandler<UserService.RegisterObserver> {

    public RegisterHandler(UserService.RegisterObserver observer) {
        super(observer);
    }

    @Override
    protected void handleException(Exception ex) {
        getObserver().registerFailed("Failed to register because of exception: " + ex.getMessage());
    }

    @Override
    protected String getExceptionKey() {
        return RegisterTask.EXCEPTION_KEY;
    }

    @Override
    protected void handleError(String message) {
        getObserver().registerFailed("Failed to register: " + message);
    }

    @Override
    protected String getMessageKey() {
        return RegisterTask.MESSAGE_KEY;
    }

    @Override
    protected void handleSuccess(Message msg) {
        User registeredUser = (User) msg.getData().getSerializable(RegisterTask.USER_KEY);
        AuthToken authToken = (AuthToken) msg.getData().getSerializable(RegisterTask.AUTH_TOKEN_KEY);

        Cache.getInstance().setCurrUser(registeredUser);
        Cache.getInstance().setCurrUserAuthToken(authToken);

        getObserver().registerSucceeded(registeredUser);
    }

    @Override
    protected String getSuccessKey() {
        return RegisterTask.SUCCESS_KEY;
    }
}
