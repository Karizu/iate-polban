package id.bl.blcom.iate.presentation.login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.rezkyatinnov.kyandroid.localdata.LocalData;
import com.rezkyatinnov.kyandroid.reztrofit.ErrorResponse;
import com.rezkyatinnov.kyandroid.reztrofit.RestCallback;
import com.rezkyatinnov.kyandroid.session.Session;
import com.rezkyatinnov.kyandroid.session.SessionNotFoundException;
import com.rezkyatinnov.kyandroid.session.SessionObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.bl.blcom.iate.R;
import id.bl.blcom.iate.RegisterActivity;
import id.bl.blcom.iate.api.AuthHelper;
import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.models.Profile;
import id.bl.blcom.iate.models.User;
import id.bl.blcom.iate.models.UserDevice;
import id.bl.blcom.iate.presentation.LoadingDialog;
import id.bl.blcom.iate.presentation.NavigationDrawerActivity;
import id.bl.blcom.iate.presentation.SplashScreenActivity;
import id.bl.blcom.iate.presentation.forgot.ForgotActivity;
import id.bl.blcom.iate.services.MyFirebaseMessagingService;
import io.realm.Realm;
import io.realm.exceptions.RealmMigrationNeededException;
import okhttp3.Headers;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // UI references.
    @BindView(R.id.username)
    AutoCompleteTextView mEmailView;

    @BindView(R.id.password)
    EditText mPasswordView;

    @BindView(R.id.login_button)
    Button mEmailSignInButton;

    @BindView(R.id.login_form)
    View mLoginFormView;

    @BindView(R.id.forgot_password)
    TextView mForgotPasswordView;

    private Intent intent;
    private final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 1;

    private LoadingDialog loadingDialog;
    private static final int SCHEMA_V_NOW = 2;
    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        intent = getIntent();
        if (intent.hasExtra("username"))
            mEmailView.setText(intent.getExtras().getString("username"));
        if (intent.hasExtra("password"))
            mPasswordView.setText(intent.getExtras().getString("password"));

        loadingDialog = new LoadingDialog(this);
    }

    @OnClick(R.id.login_button)
    void onLoginButtonClick() {
        mEmailSignInButton.setEnabled(false);
        loadingDialog.setCancelable(false);
        loadingDialog.show();
        attemptLogin();
    }

    @OnClick(R.id.register_button)
    void onRegisterButtonClick(){
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.forgot_password)
    void forgotPasswordClick(){
        Intent intent = new Intent(LoginActivity.this, ForgotActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        String username = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        if (!username.equals("") && !password.equals("")){
            User user = new User();

            user.setUsername(username);
            user.setPassword(password);

            AuthHelper.login(user, new RestCallback<ApiResponse<User>>(){

                @Override
                public void onSuccess(Headers headers, ApiResponse<User> body) {
                    loadingDialog.dismiss();
                    mEmailSignInButton.setEnabled(true);

                    if (body != null && body.isStatus()) {
                        Session.save(new SessionObject("Authorization", "Bearer "+body.getToken(),true));
                        Session.save(new SessionObject("token", body.getToken()));
                        Session.save(new SessionObject("group_id", body.getData().getProfile().getGroupId()));
                        Session.save(new SessionObject("username", body.getData().getUsername()));
                        LocalData.saveOrUpdate(body.getData());

                        UserDevice userDevice = new UserDevice();
                        userDevice.setUserId(body.getData().getId());
                        userDevice.setType("Android");
                        userDevice.setToken(MyFirebaseMessagingService.getToken(getApplicationContext()));

                        AuthHelper.registerUserDevice(userDevice, new RestCallback<ApiResponse>() {
                            @Override
                            public void onSuccess(Headers headers, ApiResponse body) {
                                try {
                                    JSONObject userDev = new JSONObject((Map) body.getData());
                                    Log.d("onJson", userDev.getString("id"));
                                    Session.save(new SessionObject("device_id", userDev.getString("id")));
                                } catch (JSONException e) {
                                    Log.d("onException", e.getMessage());
                                }
                            }

                            @Override
                            public void onFailed(ErrorResponse error) {

                            }

                            @Override
                            public void onCanceled() {

                            }
                        });
                        
                        Intent intent = new Intent(LoginActivity.this, NavigationDrawerActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        loadingDialog.dismiss();
                        Toast.makeText(LoginActivity.this, body.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailed(ErrorResponse error) {
                    Log.i("response", "Response Failed");
                    Toast.makeText(LoginActivity.this, "We cant find an account with this credentials. Please make sure you entered the right information.", Toast.LENGTH_SHORT).show();
                    mEmailSignInButton.setEnabled(true);
                    loadingDialog.dismiss();
                }

                @Override
                public void onCanceled() {
                    Log.i("response", "Request Canceled");
                    mEmailSignInButton.setEnabled(true);
                    loadingDialog.dismiss();
                }
            });
        } else {
            Toast.makeText(LoginActivity.this, "Username & Password tidak boleh kosong", Toast.LENGTH_SHORT).show();
            mEmailSignInButton.setEnabled(true);
            loadingDialog.dismiss();
        }
    }
    private void checkPermissionGrant(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_WRITE_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermissionGrant();
        try {
            if (intent.hasExtra("message"))
                Toast.makeText(LoginActivity.this, "Register Berhasil. Silahkan menunggu verifikasi oleh admin", Toast.LENGTH_SHORT).show();

            Session.get("Authorization");
            Intent intent = new Intent(LoginActivity.this, NavigationDrawerActivity.class);
            startActivity(intent);
            finish();
        } catch (SessionNotFoundException e) {
            Session.clear();
            LocalData.clear();
            e.printStackTrace();
        } catch (RealmMigrationNeededException e){
            e.printStackTrace();
            showPopup();
        }
    }

    @SuppressLint("SetTextI18n")
    private void showPopup() {
        dialog = new Dialog(LoginActivity.this);
        //set content
        dialog.setContentView(R.layout.dialog_complete_profile);
        dialog.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);

        ImageView closeImage = dialog.findViewById(R.id.close_dialog);
        TextView desc = dialog.findViewById(R.id.desc);
        Button btn_profile = dialog.findViewById(R.id.btn_profile);

        desc.setText("Sesi anda telah berakhir, lakukan login ulang");
        btn_profile.setText("Keluar");
        btn_profile.setOnClickListener(view -> {
            dialog.dismiss();

            if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                ((ActivityManager)LoginActivity.this.getSystemService(ACTIVITY_SERVICE))
                        .clearApplicationUserData(); // note: it has a return value!
            } else {
                // use old hacky way, which can be removed
                // once minSdkVersion goes above 19 in a few years.
            }

            Intent mStartActivity = new Intent(LoginActivity.this, SplashScreenActivity.class);
            mStartActivity.putExtra("isSessionExpired", true);
            int mPendingIntentId = 123456;
            PendingIntent mPendingIntent = PendingIntent.getActivity(LoginActivity.this, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager mgr = (AlarmManager)LoginActivity.this.getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
            System.exit(0);
        });

        closeImage.setOnClickListener(view -> dialog.dismiss());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private String copyBundledRealmFile(InputStream inputStream, String outFileName) {
        try {
            File file = new File(this.getFilesDir(), outFileName);
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, bytesRead);
            }
            outputStream.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String realmString(Realm realm) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Profile person : realm.where(Profile.class).findAll()) {
            stringBuilder.append(person.toString()).append("\n");
        }

        return (stringBuilder.length() == 0) ? "<data was deleted>" : stringBuilder.toString();
    }

    private void showStatus(Realm realm) {
        showStatus(realmString(realm));
    }

    private void showStatus(String txt) {
        Log.i("TAG", txt);
    }

}

