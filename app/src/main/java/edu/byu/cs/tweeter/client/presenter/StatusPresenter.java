package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class StatusPresenter extends PagedPresenter<Status>{

    public interface StatusView extends PagedPresenter.PagedView<Status> {}

    protected User user;
    protected StatusService statusService;

    public StatusPresenter(PagedView<Status> view) {
        super(view);
        statusService = new StatusService();
    }

    @Override
    protected void load() {
        doLoad(statusService, new StatusServiceObserver());
    }

    protected abstract void doLoad(StatusService statusService, StatusServiceObserver statusServiceObserver);

    protected abstract String getType();

    protected class StatusServiceObserver implements StatusService.StatusObserver {

        @Override
        public void addMoreStatuses(List<Status> statuses, boolean hasMorePages) {
            isLoading = false;
            view.setLoadingFooter(false);
            StatusPresenter.this.hasMorePages = hasMorePages;
            lastItems = (statuses.size() > 0) ? statuses.get(statuses.size() - 1) : null;
            view.addMoreItems(statuses);
        }

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
            view.displayMessage("Failed to get " + getType() + " because of exception: " + ex.getMessage());
        }

        @Override
        public void postSuccess(String s) {

        }
    }
}
