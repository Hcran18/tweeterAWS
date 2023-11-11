package edu.byu.cs.tweeter.model.net.response;

public class FollowResponse extends Response {
    public FollowResponse(String message) {
        super(false, message);
    }

    public FollowResponse() {
        super(true);
    }
}
