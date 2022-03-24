package id.bl.blcom.iate.api;

import com.rezkyatinnov.kyandroid.reztrofit.RestCallback;
import com.rezkyatinnov.kyandroid.reztrofit.Reztrofit;
import com.rezkyatinnov.kyandroid.session.Session;
import com.rezkyatinnov.kyandroid.session.SessionNotFoundException;

import java.util.List;

import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.models.CheckInEvent;
import id.bl.blcom.iate.models.Media;
import id.bl.blcom.iate.models.response.EventCalendarResponse;
import id.bl.blcom.iate.models.response.EventDataResponse;

public class EventHelper {
    public static void getAllEvent(RestCallback<ApiResponse<List<EventDataResponse>>> callback){
        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().getAllEvents().enqueue(callback);
    }
    public static void getEvent(String eventId, RestCallback<ApiResponse<EventDataResponse>> callback){
        String token = "";
        try {
            token = Session.get("token").getValue();
        } catch (SessionNotFoundException e) {
            e.printStackTrace();
        }

        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().getEvent(eventId, token).enqueue(callback);
    }
    public static void getEvents(int year, int month, RestCallback<ApiResponse<List<EventCalendarResponse>>> callback){
        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().getEvents(year,month, 1).enqueue(callback);
    }
    public static void getEventMedia(String eventId, RestCallback<ApiResponse<List<Media>>> callback){
        String token = "";
        try {
            token = Session.get("token").getValue();
        } catch (SessionNotFoundException e) {
            e.printStackTrace();
        }

        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().getEventMedia(eventId, token).enqueue(callback);
    }
    public static void approveEventInvitation(CheckInEvent checkInEvent, RestCallback<ApiResponse> callback){
        String token = "";
        try {
            token = Session.get("token").getValue();
        } catch (SessionNotFoundException e) {
            e.printStackTrace();
        }

        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().approveEventInvitation(checkInEvent, token).enqueue(callback);
    }
    public static void checkInEvent(CheckInEvent checkInEvent, RestCallback<ApiResponse> callback){
        String token = "";
        try {
            token = Session.get("token").getValue();
        } catch (SessionNotFoundException e) {
            e.printStackTrace();
        }
        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().checkInEvent(checkInEvent, token).enqueue(callback);
    }
    public static void getEventsFilter(int year, int month, String groupId, RestCallback<ApiResponse<List<EventCalendarResponse>>> callback){
        String token = "";
        try {
            token = Session.get("token").getValue();
        } catch (SessionNotFoundException e) {
            e.printStackTrace();
        }
        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().getEventsFilter(year,month, 1, groupId, token).enqueue(callback);
    }
}
