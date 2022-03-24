package id.bl.blcom.iate.models;

import com.google.gson.annotations.SerializedName;

public class ReportPost {

    @SerializedName("post_id")
    private String postId;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("content")
    private String content;

    @SerializedName("group_id")
    private String groupId;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
