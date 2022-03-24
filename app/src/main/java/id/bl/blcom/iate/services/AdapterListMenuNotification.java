package id.bl.blcom.iate.services;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rezkyatinnov.kyandroid.reztrofit.ErrorResponse;
import com.rezkyatinnov.kyandroid.reztrofit.RestCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import id.bl.blcom.iate.R;
import id.bl.blcom.iate.api.NotificationHelper;
import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.models.Notification;
import id.bl.blcom.iate.presentation.diskusi.DetailDiskusiActivity;
import id.bl.blcom.iate.presentation.diskusi.DetailDiskusiFragment;
import id.bl.blcom.iate.presentation.event.EventActivity;
import okhttp3.Headers;

public class AdapterListMenuNotification extends RecyclerView.Adapter<AdapterListMenuNotification.ViewHolder>{

    private List<Notification> notifications;
    private Context context;

    public AdapterListMenuNotification(List<Notification> notifications, Context context){
        this.notifications = notifications;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_notification, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AdapterListMenuNotification.ViewHolder holder, int position){
        Notification notification = notifications.get(position);
        Log.d("onBindViewHolder", "" + position);
        holder.dateTime.setText(dateFormater(notification.getCreatedAt(), "dd MMM yy HH:mm", "yyyy-MM-dd HH:mm:ss"));
        holder.description.setText(notification.getContent());

        if (notification.getRead()){
            Glide.with(context).load(R.drawable.icon_notifbuka).into(holder.notifIcon);
        } else {
            Glide.with(context).load(R.drawable.notif_unread).into(holder.notifIcon);
        }

        holder.notificationLayout.setOnClickListener(view -> {

            NotificationHelper.putNotificationIsRead(notification.getId(), new RestCallback<ApiResponse>() {
                @Override
                public void onSuccess(Headers headers, ApiResponse body) {

                }

                @Override
                public void onFailed(ErrorResponse error) {

                }

                @Override
                public void onCanceled() {

                }
            });

            switch (notification.getType()){

                case "event":
                    Intent myIntent = new Intent(context, EventActivity.class);
                    myIntent.putExtra("EVENT_ID", notification.getTypeId());
                    context.startActivity(myIntent);
                    break;

                case "thread":

                    Intent intent = new Intent(context, DetailDiskusiActivity.class);
                    intent.putExtra("postId", notification.getTypeId());
                    context.startActivity(intent);

//                    Bundle bundle = new Bundle();
//                    Fragment fragment = new DetailDiskusiFragment();
//                    bundle.putString("postId", notification.getTypeId());
//                    FragmentManager fragmentManager = notification.getFragmentManager();
//                    fragment.setArguments(bundle);
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    fragmentTransaction.replace(R.id.screen_area, fragment);
//                    fragmentTransaction.addToBackStack("Diskusi");
//                    fragmentTransaction.commit();
//                    break;

            }
        });
    }

    @Override
    public int getItemCount(){ return notifications.size();}

    public static String dateFormater(String dateFromJSON, String expectedFormat, String oldFormat) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(oldFormat);
        Date date = null;
        String convertedDate = null;
        try {
            date = dateFormat.parse(dateFromJSON);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(expectedFormat);
            convertedDate = simpleDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertedDate;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTime;
        public TextView description;
        public LinearLayout notificationLayout;
        public ImageView notifIcon;

        public ViewHolder(View v){
            super(v);

            dateTime = (TextView) v.findViewById(R.id.datetime);
            description = (TextView) v.findViewById(R.id.description);
            notificationLayout = (LinearLayout) v.findViewById(R.id.notification_layout);
            notifIcon = v.findViewById(R.id.notif_icon);

        }
    }

    public void updateData(List<Notification> newProfiles){
        notifications = new ArrayList<>();
        notifications.addAll(newProfiles);
        notifyDataSetChanged();
    }

}
