package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> extends Presenter {
    protected static final int PAGE_SIZE = 10;
    protected T lastItems;
    protected boolean hasMorePages;
    protected boolean isLoading;
    protected PagedView<T> view;
    protected UserService userService;

    public PagedPresenter(PagedView<T> view) {
        this.view = view;
        userService = new UserService();
    }

    public boolean hasMorePages() {
        return hasMorePages;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void loadItems() {
        if (!isLoading) {   // This guard is important for avoiding a race condition in the scrolling code.
            isLoading = true;
            view.setLoadingFooter(true);

            load();
        }
    }

    protected abstract void load();

    public interface PagedView<U> extends Presenter.MainView {
        void setLoadingFooter(boolean setOrRemove);
        void addMoreItems(List<U> items);
        void startingNewActivity(User user);
    }

    public void getUser(String givenString) {
        userService.getUser(Cache.getInstance().getCurrUserAuthToken(), givenString, new UserServiceObserver());
    }

    private class UserServiceObserver implements UserService.UserObserver {

        @Override
        public void displayGettingProfile(String message) {
            view.displayMessage(message);
        }

        @Override
        public void displayError(String message) {
            view.displayMessage(message);
        }

        @Override
        public void displayException(Exception ex) {
            view.displayMessage("Failed to get user's profile because of exception: " + ex.getMessage());
        }

        @Override
        public void startingNewActivity(User user) {
            view.startingNewActivity(user);
        }

        @Override
        public void logOutCancel() {

        }
    }
}
