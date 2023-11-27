package edu.byu.cs.tweeter.server.dao.data;

import edu.byu.cs.tweeter.server.dao.UserDAO;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

@DynamoDbBean
public class AuthTokens {
    private String authToken;
    private String alias;
    private long timestamp;

    @DynamoDbPartitionKey
    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = UserDAO.AuthTokenIndex)
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "AuthTokens{" +
                "AuthToken='" + authToken + '\'' +
                ", alias='" + alias + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
