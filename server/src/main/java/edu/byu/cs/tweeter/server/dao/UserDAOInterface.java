package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public interface UserDAOInterface {
    User getUserByUsername(String username);

    AuthToken getAuthToken(String username);
    User getUserByAlias(String alias);

    void deleteAuthToken(AuthToken authToken);

    void putUser(String username, String password, String firstName, String lastName, String image);

    void putAuthToken(String username);
}
