package edu.byu.cs.tweeter.model.net.response;

public class GetFollowingCountResponse extends Response {
    private int count;

    public GetFollowingCountResponse(boolean success, int count) {
        super(success);
        this.count = count;
    }

    public GetFollowingCountResponse(String message) {
        super(false, message);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
