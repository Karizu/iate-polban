package id.bl.blcom.iate.models.response;

import androidx.fragment.app.FragmentManager;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import id.bl.blcom.iate.models.Loves;
import id.bl.blcom.iate.models.Participant;
import id.bl.blcom.iate.models.Polling;
import id.bl.blcom.iate.models.User;

public class DiscussionDataResponse {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("user_id")
    @Expose
    private String userId;

    @SerializedName("group_id")
    @Expose
    private String groupId;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("caption")
    @Expose
    private String caption;

    @SerializedName("content")
    @Expose
    private String content;

    @SerializedName("type")
    @Expose
    private Integer type;

    @SerializedName("loved")
    @Expose
    private Integer loved;

    @SerializedName("status")
    @Expose
    private Integer status;

    @SerializedName("created_at")
    @Expose
    private String created_at;

    @SerializedName("updated_at")
    @Expose
    private String updated_at;

    @SerializedName("deleted_at")
    @Expose
    private String deleted_at;

    @SerializedName("voted")
    @Expose
    private Boolean voted;

    @SerializedName("loves_count")
    @Expose
    private Integer loves_count;

    @SerializedName("participation_count")
    @Expose
    private int participation_count;

    @SerializedName("user_loved_count")
    @Expose
    private int user_loved_count;

    @SerializedName("user")
    @Expose
    private User user;

    @SerializedName("polling")
    @Expose
    private List<Polling> polling;

    @SerializedName("loves")
    @Expose
    private List<Loves> loves;

    @SerializedName("participant")
    @Expose
    private List<Participant> participant;

    private FragmentManager fragmentManager;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getLoved() {
        return loved;
    }

    public void setLoved(Integer loved) {
        this.loved = loved;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Polling> getPolling() {
        return polling;
    }

    public void setPolling(List<Polling> polling) {
        this.polling = polling;
    }

    public Boolean getVoted() {
        return voted;
    }

    public void setVoted(Boolean voted) {
        this.voted = voted;
    }

    public List<Loves> getLoves() {
        return loves;
    }

    public void setLoves(List<Loves> loves) {
        this.loves = loves;
    }

    public Integer getLoves_count() {
        return loves_count;
    }

    public void setLoves_count(Integer loves_count) {
        this.loves_count = loves_count;
    }

    public int getParticipation_count() {
        return participation_count;
    }

    public void setParticipation_count(int participation_count) {
        this.participation_count = participation_count;
    }

    public int getUser_loved_count() {
        return user_loved_count;
    }

    public void setUser_loved_count(int user_loved_count) {
        this.user_loved_count = user_loved_count;
    }

    public List<Participant> getParticipant() {
        return participant;
    }

    public void setParticipant(List<Participant> participant) {
        this.participant = participant;
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }
}
