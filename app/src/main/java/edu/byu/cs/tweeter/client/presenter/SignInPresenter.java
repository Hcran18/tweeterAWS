package edu.byu.cs.tweeter.client.presenter;

import android.os.Message;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class SignInPresenter extends Presenter{
    String alias;
    String password;

    public interface SignInView extends Presenter.MainView {
        void hideInfoMessage();
        void hideErrorMessage();
        void displayErrorMessage(String message);
        void openMainView(User user);
    }

    SignInView view;

    public SignInPresenter(SignInView view) {
        this.view = view;
    }

    protected void performSignIn() {
        if (validate()) {
            view.hideErrorMessage();
            view.displayMessage(getMessage());

            UserService userService = new UserService();
            signIn(userService);
        }
    }

    protected void actionSucceeded(User user) {
        view.hideErrorMessage();
        view.hideInfoMessage();
        view.displayMessage("Hello, " + user.getName());
        view.openMainView(user);
    }

    protected void actionFailed(String message) {
        view.displayErrorMessage(message);
    }

    protected abstract void signIn(UserService userService);

    protected abstract String getMessage();

    protected abstract boolean validate();
}
