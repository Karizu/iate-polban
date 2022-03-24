package id.bl.blcom.iate.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class Profile extends RealmObject{
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
    @SerializedName("street_address")
    @Expose
    private String street_address;
    @SerializedName("postal_address")
    @Expose
    private String postal_address;
    @SerializedName("subdistrict_address")
    @Expose
    private String subdistrict_address;
    @SerializedName("district_address")
    @Expose
    private String district_address;
    @SerializedName("city_address")
    @Expose
    private String city_address;
    @SerializedName("province_address")
    @Expose
    private String province_address;
    @SerializedName("country_address")
    @Expose
    private String country_address;
    @SerializedName("work_place")
    @Expose
    private String work_place;
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
    @SerializedName("group")
    @Expose
    private Group group;

    @SerializedName("privacy_config")
    @Expose
    private String privacyConfig;

    @SerializedName("instagram_account")
    private String instagramAccount;

    @SerializedName("member_id")
    @Expose
    private String member_id;

    @SerializedName("log")
    @Expose
    private String log;

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

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getStreet_address() {
        return street_address;
    }

    public void setStreet_address(String street_address) {
        this.street_address = street_address;
    }

    public String getPostal_address() {
        return postal_address;
    }

    public void setPostal_address(String postal_address) {
        this.postal_address = postal_address;
    }

    public String getSubdistrict_address() {
        return subdistrict_address;
    }

    public void setSubdistrict_address(String subdistrict_address) {
        this.subdistrict_address = subdistrict_address;
    }

    public String getDistrict_address() {
        return district_address;
    }

    public void setDistrict_address(String district_address) {
        this.district_address = district_address;
    }

    public String getCity_address() {
        return city_address;
    }

    public void setCity_address(String city_address) {
        this.city_address = city_address;
    }

    public String getProvince_address() {
        return province_address;
    }

    public void setProvince_address(String province_address) {
        this.province_address = province_address;
    }

    public String getCountry_address() {
        return country_address;
    }

    public void setCountry_address(String country_address) {
        this.country_address = country_address;
    }

    public String getWork_place() {
        return work_place;
    }

    public void setWork_place(String work_place) {
        this.work_place = work_place;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
