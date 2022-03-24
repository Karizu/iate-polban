package id.bl.blcom.iate.api;

import com.rezkyatinnov.kyandroid.reztrofit.RestCallback;
import com.rezkyatinnov.kyandroid.reztrofit.Reztrofit;
import com.rezkyatinnov.kyandroid.session.Session;
import com.rezkyatinnov.kyandroid.session.SessionNotFoundException;

import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.models.response.HomeDataResponse;

public class HomeHelper {
    public static void getHome(int limit, int offset, RestCallback<ApiResponse<HomeDataResponse>> callback){
        String token = "";
        try {
            token = Session.get("token").getValue();
        } catch (SessionNotFoundException e) {
            e.printStackTrace();
        }
        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().getHome(limit, offset, token).enqueue(callback);
    }
}
