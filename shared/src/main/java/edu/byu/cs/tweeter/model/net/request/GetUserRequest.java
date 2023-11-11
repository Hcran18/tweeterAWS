package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class GetUserRequest {
    private AuthToken authToken;
    private String alias;

    private GetUserRequest() {}

    public GetUserRequest(AuthToken authToken, String alias) {
        this.authToken = authToken;
        this.alias = alias;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
