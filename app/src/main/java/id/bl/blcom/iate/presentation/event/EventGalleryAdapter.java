package id.bl.blcom.iate.presentation.event;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.bl.blcom.iate.R;
import id.bl.blcom.iate.api.ApiInterface;
import id.bl.blcom.iate.models.Media;
import id.bl.blcom.iate.presentation.ImagePreviewActivity;


public class EventGalleryAdapter extends RecyclerView.Adapter<EventGalleryAdapter.ViewHolder> {

    private final Context mContext;
    private final List<Media> mValues;

    public EventGalleryAdapter(Context context, List<Media> items) {
        super();
        mValues = items;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_event_galery_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);

        String imgUrl = ApiInterface.BASE_URL_IMAGE + holder.mItem.getMedia();

        if (holder.mItem.getType() == 1){
            Glide.with(mContext)
                    .applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ph_event))
                    .load(imgUrl)
                    .into(holder.imgMedia);
        } else {
            Glide.with(mContext)
                    .applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ph_event))
                    .load(R.drawable.ic_play_circle_outline)
                    .into(holder.imgMedia);
        }

        holder.mView.setOnClickListener(v -> {
            Intent myIntent = new Intent(mContext, ImagePreviewActivity.class);
            myIntent.putExtra("imgUrl", imgUrl);
            myIntent.putExtra("type", holder.mItem.getType());
            mContext.startActivity(myIntent);
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        @BindView(R.id.imgMedia)
        public ImageView imgMedia;
        public Media mItem;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mView = view;
        }
    }
}
