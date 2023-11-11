package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;

public class FollowHandler extends MainHandler<FollowService.FollowObserver> {

    public FollowHandler(FollowService.FollowObserver observer) {
        super(observer);
    }

    @Override
    protected void handleException(Exception ex) {
        getObserver().displayError("Failed to follow because of exception: " + ex.getMessage());
        getObserver().followEnable(true);
    }

    @Override
    protected String getExceptionKey() {
        return FollowTask.EXCEPTION_KEY;
    }

    @Override
    protected void handleError(String message) {
        getObserver().displayError("Failed to follow: " + message);
        getObserver().followEnable(true);
    }

    @Override
    protected String getMessageKey() {
        return FollowTask.MESSAGE_KEY;
    }

    @Override
    protected void handleSuccess(Message msg) {
        getObserver().updateFollow(false);
        getObserver().followEnable(true);
    }

    @Override
    protected String getSuccessKey() {
        return FollowTask.SUCCESS_KEY;
    }
}
