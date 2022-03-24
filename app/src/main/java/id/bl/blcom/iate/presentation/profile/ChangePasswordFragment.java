package id.bl.blcom.iate.presentation.profile;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.rezkyatinnov.kyandroid.reztrofit.ErrorResponse;
import com.rezkyatinnov.kyandroid.reztrofit.RestCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import id.bl.blcom.iate.R;
import id.bl.blcom.iate.api.PrivacyHelper;
import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.models.Password;
import id.bl.blcom.iate.presentation.NavigationDrawerActivity;
import okhttp3.Headers;

public class ChangePasswordFragment extends Fragment {
    @BindView(R.id.cp_tv_forgot_password)
    TextView forgotPassword;
    @BindView(R.id.cp_current_password)
    EditText currentPassword;
    @BindView(R.id.cp_new_password)
    EditText newPassword;
    @BindView(R.id.cp_image_profile)
    CircleImageView imageProfile;
    @BindView(R.id.cp_full_name)
    TextView fullName;
    @BindView(R.id.cp_dso)
    TextView dso;

    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_password, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        ((NavigationDrawerActivity)getActivity()).showBackMenu(true);

        userId = getArguments().getString("userId");
        String imgUrl = getArguments().getString("imgUrl");
        if (imgUrl != null)
            Glide.with(this).load(imgUrl).into(imageProfile);
        fullName.setText(getArguments().getString("fullName"));
        dso.setText(getArguments().getString("dso"));
        String text = "If you forgot your password, you can restart it with <font color='red'>Email</font>";
        forgotPassword.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
    }

    @OnClick(R.id.cp_change_password)
    void onClickChangePassword(){
        Password password = new Password();
        password.setPassword(currentPassword.getText().toString());
        password.setNewPassword(newPassword.getText().toString());

        PrivacyHelper.chagePassword(userId, password, new RestCallback<ApiResponse>() {
            @Override
            public void onSuccess(Headers headers, ApiResponse body) {
                Toast.makeText(getContext(), "Change password success", Toast.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager().popBackStack();
            }

            @Override
            public void onFailed(ErrorResponse error) {
                Log.d("Change password", "Change password failed");
            }

            @Override
            public void onCanceled() {
                Log.d("Change password", "Change password canceled");
            }
        });
    }
}
