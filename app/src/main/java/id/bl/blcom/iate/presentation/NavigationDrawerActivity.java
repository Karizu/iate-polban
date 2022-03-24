package id.bl.blcom.iate.presentation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;

import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView;
import com.rezkyatinnov.kyandroid.localdata.LocalData;
import com.rezkyatinnov.kyandroid.reztrofit.ErrorResponse;
import com.rezkyatinnov.kyandroid.reztrofit.RestCallback;

import java.util.ArrayList;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import id.bl.blcom.iate.R;
import id.bl.blcom.iate.RegisterActivity;
import id.bl.blcom.iate.api.ApiInterface;
import id.bl.blcom.iate.api.GroupHelper;
import id.bl.blcom.iate.api.NotificationHelper;
import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.models.Group;
import id.bl.blcom.iate.models.Notification;
import id.bl.blcom.iate.models.Profile;
import id.bl.blcom.iate.presentation.article.ArticleFragment;
import id.bl.blcom.iate.presentation.calendar.CalendarFragment;
import id.bl.blcom.iate.presentation.direktori.DirektoriFragment;
import id.bl.blcom.iate.presentation.diskusi.DiskusiFragment;
import id.bl.blcom.iate.presentation.home.HomeFragment;
import id.bl.blcom.iate.presentation.news.NewsFragment;
import id.bl.blcom.iate.presentation.notifikasi.NotifikasiActivity;
import id.bl.blcom.iate.presentation.notifikasi.NotifikasiFragment;
import id.bl.blcom.iate.presentation.profile.EditProfileActivity;
import id.bl.blcom.iate.presentation.profile.ProfileFragment;
import id.bl.blcom.iate.presentation.setting.SettingFragment;
import io.realm.Realm;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int CAMERA_REQUEST_CODE = 1;
    boolean doubleBackToExitPressedOnce = false;

    Bitmap photoImage;
    File fileImage;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    FrameLayout navigationView;

    @BindView(R.id.profile_image)
    CircleImageView profileImage;

    @BindView(R.id.full_name)
    TextView fullname;

    @BindView(R.id.region_name)
    TextView regionName;

    @BindView(R.id.counter_notif)
    TextView counterNotif;

    private ActionBarDrawerToggle toggle;
    private boolean mToolBarNavigationListenerIsRegistered = false;
    private PopupMenu popupMenu;
    private List<String> groupIds;
    private ImageView imgFilter;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Realm realm = LocalData.getRealm();

        Profile profile = realm.where(Profile.class).findFirst();
        Log.d("profil", ApiInterface.BASE_URL_IMAGE + profile.getPicture());

        SharedPreferences prefs = this.getSharedPreferences(
                "preference", Context.MODE_PRIVATE);

        if (profile.getWork() == null || profile.getWork().equals("") || profile.getWork_place() == null || profile.getWork_place().equals("")) {
            showPopup();
            Button btn_profile = dialog.findViewById(R.id.btn_profile);
            String imgUrl = ApiInterface.BASE_URL_IMAGE + profile.getPicture();
            btn_profile.setOnClickListener(view -> {
                dialog.dismiss();
                Intent intent = new Intent(this, EditProfileActivity.class);
                intent.putExtra("imgUrl", imgUrl);
                startActivity(intent);
            });
        }

        Glide.with(this).applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ic_baseline_person_24)).load(ApiInterface.BASE_URL_IMAGE + profile.getPicture()).into(profileImage);
        fullname.setText(profile.getFullname());
        regionName.setText(profile.getGroup().getName());

        setOnClickListenerMenu();
        setDefaultFragment();
        countNotification(profile.getUserId());

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            try {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            Realm realm = LocalData.getRealm();
            Profile profile = realm.where(Profile.class).findFirst();
            Glide.with(this).applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ic_baseline_person_24)).load(ApiInterface.BASE_URL_IMAGE + profile.getPicture()).into(profileImage);
            fullname.setText(profile.getFullname());
            regionName.setText(profile.getGroup().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void countNotification(String userId) {
        NotificationHelper.getCountNotification(userId, false, new RestCallback<ApiResponse<List<Notification>>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(Headers headers, ApiResponse<List<Notification>> body) {
                try {
                    int count = body.getData().size();
                    if (count < 1) {
                        counterNotif.setVisibility(View.GONE);
                    } else {
                        counterNotif.setVisibility(View.VISIBLE);
                        counterNotif.setText(Integer.toString(body.getData().size()));
                    }
                    Log.d("Counter Notif", String.valueOf(body.getData().size()));
                } catch (Exception ignored) {

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

    @OnClick(R.id.drawer_profile)
    void onClickProfileImage() {
        callProfile();
    }

    private void callProfile() {
        RelativeLayout imgNotification = findViewById(R.id.img_notification_toolbar);
        imgNotification.setOnClickListener(view -> {
//            callFragment(new NotifikasiFragment(), null);
            Intent intent = new Intent(NavigationDrawerActivity.this, NotifikasiActivity.class);
            startActivity(intent);
        });
        hideAllToolbarIcon();
        imgNotification.setVisibility(View.VISIBLE);

        callFragment(new ProfileFragment(), null);
    }

    private void populateGroup() {
        GroupHelper.getGroup(new RestCallback<ApiResponse<List<Group>>>() {
            @Override
            public void onSuccess(Headers headers, ApiResponse<List<Group>> body) {
                List<Group> data = body.getData();

                groupIds = new ArrayList<>();
                for (int i = 0; i < data.size(); i++) {
                    groupIds.add(data.get(i).getId());
                    popupMenu.getMenu().add(1, i, i, data.get(i).getName());
                }

                imgFilter.setOnClickListener(view1 -> popupMenu.show());
            }

            @Override
            public void onFailed(ErrorResponse error) {

            }

            @Override
            public void onCanceled() {

            }
        });
    }

    private void setOnClickListenerMenu() {
        LinearLayout llHome = findViewById(R.id.ll_nav_home);
        LinearLayout llDiscussion = findViewById(R.id.ll_nav_discussion);
        LinearLayout llNews = findViewById(R.id.ll_nav_news);
        LinearLayout llMemberDirectory = findViewById(R.id.ll_nav_member_directory);
        LinearLayout llArticle = findViewById(R.id.ll_nav_article);
        LinearLayout llCalendar = findViewById(R.id.ll_nav_calendar);
        LinearLayout llSetting = findViewById(R.id.ll_nav_setting);
        LinearLayout llNavDonate = findViewById(R.id.ll_nav_donate);
        LinearLayout llNavFeedback = findViewById(R.id.ll_nav_feedback);

        RelativeLayout imgNotification = findViewById(R.id.img_notification_toolbar);
        imgNotification.setOnClickListener(view -> {
//            callFragment(new NotifikasiFragment(), null);
            Intent intent = new Intent(NavigationDrawerActivity.this, NotifikasiActivity.class);
            startActivity(intent);
        });

        imgFilter = findViewById(R.id.img_filter_toolbar);

        Context wrapper = new ContextThemeWrapper(this, R.style.popupMenuStyle);

        popupMenu = new PopupMenu(wrapper, imgFilter, Gravity.CENTER_HORIZONTAL);
        populateGroup();
        popupMenu.getMenuInflater().inflate(R.menu.menu_group, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            Bundle bundle = new Bundle();
            bundle.putString("groupId", groupIds.get(menuItem.getItemId()));
            bundle.putString("groupName", menuItem.getTitle().toString());

            callFragment(new CalendarFragment(), bundle);

            return true;
        });


        llHome.setOnClickListener(view -> {
            hideAllToolbarIcon();
            imgNotification.setVisibility(View.VISIBLE);

            callFragment(new HomeFragment(), null);
        });

        llDiscussion.setOnClickListener(view -> {
            hideAllToolbarIcon();
            imgNotification.setVisibility(View.VISIBLE);

            callFragment(new DiskusiFragment(), null);
        });

        llNews.setOnClickListener(view -> {
            hideAllToolbarIcon();
            imgNotification.setVisibility(View.VISIBLE);

            callFragment(new NewsFragment(), null);
        });

        llMemberDirectory.setOnClickListener(view -> {
            hideAllToolbarIcon();
            imgNotification.setVisibility(View.VISIBLE);

            callFragment(new DirektoriFragment(), null);
        });

        llArticle.setOnClickListener(view -> {
            hideAllToolbarIcon();
            imgNotification.setVisibility(View.VISIBLE);

            callFragment(new ArticleFragment(), null);
        });

        llCalendar.setOnClickListener(view -> {
            hideAllToolbarIcon();
            imgNotification.setVisibility(View.VISIBLE);
            imgFilter.setVisibility(View.VISIBLE);

            Bundle bundle = new Bundle();
            bundle.putString("groupId", null);

            callFragment(new CalendarFragment(), bundle);
        });

        llSetting.setOnClickListener(view -> {
            hideAllToolbarIcon();
            imgNotification.setVisibility(View.VISIBLE);

            callFragment(new SettingFragment(), null);
        });

        llNavDonate.setOnClickListener(view -> {
            hideAllToolbarIcon();
            imgNotification.setVisibility(View.VISIBLE);

            callFragment(new DonateFragment(), null);
        });

        llNavFeedback.setOnClickListener(view -> {
            hideAllToolbarIcon();
            imgNotification.setVisibility(View.VISIBLE);

            callFragment(new FeedbackFragment(), null);
        });
    }

    private void hideAllToolbarIcon() {
        ImageView imgFilter = findViewById(R.id.img_filter_toolbar);
        RelativeLayout imgNotification = findViewById(R.id.img_notification_toolbar);
        ImageView imgQrCode = findViewById(R.id.img_qr_code_toolbar);
        ImageView imgSetting = findViewById(R.id.img_setting_toolbar);
        ImageView imgShare = findViewById(R.id.img_share_toolbar);

        imgFilter.setVisibility(View.GONE);
        imgNotification.setVisibility(View.GONE);
        imgQrCode.setVisibility(View.GONE);
        imgSetting.setVisibility(View.GONE);
        imgShare.setVisibility(View.GONE);
    }

    private void callFragment(@NonNull Fragment fragment, Bundle bundle) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (bundle != null)
            fragment.setArguments(bundle);

        fragmentTransaction.replace(R.id.screen_area, fragment);

        fragmentTransaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            showBackMenu(false);
        }
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;

        if (id == R.id.nav_home) {
            fragment = new HomeFragment();
        } else if (id == R.id.nav_forum_diskusi) {
            fragment = new DiskusiFragment();
        } else if (id == R.id.nav_berita_pengumuman) {
            fragment = new NewsFragment();
        } else if (id == R.id.nav_direktori_anggota) {
            fragment = new DirektoriFragment();
        } else if (id == R.id.nav_artikel) {
            fragment = new ArticleFragment();
        } else if (id == R.id.nav_kalender_kegiatan) {
            fragment = new CalendarFragment();
        } else if (id == R.id.nav_setting) {
            fragment = new SettingFragment();
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.screen_area, fragment);
            fragmentTransaction.addToBackStack("Home Fragment");

            fragmentTransaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setDefaultFragment() {
        Fragment fragment = new HomeFragment();

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.screen_area, fragment);

            fragmentTransaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    public void showBackMenu(boolean enable) {

        // To keep states of ActionBar and ActionBarDrawerToggle synchronized,
        // when you enable on one, you disable on the other.
        // And as you may notice, the order for this operation is disable first, then enable - VERY VERY IMPORTANT.
        if (enable) {
            //You may not want to open the drawer on swipe from the left in this case
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            // Remove hamburger
            toggle.setDrawerIndicatorEnabled(false);
            // Show back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // when DrawerToggle is disabled i.e. setDrawerIndicatorEnabled(false), navigation icon
            // clicks are disabled i.e. the UP button will not work.
            // We need to add a listener, as in below, so DrawerToggle will forward
            // click events to this listener.
            if (!mToolBarNavigationListenerIsRegistered) {
                toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Doesn't have to be onBackPressed
                        onBackPressed();
                    }
                });

                mToolBarNavigationListenerIsRegistered = true;
            }

        } else {
            //You must regain the power of swipe for the drawer.
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

            // Remove back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            // Show hamburger
            toggle.setDrawerIndicatorEnabled(true);
            // Remove the/any drawer toggle listener
            toggle.setToolbarNavigationClickListener(null);
            mToolBarNavigationListenerIsRegistered = false;
        }

        // So, one may think "Hmm why not simplify to:
        // .....
        // getSupportActionBar().setDisplayHomeAsUpEnabled(enable);
        // mDrawer.setDrawerIndicatorEnabled(!enable);
        // ......
        // To re-iterate, the order in which you enable and disable views IS important #dontSimplify.
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            ImageView img = findViewById(R.id.re_img_profile_photo);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photoImage = (Bitmap) data.getExtras().get("data");
            photoImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            img.setImageBitmap(photoImage);
            try {
                File outputDir = getCacheDir();
                fileImage = File.createTempFile("photo", "jpeg", outputDir);
                FileOutputStream outputStream = openFileOutput("photo.jpeg", Context.MODE_PRIVATE);
                outputStream.write(stream.toByteArray());
                outputStream.close();
                Log.d("Write File", "Success");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Write File", "Failed2");
            }
        }
    }

    public Call updateProfile(Callback callback) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Log.d("photo", "check photo");
        File file = new File(getCacheDir(), "file.jpeg");
        if (photoImage != null)
            photoImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        Log.d("photo", "check after take foto");
        OkHttpClient client = new OkHttpClient();
        String fileName = "file.jpeg";
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("fullname", fullname.getText().toString())
                .addFormDataPart("picture", "photo.jpeg", RequestBody.create(MediaType.parse("image/jpeg"), stream.toByteArray()))
                .build();
        Request request = new Request.Builder()
                .url("http://37.72.172.144/superfriends-api/public/api/register")
                .post(body)
                .build();

        Log.d("Response", "before response");
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    private void showPopup() {
        dialog = new Dialog(NavigationDrawerActivity.this);
        //set content
        dialog.setContentView(R.layout.dialog_complete_profile);
        dialog.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);

        ImageView closeImage = dialog.findViewById(R.id.close_dialog);

        closeImage.setOnClickListener(view -> dialog.dismiss());
    }
}
