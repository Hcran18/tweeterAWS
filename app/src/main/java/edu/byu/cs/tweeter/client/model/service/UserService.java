package edu.byu.cs.tweeter.client.model.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetUserHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.LoginHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.LogoutHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.RegisterHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class UserService extends Service {
    public static final String URL_PATH_LOGIN = "/login";
    public static final String URL_PATH_LOGOUT = "/logout";
    public static final String URL_PATH_REGISTER = "/register";
    public static final String URL_PATH_GET_USER = "/getuser";

    public interface UserObserver extends Service.ServiceObserver {

        void displayGettingProfile(String message);

        void startingNewActivity(User user);

        void logOutCancel();
    }

    public interface LoginObserver {
        void loginSucceeded(User user);
        void loginFailed(String message);
    }

    public interface RegisterObserver {
        void registerSucceeded(User user);
        void registerFailed(String message);
    }

    public void login(String alias, String password, LoginObserver observer) {
        // Send the login request.
        executeTask(new LoginTask(alias, password, new LoginHandler(observer)));
    }

    public void getUser(AuthToken currUserAuthToken, String aliasString, UserObserver observer) {
        executeTask(new GetUserTask(currUserAuthToken,
                aliasString, new GetUserHandler(observer)));

        observer.displayGettingProfile("Getting user's profile...");
    }

    public void register(String firstName, String lastName, String alias, String password, String imageBytesBase64, RegisterObserver observer) {
        // Send register request.
        executeTask(new RegisterTask(firstName, lastName,
                alias, password, imageBytesBase64, new RegisterHandler(observer)));
    }

    public void logout(AuthToken currUserAuthToken, UserObserver observer) {
        executeTask(new LogoutTask(currUserAuthToken, new LogoutHandler(observer)));
    }
}
