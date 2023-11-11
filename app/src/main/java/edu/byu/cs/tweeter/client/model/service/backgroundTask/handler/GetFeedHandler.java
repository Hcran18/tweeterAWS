package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.model.domain.Status;

/**
 * Message handler (i.e., observer) for GetFeedTask.
 */
public class GetFeedHandler extends MainHandler<StatusService.StatusObserver> {

    public GetFeedHandler(StatusService.StatusObserver observer) {
        super(observer);
    }

    @Override
    protected void handleException(Exception ex) {
        getObserver().displayException(ex);
    }

    @Override
    protected String getExceptionKey() {
        return GetFeedTask.EXCEPTION_KEY;
    }

    @Override
    protected void handleError(String message) {
        getObserver().displayError("Failed to get feed: " + message);
    }

    @Override
    protected String getMessageKey() {
        return GetFeedTask.MESSAGE_KEY;
    }

    @Override
    protected void handleSuccess(Message msg) {
        List<Status> statuses = (List<Status>) msg.getData().getSerializable(GetFeedTask.ITEMS_KEY);
        boolean hasMorePages = msg.getData().getBoolean(GetFeedTask.MORE_PAGES_KEY);
        getObserver().addMoreStatuses(statuses, hasMorePages);
    }

    @Override
    protected String getSuccessKey() {
        return GetFeedTask.SUCCESS_KEY;
    }
}
