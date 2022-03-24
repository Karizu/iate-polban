package id.bl.blcom.iate.presentation.profile;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.rezkyatinnov.kyandroid.localdata.LocalData;
import com.rezkyatinnov.kyandroid.reztrofit.ErrorResponse;
import com.rezkyatinnov.kyandroid.reztrofit.RestCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import id.bl.blcom.iate.R;
import id.bl.blcom.iate.api.ApiInterface;
import id.bl.blcom.iate.api.InterestHelper;
import id.bl.blcom.iate.api.PrivacyHelper;
import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.models.Interest;
import id.bl.blcom.iate.models.Profile;
import id.bl.blcom.iate.presentation.NavigationDrawerActivity;
import id.bl.blcom.iate.presentation.diskusi.CreateDiskusiActivity;
import id.bl.blcom.iate.services.AdapterInterest;
import id.bl.blcom.iate.services.ExpandableHeightGridView;
import io.realm.Realm;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import static android.app.Activity.RESULT_OK;

public class EditProfileFragment extends Fragment {
    private static final int CAMERA_REQUEST_CODE = 1;
    @BindView(R.id.ep_img_profile_photo)
    CircleImageView imageProfile;

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
    EditText address;
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

    private Realm realmDB;
    private Profile profile;
    private List<String> mInterest;
    private List<String> mSelectedInterest;
    private Bitmap photoImage;
    private File fileImage;
    private ArrayList<File> photos = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        ((NavigationDrawerActivity)getActivity()).showBackMenu(true);
        interest.setExpanded(true);

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
        age.setKeyListener(null);
        age.setOnClickListener(view1 -> new DatePickerDialog(getContext(),
                date,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show());

        realmDB = LocalData.getRealm();
        profile = realmDB.where(Profile.class).findFirst();
        populateInterest();

        parseProfileData();

        String imgUrl = getArguments().getString("imgUrl");
        if (imgUrl != null)
            Glide.with(this).applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.img_profile_default)).load(imgUrl).into(imageProfile);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.status_array, R.layout.spinner_style);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        status.setAdapter(adapter);
        int spinnerPosition = adapter.getPosition(profile.getWork());
        status.setSelection(spinnerPosition);

    }

    void parseProfileData() {
        fullName.setText(profile.getFullname());
        noHp.setText(profile.getPhoneNumber());
        email.setText(profile.getEmail());
        age.setText(profile.getBirthDate());
        shirtSize.setText(profile.getShirtSize());
        hobby.setText(profile.getHobby());
        address.setText(profile.getAddress());

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
                    interest.setAdapter(new AdapterInterest(getContext(), mInterest, profile.getInterests()));
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
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Select Option");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Take Photo")) {
                EasyImage.openCameraForImage(requireActivity(), CAMERA_REQUEST_CODE);
            } else if (options[item].equals("Choose From Gallery")) {
                EasyImage.openGallery(requireActivity(), 2);
            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK){
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            photoImage = (Bitmap) data.getExtras().get("data");
//            photoImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//            imageProfile.setImageBitmap(photoImage);
//            try {
//                File outputDir = getContext().getCacheDir();
//                fileImage = File.createTempFile("photo", "jpeg", outputDir);
//                FileOutputStream outputStream = getContext().openFileOutput("photo.jpeg", Context.MODE_PRIVATE);
//                outputStream.write(stream.toByteArray());
//                outputStream.close();
//                Log.d("Write File", "Success");
//            } catch (IOException e) {
//                e.printStackTrace();
//                Log.d("Write File", "Failed2");
//            }
//        }

        EasyImage.handleActivityResult(requestCode, resultCode, data, requireActivity(), new DefaultCallback() {
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
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(requireActivity());
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }

    private void onPhotosReturned(List<File> returnedPhotos) {
        photos.addAll(returnedPhotos);
        if (photos.size()>0){
            Log.d("photos", new Gson().toJson(photos));
            Glide.with(requireActivity()).load(photos.get(0)).into(imageProfile);
            fileImage = photos.get(0);
            String filePath = fileImage.getPath();
            photoImage = BitmapFactory.decodeFile(filePath);

        }
    }

    @OnClick(R.id.cp_edit_profile)
    void onClickUpdateProfile(){

        realmDB.beginTransaction();
        profile.setFullname(fullName.getText().toString());
        profile.setPhoneNumber(noHp.getText().toString());
        profile.setBirthDate(age.getText().toString());
        profile.setEmail(email.getText().toString());
        profile.setWork(status.getSelectedItem().toString());
        profile.setShirtSize(shirtSize.getText().toString());
        profile.setHobby(hobby.getText().toString());
        profile.setAddress(address.getText().toString());
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

        File file = new File(getContext().getCacheDir(), "file.jpeg");
        RequestBody requestBody;

        if (photoImage != null){
            photoImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);

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
                    .addFormDataPart("whatsapp_number", profile.getWhatsupNumber())
                    .addFormDataPart("line_account", profile.getLineAccount())
                    .addFormDataPart("pin_bbm", profile.getPinBbm())
                    .addFormDataPart("facebook_account", profile.getFacebookAccount())
                    .addFormDataPart("twitter_account", profile.getTwitterAccount())
                    .addFormDataPart("instagram_account", profile.getInstagramAccount())
                    .addFormDataPart("picture", "photo.jpeg", RequestBody.create(MediaType.parse("image/jpeg"), stream.toByteArray()))
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
                    .addFormDataPart("whatsapp_number", profile.getWhatsupNumber())
                    .addFormDataPart("line_account", profile.getLineAccount())
                    .addFormDataPart("pin_bbm", profile.getPinBbm())
                    .addFormDataPart("facebook_account", profile.getFacebookAccount())
                    .addFormDataPart("twitter_account", profile.getTwitterAccount())
                    .addFormDataPart("instagram_account", profile.getInstagramAccount())
                    .build();
        }

        PrivacyHelper.updateProfile(requestBody, new RestCallback<ApiResponse<Profile>>() {
            @Override
            public void onSuccess(Headers headers, ApiResponse<Profile> body) {
                Log.d("Update Success", "Update edit profile success");
                Toast.makeText(getContext(), "Update Profile success", Toast.LENGTH_SHORT).show();
                Profile profileData = body.getData();
                realmDB.beginTransaction();
                profile.setPicture(profileData.getPicture());
                realmDB.commitTransaction();

                ImageView profileImage = getActivity().findViewById(R.id.profile_image);
                TextView fullname = getActivity().findViewById(R.id.full_name);
                fullname.setText(profile.getFullname());
                Glide.with(getActivity()).load(ApiInterface.BASE_URL_IMAGE + profile.getPicture()).into(profileImage);

                getActivity().getSupportFragmentManager().popBackStack();
            }

            @Override
            public void onFailed(ErrorResponse error) {
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
}
