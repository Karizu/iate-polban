package id.bl.blcom.iate.api;

import com.rezkyatinnov.kyandroid.reztrofit.RestCallback;
import com.rezkyatinnov.kyandroid.reztrofit.Reztrofit;
import com.rezkyatinnov.kyandroid.session.Session;
import com.rezkyatinnov.kyandroid.session.SessionNotFoundException;

import java.util.List;

import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.models.PostPolling;
import id.bl.blcom.iate.models.ReportPost;
import id.bl.blcom.iate.models.response.DiscussionDataResponse;
import id.bl.blcom.iate.models.response.ThreadDataResponse;
import okhttp3.RequestBody;
import retrofit2.Callback;

public class DiscussionHelper {

    public static void getDiscussionList(String groupId, int limit, int offset, RestCallback<ApiResponse<List<DiscussionDataResponse>>> callback){
        String token = "";
        try {
            token = Session.get("token").getValue();
        } catch (SessionNotFoundException e) {
            e.printStackTrace();
        }
        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().getDiscussionList(groupId, limit, offset, token).enqueue(callback);
    }

    public static void doLikePost(RequestBody discussion, String id, RestCallback<ApiResponse> callback){
        String token = "";
        try {
            token = Session.get("token").getValue();
        } catch (SessionNotFoundException e) {
            e.printStackTrace();
        }
        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().doLikePost(discussion, id, token).enqueue(callback);
    }

    public static void getDiscussionListAll(int limit, int offset, RestCallback<ApiResponse<List<DiscussionDataResponse>>> callback){
        String token = "";
        try {
            token = Session.get("token").getValue();
        } catch (SessionNotFoundException e) {
            e.printStackTrace();
        }
        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().getDiscussionListAll(limit, offset, token).enqueue(callback);
    }

    public static void postVoting(PostPolling polling, RestCallback<ApiResponse> callback){
        String token = "";
        try {
            token = Session.get("token").getValue();
        } catch (SessionNotFoundException e) {
            e.printStackTrace();
        }
        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().postPolling(polling, token).enqueue(callback);
    }

    public static void getDiscussionDetail(String postId, RestCallback<ApiResponse<DiscussionDataResponse>> callback){
        String token = "";
        try {
            token = Session.get("token").getValue();
        } catch (SessionNotFoundException e) {
            e.printStackTrace();
        }
        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().getDiscussionDetail(postId, token).enqueue(callback);
    }
    public static void getThreadList(String postId, RestCallback<ApiResponse<List<ThreadDataResponse>>> callback){
        String token = "";
        try {
            token = Session.get("token").getValue();
        } catch (SessionNotFoundException e) {
            e.printStackTrace();
        }
        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().getThreadList(postId, "asc", token).enqueue(callback);
    }
    public static void createThread(String postId, String userId, String comment, RestCallback<ApiResponse> callback){
        String token = "";
        try {
            token = Session.get("token").getValue();
        } catch (SessionNotFoundException e) {
            e.printStackTrace();
        }
        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().createThread(postId, userId, comment, "1", token).enqueue(callback);
    }

    public static void createPost(RequestBody discussion, Callback<ApiResponse> callback){
        String token = "";
        try {
            token = Session.get("token").getValue();
        } catch (SessionNotFoundException e) {
            e.printStackTrace();
        }
        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().postDiscussion(discussion, token).enqueue(callback);
    }

    public static void createReportPost(ReportPost reportPost, RestCallback<ApiResponse> callback){
        String token = "";
        try {
            token = Session.get("token").getValue();
        } catch (SessionNotFoundException e) {
            e.printStackTrace();
        }
        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().createReportPost(reportPost, token).enqueue(callback);
    }

    public static void deletePost(String postId, RestCallback<ApiResponse> callback){
        String token = "";
        try {
            token = Session.get("token").getValue();
        } catch (SessionNotFoundException e) {
            e.printStackTrace();
        }
        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().deletePost(postId,token).enqueue(callback);
    }

    public static void createThreadWithImage(RequestBody requestBody, RestCallback<ApiResponse> callback) {
        String token = "";
        try {
            token = Session.get("token").getValue();
        } catch (SessionNotFoundException e) {
            e.printStackTrace();
        }
        Reztrofit<ApiInterface> service = Reztrofit.getInstance();
        service.getEndpoint().createThreadWithImage(requestBody, token).enqueue(callback);
    }
}
