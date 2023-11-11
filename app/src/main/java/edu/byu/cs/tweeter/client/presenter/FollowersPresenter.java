package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter extends FollowPresenter {
    public interface View extends FollowPresenter.FollowView {}

    public FollowersPresenter(View view) {
        super(view);
    }

    public void loadMoreItems(User user) {
        this.user = user;
        loadItems();
    }

    @Override
    protected void doLoad(FollowService followService, FollowPresenter.FollowServiceObserver followServiceObserver) {
        followService.loadMoreFollowers(Cache.getInstance().getCurrUserAuthToken(),
                user, PAGE_SIZE, lastItems, followServiceObserver);
    }

    @Override
    protected String getType() {
        return "followers";
    }
}
