package id.bl.blcom.iate.presentation;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.rezkyatinnov.kyandroid.localdata.LocalData;
import com.rezkyatinnov.kyandroid.reztrofit.ErrorResponse;
import com.rezkyatinnov.kyandroid.reztrofit.RestCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.bl.blcom.iate.R;
import id.bl.blcom.iate.api.DiscussionHelper;
import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.models.Profile;
import id.bl.blcom.iate.models.ReportPost;
import io.realm.Realm;
import okhttp3.Headers;

public class ReportReasonDialog extends Dialog implements
        android.view.View.OnClickListener {

    private Context context;
    private String groupId;
    private String postId;

    @BindView(R.id.close_dialog)
    ImageView closeDialog;

    @BindView(R.id.btn_report_cancel)
    Button cancelReport;

    @BindView(R.id.btn_report)
    Button reportPost;

    @BindView(R.id.report_reason)
    EditText reportReason;

    public ReportReasonDialog(@NonNull Context context, String groupId, String postId) {
        super(context);
        this.context = context;
        this.groupId = groupId;
        this.postId = postId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_report_reason);
        ButterKnife.bind(this);


        closeDialog.setOnClickListener(this);
        cancelReport.setOnClickListener(this);
        reportPost.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.btn_report_cancel:
                dismiss();
                break;

            case R.id.close_dialog:
                dismiss();
                break;

            case R.id.btn_report:
                doReportPost();
                break;

            default:
                break;
        }
        dismiss();
    }

    private void doReportPost() {

        Realm realm = LocalData.getRealm();
        Profile profile = realm.where(Profile.class).findFirst();

        ReportPost reportPost = new ReportPost();
        reportPost.setGroupId(this.groupId);
        reportPost.setPostId(this.postId);
        reportPost.setContent(reportReason.getText().toString());
        reportPost.setUserId(profile.getUserId());

        DiscussionHelper.createReportPost(reportPost, new RestCallback<ApiResponse>() {
            @Override
            public void onSuccess(Headers headers, ApiResponse body) {
                Toast.makeText(context, "Report Success", Toast.LENGTH_SHORT).show();
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
