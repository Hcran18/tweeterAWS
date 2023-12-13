package edu.byu.cs.tweeter.server.service;

import com.google.inject.Inject;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.StatusDAOInterface;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;

public class StatusService {
    StatusDAOInterface dao;

    @Inject
    public StatusService(StatusDAOInterface dao) {
     this.dao = dao;
    }

    public PostStatusResponse postStatus(PostStatusRequest request) {
        if (request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an Authtoken");
        } else if (request.getStatus() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a status");
        }

        dao.postStatus(request.getAuthToken(), request.getStatus());

        return new PostStatusResponse();
    }

    public GetStoryResponse getStory (GetStoryRequest request) {
        if (request.getTargetUser() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a target user");
        } else if (request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        } else if (request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an Authtoken");
        }

        Pair<List<Status>, Boolean> pair = dao.getStory(request.getTargetUser(), request.getLastStatus(), request.getLimit());

        return new GetStoryResponse(pair.getFirst(), pair.getSecond());
    }

    public GetFeedResponse getFeed (GetFeedRequest request) {
        if (request.getTargetUser() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a target user");
        } else if (request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        } else if (request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an Authtoken");
        }

        Pair<List<Status>, Boolean> pair = dao.getFeed(request.getTargetUser(), request.getLastStatus(), request.getLimit());

        return new GetFeedResponse(pair.getFirst(), pair.getSecond());
    }

    public void postToFeed(String alias, Status status) {
        if (alias == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a target user");
        } else if (status.getPost() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        dao.postFeed(alias, status);
    }
}
