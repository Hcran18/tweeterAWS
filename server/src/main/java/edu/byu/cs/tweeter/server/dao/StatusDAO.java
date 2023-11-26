package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;

public class StatusDAO implements StatusDAOInterface {
    @Override
    public void postStatus(AuthToken authToken, Status status) {
        //TODO: Post the status return error
        //TODO: Accesses the AuthToken Table to retrieve the user alias
        //TODO: Uses the User Alias to post to the story table and feed table
        //TODO: The tables need the Alias, the status, and the timestamp
    }

    @Override
    public Pair<List<Status>, Boolean> getStory(User targetUser, Status lastStatus, int limit) {
        //TODO: Access the story table to retrieve needed data
        // response needs a list of statuses and has more pages Boolean

        return getFakeData().getPageOfStatus(lastStatus, limit);
    }

    @Override
    public Pair<List<Status>, Boolean> getFeed(User targetUser, Status lastStatus, int limit) {
        //TODO: Access the feed table to retrieve needed data
        // response needs a list of statuses and has more pages Boolean
        return getFakeData().getPageOfStatus(lastStatus, limit);
    }

    private FakeData getFakeData() {
        return FakeData.getInstance();
    }
}
