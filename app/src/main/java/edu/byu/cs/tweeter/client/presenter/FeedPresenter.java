package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter extends StatusPresenter {

    public interface View extends StatusPresenter.StatusView {}

    public FeedPresenter (View view){
        super(view);
    }

    @Override
    protected void doLoad(StatusService statusService, StatusPresenter.StatusServiceObserver statusServiceObserver) {
        statusService.loadMoreFeed(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastItems, statusServiceObserver);
    }

    @Override
    protected String getType() {
        return "feed";
    }

    public void loadMoreFeed(User user) {
        this.user = user;
        loadItems();
    }
}
