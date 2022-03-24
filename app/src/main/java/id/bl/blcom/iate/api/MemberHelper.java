package id.bl.blcom.iate.api;

import com.rezkyatinnov.kyandroid.reztrofit.RestCallback;
import com.rezkyatinnov.kyandroid.reztrofit.Reztrofit;
import com.rezkyatinnov.kyandroid.session.Session;
import com.rezkyatinnov.kyandroid.session.SessionNotFoundException;

import java.util.List;

import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.models.GeneralDataProfile;
import id.bl.blcom.iate.models.MemberProfile;
import id.bl.blcom.iate.models.Profile;
import id.bl.blcom.iate.models.User;
import id.bl.blcom.iate.models.response.MemberDataResponse;
import retrofit2.Callback;

public class MemberHelper {
    public static void getMember(int limit, int offset, String groupId, RestCallback<ApiResponse<List<MemberDataResponse>>> callback){
        String token = "";
        try {
            token = Session.get("token").getValue();
        } catch (SessionNotFoundException e) {
            e.printStackTrace();
        }
        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().getMember(limit, offset, 1, groupId,token).enqueue(callback);
    }

    public static void getMemberNoLimit(String groupId, RestCallback<ApiResponse<List<MemberDataResponse>>> callback){
        String token = "";
        try {
            token = Session.get("token").getValue();
        } catch (SessionNotFoundException e) {
            e.printStackTrace();
        }
        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().getMemberNoLimit(1, groupId, token).enqueue(callback);
    }

    public static void getMemberNearMe(String latitude, String longitude, RestCallback<ApiResponse<List<MemberProfile>>> callback){
        String token = "";
        try {
            token = Session.get("token").getValue();
        } catch (SessionNotFoundException e) {
            e.printStackTrace();
        }
        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().getMemberNearMe(1, latitude, longitude, token).enqueue(callback);
    }

    public static void getProfile(RestCallback<ApiResponse<GeneralDataProfile>> callback){
        String token = "";
        try {
            token = Session.get("token").getValue();
        } catch (SessionNotFoundException e) {
            e.printStackTrace();
        }
        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().getProfile(token).enqueue(callback);
    }

    public static void getProfileDetail(String userId, RestCallback<ApiResponse<GeneralDataProfile>> callback){
        String token = "";
        try {
            token = Session.get("token").getValue();
        } catch (SessionNotFoundException e) {
            e.printStackTrace();
        }
        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().getProfileDetail(userId, token).enqueue(callback);
    }

    public static void getUser(int limit, Callback<ApiResponse<List<User>>> callback){
        String token = "";
        try {
            token = Session.get("token").getValue();
        } catch (SessionNotFoundException e) {
            e.printStackTrace();
        }
        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().getUser(limit, token).enqueue(callback);
    }
}
