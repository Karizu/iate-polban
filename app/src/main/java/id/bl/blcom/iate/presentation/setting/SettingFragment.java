package id.bl.blcom.iate.presentation.setting;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Switch;

import com.google.gson.Gson;
import com.rezkyatinnov.kyandroid.localdata.LocalData;
import com.rezkyatinnov.kyandroid.reztrofit.ErrorResponse;
import com.rezkyatinnov.kyandroid.reztrofit.RestCallback;
import com.rezkyatinnov.kyandroid.session.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.bl.blcom.iate.R;
import id.bl.blcom.iate.api.PrivacyHelper;
import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.models.Profile;
import id.bl.blcom.iate.models.Privacy;
import id.bl.blcom.iate.presentation.LoadingDialog;
import id.bl.blcom.iate.presentation.login.LoginActivity;
import io.realm.Realm;
import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class SettingFragment extends Fragment {

    @BindView(R.id.switch_phoneNumber)
    Switch aSwitchPhoneNumber;

    @BindView(R.id.switch_address)
    Switch aSwitchAddress;

    @BindView(R.id.switch_age)
    Switch aSwitchAge;

    @BindView(R.id.switch_email)
    Switch aSwitchEmail;

    @BindView(R.id.switch_hobby)
    Switch aSwitchHobby;

    @BindView(R.id.switch_interest)
    Switch aSwitchInterest;

    @BindView(R.id.switch_shirtSize)
    Switch aSwitchShirtSize;

    @BindView(R.id.switch_status)
    Switch aSwitchStatus;

    @BindView(R.id.switch_forum)
    Switch aSwitchForum;

    @BindView(R.id.switch_news)
    Switch aSwitchNews;

    @BindView(R.id.switch_article)
    Switch aSwitchArticle;

    Privacy privacy;
    Realm realmDb;
    Profile currentUser;
    LoadingDialog loadingDialog;
    Dialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        realmDb = LocalData.getRealm();
        loadingDialog = new LoadingDialog(getContext());
        loadingDialog.setCancelable(false);

        currentUser = realmDb.where(Profile.class).findFirst();
        String privacyConfig = Objects.requireNonNull(currentUser).getPrivacyConfig();

        privacy = new Privacy();

        if (privacyConfig != null){
            try {
                JSONObject privacyObj = new JSONObject(privacyConfig);

                privacy.setHobbyShow(privacyObj.getBoolean("hobbyShow"));
                if (privacyObj.getBoolean("hobbyShow")) {
                    aSwitchHobby.setChecked(true);
                } else {
                    aSwitchHobby.setChecked(false);
                }

                privacy.setInterestShow(privacyObj.getBoolean("interestShow"));
                if (privacyObj.getBoolean("interestShow")) {
                    aSwitchInterest.setChecked(true);
                } else {
                    aSwitchInterest.setChecked(false);
                }

                privacy.setAgeShow(privacyObj.getBoolean("ageShow"));
                if (privacyObj.getBoolean("ageShow")) {
                    aSwitchAge.setChecked(true);
                } else {
                    aSwitchAge.setChecked(false);
                }

                privacy.setEmailShow(privacyObj.getBoolean("emailShow"));
                if (privacyObj.getBoolean("emailShow")) {
                    aSwitchEmail.setChecked(true);
                } else {
                    aSwitchEmail.setChecked(false);
                }

                privacy.setPhoneShow(privacyObj.getBoolean("phoneShow"));
                if (privacyObj.getBoolean("phoneShow")) {
                    aSwitchPhoneNumber.setChecked(true);
                } else {
                    aSwitchPhoneNumber.setChecked(false);
                }

                privacy.setAddressShow(privacyObj.getBoolean("addressShow"));
                if (privacyObj.getBoolean("addressShow")) {
                    aSwitchAddress.setChecked(true);
                } else {
                    aSwitchAddress.setChecked(false);
                }

                privacy.setStatusShow(privacyObj.getBoolean("statusShow"));
                if (privacyObj.getBoolean("statusShow")) {
                    aSwitchStatus.setChecked(true);
                } else {
                    aSwitchStatus.setChecked(false);
                }

                privacy.setShirtSizeShow(privacyObj.getBoolean("shirtSizeShow"));
                if (privacyObj.getBoolean("shirtSizeShow")) {
                    aSwitchShirtSize.setChecked(true);
                } else {
                    aSwitchShirtSize.setChecked(false);
                }

                privacy.setForumNotifEnable(privacyObj.getBoolean("forumNotifEnable"));
                if (privacyObj.getBoolean("forumNotifEnable")) {
                    aSwitchForum.setChecked(true);
                } else {
                    aSwitchForum.setChecked(false);
                }

                privacy.setNewsNotifEnable(privacyObj.getBoolean("newsNotifEnable"));
                if (privacyObj.getBoolean("newsNotifEnable")) {
                    aSwitchNews.setChecked(true);
                } else {
                    aSwitchNews.setChecked(false);
                }

                privacy.setArticleNotifEnable(privacyObj.getBoolean("articleNotifEnable"));
                if (privacyObj.getBoolean("articleNotifEnable")) {
                    aSwitchArticle.setChecked(true);
                } else {
                    aSwitchArticle.setChecked(false);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            privacy.setArticleNotifEnable(true);
            privacy.setNewsNotifEnable(true);
            privacy.setForumNotifEnable(true);

            privacy.setPhoneShow(true);
            privacy.setAddressShow(true);
            privacy.setAgeShow(true);
            privacy.setEmailShow(true);
            privacy.setInterestShow(true);
            privacy.setShirtSizeShow(true);
            privacy.setStatusShow(true);
            privacy.setHobbyShow(true);
        }
    }

    @OnClick(R.id.ll_logout)
    void onLogoutClick(){
        Session.clear();
        LocalData.clear();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @OnClick(R.id.switch_hobby)
    void onSwitchHobbyClick(){
        loadingDialog.show();
        privacy.setHobbyShow(aSwitchHobby.isChecked());
        Gson privacyObj = new Gson();
        String strJson = privacyObj.toJson(privacy);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("privacy_config", strJson)
                .build();

        PrivacyHelper.updateProfile(requestBody, new RestCallback<ApiResponse<Profile>>() {
            @Override
            public void onSuccess(Headers headers, ApiResponse body) {
                loadingDialog.dismiss();
                realmDb.beginTransaction();
                currentUser.setPrivacyConfig(strJson);
                realmDb.commitTransaction();
            }

            @Override
            public void onFailed(ErrorResponse error) {
                loadingDialog.dismiss();
            }

            @Override
            public void onCanceled() {
                loadingDialog.dismiss();
            }
        });

    }

    @OnClick(R.id.switch_status)
    void onSwitchStatusClick(){
        loadingDialog.show();
        privacy.setStatusShow(aSwitchStatus.isChecked());
        Gson privacyObj = new Gson();
        String strJson = privacyObj.toJson(privacy);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("privacy_config", strJson)
                .build();

        PrivacyHelper.updateProfile(requestBody, new RestCallback<ApiResponse<Profile>>() {
            @Override
            public void onSuccess(Headers headers, ApiResponse body) {
                loadingDialog.dismiss();
                realmDb.beginTransaction();
                currentUser.setPrivacyConfig(strJson);
                realmDb.commitTransaction();
            }

            @Override
            public void onFailed(ErrorResponse error) {
                loadingDialog.dismiss();
            }

            @Override
            public void onCanceled() {
                loadingDialog.dismiss();
            }
        });

    }

    @OnClick(R.id.switch_shirtSize)
    void onSwitchShirtSizeClick(){
        loadingDialog.show();
        privacy.setShirtSizeShow(aSwitchShirtSize.isChecked());
        Gson privacyObj = new Gson();
        String strJson = privacyObj.toJson(privacy);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("privacy_config", strJson)
                .build();

        PrivacyHelper.updateProfile(requestBody, new RestCallback<ApiResponse<Profile>>() {
            @Override
            public void onSuccess(Headers headers, ApiResponse body) {
                loadingDialog.dismiss();
                realmDb.beginTransaction();
                currentUser.setPrivacyConfig(strJson);
                realmDb.commitTransaction();
            }

            @Override
            public void onFailed(ErrorResponse error) {
                loadingDialog.dismiss();
            }

            @Override
            public void onCanceled() {
                loadingDialog.dismiss();
            }
        });

    }

    @OnClick(R.id.switch_interest)
    void onSwitchInterestClick(){
        loadingDialog.show();
        privacy.setInterestShow(aSwitchInterest.isChecked());
        Gson privacyObj = new Gson();
        String strJson = privacyObj.toJson(privacy);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("privacy_config", strJson)
                .build();

        PrivacyHelper.updateProfile(requestBody, new RestCallback<ApiResponse<Profile>>() {
            @Override
            public void onSuccess(Headers headers, ApiResponse body) {
                loadingDialog.dismiss();
                realmDb.beginTransaction();
                currentUser.setPrivacyConfig(strJson);
                realmDb.commitTransaction();
            }

            @Override
            public void onFailed(ErrorResponse error) {
                loadingDialog.dismiss();
            }

            @Override
            public void onCanceled() {
                loadingDialog.dismiss();
            }
        });

    }

    @OnClick(R.id.switch_email)
    void onSwitchEmailClick(){
        loadingDialog.show();
        privacy.setEmailShow(aSwitchEmail.isChecked());
        Gson privacyObj = new Gson();
        String strJson = privacyObj.toJson(privacy);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("privacy_config", strJson)
                .build();

        PrivacyHelper.updateProfile(requestBody, new RestCallback<ApiResponse<Profile>>() {
            @Override
            public void onSuccess(Headers headers, ApiResponse body) {
                loadingDialog.dismiss();
                realmDb.beginTransaction();
                currentUser.setPrivacyConfig(strJson);
                realmDb.commitTransaction();
            }

            @Override
            public void onFailed(ErrorResponse error) {
                loadingDialog.dismiss();
            }

            @Override
            public void onCanceled() {
                loadingDialog.dismiss();
            }
        });

    }

    @OnClick(R.id.switch_age)
    void onSwitchAgeClick(){
        loadingDialog.show();
        privacy.setAgeShow(aSwitchAge.isChecked());
        Gson privacyObj = new Gson();
        String strJson = privacyObj.toJson(privacy);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("privacy_config", strJson)
                .build();

        PrivacyHelper.updateProfile(requestBody, new RestCallback<ApiResponse<Profile>>() {
            @Override
            public void onSuccess(Headers headers, ApiResponse body) {
                loadingDialog.dismiss();
                realmDb.beginTransaction();
                currentUser.setPrivacyConfig(strJson);
                realmDb.commitTransaction();
            }

            @Override
            public void onFailed(ErrorResponse error) {
                loadingDialog.dismiss();
            }

            @Override
            public void onCanceled() {
                loadingDialog.dismiss();
            }
        });

    }

    @OnClick(R.id.switch_address)
    void onSwitchAddressClick(){
        loadingDialog.show();
        privacy.setAddressShow(aSwitchAddress.isChecked());
        Gson privacyObj = new Gson();
        String strJson = privacyObj.toJson(privacy);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("privacy_config", strJson)
                .build();

        PrivacyHelper.updateProfile(requestBody, new RestCallback<ApiResponse<Profile>>() {
            @Override
            public void onSuccess(Headers headers, ApiResponse body) {
                loadingDialog.dismiss();
                realmDb.beginTransaction();
                currentUser.setPrivacyConfig(strJson);
                realmDb.commitTransaction();
            }

            @Override
            public void onFailed(ErrorResponse error) {
                loadingDialog.dismiss();
            }

            @Override
            public void onCanceled() {
                loadingDialog.dismiss();
            }
        });

    }

    @OnClick(R.id.switch_phoneNumber)
    void onSwitchPhoneNumberClick(){
        loadingDialog.show();
        privacy.setPhoneShow(aSwitchPhoneNumber.isChecked());
        Gson privacyObj = new Gson();
        String strJson = privacyObj.toJson(privacy);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("privacy_config", strJson)
                .build();

        PrivacyHelper.updateProfile(requestBody, new RestCallback<ApiResponse<Profile>>() {
            @Override
            public void onSuccess(Headers headers, ApiResponse body) {
                loadingDialog.dismiss();
                realmDb.beginTransaction();
                currentUser.setPrivacyConfig(strJson);
                realmDb.commitTransaction();
            }

            @Override
            public void onFailed(ErrorResponse error) {
                loadingDialog.dismiss();
            }

            @Override
            public void onCanceled() {
                loadingDialog.dismiss();
            }
        });

    }

    @OnClick(R.id.switch_forum)
    void onSwitchForumClick(){
        loadingDialog.show();
        privacy.setForumNotifEnable(aSwitchForum.isChecked());
        Gson privacyObj = new Gson();
        String strJson = privacyObj.toJson(privacy);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("privacy_config", strJson)
                .build();

        PrivacyHelper.updateProfile(requestBody, new RestCallback<ApiResponse<Profile>>() {
            @Override
            public void onSuccess(Headers headers, ApiResponse body) {
                loadingDialog.dismiss();
                realmDb.beginTransaction();
                currentUser.setPrivacyConfig(strJson);
                realmDb.commitTransaction();
            }

            @Override
            public void onFailed(ErrorResponse error) {
                loadingDialog.dismiss();
            }

            @Override
            public void onCanceled() {
                loadingDialog.dismiss();
            }
        });

    }

    @OnClick(R.id.switch_news)
    void onSwitchNewsClick(){
        loadingDialog.show();
        privacy.setNewsNotifEnable(aSwitchNews.isChecked());
        Gson privacyObj = new Gson();
        String strJson = privacyObj.toJson(privacy);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("privacy_config", strJson)
                .build();

        PrivacyHelper.updateProfile(requestBody, new RestCallback<ApiResponse<Profile>>() {
            @Override
            public void onSuccess(Headers headers, ApiResponse body) {
                loadingDialog.dismiss();
                realmDb.beginTransaction();
                currentUser.setPrivacyConfig(strJson);
                realmDb.commitTransaction();
            }

            @Override
            public void onFailed(ErrorResponse error) {
                loadingDialog.dismiss();
            }

            @Override
            public void onCanceled() {
                loadingDialog.dismiss();
            }
        });

    }

    @OnClick(R.id.switch_article)
    void onSwitchArticleClick(){
        loadingDialog.show();
        privacy.setArticleNotifEnable(aSwitchArticle.isChecked());
        Gson privacyObj = new Gson();
        String strJson = privacyObj.toJson(privacy);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("privacy_config", strJson)
                .build();

        PrivacyHelper.updateProfile(requestBody, new RestCallback<ApiResponse<Profile>>() {
            @Override
            public void onSuccess(Headers headers, ApiResponse body) {
                loadingDialog.dismiss();
                realmDb.beginTransaction();
                currentUser.setPrivacyConfig(strJson);
                realmDb.commitTransaction();
            }

            @Override
            public void onFailed(ErrorResponse error) {
                loadingDialog.dismiss();
            }

            @Override
            public void onCanceled() {
                loadingDialog.dismiss();
            }
        });

    }

    @OnClick(R.id.tnc)
    public void showTnc() {
        showPopup(R.layout.dialog_term_and_condition);
    }

    @OnClick(R.id.privacy_con)
    public void showPrivacy() {
        showPopup(R.layout.dialog_privacy_policy);
    }

    private void showPopup(int layout) {
        dialog = new Dialog(Objects.requireNonNull(getActivity()));
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
