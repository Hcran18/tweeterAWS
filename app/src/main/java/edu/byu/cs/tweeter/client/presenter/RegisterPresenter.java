package edu.byu.cs.tweeter.client.presenter;

import android.widget.ImageView;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter extends SignInPresenter {

    public interface View extends SignInPresenter.SignInView {}

    public RegisterPresenter(View view) {
        super(view);
    }

    String firstName;
    String lastName;
    ImageView imageToUpload;
    String imageBytesBase64;

    public void register(String firstName, String lastName, String alias, String password, ImageView imageToUpload, String imageBytesBase64) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.alias = alias;
        this.password = password;
        this.imageToUpload = imageToUpload;
        this.imageBytesBase64 = imageBytesBase64;

        performSignIn();
    }

    @Override
    protected void signIn(UserService userService) {
        userService.register(firstName, lastName, alias, password, imageBytesBase64, new UserServiceObserver());
    }

    @Override
    protected String getMessage() {
        return "Registering...";
    }

    @Override
    protected boolean validate() {
        if (firstName.length() == 0) {
            view.displayErrorMessage("First Name cannot be empty.");
            return false;
        }
        if (lastName.length() == 0) {
            view.displayErrorMessage("Last Name cannot be empty.");
            return false;
        }
        if (alias.length() == 0) {
            view.displayErrorMessage("Alias cannot be empty.");
            return false;
        }
        if (alias.charAt(0) != '@') {
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

        if (imageToUpload.getDrawable() == null) {
            view.displayErrorMessage("Profile image must be uploaded.");
            return false;
        }

        return true;
    }
    
    private class UserServiceObserver implements UserService.RegisterObserver {

        @Override
        public void registerSucceeded(User user) {
            actionSucceeded(user);
        }

        @Override
        public void registerFailed(String message) {
            actionFailed(message);
        }
    }
}
