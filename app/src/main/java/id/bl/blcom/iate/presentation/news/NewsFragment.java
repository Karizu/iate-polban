package id.bl.blcom.iate.presentation.news;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rezkyatinnov.kyandroid.reztrofit.ErrorResponse;
import com.rezkyatinnov.kyandroid.reztrofit.RestCallback;

import java.util.ArrayList;
import java.util.List;

import id.bl.blcom.iate.R;
import id.bl.blcom.iate.api.NewsHelper;
import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.models.ArticleModel;
import id.bl.blcom.iate.models.response.ArticleDataResponse;
import id.bl.blcom.iate.services.AdapterListMenuHome;
import id.bl.blcom.iate.services.EndlessRecyclerViewScrollListener;
import okhttp3.Headers;

public class NewsFragment extends Fragment{

    private int limit = 5;
    private int offset = 0;
    private EndlessRecyclerViewScrollListener scrollListener;
    private Context activity;
    private RecyclerView recyclerView;
    private List<ArticleModel> articleModels;
    private RecyclerView.Adapter adapter;
    private SwipeRefreshLayout swipeContainer;
    private LinearLayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeContainer = view.findViewById(R.id.swipeContainer);
        swipeContainer.setRefreshing(true);

        activity = getActivity();
        layoutManager = new LinearLayoutManager(activity,
                LinearLayoutManager.VERTICAL,
                false);

        recyclerView = getView().findViewById(R.id.news_recycle_view);
        recyclerView.setLayoutManager(layoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        recyclerView.addOnScrollListener(scrollListener);

        swipeContainer.setOnRefreshListener(() -> {
            // Your code to refresh the list here.
            // Make sure you call swipeContainer.setRefreshing(false)
            // once the network request has completed successfully.

            populateData(true);
        });

        swipeContainer.setColorSchemeResources(
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        populateData(false);
    }

    private void populateData(boolean onRefresh){
        try {
            if (onRefresh){
                NewsHelper.getNews(limit, offset, new RestCallback<ApiResponse<List<ArticleDataResponse>>>() {
                    @Override
                    public void onSuccess(Headers headers, ApiResponse<List<ArticleDataResponse>> body) {
                        try {
                            List<ArticleDataResponse> res = body.getData();
                            articleModels.clear();

                            for (int i = 0; i < res.size(); i++){
                                ArticleDataResponse article = res.get(i);
                                articleModels.add(new ArticleModel(article.getId(),
                                        article.getBanner(),
                                        article.getTitle(),
                                        article.getHeadline(),
                                        article.getContent(),
                                        article.getCreated_at(),
                                        article.getCreator(),
                                        article.getUser(),
                                        getFragmentManager(),
                                        null));
                            }

                            adapter.notifyDataSetChanged(); // or notifyItemRangeRemoved
                            scrollListener.resetState();

                            swipeContainer.setRefreshing(false);
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onFailed(ErrorResponse error) {
                        Log.i("response", "Response Failed");
                    }

                    @Override
                    public void onCanceled() {
                        Log.i("response", "Response Failed");
                    }
                });
            } else {
                NewsHelper.getNews(limit, offset, new RestCallback<ApiResponse<List<ArticleDataResponse>>>() {
                    @Override
                    public void onSuccess(Headers headers, ApiResponse<List<ArticleDataResponse>> body) {
                        try {
                            List<ArticleDataResponse> res = body.getData();
                            articleModels = new ArrayList<>();
                            for (int i = 0; i < res.size(); i++){
                                ArticleDataResponse article = res.get(i);
                                articleModels.add(new ArticleModel(article.getId(),
                                        article.getBanner(),
                                        article.getTitle(),
                                        article.getHeadline(),
                                        article.getContent(),
                                        article.getUpdated_at(),
                                        article.getCreator(),
                                        article.getUser(),
                                        getFragmentManager(),
                                        null));
                            }

                            adapter = new AdapterListMenuHome(articleModels, activity);
                            recyclerView.setAdapter(adapter);
                            swipeContainer.setRefreshing(false);
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onFailed(ErrorResponse error) {
                        Log.i("response", "Response Failed");
                    }

                    @Override
                    public void onCanceled() {
                        Log.i("response", "Response Failed");
                    }
                });
            }
        } catch (Exception e) {

        }
    }

    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
        try {
            NewsHelper.getNews(limit, limit*offset, new RestCallback<ApiResponse<List<ArticleDataResponse>>>() {
                @Override
                public void onSuccess(Headers headers, ApiResponse<List<ArticleDataResponse>> body) {
                    try {
                        if (body != null && body.isStatus()) {
                            List<ArticleDataResponse> res = body.getData();
                            List<ArticleModel> newArticles = new ArrayList<>();
                            for (int i = 0; i < res.size(); i++) {
                                ArticleDataResponse article = res.get(i);
                                newArticles.add(new ArticleModel(article.getId(),
                                        article.getBanner(),
                                        article.getTitle(),
                                        article.getHeadline(),
                                        article.getContent(),
                                        article.getCreated_at(),
                                        article.getCreator(),
                                        article.getUser(),
                                        getFragmentManager(),
                                        null));
                            }

                            articleModels.addAll(newArticles);
                            adapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onFailed(ErrorResponse error) {
                    Log.i("response", "Response Failed");
                }

                @Override
                public void onCanceled() {
                    Log.i("response", "Response Failed");
                }
            });
        } catch (Exception e) {

        }
    }

}
