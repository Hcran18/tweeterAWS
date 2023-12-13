package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.DAOModule;
import edu.byu.cs.tweeter.server.service.StatusService;

public class PostStatusHandler implements RequestHandler<PostStatusRequest, PostStatusResponse> {
    @Override
    public PostStatusResponse handleRequest(PostStatusRequest request, Context context) {
        Injector injector = Guice.createInjector(new DAOModule());
        StatusService service = injector.getInstance(StatusService.class);

        //TODO: Send a request with the message to the SQS Post Status Queue
        System.out.println("Serializing the request");
        Gson gson = new Gson();

        String authTokenJson = gson.toJson(request.getAuthToken());
        String statusJson = gson.toJson(request.getStatus());

        System.out.println("Creating the message body");
        String messageBody = "{\"authToken\":" + authTokenJson + ", \"status\":" + statusJson + "}";
        System.out.println(messageBody);
        String queueUrl = "https://sqs.us-west-1.amazonaws.com/265729231418/PostStatusQueue";

        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(messageBody);

        System.out.println("Sending the request to the queue");
        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        sqs.sendMessage(send_msg_request);

        System.out.println("Posting the status to the users story");
        return service.postStatus(request);
    }
}
