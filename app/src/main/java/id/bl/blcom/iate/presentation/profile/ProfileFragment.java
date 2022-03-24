package id.bl.blcom.iate.presentation.profile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.rezkyatinnov.kyandroid.reztrofit.ErrorResponse;
import com.rezkyatinnov.kyandroid.reztrofit.RestCallback;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import id.bl.blcom.iate.R;
import id.bl.blcom.iate.api.ApiInterface;
import id.bl.blcom.iate.api.MemberHelper;
import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.models.GeneralDataProfile;
import id.bl.blcom.iate.models.Profile;
import id.bl.blcom.iate.presentation.ImagePreviewActivity;
import id.bl.blcom.iate.presentation.NavigationDrawerActivity;
import id.bl.blcom.iate.presentation.direktori.MemberDetailActivity;
import okhttp3.Headers;

public class ProfileFragment extends Fragment {
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 2;

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
    @BindView(R.id.interest_wrap)
    LinearLayout interestWrap;
    @BindView(R.id.tvCardName)
    TextView tvCardName;
    @BindView(R.id.tvCardId)
    TextView tvCardId;
    @BindView(R.id.imgQrCode)
    ImageView imgQrCode;
    @BindView(R.id.card_layout)
    RelativeLayout card_layout;
    @BindView(R.id.imgCard)
    ImageView imgCard;
    @BindView(R.id.tvDownloadImage)
    TextView tvDownloadImage;
    @BindView(R.id.pr_work_place)
    TextView pr_work_place;

    private void saveFile(){
        //to get the image from the ImageView (say iv)
        BitmapDrawable draw = (BitmapDrawable) imgCard.getDrawable();
        Bitmap bitmap = draw.getBitmap();

        FileOutputStream outStream = null;
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + "/Pictures");
        dir.mkdirs();
        @SuppressLint("DefaultLocale") String fileName = String.format("%d.jpg", System.currentTimeMillis());
        File outFile = new File(dir, fileName);

        Log.d("dir", dir + fileName);
        try {
            outStream = new FileOutputStream(outFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
        try {
            outStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(requireActivity(), "Berhasil menyimpan ke galeri anda", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(dir + "/" + fileName), "image/*");
        startActivity(intent);
    }

    @OnClick(R.id.phone)
    void onClickPhone(){
        if (ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);

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
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
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
            getOpenFacebookIntent(requireActivity());
        }
    }

    @OnClick(R.id.btnTwitter)
    void onClickBtnTwitter(){
        if (!prTwitter.getText().toString().equals("-") && !prFB.getText().toString().equals("")) {
            Intent intent = null;
            try {
                // get the Twitter app if possible
                requireActivity().getPackageManager().getPackageInfo("com.twitter.android", 0);
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + prTwitter));
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

    @OnClick(R.id.tvDownloadImage)
    void onClickDonwloadImage(){
        card_layout.setDrawingCacheEnabled(true);
        card_layout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        card_layout.layout(0, 0, card_layout.getMeasuredWidth(), card_layout.getMeasuredHeight());
        card_layout.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(card_layout.getDrawingCache());
        card_layout.setDrawingCacheEnabled(false);

        card_layout.setVisibility(View.GONE);
        imgCard.setImageBitmap(b);
        imgCard.setVisibility(View.VISIBLE);

        saveFile();
    }

    public Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private String userId;
    private String imgUrl;
    private String fullName;
    private String dso;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        populateProfileData();
        ((NavigationDrawerActivity) getActivity()).showBackMenu(false);

        tvDownloadImage.setVisibility(View.VISIBLE);

        if (ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        2);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        populateProfileData();
    }

    @OnClick(R.id.pr_change_password)
    void onClickChangePassword() {
        Bundle bundle = new Bundle();
        bundle.putString("userId", userId);
        bundle.putString("imgUrl", imgUrl);
        bundle.putString("fullName", fullName);
        bundle.putString("dso", dso);
        Fragment fragment = new ChangePasswordFragment();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.screen_area, fragment, "change password");
        fragmentTransaction.addToBackStack("change password");

        fragmentTransaction.commit();
    }

    @OnClick(R.id.pr_edit_profile)
    void onClickEditProfile() {

//        Bundle bundle = new Bundle();
//        bundle.putString("imgUrl", imgUrl);
//        Fragment fragment = new EditProfileFragment();
//        fragment.setArguments(bundle);
//        FragmentManager fragmentManager = getFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//        fragmentTransaction.replace(R.id.screen_area, fragment, "edit profile");
//        fragmentTransaction.addToBackStack("edit profile");
//
//        fragmentTransaction.commit();

        Intent intent = new Intent(requireActivity(), EditProfileActivity.class);
        intent.putExtra("imgUrl", imgUrl);
        startActivity(intent);
    }

    private void populateProfileData() {
        try {
            MemberHelper.getProfile(new RestCallback<ApiResponse<GeneralDataProfile>>() {
                @Override
                public void onSuccess(Headers headers, ApiResponse<GeneralDataProfile> body) {
                    try {
                        Profile profile = body.getData().getProfile();
                        String address = "-";
                        if (profile.getStreet_address()!=null){
                            address = profile.getStreet_address() + ", " + profile.getSubdistrict_address() + ", " + profile.getDistrict_address() + ", " + profile.getProvince_address() + " " + profile.getPostal_address() + ", " + profile.getCountry_address();
                        }

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar dateNow = Calendar.getInstance();
                        Calendar birthDate = Calendar.getInstance();
                        try {
                            if (profile.getBirthDate() != null)
                                birthDate.setTime(format.parse(profile.getBirthDate()));
                            Log.d("Birth Date", profile.getBirthDate());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (profile != null) {

                            if (profile.getInterests() != null) {
                                List<String> interests = Arrays.asList(profile.getInterests().split(","));

                                for (int idx = 0; idx < interests.size(); idx++) {
                                    LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    TextView tv = new TextView(getContext());
                                    lparams.setMargins(0, 0, 20, 0);
                                    tv.setLayoutParams(lparams);
                                    tv.setPadding(10, 5, 5, 10);
                                    tv.setBackgroundResource(R.color.darkRed);
                                    tv.setTextColor(getResources().getColor(R.color.colorDefault));
                                    tv.setText(interests.get(idx));
                                    interestWrap.addView(tv);
                                }
                            }

                            userId = profile.getUserId();
                            imgUrl = ApiInterface.BASE_URL_IMAGE + profile.getPicture();
                            fullName = profile.getFullname();
                            prFullname.setText(profile.getFullname());
                            prDSO.setText(profile.getGroup().getName());
                            dso = profile.getGroup() != null ? profile.getGroup().getName() : "";
                            prHobby.setText(profile.getHobby());
                            if (imgUrl != null)
                                Glide.with(getContext()).applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.img_profile_default)).load(imgUrl).into(imageProfile);
                            prNoHp.setText(profile.getPhoneNumber());
                            prEmail.setText(profile.getEmail());
                            prAge.setText(dateNow.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR) + " Tahun");
                            prStatus.setText(profile.getWork() != null ? profile.getWork() : "-");
                            prSize.setText(profile.getShirtSize() != null ? profile.getShirtSize() : "-");
                            prAddress.setText(address);
                            pr_work_place.setText(profile.getWork_place() != null ? profile.getWork_place() : "-");
                            String phone = profile.getWhatsupNumber() != null ? profile.getWhatsupNumber() : "-";
                            if (!phone.equals("-")) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    phone =  PhoneNumberUtils.formatNumber(phone, Locale.getDefault().getCountry());
                                } else {
                                    phone =  PhoneNumberUtils.formatNumber(phone);
                                }
                            }

                            prWA.setText(phone);
                            prLine.setText(profile.getLineAccount() != null ? profile.getLineAccount() : "-");
                            prBB.setText(profile.getPinBbm() != null ? profile.getPinBbm() : "-");
                            prFB.setText(profile.getFacebookAccount() != null ? profile.getFacebookAccount() : "-");
                            prTwitter.setText(profile.getTwitterAccount() != null ? profile.getTwitterAccount() : "-");
                            prInstagram.setText(profile.getInstagramAccount() != null ? profile.getInstagramAccount() : "-");


                            tvCardName.setText(prFullname.getText().toString());
                            tvCardId.setText(profile.getMember_id() != null ? profile.getMember_id() : "-");

                            QRCodeWriter writer = new QRCodeWriter();
                            try {
                                BitMatrix bitMatrix = writer.encode(profile.getUserId(), BarcodeFormat.QR_CODE, 512, 512);
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

                            imageProfile.setOnClickListener(view1 -> {
                                Intent myIntent = new Intent(requireActivity(), ImagePreviewActivity.class);
                                myIntent.putExtra("imgUrl", ApiInterface.BASE_URL_IMAGE + profile.getPicture());
                                myIntent.putExtra("type", 1);
                                startActivity(myIntent);
                            });
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
        } catch (Exception e) {

        }
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
