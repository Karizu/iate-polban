package id.bl.blcom.iate.presentation.direktori;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.rezkyatinnov.kyandroid.localdata.LocalData;
import com.rezkyatinnov.kyandroid.reztrofit.ErrorResponse;
import com.rezkyatinnov.kyandroid.reztrofit.RestCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.bl.blcom.iate.NearMeActivity;
import id.bl.blcom.iate.R;
import id.bl.blcom.iate.api.GroupHelper;
import id.bl.blcom.iate.api.MemberHelper;
import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.models.Group;
import id.bl.blcom.iate.models.MemberProfile;
import id.bl.blcom.iate.models.Profile;
import id.bl.blcom.iate.models.response.MemberDataResponse;
import id.bl.blcom.iate.services.AdapterListMenuDirectory;
import id.bl.blcom.iate.services.EndlessRecyclerViewScrollListener;
import io.realm.Realm;
import okhttp3.Headers;

public class DirektoriFragment extends Fragment implements SearchView.OnQueryTextListener {

    private int limit = 5;
    private int offset = 0;
    private EndlessRecyclerViewScrollListener scrollListener;
    private Context activity;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeContainer;
    private LinearLayoutManager layoutManager;
    private AdapterListMenuDirectory mAdapter;
    private List<MemberProfile> mProfiles;
    private String groupId;
    private PopupMenu popupMenu;
    private List<String> groupIds;

    @BindView(R.id.group_name)
    TextView groupName;

    @BindView(R.id.ll_filter_member)
    LinearLayout llFilterMember;

    @OnClick(R.id.btnNearMe)
    void onClickNearMe(){
        Intent intent = new Intent(requireActivity(), NearMeActivity.class);
        startActivity(intent);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_directory,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        swipeContainer = view.findViewById(R.id.swipeContainer);

        Realm realm = LocalData.getRealm();
        Profile profile = realm.where(Profile.class).findFirst();
        groupId = profile.getGroupId();
        groupName.setText("Group - "+profile.getGroup().getName());

        Context wrapper = new ContextThemeWrapper(getContext(), R.style.popupMenuStyle);

        popupMenu = new PopupMenu(wrapper, llFilterMember, Gravity.CENTER_HORIZONTAL);
        populateGroup();
        popupMenu.getMenuInflater().inflate(R.menu.menu_group, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            swipeContainer.setRefreshing(true);

            populateDataMemberDirectory(true, groupIds.get(menuItem.getItemId()));
            groupName.setText("Group - "+menuItem.getTitle());
            groupId = groupIds.get(menuItem.getItemId());

            return true;
        });

        activity = getActivity();
        layoutManager = new LinearLayoutManager(activity,
                LinearLayoutManager.VERTICAL,
                false);

        recyclerView = getView().findViewById(R.id.member_directory_recycler_view);
        recyclerView.setLayoutManager(layoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
//                loadNextDataFromApi(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        recyclerView.addOnScrollListener(scrollListener);

        swipeContainer.setOnRefreshListener(() -> {
            // Your code to refresh the list here.
            // Make sure you call swipeContainer.setRefreshing(false)
            // once the network request has completed successfully.
            populateDataMemberDirectory(true, groupId);
        });

        swipeContainer.setColorSchemeResources(
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        populateDataMemberDirectory(false, groupId);
        SearchView searchView = getView().findViewById(R.id.search_view);
        searchView.setQueryHint("Nama Anggota");

        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        EditText searchEditText = searchView.findViewById(R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        searchEditText.setHintTextColor(getResources().getColor(R.color.colorPrimaryDark));
        searchEditText.setTextSize(14);

        searchView.setOnQueryTextListener(this);
        searchView.setOnClickListener(v -> searchView.setIconified(false));
    }

    private void populateGroup(){
        try {
            GroupHelper.getGroup(new RestCallback<ApiResponse<List<Group>>>() {
                @Override
                public void onSuccess(Headers headers, ApiResponse<List<Group>> body) {
                    try {
                        List<Group> data = body.getData();

                        groupIds = new ArrayList<>();
                        int index = 0;
                        for (int i = 0; i < data.size(); i++){
                            if (!data.get(i).getParent()){
                                groupIds.add(data.get(i).getId());
                                popupMenu.getMenu().add(1, index, index, data.get(i).getName());
                                index++;
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
        } catch (Exception e){

        }
    }

    private void populateDataMemberDirectory(boolean onRefresh, String group){
        try {
            if (onRefresh){
                MemberHelper.getMemberNoLimit(group, new RestCallback<ApiResponse<List<MemberDataResponse>>>() {
                    @Override
                    public void onSuccess(Headers headers, ApiResponse<List<MemberDataResponse>> body) {
                        try {
                            if (body != null && body.isStatus()) {
                                List<MemberDataResponse> data = body.getData();
                                mProfiles.clear();

                                for (int i = 0; i < data.size(); i++){
                                    MemberProfile profile = data.get(i).getProfile();
                                    profile.setFragmentManager(getFragmentManager());

                                    mProfiles.add(profile);
                                }

                                mAdapter.notifyDataSetChanged(); // or notifyItemRangeRemoved
                                scrollListener.resetState();

                                swipeContainer.setRefreshing(false);
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
            } else {
                MemberHelper.getMemberNoLimit(group, new RestCallback<ApiResponse<List<MemberDataResponse>>>() {
                    @Override
                    public void onSuccess(Headers headers, ApiResponse<List<MemberDataResponse>> body) {
                        try {
                            if (body != null && body.isStatus()) {
                                List<MemberDataResponse> data = body.getData();
                                mProfiles = new ArrayList<>();
                                for (int i = 0; i < data.size(); i++){
                                    MemberProfile profile = data.get(i).getProfile();
                                    profile.setFragmentManager(getFragmentManager());

                                    mProfiles.add(profile);
                                }

                                mAdapter = new AdapterListMenuDirectory(mProfiles, activity);
                                recyclerView.setAdapter(mAdapter);
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
            }
        } catch (Exception e) {

        }
    }

    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
            try {
                MemberHelper.getMember(limit, limit*offset, groupId, new RestCallback<ApiResponse<List<MemberDataResponse>>>() {
                    @Override
                    public void onSuccess(Headers headers, ApiResponse<List<MemberDataResponse>> body) {
                        try {
                            if (body != null && body.isStatus()) {
                                int j =+ 1;
                                Log.d("TAG SCROLL", j+"");
                                List<MemberDataResponse> data = body.getData();
                                List<MemberProfile> newProfile = new ArrayList<>();
                                for (int i = 0; i < data.size(); i++){
                                    MemberProfile profile = data.get(i).getProfile();
                                    profile.setFragmentManager(getFragmentManager());

                                    newProfile.add(profile);
                                }

                                mProfiles.addAll(newProfile);
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


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        List<MemberProfile> newProfiles = new ArrayList<>();
        String newTextLowerCase = newText.toLowerCase();
        for (MemberProfile profile: mProfiles){
            if (profile.getFullname().toLowerCase().contains(newTextLowerCase) ||
                    profile.getGroup().getName().toLowerCase().contains(newTextLowerCase)){
                newProfiles.add(profile);
            }
        }
        mAdapter.updateData(newProfiles);
        return true;
    }
}
