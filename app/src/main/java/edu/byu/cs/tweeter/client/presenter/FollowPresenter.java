package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class FollowPresenter extends PagedPresenter<User>{

    public interface FollowView extends PagedPresenter.PagedView<User> {}

    User user;
    protected FollowService followService;

    public FollowPresenter(PagedView<User> view) {
        super(view);
        followService = new FollowService();
    }

    @Override
    protected void load() {
        doLoad(followService, new FollowServiceObserver());
    }

    protected abstract void doLoad(FollowService followService, FollowServiceObserver followServiceObserver);
    protected abstract String getType();

    protected class FollowServiceObserver implements FollowService.FollowObserver {
        @Override
        public void displayError(String message) {
            isLoading = false;
            view.setLoadingFooter(false);
            view.displayMessage(message);
        }

        @Override
        public void displayException(Exception ex) {
            isLoading = false;
            view.setLoadingFooter(false);
            getType();
            view.displayMessage("Failed to get " + getType() + " because of exception: " + ex.getMessage());
        }

        @Override
        public void addMoreFollowees(List<User> items, boolean hasMorePages) {
            add(items, hasMorePages);
        }

        @Override
        public void addMoreFollowers(List<User> items, boolean hasMorePages) {
            add(items, hasMorePages);
        }

        private void add(List<User> items, boolean hasMorePages) {
            isLoading = false;
            view.setLoadingFooter(false);
            FollowPresenter.this.hasMorePages = hasMorePages;
            lastItems = (items.size() > 0) ? items.get(items.size() - 1) : null;
            view.addMoreItems(items);
        }

        @Override
        public void setIsFollower(boolean isFollower) {

        }

        @Override
        public void followEnable(boolean enable) {

        }

        @Override
        public void displayMessage(String message) {

        }

        @Override
        public void updateFollow(boolean update) {

        }

        @Override
        public void followerCount(int count) {

        }

        @Override
        public void followingCount(int count) {

        }
    }
}
