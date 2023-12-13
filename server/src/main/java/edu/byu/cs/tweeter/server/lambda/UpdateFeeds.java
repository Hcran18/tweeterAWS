package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Guice;
import com.google.inject.Injector;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.DAOModule;
import edu.byu.cs.tweeter.server.service.StatusService;


public class UpdateFeeds implements RequestHandler<SQSEvent, Void> {
    @Override
    public Void handleRequest(SQSEvent input, Context context) {
        //TODO: Use the status service to post the feed
        Injector injector = Guice.createInjector(new DAOModule());
        StatusService service = injector.getInstance(StatusService.class);
        Gson gson = new Gson();

        System.out.println("deserializing the data");
        for (SQSEvent.SQSMessage msg : input.getRecords()) {
            String messageBody = msg.getBody();
            System.out.println("messageBody: " + messageBody);

            System.out.println("extracting status and followers");
            // Deserialize the message body into a map containing status and followers
            JsonObject jsonObject = gson.fromJson(messageBody, JsonObject.class);
            Status status = gson.fromJson(jsonObject.get("status"), Status.class);
            Type userListType = new TypeToken<List<User>>(){}.getType();
            List<User> followers = gson.fromJson(jsonObject.get("batch"), userListType);

            System.out.println("numFollowers: " + followers.size());

            System.out.println("posting the feed for each follower");
            // For each follower in the batch, post to the feeds
            for (User follower : followers) {
                System.out.println(follower.getAlias());
                try {
                    // Post the status to each follower's feed
                    service.postToFeed(follower.getAlias(), status);
                } catch (Exception e) {
                    // Handle exceptions or errors, if any
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
