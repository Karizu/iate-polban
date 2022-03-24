package id.bl.blcom.iate.presentation.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rezkyatinnov.kyandroid.localdata.LocalData;
import com.rezkyatinnov.kyandroid.reztrofit.ErrorResponse;
import com.rezkyatinnov.kyandroid.reztrofit.RestCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import id.bl.blcom.iate.R;
import id.bl.blcom.iate.api.Api;
import id.bl.blcom.iate.api.InterestHelper;
import id.bl.blcom.iate.api.PrivacyHelper;
import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.models.Interest;
import id.bl.blcom.iate.models.Profile;
import id.bl.blcom.iate.models.response.AddressDatum;
import id.bl.blcom.iate.presentation.LoadingDialog;
import id.bl.blcom.iate.presentation.NavigationDrawerActivity;
import id.bl.blcom.iate.presentation.Utils;
import id.bl.blcom.iate.services.AdapterInterest;
import id.bl.blcom.iate.services.ExpandableHeightGridView;
import id.bl.blcom.iate.services.GpsTracker;
import io.realm.Realm;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class EditProfileActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 1;
    @BindView(R.id.ep_img_profile_photo)
    ImageView imageProfile;

    @BindView(R.id.ep_full_name)
    EditText fullName;
    @BindView(R.id.ep_no_hp)
    EditText noHp;
    @BindView(R.id.ep_email)
    EditText email;
    @BindView(R.id.ep_age)
    EditText age;
    @BindView(R.id.ep_status)
    Spinner status;
    @BindView(R.id.ep_shirt_size)
    EditText shirtSize;
    @BindView(R.id.ep_hobby)
    EditText hobby;
    @BindView(R.id.grid_interest)
    ExpandableHeightGridView interest;
    @BindView(R.id.ep_address)
    TextView address;
    @BindView(R.id.ep_wa)
    EditText wa;
    @BindView(R.id.ep_line)
    EditText line;
    @BindView(R.id.ep_bb)
    EditText bb;
    @BindView(R.id.ep_fb)
    EditText fb;
    @BindView(R.id.ep_twitter)
    EditText twitter;
    @BindView(R.id.ep_instagram)
    EditText instagram;
    @BindView(R.id.ep_work_place)
    EditText ep_work_place;

    @BindView(R.id.imgProfile)
    CircleImageView imgProfile;

    private double longitude, latitude;
    private GpsTracker gpsTracker;
    private Realm realmDB;
    private Profile profile;
    private List<String> mInterest;
    private List<String> mSelectedInterest;
    private Bitmap photoImage;
    private File fileImage;
    private ArrayList<File> photos = new ArrayList<>();
    private boolean isFromResult = false;
    private String provinceName = "";
    private String regenciesName = "";
    private String districtsName = "";
    private String villagesName = "";
    private String countryName = "Indonesia";
    private String fullAddress = "";
    private String streetAddress = "";
    private String postalCode = "";
    private Dialog dialog;
    private Spinner re_regencies;
    private Spinner re_districts;
    private Spinner re_villages;
    private Spinner re_country;

    @OnClick(R.id.ep_address)
    void onClickAddress(){
        showPopupAddress();
        EditText addre = dialog.findViewById(R.id.ep_address);
        Spinner re_province = dialog.findViewById(R.id.re_province);
        re_regencies = dialog.findViewById(R.id.re_regencies);
        re_districts = dialog.findViewById(R.id.re_districts);
        re_villages = dialog.findViewById(R.id.re_villages);
        EditText re_postal_code = dialog.findViewById(R.id.re_postal_code);
        re_country = dialog.findViewById(R.id.re_country);
        Button cp_edit_profile = dialog.findViewById(R.id.cp_edit_profile);

        String json = Utils.loadJSONFromAsset(this, "provinces.json");
        Type listUserType = new TypeToken<List<AddressDatum>>() { }.getType();
        List<AddressDatum> addressDatum = new Gson().fromJson(json, listUserType);
        Log.d("json", new Gson().toJson(addressDatum));

        setProvinceSpinner(addressDatum, re_province);
        setCountrySpinner();

        String addr = profile.getStreet_address();
        addre.setText(addr);
        re_postal_code.setText(profile.getPostal_address());

        cp_edit_profile.setOnClickListener(view -> {
            dialog.dismiss();
            streetAddress = addre.getText().toString();
            fullAddress = addre.getText().toString() + " " + villagesName + " " + districtsName + " " + regenciesName + " " + provinceName + " " + re_postal_code.getText().toString() + " " + countryName;
            postalCode = re_postal_code.getText().toString();

            address.setText(fullAddress);
        });

        re_regencies.setVisibility(View.GONE);
        re_districts.setVisibility(View.GONE);
        re_villages.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        interest.setExpanded(true);

        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = (datePicker, i, i1, i2) -> {
            calendar.set(Calendar.YEAR, i);
            calendar.set(Calendar.MONTH, i1);
            calendar.set(Calendar.DAY_OF_MONTH, i2);
            updateLabel(calendar);
        };
        age.setKeyListener(null);
        age.setOnClickListener(view1 -> new DatePickerDialog(this,
                date,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show());

        realmDB = LocalData.getRealm();
        profile = realmDB.where(Profile.class).findFirst();
        populateInterest();
        parseProfileData();

        String imgUrl = getIntent().getStringExtra("imgUrl");
        if (imgUrl != null) {
            if (!isFromResult) {
                Log.d("isFromResult", isFromResult+"");
                Glide.with(this).applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.img_profile_default)).load(imgUrl).into(imageProfile);
            }
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.status_array, R.layout.spinner_style);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        status.setAdapter(adapter);
        int spinnerPosition = adapter.getPosition(profile.getWork());
        status.setSelection(spinnerPosition);

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setProvinceSpinner(List<AddressDatum> addressDatum, Spinner re_province) {
        List<String> provinceList = new ArrayList<>();
        List<String> provinceIdList = new ArrayList<>();

        provinceList.add("Pilih Provinsi");
        provinceIdList.add("0");
        for (AddressDatum datum: addressDatum) {
            provinceList.add(datum.getName());
            provinceIdList.add(datum.getId());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter(EditProfileActivity.this, R.layout.spinner_style, provinceList) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                } else {
                    return true;
                }
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        re_province.setAdapter(adapter);

        re_province.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                if (position > 0) {
                    provinceName = selectedItemText;
                    setRegenciesSpinner(provinceIdList.get(position));
                    re_regencies.performClick();
                } else {
                    provinceName = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setRegenciesSpinner(String provinceId) {
        Api.apiInterface().getRegencies(provinceId).enqueue(new retrofit2.Callback<List<AddressDatum>>() {
            @Override
            public void onResponse(retrofit2.Call<List<AddressDatum>> call, retrofit2.Response<List<AddressDatum>> response) {
                try {
                    re_regencies.setVisibility(View.VISIBLE);
                    List<AddressDatum> datumList = response.body();
                    List<String> regenciesList = new ArrayList<>();
                    List<String> regenciesIdList = new ArrayList<>();

                    regenciesList.add("Pilih Kab/Kota");
                    regenciesIdList.add("0");
                    for (AddressDatum datum: datumList) {
                        regenciesList.add(datum.getName());
                        regenciesIdList.add(datum.getId());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter(EditProfileActivity.this, R.layout.spinner_style, regenciesList) {
                        @Override
                        public boolean isEnabled(int position) {
                            if (position == 0) {
                                return false;
                            } else {
                                return true;
                            }
                        }
                    };

                    adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                    re_regencies.setAdapter(adapter);

                    re_regencies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String selectedItemText = (String) parent.getItemAtPosition(position);
                            if (position > 0) {
                                regenciesName = selectedItemText;
                                setDistrictsSpinner(regenciesIdList.get(position));
                                re_districts.performClick();
                            } else {
                                regenciesName = "";
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<AddressDatum>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void setDistrictsSpinner(String regenciesId) {
        Api.apiInterface().getDistricts(regenciesId).enqueue(new retrofit2.Callback<List<AddressDatum>>() {
            @Override
            public void onResponse(retrofit2.Call<List<AddressDatum>> call, retrofit2.Response<List<AddressDatum>> response) {
                try {
                    re_districts.setVisibility(View.VISIBLE);
                    List<AddressDatum> datumList = response.body();
                    List<String> districtsList = new ArrayList<>();
                    List<String> districtsIdList = new ArrayList<>();

                    districtsList.add("Pilih Kecamatan");
                    districtsIdList.add("0");
                    for (AddressDatum datum: datumList) {
                        districtsList.add(datum.getName());
                        districtsIdList.add(datum.getId());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter(EditProfileActivity.this, R.layout.spinner_style, districtsList) {
                        @Override
                        public boolean isEnabled(int position) {
                            if (position == 0) {
                                return false;
                            } else {
                                return true;
                            }
                        }
                    };

                    adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                    re_districts.setAdapter(adapter);

                    re_districts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String selectedItemText = (String) parent.getItemAtPosition(position);
                            if (position > 0) {
                                districtsName = selectedItemText;
                                setVillagesSpinner(districtsIdList.get(position));
                                re_villages.performClick();
                            } else {
                                districtsName = "";
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<AddressDatum>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void setVillagesSpinner(String districtsId) {
        Api.apiInterface().getVillages(districtsId).enqueue(new retrofit2.Callback<List<AddressDatum>>() {
            @Override
            public void onResponse(retrofit2.Call<List<AddressDatum>> call, retrofit2.Response<List<AddressDatum>> response) {
                try {
                    re_villages.setVisibility(View.VISIBLE);
                    List<AddressDatum> datumList = response.body();
                    List<String> villagesList = new ArrayList<>();
                    List<String> villagesIdList = new ArrayList<>();

                    villagesList.add("Pilih Kelurahan");
                    villagesIdList.add("0");
                    for (AddressDatum datum: datumList) {
                        villagesList.add(datum.getName());
                        villagesIdList.add(datum.getId());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter(EditProfileActivity.this, R.layout.spinner_style, villagesList) {
                        @Override
                        public boolean isEnabled(int position) {
                            if (position == 0) {
                                return false;
                            } else {
                                return true;
                            }
                        }
                    };

                    adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                    re_villages.setAdapter(adapter);

                    re_villages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String selectedItemText = (String) parent.getItemAtPosition(position);
                            if (position > 0) {
                                villagesName = selectedItemText;
                                re_country.performClick();
                            } else {
                                villagesName = "";
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<AddressDatum>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void setCountrySpinner(){
        List<String> provinceList = new ArrayList<>();
        provinceList.add("Indonesia");

        ArrayAdapter<String> adapter = new ArrayAdapter(EditProfileActivity.this, R.layout.spinner_style, provinceList);

        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        re_country.setAdapter(adapter);

        re_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                countryName = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void getLocation(){
        gpsTracker = new GpsTracker(EditProfileActivity.this);
        if(gpsTracker.canGetLocation()){
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
        } else {
            gpsTracker.showSettingsAlert();
        }


        Log.d("latitude", latitude + "");
        Log.d("longitude", longitude + "");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void parseProfileData() {
        fullName.setText(profile.getFullname());
        noHp.setText(profile.getPhoneNumber());
        email.setText(profile.getEmail());
        age.setText(profile.getBirthDate());
        shirtSize.setText(profile.getShirtSize());
        hobby.setText(profile.getHobby());
        String addr = "-";
        if (profile.getStreet_address()!=null){
            addr = profile.getStreet_address() + ", " + profile.getSubdistrict_address() + ", " + profile.getDistrict_address() + ", " + profile.getProvince_address() + " " + profile.getPostal_address() + ", " + profile.getCountry_address();
        }
        address.setText(addr);

        ep_work_place.setText(profile.getWork_place());
        streetAddress = profile.getStreet_address();
        provinceName = profile.getProvince_address();
        regenciesName = profile.getCity_address();
        districtsName = profile.getDistrict_address();
        villagesName = profile.getSubdistrict_address();
        postalCode = profile.getPostal_address();
        countryName = profile.getCountry_address();

//        re_postal_code.setText(profile.getPostal_address());
//        re_province.setSelection();

        wa.setText(profile.getWhatsupNumber());
        line.setText(profile.getLineAccount());
        bb.setText(profile.getPinBbm());
        fb.setText(profile.getFacebookAccount());
        twitter.setText(profile.getTwitterAccount());
        instagram.setText(profile.getInstagramAccount());

        if (profile.getInterests() != null){
            mSelectedInterest = new LinkedList<>(Arrays.asList(profile.getInterests().split(",")));
        } else {
            mSelectedInterest = new LinkedList<>();
        }
        interest.setOnItemClickListener((adapterView, view, i, l) -> {
            TextView itemInterest = view.findViewById(R.id.interest_text);

            if (checkSelectedInterest(mInterest.get(i))){
                itemInterest.setBackgroundColor(getResources().getColor(R.color.darkGold));
                mSelectedInterest.remove(mInterest.get(i));
            } else {
                itemInterest.setBackgroundColor(getResources().getColor(R.color.darkRed));
                mSelectedInterest.add(mInterest.get(i));
            }
        });
    }

    void populateInterest() {

        InterestHelper.getInterest(new RestCallback<ApiResponse<List<Interest>>>() {
            @Override
            public void onSuccess(Headers headers, ApiResponse<List<Interest>> body) {
                if (body != null && body.isStatus()){
                    mInterest = new ArrayList();
                    List<Interest> data = body.getData();
                    for (int i = 0; i < data.size(); i++){
                        mInterest.add(data.get(i).getName());
                    }
                    interest.setAdapter(new AdapterInterest(EditProfileActivity.this, mInterest, profile.getInterests()));
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

    private boolean checkSelectedInterest(String interest){
        for(String str: mSelectedInterest) {
            if(str.trim().contains(interest))
                return true;
        }
        return false;
    }

    @OnClick(R.id.img_change_profile_photo)
    void onClickChangeProfilePhoto(){
//        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
//                == PackageManager.PERMISSION_DENIED){
//            ActivityCompat.requestPermissions(getActivity(),
//                    new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
//        } else {
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            startActivityForResult(intent, CAMERA_REQUEST_CODE);
//        }

        selectImage();
    }

    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        builder.setTitle("Select Option");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Take Photo")) {
                EasyImage.openCameraForImage(EditProfileActivity.this, CAMERA_REQUEST_CODE);
//                isFromResult = true;
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            } else if (options[item].equals("Choose From Gallery")) {
                EasyImage.openGallery(EditProfileActivity.this, 2);
                isFromResult = true;
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 2);
            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        isFromResult = true;
        Log.d("isFromResult", isFromResult+"");
//        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK){
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            photoImage = (Bitmap) data.getExtras().get("data");
//            photoImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//            imageProfile.setImageBitmap(photoImage); imageProfile.setVisibility(View.GONE);
//            imgProfile.setImageBitmap(photoImage);
//            try {
//                File outputDir = EditProfileActivity.this.getCacheDir();
//                fileImage = File.createTempFile("photo", "jpeg", outputDir);
//                FileOutputStream outputStream = EditProfileActivity.this.openFileOutput("photo.jpeg", Context.MODE_PRIVATE);
//                outputStream.write(stream.toByteArray());
//                outputStream.close();
//                Log.d("Write File", "Success");
//            } catch (IOException e) {
//                e.printStackTrace();
//                Log.d("Write File", "Failed2");
//            }
//        }
//
//        if (requestCode == 2 && resultCode == RESULT_OK){
//            try {
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                final Uri imageUri = data.getData();
//                final InputStream imageStream = getContentResolver().openInputStream(Objects.requireNonNull(imageUri));
//                photoImage = BitmapFactory.decodeStream(imageStream);
//                photoImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                imageProfile.setImageBitmap(photoImage); imageProfile.setVisibility(View.GONE);
//                imgProfile.setImageBitmap(photoImage);
//                try {
//                    File outputDir = EditProfileActivity.this.getCacheDir();
//                    fileImage = File.createTempFile("photo", "jpeg", outputDir);
//                    FileOutputStream outputStream = EditProfileActivity.this.openFileOutput("photo.jpeg", Context.MODE_PRIVATE);
//                    outputStream.write(stream.toByteArray());
//                    outputStream.close();
//                    Log.d("Write File", "Success");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Log.d("Write File", "Failed2");
//                }
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//                Toast.makeText(EditProfileActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
//            }
//        }

        EasyImage.handleActivityResult(requestCode, resultCode, data, EditProfileActivity.this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
                e.printStackTrace();
            }

            @Override
            public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {
                Log.d("photos", new Gson().toJson(imageFiles));
                onPhotosReturned(imageFiles);
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA_IMAGE) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(EditProfileActivity.this);
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private void onPhotosReturned(List<File> returnedPhotos) {
        photos.addAll(returnedPhotos);
        if (photos.size()>0){
            Log.d("photos", new Gson().toJson(photos));
            Glide.with(EditProfileActivity.this).load(photos.get(0)).into(imageProfile);
            fileImage = photos.get(0);
            String filePath = fileImage.getPath();
            photoImage = BitmapFactory.decodeFile(filePath);

            Bitmap bitmap = BitmapFactory.decodeFile(fileImage.getAbsolutePath());
            ExifInterface ei = null;
            try {
                ei = new ExifInterface(fileImage.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap rotatedBitmap = null;
            switch(orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }

            photoImage = rotatedBitmap;
            imageProfile.setVisibility(View.GONE);
            imgProfile.setImageBitmap(photoImage);

        }
    }

    @OnClick(R.id.cp_edit_profile)
    void onClickUpdateProfile(){
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        getLocation();

        realmDB.beginTransaction();
        profile.setFullname(fullName.getText().toString());
        profile.setPhoneNumber(noHp.getText().toString());
        profile.setBirthDate(age.getText().toString());
        profile.setEmail(email.getText().toString());
        profile.setWork(status.getSelectedItem().toString());
        profile.setShirtSize(shirtSize.getText().toString());
        profile.setHobby(hobby.getText().toString());
        profile.setPostal_address(postalCode);
        profile.setAddress(fullAddress);
        profile.setStreet_address(streetAddress);
        profile.setSubdistrict_address(villagesName);
        profile.setDistrict_address(districtsName);
        profile.setCity_address(regenciesName);
        profile.setProvince_address(provinceName);
        profile.setCountry_address(countryName);
        profile.setWork_place(ep_work_place.getText().toString());
        profile.setWhatsupNumber(wa.getText().toString());
        profile.setLineAccount(line.getText().toString());
        profile.setPinBbm(bb.getText().toString());
        profile.setFacebookAccount(fb.getText().toString());
        profile.setTwitterAccount(twitter.getText().toString());
        profile.setInstagramAccount(instagram.getText().toString());
        StringBuilder sb = new StringBuilder();
        if (mSelectedInterest.size() > 0){
            for (int i = 0; i < mSelectedInterest.size(); i++) {
                if (!mSelectedInterest.get(i).trim().equals("")) {
                    sb.append(mSelectedInterest.get(i));
                    if (i != mSelectedInterest.size() - 1) {
                        sb.append(",");
                    }
                }
            }
            profile.setInterests(sb.toString());
        } else {
            profile.setInterests("");
        }

        realmDB.commitTransaction();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        File file = new File(EditProfileActivity.this.getCacheDir(), "file.jpeg");
        RequestBody requestBody;

        if (photoImage != null){
            photoImage.compress(Bitmap.CompressFormat.JPEG, 50, stream);

            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("fullname", profile.getFullname())
                    .addFormDataPart("phone_number", profile.getPhoneNumber())
                    .addFormDataPart("email", profile.getEmail())
                    .addFormDataPart("birth_date", profile.getBirthDate())
                    .addFormDataPart("work", profile.getWork())
                    .addFormDataPart("shirt_size", profile.getShirtSize())
                    .addFormDataPart("hobby", profile.getHobby())
                    .addFormDataPart("interests", profile.getInterests())
                    .addFormDataPart("address", profile.getAddress())
                    .addFormDataPart("street_address", profile.getStreet_address())
                    .addFormDataPart("postal_address", profile.getPostal_address())
                    .addFormDataPart("subdistrict_address", profile.getSubdistrict_address())
                    .addFormDataPart("district_address", profile.getDistrict_address())
                    .addFormDataPart("city_address", profile.getCity_address())
                    .addFormDataPart("province_address", profile.getProvince_address())
                    .addFormDataPart("country_address", profile.getCountry_address())
                    .addFormDataPart("work_place", profile.getWork_place())
                    .addFormDataPart("whatsapp_number", profile.getWhatsupNumber())
                    .addFormDataPart("line_account", profile.getLineAccount())
                    .addFormDataPart("pin_bbm", profile.getPinBbm())
                    .addFormDataPart("facebook_account", profile.getFacebookAccount())
                    .addFormDataPart("twitter_account", profile.getTwitterAccount())
                    .addFormDataPart("instagram_account", profile.getInstagramAccount())
                    .addFormDataPart("current_latitude", latitude + "")
                    .addFormDataPart("current_longitude", longitude + "")
                    .addFormDataPart("picture", "photo.jpeg", RequestBody.create(MediaType.parse("image/*"), stream.toByteArray()))
                    .build();
        } else {
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("fullname", profile.getFullname())
                    .addFormDataPart("phone_number", profile.getPhoneNumber())
                    .addFormDataPart("email", profile.getEmail())
                    .addFormDataPart("birth_date", profile.getBirthDate())
                    .addFormDataPart("work", profile.getWork())
                    .addFormDataPart("shirt_size", profile.getShirtSize())
                    .addFormDataPart("hobby", profile.getHobby())
                    .addFormDataPart("interests", profile.getInterests())
                    .addFormDataPart("address", profile.getAddress())
                    .addFormDataPart("street_address", profile.getStreet_address())
                    .addFormDataPart("postal_address", profile.getPostal_address())
                    .addFormDataPart("subdistrict_address", profile.getSubdistrict_address())
                    .addFormDataPart("district_address", profile.getDistrict_address())
                    .addFormDataPart("city_address", profile.getCity_address())
                    .addFormDataPart("province_address", profile.getProvince_address())
                    .addFormDataPart("country_address", profile.getCountry_address())
                    .addFormDataPart("work_place", profile.getWork_place())
                    .addFormDataPart("whatsapp_number", profile.getWhatsupNumber())
                    .addFormDataPart("line_account", profile.getLineAccount())
                    .addFormDataPart("pin_bbm", profile.getPinBbm())
                    .addFormDataPart("facebook_account", profile.getFacebookAccount())
                    .addFormDataPart("twitter_account", profile.getTwitterAccount())
                    .addFormDataPart("instagram_account", profile.getInstagramAccount())
                    .addFormDataPart("current_latitude", latitude + "")
                    .addFormDataPart("current_longitude", longitude + "")
                    .build();
        }

        PrivacyHelper.updateProfile(requestBody, new RestCallback<ApiResponse<Profile>>() {
            @Override
            public void onSuccess(Headers headers, ApiResponse<Profile> body) {
                loadingDialog.dismiss();
                Log.d("Update Success", "Update edit profile success");
                Toast.makeText(EditProfileActivity.this, "Update Profile success", Toast.LENGTH_SHORT).show();
                Profile profileData = body.getData();
                realmDB.beginTransaction();
                profile.setPicture(profileData.getPicture());
                realmDB.commitTransaction();

//                ImageView profileImage = findViewById(R.id.profile_image);
//                TextView fullname = findViewById(R.id.full_name);
//                fullname.setText(profile.getFullname());
//                Glide.with(EditProfileActivity.this).load(profile.getPicture()).into(profileImage);

//                EditProfileActivity.this.getSupportFragmentManager().popBackStack();

                onBackPressed();
                finish();
            }

            @Override
            public void onFailed(ErrorResponse error) {
                loadingDialog.dismiss();
                Log.d("Update Failed", "Update edit profile failed");
            }

            @Override
            public void onCanceled() {
                Log.d("Update canceled", "Update edit profile canceled");
            }
        });
    }

    private void updateLabel(Calendar calendar){
        String format = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);
        age.setText(simpleDateFormat.format(calendar.getTime()));
    }

    private void showPopupAddress() {
        dialog = new Dialog(EditProfileActivity.this);
        //set content
        dialog.setContentView(R.layout.dialog_change_address);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
}