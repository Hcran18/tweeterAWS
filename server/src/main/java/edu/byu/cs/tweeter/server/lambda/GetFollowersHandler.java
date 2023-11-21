package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.inject.Guice;
import com.google.inject.Injector;

import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
import edu.byu.cs.tweeter.server.dao.DAOModule;
import edu.byu.cs.tweeter.server.service.FollowService;

public class GetFollowersHandler implements RequestHandler<FollowerRequest, FollowerResponse> {
    @Override
    public FollowerResponse handleRequest(FollowerRequest request, Context context) {
        Injector injector = Guice.createInjector(new DAOModule());
        FollowService service = injector.getInstance(FollowService.class);

        return service.getFollowers(request);
    }
}
