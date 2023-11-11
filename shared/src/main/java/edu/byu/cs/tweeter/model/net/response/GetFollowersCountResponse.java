package edu.byu.cs.tweeter.model.net.response;

public class GetFollowersCountResponse extends Response {
    private int count;

    public GetFollowersCountResponse(boolean success, int count) {
        super(success);
        this.count = count;
    }

    public GetFollowersCountResponse(String message) {
        super(false, message);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
