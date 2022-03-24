package id.bl.blcom.iate.models;

import com.google.gson.annotations.SerializedName;

public class UserDevice {

    @SerializedName("user_id")
    private String userId;

    @SerializedName("type")
    private String type;

    @SerializedName("vendor")
    private String vendor;

    @SerializedName("version")
    private String version;

    @SerializedName("os")
    private String os;

    @SerializedName("token")
    private String token;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
