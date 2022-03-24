package id.bl.blcom.iate.api;

import com.rezkyatinnov.kyandroid.reztrofit.RestCallback;
import com.rezkyatinnov.kyandroid.reztrofit.Reztrofit;
import com.rezkyatinnov.kyandroid.session.Session;
import com.rezkyatinnov.kyandroid.session.SessionNotFoundException;

import java.util.List;

import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.models.Group;

public class GroupHelper {
    public static void getGroup(RestCallback<ApiResponse<List<Group>>> callback){
        String token = "";
        try {
            token = Session.get("token").getValue();
        } catch (SessionNotFoundException e) {
            e.printStackTrace();
        }
        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().getGroup(token).enqueue(callback);
    }
}
