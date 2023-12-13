package edu.byu.cs.tweeter.server.dao;

import org.checkerframework.checker.units.qual.K;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.data.AuthTokens;
import edu.byu.cs.tweeter.server.dao.data.DataPage;
import edu.byu.cs.tweeter.server.dao.data.Follows;
import edu.byu.cs.tweeter.server.dao.data.Users;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

/**
 * A DAO for accessing 'following' data from the database.
 */
public class FollowDAO implements FollowDAOInterface {
    private static final Logger LOGGER = Logger.getLogger(FollowDAO.class.getName());
    private static final String UserTableName = "users";
    private static final String AuthTableName = "authToken";
    private static final String FollowTableName = "follows";
    private static final String FollowerAttr = "follower_handle";
    private static final String FolloweeAttr = "followee_handle";
    public static final String FollowIndexName = "follows_index";
    private static DynamoDbClient dynamoDbClient;

    private static DynamoDbClient getClient() {

        if (dynamoDbClient == null) {
            dynamoDbClient = DynamoDbClient.builder()
                    .region(Region.US_WEST_1)
                    .build();
        }

        return dynamoDbClient;
    }

    private static DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(getClient())
            .build();

    /**
     * Gets the count of users from the database that the user specified is following. The
     * current implementation uses generated data and doesn't actually access a database.
     *
     * @param targetUser the User whose count of how many following is desired.
     * @return said count.
     */
    public Integer getFolloweeCount(User targetUser) {
        //TODO: uses the dummy data.  Replace with a real implementation.
        //TODO: Accesses the users table
        DynamoDbTable<Users> table = enhancedClient.table(UserTableName, TableSchema.fromBean(Users.class));
        Key key = Key.builder()
                .partitionValue(targetUser.getAlias())
                .build();

        Users users = table.getItem(key);

        return users.getNumFollowing();
    }

    public Integer getFollowersCount(User targetUser) {
        DynamoDbTable<Users> table = enhancedClient.table(UserTableName, TableSchema.fromBean(Users.class));
        Key key = Key.builder()
                .partitionValue(targetUser.getAlias())
                .build();

        Users users = table.getItem(key);

        return users.getNumFollowers();
    }

    /**
     * Gets the users from the database that the user specified in the request is following. Uses
     * information in the request object to limit the number of followees returned and to return the
     * next set of followees after any that were returned in a previous request. The current
     * implementation returns generated data and doesn't actually access a database.
     *
     * @param followerAlias the alias of the user whose followees are to be returned
     * @param limit the number of followees to be returned in one page
     * @param lastFolloweeAlias the alias of the last followee in the previously retrieved page or
     *                          null if there was no previous request.
     * @return the followees.
     */
    public Pair<List<User>, Boolean> getFollowees(String followerAlias, int limit, String lastFolloweeAlias) {
        //TODO: Generates dummy data. Replace with a real implementation.
        //TODO: Access the follows table
        DynamoDbTable<Follows> table = enhancedClient.table(FollowTableName, TableSchema.fromBean(Follows.class));
        Key key = Key.builder()
                .partitionValue(followerAlias)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(limit);

        if(isNonEmptyString(lastFolloweeAlias)) {
            // Build up the Exclusive Start Key (telling DynamoDB where you left off reading items)
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FollowerAttr, AttributeValue.builder().s(followerAlias).build());
            startKey.put(FolloweeAttr, AttributeValue.builder().s(lastFolloweeAlias).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder
                .scanIndexForward(true)
                .build();

        DataPage<User> result = new DataPage<>();

        PageIterable<Follows> pages = table.query(request);
        pages.stream()
                .limit(1)
                .forEach((Page<Follows> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);

                    for (Follows follows: page.items()) {
                        User user = getUser(follows.getFollowee_handle());

                        result.getValues().add(user);
                    }
                });

        return new Pair<>(result.getValues(), result.isHasMorePages());
    }

    public Pair<List<User>, Boolean> getFollowers(String targetUserAlias, int limit, String lastFollowerAlias) {
        DynamoDbIndex<Follows> index = enhancedClient.table(FollowTableName, TableSchema.fromBean(Follows.class)).index(FollowIndexName);
        Key key = Key.builder()
                .partitionValue(targetUserAlias)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(limit);

        if(isNonEmptyString(lastFollowerAlias)) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FolloweeAttr, AttributeValue.builder().s(targetUserAlias).build());
            startKey.put(FollowerAttr, AttributeValue.builder().s(lastFollowerAlias).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder
                .scanIndexForward(true)
                .build();

        DataPage<User> result = new DataPage<>();

        SdkIterable<Page<Follows>> sdkIterable = index.query(request);
        PageIterable<Follows> pages = PageIterable.create(sdkIterable);
        pages.stream()
                .limit(1)
                .forEach((Page<Follows> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    for (Follows follows: page.items()) {
                        User user = getUser(follows.getFollower_handle());

                        result.getValues().add(user);
                    }
                });
        
        return new Pair<>(result.getValues(), result.isHasMorePages());
    }

    @Override
    public void follow(AuthToken authToken, User userToFollow) {
        DynamoDbTable<AuthTokens> authTable = enhancedClient.table(AuthTableName, TableSchema.fromBean(AuthTokens.class));
        Key authKey = Key.builder()
                .partitionValue(authToken.getToken())
                .build();

        AuthTokens receivedAuthToken = authTable.getItem(authKey);

        DynamoDbTable<Users> usersTable = enhancedClient.table(UserTableName, TableSchema.fromBean(Users.class));
        Key userKey = Key.builder()
                .partitionValue(receivedAuthToken.getAlias())
                .build();

        Users users = usersTable.getItem(userKey);

        DynamoDbTable<Follows> followsTable = enhancedClient.table(FollowTableName, TableSchema.fromBean(Follows.class));

        Follows newFollow = new Follows();

        newFollow.setFollower_handle(receivedAuthToken.getAlias());
        newFollow.setFollower_name(users.getFirstName() + users.getLastName());
        newFollow.setFollowee_handle(userToFollow.getAlias());
        newFollow.setFollowee_name(userToFollow.getName());

        followsTable.putItem(newFollow);

        User user = new User(users.getFirstName(), users.getLastName(), users.getAlias(), users.getAlias());

        updateFollowingCount(user, userToFollow, true);
    }

    @Override
    public void unFollow(AuthToken authToken, User userToUnfollow) {
        //TODO: Unfollow a user
        DynamoDbTable<AuthTokens> authTable = enhancedClient.table(AuthTableName, TableSchema.fromBean(AuthTokens.class));
        Key authKey = Key.builder()
                .partitionValue(authToken.getToken())
                .build();

        AuthTokens receivedAuthToken = authTable.getItem(authKey);

        DynamoDbTable<Follows> followsTable = enhancedClient.table(FollowTableName, TableSchema.fromBean(Follows.class));

        // Prepare the key to delete the follow relationship
        Key followKey = Key.builder()
                .partitionValue(userToUnfollow.getAlias())
                .sortValue(receivedAuthToken.getAlias())
                .build();

        followsTable.deleteItem(followKey);

        User user = getUser(receivedAuthToken.getAlias());

        updateFollowingCount(userToUnfollow, user, false);
    }

    @Override
    public Boolean isFollower(User follower, User followee) {
        //TODO: calculate if the follower is following the followee
        try {
            DynamoDbTable<Follows> table = enhancedClient.table(FollowTableName, TableSchema.fromBean(Follows.class));
            Key key = Key.builder()
                    .partitionValue(follower.getAlias())
                    .sortValue(followee.getAlias())
                    .build();

            QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(key));

            QueryEnhancedRequest request = requestBuilder
                    .scanIndexForward(true)
                    .build();

            PageIterable<Follows> pages = table.query(request);

            Boolean followFound = false;

            for (Page<Follows> page : pages) {
                LOGGER.info(page.toString());
                if (!page.items().isEmpty()) {
                    LOGGER.info("Returning True");
                    followFound = true;
                }
            }

            // If no entry is found, return false
            return followFound;
        }
        catch (DynamoDbException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        }
    }

    private User getUser(String alias) {
        DynamoDbTable<Users> table = enhancedClient.table(UserTableName, TableSchema.fromBean(Users.class));
        Key key = Key.builder()
                .partitionValue(alias)
                .build();

        Users users = table.getItem(key);

        return new User(users.getFirstName(), users.getLastName(), users.getAlias(), users.getImage());
    }

    private void updateFollowingCount(User user, User userToFollow, Boolean follow) {
        DynamoDbTable<Users> usersTable = enhancedClient.table(UserTableName, TableSchema.fromBean(Users.class));
        Key userKey = Key.builder()
                .partitionValue(user.getAlias())
                .build();

        Users users = usersTable.getItem(userKey);

        DynamoDbTable<Users> usersToFollowTable = enhancedClient.table(UserTableName, TableSchema.fromBean(Users.class));
        Key userToFollowKey = Key.builder()
                .partitionValue(userToFollow.getAlias())
                .build();

        Users usersToFollow = usersToFollowTable.getItem(userToFollowKey);

        if (follow) {
            users.setNumFollowing(users.getNumFollowing() + 1);

            usersToFollow.setNumFollowers(usersToFollow.getNumFollowers() + 1);
        }else {
            users.setNumFollowing(users.getNumFollowing() - 1);

            usersToFollow.setNumFollowers(usersToFollow.getNumFollowers() - 1);
        }

        usersTable.updateItem(users);
        usersToFollowTable.updateItem(usersToFollow);
    }

    private static boolean isNonEmptyString(String value) {
        return (value != null && value.length() > 0);
    }

    public void addFollowBatch(List<Follow> followers) {
        List<Follows> batchToWrite = new ArrayList<>();
        for (Follow f : followers) {
            Follows dto = new Follows();
            dto.setFollower_handle(f.getFollower().getAlias());
            dto.setFollowee_handle(f.getFollowee().getAlias());
            dto.setFollower_name(f.getFollower().getName());
            dto.setFollowee_name(f.getFollowee().getName());

            batchToWrite.add(dto);

            if (batchToWrite.size() == 25) {
                // package this batch up and send to DynamoDB.
                writeChunkOfUserDTOs(batchToWrite);
                batchToWrite = new ArrayList<>();
            }
        }

        // write any remaining
        if (batchToWrite.size() > 0) {
            // package this batch up and send to DynamoDB.
            writeChunkOfUserDTOs(batchToWrite);
        }
    }
    private void writeChunkOfUserDTOs(List<Follows> followsDTOs) {
        if(followsDTOs.size() > 25)
            throw new RuntimeException("Too many users to write");

        DynamoDbTable<Follows> table = enhancedClient.table(FollowTableName, TableSchema.fromBean(Follows.class));
        WriteBatch.Builder<Follows> writeBuilder = WriteBatch.builder(Follows.class).mappedTableResource(table);
        for (Follows item : followsDTOs) {
            writeBuilder.addPutItem(builder -> builder.item(item));
        }
        BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(writeBuilder.build()).build();

        try {
            BatchWriteResult result = enhancedClient.batchWriteItem(batchWriteItemEnhancedRequest);

            // just hammer dynamodb again with anything that didn't get written this time
            if (result.unprocessedPutItemsForTable(table).size() > 0) {
                writeChunkOfUserDTOs(result.unprocessedPutItemsForTable(table));
            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
