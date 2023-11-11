package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Message handler (i.e., observer) for GetUserTask.
 */
public class GetUserHandler extends MainHandler<UserService.UserObserver> {

    public GetUserHandler(UserService.UserObserver observer) {
        super(observer);
    }

    @Override
    protected void handleException(Exception ex) {
        getObserver().displayException(ex);
    }

    @Override
    protected String getExceptionKey() {
        return GetUserTask.EXCEPTION_KEY;
    }

    @Override
    protected void handleError(String message) {
        getObserver().displayError("Failed to get user's profile: " + message);
    }

    @Override
    protected String getMessageKey() {
        return GetUserTask.MESSAGE_KEY;
    }

    @Override
    protected void handleSuccess(Message msg) {
        User user = (User) msg.getData().getSerializable(GetUserTask.USER_KEY);
        getObserver().startingNewActivity(user);
    }

    @Override
    protected String getSuccessKey() {
        return GetUserTask.SUCCESS_KEY;
    }
}
