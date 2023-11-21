package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.Pair;

public interface FollowDAOInterface {
    Integer getFolloweeCount(User follower);

    Pair<List<User>, Boolean> getFollowees(String followerAlias, int limit, String lastFolloweeAlias);

    Pair<List<User>, Boolean> getFollowers(String targetUserAlias, int limit, String lastFollowerAlias);

    List<User> getDummyFollowees();

}
