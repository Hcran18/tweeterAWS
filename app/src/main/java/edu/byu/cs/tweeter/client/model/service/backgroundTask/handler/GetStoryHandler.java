package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.model.domain.Status;

/**
 * Message handler (i.e., observer) for GetStoryTask.
 */
public class GetStoryHandler extends MainHandler<StatusService.StatusObserver> {

    public GetStoryHandler(StatusService.StatusObserver observer) {
        super(observer);
    }

    @Override
    protected void handleException(Exception ex) {
        getObserver().displayException(ex);
    }

    @Override
    protected String getExceptionKey() {
        return GetStoryTask.EXCEPTION_KEY;
    }

    @Override
    protected void handleError(String message) {
        getObserver().displayError(message);
    }

    @Override
    protected String getMessageKey() {
        return GetStoryTask.MESSAGE_KEY;
    }

    @Override
    protected void handleSuccess(Message msg) {
        List<Status> statuses = (List<Status>) msg.getData().getSerializable(GetStoryTask.ITEMS_KEY);
        boolean hasMorePages = msg.getData().getBoolean(GetStoryTask.MORE_PAGES_KEY);
        getObserver().addMoreStatuses(statuses, hasMorePages);
    }

    @Override
    protected String getSuccessKey() {
        return GetStoryTask.SUCCESS_KEY;
    }
}
