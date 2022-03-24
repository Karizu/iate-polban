package id.bl.blcom.iate.api;

import java.util.List;

import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.models.CheckInEvent;
import id.bl.blcom.iate.models.GeneralDataProfile;
import id.bl.blcom.iate.models.Group;
import id.bl.blcom.iate.models.Interest;
import id.bl.blcom.iate.models.Media;
import id.bl.blcom.iate.models.MemberProfile;
import id.bl.blcom.iate.models.Notification;
import id.bl.blcom.iate.models.Password;
import id.bl.blcom.iate.models.PostPolling;
import id.bl.blcom.iate.models.Profile;
import id.bl.blcom.iate.models.Register;
import id.bl.blcom.iate.models.ReportPost;
import id.bl.blcom.iate.models.UserDevice;
import id.bl.blcom.iate.models.response.AddressDatum;
import id.bl.blcom.iate.models.response.DiscussionDataResponse;
import id.bl.blcom.iate.models.response.EventCalendarResponse;
import id.bl.blcom.iate.models.User;

import id.bl.blcom.iate.models.response.AreaDataResponse;
import id.bl.blcom.iate.models.response.ArticleDataResponse;
import id.bl.blcom.iate.models.response.EventDataResponse;
import id.bl.blcom.iate.models.response.HomeDataResponse;
import id.bl.blcom.iate.models.response.MemberDataResponse;
import id.bl.blcom.iate.models.response.ThreadDataResponse;
import okhttp3.RequestBody;
import retrofit2.Call;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

//    String BASE_URL = "http://superfriends-api.ap-southeast-1.elasticbeanstalk.com/api/";
    String BASE_URL = " http://iate.boardinglabs.id/api/api/";
    String BASE_URL_JSON = " https://seladaid.github.io/api-wilayah-indonesia/api/";
    String BASE_URL_IMAGE = "http://iate.boardinglabs.id";

    @POST("login")
    Call<ApiResponse<User>> login(@Body User user);

    @GET("news")
    Call<ApiResponse<List<ArticleDataResponse>>> getNews(@Query("limit") int limit, @Query("offset") int offset, @Query("status") int status, @Query("token") String token);

    @GET("article")
    Call<ApiResponse<List<ArticleDataResponse>>> getArticle(@Query("limit") int limit, @Query("offset") int offset, @Query("status") int status, @Query("token") String token);

    @GET("article/{id}")
    Call<ApiResponse<List<ArticleDataResponse>>> getArticle(@Path("id") String id);

    @GET("event")
    Call<ApiResponse<List<ArticleDataResponse>>> getEvent();

    @POST("eventInvitationAction")
    Call<ApiResponse> approveEventInvitation(@Body CheckInEvent checkInEvent, @Query("token") String token);

    @POST("eventInvitation/checkin")
    Call<ApiResponse> checkInEvent(@Body CheckInEvent checkInEvent, @Query("token") String token);

    @GET("home")
    Call<ApiResponse<HomeDataResponse>> getHome(@Query("limit") int limit, @Query("offset") int offset, @Query("token") String token);

    @GET("member")
    Call<ApiResponse<List<MemberDataResponse>>> getMember(@Query("limit") int limit, @Query("offset") int offset, @Query("status") int status, @Query("group_id") String groupId, @Query("token") String token);

    @GET("member")
    Call<ApiResponse<List<MemberDataResponse>>> getMemberNoLimit(@Query("status") int status, @Query("group_id") String groupId, @Query("token") String token);

    @GET("member/nearme")
    Call<ApiResponse<List<MemberProfile>>> getMemberNearMe(@Query("status") int status, @Query("latitude") String latitude, @Query("longitude") String longitude, @Query("token") String token);

    @GET("user/{user_id}")
    Call<ApiResponse<GeneralDataProfile>> getProfileDetail(@Path("user_id") String user_id, @Query("token") String token);

    @GET("user")
    Call<ApiResponse<List<User>>> getUser(@Query("limit") int limit, @Query("token") String token);

    @GET("area")
    Call<ApiResponse<List<AreaDataResponse>>> getArea();

    @GET("event")
    Call<ApiResponse<List<EventDataResponse>>> getAllEvents();

    @GET("event/list")
    Call<ApiResponse<List<EventCalendarResponse>>> getEvents(@Query("year") int year, @Query("month") int month, @Query("status") int status);

    @GET("event/list")
    Call<ApiResponse<List<EventCalendarResponse>>> getEventsFilter(@Query("year") int year, @Query("month") int month, @Query("status") int status, @Query("group_id") String groupId, @Query("token") String token);

    @GET("event/{id}")
    Call<ApiResponse<EventDataResponse>> getEvent(@Path("id") String id, @Query("token") String token);

    @GET("eventMedia/{id}")
    Call<ApiResponse<List<Media>>> getEventMedia(@Path("id") String id, @Query("token") String token);

    @POST("register")
    Call<ApiResponse> postRegister(@Body Register register);

    @GET("group")
    Call<ApiResponse<List<Group>>> getGroup(@Query("token") String token);

    @GET("interest")
    Call<ApiResponse<List<Interest>>> getInterest(@Query("token") String token);

    @POST("forgot")
    Call<ApiResponse<User>> forgotPassword(@Body User user);

    @POST("recover")
    Call<ApiResponse> recoverPassword(@Body RequestBody requestBody);

    @POST("updateProfile")
    Call<ApiResponse<Profile>> updateProfile(@Body RequestBody profile, @Query("token") String token);

    @GET("notification")
    Call<ApiResponse<List<Notification>>> getNotification(@Query("user_id") String user_id, @Query("limit") int limit, @Query("offset") int offset, @Query("token") String token);

    @GET("notification")
    Call<ApiResponse<List<Notification>>> getCountNotification(@Query("user_id") String user_id, @Query("is_read") boolean is_read, @Query("token") String token);

    @PUT("notification/{id}")
    Call<ApiResponse> putNotificationIsRead(@Path("id") String notificationId, @Query("token") String token);

    @GET("profile")
    Call<ApiResponse<GeneralDataProfile>> getProfile(@Query("token") String token);

    @POST("userDevice")
    Call<ApiResponse> registerUserDevice(@Body UserDevice userDevice, @Query("token") String token);

    @POST("user/{userId}/changePassword")
    Call<ApiResponse> changePassword(@Path("userId") String userId, @Body Password password, @Query("token") String token);

    @GET("post")
    Call<ApiResponse<List<DiscussionDataResponse>>> getDiscussionList(@Query("group_id") String groupId, @Query("limit") int limit, @Query("offset") int offset, @Query("token") String token);

    @GET("post")
    Call<ApiResponse<List<DiscussionDataResponse>>> getDiscussionListAll(@Query("limit") int limit, @Query("offset") int offset, @Query("token") String token);

    @POST("post/{id}/love")
    Call<ApiResponse> doLikePost(@Body RequestBody discussion, @Path("id") String id, @Query("token") String token);

    @POST("post/store")
    Call<ApiResponse> postDiscussion(@Body RequestBody discussion, @Query("token") String token);

    @POST("post/polling")
    Call<ApiResponse> postPolling(@Body PostPolling polling, @Query("token") String token);

    @GET("post/{post_id}")
    Call<ApiResponse<DiscussionDataResponse>> getDiscussionDetail(@Path("post_id") String postId, @Query("token") String token);

    @GET("thread")
    Call<ApiResponse<List<ThreadDataResponse>>> getThreadList(@Query("post_id") String postId, @Query("order_type") String orderType, @Query("token") String token);

    @POST("thread")
    Call<ApiResponse> createThread(@Query("post_id") String postId, @Query("user_id") String userId, @Query("comment") String comment, @Query("type") String type, @Query("token") String token);

    @POST("thread")
    Call<ApiResponse> createThreadWithImage(@Body RequestBody requestBody, @Query("token") String token);

    @POST("postReport")
    Call<ApiResponse> createReportPost(@Body ReportPost reportPost, @Query("token") String token);

    @DELETE("post/{post_id}")
    Call<ApiResponse> deletePost(@Path("post_id") String postId, @Query("token") String token);

    @GET("provinces")
    Call<List<AddressDatum>> getProvince();

    @GET("regencies/{id}.json")
    Call<List<AddressDatum>> getRegencies(@Path("id") String id);

    @GET("districts/{id}.json")
    Call<List<AddressDatum>> getDistricts(@Path("id") String id);

    @GET("villages/{id}.json")
    Call<List<AddressDatum>> getVillages(@Path("id") String id);

}
