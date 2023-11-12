package edu.byu.cs.tweeter.client.model.service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.FollowHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFollowersCountHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFollowersHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFollowingCountHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFollowingHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.IsFollowerHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.UnfollowHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService extends Service {

    public static final String URL_PATH_GET_FOLLOWING = "/getfollowing";
    public static final String URL_PATH_GET_FOLLOWERS = "/getfollowers";
    public static final String URL_PATH_IS_FOLLOWER = "/isfollower";
    public static final String URL_PATH_UNFOLLOW = "/unfollow";
    public static final String URL_PATH_FOLLOW = "/follow";
    public static final String URL_PATH_GET_FOLLOWERS_COUNT = "/getfollowerscount";
    public static final String URL_PATH_GET_FOLLOWING_COUNT = "/getfollowingcount";

    public interface FollowObserver extends Service.ServiceObserver {

        void addMoreFollowees(List<User> followees, boolean hasMorePages);

        void addMoreFollowers(List<User> followers, boolean hasMorePages);

        void setIsFollower(boolean isFollower);

        void followEnable(boolean enable);

        void displayMessage(String message);

        void updateFollow(boolean update);

        void followerCount(int count);

        void followingCount(int count);
    }

    public void loadMoreFollowing(AuthToken currUserAuthToken, User user, int pageSize, User lastFollowee, FollowObserver observer) {
        executeTask(new GetFollowingTask(currUserAuthToken,
                user, pageSize, lastFollowee, new GetFollowingHandler(observer)));
    }

    //TODO: Create the Lambda for this
    public void loadMoreFollowers(AuthToken currUserAuthToken, User user, int pageSize, User lastFollower, FollowObserver observer) {
        executeTask(new GetFollowersTask(currUserAuthToken,
                user, pageSize, lastFollower, new GetFollowersHandler(observer)));
    }

    public void isFollower(AuthToken currUserAuthToken, User currUser, User selectedUser, FollowObserver observer) {
        executeTask(new IsFollowerTask(currUserAuthToken,
                currUser, selectedUser, new IsFollowerHandler(observer)));
    }

    public void unfollow(AuthToken currUserAuthToken, User selectedUser, FollowObserver observer) {
        executeTask(new UnfollowTask(currUserAuthToken,
                selectedUser, new UnfollowHandler(observer)));

        display(observer, selectedUser, "Removing ");
    }

    public void follow(AuthToken currUserAuthToken, User selectedUser, FollowObserver observer) {
        executeTask(new FollowTask(currUserAuthToken,
                selectedUser, new FollowHandler(observer)));

        display(observer, selectedUser, "Adding ");
    }

    public void updateFollowingAndFollowers(AuthToken currUserAuthToken, User selectedUser, FollowObserver observer) {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Get count of most recently selected user's followers.
        executeFollowingFollowers(new GetFollowersCountTask(currUserAuthToken, selectedUser,
                new GetFollowersCountHandler(observer)), executor);

        // Get count of most recently selected user's followees (who they are following)
        executeFollowingFollowers(new GetFollowingCountTask(currUserAuthToken,
                selectedUser, new GetFollowingCountHandler(observer)), executor);
    }

    private void executeFollowingFollowers(Runnable task, ExecutorService executor) {
        executor.execute(task);
    }

    private void display(FollowObserver observer, User selectedUser, String message) {
        observer.displayMessage(message + selectedUser.getName() + "...");
    }
}
