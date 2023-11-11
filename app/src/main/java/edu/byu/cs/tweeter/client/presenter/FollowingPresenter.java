package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter extends FollowPresenter {

    public interface View extends FollowPresenter.FollowView {}

    public FollowingPresenter (View view) {
        super(view);
    }


    public void loadMoreItems(User user) {
        this.user = user;
        loadItems();
    }

    @Override
    protected void doLoad(FollowService followService, FollowPresenter.FollowServiceObserver followServiceObserver) {
        followService.loadMoreFollowing(Cache.getInstance().getCurrUserAuthToken(),
                user, PAGE_SIZE, lastItems, followServiceObserver);
    }

    @Override
    protected String getType() {
        return "following";
    }
}
