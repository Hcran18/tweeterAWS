package edu.byu.cs.tweeter.server.dao.data;

import java.util.List;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class Feeds {
    private String recieverAlias;
    private long postedTimestamp;
    private String post;
    private List<String> urls;
    private List<String> mentions;

    @DynamoDbPartitionKey
    public String getRecieverAlias() {
        return recieverAlias;
    }

    public void setRecieverAlias(String recieverAlias) {
        this.recieverAlias = recieverAlias;
    }

    @DynamoDbSortKey
    public long getPostedTimestamp() {
        return postedTimestamp;
    }

    public void setPostedTimestamp(long postedTimestamp) {
        this.postedTimestamp = postedTimestamp;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public List<String> getMentions() {
        return mentions;
    }

    public void setMentions(List<String> mentions) {
        this.mentions = mentions;
    }

    @Override
    public String toString() {
        return "Feeds{" +
                "recieverAlias='" + recieverAlias + '\'' +
                ", postedTimestamp=" + postedTimestamp +
                ", post='" + post + '\'' +
                ", urls=" + urls +
                ", mentions=" + mentions +
                '}';
    }
}
