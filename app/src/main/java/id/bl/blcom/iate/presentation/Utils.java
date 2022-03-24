package id.bl.blcom.iate.presentation;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import id.bl.blcom.iate.R;
import id.bl.blcom.iate.models.ApiResponse;
import retrofit2.Response;

public class Utils {
    public static String errorMsg(Context context, Response<ApiResponse> response) {
        try {
            JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
            return jObjError.getJSONObject("error").getString("message");
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public static String getErrorMsg(Context context, String errorBody) {
        try {
            JSONObject jObjError = new JSONObject(Objects.requireNonNull(errorBody));
            return jObjError.getJSONObject("error").getString("message");
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public static String loadJSONFromAsset(Context context, String fileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

    public boolean saveBitmapToFile(File dir, String fileName, Bitmap bm,
                                    Bitmap.CompressFormat format, int quality) {

        File imageFile = new File(dir,fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);

            bm.compress(format,quality,fos);

            fos.close();

            return true;
        }
        catch (IOException e) {
            Log.e("app",e.getMessage());
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return false;
    }

    public static String getURLForResource (int resourceId) {
        //use BuildConfig.APPLICATION_ID instead of R.class.getPackage().getName() if both are not same
        return Uri.parse("android.resource://"+ R.class.getPackage().getName()+"/" +resourceId).toString();
    }
}
