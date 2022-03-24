package id.bl.blcom.iate.api;

import com.rezkyatinnov.kyandroid.reztrofit.RestCallback;
import com.rezkyatinnov.kyandroid.reztrofit.Reztrofit;
import com.rezkyatinnov.kyandroid.session.Session;
import com.rezkyatinnov.kyandroid.session.SessionNotFoundException;

import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.models.Register;
import id.bl.blcom.iate.models.User;
import id.bl.blcom.iate.models.UserDevice;
import okhttp3.RequestBody;

public class AuthHelper {
    public static void login(User user, RestCallback<ApiResponse<User>> callback){
        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().login(user).enqueue(callback);
    }

    public static void register(Register register, RestCallback<ApiResponse> callback){
        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().postRegister(register).enqueue(callback);
    }

    public static void forgotPassword(User user, RestCallback<ApiResponse<User>> callback){
        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().forgotPassword(user).enqueue(callback);
    }

    public static void recoverPassword(RequestBody requestBody, RestCallback<ApiResponse> callback){
        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().recoverPassword(requestBody).enqueue(callback);
    }

    public static void registerUserDevice(UserDevice userDevice, RestCallback<ApiResponse> callback){
        String token = "";
        try {
            token = Session.get("token").getValue();
        } catch (SessionNotFoundException e) {
            e.printStackTrace();
        }
        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().registerUserDevice(userDevice, token).enqueue(callback);
    }
    
//    public static void removeUserDevice(String deviceId, RestCallback<ApiResponse> callback){
//        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
//        service.getEndpoint().re(deviceId).enqueue(callback);
//    }
}
