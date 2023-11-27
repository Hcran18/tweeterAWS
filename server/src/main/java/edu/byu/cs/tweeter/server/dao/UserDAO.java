package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.data.AuthTokens;
import edu.byu.cs.tweeter.server.dao.data.Users;
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
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class UserDAO implements UserDAOInterface {
    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());
    private static final String UsersTableName = "users";
    private static final String AuthTokenTableName = "authToken";
    public static final String AuthTokenIndex = "alias-index";
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
    public User getUserByUsername(String username) {
        DynamoDbTable<Users> table = enhancedClient.table(UsersTableName, TableSchema.fromBean(Users.class));
        Key key = Key.builder()
                .partitionValue(username)
                .build();

        //return getDummyUser();
        Users users = table.getItem(key);
        return new User(users.getFirstName(), users.getLastName(), users.getAlias(), users.getImage());
    }

    @Override
    public AuthToken getAuthToken(String username) {
        try {
            LOGGER.info("Attempting to retrieve authToken for username: " + username);
            DynamoDbIndex<AuthTokens> index = enhancedClient.table(AuthTokenTableName, TableSchema.fromBean(AuthTokens.class)).index(AuthTokenIndex);

            Key key = Key.builder()
                    .partitionValue(username)
                    .build();

            QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(key));

            QueryEnhancedRequest request = requestBuilder
                    .scanIndexForward(true)
                    .build();

            SdkIterable<Page<AuthTokens>> sdkIterable = index.query(request);
            PageIterable<AuthTokens> pages = PageIterable.create(sdkIterable);

            AuthTokens authToken = null;

            for (Page<AuthTokens> page : pages) {
                for (AuthTokens tokens : page.items()) {
                    authToken = tokens;
                    // Assuming we only need the first item associated with the username
                    break;
                }
                if (authToken != null) {
                    // Found the required AuthToken for the username
                    break;
                }
            }

            if (authToken != null) {
                LOGGER.info(authToken.getAuthToken());
                return new AuthToken(authToken.getAuthToken(), authToken.getTimestamp());
            } else {
                throw new RuntimeException("[Bad Request] authToken does not exist");
            }
        } catch (DynamoDbException ex) {
            LOGGER.severe("Error retrieving AuthToken for username: " + username + ex.getMessage());
            throw new RuntimeException("Error retrieving AuthToken");
        }
    }

    @Override
    public User getUserByAlias(String alias) {
        DynamoDbTable<Users> table = enhancedClient.table(UsersTableName, TableSchema.fromBean(Users.class));
        Key key = Key.builder()
                .partitionValue(alias)
                .build();

        //return getDummyUser();
        Users users = table.getItem(key);
        return new User(users.getFirstName(), users.getLastName(), users.getAlias(), users.getImage());
    }

    @Override
    public void deleteAuthToken(AuthToken authToken) {
        DynamoDbTable<AuthTokens> table = enhancedClient.table(AuthTokenTableName, TableSchema.fromBean(AuthTokens.class));
        Key key = Key.builder()
                .partitionValue(authToken.getToken())
                .build();

        table.deleteItem(key);

        deleteAllExpiredAuthTokens();
    }

    @Override
    public void putUser(String username, String password, String firstName, String lastName, String image) {
        LOGGER.info("preparing table");
        DynamoDbTable<Users> table = enhancedClient.table(UsersTableName, TableSchema.fromBean(Users.class));

        Users newUser = new Users();

        LOGGER.info("storing the image in s3");
        String imageLink = storeImage(image, username);

        LOGGER.info("creating a new user");
        newUser.setAlias(username);
        newUser.setPassword(getSecurePassword(password));
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setImage(imageLink);
        newUser.setNumFollowing(0);
        newUser.setNumFollowers(0);
        LOGGER.info(newUser.toString());

        try {
            LOGGER.info("putting the new user in the table");
            table.putItem(newUser);
        } catch (DynamoDbException e) {
            LOGGER.severe("Error putting user in DynamoDB: " + e.getMessage());
            e.printStackTrace();
            // Handle the exception accordingly based on your application's logic
        }
    }

    @Override
    public void putAuthToken(String username) {
        LOGGER.info("Attempting to put authToken");
        DynamoDbTable<AuthTokens> table = enhancedClient.table(AuthTokenTableName,
                TableSchema.fromBean(AuthTokens.class));

        String newAuthToken = generateAuthToken();
        long timestamp = System.currentTimeMillis();

        try {
            AuthTokens authTokens = new AuthTokens();
            authTokens.setAuthToken(newAuthToken);
            authTokens.setAlias(username);
            authTokens.setTimestamp(timestamp);

            table.putItem(authTokens);
            LOGGER.info("AuthToken added successfully.");
        } catch (DynamoDbException ex) {
            LOGGER.severe("Unable to put the authToken: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public Boolean isCorrectPassword(String username, String password) {
        DynamoDbTable<Users> table = enhancedClient.table(UsersTableName, TableSchema.fromBean(Users.class));
        Key key = Key.builder()
                .partitionValue(username)
                .build();

        Users users = table.getItem(key);
        String hashedPassword = getSecurePassword(password);

        if (Objects.equals(users.getPassword(), hashedPassword)) {
            return true;
        }
        else {
            return false;
        }
    }

    private String storeImage(String image, String alias) {
        AmazonS3 s3 = AmazonS3ClientBuilder
                .standard()
                .withRegion("us-west-1")
                .build();

        byte[] byteArray = Base64.getDecoder().decode(image);

        ObjectMetadata data = new ObjectMetadata();

        data.setContentLength(byteArray.length);

        data.setContentType("image/jpeg");

        LOGGER.info("Creating the S3 request");
        PutObjectRequest request = new PutObjectRequest("hcrandallcs340bucket", alias,
                new ByteArrayInputStream(byteArray), data).withCannedAcl(CannedAccessControlList.PublicRead);

        LOGGER.info("Putting the image in S3");
        s3.putObject(request);

        return "https://hcrandallcs340bucket.s3.us-west-1.amazonaws.com/" + alias;
    }

    private String getSecurePassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating secure password");
        }
    }

    private static String generateAuthToken() {
        LOGGER.info("Creating authToken");
        return UUID.randomUUID().toString();
    }

    private void deleteAllExpiredAuthTokens() {
        //TODO: read in and delete all expired AuthTokens ie authtokens older than 5 minutes
        try {
            DynamoDbTable<AuthTokens> table = enhancedClient.table(AuthTokenTableName, TableSchema.fromBean(AuthTokens.class));

            // Calculate the timestamp for five minutes ago
            long fiveMinutesAgo = System.currentTimeMillis() - (5 * 60 * 1000);

            ScanEnhancedRequest scanRequest = ScanEnhancedRequest.builder().build();
            SdkIterable<Page<AuthTokens>> sdkIterable = table.scan(scanRequest);
            PageIterable<AuthTokens> pages = PageIterable.create(sdkIterable);

            for (Page<AuthTokens> page : pages) {
                for (AuthTokens token : page.items()) {
                    if (token.getTimestamp() < fiveMinutesAgo) {
                        // Token is expired, delete it
                        Key key = Key.builder().partitionValue(token.getAuthToken()).build();
                        table.deleteItem(key);
                        LOGGER.info("Expired authToken deleted: " + token.getAuthToken());
                    }
                }
            }
        } catch (DynamoDbException ex) {
            LOGGER.severe("Error deleting expired AuthTokens: " + ex.getMessage());
            throw new RuntimeException("Error deleting expired AuthTokens");
        }
    }
}
