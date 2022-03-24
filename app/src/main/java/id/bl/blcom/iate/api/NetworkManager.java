package id.bl.blcom.iate.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

/**
 * Created by Dhimas on 10/6/17.
 */

public class NetworkManager {
    public static ApiInterface instance;
    public static Retrofit retrofit;

    private static final int CONNECT_TIME_OUT = 300 * 1000;
    private static final int READ_TIME_OUT = 300 * 1000;

    public static OkHttpClient client(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }
}
