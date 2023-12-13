package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.Pair;

public interface StatusDAOInterface {
    void postStatus(AuthToken authToken, Status status);

    Pair<List<Status>, Boolean> getStory(User targetUser, Status lastStatus, int limit);

    Pair<List<Status>, Boolean> getFeed(User targetUser, Status lastStatus, int limit);

    void postFeed(String alias, Status status);
}
