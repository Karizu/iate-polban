package id.bl.blcom.iate.presentation.event;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.rezkyatinnov.kyandroid.localdata.LocalData;
import com.rezkyatinnov.kyandroid.reztrofit.ErrorResponse;
import com.rezkyatinnov.kyandroid.reztrofit.RestCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.bl.blcom.iate.R;
import id.bl.blcom.iate.api.ApiInterface;
import id.bl.blcom.iate.api.EventHelper;
import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.models.CheckInEvent;
import id.bl.blcom.iate.models.Media;
import id.bl.blcom.iate.models.Profile;
import id.bl.blcom.iate.models.response.EventDataResponse;
import id.bl.blcom.iate.presentation.ImagePreviewActivity;
import id.bl.blcom.iate.presentation.LoadingDialog;
import id.bl.blcom.iate.presentation.scanqr.ScanQrActivity;
import io.realm.Realm;
import okhttp3.Headers;

public class EventActivity extends AppCompatActivity {
    String eventId = "";

    @BindView(R.id.tvEventName)
    TextView tvEventName;

    @BindView(R.id.tvEventDate)
    TextView tvEventDate;

    @BindView(R.id.tvEventTime)
    TextView tvEventTime;

    @BindView(R.id.tvEventPlace)
    TextView tvEventPlace;

    @BindView(R.id.tvEventDesc)
    WebView tvEventDesc;

    @BindView(R.id.imgBanner)
    ImageView imgBanner;

    @BindView(R.id.imgMapIcon)
    ImageView imgMapIcon;

    @BindView(R.id.rvGallery)
    RecyclerView rvGallery;

    @BindView(R.id.invitation_btn)
    Button btnInvitation;

    @BindView(R.id.reminder_btn)
    Button btnReminder;

    EventDataResponse mData;
    CheckInEvent checkInEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        rvGallery.setLayoutManager(new GridLayoutManager(this, 3));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_event, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_scanqr:
                Intent myIntent = new Intent(this, ScanQrActivity.class);
                startActivity(myIntent);
                break;
            case android.R.id.home:
                // do something useful
                finish();
                return (true);
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        eventId = intent.getStringExtra("EVENT_ID");
        try {
            EventHelper.getEvent(eventId, new RestCallback<ApiResponse<EventDataResponse>>() {
                @Override
                public void onSuccess(Headers headers, ApiResponse<EventDataResponse> body) {
                    try {
                        mData = body.getData();
                        tvEventName.setText(body.getData().getTitle());
                        tvEventDate.setText("Date : " + body.getData().getDate());
                        tvEventTime.setText("Waktu : " + body.getData().getTime());
                        tvEventPlace.setText(body.getData().getPlace());

                        String content = "<html><head>"
                                + "<style type=\"text/css\"> body{color: #483434} p{color: black; font-size: 15px;}"
                                + "</style></head>"
                                + "<body>"
                                + body.getData().getDescription()
                                + "</body></html>";

                        tvEventDesc.getSettings().setJavaScriptEnabled(true);
                        tvEventDesc.setBackgroundColor(Color.TRANSPARENT);
                        tvEventDesc.loadDataWithBaseURL("", content, "text/html", "UTF-8", "");

                        Glide.with(EventActivity.this).applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ph_banner)).load(ApiInterface.BASE_URL_IMAGE + body.getData().getBanner()).into(imgBanner);
                        imgBanner.setOnClickListener(view -> {
                            Intent myIntent = new Intent(getApplicationContext(), ImagePreviewActivity.class);
                            myIntent.putExtra("imgUrl", ApiInterface.BASE_URL_IMAGE + body.getData().getBanner());
                            myIntent.putExtra("type", 1);
                            startActivity(myIntent);
                        });

                        Realm realm = LocalData.getRealm();
                        Profile profile = realm.where(Profile.class).findFirst();

                        checkInEvent = new CheckInEvent();
                        checkInEvent.setAction(body.getData().getInvitationStatus());
                        checkInEvent.setUser_id(profile.getUserId());
                        checkInEvent.setEvent_id(body.getData().getId());

                        if(body.getData().getInvitationStatus() == null){
                            btnInvitation.setVisibility(View.INVISIBLE);
                        } else {
                            switch (body.getData().getInvitationStatus()){
                                case 1:
                                    btnInvitation.setText("Batal Hadir");
                                    break;

                                default:
                                    btnInvitation.setText("Hadir");
                                    break;

                            }

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

            EventHelper.getEventMedia(eventId, new RestCallback<ApiResponse<List<Media>>>() {
                @Override
                public void onSuccess(Headers headers, ApiResponse<List<Media>> body) {
                    try {
                        rvGallery.setAdapter(new EventGalleryAdapter(EventActivity.this,body.getData()));
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
        } catch (Exception e) {

        }
    }

    @OnClick(R.id.tvEventPlace)
    public void onTvEventPlaceClick() {
        goToGoogleMaps();
    }

    @OnClick(R.id.imgMapIcon)
    public void onImgMapIconClick() {
        goToGoogleMaps();
    }

    @OnClick(R.id.invitation_btn)
    public void onInvitationBtnClick() {
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.setCancelable(false);
        loadingDialog.show();
        try {
            switch (checkInEvent.getAction()){

                case 0:
                    checkInEvent.setAction(1);
                    break;

                case 1:
                    checkInEvent.setAction(2);
                    break;

                case 2:
                    checkInEvent.setAction(1);
                    break;

            }
        } catch (Exception e){
            e.printStackTrace();
        }

        try {
            EventHelper.approveEventInvitation(checkInEvent, new RestCallback<ApiResponse>() {
                @Override
                public void onSuccess(Headers headers, ApiResponse body) {
                    EventHelper.getEvent(eventId, new RestCallback<ApiResponse<EventDataResponse>>() {
                        @SuppressLint({"SetTextI18n", "SetJavaScriptEnabled"})
                        @Override
                        public void onSuccess(Headers headers, ApiResponse<EventDataResponse> body) {
                            try {
                                loadingDialog.dismiss();
                                mData = body.getData();
                                tvEventName.setText(body.getData().getTitle());
                                tvEventDate.setText("Date : " + body.getData().getDate());
                                tvEventTime.setText("Waktu : " + body.getData().getTime());
                                tvEventPlace.setText(body.getData().getPlace());
                                String content = "<html><head>"
                                        + "<style type=\"text/css\">body{color: #FFFFFF} p{font-size: 12px}"
                                        + "</style></head>"
                                        + "<body>"
                                        + body.getData().getDescription()
                                        + "</body></html>";

                                tvEventDesc.getSettings().setJavaScriptEnabled(true);
                                tvEventDesc.setBackgroundColor(Color.TRANSPARENT);
                                tvEventDesc.loadDataWithBaseURL("", content, "text/html", "UTF-8", "");
                                Glide.with(EventActivity.this).applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ph_banner)).load(body.getData().getBanner()).into(imgBanner);
                                imgBanner.setOnClickListener(view -> {
                                    Intent myIntent = new Intent(getApplicationContext(), ImagePreviewActivity.class);
                                    myIntent.putExtra("imgUrl", ApiInterface.BASE_URL_IMAGE + body.getData().getBanner());
                                    startActivity(myIntent);
                                });

                                Realm realm = LocalData.getRealm();
                                Profile profile = realm.where(Profile.class).findFirst();

                                checkInEvent = new CheckInEvent();
                                checkInEvent.setAction(body.getData().getInvitationStatus());
                                checkInEvent.setUser_id(profile.getUserId());
                                checkInEvent.setEvent_id(body.getData().getId());

                                if(body.getData().getInvitationStatus() == null){
                                    btnInvitation.setVisibility(View.INVISIBLE);
                                } else {
                                    switch (body.getData().getInvitationStatus()){
                                        case 1:
                                            Toast.makeText(getApplicationContext(), "You will attend this event", Toast.LENGTH_SHORT).show();
                                            btnInvitation.setText("Batal Hadir");
                                            break;

                                        default:
                                            Toast.makeText(getApplicationContext(), "You cancel attending this event", Toast.LENGTH_SHORT).show();
                                            btnInvitation.setText("Hadir");
                                            break;

                                    }

                                }
                            } catch (Exception e) {

                            }
                        }

                        @Override
                        public void onFailed(ErrorResponse error) {
                            loadingDialog.dismiss();
                        }

                        @Override
                        public void onCanceled() {
                            loadingDialog.dismiss();
                        }
                    });

                    EventHelper.getEventMedia(eventId, new RestCallback<ApiResponse<List<Media>>>() {
                        @Override
                        public void onSuccess(Headers headers, ApiResponse<List<Media>> body) {
                            try {
                                rvGallery.setAdapter(new EventGalleryAdapter(EventActivity.this,body.getData()));
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
    }

    @OnClick(R.id.reminder_btn)
    public void onReminderEventClick(){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date;
        Calendar calendar = Calendar.getInstance();
        try {
            date = sdf.parse(mData.getDate()+" "+mData.getTime());
            calendar.setTime(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", calendar.getTimeInMillis());
        intent.putExtra(CalendarContract.Events.TITLE, mData.getTitle());
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, mData.getPlace());
        startActivity(intent);
    }

    private void goToGoogleMaps() {
        Uri gmmIntentUri = Uri.parse("geo:"
                        + mData.getLatitude() + "," + mData.getLongitude()
                        + "?q="
                        + mData.getLatitude() + "," + mData.getLongitude()
                        + "("+mData.getTitle()+")");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
}
