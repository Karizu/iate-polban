package id.bl.blcom.iate.services;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import id.bl.blcom.iate.R;
import id.bl.blcom.iate.presentation.diskusi.DetailDiskusiActivity;
import id.bl.blcom.iate.presentation.event.EventActivity;
import id.bl.blcom.iate.presentation.login.LoginActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM Service";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("firebase-token", s).apply();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "From: " + remoteMessage.getData().size()); // for the data size

        final Map<String, String> data = remoteMessage.getData();

        String title = data.get("title");
        String body = data.get("body");

        if(!title.equals("") && !body.equals("")){
            sendMyNotification(title, body, data); //send notification to user
        }
    }

    public static String getToken(Context context) {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("firebase-token", "empty");
    }


    private void sendMyNotification(String title, String body, Map<String, String> data) {
        String eventId = "";
        String type = "";
        try {
            Log.d(TAG, "id: " + data.get("id"));
            Log.d(TAG, "type: " + data.get("type"));

            eventId = data.get("id");
            type = data.get("type");
        } catch (Exception e){
            e.printStackTrace();
        }

        NotificationCompat.Builder notificationBuilder;
        PendingIntent pendingIntent;
        Intent intent;

        switch (type){
            case "thread":
                intent = new Intent(this, DetailDiskusiActivity.class);
                intent.putExtra("postId", eventId);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pendingIntent = PendingIntent.getActivity(this,0 /* request code */, intent,PendingIntent.FLAG_UPDATE_CURRENT);

                long[] pattern = {500,500,500,500,500};

                Uri soundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    prepareChannel(this, "event_notification", NotificationManagerCompat.IMPORTANCE_LOW);
                    notificationBuilder = new NotificationCompat.Builder(this, "event_notification")
                            .setSmallIcon(R.drawable.logoheader)
                            .setContentTitle(title)
                            .setContentText(body)
                            .setAutoCancel(true)
                            .setVibrate(pattern)
                            .setLights(Color.BLUE,1,1)
                            .setSound(soundUri)
                            .setContentIntent(pendingIntent);
                } else {
                    notificationBuilder = new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.logoheader)
                            .setContentTitle(title)
                            .setContentText(body)
                            .setAutoCancel(true)
                            .setVibrate(pattern)
                            .setLights(Color.BLUE,1,1)
                            .setSound(soundUri)
                            .setContentIntent(pendingIntent);
                }
                notificationManager.notify(0, notificationBuilder.build());
                break;
            case "event":
                //On click of notification it redirect to this Activity
                intent = new Intent(this, EventActivity.class);
                intent.putExtra("EVENT_ID", eventId);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pendingIntent = PendingIntent.getActivity(this,0 /* request code */, intent,PendingIntent.FLAG_UPDATE_CURRENT);

                long[] patterns = {500,500,500,500,500};

                Uri soundUris= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                NotificationManager notificationManagers =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    prepareChannel(this, "event_notification", NotificationManagerCompat.IMPORTANCE_LOW);
                    notificationBuilder = new NotificationCompat.Builder(this, "event_notification")
                        .setSmallIcon(R.drawable.logoheader)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setVibrate(patterns)
                        .setLights(Color.BLUE,1,1)
                        .setSound(soundUris)
                        .setContentIntent(pendingIntent);
                } else {
                    notificationBuilder = new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.logoheader)
                            .setContentTitle(title)
                            .setContentText(body)
                            .setAutoCancel(true)
                            .setVibrate(patterns)
                            .setLights(Color.BLUE,1,1)
                            .setSound(soundUris)
                            .setContentIntent(pendingIntent);
                }
                notificationManagers.notify(0, notificationBuilder.build());

                break;

            default:
                Intent intents = new Intent(this, LoginActivity.class);
                intents.putExtra("EVENT_ID", eventId);
                intents.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntents = PendingIntent.getActivity(this,0 /* request code */, intents,PendingIntent.FLAG_UPDATE_CURRENT);

                long[] patter = {500,500,500,500,500};

                Uri soundUr= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                NotificationManager notificationManage =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    prepareChannel(this, "event_notification", NotificationManagerCompat.IMPORTANCE_LOW);
                    notificationBuilder = new NotificationCompat.Builder(this, "event_notification")
                            .setSmallIcon(R.drawable.logoheader)
                            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                            .setContentTitle(title)
                            .setContentText(body)
                            .setAutoCancel(true)
                            .setVibrate(patter)
                            .setLights(Color.BLUE,1,1)
                            .setSound(soundUr)
                            .setContentIntent(pendingIntents);
                } else {
                    notificationBuilder = new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.logoheader)
                            .setContentTitle(title)
                            .setContentText(body)
                            .setAutoCancel(true)
                            .setVibrate(patter)
                            .setLights(Color.BLUE,1,1)
                            .setSound(soundUr)
                            .setContentIntent(pendingIntents);
                }
                notificationManage.notify(0, notificationBuilder.build());
                break;
        }
    }

    @TargetApi(26)
    private static void prepareChannel(Context context, String id, int importance) {
        final String appName = context.getString(R.string.app_name);
        String description = "IATE Polban";
        final NotificationManager nm = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);

        if(nm != null) {
            NotificationChannel nChannel = nm.getNotificationChannel(id);

            if (nChannel == null) {
                nChannel = new NotificationChannel(id, appName, importance);
                nChannel.setDescription(description);
                nm.createNotificationChannel(nChannel);
            }
        }
    }

}

