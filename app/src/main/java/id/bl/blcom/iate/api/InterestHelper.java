package id.bl.blcom.iate.api;

import com.rezkyatinnov.kyandroid.reztrofit.RestCallback;
import com.rezkyatinnov.kyandroid.reztrofit.Reztrofit;
import com.rezkyatinnov.kyandroid.session.Session;
import com.rezkyatinnov.kyandroid.session.SessionNotFoundException;

import java.util.List;

import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.models.Interest;

public class InterestHelper {
    public static void getInterest(RestCallback<ApiResponse<List<Interest>>> callback){
        String token = "";
        try {
            token = Session.get("token").getValue();
        } catch (SessionNotFoundException e) {
            e.printStackTrace();
        }
        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().getInterest(token).enqueue(callback);
    }
}
