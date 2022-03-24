package id.bl.blcom.iate.api;

import com.rezkyatinnov.kyandroid.reztrofit.RestCallback;
import com.rezkyatinnov.kyandroid.reztrofit.Reztrofit;

import java.util.List;

import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.models.response.AddressDatum;
import id.bl.blcom.iate.models.response.AreaDataResponse;

public class AddressHelper {
    public static void getRegencies(RestCallback<List<AddressDatum>> callback, String provinceId){
        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().getRegencies(provinceId).enqueue(callback);
    }

    public static void getDistricts(RestCallback<List<AddressDatum>> callback, String regenciesId){
        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().getDistricts(regenciesId).enqueue(callback);
    }

    public static void getVillages(RestCallback<List<AddressDatum>> callback, String districsId){
        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().getVillages(districsId).enqueue(callback);
    }
}
