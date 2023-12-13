package edu.byu.cs.tweeter.server.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.data.AuthTokens;
import edu.byu.cs.tweeter.server.dao.data.DataPage;
import edu.byu.cs.tweeter.server.dao.data.Feeds;
import edu.byu.cs.tweeter.server.dao.data.Follows;
import edu.byu.cs.tweeter.server.dao.data.Stories;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class StatusDAO implements StatusDAOInterface {
    private static final Logger LOGGER = Logger.getLogger(StatusDAO.class.getName());
    private static final String AuthTableName = "authToken";
    private static final String StoryTableName = "story";
    private static final String FeedTableName = "feed";
    private static final String StoryAliasAttr = "posterAlias";
    private static final String StoryPostAttr = "post";
    private static final String FeedAliasAttr = "recieverAlias";
    private static final String FeedPostAttr = "post";

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

    @Override
    public void postStatus(AuthToken authToken, Status status) {
        try {
            LOGGER.info("posting");
            DynamoDbTable<AuthTokens> authTable = enhancedClient.table(AuthTableName, TableSchema.fromBean(AuthTokens.class));

            LOGGER.info("getting user from authToken");
            Key key = Key.builder()
                    .partitionValue(authToken.getToken())
                    .build();

            AuthTokens receivedAuthToken = authTable.getItem(key);

            DynamoDbTable<Stories> storyTable = enhancedClient.table(StoryTableName, TableSchema.fromBean(Stories.class));

            LOGGER.info("Creating post");
            Stories newPost = new Stories();

            long timestamp = System.currentTimeMillis();

            newPost.setPosterAlias(receivedAuthToken.getAlias());
            newPost.setPostedTimestamp(timestamp);
            newPost.setPost(status.getPost());
            newPost.setUrls(status.getUrls());
            newPost.setMentions(status.getMentions());

            storyTable.putItem(newPost);
        }
        catch (DynamoDbException ex) {
            ex.printStackTrace();
            throw new RuntimeException("[Bad Request] error posting");
        }
    }

    @Override
    public Pair<List<Status>, Boolean> getStory(User targetUser, Status lastStatus, int limit) {
        try {
            LOGGER.info("creating table");
            DynamoDbTable<Stories> table = enhancedClient.table(StoryTableName, TableSchema.fromBean(Stories.class));
            Key key = Key.builder()
                    .partitionValue(targetUser.getAlias())
                    .build();

            LOGGER.info("building");
            QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(key))
                    .limit(limit);

            LOGGER.info("Setting start key");
            if (lastStatus != null && isNonEmptyString(lastStatus.getPost())) {
                // Build the Exclusive Start Key
                Map<String, AttributeValue> startKey = new HashMap<>();

                startKey.put(StoryAliasAttr, AttributeValue.builder().s(targetUser.getAlias()).build());
                startKey.put(StoryPostAttr, AttributeValue.builder().s(lastStatus.getPost()).build());

                requestBuilder.exclusiveStartKey(startKey);
            }

            QueryEnhancedRequest request = requestBuilder
                    .scanIndexForward(true)
                    .build();

            LOGGER.info("Building data page");
            DataPage<Status> result = new DataPage<>();

            PageIterable<Stories> pages = table.query(request);
            pages.stream()
                    .limit(1)
                    .forEach((Page<Stories> page) -> {
                        result.setHasMorePages(page.lastEvaluatedKey() != null);
                        for (Stories story : page.items()) {
                            // Convert DynamoDB Stories to Status
                            Status status = new Status();
                            status.setUser(targetUser);
                            status.post = story.getPost();
                            status.urls = story.getUrls();
                            status.mentions = story.getMentions();
                            status.timestamp = story.getPostedTimestamp();

                            // Add status to the result
                            result.getValues().add(status);
                        }
                    });

            return new Pair<>(result.getValues(), result.isHasMorePages());
        }
        catch (DynamoDbException ex) {
            ex.printStackTrace();
            throw new RuntimeException("[Bad Request] error with getting story" + ex.getMessage());
        }
    }

    @Override
    public Pair<List<Status>, Boolean> getFeed(User targetUser, Status lastStatus, int limit) {
        //TODO: Access the feed table to retrieve needed data
        // response needs a list of statuses and has more pages Boolean
        try {
            DynamoDbTable<Feeds> table = enhancedClient.table(FeedTableName, TableSchema.fromBean(Feeds.class));
            Key key = Key.builder()
                    .partitionValue(targetUser.getAlias())
                    .build();

            QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(key))
                    .limit(limit);

            if (lastStatus != null && isNonEmptyString(lastStatus.getPost())) {
                // Build the Exclusive Start Key
                Map<String, AttributeValue> startKey = new HashMap<>();

                startKey.put(FeedAliasAttr, AttributeValue.builder().s(targetUser.getAlias()).build());
                startKey.put(FeedPostAttr, AttributeValue.builder().s(lastStatus.getPost()).build());

                requestBuilder.exclusiveStartKey(startKey);
            }

            QueryEnhancedRequest request = requestBuilder
                    .scanIndexForward(true)
                    .build();

            DataPage<Status> result = new DataPage<>();

            PageIterable<Feeds> pages = table.query(request);
            pages.stream()
                    .limit(1)
                    .forEach((Page<Feeds> page) -> {
                        result.setHasMorePages(page.lastEvaluatedKey() != null);
                        for (Feeds story : page.items()) {
                            // Convert DynamoDB Stories to Status
                            Status status = new Status();
                            status.setUser(targetUser);
                            status.post = story.getPost();
                            status.urls = story.getUrls();
                            status.mentions = story.getMentions();
                            status.timestamp = story.getPostedTimestamp();

                            // Add status to the result
                            result.getValues().add(status);
                        }
                    });

            return new Pair<>(result.getValues(), result.isHasMorePages());
        }
        catch (DynamoDbException ex) {
            ex.printStackTrace();
            throw new RuntimeException("[Bad Request] error with getting feed" + ex.getMessage());
        }

        //return getFakeData().getPageOfStatus(lastStatus, limit);
    }

    @Override
    public void postFeed(String alias, Status status) {
        //TODO: post the feed
        DynamoDbTable<Feeds> table = enhancedClient.table(FeedTableName, TableSchema.fromBean(Feeds.class));

        Feeds newFeed = new Feeds();

        long timestamp = System.currentTimeMillis();

        newFeed.setRecieverAlias(alias);
        newFeed.setPostedTimestamp(timestamp);
        newFeed.setPost(status.getPost());
        newFeed.setUrls(status.getUrls());
        newFeed.setMentions(status.getMentions());

        table.putItem(newFeed);
    }

    private static boolean isNonEmptyString(String value) {
        return (value != null && value.length() > 0);
    }

    private FakeData getFakeData() {
        return FakeData.getInstance();
    }
}
