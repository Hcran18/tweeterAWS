package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;

public class GetFollowingCountHandler extends MainHandler<FollowService.FollowObserver> {

    public GetFollowingCountHandler(FollowService.FollowObserver observer) {
        super(observer);
    }

    @Override
    protected void handleException(Exception ex) {
        getObserver().displayError("Failed to get following count because of exception: " + ex.getMessage());
    }

    @Override
    protected String getExceptionKey() {
        return GetFollowingCountTask.EXCEPTION_KEY;
    }

    @Override
    protected void handleError(String message) {
        getObserver().displayError("Failed to get following count: " + message);
    }

    @Override
    protected String getMessageKey() {
        return GetFollowingCountTask.MESSAGE_KEY;
    }

    @Override
    protected void handleSuccess(Message msg) {
        int count = msg.getData().getInt(GetFollowingCountTask.COUNT_KEY);
        getObserver().followingCount(count);
    }

    @Override
    protected String getSuccessKey() {
        return GetFollowingCountTask.SUCCESS_KEY;
    }
}
