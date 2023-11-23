package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.FakeData;

public class UserDAO implements UserDAOInterface {
    @Override
    public User getUserByUsername(String username) {
        return getDummyUser();
    }

    @Override
    public AuthToken getAuthToken(String username) {
        return getDummyAuthToken();
    }

    @Override
    public User getUserByAlias(String alias) {
        return getFakeData().findUserByAlias(alias);
    }

    @Override
    public void deleteAuthToken(AuthToken authToken) {
        //TODO: Delete AuthToken from table
    }

    @Override
    public void putUser(String username, String password, String firstName, String lastName, String image) {
        //TODO: Put new user into table
    }

    @Override
    public void putAuthToken(String username) {
        //TODO: Put new Authtoken into Table
    }

    /**
     * Returns the dummy user to be returned by the login operation.
     * This is written as a separate method to allow mocking of the dummy user.
     *
     * @return a dummy user.
     */
    User getDummyUser() {
        return getFakeData().getFirstUser();
    }

    /**
     * Returns the dummy auth token to be returned by the login operation.
     * This is written as a separate method to allow mocking of the dummy auth token.
     *
     * @return a dummy auth token.
     */
    AuthToken getDummyAuthToken() {
        return getFakeData().getAuthToken();
    }

    /**
     * Returns the {@link FakeData} object used to generate dummy users and auth tokens.
     * This is written as a separate method to allow mocking of the {@link FakeData}.
     *
     * @return a {@link FakeData} instance.
     */
    FakeData getFakeData() {
        return FakeData.getInstance();
    }
}
