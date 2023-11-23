package edu.byu.cs.tweeter.server.service;

import com.google.inject.Inject;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.UserDAOInterface;
import edu.byu.cs.tweeter.util.FakeData;

public class UserService {
    UserDAOInterface dao;

    @Inject
    public UserService(UserDAOInterface dao) {
        this.dao = dao;
    }

    public LoginResponse login(LoginRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }

        // TODO: Generates dummy data. Replace with a real implementation.
        User user = dao.getUserByUsername(request.getUsername());
        //User user = getDummyUser();
        AuthToken authToken = dao.getAuthToken(request.getUsername());
        //AuthToken authToken = getDummyAuthToken();
        return new LoginResponse(user, authToken);
    }

    public LogoutResponse logout(LogoutRequest request) {
        if (request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Missing an Authtoken");
        }

        //TODO: Should delete the Authtoken and then return the response
        dao.deleteAuthToken(request.getAuthToken());

        return new LogoutResponse();
    }

    public RegisterResponse register(RegisterRequest request) {
        if (request.getUsername() == null) {
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if (request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        } else if (request.getFirstName() == null) {
            throw new RuntimeException("[Bad Request] Missing a first name");
        } else if (request.getLastName() == null) {
            throw new RuntimeException("[Bad Request] Missing a last name");
        } else if (request.getImage() == null) {
            throw new RuntimeException("[Bad Request] Missing an image");
        }

        dao.putUser(request.getUsername(), request.getPassword(), request.getFirstName(), request.getLastName(), request.getImage());
        User user = dao.getUserByUsername(request.getUsername());
        //User user = getDummyUser();
        dao.putAuthToken(request.getUsername());
        AuthToken authToken = dao.getAuthToken(request.getUsername());
        //AuthToken authToken = getDummyAuthToken();
        return new RegisterResponse(user, authToken);
    }

    public GetUserResponse getUser(GetUserRequest request) {
        if (request.getAlias() == null) {
            throw new RuntimeException("[Bad Request] Missing an alias");
        }
        if (request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Missing an authToken");
        }

        User user = dao.getUserByAlias(request.getAlias());
        //User user = getFakeData().findUserByAlias(request.getAlias());
        return new GetUserResponse(user);
    }
}
