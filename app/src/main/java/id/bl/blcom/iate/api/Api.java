package id.bl.blcom.iate.api;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api {
    private static <T> T builder(Class<T> endpoint) {
        return new Retrofit.Builder()
                .client(NetworkManager.client())
                .baseUrl(ApiInterface.BASE_URL_JSON)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(endpoint);
    }

    public static ApiInterface apiInterface() {
        return builder(ApiInterface.class);
    }
}