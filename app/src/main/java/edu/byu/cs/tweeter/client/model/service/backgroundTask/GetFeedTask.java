package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of statuses from a user's feed.
 */
public class GetFeedTask extends PagedStatusTask {
    private static final String LOG_TAG = "GetFeedTask";

    public GetFeedTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                       Handler messageHandler) {
        super(authToken, targetUser, limit, lastStatus, messageHandler);
    }

    @Override
    protected Pair<List<Status>, Boolean> getItems() {
        try {
            GetFeedRequest request = new GetFeedRequest(authToken, targetUser, limit, lastItem);
            GetFeedResponse response = getServerFacade().getFeed(request, StatusService.URL_PATH_GET_FEED);

            if (response.isSuccess()) {
                return new Pair<>(response.getStatuses(), response.getHasMorePages());
            }
            else {
                sendFailedMessage(response.getMessage());
                return null;
            }
        }
        catch (Exception ex) {
            Log.e(LOG_TAG, "Failed to get Feed", ex);
            sendExceptionMessage(ex);
            return null;
        }
    }
}
