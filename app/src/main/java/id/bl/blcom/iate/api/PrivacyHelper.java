package id.bl.blcom.iate.api;

import com.rezkyatinnov.kyandroid.reztrofit.RestCallback;
import com.rezkyatinnov.kyandroid.reztrofit.Reztrofit;
import com.rezkyatinnov.kyandroid.session.Session;
import com.rezkyatinnov.kyandroid.session.SessionNotFoundException;

import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.models.Password;
import id.bl.blcom.iate.models.Profile;
import okhttp3.RequestBody;

public class PrivacyHelper {

    public static void updateProfile(RequestBody profile, RestCallback<ApiResponse<Profile>> callback){
        String token = "";
        try {
            token = Session.get("token").getValue();
        } catch (SessionNotFoundException e) {
            e.printStackTrace();
        }
        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().updateProfile(profile, token).enqueue(callback);
    }

    public static void chagePassword(String userId, Password password, RestCallback<ApiResponse> callback){
        String token = "";
        try {
            token = Session.get("token").getValue();
        } catch (SessionNotFoundException e) {
            e.printStackTrace();
        }
        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().changePassword(userId, password, token).enqueue(callback);
    }
}
