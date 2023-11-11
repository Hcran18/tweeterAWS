package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Message;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Message handler (i.e., observer) for GetFollowersTask.
 */
public class GetFollowersHandler extends MainHandler<FollowService.FollowObserver> {

    public GetFollowersHandler(FollowService.FollowObserver observer) {
        super(observer);
    }

    @Override
    protected void handleException(Exception ex) {
        getObserver().displayException(ex);
    }

    @Override
    protected String getExceptionKey() {
        return GetFollowersTask.EXCEPTION_KEY;
    }

    @Override
    protected void handleError(String message) {
        getObserver().displayError("Failed to get followers: " + message);
    }

    @Override
    protected String getMessageKey() {
        return GetFollowersTask.MESSAGE_KEY;
    }

    @Override
    protected void handleSuccess(Message msg) {
        List<User> followers = (List<User>) msg.getData().getSerializable(GetFollowersTask.ITEMS_KEY);
        boolean hasMorePages = msg.getData().getBoolean(GetFollowersTask.MORE_PAGES_KEY);
        getObserver().addMoreFollowers(followers, hasMorePages);
    }

    @Override
    protected String getSuccessKey() {
        return GetFollowersTask.SUCCESS_KEY;
    }
}
