package id.bl.blcom.iate.presentation.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.rezkyatinnov.kyandroid.localdata.LocalData;
import com.rezkyatinnov.kyandroid.reztrofit.ErrorResponse;
import com.rezkyatinnov.kyandroid.reztrofit.RestCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.bl.blcom.iate.R;
import id.bl.blcom.iate.api.ApiInterface;
import id.bl.blcom.iate.api.EventHelper;
import id.bl.blcom.iate.api.HomeHelper;
import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.models.ArticleModel;
import id.bl.blcom.iate.models.CheckInEvent;
import id.bl.blcom.iate.models.Profile;
import id.bl.blcom.iate.models.response.EventDataResponse;
import id.bl.blcom.iate.models.response.GeneralDataResponse;
import id.bl.blcom.iate.models.response.HomeDataResponse;
import id.bl.blcom.iate.presentation.article.ArticleDetailFragment;
import id.bl.blcom.iate.presentation.event.EventActivity;
import id.bl.blcom.iate.presentation.news.NewsDetailFragment;
import id.bl.blcom.iate.services.AdapterListMenuHome;
import id.bl.blcom.iate.services.AdapterSliderPager;
import id.bl.blcom.iate.services.EndlessRecyclerViewScrollListener;
import io.realm.Realm;
import okhttp3.Headers;
import ss.com.bannerslider.Slider;

import static id.bl.blcom.iate.services.AdapterListMenuHome.dateFormater;

public class HomeFragment extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 1;
    @BindView(R.id.banner_slider)
    Slider sliderView;
    @BindView(R.id.pagesContainer)
    LinearLayout linearLayout;
    @BindView(R.id.popup_event)
    FrameLayout frameLayout;
    @Nullable
    @BindView(R.id.home_popup_title_event)
    TextView txtTitleEvent;
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;
    @BindView(R.id.btnLoadMore)
    Button btnLoadMore;

    private int limit = 5;
    private int offset = 0;
    private EndlessRecyclerViewScrollListener scrollListener;
    private AdapterSliderPager mAdapter;
    ImageView closeImageView;
    Button eventDetail;
    private static int index = 0;
    private Context activity;
    private LinearLayoutManager layoutManager;
    private AdapterListMenuHome adapter;
    private List<ArticleModel> articleModels;
    private int mCounter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        activity = getActivity();
        articleModels = new ArrayList<>();
        swipeContainer.setRefreshing(true);
        swipeContainer.setOnRefreshListener(() -> {
            swipeContainer.setRefreshing(true);
            articleModels.clear();
            populateData();
        });

        swipeContainer.setColorSchemeResources(
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        populateData();

        RelativeLayout imgNotification = requireActivity().findViewById(R.id.img_notification_toolbar);
        imgNotification.setVisibility(View.VISIBLE);

        ImageView imgShare = getActivity().findViewById(R.id.img_share_toolbar);
        imgShare.setVisibility(View.GONE);

        checkPermissionGrant();
    }

    private void checkPermissionGrant(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void populateDataNews(List<GeneralDataResponse> res, Boolean isLoadMore, int counter) {
        List<ArticleModel> modelList = new ArrayList<>();

        if (isLoadMore) {
            mCounter += counter;
            btnLoadMore.setVisibility(View.GONE);
            for (int i = 0; i < res.size(); i++) {
                GeneralDataResponse article = res.get(i);
                modelList.add(new ArticleModel(article.getId(),
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
            articleModels.addAll(modelList);
        } else {
            mCounter = 0;
            for (int i = 0; i < res.size(); i++) {
                GeneralDataResponse article = res.get(i);
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
        }

        Context activity = getActivity();
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity,
                LinearLayoutManager.VERTICAL,
                false);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                Log.d("PAGE", page + "");
                page = page + mCounter;
                if (mCounter >= 4 ){
                    btnLoadMore.setVisibility(View.GONE);
                } else {
                    btnLoadMore.setVisibility(View.VISIBLE);
                    int finalPage = page;
                    btnLoadMore.setOnClickListener(v -> loadNextDataFromApi(finalPage));
                }
            }
        };

        RecyclerView recyclerView = requireView().findViewById(R.id.home_recycle_view);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(scrollListener);
        adapter = new AdapterListMenuHome(articleModels, activity);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    public void loadNextDataFromApi(int offset) {
        try {
            swipeContainer.setRefreshing(true);
            HomeHelper.getHome(limit, limit * offset, new RestCallback<ApiResponse<HomeDataResponse>>() {
                @Override
                public void onSuccess(Headers headers, ApiResponse<HomeDataResponse> body) {
                    try {
                        swipeContainer.setRefreshing(false);
                        if (body != null && body.isStatus()) {
//                            populateImageSlider(body.getData().getBanner());
                            populateDataNews(body.getData().getNews(), true, 1);
//                            showPopupEvent(body.getData().getEvent());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailed(ErrorResponse error) {
                    swipeContainer.setRefreshing(false);
                }

                @Override
                public void onCanceled() {
                    swipeContainer.setRefreshing(false);
                }
            });
        } catch (Exception e) {
            swipeContainer.setRefreshing(false);
        }
    }

    private void populateImageSlider(List<GeneralDataResponse> data) {
        sliderView.setAdapter(new AdapterSliderPager(data));
        sliderView.setLoopSlides(true);
        sliderView.setInterval(3000);
        sliderView.setOnSlideClickListener(position -> {
            Bundle bundle = new Bundle();

            final String createAt = dateFormater(data.get(position).getCreated_at(), "dd MMMM yyyy", "yyyy-MM-dd HH:mm:ss");
            final String creator = "-";

            switch (data.get(position).getType()) {

                case "news":
                    bundle.putString("pageTitle", "BERITA/PENGUMUMAN");
                    bundle.putString("imgUrl", ApiInterface.BASE_URL_IMAGE + data.get(position).getBanner());
                    bundle.putString("title", data.get(position).getTitle());
                    bundle.putString("detailArticle", data.get(position).getContent());
                    bundle.putString("creator", createAt + " - Oleh " + creator);

                    Fragment fragmentNews = new NewsDetailFragment();
                    fragmentNews.setArguments(bundle);
                    FragmentManager fragmentManagerNews = getFragmentManager();
                    FragmentTransaction fragmentTransactionNews = fragmentManagerNews.beginTransaction();

                    fragmentTransactionNews.replace(R.id.screen_area, fragmentNews, "News list");
                    fragmentTransactionNews.addToBackStack("News list");

                    fragmentTransactionNews.commit();

                    break;

                case "event":
                    Intent myIntent = new Intent(getActivity(), EventActivity.class);
                    myIntent.putExtra("EVENT_ID", data.get(position).getId());
                    startActivity(myIntent);

                    break;

                case "article":
                    bundle.putString("pageTitle", "ARTIKEL");
                    bundle.putString("imgUrl", data.get(position).getBanner());
                    bundle.putString("title", data.get(position).getTitle());
                    bundle.putString("detailArticle", data.get(position).getContent());
                    bundle.putString("creator", createAt + " - Oleh " + creator);

                    Fragment fragmentArticle = new ArticleDetailFragment();
                    fragmentArticle.setArguments(bundle);
                    FragmentManager fragmentManagerArticle = getFragmentManager();
                    FragmentTransaction fragmentTransactionArticle = fragmentManagerArticle.beginTransaction();

                    fragmentTransactionArticle.replace(R.id.screen_area, fragmentArticle, "Article list");
                    fragmentTransactionArticle.addToBackStack("Article list");

                    fragmentTransactionArticle.commit();

                    break;
            }
        });
    }

    private void populateData() {
        try {
            HomeHelper.getHome(limit, offset, new RestCallback<ApiResponse<HomeDataResponse>>() {
                @Override
                public void onSuccess(Headers headers, ApiResponse<HomeDataResponse> body) {
                    try {
                        swipeContainer.setRefreshing(false);
                        if (body != null && body.isStatus()) {
                            if (body.getData().getBanner().size()>0) populateImageSlider(body.getData().getBanner());
                            if (body.getData().getNews().size()>0) populateDataNews(body.getData().getNews(), false, 0);
                            if (body.getData().getEvent().size()>0) showPopupEvent(body.getData().getEvent());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailed(ErrorResponse error) {
                    swipeContainer.setRefreshing(false);
                }

                @Override
                public void onCanceled() {
                    swipeContainer.setRefreshing(false);
                }
            });
        } catch (Exception e) {
            swipeContainer.setRefreshing(false);
        }
    }

    public void showPopupEvent(List<EventDataResponse> data) {
        final View homePopupView = getLayoutInflater().inflate(R.layout.home_popup, null);

        closeImageView = homePopupView.findViewById(R.id.close_image);
        frameLayout.addView(homePopupView);
        frameLayout.setOnClickListener(view -> {
            // do nothing
        });
        closeImageView.setOnClickListener(view -> frameLayout.setVisibility(View.INVISIBLE));
        frameLayout.setVisibility(View.VISIBLE);
        frameLayout.bringToFront();
        populateEventInHomePopup(homePopupView, data);
    }

    @SuppressLint("SetTextI18n")
    private void populateEventInHomePopup(View viewPopup, List<EventDataResponse> data) {
        final int sizeData = data.size();

        Log.d("sizeData", "Before sizeData " + sizeData);
        if (sizeData > 0) {
            if (index != 0)
                index = 0;
            Log.d("sizeData", "index " + index);
            Log.d("sizeData", "title " + data.get(index).getTitle());
            ImageView imgEvent = viewPopup.findViewById(R.id.home_popup_image_event);
            if (txtTitleEvent == null) {
                Log.d("sizeData", "gettitleevent " + data.get(index).getTitle());
                txtTitleEvent = viewPopup.findViewById(R.id.home_popup_title_event);
            }
            Button btnOk = viewPopup.findViewById(R.id.img_button_event_ok);
            Button btnNo = viewPopup.findViewById(R.id.img_button_event_no);
            eventDetail = viewPopup.findViewById(R.id.detail_event);
            eventDetail.setOnClickListener(view -> {
                String eventId = data.get(index).getId();

                Intent myIntent = new Intent(getActivity(), EventActivity.class);
                myIntent.putExtra("EVENT_ID", eventId);
                startActivity(myIntent);
            });

            Glide.with(requireActivity()).applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ph_event)).load(data.get(index).getBanner()).into(imgEvent);
            Objects.requireNonNull(txtTitleEvent).setText(data.get(index).getTitle() + " - " + data.get(index).getDate().toString());
            btnOk.setOnClickListener(view -> {
                String eventId = data.get(index).getId();
                CheckInEvent approveEvent = new CheckInEvent();

                Realm realm = LocalData.getRealm();
                Profile profile = realm.where(Profile.class).findFirst();

                approveEvent.setEvent_id(eventId);
                approveEvent.setUser_id(profile.getUserId());
                approveEvent.setAction(1);

                try {
                    EventHelper.approveEventInvitation(approveEvent, new RestCallback<ApiResponse>() {
                        @Override
                        public void onSuccess(Headers headers, ApiResponse body) {

                        }

                        @Override
                        public void onFailed(ErrorResponse error) {

                        }

                        @Override
                        public void onCanceled() {

                        }
                    });
                } catch (Exception e) {

                }

                index++;
                if (index == sizeData) {
                    index = 0;
                    frameLayout.setVisibility(View.GONE);
                } else {
                    Glide.with(getActivity()).applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ph_event)).load(data.get(index).getBanner()).into(imgEvent);
                    txtTitleEvent.setText(data.get(index).getTitle() + " - " + data.get(index).getDate().toString());
                    //Todo - Update data in server if can join event

                }
            });
            btnNo.setOnClickListener(view -> {
                CheckInEvent approveEvent = new CheckInEvent();
                Realm realm = LocalData.getRealm();
                Profile profile = realm.where(Profile.class).findFirst();
                String eventId = data.get(index).getId();

                approveEvent.setEvent_id(eventId);
                approveEvent.setUser_id(profile.getUserId());
                approveEvent.setAction(2);

                try {
                    EventHelper.approveEventInvitation(approveEvent, new RestCallback<ApiResponse>() {
                        @Override
                        public void onSuccess(Headers headers, ApiResponse body) {

                        }

                        @Override
                        public void onFailed(ErrorResponse error) {

                        }

                        @Override
                        public void onCanceled() {

                        }
                    });
                } catch (Exception e) {

                }

                index++;
                if (index == sizeData) {
                    index = 0;
                    frameLayout.setVisibility(View.GONE);
                } else {
                    Glide.with(getActivity()).applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ph_event)).load(data.get(index).getBanner()).into(imgEvent);
                    txtTitleEvent.setText(data.get(index).getTitle() + " - " + data.get(index).getDate().toString());
                    //Todo - Update data in server if cannot join event
                }
            });
        } else {
            frameLayout.setVisibility(View.GONE);
        }
    }
}
