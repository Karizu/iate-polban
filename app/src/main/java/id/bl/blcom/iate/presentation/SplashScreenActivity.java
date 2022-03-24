package id.bl.blcom.iate.presentation;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.rezkyatinnov.kyandroid.localdata.LocalData;
import com.rezkyatinnov.kyandroid.session.Session;
import com.rezkyatinnov.kyandroid.session.SessionNotFoundException;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.bl.blcom.iate.R;
import id.bl.blcom.iate.presentation.login.LoginActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @BindView(R.id.SplashScreenImage)
    ImageView splashScreenImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);

        splashScreenImage.startAnimation(AnimationUtils.loadAnimation(this, R.anim.side_slide));

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, 3000);
    }
}