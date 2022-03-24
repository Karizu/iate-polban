package id.bl.blcom.iate.models.response;

import id.bl.blcom.iate.models.MemberProfile;

public class MemberDataResponse extends GeneralDataResponse{
    private MemberProfile profile;

    public MemberProfile getProfile() {
        return profile;
    }

    public void setProfile(MemberProfile profile) {
        this.profile = profile;
    }
}
