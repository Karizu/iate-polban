package id.bl.blcom.iate.presentation.diskusi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.Looper;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.rezkyatinnov.kyandroid.localdata.LocalData;
import com.rezkyatinnov.kyandroid.reztrofit.ErrorResponse;
import com.rezkyatinnov.kyandroid.reztrofit.RestCallback;
import com.rezkyatinnov.kyandroid.session.Session;
import com.rezkyatinnov.kyandroid.session.SessionNotFoundException;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.EmojiTextView;

import org.ocpsoft.prettytime.PrettyTime;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.bl.blcom.iate.R;
import id.bl.blcom.iate.api.ApiInterface;
import id.bl.blcom.iate.api.DiscussionHelper;
import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.models.Loves;
import id.bl.blcom.iate.models.Polling;
import id.bl.blcom.iate.models.PostPolling;
import id.bl.blcom.iate.models.Profile;
import id.bl.blcom.iate.models.StickerModel;
import id.bl.blcom.iate.models.User;
import id.bl.blcom.iate.models.response.DiscussionDataResponse;
import id.bl.blcom.iate.models.response.ThreadDataResponse;
import id.bl.blcom.iate.presentation.ImagePreviewActivity;
import id.bl.blcom.iate.presentation.LoadingDialog;
import id.bl.blcom.iate.presentation.login.LoginActivity;
import id.bl.blcom.iate.services.AdapterListSticker;
import id.bl.blcom.iate.services.AdapterListThread;
import id.bl.blcom.iate.services.AdapterPostLoves;
import io.realm.Realm;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class DetailDiskusiActivity extends AppCompatActivity {

    private Context activity;
    private Activity act;
    private RecyclerView recyclerView;
    private RecyclerView recyclerViewSticker;
    private AdapterListSticker mAdapterSticker;
    private List<StickerModel> stickerModels;
    private NestedScrollView scrollView;
    private LinearLayoutManager layoutManager;
    private GridLayoutManager gridLayoutManager;
    private AdapterListThread mAdapter;
    private List<ThreadDataResponse> mThreads;
    private List<Loves> lovesList;
    private DiscussionDataResponse mDiscussion;
    private String postId;
    private Profile profile;
    private User user;
    private SwipeRefreshLayout swipeContainer;
    private ArrayList<File> photos = new ArrayList<>();

    private ImageView contentImageView;
    private FrameLayout pollingLayout;
    private LinearLayout pollingContainer;
    private TextView postTitle;
    private TextView postCaption;
    private TextView postTime;
    private TextView username;

    private EditText commentEditText;
    private Button submitButton;
    private final int CAPTION = 1, IMAGE = 2, POLLING = 3;

    private final int REQEUST_CAMERA = 1, REQUEST_GALLERY = 2;
    private File imagePostFile;
    private Dialog dialog;
    private BottomSheetDialog bottomDialog;
    private BottomSheetBehavior sheetBehavior;

    private String groupId;
    @BindView(R.id.group_name)
    TextView groupName;
    @BindView(R.id.composer_wrapper)
    RelativeLayout composer_wrapper;
    @BindView(R.id.sticker)
    ImageView sticker;
    @BindView(R.id.laySticker)
    RelativeLayout laySticker;
    @BindView(R.id.imgShow)
    ImageView imgShow;
    @BindView(R.id.imgClose)
    ImageView imgClose;
    @BindView(R.id.layoutPreview)
    LinearLayout layoutPreview;
    @BindView(R.id.tvLoves)
    TextView tvLoves;
    @BindView(R.id.tvNoLike)
    TextView tvNoLike;
    @BindView(R.id.bottom_sheet)
    LinearLayout layoutBottomSheet;
    @BindView(R.id.linear_layout)
    LinearLayout linear_layout;
    @BindView(R.id.emoji)
    ImageView emoji;

    private BottomSheetDialog bsDialog;
    private EasyImage easyImage;
    private EmojiPopup emojiPopup;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.sticker)
    void onClickSticker() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        emojiPopup.dismiss();
        boolean show = false;
        if (laySticker.getVisibility() == View.GONE) {
            show = true;
        }

        Transition transition = new Slide(Gravity.BOTTOM);
        transition.setDuration(600);
        transition.addTarget(R.id.image);
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        TransitionManager.beginDelayedTransition(viewGroup, transition);
        laySticker.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void populatePreviewSticker(Boolean isClick, int img, Drawable drawable, Context context) {
        imgClose.setOnClickListener(v -> {
            layoutPreview.setVisibility(View.GONE);
        });

        if (isClick) {
            layoutPreview.setVisibility(View.VISIBLE);
            imgShow.setImageDrawable(context.getResources().getDrawable(img));
            imgShow.setOnClickListener(v -> {
                LoadingDialog loadingDialog = new LoadingDialog(context);
                loadingDialog.setCancelable(false);
                loadingDialog.show();

                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                        img);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("post_id", postId)
                        .addFormDataPart("user_id", profile.getUserId())
                        .addFormDataPart("comment", "Send Image")
                        .addFormDataPart("type", String.valueOf(2))
                        .addFormDataPart("content", "photo.jpeg", RequestBody.create(MediaType.parse("image/jpeg"), byteArrayOutputStream.toByteArray()))
                        .build();

                try {
                    DiscussionHelper.createThreadWithImage(requestBody, new RestCallback<ApiResponse>() {
                        @Override
                        public void onSuccess(Headers headers, ApiResponse body) {
                            loadingDialog.dismiss();
                            populateThreadData(true, postId);
                            layoutPreview.setVisibility(View.GONE);
                            if (laySticker.getVisibility() == View.VISIBLE) {
                                laySticker.setVisibility(View.GONE);
                            }

                            try {
                                InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
                            } catch (Exception e) {
                                // TODO: handle exception
                            }
                        }

                        @Override
                        public void onFailed(ErrorResponse error) {
                            loadingDialog.dismiss();
                            Toast.makeText(context, "Create Post Failed", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCanceled() {
                            loadingDialog.dismiss();
                            Toast.makeText(context, "Crete Post Canceled", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    loadingDialog.dismiss();
                }
            });

            submitButton.setOnClickListener(view -> {
                if (layoutPreview.getVisibility() == View.VISIBLE) {
                    LoadingDialog loadingDialog = new LoadingDialog(context);
                    loadingDialog.setCancelable(false);
                    loadingDialog.show();

                    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                            img);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);

                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("post_id", postId)
                            .addFormDataPart("user_id", profile.getUserId())
                            .addFormDataPart("comment", "Send Image")
                            .addFormDataPart("type", String.valueOf(2))
                            .addFormDataPart("content", "photo.jpeg", RequestBody.create(MediaType.parse("image/jpeg"), byteArrayOutputStream.toByteArray()))
                            .build();

                    try {
                        DiscussionHelper.createThreadWithImage(requestBody, new RestCallback<ApiResponse>() {
                            @Override
                            public void onSuccess(Headers headers, ApiResponse body) {
                                loadingDialog.dismiss();

                                submitButton.setOnClickListener(view1 -> submitCommentText());

                                populateThreadData(true, postId);
                                layoutPreview.setVisibility(View.GONE);
                                if (laySticker.getVisibility() == View.VISIBLE) {
                                    laySticker.setVisibility(View.GONE);
                                }

                                try {
                                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
                                } catch (Exception e) {
                                    // TODO: handle exception
                                }
                            }

                            @Override
                            public void onFailed(ErrorResponse error) {
                                loadingDialog.dismiss();
                                Toast.makeText(context, "Create Post Failed", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCanceled() {
                                loadingDialog.dismiss();
                                Toast.makeText(context, "Crete Post Canceled", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        loadingDialog.dismiss();
                    }
                }
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> {
                    //Do something after 100ms
                    submitCommentText();
                }, 1000);

            });
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_diskusi);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        activity = this;
        act = this;
//        ((NavigationDrawerActivity)activity).showBackMenu(true);

        Intent data = getIntent();

        postId = data.getStringExtra("postId");
        String groupIds = data.getStringExtra("group_id");
        String groupNames = data.getStringExtra("group_name");

        Realm realm = LocalData.getRealm();
        this.profile = realm.where(Profile.class).findFirst();
        this.user = realm.where(User.class).findFirst();
        Profile profile = realm.where(Profile.class).findFirst();
        User user = realm.where(User.class).findFirst();
        groupId = this.profile.getGroupId();
//        groupName.setText(this.profile.getGroup().getName());
        groupName.setText(groupNames);
        stickerModels = new ArrayList<>();
        lovesList = new ArrayList<>();

        composer_wrapper.setVisibility(View.VISIBLE);

        Log.d("USERNAME", Objects.requireNonNull(user).getUsername());

        try {
            if (!Objects.equals(groupIds, Session.get("group_id").getValue())) {
//                composer_wrapper.setVisibility(View.GONE);
            }
        } catch (SessionNotFoundException e) {
            e.printStackTrace();
            showDialog();
            Button btnOK = dialog.findViewById(R.id.btn_ok);
            btnOK.setOnClickListener(v -> {
                Session.clear();
                LocalData.clear();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra("username", Objects.requireNonNull(user).getUsername());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            });
        }

        Context wrapper = new ContextThemeWrapper(this, R.style.popupMenuStyle);
        swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setRefreshing(true);

        contentImageView = findViewById(R.id.image_content);
        pollingLayout = findViewById(R.id.polling_wrapper);
        pollingContainer = findViewById(R.id.polling_container);

        postTitle = findViewById(R.id.post_title);
        postCaption = findViewById(R.id.post_caption);
        username = findViewById(R.id.tvUsername);
        postTime = findViewById(R.id.post_time);

        commentEditText = findViewById(R.id.comment_textarea);
        submitButton = findViewById(R.id.submit_comment_btn);

        submitButton.setOnClickListener(view1 -> {
//            EmojiTextView emojiTextView = (EmojiTextView) LayoutInflater
//                    .from(view1.getContext())
//                    .inflate(R.layout.emoji_text_view, linear_layout, false);
//
//            emojiTextView.setText(commentEditText.getText().toString());
//            linear_layout.addView(emojiTextView);
            submitCommentText();
        });

        layoutManager = new LinearLayoutManager(activity,
                LinearLayoutManager.VERTICAL,
                false);

        scrollView = findViewById(R.id.scrollView);
        recyclerView = findViewById(R.id.thread_recycle_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(this.scrollListener);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerViewSticker = findViewById(R.id.rv_sticker);
        recyclerViewSticker.setLayoutManager(new GridLayoutManager(activity, 4));


        swipeContainer.setOnRefreshListener(() -> {
            populateDiscussionData(postId);
            populateThreadData(true, postId);
        });

        swipeContainer.setColorSchemeResources(
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


//        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
//        sheetBehavior.setHideable(true);//Important to add
//        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
//        bottomSheetExpand();

        populateBottomSheetDialog();
        populateStickerImage();
        populateDiscussionData(postId);
        populateThreadData(false, postId);

        emojiPopup = EmojiPopup.Builder.fromRootView(findViewById(R.id.root_view)).build(commentEditText);
        emoji.setOnClickListener(view -> {
            laySticker.setVisibility(View.GONE);
            layoutPreview.setVisibility(View.GONE);
            emojiPopup.toggle();
        });

    }

    private void submitCommentText(){


        String commentText = commentEditText.getText().toString();
        if (!commentText.trim().equals("")) {
            submitButton.setEnabled(false);
            swipeContainer.setRefreshing(true);
            try {
                DiscussionHelper.createThread(postId, profile.getUserId(), commentText, new RestCallback<ApiResponse>() {
                    @Override
                    public void onSuccess(Headers headers, ApiResponse body) {
                        try {
                            if (body != null && body.isStatus()) {
                                submitButton.setEnabled(true);
                                commentEditText.setText("");
                                populateThreadData(true, postId);

                                try {
                                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
                                } catch (Exception e) {
                                    // TODO: handle exception
                                }
                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onFailed(ErrorResponse error) {
                        submitButton.setEnabled(true);
                    }

                    @Override
                    public void onCanceled() {
                        submitButton.setEnabled(true);

                    }
                });
            } catch (Exception e) {

            }
        }
    }

    private void populateBottomSheetDialog(){
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        sheetBehavior.setPeekHeight(viewGroup.getMeasuredHeight());

        /**
         * bottom sheet state change listener
         * we are changing button text when sheet changed state
         * */
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
//                        Toast.makeText(activity, "STATE_HIDDEN", Toast.LENGTH_SHORT).show();
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
//                        btnBottomSheet.setText("Close Sheet");
//                        Toast.makeText(activity, "STATE_EXPANDED", Toast.LENGTH_SHORT).show();
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
//                        btnBottomSheet.setText("Expand Sheet");
//                        Toast.makeText(activity, "STATE_COLLAPSED", Toast.LENGTH_SHORT).show();
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
//                        Toast.makeText(activity, "STATE_DRAGGING", Toast.LENGTH_SHORT).show();
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
//                        Toast.makeText(activity, "STATE_SETTLING", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    @OnClick(R.id.btnViewPostLoves)
    void showLoves() {
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < lovesList.size(); i++) {
            userList.add(lovesList.get(i).getUser());
        }

        boolean isListEmpty = false;
        if (lovesList.size() == 0){
            isListEmpty = true;
        }

        showDialogBottom(userList, isListEmpty);
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

    @OnClick(R.id.attach)
    void onChooseImageClick() {
        selectImage();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, DetailDiskusiActivity.this, new DefaultCallback() {
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
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(DetailDiskusiActivity.this);
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }

    private void onPhotosReturned(List<File> returnedPhotos) {
        photos.addAll(returnedPhotos);
        if (photos.size()>0){
            LoadingDialog loadingDialog = new LoadingDialog(activity);
            loadingDialog.setCancelable(false);
            loadingDialog.show();

            imagePostFile = photos.get(0);

            Bitmap bitmap = BitmapFactory.decodeFile(imagePostFile.getAbsolutePath());

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("post_id", postId)
                    .addFormDataPart("user_id", profile.getUserId())
                    .addFormDataPart("comment", "Send Image")
                    .addFormDataPart("type", String.valueOf(2))
                    .addFormDataPart("content", "photo.jpeg", RequestBody.create(MediaType.parse("image/jpeg"), byteArrayOutputStream.toByteArray()))
                    .build();

            try {
                DiscussionHelper.createThreadWithImage(requestBody, new RestCallback<ApiResponse>() {
                    @Override
                    public void onSuccess(Headers headers, ApiResponse body) {
                        loadingDialog.dismiss();
                        swipeContainer.setRefreshing(true);
                        populateThreadData(true,postId);

                        try {
                            InputMethodManager imm = (InputMethodManager)activity.getSystemService(INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }

                    @Override
                    public void onFailed(ErrorResponse error) {
                        loadingDialog.dismiss();
                        Toast.makeText(activity, "Create Post Failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCanceled() {
                        loadingDialog.dismiss();
                        Toast.makeText(activity, "Crete Post Canceled", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                loadingDialog.dismiss();
            }
        }
    }

    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose From Gallery","Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Select Option");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Take Photo")) {
                EasyImage.openCameraForImage(DetailDiskusiActivity.this, 1111);
            } else if (options[item].equals("Choose From Gallery")) {
                EasyImage.openGallery(DetailDiskusiActivity.this, 2222);
            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void populateStickerImage() {
        StickerModel stickerModel;
        TypedArray ar = Objects.requireNonNull(activity).getResources().obtainTypedArray(R.array.integer_array_name);
        int len = ar.length();
        int[] resIds = new int[len];
        for (int i = 0; i < len; i++)
            resIds[i] = ar.getResourceId(i, 0);
        ar.recycle();
        for (int i = 0; i < len; i++) {
            stickerModel = new StickerModel();
            stickerModel.setImgSticker(resIds[i]);
            stickerModels.add(stickerModel);
        }

        mAdapterSticker = new AdapterListSticker(stickerModels, postId, profile, DetailDiskusiActivity.this, act, activity);
        recyclerViewSticker.setAdapter(mAdapterSticker);
        mAdapterSticker.notifyDataSetChanged();
    }

    private void populateDiscussionData(String postId) {
        try {
            DiscussionHelper.getDiscussionDetail(postId, new RestCallback<ApiResponse<DiscussionDataResponse>>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onSuccess(Headers headers, ApiResponse<DiscussionDataResponse> body) {
                    try {
                        if (body != null && body.isStatus()) {
                            mDiscussion = body.getData();
                            lovesList = body.getData().getLoves();
                            switch (mDiscussion.getType()) {
                                case CAPTION:
                                    contentImageView.setVisibility(View.GONE);
                                    pollingLayout.setVisibility(View.GONE);
                                    break;
                                case IMAGE:
                                    contentImageView.setVisibility(View.VISIBLE);
                                    pollingLayout.setVisibility(View.GONE);
                                    Glide.with(activity).applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ph_image)).load(ApiInterface.BASE_URL_IMAGE + mDiscussion.getContent()).into(contentImageView);
                                    contentImageView.setOnClickListener(view -> {
                                        Intent myIntent = new Intent(act, ImagePreviewActivity.class);
                                        myIntent.putExtra("imgUrl", ApiInterface.BASE_URL_IMAGE + mDiscussion.getContent());
                                        myIntent.putExtra("type", 1);
                                        startActivity(myIntent);
                                    });
                                    break;
                                case POLLING:
                                    contentImageView.setVisibility(View.GONE);
                                    pollingLayout.setVisibility(View.VISIBLE);

                                    ViewGroup insertPoint = pollingContainer;
                                    insertPoint.removeAllViews();

                                    if (mDiscussion.getParticipation_count() != 1){
                                        for (int i = 0; i < mDiscussion.getPolling().size(); i++) {
                                            LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                            View v = vi.inflate(R.layout.vote_event, null);

                                            // fill in any details dynamically here
                                            TextView textView = v.findViewById(R.id.title_progress_bar_event);
                                            textView.setVisibility(View.GONE);
                                            ProgressBar progressBar = v.findViewById(R.id.progress_bar_event);
                                            progressBar.setVisibility(View.GONE);
                                            TextView counter = v.findViewById(R.id.counter_vote);
                                            counter.setVisibility(View.GONE);

                                            Button voteButton = v.findViewById(R.id.vote_button);
                                            voteButton.setVisibility(View.VISIBLE);
                                            voteButton.setText(mDiscussion.getPolling().get(i).getOption());
                                            voteButton.setOnClickListener(view -> {
                                                Button b = v.findViewById(R.id.vote_button);
                                                String buttonText = b.getText().toString();

                                                for (Polling d : mDiscussion.getPolling()) {
                                                    if (d.getOption().equals(buttonText)) {
                                                        Realm realm = LocalData.getRealm();
                                                        Profile profile = realm.where(Profile.class).findFirst();
                                                        PostPolling postPolling = new PostPolling();
                                                        postPolling.setPollingId(d.getId());
                                                        postPolling.setPostId(d.getPostId());
                                                        postPolling.setUserId(profile.getUserId());

                                                        try {
                                                            DiscussionHelper.postVoting(postPolling, new RestCallback<ApiResponse>() {
                                                                @Override
                                                                public void onSuccess(Headers headers, ApiResponse body) {
//                                                                    Fragment fragment = new DiskusiFragment();
//                                                                    FragmentManager fragmentManager = (activity).getSupportFragmentManager();
//                                                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//                                                                    fragmentTransaction.replace(R.id.screen_area, fragment, "discussion list");
//                                                                    fragmentTransaction.addToBackStack("discussion list");
//
//                                                                    fragmentTransaction.commit();
                                                                    Intent intent = new Intent(act, DiskusiFragment.class);
                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                    startActivity(intent);
                                                                    finish();
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
                                                }
                                            });

                                            // insert into main view
                                            insertPoint = pollingContainer;
                                            insertPoint.addView(v, i, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                                        }
                                    } else {
                                        for (int i = 0; i < mDiscussion.getPolling().size(); i++) {
                                            LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                            View v = vi.inflate(R.layout.vote_event, null);

                                            // fill in any details dynamically here
                                            TextView textView = v.findViewById(R.id.title_progress_bar_event);
                                            textView.setText(mDiscussion.getPolling().get(i).getOption());

                                            TextView counter = v.findViewById(R.id.counter_vote);
                                            counter.setText(mDiscussion.getPolling().get(i).getCount().toString());

                                            ProgressBar progressBar = v.findViewById(R.id.progress_bar_event);
                                            progressBar.setMax(10);
                                            progressBar.setProgress(mDiscussion.getPolling().get(i).getCount());

                                            // insert into main view
                                            insertPoint = pollingContainer;
                                            insertPoint.addView(v, i, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                                        }
                                    }
                                    break;
                                default:
                                    break;
                            }

                            if (mDiscussion.getTitle() != null && !mDiscussion.getTitle().trim().equals("")) {
                                postTitle.setText(mDiscussion.getTitle());
                            } else {
                                postTitle.setVisibility(View.GONE);
                            }
                            username.setText(mDiscussion.getUser().getProfile().getFullname());
                            postCaption.setText(mDiscussion.getCaption());
                            tvLoves.setText(mDiscussion.getLoves_count() + " Suka");

                            String result = "";
                            try {
                                PrettyTime timeAgo = new PrettyTime();
                                result = timeAgo.format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(mDiscussion.getCreated_at()));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            postTime.setText(result);
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

    public void populateThreadData(boolean onRefresh, String postId) {
        try {
            if (onRefresh) {
                DiscussionHelper.getThreadList(postId, new RestCallback<ApiResponse<List<ThreadDataResponse>>>() {
                    @Override
                    public void onSuccess(Headers headers, ApiResponse<List<ThreadDataResponse>> body) {
                        try {
                            if (body != null && body.isStatus()) {
                                List<ThreadDataResponse> data = body.getData();
                                mThreads.clear();

                                //                                    threadDataResponse.setFragmentManager(getFragmentManager());
                                mThreads.addAll(data);

                                mAdapter.notifyDataSetChanged(); // or notifyItemRangeRemoved

                                swipeContainer.setRefreshing(false);
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

            } else {
                DiscussionHelper.getThreadList(postId, new RestCallback<ApiResponse<List<ThreadDataResponse>>>() {
                    @Override
                    public void onSuccess(Headers headers, ApiResponse<List<ThreadDataResponse>> body) {
                        try {
                            if (body != null && body.isStatus()) {
                                List<ThreadDataResponse> data = body.getData();
                                mThreads = new ArrayList<>();
                                //                                    threadDataResponse.setFragmentManager(getFragmentManager());
                                mThreads.addAll(data);

                                mAdapter = new AdapterListThread(mThreads, profile, activity);
                                recyclerView.setAdapter(mAdapter);
                                mAdapter.notifyDataSetChanged(); // or notifyItemRangeRemoved
                                swipeContainer.setRefreshing(false);
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
            }
        } catch (Exception e) {

        }
    }

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            LinearLayoutManager manager = ((LinearLayoutManager) recyclerView.getLayoutManager());
            boolean enabled = manager.findFirstCompletelyVisibleItemPosition() == 0;
            swipeContainer.setEnabled(enabled);
        }
    };

    private void showDialog() {
        dialog = new Dialog(Objects.requireNonNull(activity));
        //set content
        dialog.setContentView(R.layout.dialog_login_again);
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

    private void showDialogBottom(List<User> userList, boolean isListEmpty) {

        if (isListEmpty){
            tvNoLike.setVisibility(View.VISIBLE);
        }

        AdapterPostLoves mAdapter;
        RecyclerView recyclerView;
        LinearLayoutManager layoutManager;
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(activity,
                LinearLayoutManager.VERTICAL,
                false);
        mAdapter = new AdapterPostLoves(userList, activity);
        Objects.requireNonNull(recyclerView).setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        ImageView btnSwipeDialog = findViewById(R.id.btnSwipeDialog);
        Objects.requireNonNull(btnSwipeDialog).setOnClickListener(v2 -> sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN));
//        new CustomBottomSheetDialogFragment(userList).show(Objects.requireNonNull(getFragmentManager()), "Dialog");

        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }
}
