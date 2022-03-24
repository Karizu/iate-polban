package id.bl.blcom.iate.presentation.diskusi;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
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
import com.rezkyatinnov.kyandroid.localdata.LocalData;
import com.rezkyatinnov.kyandroid.reztrofit.ErrorResponse;
import com.rezkyatinnov.kyandroid.reztrofit.RestCallback;
import com.rezkyatinnov.kyandroid.session.Session;
import com.rezkyatinnov.kyandroid.session.SessionNotFoundException;

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
import id.bl.blcom.iate.presentation.CustomBottomSheetDialogFragment;
import id.bl.blcom.iate.presentation.ImagePreviewActivity;
import id.bl.blcom.iate.presentation.LoadingDialog;
import id.bl.blcom.iate.presentation.NavigationDrawerActivity;
import id.bl.blcom.iate.presentation.login.LoginActivity;
import id.bl.blcom.iate.services.AdapterListSticker;
import id.bl.blcom.iate.services.AdapterListThread;
import io.realm.Realm;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class DetailDiskusiFragment extends Fragment {

    private Context activity;
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
    private EasyImage easyImage;

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
//    @BindView(R.id.bottom_sheet)
//    LinearLayout layoutBottomSheet;
//    @BindView(R.id.bottomSheet)
//    CoordinatorLayout bottomSheet;

    @OnClick(R.id.sticker)
    void onClickSticker() {
        if (laySticker.getVisibility() == View.GONE) {
            laySticker.setVisibility(View.VISIBLE);
        } else {
            laySticker.setVisibility(View.GONE);
        }
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
                                imm.hideSoftInputFromWindow(Objects.requireNonNull(Objects.requireNonNull(getActivity()).getCurrentFocus()).getWindowToken(), 0);
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
        }

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_discussion_detail, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        ((NavigationDrawerActivity) getActivity()).showBackMenu(true);

        postId = Objects.requireNonNull(getArguments()).getString("postId");
        String groupIds = Objects.requireNonNull(getArguments()).getString("group_id");

        Realm realm = LocalData.getRealm();
        this.profile = realm.where(Profile.class).findFirst();
        this.user = realm.where(User.class).findFirst();
        Profile profile = realm.where(Profile.class).findFirst();
        User user = realm.where(User.class).findFirst();
        groupId = this.profile.getGroupId();
        groupName.setText(this.profile.getGroup().getName());
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
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtra("username", Objects.requireNonNull(user).getUsername());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getActivity().startActivity(intent);
                getActivity().finish();
            });
        }

        Context wrapper = new ContextThemeWrapper(getContext(), R.style.popupMenuStyle);
        swipeContainer = view.findViewById(R.id.swipeContainer);
        swipeContainer.setRefreshing(true);

        contentImageView = view.findViewById(R.id.image_content);
        pollingLayout = view.findViewById(R.id.polling_wrapper);
        pollingContainer = view.findViewById(R.id.polling_container);

        postTitle = view.findViewById(R.id.post_title);
        postCaption = view.findViewById(R.id.post_caption);
        username = view.findViewById(R.id.tvUsername);
        postTime = view.findViewById(R.id.post_time);

        commentEditText = view.findViewById(R.id.comment_textarea);
        submitButton = view.findViewById(R.id.submit_comment_btn);

        submitButton.setOnClickListener(view1 -> {
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
                                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
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
        });

        activity = getActivity();
        layoutManager = new LinearLayoutManager(activity,
                LinearLayoutManager.VERTICAL,
                false);

        scrollView = getView().findViewById(R.id.scrollView);
        recyclerView = getView().findViewById(R.id.thread_recycle_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(this.scrollListener);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerViewSticker = getView().findViewById(R.id.rv_sticker);
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

//        populateStickerImage();
        populateDiscussionData(postId);
        populateThreadData(false, postId);
    }

    @OnClick(R.id.btnViewPostLoves)
    void showLoves() {
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < lovesList.size(); i++) {
            userList.add(lovesList.get(i).getUser());
        }
        new CustomBottomSheetDialogFragment(userList).show(Objects.requireNonNull(getFragmentManager()), "Dialog");
    }

    @OnClick(R.id.attach)
    void onChooseImageClick() {
        selectImage();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, requireActivity(), new DefaultCallback() {
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
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(requireActivity());
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
                        populateThreadData(true, postId);

                        try {
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }

                    @Override
                    public void onFailed(ErrorResponse error) {
                        loadingDialog.dismiss();
                        Toast.makeText(getContext(), "Create Post Failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCanceled() {
                        loadingDialog.dismiss();
                        Toast.makeText(getContext(), "Crete Post Canceled", Toast.LENGTH_SHORT).show();
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
                EasyImage.openCameraForImage(requireActivity(), 1111);
            } else if (options[item].equals("Choose From Gallery")) {
                EasyImage.openGallery(requireActivity(), 2222);
            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

//    private void populateStickerImage() {
//        StickerModel stickerModel;
//        TypedArray ar = Objects.requireNonNull(getContext()).getResources().obtainTypedArray(R.array.integer_array_name);
//        int len = ar.length();
//        int[] resIds = new int[len];
//        for (int i = 0; i < len; i++)
//            resIds[i] = ar.getResourceId(i, 0);
//            ar.recycle();
//        for (int i = 0; i < len; i++) {
//            stickerModel = new StickerModel();
//            stickerModel.setImgSticker(resIds[i]);
//            stickerModels.add(stickerModel);
//        }
//
//        mAdapterSticker = new AdapterListSticker(stickerModels, postId, profile, DetailDiskusiFragment.this, getActivity(), getContext());
//        recyclerViewSticker.setAdapter(mAdapterSticker);
//        mAdapterSticker.notifyDataSetChanged();
//    }

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
                                    Glide.with(getActivity()).applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ph_image)).load(mDiscussion.getContent()).into(contentImageView);
                                    contentImageView.setOnClickListener(view -> {
                                        Intent myIntent = new Intent(getActivity(), ImagePreviewActivity.class);
                                        myIntent.putExtra("imgUrl", mDiscussion.getContent());
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
                                            LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                                                                    Fragment fragment = new DiskusiFragment();
                                                                    FragmentManager fragmentManager = (getActivity()).getSupportFragmentManager();
                                                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                                                                    fragmentTransaction.replace(R.id.screen_area, fragment, "discussion list");
                                                                    fragmentTransaction.addToBackStack("discussion list");

                                                                    fragmentTransaction.commit();
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
                                            LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

                                for (int i = 0; i < data.size(); i++) {
                                    ThreadDataResponse threadDataResponse = data.get(i);
                                    threadDataResponse.setFragmentManager(getFragmentManager());

                                    mThreads.add(threadDataResponse);
                                }

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
                                for (int i = 0; i < data.size(); i++) {
                                    ThreadDataResponse threadDataResponse = data.get(i);
                                    threadDataResponse.setFragmentManager(getFragmentManager());

                                    mThreads.add(threadDataResponse);
                                }

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
        dialog = new Dialog(Objects.requireNonNull(getActivity()));
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
}
