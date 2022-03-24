package id.bl.blcom.iate.presentation.diskusi;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.rezkyatinnov.kyandroid.localdata.LocalData;
import com.rezkyatinnov.kyandroid.session.Session;
import com.rezkyatinnov.kyandroid.session.SessionNotFoundException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.bl.blcom.iate.R;
import id.bl.blcom.iate.api.DiscussionHelper;
import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.models.Profile;
import id.bl.blcom.iate.presentation.LoadingDialog;
import id.bl.blcom.iate.presentation.Utils;
import io.realm.Realm;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class CreateDiskusiActivity extends AppCompatActivity {

    @BindView(R.id.caption_container)
    LinearLayout captionContainer;

    @BindView(R.id.image_container)
    LinearLayout imageContainer;

    @BindView(R.id.voting_container)
    LinearLayout votingContainer;

    @BindView(R.id.create_caption)
    CheckedTextView createCaption;

    @BindView(R.id.create_image)
    CheckedTextView createImage;

    @BindView(R.id.create_voting)
    CheckedTextView createVoting;

    @BindView(R.id.vote_list_container)
    LinearLayout voteListContainer;

    @BindView(R.id.button_add_votelist)
    ImageView addVoteList;

    @BindView(R.id.caption_title)
    EditText captionTitle;

    @BindView(R.id.caption_caption)
    EditText captionCaption;

    @BindView(R.id.image_title)
    EditText imageTitle;

    @BindView(R.id.image_caption)
    EditText imageCaption;

    @BindView(R.id.image_imagePost)
    ImageView imagePost;

    @BindView(R.id.voting_title)
    EditText votingTitle;

    @BindView(R.id.voting_caption)
    EditText votingCaption;

    @BindView(R.id.group_name)
    TextView groupName;

    private int numberOfLines = 0;
    private int type = 1;
    private final int CAPTION = 1, IMAGE = 2, POLLING = 3;
    private final int REQEUST_CAMERA = 1, REQUEST_GALLERY = 2;
    private String groupId;
    private Profile profile;
    private File imagePostFile;
    private Dialog dialog;
    private EasyImage easyImage;
    private ArrayList<File> photos = new ArrayList<>();

    private EditText ed;
    private List<EditText> allEds;
    ArrayList<String> strings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_diskusi);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        groupId = intent.getStringExtra("group_id");
        try {
            groupId = !groupId.equalsIgnoreCase("null")?groupId: Session.get("group_id").getValue();
            Log.d("groupID", groupId);
        } catch (SessionNotFoundException e) {
            e.printStackTrace();
        }

        groupName.setText(intent.getStringExtra("group_name"));

        Realm realm = LocalData.getRealm();
        profile = realm.where(Profile.class).findFirst();

        allEds = new ArrayList<EditText>();

        captionContainer.setVisibility(View.VISIBLE);

        createCaption.setOnClickListener(view -> {
            createCaption.setChecked(true);
            createImage.setChecked(false);
            createVoting.setChecked(false);
            captionContainer.setVisibility(View.VISIBLE);
            imageContainer.setVisibility(View.GONE);
            votingContainer.setVisibility(View.GONE);
            type = 1;
        });

        createImage.setOnClickListener(view -> {
            createCaption.setChecked(false);
            createImage.setChecked(true);
            createVoting.setChecked(false);
            captionContainer.setVisibility(View.GONE);
            imageContainer.setVisibility(View.VISIBLE);
            votingContainer.setVisibility(View.GONE);
            type = 2;
        });

        createVoting.setOnClickListener(view -> {
            createCaption.setChecked(false);
            createImage.setChecked(false);
            createVoting.setChecked(true);
            captionContainer.setVisibility(View.GONE);
            imageContainer.setVisibility(View.GONE);
            votingContainer.setVisibility(View.VISIBLE);
            type = 3;
        });

        addVoteList.setOnClickListener(view -> addVoteList());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_create_diskusi, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_post:
                createPost();
                break;
            case android.R.id.home:
                // do something useful
                finish();
                return (true);
        }
        return super.onOptionsItemSelected(item);

    }

    @OnClick(R.id.chooseImage)
    void onChooseImageClick() {
        selectImage();
    }

    void createPost() {
        RequestBody requestBody = null;
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        switch (type) {
            case CAPTION:
                requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("group_id", groupId)
                        .addFormDataPart("user_id", profile.getUserId())
                        .addFormDataPart("title", captionTitle.getText().toString())
                        .addFormDataPart("caption", captionCaption.getText().toString())
                        .addFormDataPart("type", String.valueOf(type))
                        .build();
                break;

            case IMAGE:
                Bitmap bitmap = BitmapFactory.decodeFile(imagePostFile.getAbsolutePath());
                ExifInterface ei = null;
                try {
                    ei = new ExifInterface(imagePostFile.getPath());
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

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);

                requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("group_id", groupId)
                        .addFormDataPart("user_id", profile.getUserId())
                        .addFormDataPart("title", imageTitle.getText().toString())
                        .addFormDataPart("caption", imageCaption.getText().toString())
                        .addFormDataPart("type", String.valueOf(type))
                        .addFormDataPart("content", "photo.jpeg", RequestBody.create(MediaType.parse("image/jpeg"), byteArrayOutputStream.toByteArray()))
                        .build();
                break;

            case POLLING:
                strings = new ArrayList<>();

                for (int i = 0; i < allEds.size(); i++) {
                    strings.add(allEds.get(i).getText().toString());
                }

                MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("group_id", groupId)
                        .addFormDataPart("user_id", profile.getUserId())
                        .addFormDataPart("title", votingTitle.getText().toString())
                        .addFormDataPart("caption", votingCaption.getText().toString())
                        .addFormDataPart("type", String.valueOf(type));

                for (int i = 0; i < strings.size(); i++) {
                    multipartBuilder.addFormDataPart("polling[][option]", strings.get(i));
                }

                requestBody = multipartBuilder.build();

                break;

            default:

                break;
        }

        try {

            DiscussionHelper.createPost(requestBody, new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    loadingDialog.dismiss();
                    if (response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Create Post Success", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        showDialog();
                        Button btn = dialog.findViewById(R.id.btn_ok);
                        btn.setOnClickListener(v -> dialog.dismiss());
                        TextView tvDesc = dialog.findViewById(R.id.tvDescription);
                        tvDesc.setText(Utils.errorMsg(CreateDiskusiActivity.this, response));
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                    loadingDialog.dismiss();
                    showDialog();
                    Button btn = dialog.findViewById(R.id.btn_ok);
                    btn.setOnClickListener(v -> dialog.dismiss());
                    if (throwable instanceof HttpException) {
                        ResponseBody body = ((HttpException) throwable).response().errorBody();
                        Gson gson = new Gson();
                        TypeAdapter<ApiResponse> adapter = gson.getAdapter
                                (ApiResponse
                                        .class);
                        try {
                            ApiResponse errorParser =
                                    adapter.fromJson(Objects.requireNonNull(body).string());
                            Log.i("ON ERROR", "Error:" + errorParser.getError());

                            TextView tvDesc = dialog.findViewById(R.id.tvDescription);
                            tvDesc.setText(errorParser.getError());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (Exception e) {
            loadingDialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, CreateDiskusiActivity.this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
                e.printStackTrace();
            }

            @Override
            public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {
                onPhotosReturned(imageFiles);
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA_IMAGE) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(CreateDiskusiActivity.this);
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }

    private void onPhotosReturned(List<File> returnedPhotos) {
        photos.addAll(returnedPhotos);
        if (photos.size()>0){
            Glide.with(getApplicationContext()).load(photos.get(0)).into(imagePost);
            imagePostFile = photos.get(0);
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Option");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Take Photo")) {
                EasyImage.openCameraForImage(CreateDiskusiActivity.this, 1111);
            } else if (options[item].equals("Choose From Gallery")) {
                EasyImage.openGallery(CreateDiskusiActivity.this, 2222);
            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    void addVoteList() {

        RelativeLayout relativeLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        relativeLayoutParams.setMargins(20, 0, 20, 25);
        relativeLayout.setLayoutParams(relativeLayoutParams);
        relativeLayout.setId(numberOfLines + 1);


        ed = new EditText(this);
        RelativeLayout.LayoutParams editTextParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ImageView imageView = new ImageView(this);
        ed.setLayoutParams(editTextParams);
        ed.setHint("Vote Item");
        ed.setId(numberOfLines + 1);

        RelativeLayout.LayoutParams imageViewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageViewParams.setMargins(0, 10, 0, 0);
        imageViewParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        imageView.setId(numberOfLines + 1);
        imageView.setLayoutParams(imageViewParams);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.icon_close_item));

        imageView.setOnClickListener(view -> {
            int id = view.getId();
            EditText removedEditText = null;

            view.setVisibility(View.GONE);

            for (EditText editText : allEds) {
                if (editText.getId() == id) {
                    removedEditText = editText;
                }
            }

            removedEditText.setVisibility(View.GONE);
            allEds.remove(removedEditText);
        });

        relativeLayout.addView(ed);
        relativeLayout.addView(imageView);

        allEds.add(ed);
        voteListContainer.addView(relativeLayout);
        numberOfLines++;
    }

    private void showDialog() {
        dialog = new Dialog(CreateDiskusiActivity.this);
        //set content
        dialog.setContentView(R.layout.dialog_post_failed);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

}
