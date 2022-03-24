package id.bl.blcom.iate.presentation;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.rezkyatinnov.kyandroid.reztrofit.ErrorResponse;
import com.rezkyatinnov.kyandroid.reztrofit.RestCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.bl.blcom.iate.R;
import id.bl.blcom.iate.api.DiscussionHelper;
import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.presentation.diskusi.DiskusiFragment;
import okhttp3.Headers;

public class ReportDialog extends Dialog implements
        android.view.View.OnClickListener {

    private Context context;
    private String groupId;
    private String postId;
    private Boolean showRemoveButton = false;
    private FragmentManager fragmentManager;

    @BindView(R.id.close_dialog)
    ImageView closeDialog;

    @BindView(R.id.btn_remove_post)
    Button removePost;

    @BindView(R.id.btn_report_post)
    Button reportPost;

    public ReportDialog(@NonNull Context context, String groupId, String postId, FragmentManager mFragmentManager) {
        super(context);
        this.context = context;
        this.groupId = groupId;
        this.postId = postId;
        this.fragmentManager = mFragmentManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_post_report);
        ButterKnife.bind(this);

        if (showRemoveButton)
            removePost.setVisibility(View.VISIBLE);

        closeDialog.setOnClickListener(this);
        removePost.setOnClickListener(this);
        reportPost.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.close_dialog:
                dismiss();
                break;

            case R.id.btn_remove_post:
                deletePost();
                break;

            case R.id.btn_report_post:
                ReportReasonDialog reportReasonDialog = new ReportReasonDialog(this.context, this.groupId, this.postId);
                reportReasonDialog.show();
                break;
            default:
                break;

        }
        dismiss();
    }

    public void setShowRemoveButton(Boolean showRemoveButton) {
        this.showRemoveButton = showRemoveButton;
    }

    private void deletePost(){
        DiscussionHelper.deletePost(this.postId, new RestCallback<ApiResponse>() {
            @Override
            public void onSuccess(Headers headers, ApiResponse body) {
                Toast.makeText(context, "Delete Success", Toast.LENGTH_SHORT).show();
                Fragment fragment = new DiskusiFragment();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.screen_area, fragment);
                fragmentTransaction.commit();
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
