package id.bl.blcom.iate.presentation.scanqr;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.rezkyatinnov.kyandroid.localdata.LocalData;
import com.rezkyatinnov.kyandroid.reztrofit.ErrorResponse;
import com.rezkyatinnov.kyandroid.reztrofit.RestCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.bl.blcom.iate.R;
import id.bl.blcom.iate.api.EventHelper;
import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.models.CheckInEvent;
import id.bl.blcom.iate.models.User;
import io.realm.Realm;
import okhttp3.Headers;

public class ScanQrActivity extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener  {

    private static final int MY_PERMISSION_REQUEST_CAMERA = 0;
    String qrtext="";

    @BindView(R.id.qrdecoderview)
    QRCodeReaderView qrCodeReaderView;
    @BindView(R.id.activityScanQrLayout)
    LinearLayout activityScanQrLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
        }else{
            initQr();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_scanqr, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_notification:
                Toast.makeText(ScanQrActivity.this, item.getTitle().toString(), Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                // do something useful
                finish();
                return(true);
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onResume() {
        super.onResume();
        qrCodeReaderView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrCodeReaderView.stopCamera();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case  MY_PERMISSION_REQUEST_CAMERA : {
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent myIntent = new Intent(this,ScanQrActivity.class);
                    startActivity(myIntent);
                    finish();
                } else {
                    AlertDialog dialog = new AlertDialog.Builder(ScanQrActivity.this).create();
                    dialog.setTitle("Allow Camera");
                    dialog.setMessage("Please allow this app to access your camera");
                    dialog.setCancelable(false);
                    dialog.setButton(Dialog.BUTTON_POSITIVE,"OK",(dialogInterface, i) -> {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                        finish();
                    } );
                    dialog.setButton(Dialog.BUTTON_NEGATIVE,"CANCEL",(dialogInterface, i) -> {
                        finish();
                    } );
                    dialog.show();
                }
                break;
            }
        }
    }

    private void initQr(){
        qrCodeReaderView.setOnQRCodeReadListener(this);
        qrCodeReaderView.setQRDecodingEnabled(true);
        qrCodeReaderView.setAutofocusInterval(2000L);
        qrCodeReaderView.setTorchEnabled(true);
        qrCodeReaderView.setFrontCamera();
        qrCodeReaderView.setBackCamera();
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            Snackbar.make(activityScanQrLayout, "Camera access is required to display the camera preview.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", view -> ActivityCompat.requestPermissions(ScanQrActivity.this, new String[] {
                            Manifest.permission.CAMERA
                    }, MY_PERMISSION_REQUEST_CAMERA)).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.CAMERA
            }, MY_PERMISSION_REQUEST_CAMERA);
        }
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        if(!text.equalsIgnoreCase(qrtext)) {
            qrtext = text;

            Realm realmDb = LocalData.getRealm();

            User currentUser = realmDb.where(User.class).findFirst();

            CheckInEvent checkInEvent = new CheckInEvent();

            checkInEvent.setEvent_id(text);
            checkInEvent.setUser_id(currentUser.getId());
            checkInEvent.setLatitude(0);
            checkInEvent.setLongitude(0);

            EventHelper.checkInEvent(checkInEvent, new RestCallback<ApiResponse>() {
                @Override
                public void onSuccess(Headers headers, ApiResponse body) {
                    showMessage();
                    finish();
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void showMessage() {
        Toast.makeText(ScanQrActivity.this, "Check In Success", Toast.LENGTH_SHORT).show();
    }
}
