package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;

/**
 * Background task that establishes a following relationship between two users.
 */
public class FollowTask extends AuthenticatedTask {
    private static final String LOG_TAG = "FollowTask";
    /**
     * The user that is being followed.
     */
    private final User followee;

    public FollowTask(AuthToken authToken, User followee, Handler messageHandler) {
        super(authToken, messageHandler);
        this.followee = followee;
    }

    @Override
    protected void runTask() {
        //This is going to need to do more. The response will probably pass back more info
        try {
            FollowRequest request = new FollowRequest(authToken, followee);
            FollowResponse response = getServerFacade().follow(request, FollowService.URL_PATH_FOLLOW);

            if (response.isSuccess()) {
                sendSuccessMessage();
            }
            else {
                sendFailedMessage(response.getMessage());
            }
        }
        catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            sendExceptionMessage(ex);
        }
    }
}
