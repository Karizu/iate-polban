package id.bl.blcom.iate.api;

import com.rezkyatinnov.kyandroid.reztrofit.RestCallback;
import com.rezkyatinnov.kyandroid.reztrofit.Reztrofit;
import com.rezkyatinnov.kyandroid.session.Session;
import com.rezkyatinnov.kyandroid.session.SessionNotFoundException;

import java.util.List;

import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.models.Notification;

public class NotificationHelper {
    public static void getAllNotification(String user_id, int limit, int offset, RestCallback<ApiResponse<List<Notification>>> callback){
        String token = "";
        try {
            token = Session.get("token").getValue();
        } catch (SessionNotFoundException e) {
            e.printStackTrace();
        }
        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().getNotification(user_id, limit, offset, token).enqueue(callback);
    }

    public static void getCountNotification(String user_id, boolean is_read, RestCallback<ApiResponse<List<Notification>>> callback){
        String token = "";
        try {
            token = Session.get("token").getValue();
        } catch (SessionNotFoundException e) {
            e.printStackTrace();
        }
        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().getCountNotification(user_id, is_read, token).enqueue(callback);
    }

    public static void putNotificationIsRead(String notificationId, RestCallback<ApiResponse> callback){
        String token = "";
        try {
            token = Session.get("token").getValue();
        } catch (SessionNotFoundException e) {
            e.printStackTrace();
        }
        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().putNotificationIsRead(notificationId, token).enqueue(callback);
    }
}
