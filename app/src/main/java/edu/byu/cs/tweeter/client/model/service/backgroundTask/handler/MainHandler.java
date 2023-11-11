package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;

public abstract class MainHandler <O> extends Handler {

    private O observer;

    public MainHandler(O observer) {
        super(Looper.getMainLooper());
        this.observer = observer;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        boolean success = msg.getData().getBoolean(getSuccessKey());
        if (success) {
            handleSuccess(msg);
        } else if (msg.getData().containsKey(getMessageKey())) {
            String message = msg.getData().getString(getMessageKey());
            handleError(message);
        } else if (msg.getData().containsKey(getExceptionKey())) {
            Exception ex = (Exception) msg.getData().getSerializable(getExceptionKey());
            handleException(ex);
        }
    }

    public O getObserver() {
        return observer;
    }

    protected abstract void handleException(Exception ex);
    protected abstract String getExceptionKey();
    protected abstract void handleError(String message);
    protected abstract String getMessageKey();
    protected abstract void handleSuccess(Message msg);
    protected abstract String getSuccessKey();



}
