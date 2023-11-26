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
import java.util.logging.Logger;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.data.Users;
import edu.byu.cs.tweeter.util.FakeData;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class UserDAO implements UserDAOInterface {
    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());
    private static final String TableName = "users";
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
        //TODO: Access the users table to retrieve the
        // needed info to create a new user object to return
        DynamoDbTable<Users> table = enhancedClient.table(TableName, TableSchema.fromBean(Users.class));
        Key key = Key.builder()
                .partitionValue(username)
                .build();

        //return getDummyUser();
        Users users = table.getItem(key);
        return new User(users.getFirstName(), users.getLastName(), users.getAlias(), users.getImage());
    }

    @Override
    public AuthToken getAuthToken(String username) {
        //TODO: Access the authToken table to retrieve the
        // needed info to create a new authToken object to return
        
        return getDummyAuthToken();
    }

    @Override
    public User getUserByAlias(String alias) {
        //TODO: Access the users table to retrieve the
        // needed info to create a new user object to return
        DynamoDbTable<Users> table = enhancedClient.table(TableName, TableSchema.fromBean(Users.class));
        Key key = Key.builder()
                .partitionValue(alias)
                .build();

        //return getDummyUser();
        Users users = table.getItem(key);
        return new User(users.getFirstName(), users.getLastName(), users.getAlias(), users.getImage());
    }

    @Override
    public void deleteAuthToken(AuthToken authToken) {
        //TODO: Delete AuthToken and all expired AuthTokens from table
    }

    @Override
    public void putUser(String username, String password, String firstName, String lastName, String image) {
        //TODO: Put new user into table
        LOGGER.info("preparing table");
        DynamoDbTable<Users> table = enhancedClient.table(TableName, TableSchema.fromBean(Users.class));

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
        }
        return "FAILED TO HASH PASSWORD";
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
