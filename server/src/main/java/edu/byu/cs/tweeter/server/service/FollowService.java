package edu.byu.cs.tweeter.server.service;

import com.google.inject.Inject;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.FollowDAOInterface;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {
    private static final Logger LOGGER = Logger.getLogger(FollowService.class.getName());

    FollowDAOInterface dao;

    @Inject
    public FollowService(FollowDAOInterface dao) {
        this.dao = dao;
    }

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link FollowDAO} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    public FollowingResponse getFollowees(FollowingRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        }
        else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        Pair<List<User>, Boolean> pair = dao.getFollowees(request.getFollowerAlias(), request.getLimit(), request.getLastFolloweeAlias());
        return new FollowingResponse(pair.getFirst(), pair.getSecond());
    }

    public FollowerResponse getFollowers(FollowerRequest request) {
        if (request.getTargetUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a target user");
        } else if (request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an Authtoken");
        } else if (request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        Pair<List<User>, Boolean> pair = dao.getFollowers(request.getTargetUserAlias(), request.getLimit(), request.getLastFollowerAlias());
        return new FollowerResponse(pair.getFirst(), pair.getSecond());
    }

    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        if (request.getFollower() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower");
        }
        else if (request.getFollowee() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee");
        }
        else if (request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an Authtoken");
        }

        Boolean isFollower = dao.isFollower(request.getFollower(), request.getFollowee());
        LOGGER.info(String.valueOf(isFollower));

        return new IsFollowerResponse(isFollower);
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {
        if (request.getFollowee() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee");
        } else if (request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an Authtoken");
        }

        dao.unFollow(request.getAuthToken(), request.getFollowee());

        return new UnfollowResponse();
    }

    public FollowResponse follow(FollowRequest request) {
        if (request.getFollowee() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee");
        } else if (request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an Authtoken");
        }

        dao.follow(request.getAuthToken(), request.getFollowee());

        return new FollowResponse();
    }

    public GetFollowersCountResponse getFollowersCount(GetFollowersCountRequest request) {
        if (request.getTargetUser() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a target user");
        } else if (request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an Authtoken");
        }

        int count = dao.getFollowersCount(request.getTargetUser());

        return new GetFollowersCountResponse(true, count);
    }

    public GetFollowingCountResponse getFollowingCount (GetFollowingCountRequest request) {
        if (request.getTargetUser() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a target user");
        } else if (request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an Authtoken");
        }


        int count = dao.getFolloweeCount(request.getTargetUser());

        return new GetFollowingCountResponse(true, count);
    }

    /**
     * Returns an instance of {@link FollowDAO}. Allows mocking of the FollowDAO class
     * for testing purposes. All usages of FollowDAO should get their FollowDAO
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    FollowDAO getFollowingDAO() {
        return new FollowDAO();
    }
}
