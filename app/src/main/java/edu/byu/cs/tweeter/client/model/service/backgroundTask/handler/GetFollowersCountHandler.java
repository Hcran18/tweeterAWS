package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;

public class GetFollowersCountHandler extends MainHandler<FollowService.FollowObserver> {

    public GetFollowersCountHandler(FollowService.FollowObserver observer) {
        super(observer);
    }

    @Override
    protected void handleException(Exception ex) {
        getObserver().displayError("Failed to get followers count because of exception: " + ex.getMessage());
    }

    @Override
    protected String getExceptionKey() {
        return GetFollowersCountTask.EXCEPTION_KEY;
    }

    @Override
    protected void handleError(String message) {
        getObserver().displayError("Failed to get followers count: " + message);
    }

    @Override
    protected String getMessageKey() {
        return GetFollowersCountTask.MESSAGE_KEY;
    }

    @Override
    protected void handleSuccess(Message msg) {
        int count = msg.getData().getInt(GetFollowersCountTask.COUNT_KEY);
        getObserver().followerCount(count);
    }

    @Override
    protected String getSuccessKey() {
        return GetFollowersCountTask.SUCCESS_KEY;
    }
}
