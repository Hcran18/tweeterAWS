package edu.byu.cs.tweeter.model.net.response;

public class PostStatusResponse extends Response {
    public PostStatusResponse (String message) {
        super(false, message);
    }

    public PostStatusResponse() {
        super(true);
    }
}
