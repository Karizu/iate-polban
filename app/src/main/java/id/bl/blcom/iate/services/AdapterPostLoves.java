package id.bl.blcom.iate.services;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import id.bl.blcom.iate.R;
import id.bl.blcom.iate.models.User;

public class AdapterPostLoves extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<User> user;
    private Context context;

    public AdapterPostLoves(List<User> user, Context context) {
        this.user = user;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View v1 = inflater.inflate(R.layout.item_post_loves, viewGroup, false);
        viewHolder = new ThreadViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        User userData = user.get(i);
        ThreadViewHolder holder = (ThreadViewHolder) viewHolder;
        holder.fullname.setText(userData.getProfile().getFullname());
        holder.groupName.setText(userData.getUsername());
        Glide.with(context).applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ic_baseline_person_24)).load(userData.getProfile().getPicture()).into(holder.profilePicture);
    }

    @Override
    public int getItemCount() {
        return user.size();
    }

    public class ThreadViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView profilePicture;
        public TextView fullname;
        public TextView groupName;
        public LinearLayout postLayout;

        public ThreadViewHolder(View v){
            super(v);

            profilePicture = v.findViewById(R.id.imgThreadProfile);
            fullname = v.findViewById(R.id.tvFullname);
            groupName = v.findViewById(R.id.tvGroupName);
            postLayout = v.findViewById(R.id.layPostLoves);
        }
    }
}
