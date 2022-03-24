package id.bl.blcom.iate.presentation.notifikasi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.rezkyatinnov.kyandroid.localdata.LocalData;
import com.rezkyatinnov.kyandroid.reztrofit.ErrorResponse;
import com.rezkyatinnov.kyandroid.reztrofit.RestCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.bl.blcom.iate.R;
import id.bl.blcom.iate.api.NotificationHelper;
import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.models.Notification;
import id.bl.blcom.iate.models.User;
import id.bl.blcom.iate.presentation.NavigationDrawerActivity;
import id.bl.blcom.iate.services.AdapterListMenuNotification;
import id.bl.blcom.iate.services.EndlessRecyclerViewScrollListener;
import io.realm.Realm;
import okhttp3.Headers;

public class NotifikasiActivity extends AppCompatActivity {

    private AdapterListMenuNotification mAdapter;
    private List<Notification> mNotification;
    private int limit = 5;
    private int offset = 0;
    private EndlessRecyclerViewScrollListener scrollListener;
    private Context activity;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeContainer;
    private LinearLayoutManager layoutManager;

    private User user;

    @BindView(R.id.tvNoData)
    TextView tvNoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifikasi);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setRefreshing(true);

        activity = this;
        layoutManager = new LinearLayoutManager(activity,
                LinearLayoutManager.VERTICAL,
                false);

        recyclerView = findViewById(R.id.rv_notification);
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

            populateDataNotif(true);
        });

        swipeContainer.setColorSchemeResources(
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @Override
    public void onStart() {
        super.onStart();
        Realm realm = LocalData.getRealm();
        user = realm.where(User.class).findFirst();

        populateDataNotif(false);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void populateDataNotif(boolean onRefresh) {

        if (onRefresh) {
            try{
                NotificationHelper.getAllNotification(user.getId(), limit, offset, new RestCallback<ApiResponse<List<Notification>>>() {
                    @Override
                    public void onSuccess(Headers headers, ApiResponse<List<Notification>> body) {
                        try {
                            if (body != null && body.isStatus()) {
                                List<Notification> data = body.getData();
                                mNotification.clear();

                                for (int i = 0; i < data.size(); i++){
                                    Notification notification = new Notification();
                                    notification.setId(data.get(i).getId());
                                    notification.setTypeId(data.get(i).getTypeId());
                                    notification.setType(data.get(i).getType());
                                    notification.setTitle(data.get(i).getTitle());
                                    notification.setContent(data.get(i).getContent());
                                    notification.setRead(data.get(i).getRead());
                                    notification.setCreatedAt(data.get(i).getCreatedAt());
                                    notification.setFragmentManager(getSupportFragmentManager());

                                    mNotification.add(notification);
                                }

                                mAdapter.notifyDataSetChanged(); // or notifyItemRangeRemoved
                                scrollListener.resetState();

                                swipeContainer.setRefreshing(false);

                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onFailed(ErrorResponse error) {

                    }

                    @Override
                    public void onCanceled() {

                    }
                });
            } catch (Exception e){

            }
        } else {
            try{
                NotificationHelper.getAllNotification(user.getId(), limit, offset, new RestCallback<ApiResponse<List<Notification>>>() {
                    @Override
                    public void onSuccess(Headers headers, ApiResponse<List<Notification>> body) {
                        try {
                            if (body != null && body.isStatus()) {
                                List<Notification> data = body.getData();
                                mNotification = new ArrayList<>();
                                for (int i = 0; i < data.size(); i++){
                                    Notification notification = new Notification();
                                    notification.setId(data.get(i).getId());
                                    notification.setTypeId(data.get(i).getTypeId());
                                    notification.setType(data.get(i).getType());
                                    notification.setTitle(data.get(i).getTitle());
                                    notification.setContent(data.get(i).getContent());
                                    notification.setRead(data.get(i).getRead());
                                    notification.setCreatedAt(data.get(i).getCreatedAt());
                                    notification.setFragmentManager(getSupportFragmentManager());

                                    mNotification.add(notification);
                                }

                                try {
                                    if (data.size()==0) {
                                        tvNoData.setVisibility(View.VISIBLE);
                                    } else {
                                        tvNoData.setVisibility(View.GONE);
                                    }
                                } catch (Exception e){
                                    e.printStackTrace();
                                }


                                mAdapter = new AdapterListMenuNotification(mNotification, activity);
                                recyclerView.setAdapter(mAdapter);
                                swipeContainer.setRefreshing(false);

                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onFailed(ErrorResponse error) {

                    }

                    @Override
                    public void onCanceled() {

                    }
                });
            } catch (Exception e){

            }
        }
    }

    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`

        try{
            NotificationHelper.getAllNotification(user.getId(), limit, limit*offset, new RestCallback<ApiResponse<List<Notification>>>() {
                @Override
                public void onSuccess(Headers headers, ApiResponse<List<Notification>> body) {
                    try {
                        if (body != null && body.isStatus()) {
                            List<Notification> data = body.getData();
                            List<Notification> listNotification = new ArrayList<>();
                            for (int i = 0; i < data.size(); i++){
                                Notification notification = new Notification();
                                notification.setId(data.get(i).getId());
                                notification.setTypeId(data.get(i).getTypeId());
                                notification.setType(data.get(i).getType());
                                notification.setTitle(data.get(i).getTitle());
                                notification.setContent(data.get(i).getContent());
                                notification.setRead(data.get(i).getRead());
                                notification.setCreatedAt(data.get(i).getCreatedAt());
                                notification.setFragmentManager(getSupportFragmentManager());

                                listNotification.add(notification);
                            }

                            mNotification.addAll(listNotification);
                            mAdapter.notifyDataSetChanged();

                        }
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onFailed(ErrorResponse error) {

                }

                @Override
                public void onCanceled() {

                }
            });
        } catch (Exception e){

        }
    }
}