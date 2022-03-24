package id.bl.blcom.iate.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.rezkyatinnov.kyandroid.localdata.LocalData;
import com.rezkyatinnov.kyandroid.reztrofit.ErrorResponse;
import com.rezkyatinnov.kyandroid.reztrofit.RestCallback;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import id.bl.blcom.iate.R;
import id.bl.blcom.iate.api.ApiInterface;
import id.bl.blcom.iate.api.DiscussionHelper;
import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.models.Polling;
import id.bl.blcom.iate.models.PostPolling;
import id.bl.blcom.iate.models.Profile;
import id.bl.blcom.iate.models.response.DiscussionDataResponse;
import id.bl.blcom.iate.presentation.NavigationDrawerActivity;
import id.bl.blcom.iate.presentation.ReportDialog;
import id.bl.blcom.iate.presentation.diskusi.DetailDiskusiActivity;
import id.bl.blcom.iate.presentation.diskusi.DiskusiFragment;
import io.realm.Realm;
import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class AdapterListMenuDiscussion extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<DiscussionDataResponse> discussions;
    private Context context;
    private final int CAPTION = 1, IMAGE = 2, POLLING = 3;
    public static int POTRAIT = 1;
    public static int LANDSCAPE = 2;
    private int rotate;
    private String groupName;

    public AdapterListMenuDiscussion(List<DiscussionDataResponse> discussions, Context context, String groupNm) {
        this.discussions = discussions;
        this.context = context;
        this.groupName = groupNm;
    }

    public AdapterListMenuDiscussion(List<DiscussionDataResponse> discussions, Context context, int rotate) {
        this.discussions = discussions;
        this.context = context;
        this.rotate = rotate;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case CAPTION:
                View v1 = inflater.inflate(R.layout.layout_discussion_caption, viewGroup, false);
                viewHolder = new ViewHolderCaption(v1);
                break;
            case IMAGE:
                View v2 = inflater.inflate(R.layout.layout_discussion_item, viewGroup, false);
                viewHolder = new ViewHolderImagePost(v2);
                break;
            case POLLING:
                View v3 = inflater.inflate(R.layout.layout_discussion_polling, viewGroup, false);
                viewHolder = new ViewHolderPolling(v3);
                break;
            default:
                View v = inflater.inflate(R.layout.layout_discussion_caption, viewGroup, false);
                viewHolder = new ViewHolderCaption(v);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        LinearLayout postLayout;
        ImageView reportPost;
        switch (viewHolder.getItemViewType()) {
            case CAPTION:
                ViewHolderCaption vh1 = (ViewHolderCaption) viewHolder;
                configureViewHolder1(vh1, position);
                postLayout = vh1.postLayout;
                reportPost = vh1.report;
                break;
            case IMAGE:
                ViewHolderImagePost vh2 = (ViewHolderImagePost) viewHolder;
                configureViewHolder2(vh2, position);
                postLayout = vh2.postLayout;
                reportPost = vh2.report;
                break;
            case POLLING:
                ViewHolderPolling vh3 = (ViewHolderPolling) viewHolder;
                configureViewHolder3(vh3, position);
                postLayout = vh3.postLayout;
                reportPost = vh3.report;
                break;
            default:
                ViewHolderCaption vhDef = (ViewHolderCaption) viewHolder;
                configureViewHolder1(vhDef, position);
                postLayout = vhDef.postLayout;
                reportPost = vhDef.report;
                break;
        }

        DiscussionDataResponse dataResponse = discussions.get(position);

        postLayout.setOnClickListener(view -> {
            Intent intent = new Intent(context, DetailDiskusiActivity.class);
            intent.putExtra("postId", dataResponse.getId());
            intent.putExtra("group_id", dataResponse.getGroupId());
            intent.putExtra("group_name", groupName);
            context.startActivity(intent);

//            Bundle bundle = new Bundle();
//            Fragment fragment = new DetailDiskusiFragment();
//            bundle.putString("postId", dataResponse.getId());
//            bundle.putString("group_id", dataResponse.getGroupId());
//            FragmentManager fragmentManager = dataResponse.getFragmentManager();
//            fragment.setArguments(bundle);
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.screen_area, fragment, dataResponse.getTitle());
//            fragmentTransaction.addToBackStack("Diskusi");
//            fragmentTransaction.commit();
        });

        reportPost.setOnClickListener(view -> {
            ReportDialog reportDialog = new ReportDialog(context, discussions.get(position).getGroupId(), discussions.get(position).getId(), discussions.get(position).getFragmentManager());
            Realm realm = LocalData.getRealm();
            Profile profile = realm.where(Profile.class).findFirst();
            if (profile.getUserId().equals(discussions.get(position).getUserId())) {
                reportDialog.setShowRemoveButton(true);
            }
            reportDialog.show();
        });
    }

    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    private void configureViewHolder1(ViewHolderCaption vh1, int position) {
        DiscussionDataResponse dataResponse = discussions.get(position);
        if (dataResponse != null) {
            String image = ApiInterface.BASE_URL_IMAGE + dataResponse.getUser().getProfile().getPicture();
            vh1.getUsername().setText(dataResponse.getUser().getProfile().getFullname());
            vh1.getCaption().setText(dataResponse.getCaption());
            Realm realm = LocalData.getRealm();
            Profile profile = realm.where(Profile.class).findFirst();
//            vh1.getLiked().setText(dataResponse.getLoved() + "");
            vh1.getLiked().setText(dataResponse.getLoves_count() + "");

            switch (dataResponse.getUser_loved_count()){
                case 0:
                    vh1.getPostLiked().setLiked(false);
                    break;
                case 1:
                    vh1.getPostLiked().setLiked(true);
                    break;
                default:
                    vh1.getPostLiked().setLiked(false);
                    break;
            }

            vh1.getPostLiked().setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    vh1.getLiked().setText(Integer.parseInt(vh1.getLiked().getText().toString()) + 1 + "");
                    RequestBody body = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("status", "1")
                            .build();
                    DiscussionHelper.doLikePost(body, dataResponse.getId(), new RestCallback<ApiResponse>() {
                        @Override
                        public void onSuccess(Headers headers, ApiResponse body) {
                            Log.d("LOVED", "SUKSES");
                        }

                        @Override
                        public void onFailed(ErrorResponse error) {
                            Log.d("LOVED", "GAGAL");
                            likeButton.setLiked(false);
                            try {
                                Snackbar.make(vh1.itemView, "", Snackbar.LENGTH_LONG)
                                        .setActionTextColor(context.getResources().getColor(android.R.color.holo_red_light))
                                        .show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onCanceled() {
                            Log.d("LOVED", "CANCEL");
                        }
                    });
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    likeButton.setLiked(false);
                    vh1.getLiked().setText(Integer.parseInt(vh1.getLiked().getText().toString()) != 0 ? Integer.parseInt(vh1.getLiked().getText().toString()) - 1 + "" : vh1.getLiked().getText().toString());

                    RequestBody body = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("status", "1")
                            .build();
                    DiscussionHelper.doLikePost(body, dataResponse.getId(), new RestCallback<ApiResponse>() {
                        @Override
                        public void onSuccess(Headers headers, ApiResponse body) {
                            Log.d("LOVED", "SUKSES");
                        }

                        @Override
                        public void onFailed(ErrorResponse error) {
                            Log.d("LOVED", "GAGAL");
                            likeButton.setLiked(false);
                            try {
                                Snackbar.make(vh1.itemView, "", Snackbar.LENGTH_LONG)
                                        .setActionTextColor(context.getResources().getColor(android.R.color.holo_red_light))
                                        .show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onCanceled() {
                            Log.d("LOVED", "CANCEL");
                        }
                    });
                }
            });

            String result = "";
            try {
                PrettyTime timeAgo = new PrettyTime();
                result = timeAgo.format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dataResponse.getCreated_at()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            vh1.getTimeAgo().setText(result);

            Glide.with(context)
                    .applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ic_baseline_person_24))
                    .load(image)
                    .into(vh1.getProfilePicture());

        }
    }

    @SuppressLint("SetTextI18n")
    private void configureViewHolder2(ViewHolderImagePost vh1, int position) {
        DiscussionDataResponse dataResponse = discussions.get(position);
        if (dataResponse != null) {
            vh1.getUsername().setText(dataResponse.getUser().getProfile().getFullname());
            vh1.getCaption().setText(dataResponse.getCaption());
            Realm realm = LocalData.getRealm();
            Profile profile = realm.where(Profile.class).findFirst();
//            vh1.getLiked().setText(dataResponse.getLoved() + "");
            vh1.getLiked().setText(dataResponse.getLoves_count() + "");

            switch (dataResponse.getUser_loved_count()){
                case 0:
                    vh1.getPostLiked().setLiked(false);
                    break;
                case 1:
                    vh1.getPostLiked().setLiked(true);
                    break;
                default:
                    vh1.getPostLiked().setLiked(false);
                    break;
            }

            vh1.getPostLiked().setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {

                    vh1.getLiked().setText(Integer.parseInt(vh1.getLiked().getText().toString()) + 1 + "");
                    RequestBody body = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("status", "1")
                            .build();
                    DiscussionHelper.doLikePost(body, dataResponse.getId(), new RestCallback<ApiResponse>() {
                        @Override
                        public void onSuccess(Headers headers, ApiResponse body) {
                            Log.d("LOVED", "SUKSES");
                        }

                        @Override
                        public void onFailed(ErrorResponse error) {
                            Log.d("LOVED", "GAGAL");
                            likeButton.setLiked(false);
                            try {
                                Snackbar.make(vh1.itemView, "", Snackbar.LENGTH_LONG)
                                        .setActionTextColor(context.getResources().getColor(android.R.color.holo_red_light))
                                        .show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onCanceled() {
                            Log.d("LOVED", "CANCEL");
                        }
                    });
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    likeButton.setLiked(false);
                    vh1.getLiked().setText(Integer.parseInt(vh1.getLiked().getText().toString()) != 0 ? Integer.parseInt(vh1.getLiked().getText().toString()) - 1 + "" : vh1.getLiked().getText().toString());
                    RequestBody body = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("status", "1")
                            .build();
                    DiscussionHelper.doLikePost(body, dataResponse.getId(), new RestCallback<ApiResponse>() {
                        @Override
                        public void onSuccess(Headers headers, ApiResponse body) {
                            Log.d("UNLOVED", "SUKSES");
                        }

                        @Override
                        public void onFailed(ErrorResponse error) {
                            Log.d("UNLOVED", "GAGAL");
                            likeButton.setLiked(false);
                            try {
                                Snackbar.make(vh1.itemView, "", Snackbar.LENGTH_LONG)
                                        .setActionTextColor(context.getResources().getColor(android.R.color.holo_red_light))
                                        .show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onCanceled() {
                            Log.d("UNLOVED", "CANCEL");
                        }
                    });
                }
            });

            String result = "";
            try {
                PrettyTime timeAgo = new PrettyTime();
                result = timeAgo.format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dataResponse.getCreated_at()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            vh1.getTimeAgo().setText(result);

            Glide.with(context).applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ic_baseline_person_24)).load(ApiInterface.BASE_URL_IMAGE + dataResponse.getUser().getProfile().getPicture()).into(vh1.getProfilePicture());

//            if (rotate == LANDSCAPE)
            RoundedCorners roundedCorners = new RoundedCorners(8);
            Glide.with(context)
                    .applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ph_image))
                    .load(ApiInterface.BASE_URL_IMAGE + dataResponse.getContent())
                    .transform(roundedCorners)
                    .into(vh1.getImagePost());
//            vh1.getImagePost().getScaleType();
//            vh1.getImagePost().setRotation(90f);
//            vh1.getImagePost().setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void configureViewHolder3(ViewHolderPolling vh1, int position) {
        DiscussionDataResponse dataResponse = discussions.get(position);
        if (dataResponse != null) {
            vh1.getUsername().setText(dataResponse.getUser().getProfile().getFullname());
            vh1.getCaption().setText(dataResponse.getCaption());
            Realm realm = LocalData.getRealm();
            Profile profile = realm.where(Profile.class).findFirst();
//            vh1.getLiked().setText(dataResponse.getLoved() + "");
            vh1.getLiked().setText(dataResponse.getLoves_count() + "");

            switch (dataResponse.getUser_loved_count()){
                case 0:
                    vh1.getPostLiked().setLiked(false);
                    break;
                case 1:
                    vh1.getPostLiked().setLiked(true);
                    break;
                default:
                    vh1.getPostLiked().setLiked(false);
                    break;
            }

            vh1.getPostLiked().setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    vh1.getLiked().setText(Integer.parseInt(vh1.getLiked().getText().toString()) + 1 +"");
                    RequestBody body = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("status", "1")
                            .build();
                    DiscussionHelper.doLikePost(body, dataResponse.getId(), new RestCallback<ApiResponse>() {
                        @Override
                        public void onSuccess(Headers headers, ApiResponse body) {
                            Log.d("LOVED", "SUKSES");
                        }

                        @Override
                        public void onFailed(ErrorResponse error) {
                            Log.d("LOVED", "GAGAL");
                            likeButton.setLiked(false);
                            try {
                                Snackbar.make(vh1.itemView, "", Snackbar.LENGTH_LONG)
                                        .setActionTextColor(context.getResources().getColor(android.R.color.holo_red_light))
                                        .show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onCanceled() {
                            Log.d("LOVED", "CANCEL");
                        }
                    });
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    likeButton.setLiked(false);
                    vh1.getLiked().setText(Integer.parseInt(vh1.getLiked().getText().toString()) != 0 ? Integer.parseInt(vh1.getLiked().getText().toString()) - 1 + "" : vh1.getLiked().getText().toString());
                    Log.d("UNLOVED", "UNLIKE");
                    RequestBody body = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("status", "1")
                            .build();
                    DiscussionHelper.doLikePost(body, dataResponse.getId(), new RestCallback<ApiResponse>() {
                        @Override
                        public void onSuccess(Headers headers, ApiResponse body) {
                            Log.d("UNLOVED", "SUKSES");
                        }

                        @Override
                        public void onFailed(ErrorResponse error) {
                            Log.d("UNLOVED", "GAGAL");
                            likeButton.setLiked(false);
                            try {
                                Snackbar.make(vh1.itemView, "", Snackbar.LENGTH_LONG)
                                        .setActionTextColor(context.getResources().getColor(android.R.color.holo_red_light))
                                        .show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onCanceled() {
                            Log.d("UNLOVED", "CANCEL");
                        }
                    });

                }
            });

            String result = "";
            try {
                PrettyTime timeAgo = new PrettyTime();
                result = timeAgo.format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dataResponse.getCreated_at()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            vh1.getTimeAgo().setText(result);

            Glide.with(context).applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ic_baseline_person_24)).load(ApiInterface.BASE_URL_IMAGE + dataResponse.getUser().getProfile().getPicture()).into(vh1.getProfilePicture());

            ViewGroup insertPoint = vh1.getPollingContainer();
            insertPoint.removeAllViews();

            if (dataResponse.getParticipation_count() != 1){
                for (int i = 0; i < dataResponse.getPolling().size(); i++) {
                    LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                    voteButton.setText(dataResponse.getPolling().get(i).getOption());
                    voteButton.setOnClickListener(view -> {
                        Button b = v.findViewById(R.id.vote_button);
                        String buttonText = b.getText().toString();

                        for (Polling d : dataResponse.getPolling()) {
                            if (d.getOption().equals(buttonText)) {
                                Realm realms = LocalData.getRealm();
                                Profile profiles = realms.where(Profile.class).findFirst();
                                PostPolling postPolling = new PostPolling();
                                postPolling.setPollingId(d.getId());
                                postPolling.setPostId(d.getPostId());
                                postPolling.setUserId(profiles.getUserId());

                                DiscussionHelper.postVoting(postPolling, new RestCallback<ApiResponse>() {
                                    @Override
                                    public void onSuccess(Headers headers, ApiResponse body) {
                                        Fragment fragment = new DiskusiFragment();
                                        FragmentManager fragmentManager = ((NavigationDrawerActivity) context).getSupportFragmentManager();
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
                            }
                        }
                    });

                    // insert into main view
                    insertPoint = vh1.getPollingContainer();
                    insertPoint.addView(v, i, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                }
            } else {
                for (int i = 0; i < dataResponse.getPolling().size(); i++) {
                    LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View v = vi.inflate(R.layout.vote_event, null);

                    // fill in any details dynamically here
                    TextView textView = v.findViewById(R.id.title_progress_bar_event);
                    textView.setText(dataResponse.getPolling().get(i).getOption());

                    TextView counter = v.findViewById(R.id.counter_vote);
                    counter.setText(dataResponse.getPolling().get(i).getCount().toString());

                    ProgressBar progressBar = v.findViewById(R.id.progress_bar_event);
                    progressBar.setMax(10);
                    progressBar.setProgress(dataResponse.getPolling().get(i).getCount());

                    // insert into main view
                    insertPoint = vh1.getPollingContainer();
                    insertPoint.addView(v, i, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (discussions.get(position).getType()) {
            case CAPTION:
                return CAPTION;

            case IMAGE:
                return IMAGE;

            case POLLING:
                return POLLING;
        }

        return -1;
    }

    @Override
    public int getItemCount() {
        return discussions.size();
    }

    public class ViewHolderImagePost extends RecyclerView.ViewHolder {
        public CircleImageView profilePicture;
        public ImageView imagePost;
        public TextView username;
        public TextView timeAgo;
        public TextView caption;
        public TextView liked;
        public TextView content;
        public ImageView report;
        public LikeButton postLiked;
        public LinearLayout postLayout;

        public ViewHolderImagePost(View v) {
            super(v);

            imagePost = v.findViewById(R.id.imgDisscussion);
            profilePicture = v.findViewById(R.id.imgDiscussionProfile);
            username = v.findViewById(R.id.tvUsername);
            timeAgo = v.findViewById(R.id.tvTimeAgo);
            caption = v.findViewById(R.id.tvDiscussionCaption);
            content = v.findViewById(R.id.discussion_content);
            report = v.findViewById(R.id.imgDiscussReport);
            liked = v.findViewById(R.id.tvLikePost);
            postLiked = v.findViewById(R.id.imgLikePost);

            postLayout = v.findViewById(R.id.discussion_container);
        }

        public LikeButton getPostLiked() {
            return postLiked;
        }

        public void setPostLiked(LikeButton postLiked) {
            this.postLiked = postLiked;
        }

        public CircleImageView getProfilePicture() {
            return profilePicture;
        }

        public void setProfilePicture(CircleImageView profilePicture) {
            this.profilePicture = profilePicture;
        }

        public ImageView getImagePost() {
            return imagePost;
        }

        public void setImagePost(ImageView imagePost) {
            this.imagePost = imagePost;
        }

        public TextView getUsername() {
            return username;
        }

        public void setUsername(TextView username) {
            this.username = username;
        }

        public TextView getTimeAgo() {
            return timeAgo;
        }

        public void setTimeAgo(TextView timeAgo) {
            this.timeAgo = timeAgo;
        }

        public TextView getCaption() {
            return caption;
        }

        public void setCaption(TextView caption) {
            this.caption = caption;
        }

        public TextView getLiked() {
            return liked;
        }

        public void setLiked(TextView liked) {
            this.liked = liked;
        }

        public TextView getContent() {
            return content;
        }

        public void setContent(TextView content) {
            this.content = content;
        }

        public ImageView getReport() {
            return report;
        }

        public void setReport(ImageView report) {
            this.report = report;
        }

        public LinearLayout getPostLayout() {
            return postLayout;
        }

        public void setPostLayout(LinearLayout postLayout) {
            this.postLayout = postLayout;
        }
    }

    public class ViewHolderCaption extends RecyclerView.ViewHolder {
        public CircleImageView profilePicture;
        public TextView username;
        public TextView timeAgo;
        public TextView caption;
        public TextView liked;
        public ImageView report;
        public LikeButton postLiked;
        public LinearLayout postLayout;

        public ViewHolderCaption(View v) {
            super(v);

            profilePicture = v.findViewById(R.id.imgDiscussionProfile);
            username = v.findViewById(R.id.tvUsername);
            timeAgo = v.findViewById(R.id.tvTimeAgo);
            caption = v.findViewById(R.id.tvDiscussionCaption);
            report = v.findViewById(R.id.imgDiscussReport);
            liked = v.findViewById(R.id.tvLikePost);
            postLiked = v.findViewById(R.id.imgLikePost);

            postLayout = v.findViewById(R.id.discussion_container);
        }

        public CircleImageView getProfilePicture() {
            return profilePicture;
        }

        public void setProfilePicture(CircleImageView profilePicture) {
            this.profilePicture = profilePicture;
        }

        public TextView getUsername() {
            return username;
        }

        public void setUsername(TextView username) {
            this.username = username;
        }

        public TextView getTimeAgo() {
            return timeAgo;
        }

        public void setTimeAgo(TextView timeAgo) {
            this.timeAgo = timeAgo;
        }

        public TextView getCaption() {
            return caption;
        }

        public void setCaption(TextView caption) {
            this.caption = caption;
        }

        public ImageView getReport() {
            return report;
        }

        public void setReport(ImageView report) {
            this.report = report;
        }

        public LikeButton getPostLiked() {
            return postLiked;
        }

        public void setPostLiked(LikeButton postLiked) {
            this.postLiked = postLiked;
        }

        public TextView getLiked() {
            return liked;
        }

        public void setLiked(TextView liked) {
            this.liked = liked;
        }

        public LinearLayout getPostLayout() {
            return postLayout;
        }

        public void setPostLayout(LinearLayout postLayout) {
            this.postLayout = postLayout;
        }
    }

    public class ViewHolderPolling extends RecyclerView.ViewHolder {
        public CircleImageView profilePicture;
        public TextView username;
        public TextView timeAgo;
        public TextView caption;
        public ImageView report;
        public TextView liked;
        public LikeButton postLiked;
        public LinearLayout pollingContainer;
        public LinearLayout postLayout;

        public CircleImageView getProfilePicture() {
            return profilePicture;
        }

        public void setProfilePicture(CircleImageView profilePicture) {
            this.profilePicture = profilePicture;
        }

        public TextView getUsername() {
            return username;
        }

        public void setUsername(TextView username) {
            this.username = username;
        }

        public TextView getTimeAgo() {
            return timeAgo;
        }

        public void setTimeAgo(TextView timeAgo) {
            this.timeAgo = timeAgo;
        }

        public TextView getCaption() {
            return caption;
        }

        public void setCaption(TextView caption) {
            this.caption = caption;
        }

        public ImageView getReport() {
            return report;
        }

        public void setReport(ImageView report) {
            this.report = report;
        }

        public LikeButton getPostLiked() {
            return postLiked;
        }

        public void setPostLiked(LikeButton postLiked) {
            this.postLiked = postLiked;
        }

        public TextView getLiked() {
            return liked;
        }

        public void setLiked(TextView liked) {
            this.liked = liked;
        }

        public LinearLayout getPollingContainer() {
            return pollingContainer;
        }

        public void setPollingContainer(LinearLayout pollingContainer) {
            this.pollingContainer = pollingContainer;
        }

        public LinearLayout getPostLayout() {
            return postLayout;
        }

        public void setPostLayout(LinearLayout postLayout) {
            this.postLayout = postLayout;
        }

        public ViewHolderPolling(View v) {
            super(v);

            profilePicture = v.findViewById(R.id.imgDiscussionProfile);
            username = v.findViewById(R.id.tvUsername);
            timeAgo = v.findViewById(R.id.tvTimeAgo);
            caption = v.findViewById(R.id.tvDiscussionCaption);
            report = v.findViewById(R.id.imgDiscussReport);
            postLiked = v.findViewById(R.id.imgLikePost);
            liked = v.findViewById(R.id.tvLikePost);
            pollingContainer = v.findViewById(R.id.polling_container);

            postLayout = v.findViewById(R.id.discussion_container);


        }
    }

}