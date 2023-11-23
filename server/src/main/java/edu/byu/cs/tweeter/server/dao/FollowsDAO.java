package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.server.dao.data.DataPage;
import edu.byu.cs.tweeter.server.dao.data.Follows;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
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

import java.util.*;


public class FollowsDAO {
    private static final String TableName = "follows";
    public static final String IndexName = "follows_index";
    private static final String FollowerAttr = "follower_handle";
    private static final String FolloweeAttr = "followee_handle";

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

    private static boolean isNonEmptyString(String value) {
        return (value != null && value.length() > 0);
    }

    public void putFollows(String follower_handle, String follower_name, String followee_handle, String followee_name) {
        DynamoDbTable<Follows> table = enhancedClient.table(TableName, TableSchema.fromBean(Follows.class));

        Follows newFollow = new Follows();

        newFollow.setFollower_handle(follower_handle);
        newFollow.setFollower_name(follower_name);
        newFollow.setFollowee_handle(followee_handle);
        newFollow.setFollowee_name(followee_name);

        table.putItem(newFollow);
    }

    public Follows getFollows(String follower_handle, String followee_handle) {
        DynamoDbTable<Follows> table = enhancedClient.table(TableName, TableSchema.fromBean(Follows.class));
        Key key = Key.builder()
                .partitionValue(follower_handle).sortValue(followee_handle)
                .build();

        return table.getItem(key);
    }

    public void updateFollows(String follower_handle, String followee_handle, String new_follower, String new_followee) {
        DynamoDbTable<Follows> table = enhancedClient.table(TableName, TableSchema.fromBean(Follows.class));
        Key key = Key.builder()
                .partitionValue(follower_handle).sortValue(followee_handle)
                .build();

        Follows follows = table.getItem(key);

        follows.setFollower_name(new_follower);
        follows.setFollowee_name(new_followee);

        table.updateItem(follows);
    }

    public void deleteFollows(String follower_handle, String followee_handle) {
        DynamoDbTable<Follows> table = enhancedClient.table(TableName, TableSchema.fromBean(Follows.class));
        Key key = Key.builder()
                .partitionValue(follower_handle).sortValue(followee_handle)
                .build();

        table.deleteItem(key);
    }

    public DataPage<Follows> getPageOfFollowers(String targetUserAlias, int pageSize, String lastUserAlias ) {
        DynamoDbTable<Follows> table = enhancedClient.table(TableName, TableSchema.fromBean(Follows.class));
        Key key = Key.builder()
                .partitionValue(targetUserAlias)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(pageSize);

        if(isNonEmptyString(lastUserAlias)) {
            // Build up the Exclusive Start Key (telling DynamoDB where you left off reading items)
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FollowerAttr, AttributeValue.builder().s(targetUserAlias).build());
            startKey.put(FolloweeAttr, AttributeValue.builder().s(lastUserAlias).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder
                .scanIndexForward(true)
                .build();

        DataPage<Follows> result = new DataPage<>();

        PageIterable<Follows> pages = table.query(request);
        pages.stream()
                .limit(1)
                .forEach((Page<Follows> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(follower -> result.getValues().add(follower));
                });

        return result;
    }

    public DataPage<Follows> getPageOfFollowees(String targetUserAlias, int pageSize, String lastUserAlias ) {
        DynamoDbIndex<Follows> index = enhancedClient.table(TableName, TableSchema.fromBean(Follows.class)).index(IndexName);
        Key key = Key.builder()
                .partitionValue(targetUserAlias)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(pageSize);

        if(isNonEmptyString(lastUserAlias)) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FolloweeAttr, AttributeValue.builder().s(targetUserAlias).build());
            startKey.put(FollowerAttr, AttributeValue.builder().s(lastUserAlias).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder
                .scanIndexForward(true)
                .build();

        DataPage<Follows> result = new DataPage<>();

        SdkIterable<Page<Follows>> sdkIterable = index.query(request);
        PageIterable<Follows> pages = PageIterable.create(sdkIterable);
        pages.stream()
                .limit(1)
                .forEach((Page<Follows> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(followee -> result.getValues().add(followee));
                });

        return result;
    }
}
