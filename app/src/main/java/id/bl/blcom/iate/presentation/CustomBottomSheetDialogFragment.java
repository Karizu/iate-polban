package id.bl.blcom.iate.presentation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;
import java.util.Objects;

import id.bl.blcom.iate.R;
import id.bl.blcom.iate.api.MemberHelper;
import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.models.User;
import id.bl.blcom.iate.services.AdapterPostLoves;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("ValidFragment")
public class CustomBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private ProgressBar spinner;
    private List<User> userList;
    private AdapterPostLoves mAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;

    @SuppressLint("ValidFragment")
    public CustomBottomSheetDialogFragment(List<User> users) {
        this.userList = users;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet, container, false);
        spinner = v.findViewById(R.id.progressBar);
        recyclerView = v.findViewById(R.id.recyclerView);
        Activity activity = getActivity();
        layoutManager = new LinearLayoutManager(activity,
                LinearLayoutManager.VERTICAL,
                false);
        mAdapter = new AdapterPostLoves(userList, getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        ImageView btnSwipeDialog = v.findViewById(R.id.btnSwipeDialog);
        btnSwipeDialog.setOnClickListener(v2 -> dismiss());
//        getUserPostLoves();
        return v;
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public void getUserPostLoves(){
        spinner.setVisibility(View.VISIBLE);
        MemberHelper.getUser(20, new Callback<ApiResponse<List<User>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<User>>> call, Response<ApiResponse<List<User>>> response) {
                spinner.setVisibility(View.GONE);
                try {
                    if (response.isSuccessful()){
                        List<User> res = Objects.requireNonNull(response.body()).getData();
                        userList.addAll(res);
                        mAdapter = new AdapterPostLoves(userList, getContext());
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(mAdapter);
                    } else {
                        Utils.getErrorMsg(getContext(), Objects.requireNonNull(response.errorBody()).toString());
                    }
                } catch (Exception e){
                    Toast.makeText(getContext(), "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<User>>> call, Throwable t) {
                spinner.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Silahkan Periksa Koneksi Anda", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }
}