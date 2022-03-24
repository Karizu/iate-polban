package id.bl.blcom.iate.presentation.diskusi;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rezkyatinnov.kyandroid.localdata.LocalData;
import com.rezkyatinnov.kyandroid.reztrofit.ErrorResponse;
import com.rezkyatinnov.kyandroid.reztrofit.RestCallback;
import com.rezkyatinnov.kyandroid.session.Session;
import com.rezkyatinnov.kyandroid.session.SessionNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.bl.blcom.iate.R;
import id.bl.blcom.iate.api.DiscussionHelper;
import id.bl.blcom.iate.api.GroupHelper;
import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.models.Group;
import id.bl.blcom.iate.models.Profile;
import id.bl.blcom.iate.models.User;
import id.bl.blcom.iate.models.response.DiscussionDataResponse;
import id.bl.blcom.iate.presentation.login.LoginActivity;
import id.bl.blcom.iate.services.AdapterListMenuDiscussion;
import id.bl.blcom.iate.services.EndlessRecyclerViewScrollListener;
import io.realm.Realm;
import okhttp3.Headers;

public class DiskusiFragment extends Fragment {

    private int limit = 5;
    private int offset = 0;
    private EndlessRecyclerViewScrollListener scrollListener;
    private Context activity;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private AdapterListMenuDiscussion mAdapter;
    private List<DiscussionDataResponse> mDiscussions;
    private String groupId;
    private PopupMenu popupMenu;
    private List<String> groupIds;
    private List<Group> data;
    private Dialog dialog;
    private Boolean isNull;

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    @BindView(R.id.group_name)
    TextView groupName;

    @BindView(R.id.tvNoData)
    TextView tvNoData;

    @BindView(R.id.ll_filter_member)
    LinearLayout llFilterMember;

    @BindView(R.id.fab)
    FloatingActionButton fabCreatePost;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discussion, null);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        try {
            String token = Session.get("Authorization").getValue();
            Log.d("TOKEN", token);
        } catch (SessionNotFoundException e) {
            e.printStackTrace();
        }

        swipeContainer.setRefreshing(true);

        Realm realm = LocalData.getRealm();
        Profile profile = realm.where(Profile.class).findFirst();
        User user = realm.where(User.class).findFirst();
        groupId = Objects.requireNonNull(profile).getGroupId();
        groupName.setText(profile.getGroup().getName());

        Context wrapper = new ContextThemeWrapper(getContext(), R.style.popupMenuStyle);

        popupMenu = new PopupMenu(wrapper, llFilterMember, Gravity.CENTER_HORIZONTAL);
        populateGroup();

        popupMenu.getMenuInflater().inflate(R.menu.menu_group, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            swipeContainer.setRefreshing(true);
//            populateDiscussionData(true, groupIds.get(menuItem.getItemId()));
            groupName.setText(menuItem.getTitle());
            groupId = groupIds.get(menuItem.getItemId());
            Log.d("groupId", groupId);

            fabCreatePost.setVisibility(View.GONE);

            try {
                if (groupId.equals(Session.get("group_id").getValue())){
                    fabCreatePost.setVisibility(View.VISIBLE);
                }
                proccessFragment(true, menuItem.getTitle()+"", groupIds.get(menuItem.getItemId())+"");
            } catch (SessionNotFoundException e) {
                e.printStackTrace();
                showDialog();
                Button btnOK = dialog.findViewById(R.id.btn_ok);
                btnOK.setOnClickListener(v -> {
                    Session.clear();
                    LocalData.clear();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.putExtra("username", Objects.requireNonNull(user).getUsername());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    requireActivity().startActivity(intent);
                    getActivity().finish();
                });
            }

            return true;
        });

        activity = getActivity();
        layoutManager = new LinearLayoutManager(activity,
                LinearLayoutManager.VERTICAL,
                false);

        recyclerView = requireView().findViewById(R.id.discussion_recycle_view);
        recyclerView.setLayoutManager(layoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        recyclerView.addOnScrollListener(scrollListener);

        swipeContainer.setOnRefreshListener(() -> {
            // Your code to refresh the list here.
            // Make sure you call swipeContainer.setRefreshing(false)
            // once the network request has completed successfully.

            populateDiscussionData(true, groupId);
        });

        swipeContainer.setColorSchemeResources(
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        groupName.setText("All");

        try {
            if (requireArguments().getString("groupId")!=null){
                groupName.setText(getArguments().getString("title"));
                groupId = getArguments().getString("groupId");
                populateDiscussionData(true, groupId);
            } else {
                populateDiscussionData(true, "null");
            }
        } catch (Exception e){
            e.printStackTrace();
            populateDiscussionData(false, "null");
        }

        fabCreatePost.setOnClickListener(view1 -> {
            Intent myIntent = new Intent(getActivity(), CreateDiskusiActivity.class);
            myIntent.putExtra("group_id", groupId);
            myIntent.putExtra("group_name", groupName.getText().toString());
            startActivity(myIntent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (requireArguments().getString("groupId")!=null){
                groupName.setText(getArguments().getString("title"));
                groupId = getArguments().getString("groupId");
                populateDiscussionData(true, groupId);
            } else {
                populateDiscussionData(true, "null");
            }
        } catch (Exception e){
            e.printStackTrace();
            populateDiscussionData(false, "null");
        }
    }

    private void populateGroup() {
        try {
            GroupHelper.getGroup(new RestCallback<ApiResponse<List<Group>>>() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onSuccess(Headers headers, ApiResponse<List<Group>> body) {
                    try {
                        data = body.getData();

//                        Group myGroup = findGroup(data, groupId);

                        groupIds = new ArrayList<>();
                        int index = 0;

                        groupIds.add("null");
                        popupMenu.getMenu().add(1, index, index, "All");

                        index = 1;

                        if (data != null) {
                            if (data.get(index).getParent()) {
                                for (int i = 0; i < data.size(); i++) {
                                    groupIds.add(data.get(i).getId());
                                    popupMenu.getMenu().add(1, index, index, data.get(i).getName());
                                    index++;
                                }
                            } else {
                                for (int i = 0; i < data.size(); i++) {
//                                    if (data.get(i).getParent() || data.get(i).getId().equals(groupId)){
                                    groupIds.add(data.get(i).getId());
                                    popupMenu.getMenu().add(1, index, index, data.get(i).getName());
                                    index++;
//                                    }
                                }
                            }
                        }

                        llFilterMember.setOnClickListener(view1 -> popupMenu.show());
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

    private static Group findGroup(Iterable<Group> group, String groupId) {
        for (Group grp : group) //assume categories isn't null.
        {
            if (groupId.equals(grp.getId())) //assumes name isn't null.
            {
                return grp;
            }
        }

        return null;
    }

    private void populateDiscussionData(boolean onRefresh, String group) {
        try {
            if (group.equals("null")) {
                groupId = "null";
                if (onRefresh) {
                    DiscussionHelper.getDiscussionListAll(limit, offset, new RestCallback<ApiResponse<List<DiscussionDataResponse>>>() {
                        @Override
                        public void onSuccess(Headers headers, ApiResponse<List<DiscussionDataResponse>> body) {
                            try {
                                if (body != null && body.isStatus()) {
                                    mDiscussions = new ArrayList<>();
                                    List<DiscussionDataResponse> data = body.getData();

                                    if (data.size() < 1) {
                                        tvNoData.setVisibility(View.VISIBLE);
                                    } else {
                                        tvNoData.setVisibility(View.GONE);
                                    }

                                    for (int i = 0; i < data.size(); i++) {
                                        DiscussionDataResponse discussionDataResponse = data.get(i);
                                        discussionDataResponse.setFragmentManager(getFragmentManager());

                                        mDiscussions.add(discussionDataResponse);
                                    }

                                    mAdapter = new AdapterListMenuDiscussion(mDiscussions, activity, groupName.getText().toString());
                                    recyclerView.setAdapter(mAdapter); // or notifyItemRangeRemoved
                                    scrollListener.resetState();

                                    swipeContainer.setRefreshing(false);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailed(ErrorResponse error) {
                            Toast.makeText(getContext(), "Failed to load data, please try again", Toast.LENGTH_SHORT).show();
                            swipeContainer.setRefreshing(false);
                        }

                        @Override
                        public void onCanceled() {
                            Toast.makeText(getContext(), "Request canceled", Toast.LENGTH_SHORT).show();
                            swipeContainer.setRefreshing(false);
                        }
                    });

                } else {
                    DiscussionHelper.getDiscussionListAll(limit, offset, new RestCallback<ApiResponse<List<DiscussionDataResponse>>>() {
                        @Override
                        public void onSuccess(Headers headers, ApiResponse<List<DiscussionDataResponse>> body) {
                            try {
                                if (body != null && body.isStatus()) {
                                    List<DiscussionDataResponse> data = body.getData();
                                    mDiscussions = new ArrayList<>();

                                    if (data.size() < 1) {
                                        tvNoData.setVisibility(View.VISIBLE);
                                    } else {
                                        tvNoData.setVisibility(View.GONE);
                                    }

                                    for (int i = 0; i < data.size(); i++) {
                                        DiscussionDataResponse discussionDataResponse = data.get(i);
                                        discussionDataResponse.setFragmentManager(getFragmentManager());

                                        mDiscussions.add(discussionDataResponse);
                                    }

                                    mAdapter = new AdapterListMenuDiscussion(mDiscussions, activity, groupName.getText().toString());
                                    recyclerView.setAdapter(mAdapter);
                                    swipeContainer.setRefreshing(false);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailed(ErrorResponse error) {
                            Toast.makeText(getContext(), "Failed to load data, please try again", Toast.LENGTH_SHORT).show();
                            swipeContainer.setRefreshing(false);
                        }

                        @Override
                        public void onCanceled() {
                            Toast.makeText(getContext(), "Request canceled", Toast.LENGTH_SHORT).show();
                            swipeContainer.setRefreshing(false);
                        }
                    });
                }
            } else {
                if (onRefresh) {
                    DiscussionHelper.getDiscussionList(group, limit, offset, new RestCallback<ApiResponse<List<DiscussionDataResponse>>>() {
                        @Override
                        public void onSuccess(Headers headers, ApiResponse<List<DiscussionDataResponse>> body) {
                            try {
                                if (body != null && body.isStatus()) {
                                    mDiscussions = new ArrayList<>();
                                    List<DiscussionDataResponse> data = body.getData();
//                                    mDiscussions.clear();

                                    if (data.size() < 1) {
                                        tvNoData.setVisibility(View.VISIBLE);
                                    } else {
                                        tvNoData.setVisibility(View.GONE);
                                    }

                                    for (int i = 0; i < data.size(); i++) {
                                        DiscussionDataResponse discussionDataResponse = data.get(i);
                                        discussionDataResponse.setFragmentManager(getFragmentManager());

                                        mDiscussions.add(discussionDataResponse);
                                    }

//                                    mAdapter.notifyDataSetChanged(); // or notifyItemRangeRemoved
                                    scrollListener.resetState();
                                    mAdapter = new AdapterListMenuDiscussion(mDiscussions, activity, groupName.getText().toString());
                                    recyclerView.setAdapter(mAdapter);

                                    swipeContainer.setRefreshing(false);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailed(ErrorResponse error) {
                            Toast.makeText(getContext(), "Failed to load data, please try again", Toast.LENGTH_SHORT).show();
                            swipeContainer.setRefreshing(false);
                        }

                        @Override
                        public void onCanceled() {
                            Toast.makeText(getContext(), "Request canceled", Toast.LENGTH_SHORT).show();
                            swipeContainer.setRefreshing(false);
                        }
                    });

                } else {
                    DiscussionHelper.getDiscussionList(group, limit, offset, new RestCallback<ApiResponse<List<DiscussionDataResponse>>>() {
                        @Override
                        public void onSuccess(Headers headers, ApiResponse<List<DiscussionDataResponse>> body) {
                            try {
                                if (body != null && body.isStatus()) {
                                    List<DiscussionDataResponse> data = body.getData();
                                    mDiscussions = new ArrayList<>();

                                    if (data.size() < 1) {
                                        tvNoData.setVisibility(View.VISIBLE);
                                    } else {
                                        tvNoData.setVisibility(View.GONE);
                                    }

                                    for (int i = 0; i < data.size(); i++) {
                                        DiscussionDataResponse discussionDataResponse = data.get(i);
                                        discussionDataResponse.setFragmentManager(getFragmentManager());

                                        mDiscussions.add(discussionDataResponse);
                                    }

                                    mAdapter = new AdapterListMenuDiscussion(mDiscussions, activity, groupName.getText().toString());
                                    recyclerView.setAdapter(mAdapter);
                                    swipeContainer.setRefreshing(false);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailed(ErrorResponse error) {
                            Toast.makeText(getContext(), "Failed to load data, please try again", Toast.LENGTH_SHORT).show();
                            swipeContainer.setRefreshing(false);
                        }

                        @Override
                        public void onCanceled() {
                            Toast.makeText(getContext(), "Request canceled", Toast.LENGTH_SHORT).show();
                            swipeContainer.setRefreshing(false);
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`

        if (groupId.equals("null")) {
            try {
                DiscussionHelper.getDiscussionListAll(limit, limit * offset, new RestCallback<ApiResponse<List<DiscussionDataResponse>>>() {
                    @Override
                    public void onSuccess(Headers headers, ApiResponse<List<DiscussionDataResponse>> body) {
                        try {
                            if (body != null && body.isStatus()) {
                                List<DiscussionDataResponse> data = body.getData();
                                List<DiscussionDataResponse> newData = new ArrayList<>();
                                for (int i = 0; i < data.size(); i++) {
                                    DiscussionDataResponse discussionDataResponse = data.get(i);
                                    discussionDataResponse.setFragmentManager(getFragmentManager());

                                    newData.add(discussionDataResponse);
                                }

                                mDiscussions.addAll(newData);
                                mAdapter.notifyDataSetChanged();
                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onFailed(ErrorResponse error) {
                        Log.d("populate Directory", "Failed");
                    }

                    @Override
                    public void onCanceled() {
                        Log.d("populate Directory", "Cancel");
                    }
                });
            } catch (Exception e) {

            }
        } else {
            try {
                DiscussionHelper.getDiscussionList(groupId, limit, limit * offset, new RestCallback<ApiResponse<List<DiscussionDataResponse>>>() {
                    @Override
                    public void onSuccess(Headers headers, ApiResponse<List<DiscussionDataResponse>> body) {
                        try {
                            if (body != null && body.isStatus()) {
                                List<DiscussionDataResponse> data = body.getData();
                                List<DiscussionDataResponse> newData = new ArrayList<>();
                                for (int i = 0; i < data.size(); i++) {
                                    DiscussionDataResponse discussionDataResponse = data.get(i);
                                    discussionDataResponse.setFragmentManager(getFragmentManager());

                                    newData.add(discussionDataResponse);
                                }

                                mDiscussions.addAll(newData);
                                mAdapter.notifyDataSetChanged();
                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onFailed(ErrorResponse error) {
                        Log.d("populate Directory", "Failed");
                    }

                    @Override
                    public void onCanceled() {
                        Log.d("populate Directory", "Cancel");
                    }
                });
            } catch (Exception e) {

            }
        }
    }

    private void showDialog() {
        dialog = new Dialog(requireActivity());
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

    private void proccessFragment(Boolean isRefresh, String title, String groupId){
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("groupId", groupId);
        bundle.putBoolean("isRefresh", true);

        Fragment fragmentNews = new DiskusiFragment();
        fragmentNews.setArguments(bundle);
        FragmentManager fragmentManagerNews = getFragmentManager();
        FragmentTransaction fragmentTransactionNews = fragmentManagerNews.beginTransaction();

        fragmentTransactionNews.replace(R.id.screen_area, fragmentNews, "Diskusi Fragment");
        fragmentTransactionNews.addToBackStack("Diskusi Fragment");

        fragmentTransactionNews.commit();
    }
}
