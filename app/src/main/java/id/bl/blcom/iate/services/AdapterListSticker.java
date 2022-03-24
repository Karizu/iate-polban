package id.bl.blcom.iate.services;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.List;

import id.bl.blcom.iate.R;
import id.bl.blcom.iate.models.Profile;
import id.bl.blcom.iate.models.StickerModel;
import id.bl.blcom.iate.presentation.diskusi.DetailDiskusiActivity;

public class AdapterListSticker extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<StickerModel> threads;
    private Context context;
    private String postId;
    private Profile profile;
    private Activity fragment;
    private Activity activity;

    public AdapterListSticker(List<StickerModel> threads, String postId, Profile profile, Activity fragment, Activity activity,Context context) {
        this.threads = threads;
        this.context = context;
        this.postId = postId;
        this.profile = profile;
        this.fragment = fragment;
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View v1 = inflater.inflate(R.layout.item_list_sticker, viewGroup, false);
        viewHolder = new ThreadViewHolder(v1);
        return viewHolder;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        StickerModel stickerModel = threads.get(i);
        ThreadViewHolder threadViewHolder = (ThreadViewHolder) viewHolder;
        threadViewHolder.imgSticker.setImageDrawable(context.getResources().getDrawable(stickerModel.getImgSticker()));

//        threadViewHolder.frameSticker.setOnTouchListener((v, event) -> {
//            if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                if (threadViewHolder.imgStickerPreview.getVisibility() == View.GONE){
//                    threadViewHolder.imgStickerPreview.setVisibility(View.VISIBLE);
//                    threadViewHolder.imgStickerPreview.setImageDrawable(context.getResources().getDrawable(stickerModel.getImgSticker()));
//                    return true;
//                }
//            }
//
//            if (event.getAction() == MotionEvent.ACTION_UP) {
//                threadViewHolder.imgStickerPreview.setVisibility(View.GONE);
////                    threadViewHolder.imgStickerPreview.setImageDrawable(context.getResources().getDrawable(stickerModel.getImgSticker()));
//                return true;
//            }
//
//            return false;
//        });

        threadViewHolder.frameSticker.setOnClickListener(v -> ((DetailDiskusiActivity) fragment).populatePreviewSticker(true, stickerModel.getImgSticker(), stickerModel.getImgStickerDraw(), context));
    }

    @Override
    public int getItemCount() {
        return threads.size();
    }

    public class ThreadViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgSticker;
        public ImageView imgStickerPreview;
        public FrameLayout frameSticker;

        public ThreadViewHolder(View v){
            super(v);

            imgSticker = v.findViewById(R.id.imgSticker);
            frameSticker = v.findViewById(R.id.frameSticker);
            imgStickerPreview = v.findViewById(R.id.imgStickerPreview);

        }
    }
}
