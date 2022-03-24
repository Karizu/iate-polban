package id.bl.blcom.iate;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rezkyatinnov.kyandroid.reztrofit.ErrorResponse;
import com.rezkyatinnov.kyandroid.reztrofit.RestCallback;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.bl.blcom.iate.api.AddressHelper;
import id.bl.blcom.iate.api.Api;
import id.bl.blcom.iate.api.ApiInterface;
import id.bl.blcom.iate.api.GroupHelper;
import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.models.Group;
import id.bl.blcom.iate.models.response.AddressDatum;
import id.bl.blcom.iate.presentation.LoadingDialog;
import id.bl.blcom.iate.presentation.Utils;
import id.bl.blcom.iate.presentation.diskusi.DetailDiskusiActivity;
import id.bl.blcom.iate.presentation.login.LoginActivity;
import id.bl.blcom.iate.presentation.profile.EditProfileActivity;
import id.bl.blcom.iate.services.GpsTracker;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class RegisterActivity extends Activity {

    private static final int CAMERA_REQUEST_CODE = 1;
    @BindView(R.id.re_full_name)
    EditText fullname;
    @BindView(R.id.re_birth_date)
    EditText birthDate;
    @BindView(R.id.re_phone_number)
    EditText phoneNumber;
    @BindView(R.id.re_email)
    EditText email;
    @BindView(R.id.re_dso)
    Spinner dso;
    @BindView(R.id.re_username)
    EditText username;
    @BindView(R.id.re_password)
    EditText password;
    @BindView(R.id.re_province)
    Spinner re_province;
    @BindView(R.id.re_regencies)
    Spinner re_regencies;
    @BindView(R.id.re_districts)
    Spinner re_districts;
    @BindView(R.id.re_villages)
    Spinner re_villages;
    @BindView(R.id.re_country)
    Spinner re_country;
    @BindView(R.id.re_address)
    EditText re_address;
    @BindView(R.id.re_postal_code)
    EditText re_postal_code;

    private GpsTracker gpsTracker;
    Bitmap photoImage;
    File fileImage;
    ArrayList<String> dsoId;
    Context appContext;
    Dialog dialog;

    private LoadingDialog loadingDialog;
    private double longitude, latitude;
    private ArrayList<File> photos = new ArrayList<>();
    private SharedPreferences prefs;
    private String provinceName = "";
    private String regenciesName = "";
    private String districtsName = "";
    private String villagesName = "";
    private String countryName = "Indonesia";
    private String groupName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        getArea(this);
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(Calendar.YEAR, i);
                calendar.set(Calendar.MONTH, i1);
                calendar.set(Calendar.DAY_OF_MONTH, i2);
                updateLabel(calendar);
            }
        };
        birthDate.setKeyListener(null);
        birthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(RegisterActivity.this,
                        date,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        appContext = this;
        loadingDialog = new LoadingDialog(this);

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        prefs = this.getSharedPreferences(
                "preference", Context.MODE_PRIVATE);

        String json = Utils.loadJSONFromAsset(this, "provinces.json");
        Type listUserType = new TypeToken<List<AddressDatum>>() { }.getType();
        List<AddressDatum> addressDatum = new Gson().fromJson(json, listUserType);
        Log.d("json", new Gson().toJson(addressDatum));

        re_regencies.setVisibility(View.GONE);
        re_districts.setVisibility(View.GONE);
        re_villages.setVisibility(View.GONE);

        setProvinceSpinner(addressDatum);
        setCountrySpinner();
    }

    private void setProvinceSpinner(List<AddressDatum> addressDatum) {
        List<String> provinceList = new ArrayList<>();
        List<String> provinceIdList = new ArrayList<>();

        provinceList.add("Pilih Provinsi");
        provinceIdList.add("0");
        for (AddressDatum datum: addressDatum) {
            provinceList.add(datum.getName());
            provinceIdList.add(datum.getId());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter(RegisterActivity.this, R.layout.spinner_style, provinceList) {
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

                    ArrayAdapter<String> adapter = new ArrayAdapter(RegisterActivity.this, R.layout.spinner_style, regenciesList) {
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

                    ArrayAdapter<String> adapter = new ArrayAdapter(RegisterActivity.this, R.layout.spinner_style, districtsList) {
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

                    ArrayAdapter<String> adapter = new ArrayAdapter(RegisterActivity.this, R.layout.spinner_style, villagesList) {
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

        ArrayAdapter<String> adapter = new ArrayAdapter(RegisterActivity.this, R.layout.spinner_style, provinceList);

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
        gpsTracker = new GpsTracker(RegisterActivity.this);
        if(gpsTracker.canGetLocation()){
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
        } else {
            gpsTracker.showSettingsAlert();
        }


        Log.d("latitude", latitude + "");
        Log.d("longitude", longitude + "");
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    @OnClick(R.id.img_change_profile_photo)
    void onClickChangeProfilePhoto(){
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(RegisterActivity.this,
                    new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
            try {
                final CharSequence[] options = {"Take Photo", "Choose From Gallery","Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(appContext);
                builder.setTitle("Select Option");
                builder.setItems(options, (dialog, item) -> {
                    if (options[item].equals("Take Photo")) {
                        EasyImage.openCameraForImage(RegisterActivity.this, 1111);
                    } else if (options[item].equals("Choose From Gallery")) {
                        EasyImage.openGallery(RegisterActivity.this, 2222);
                    } else if (options[item].equals("Cancel")) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            } catch (Exception e){
                e.printStackTrace();
            }
        } else {
            final CharSequence[] options = {"Take Photo", "Choose From Gallery","Cancel"};
            AlertDialog.Builder builder = new AlertDialog.Builder(appContext);
            builder.setTitle("Select Option");
            builder.setItems(options, (dialog, item) -> {
                if (options[item].equals("Take Photo")) {
                    EasyImage.openCameraForImage(RegisterActivity.this, 1111);
                } else if (options[item].equals("Choose From Gallery")) {
                    EasyImage.openGallery(RegisterActivity.this, 2222);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }

    @OnClick(R.id.re_btn_register)
    void onClickBtnRegister(){
        getLocation();

        if (fullname.getText().toString().equals("")) {
            Toast.makeText(appContext, "Nama lengkap harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (birthDate.getText().toString().equals("")) {
            Toast.makeText(appContext, "Tanggal lahir harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phoneNumber.getText().toString().equals("")) {
            Toast.makeText(appContext, "Nomor handphone harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.getText().toString().equals("")){
            Toast.makeText(appContext, "Email harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (groupName.equals("")){
            Toast.makeText(appContext, "Silahkan pilih keanggotaan", Toast.LENGTH_SHORT).show();
        }

        if (re_address.getText().toString().equals("") || provinceName.equals("") || regenciesName.equals("") || districtsName.equals("") || villagesName.equals("") || countryName.equals("") || re_postal_code.getText().toString().equals("")) {
            Toast.makeText(appContext, "Silahkan lengkapi alamat", Toast.LENGTH_SHORT).show();
            return;
        }

        if (username.getText().toString().equals("")){
            Toast.makeText(appContext, "Username harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.getText().toString().equals("")) {
            Toast.makeText(appContext, "Password harus diisi", Toast.LENGTH_SHORT).show();
        }

        loadingDialog.setCancelable(false);
        loadingDialog.show();
        post(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadingDialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                loadingDialog.dismiss();
                if (response.isSuccessful()){
                    Log.d("Response", "Response successful");
                    Log.d("Body", response.body().string());

                    prefs.edit().putBoolean("isFromRegister", true).apply();

                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.putExtra("username", username.getText().toString());
                    intent.putExtra("password", password.getText().toString());
                    intent.putExtra("message", "success register");
                    startActivity(intent);
                    finish();
                }else{
                    runOnUiThread(() -> Toast.makeText(appContext, "Username has already been taken.", Toast.LENGTH_SHORT).show());
                    Log.d("Response Failed", response.message());
                    Log.d("Body", response.body().string());
                }
            }
        });
    }

    Call post(Callback callback){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Log.d("photo", "check photo");
        File file = new File(getCacheDir(), "file.jpeg");
        Log.d("photo", "check after take foto");
        OkHttpClient client = new OkHttpClient();
        String fileName = "file.jpeg";
        RequestBody body;

        String address = re_address.getText().toString() + " " + villagesName + " " + districtsName + " " + regenciesName + " " + provinceName + " " + countryName + " " + re_postal_code;

        if (photoImage != null) {
            photoImage.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("fullname", fullname.getText().toString())
                    .addFormDataPart("birth_date", birthDate.getText().toString())
                    .addFormDataPart("phone_number", phoneNumber.getText().toString())
                    .addFormDataPart("email", email.getText().toString())
                    .addFormDataPart("group_id", dsoId.get(dso.getSelectedItemPosition()))
                    .addFormDataPart("username", username.getText().toString())
                    .addFormDataPart("password", password.getText().toString())
                    .addFormDataPart("origin_latitude", latitude+"")
                    .addFormDataPart("origin_longitude", longitude+"")
                    .addFormDataPart("address", address)
                    .addFormDataPart("street_address", re_address.getText().toString())
                    .addFormDataPart("postal_address", re_postal_code.getText().toString())
                    .addFormDataPart("subdistrict_address", villagesName)
                    .addFormDataPart("district_address", districtsName)
                    .addFormDataPart("city_address", regenciesName)
                    .addFormDataPart("province_address", provinceName)
                    .addFormDataPart("country_address", countryName)
                    .addFormDataPart("picture", "photo.jpeg", RequestBody.create(MediaType.parse("image/jpeg"), stream.toByteArray()))
                    .build();
        } else {
            body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("fullname", fullname.getText().toString())
                    .addFormDataPart("birth_date", birthDate.getText().toString())
                    .addFormDataPart("phone_number", phoneNumber.getText().toString())
                    .addFormDataPart("email", email.getText().toString())
                    .addFormDataPart("group_id", dsoId.get(dso.getSelectedItemPosition()))
                    .addFormDataPart("username", username.getText().toString())
                    .addFormDataPart("password", password.getText().toString())
                    .addFormDataPart("origin_latitude", latitude+"")
                    .addFormDataPart("origin_longitude", longitude+"")
                    .addFormDataPart("address", address)
                    .addFormDataPart("street_address", re_address.getText().toString())
                    .addFormDataPart("postal_address", re_postal_code.getText().toString())
                    .addFormDataPart("subdistrict_address", villagesName)
                    .addFormDataPart("district_address", districtsName)
                    .addFormDataPart("city_address", regenciesName)
                    .addFormDataPart("province_address", provinceName)
                    .addFormDataPart("country_address", countryName)
                    .build();
        }

        Request request = new Request.Builder()
                .url(ApiInterface.BASE_URL + "register")
                .post(body)
                .build();

        Log.d("Response", "before response");
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    @OnClick(R.id.tv_have_account)
    void haveAccount(){
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK){
//            ImageView img = findViewById(R.id.re_img_profile_photo);
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            photoImage = (Bitmap) data.getExtras().get("data");
//            photoImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//            img.setImageBitmap(photoImage);
//            try {
//                File outputDir = getCacheDir();
//                fileImage = File.createTempFile("photo", "jpeg", outputDir);
//                FileOutputStream outputStream = openFileOutput("photo.jpeg", Context.MODE_PRIVATE);
//                outputStream.write(stream.toByteArray());
//                outputStream.close();
//                Log.d("Write File", "Success");
//            } catch (IOException e) {
//                e.printStackTrace();
//                Log.d("Write File", "Failed2");
//            }
//        }

        EasyImage.handleActivityResult(requestCode, resultCode, data, RegisterActivity.this, new DefaultCallback() {
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
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(RegisterActivity.this);
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
            ImageView img = findViewById(R.id.re_img_profile_photo);
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
            img.setImageBitmap(photoImage);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void getArea(Activity activity){
        GroupHelper.getGroup(new RestCallback<ApiResponse<List<Group>>>() {
            @Override
            public void onSuccess(Headers headers, ApiResponse<List<Group>> body) {
                if (body != null && body.isStatus()){
                    ArrayList<String> dataDSO = new ArrayList();
                    dsoId = new ArrayList();
                    List<Group> data = body.getData();
                    for (int i = 0; i < data.size(); i++){
                        if (!data.get(i).getParent()){
                            dataDSO.add(data.get(i).getName());
                            dsoId.add(data.get(i).getId());
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter(activity, R.layout.spinner_style, dataDSO);
                    adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                    dso.setAdapter(adapter);

                    dso.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            groupName = (String) adapterView.getItemAtPosition(i);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
            }

            @Override
            public void onFailed(ErrorResponse error) {
                Log.d("teststst", "failed!");
            }

            @Override
            public void onCanceled() {
                Log.d("teststst", "failed!");
            }
        });
    }

    private void updateLabel(Calendar calendar){
        String format = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);

        birthDate.setText(simpleDateFormat.format(calendar.getTime()));
    }

    @OnClick(R.id.tnc)
    public void showTnc() {
        showPopup(R.layout.dialog_term_and_condition);
    }

    private void showPopup(int layout) {
        dialog = new Dialog(Objects.requireNonNull(RegisterActivity.this));
        //SET TITLE
        dialog.setTitle("Syarat & Ketentuan");

        //set content
        dialog.setContentView(layout);
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
