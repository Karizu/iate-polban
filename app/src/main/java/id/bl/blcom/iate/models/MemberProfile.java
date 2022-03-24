package id.bl.blcom.iate.models;

import androidx.fragment.app.FragmentManager;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class MemberProfile {
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("group_id")
    @Expose
    private String groupId;
    @SerializedName("fullname")
    @Expose
    private String fullname;
    @SerializedName("phone_number")
    @Expose
    private String phoneNumber;
    @SerializedName("birth_place")
    @Expose
    private String birthPlace;
    @SerializedName("birth_date")
    @Expose
    private String birthDate;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("picture")
    @Expose
    private String picture;
    @SerializedName("hobby")
    @Expose
    private String hobby;
    @SerializedName("interests")
    @Expose
    private String interests;
    @SerializedName("work")
    @Expose
    private String work;
    @SerializedName("whatsapp_number")
    @Expose
    private String whatsupNumber;
    @SerializedName("line_account")
    @Expose
    private String lineAccount;
    @SerializedName("pin_bbm")
    @Expose
    private String pinBbm;
    @SerializedName("facebook_account")
    @Expose
    private String facebookAccount;
    @SerializedName("twitter_account")
    @Expose
    private String twitterAccount;
    @SerializedName("shirt_size")
    @Expose
    private String shirtSize;
    @SerializedName("group_name")
    @Expose
    private String group_name;
    @SerializedName("member_id")
    @Expose
    private String member_id;
    @SerializedName("distance_in_km")
    @Expose
    private String distance_in_km;
    @SerializedName("group")
    @Expose
    private Group group;

    @SerializedName("privacy_config")
    @Expose
    private String privacyConfig;

    @SerializedName("instagram_account")
    private String instagramAccount;

    private FragmentManager fragmentManager;

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

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getWhatsupNumber() {
        return whatsupNumber;
    }

    public void setWhatsupNumber(String whatsupNumber) {
        this.whatsupNumber = whatsupNumber;
    }

    public String getLineAccount() {
        return lineAccount;
    }

    public void setLineAccount(String lineAccount) {
        this.lineAccount = lineAccount;
    }

    public String getPinBbm() {
        return pinBbm;
    }

    public void setPinBbm(String pinBbm) {
        this.pinBbm = pinBbm;
    }

    public String getFacebookAccount() {
        return facebookAccount;
    }

    public void setFacebookAccount(String facebookAccount) {
        this.facebookAccount = facebookAccount;
    }

    public String getTwitterAccount() {
        return twitterAccount;
    }

    public void setTwitterAccount(String twitterAccount) {
        this.twitterAccount = twitterAccount;
    }

    public String getShirtSize() {
        return shirtSize;
    }

    public void setShirtSize(String shirtSize) {
        this.shirtSize = shirtSize;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getPrivacyConfig() {
        return privacyConfig;
    }

    public void setPrivacyConfig(String privacyConfig) {
        this.privacyConfig = privacyConfig;
    }

    public String getInstagramAccount() {
        return instagramAccount;
    }

    public void setInstagramAccount(String instagramAccount) {
        this.instagramAccount = instagramAccount;
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getDistance_in_km() {
        return distance_in_km;
    }

    public void setDistance_in_km(String distance_in_km) {
        this.distance_in_km = distance_in_km;
    }
}
