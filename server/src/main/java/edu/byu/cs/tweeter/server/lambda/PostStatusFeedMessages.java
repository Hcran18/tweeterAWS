package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.inject.Guice;
import com.google.inject.Injector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
import edu.byu.cs.tweeter.server.dao.DAOModule;
import edu.byu.cs.tweeter.server.service.FollowService;

public class PostStatusFeedMessages implements RequestHandler<SQSEvent, Void> {
    @Override
    public Void handleRequest(SQSEvent input, Context context) {
        Injector injector = Guice.createInjector(new DAOModule());
        FollowService service = injector.getInstance(FollowService.class);

        System.out.println("Deserializing the request");
        Gson gson = new Gson();

        for (SQSEvent.SQSMessage message : input.getRecords()) {
            String getMessageBody = message.getBody();

            // Deserialize the message body into your custom objects
            // Assuming the message body is in the format {"authToken": {...}, "status": {...}}
            JsonObject jsonObject = gson.fromJson(getMessageBody, JsonObject.class);

            // Extract AuthToken and Status from the JSON object
            AuthToken authToken = gson.fromJson(jsonObject.get("authToken"), AuthToken.class);
            Status status = gson.fromJson(jsonObject.get("status"), Status.class);

            FollowerRequest followerRequest = new FollowerRequest(authToken, status.getUser().getAlias(), 25, null);

            try {
                System.out.println("Getting the Followers");
                // Call the getFollowers method from the FollowService
                FollowerResponse followerResponse = service.getFollowers(followerRequest);

                // Handle the fetched followers in batches of 25
                List<User> followers = followerResponse.getFollowers();
                boolean moreAvailable = followerResponse.getHasMorePages();

                System.out.println(followers.get(0).getAlias());

                System.out.println("Creating the batches");
                // Split the followers into groups of 25 and send each group to another SQS queue
                List<List<User>> batches = partitionIntoBatches(followers, 25);

                System.out.println("Sending the batches as requests to the queue");

                System.out.println("Serializing the status");
                String statusJson = gson.toJson(status);

                System.out.println("alias of first user: " + batches.get(0).get(0).getAlias());
                for (List<User> batch : batches) {
                    System.out.println("Serializing a batch");

                    JsonArray jsonArray = new JsonArray();
                    for (User user : batch) {
                        System.out.println("Getting user: " + user.getAlias());
                        String userJson = gson.toJson(user);
                        JsonObject userJsonObject = gson.fromJson(userJson, JsonObject.class); // Convert userJson to JsonObject
                        jsonArray.add(userJsonObject);
                    }

                    System.out.println("serializing list of users");
                    String batchJson = gson.toJson(jsonArray);

                    // Convert the batch of followers to JSON string
                    String messageBody = "{\"status\":" + statusJson + ", \"batch\":" + batchJson + "}";
                    System.out.println(messageBody);
                    String queueUrl = "https://sqs.us-west-1.amazonaws.com/265729231418/UpdateFeedQueue";

                    SendMessageRequest send_msg_request = new SendMessageRequest()
                            .withQueueUrl(queueUrl)
                            .withMessageBody(messageBody);

                    // Create a message for the Update Feed Queue
                    AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
                    sqs.sendMessage(send_msg_request);
                    System.out.println("message sent");
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private List<List<User>> partitionIntoBatches(List<User> followers, int batchSize) {
        List<List<User>> batches = new ArrayList<>();
        for (int i = 0; i < followers.size(); i += batchSize) {
            batches.add(followers.subList(i, Math.min(i + batchSize, followers.size())));
        }
        return batches;
    }
}
