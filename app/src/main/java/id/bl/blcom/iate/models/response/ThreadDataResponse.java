package id.bl.blcom.iate.models.response;

import androidx.fragment.app.FragmentManager;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import id.bl.blcom.iate.models.User;

public class ThreadDataResponse {
    @SerializedName("postId")
    @Expose
    private String postId;

    @SerializedName("user_id")
    @Expose
    private String userId;

    @SerializedName("comment")
    @Expose
    private String comment;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("type")
    private int type;

    @SerializedName("created_at")
    @Expose
    private String createdAt;

    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    @SerializedName("content")
    @Expose
    private String content;
    private User user;

    private FragmentManager fragmentManager;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
