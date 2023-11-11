package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginPresenter extends SignInPresenter {

    public interface View extends SignInPresenter.SignInView {}

    public LoginPresenter(View view) {
        super(view);
    }

    public void login(String alias, String password) {
        this.alias = alias;
        this.password = password;

        performSignIn();
    }

    @Override
    protected void signIn(UserService userService) {
        userService.login(alias, password, new LoginPresenter.UserServiceObserver());
    }

    @Override
    protected String getMessage() {
        return "Logging In...";
    }

    @Override
    protected boolean validate() {
        if (alias.length() > 0 && alias.charAt(0) != '@') {
            view.displayErrorMessage("Alias must begin with @.");
            return false;
        }
        if (alias.length() < 2) {
            view.displayErrorMessage("Alias must contain 1 or more characters after the @.");
            return false;
        }
        if (password.length() == 0) {
            view.displayErrorMessage("Password cannot be empty.");
            return false;
        }

        return true;
    }

    private class UserServiceObserver implements UserService.LoginObserver {

        @Override
        public void loginSucceeded(User user) {
            actionSucceeded(user);
        }

        @Override
        public void loginFailed(String message) {
            actionFailed(message);
        }
    }
}
