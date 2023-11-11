package edu.byu.cs.tweeter.client.presenter;

import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.R;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter extends Presenter {

    public interface View extends Presenter.MainView {

        void setVisability(boolean visible);

        void setIsFollower(boolean isFollower);

        void followEnable(boolean enable);

        void updateFollow(boolean update);

        void followerCount(int count);

        void followingCount(int count);

        void logOutCancel();

        void postSuccess(String message);
    }

    private View view;

    private FollowService followService;

    private UserService userService;

    private StatusService statusService;

    public MainPresenter (View view) {
        this.view = view;
        followService = new FollowService();
        userService = new UserService();
   }

    protected StatusService getStatusService() {
        if (statusService == null) {
            statusService = new StatusService();
        }
        return statusService;
    }

    public void isFollower(User selectedUser) {
        if (selectedUser.compareTo(Cache.getInstance().getCurrUser()) == 0) {
            view.setVisability(false);
        } else {
            view.setVisability(true);

            followService.isFollower(Cache.getInstance().getCurrUserAuthToken(), Cache.getInstance().getCurrUser(), selectedUser, new FollowServiceObserver());
        }
    }

    public void followUnfollow(Button followButton, User selectedUser, android.view.View v) {
        followButton.setEnabled(false);

        if (followButton.getText().toString().equals(v.getContext().getString(R.string.following))) {
            followService.unfollow(Cache.getInstance().getCurrUserAuthToken(), selectedUser, new FollowServiceObserver());
        } else {
            followService.follow(Cache.getInstance().getCurrUserAuthToken(), selectedUser, new FollowServiceObserver());
        }
    }

    public void updateFollowingAndFollowers(User selectedUser) {
        followService.updateFollowingAndFollowers(Cache.getInstance().getCurrUserAuthToken(), selectedUser, new FollowServiceObserver());
    }

    public void logout() {
        view.displayMessage("Logging Out...");

        userService.logout(Cache.getInstance().getCurrUserAuthToken(), new UserServiceObserver());
    }

    public void post(String post) {
        view.displayMessage("Posting Status...");

        Status newStatus = new Status(post, Cache.getInstance().getCurrUser(), System.currentTimeMillis(), parseURLs(post), parseMentions(post));
        getStatusService().post(newStatus, Cache.getInstance().getCurrUserAuthToken(), new StatusServiceObserver());
    }

    public List<String> parseURLs(String post) {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }

        return containedUrls;
    }

    public List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }

    public int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }

    private class FollowServiceObserver implements FollowService.FollowObserver {

        @Override
        public void displayError(String message) {
            view.displayMessage(message);
        }

        @Override
        public void displayException(Exception ex) {
            view.displayMessage("Failed to determine following relationship because of exception: " + ex.getMessage());
        }

        @Override
        public void addMoreFollowees(List<User> followees, boolean hasMorePages) {

        }

        @Override
        public void addMoreFollowers(List<User> followers, boolean hasMorePages) {

        }

        @Override
        public void setIsFollower(boolean isFollower) {
            view.setIsFollower(isFollower);
        }

        @Override
        public void followEnable(boolean enable) {
            view.followEnable(enable);
        }

        @Override
        public void displayMessage(String message) {
            view.displayMessage(message);
        }

        @Override
        public void updateFollow(boolean update) {
            view.updateFollow(update);
        }

        @Override
        public void followerCount(int count) {
            view.followerCount(count);
        }

        @Override
        public void followingCount(int count) {
            view.followingCount(count);
        }
    }

    private class UserServiceObserver implements UserService.UserObserver {

        @Override
        public void displayGettingProfile(String message) {

        }

        @Override
        public void displayError(String message) {
            view.displayMessage(message);
        }

        @Override
        public void displayException(Exception ex) {

        }

        @Override
        public void startingNewActivity(User user) {

        }

        @Override
        public void logOutCancel() {
            view.logOutCancel();
        }
    }

    private class StatusServiceObserver implements StatusService.StatusObserver {

        @Override
        public void addMoreStatuses(List<Status> statuses, boolean hasMorePages) {

        }

        @Override
        public void displayError(String message) {
            view.displayMessage(message);
        }

        @Override
        public void displayException(Exception ex) {
            view.displayMessage(ex.getMessage());
        }

        @Override
        public void postSuccess(String msg) {
            view.postSuccess(msg);
        }
    }
}
