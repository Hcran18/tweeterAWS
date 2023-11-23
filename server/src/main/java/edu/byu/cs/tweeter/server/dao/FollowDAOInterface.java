package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;

public interface FollowDAOInterface {
    /**
     * Gets the count of users from the database that the user specified is following. The
     * current implementation uses generated data and doesn't actually access a database.
     *
     * @param follower the User whose count of how many following is desired.
     * @return said count.
     */
    Integer getFolloweeCount(User follower);

    /**
     * Gets the users from the database that the user specified in the request is following. Uses
     * information in the request object to limit the number of followees returned and to return the
     * next set of followees after any that were returned in a previous request. The current
     * implementation returns generated data and doesn't actually access a database.
     *
     * @param followerAlias     the alias of the user whose followees are to be returned
     * @param limit             the number of followees to be returned in one page
     * @param lastFolloweeAlias the alias of the last followee in the previously retrieved page or
     *                          null if there was no previous request.
     * @return the followees.
     */
    Pair<List<User>, Boolean> getFollowees(String followerAlias, int limit, String lastFolloweeAlias);

    Pair<List<User>, Boolean> getFollowers(String targetUserAlias, int limit, String lastFollowerAlias);

}
