package id.bl.blcom.iate.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import id.bl.blcom.iate.models.Group;

public class EventDataResponse extends GeneralDataResponse {
    @SerializedName("place")
    @Expose
    private String place;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("time")
    @Expose
    private String time;

    @SerializedName("lat")
    @Expose
    private String latitude;

    @SerializedName("long")
    @Expose
    private String longitude;

    @SerializedName("group")
    @Expose
    private Group group;

    @SerializedName("invitation_status")
    private Integer invitationStatus;

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Integer getInvitationStatus() {
        return invitationStatus;
    }

    public void setInvitationStatus(Integer invitationStatus) {
        this.invitationStatus = invitationStatus;
    }
}
