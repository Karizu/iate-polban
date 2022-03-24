package id.bl.blcom.iate.api;

import com.rezkyatinnov.kyandroid.reztrofit.RestCallback;
import com.rezkyatinnov.kyandroid.reztrofit.Reztrofit;

import java.util.List;

import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.models.response.AreaDataResponse;

public class AreaHelper {
    public static void getArea(RestCallback<ApiResponse<List<AreaDataResponse>>> callback){
        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().getArea().enqueue(callback);
    }
}
