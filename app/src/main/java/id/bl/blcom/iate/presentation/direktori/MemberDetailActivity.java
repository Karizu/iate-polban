package id.bl.blcom.iate.presentation.direktori;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import id.bl.blcom.iate.R;
import id.bl.blcom.iate.api.ApiInterface;
import id.bl.blcom.iate.models.MemberProfile;
import id.bl.blcom.iate.models.Privacy;
import id.bl.blcom.iate.models.Profile;
import id.bl.blcom.iate.presentation.ImagePreviewActivity;

public class MemberDetailActivity extends AppCompatActivity {

    @BindView(R.id.pr_image_profile)
    CircleImageView imageProfile;

    @BindView(R.id.pr_no_hp)
    TextView prNoHp;

    @BindView(R.id.pr_email)
    TextView prEmail;

    @BindView(R.id.pr_age)
    TextView prAge;

    @BindView(R.id.pr_status)
    TextView prStatus;

    @BindView(R.id.pr_size)
    TextView prSize;

    @BindView(R.id.pr_hobby)
    TextView prHobby;

    @BindView(R.id.pr_address)
    TextView prAddress;

    @BindView(R.id.pr_wa)
    TextView prWA;

    @BindView(R.id.pr_line)
    TextView prLine;

    @BindView(R.id.pr_bb)
    TextView prBB;

    @BindView(R.id.pr_fb)
    TextView prFB;

    @BindView(R.id.pr_twitter)
    TextView prTwitter;

    @BindView(R.id.pr_instagram)
    TextView prInstagram;

    @BindView(R.id.fullname)
    TextView prFullname;

    @BindView(R.id.dso)
    TextView prDSO;

    @BindView(R.id.pr_change_password)
    ImageView ivChangePassword;

    @BindView(R.id.pr_edit_profile)
    ImageView ivEditProfile;

    @BindView(R.id.tvCardName)
    TextView tvCardName;
    @BindView(R.id.tvCardId)
    TextView tvCardId;
    @BindView(R.id.imgQrCode)
    ImageView imgQrCode;
    @BindView(R.id.card_layout)
    RelativeLayout card_layout;

    @OnClick(R.id.phone)
    void onClickPhone(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    2);

            // MY_PERMISSIONS_REQUEST_CALL_PHONE is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + prNoHp.getText().toString()));
            startActivity(intent);
        }
    }

    @OnClick(R.id.email)
    void onClickEmail(){
        Log.d("email", prEmail.getText().toString());
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + prEmail.getText().toString())); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, prEmail.getText().toString());
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @OnClick(R.id.address)
    void onClickAddres(){
        if (!prAddress.getText().toString().equals("") && !prAddress.getText().toString().equals("-")) {
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + prAddress.getText().toString());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }
    }

    @OnClick(R.id.btnWhatsapp)
    void onClickBtnWhatsapp() {
        if (!prWA.getText().toString().equals("-") && !prWA.getText().toString().equals("")) {
            String phone = prWA.getText().toString();

            if (phone.contains("-")) phone = phone.replace("-", "");
            if (phone.startsWith("0")) phone = "62" + phone.substring(1);

            String url = "https://api.whatsapp.com/send?phone=" + phone;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
    }

    @OnClick(R.id.btnLine)
    void onClickBtnLine() {
        if (!prLine.getText().toString().equals("-") && !prLine.getText().toString().equals("")) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://line.me/R/nv/addFriends"));
            startActivity(intent);
        }
    }

    @OnClick(R.id.btnFacebook)
    void onClickBtnFacebook() {
        if (!prFB.getText().toString().equals("-") && !prFB.getText().toString().equals("")) {
            getOpenFacebookIntent(MemberDetailActivity.this);
        }
    }

    @OnClick(R.id.btnTwitter)
    void onClickBtnTwitter(){
        if (!prTwitter.getText().toString().equals("-") && !prFB.getText().toString().equals("")) {
            Intent intent = null;
            try {
                // get the Twitter app if possible
                getPackageManager().getPackageInfo("com.twitter.android", 0);
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + prTwitter));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            } catch (Exception e) {
                // no Twitter app, revert to browser
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + prTwitter));
            }
            this.startActivity(intent);
        }
    }

    @OnClick(R.id.btnInstagram)
    void onClickBtnInstagram(){
        if (!prInstagram.getText().toString().equals("-") && !prInstagram.getText().toString().equals("")) {
            Uri uri = Uri.parse("http://instagram.com/_u/" + prInstagram);
            Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

            likeIng.setPackage("com.instagram.android");

            try {
                startActivity(likeIng);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://instagram.com/" + prInstagram)));
            }
        }
    }

    private Bundle bundle;
    private Profile profile;
    private String age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_detail);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ivChangePassword.setVisibility(View.INVISIBLE);
        ivEditProfile.setVisibility(View.INVISIBLE);

        String json = getIntent().getStringExtra("json");
        profile = new Gson().fromJson(json, Profile.class);
        age = getIntent().getStringExtra("age");

        populateData();

        imageProfile.setOnClickListener(view1 -> {
            Intent myIntent = new Intent(MemberDetailActivity.this, ImagePreviewActivity.class);
            myIntent.putExtra("imgUrl", ApiInterface.BASE_URL_IMAGE + profile.getPicture());
            myIntent.putExtra("type", 1);
            startActivity(myIntent);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void populateData(){
        Glide.with(this).applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.img_profile_default)).load(ApiInterface.BASE_URL_IMAGE + profile.getPicture()).into(imageProfile);

        prFullname.setText(profile.getFullname());
        prDSO.setText(profile.getGroup()!=null?profile.getGroup().getName():"-");

        tvCardName.setText(profile.getFullname());
        tvCardId.setText(profile.getMember_id() != null ? profile.getMember_id() : "-");

        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(profile.getMember_id() != null ? profile.getMember_id() : "-", BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            imgQrCode.setImageBitmap(bmp);

        } catch (WriterException e) {
            e.printStackTrace();
        }

        if (profile.getPrivacyConfig() != null){

            Gson gson = new Gson();
            Privacy privacy = gson.fromJson(profile.getPrivacyConfig(), Privacy.class);

            if (privacy.isPhoneShow()) {
                prNoHp.setText(profile.getPhoneNumber());
            } else {
                prNoHp.setText("-");
            }

            if (privacy.isEmailShow()){
                prEmail.setText(profile.getEmail());
            } else {
                prEmail.setText("-");
            }

            if (privacy.isAgeShow()){
                prAge.setText(age);

            } else {
                prAge.setText("-");

            }

            if (privacy.isStatusShow()){
                prStatus.setText(profile.getWork());

            } else {
                prStatus.setText("-");

            }

            if(privacy.isShirtSizeShow()){
                prSize.setText(profile.getShirtSize() != null ? profile.getShirtSize() : "-");

            } else {
                prSize.setText("-");

            }

            if (privacy.isAddressShow()){
                prAddress.setText(profile.getAddress() != null ? profile.getAddress() : "");

            } else {
                prAddress.setText("-");

            }

            if (privacy.isHobbyShow()){
                prHobby.setText(profile.getHobby() != null ? profile.getHobby() : "");

            } else {
                prHobby.setText("-");

            }

        } else {
            prNoHp.setText(profile.getPhoneNumber());
            prEmail.setText(profile.getEmail());
            prAge.setText(age);
            prStatus.setText(profile.getWork());
            prSize.setText(profile.getShirtSize() != null ? profile.getShirtSize() : "-");
            prAddress.setText(profile.getAddress() != null ? profile.getAddress() : "");
            prHobby.setText(profile.getHobby() != null ? profile.getHobby() : "");
        }

        prWA.setText(profile.getWhatsupNumber() != null ? profile.getWhatsupNumber() : "");
        prLine.setText(profile.getLineAccount() != null ? profile.getLineAccount() : "");
        prBB.setText(profile.getPinBbm() != null ? profile.getPinBbm() : "");
        prFB.setText(profile.getFacebookAccount() != null ? profile.getFacebookAccount() : "");
        prTwitter.setText(profile.getTwitterAccount() != null ? profile.getTwitterAccount() : "");
        prInstagram.setText(profile.getInstagramAccount() != null ? profile.getInstagramAccount() : "");

    }

    private Intent getOpenFacebookIntent(Context context) {

        try {
            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + prFB.getText().toString()));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + prFB.getText().toString()));
        }
    }
}