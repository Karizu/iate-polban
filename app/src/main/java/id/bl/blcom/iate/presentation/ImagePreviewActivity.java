package id.bl.blcom.iate.presentation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.piasy.biv.view.BigImageView;
import com.github.piasy.biv.view.ImageSaveCallback;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.bl.blcom.iate.R;

public class ImagePreviewActivity extends AppCompatActivity {

    private ShareActionProvider mShareActionProvider;

    @BindView(R.id.mBigImage)
    BigImageView bigImageView;
    @BindView(R.id.videoView)
    VideoView videoView;

    String urlImg;
    int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        urlImg = intent.getStringExtra("imgUrl");
         type = intent.getIntExtra("type", 0);

        if (type == 1){
            bigImageView.setVisibility(View.VISIBLE);
            bigImageView.showImage(Uri.parse(urlImg));
        } else {
            videoView.setVisibility(View.VISIBLE);
            Uri uri = Uri.parse(urlImg);
            videoView.setVideoURI(uri);
            videoView.requestFocus();
            videoView.start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_preview, menu);
//        MenuItem item = menu.findItem(R.id.action_share);
//
//        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
//        sharingIntent.setType("image/*");
//        sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(urlImg));
//        startActivity(Intent.createChooser(sharingIntent, "Share image using"));
//
//        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
//
//        //then set the sharingIntent
//        mShareActionProvider.setShareIntent(sharingIntent);

        return true;
    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            case android.R.id.home:
                // do something useful
                finish();
                return (true);

            case R.id.action_share:
                bigImageView.setImageSaveCallback(new ImageSaveCallback() {
                    @Override
                    public void onSuccess(String uri) {
                        Toast.makeText(ImagePreviewActivity.this,
                                "Download Image Success",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFail(Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(ImagePreviewActivity.this,
                                "Fail",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                bigImageView.saveImageIntoGallery();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

}
