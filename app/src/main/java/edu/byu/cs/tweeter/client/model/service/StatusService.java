package edu.byu.cs.tweeter.client.model.service;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFeedHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetStoryHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.PostStatusHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusService extends Service {

    public interface StatusObserver extends Service.ServiceObserver {

        void addMoreStatuses(List<Status> statuses, boolean hasMorePages);

        void postSuccess(String s);
    }

    public void loadMoreFeed(AuthToken currUserAuthToken, User user, int pageSize, Status lastStatus, StatusObserver observer) {
        executeTask(new GetFeedTask(currUserAuthToken,
                user, pageSize, lastStatus, new GetFeedHandler(observer)));
    }

    public void loadMoreStory(AuthToken currUserAuthToken, User user, int pageSize, Status lastStatus, StatusObserver observer) {
        executeTask(new GetStoryTask(currUserAuthToken,
                user, pageSize, lastStatus, new GetStoryHandler(observer)));
    }

    public void post(Status newStatus, AuthToken currUserAuthToken, StatusObserver observer) {
        executeTask(new PostStatusTask(currUserAuthToken,
                newStatus, new PostStatusHandler(observer)));
    }
}
