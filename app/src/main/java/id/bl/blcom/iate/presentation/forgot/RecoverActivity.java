package id.bl.blcom.iate.presentation.forgot;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.rezkyatinnov.kyandroid.reztrofit.ErrorResponse;
import com.rezkyatinnov.kyandroid.reztrofit.RestCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.bl.blcom.iate.R;
import id.bl.blcom.iate.api.AuthHelper;
import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.presentation.LoadingDialog;
import id.bl.blcom.iate.presentation.login.LoginActivity;
import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class RecoverActivity extends AppCompatActivity {

    @BindView(R.id.email)
    EditText mEmailView;
    @BindView(R.id.number)
    EditText number;
    @BindView(R.id.newPassword)
    EditText newPassword;
    @BindView(R.id.confirmNewPassword)
    EditText confirmNewPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover);
        ButterKnife.bind(this);


    }

    @OnClick(R.id.forgot_button)
    void prosesRecover(){
        RequestBody requestBody = null;
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        String password = newPassword.getText().toString();

        if (mEmailView.getText().toString().equals("") || number.getText().toString().equals("")
        || newPassword.getText().toString().equals("") || confirmNewPassword.getText().toString().equals("")) {
            Toast.makeText(this, "Mohon lengkapi field", Toast.LENGTH_SHORT).show();
            loadingDialog.dismiss();
        } else if (!confirmNewPassword.getText().toString().equals(newPassword.getText().toString())) {
            confirmNewPassword.setError("Password baru tidak sesuai");
            loadingDialog.dismiss();
        } else {
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("email", mEmailView.getText().toString())
                    .addFormDataPart("number", number.getText().toString())
                    .addFormDataPart("password", newPassword.getText().toString())
                    .build();

            AuthHelper.recoverPassword(requestBody, new RestCallback<ApiResponse>() {
                @Override
                public void onSuccess(Headers headers, ApiResponse body) {
                    loadingDialog.dismiss();
                    Toast.makeText(RecoverActivity.this, body.getMessage(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RecoverActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

                @Override
                public void onFailed(ErrorResponse error) {
                    loadingDialog.dismiss();
                    Log.d("onFailed Error", error.getMessage());
                    Toast.makeText(RecoverActivity.this, "Gagal recover password", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCanceled() {

                }
            });
        }
    }
}
