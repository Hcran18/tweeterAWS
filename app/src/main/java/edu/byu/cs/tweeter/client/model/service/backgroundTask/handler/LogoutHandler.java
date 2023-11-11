package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask;

public class LogoutHandler extends MainHandler<UserService.UserObserver> {

    public LogoutHandler(UserService.UserObserver observer) {
        super(observer);
    }

    @Override
    protected void handleException(Exception ex) {
        getObserver().displayError("Failed to logout because of exception: " + ex.getMessage());
    }

    @Override
    protected String getExceptionKey() {
        return LogoutTask.EXCEPTION_KEY;
    }

    @Override
    protected void handleError(String message) {
        getObserver().displayError("Failed to logout: " + message);
    }

    @Override
    protected String getMessageKey() {
        return LogoutTask.MESSAGE_KEY;
    }

    @Override
    protected void handleSuccess(Message msg) {
        getObserver().logOutCancel();
    }

    @Override
    protected String getSuccessKey() {
        return LogoutTask.SUCCESS_KEY;
    }
}
