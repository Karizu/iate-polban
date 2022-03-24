package id.bl.blcom.iate;

import id.bl.blcom.iate.api.ApiInterface;

public class SuperfriendUtility {
    private static String token;
    private static ApiInterface client;

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        SuperfriendUtility.token = token;
    }

    public static ApiInterface getClient() {
        return client;
    }

    public static void setClient(ApiInterface client) {
        SuperfriendUtility.client = client;
    }
}
