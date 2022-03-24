package id.bl.blcom.iate.presentation.forgot;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rezkyatinnov.kyandroid.reztrofit.ErrorResponse;
import com.rezkyatinnov.kyandroid.reztrofit.RestCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.bl.blcom.iate.R;
import id.bl.blcom.iate.api.AuthHelper;
import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.models.User;
import okhttp3.Headers;

/**
 * A login screen that offers login via email/password.
 */
public class ForgotActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    // UI references.
    @BindView(R.id.email)
    AutoCompleteTextView mEmailView;

    @BindView(R.id.forgot_button)
    Button mForgotButtonView;

    @BindView(R.id.forgot_progress)
    View mProgressView;

    @BindView(R.id.forgot_form)
    View mForgotFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        ButterKnife.bind(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @OnClick(R.id.forgot_button)
    void onForgotButtonClick() {
        attemptForgot();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptForgot() {

        String email = mEmailView.getText().toString();

        if (!email.equals("")){
            showProgress(true);
            User user = new User();

            user.setEmail(email);

            AuthHelper.forgotPassword(user, new RestCallback<ApiResponse<User>>(){

                @Override
                public void onSuccess(Headers headers, ApiResponse<User> body) {
                    showProgress(false);
                    if (body != null && body.isStatus()) {
                        Intent intent = new Intent(ForgotActivity.this, RecoverActivity.class);
                        startActivity(intent);
                        Toast.makeText(ForgotActivity.this, body.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(ForgotActivity.this, body.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailed(ErrorResponse error) {
                    Log.i("response", "Response Failed");
                    Toast.makeText(ForgotActivity.this, "Email tidak ditemukan", Toast.LENGTH_SHORT).show();
                    showProgress(false);
                }

                @Override
                public void onCanceled() {
                    Log.i("response", "Request Canceled");
                    showProgress(false);
                }
            });
        } else {
            Toast.makeText(ForgotActivity.this, "Username tidak boleh kosong", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mForgotFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mForgotFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mForgotFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(ForgotActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }
}

