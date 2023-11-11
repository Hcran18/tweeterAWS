package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;

public class UnfollowHandler extends MainHandler<FollowService.FollowObserver> {

    public UnfollowHandler(FollowService.FollowObserver observer) {
        super(observer);
    }

    @Override
    protected void handleException(Exception ex) {
        getObserver().displayError("Failed to unfollow because of exception: " + ex.getMessage());
        getObserver().followEnable(true);
    }

    @Override
    protected String getExceptionKey() {
        return UnfollowTask.EXCEPTION_KEY;
    }

    @Override
    protected void handleError(String message) {
        getObserver().displayError("Failed to unfollow: " + message);
        getObserver().followEnable(true);
    }

    @Override
    protected String getMessageKey() {
        return UnfollowTask.MESSAGE_KEY;
    }

    @Override
    protected void handleSuccess(Message msg) {
        getObserver().updateFollow(true);
        getObserver().followEnable(true);
    }

    @Override
    protected String getSuccessKey() {
        return UnfollowTask.SUCCESS_KEY;
    }
}
