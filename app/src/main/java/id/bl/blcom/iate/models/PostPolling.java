package id.bl.blcom.iate.models;

import com.google.gson.annotations.SerializedName;

public class PostPolling {
    @SerializedName("user_id")
    private String userId;

    @SerializedName("post_id")
    private String postId;

    @SerializedName("polling_id")
    private  String pollingId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPollingId() {
        return pollingId;
    }

    public void setPollingId(String pollingId) {
        this.pollingId = pollingId;
    }
}
