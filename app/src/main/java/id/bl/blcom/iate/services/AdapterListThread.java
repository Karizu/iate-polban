package id.bl.blcom.iate.services;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import id.bl.blcom.iate.R;
import id.bl.blcom.iate.api.ApiInterface;
import id.bl.blcom.iate.models.Profile;
import id.bl.blcom.iate.models.response.ThreadDataResponse;

public class AdapterListThread extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ThreadDataResponse> threads;
    private Context context;
    private Profile profile;

    public AdapterListThread(List<ThreadDataResponse> discussions, Profile profile, Context context){
        this.threads = discussions;
        this.context = context;
        this.profile = profile;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View v1 = inflater.inflate(R.layout.item_thread, viewGroup, false);
        viewHolder = new ThreadViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ThreadDataResponse dataResponse = threads.get(position);
        ThreadViewHolder threadViewHolder = (ThreadViewHolder) viewHolder;
        if (this.profile.getUserId().equals(dataResponse.getUserId())){
            threadViewHolder.threadLayout.setVisibility(View.VISIBLE);
            threadViewHolder.threadLayoutTheir.setVisibility(View.GONE);
            if (dataResponse.getUser().getProfile() != null){
                threadViewHolder.username.setText(dataResponse.getUser().getProfile().getFullname());
                Glide.with(context).applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ic_baseline_person_24)).load(ApiInterface.BASE_URL_IMAGE + dataResponse.getUser().getProfile().getPicture()).into(threadViewHolder.profilePicture);
            }

            if (dataResponse.getType() == 1) {
                threadViewHolder.attachImage.setVisibility(View.GONE);
                threadViewHolder.content.setVisibility(View.VISIBLE);
                threadViewHolder.content.setText(dataResponse.getComment());
            } else {
                threadViewHolder.attachImage.setVisibility(View.VISIBLE);
                threadViewHolder.content.setVisibility(View.GONE);
                Glide.with(context).applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ph_image)).load(ApiInterface.BASE_URL_IMAGE + dataResponse.getContent()).into(threadViewHolder.attachImage);
            }

            String result = "Now";
            try {
                PrettyTime timeAgo = new PrettyTime();
                result = timeAgo.format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dataResponse.getCreatedAt()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            threadViewHolder.timeAgo.setText(result);
        }
        else{
            threadViewHolder.threadLayout.setVisibility(View.GONE);
            threadViewHolder.threadLayoutTheir.setVisibility(View.VISIBLE);
            if (dataResponse.getUser().getProfile() != null){
                threadViewHolder.usernameTheir.setText(dataResponse.getUser().getProfile().getFullname());
                Glide.with(context).applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ic_baseline_person_24)).load(ApiInterface.BASE_URL_IMAGE + dataResponse.getUser().getProfile().getPicture()).into(threadViewHolder.profilePictureTheir);
            }

            if (dataResponse.getType() == 1) {
                threadViewHolder.attachImageTheir.setVisibility(View.GONE);
                threadViewHolder.contentTheir.setVisibility(View.VISIBLE);
                threadViewHolder.contentTheir.setText(dataResponse.getComment());
            } else {
                threadViewHolder.attachImageTheir.setVisibility(View.VISIBLE);
                threadViewHolder.contentTheir.setVisibility(View.GONE);
                Glide.with(context).applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ph_image)).load(ApiInterface.BASE_URL_IMAGE + dataResponse.getContent()).into(threadViewHolder.attachImageTheir);
            }

            String result = "Now";
            try {
                PrettyTime timeAgo = new PrettyTime();
                result = timeAgo.format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dataResponse.getCreatedAt()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            threadViewHolder.timeAgoTheir.setText(result);
        }

    }

    @Override
    public int getItemViewType(int position) {
        ThreadDataResponse dataResponse = threads.get(position);
        if (this.profile.getUserId().equals(dataResponse.getUserId())){
            return 1;
        }
        else{
            return 2;
        }
    }

    @Override
    public int getItemCount() {
        return threads.size();
    }

    public class ThreadViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView profilePicture;
        public TextView username;
        public TextView timeAgo;
        public TextView content;
        public ImageView attachImage;
        public LinearLayout threadLayout;

        public CircleImageView profilePictureTheir;
        public TextView usernameTheir;
        public TextView timeAgoTheir;
        public TextView contentTheir;
        public ImageView attachImageTheir;
        public LinearLayout threadLayoutTheir;

        public ThreadViewHolder(View v){
            super(v);

            profilePicture = v.findViewById(R.id.imgThreadProfile);
            username = v.findViewById(R.id.tvUsername);
            timeAgo = v.findViewById(R.id.tvThradTime);
            content = v.findViewById(R.id.tvThreadComment);
            threadLayout = v.findViewById(R.id.thread_me_container);
            attachImage = v.findViewById(R.id.ivAttachImage);


            profilePictureTheir = v.findViewById(R.id.imgThreadProfileTheir);
            usernameTheir = v.findViewById(R.id.tvUsernameTheir);
            timeAgoTheir = v.findViewById(R.id.tvThradTimeTheir);
            contentTheir = v.findViewById(R.id.tvThreadCommentTheir);
            threadLayoutTheir = v.findViewById(R.id.thread_their_container);
            attachImageTheir = v.findViewById(R.id.ivAttachImageTheir);
        }
    }
}
