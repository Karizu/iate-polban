package id.bl.blcom.iate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.PopupMenu;

import com.rezkyatinnov.kyandroid.localdata.LocalData;
import com.rezkyatinnov.kyandroid.reztrofit.ErrorResponse;
import com.rezkyatinnov.kyandroid.reztrofit.RestCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.ButterKnife;
import id.bl.blcom.iate.api.MemberHelper;
import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.models.MemberProfile;
import id.bl.blcom.iate.models.Profile;
import id.bl.blcom.iate.models.response.MemberDataResponse;
import id.bl.blcom.iate.presentation.LoadingDialog;
import id.bl.blcom.iate.services.AdapterListMenuDirectory;
import id.bl.blcom.iate.services.EndlessRecyclerViewScrollListener;
import id.bl.blcom.iate.services.GpsTracker;
import io.realm.Realm;
import okhttp3.Headers;

public class NearMeActivity extends AppCompatActivity {

    private int limit = 5;
    private int offset = 0;
    private EndlessRecyclerViewScrollListener scrollListener;
    private Context activity;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeContainer;
    private LinearLayoutManager layoutManager;
    private AdapterListMenuDirectory mAdapter;
    private List<MemberProfile> mProfiles;
    private String groupId;
    private PopupMenu popupMenu;
    private List<String> groupIds;
    private GpsTracker gpsTracker;
    private double longitude, latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_me);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        swipeContainer = findViewById(R.id.swipeContainer);

        Realm realm = LocalData.getRealm();
        Profile profile = realm.where(Profile.class).findFirst();

        activity = this;
        layoutManager = new LinearLayoutManager(activity,
                LinearLayoutManager.VERTICAL,
                false);

        recyclerView = findViewById(R.id.member_directory_recycler_view);
        recyclerView.setLayoutManager(layoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
//                loadNextDataFromApi(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        recyclerView.addOnScrollListener(scrollListener);

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        swipeContainer.setOnRefreshListener(() -> {
            // Your code to refresh the list here.
            // Make sure you call swipeContainer.setRefreshing(false)
            // once the network request has completed successfully.
            populateDataMemberDirectory(true, latitude, longitude);
        });

        swipeContainer.setColorSchemeResources(
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        getLocation();
        populateDataMemberDirectory(false, latitude, longitude);
    }

    public void getLocation(){
        gpsTracker = new GpsTracker(NearMeActivity.this);
        if(gpsTracker.canGetLocation()){
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
        } else {
            gpsTracker.showSettingsAlert();
        }


        Log.d("latitude", latitude + "");
        Log.d("longitude", longitude + "");
    }

    private void populateDataMemberDirectory(boolean onRefresh, double latitude, double longitude){
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.setCancelable(false);
        loadingDialog.show();
        try {
            if (onRefresh){
                MemberHelper.getMemberNearMe(latitude + "", longitude + "", new RestCallback<ApiResponse<List<MemberProfile>>>() {
                    @Override
                    public void onSuccess(Headers headers, ApiResponse<List<MemberProfile>> body) {
                        loadingDialog.dismiss();
                        try {
                            if (body != null && body.isStatus()) {
                                List<MemberProfile> data = body.getData();
                                mProfiles.clear();

                                for (int i = 0; i < data.size(); i++){
                                    MemberProfile profile = data.get(i);
                                    profile.setFragmentManager(getSupportFragmentManager());

                                    mProfiles.add(profile);
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
                        loadingDialog.dismiss();
                        Log.d("populate Directory", "Failed");
                    }

                    @Override
                    public void onCanceled() {
                        Log.d("populate Directory", "Cancel");
                    }
                });
            } else {
                MemberHelper.getMemberNearMe(latitude + "", longitude + "", new RestCallback<ApiResponse<List<MemberProfile>>>() {
                    @Override
                    public void onSuccess(Headers headers, ApiResponse<List<MemberProfile>> body) {
                        loadingDialog.dismiss();
                        try {
                            if (body != null && body.isStatus()) {
                                List<MemberProfile> data = body.getData();
                                mProfiles = new ArrayList<>();
                                for (int i = 0; i < data.size(); i++){
                                    MemberProfile profile = data.get(i);
                                    profile.setFragmentManager(getSupportFragmentManager());

                                    mProfiles.add(profile);
                                }

                                mAdapter = new AdapterListMenuDirectory(mProfiles, activity, true);
                                recyclerView.setAdapter(mAdapter);
                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onFailed(ErrorResponse error) {
                        loadingDialog.dismiss();
                        Log.d("populate Directory", "Failed");
                    }

                    @Override
                    public void onCanceled() {
                        Log.d("populate Directory", "Cancel");
                    }
                });
            }
        } catch (Exception e) {

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}