package id.bl.blcom.iate.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.rezkyatinnov.kyandroid.localdata.LocalData;
import com.rezkyatinnov.kyandroid.reztrofit.ErrorResponse;
import com.rezkyatinnov.kyandroid.reztrofit.RestCallback;

import id.bl.blcom.iate.api.AuthHelper;
import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.models.Profile;
import id.bl.blcom.iate.models.UserDevice;
import io.realm.Realm;
import okhttp3.Headers;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseIDService";

    @Override
    public void onTokenRefresh() {

        //For registration of token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //To displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);

//        postUserDevice(refreshedToken);

    }

    public void postUserDevice(String refreshedToken) {

        Realm realm = LocalData.getRealm();
        Profile profile = realm.where(Profile.class).findFirst();

        UserDevice userDevice = new UserDevice();
        userDevice.setUserId(profile.getUserId());
        userDevice.setType("Android");
        userDevice.setToken(refreshedToken);

        AuthHelper.registerUserDevice(userDevice, new RestCallback<ApiResponse>() {
            @Override
            public void onSuccess(Headers headers, ApiResponse body) {

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

