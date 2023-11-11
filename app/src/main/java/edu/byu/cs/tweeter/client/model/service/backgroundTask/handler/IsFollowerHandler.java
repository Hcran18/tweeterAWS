package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;

public class IsFollowerHandler extends MainHandler<FollowService.FollowObserver> {

    public IsFollowerHandler(FollowService.FollowObserver observer) {
        super(observer);
    }

    @Override
    protected void handleException(Exception ex) {
        getObserver().displayException(ex);
    }

    @Override
    protected String getExceptionKey() {
        return IsFollowerTask.EXCEPTION_KEY;
    }

    @Override
    protected void handleError(String message) {
        getObserver().displayError("Failed to determine following relationship: " + message);
    }

    @Override
    protected String getMessageKey() {
        return IsFollowerTask.MESSAGE_KEY;
    }

    @Override
    protected void handleSuccess(Message msg) {
        boolean isFollower = msg.getData().getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);

        // If logged in user if a follower of the selected user, display the follow button as "following"
        if (isFollower) {
            getObserver().setIsFollower(true);
        } else {
            getObserver().setIsFollower(false);
        }
    }

    @Override
    protected String getSuccessKey() {
        return IsFollowerTask.SUCCESS_KEY;
    }
}
