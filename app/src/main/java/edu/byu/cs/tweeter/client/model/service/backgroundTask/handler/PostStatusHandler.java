package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PostStatusTask;

public class PostStatusHandler extends MainHandler<StatusService.StatusObserver> {

    public PostStatusHandler(StatusService.StatusObserver observer) {
        super(observer);
    }

    @Override
    protected void handleException(Exception ex) {
        getObserver().displayError("Failed to post status because of exception: " + ex.getMessage());
    }

    @Override
    protected String getExceptionKey() {
        return PostStatusTask.EXCEPTION_KEY;
    }

    @Override
    protected void handleError(String message) {
        getObserver().displayError("Failed to post status: " + message);
    }

    @Override
    protected String getMessageKey() {
        return PostStatusTask.MESSAGE_KEY;
    }

    @Override
    protected void handleSuccess(Message msg) {
        getObserver().postSuccess("Successfully Posted!");
    }

    @Override
    protected String getSuccessKey() {
        return PostStatusTask.SUCCESS_KEY;
    }
}
