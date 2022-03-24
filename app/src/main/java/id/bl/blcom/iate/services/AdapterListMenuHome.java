package id.bl.blcom.iate.services;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import id.bl.blcom.iate.R;
import id.bl.blcom.iate.api.ApiInterface;
import id.bl.blcom.iate.models.ArticleModel;
import id.bl.blcom.iate.presentation.LoadingDialog;
import id.bl.blcom.iate.presentation.news.NewsDetailFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdapterListMenuHome extends RecyclerView.Adapter<AdapterListMenuHome.ViewHolder> {
    private List<ArticleModel> articleModels;
    private Context context;

    public AdapterListMenuHome(List<ArticleModel> articleModels, Context context){
        this.articleModels = articleModels;
        this.context = context;
    }

    @Override
    public AdapterListMenuHome.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_home, parent, false);

        return new AdapterListMenuHome.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AdapterListMenuHome.ViewHolder holder, int position){
        final ArticleModel articleModel = articleModels.get(position);
        final String imgUrl = ApiInterface.BASE_URL_IMAGE + articleModel.getBanner();
        final String title = articleModel.getTitle();
        final String detailArticle = articleModel.getArticleDetail();
        final String createAt = dateFormater(articleModel.getCreateAt(), "dd MMMM yyyy", "yyyy-MM-dd HH:mm:ss");
        final String dateView = dateFormater(articleModel.getCreateAt(), "dd MMM yy", "yyyy-MM-dd HH:mm:ss");
        final String timeView = dateFormater(articleModel.getCreateAt(), "HH:mm", "yyyy-MM-dd HH:mm:ss");
        final String creator = articleModel.getUser().getUsername();

        Glide.with(context).applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ph_berita)).load(ApiInterface.BASE_URL_IMAGE + articleModel.getBanner()).into(holder.imageView);
        holder.textViewTitle.setText(articleModel.getTitle());
        holder.textViewHeadline.setText(articleModel.getArticleHeadline());
        holder.textViewDate.setText(dateView);
        holder.textViewTime.setText(timeView);
        if (title != null){
            Log.d("TAG", title);
        }
        holder.layout.setOnClickListener(view -> {
            LoadingDialog loadingDialog = new LoadingDialog(context);
            loadingDialog.setCancelable(false);
            loadingDialog.show();

            Bundle bundle = new Bundle();
            Fragment fragment = new NewsDetailFragment();
            bundle.putString("pageTitle", "BERITA/PENGUMUMAN");
            bundle.putString("imgUrl", imgUrl);
            bundle.putString("title", title);
            bundle.putString("detailArticle", detailArticle);
            bundle.putString("creator", createAt +" - Oleh "+creator);
            FragmentManager fragmentManager = articleModel.getFragmentManager();
            fragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.screen_area, fragment, "News Detail");
            fragmentTransaction.addToBackStack("News Detail");

            fragmentTransaction.commit();
            Handler handler = new Handler();
            handler.postDelayed(loadingDialog::dismiss, 2000);
        });
    }

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

    @Override
    public int getItemCount(){ return articleModels.size();}

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textViewTitle;
        public TextView textViewHeadline;
        public TextView textViewDate;
        public TextView textViewTime;
        public FrameLayout layout;

        public ViewHolder(View v){
            super(v);

            imageView = (ImageView) v.findViewById(R.id.home_image_view);
            textViewTitle = (TextView) v.findViewById(R.id.title_home_card_view);
            textViewHeadline = (TextView) v.findViewById(R.id.headline_home_card_view);
            textViewDate = v.findViewById(R.id.date_);
            textViewTime = v.findViewById(R.id.time_);
            layout = (FrameLayout) v.findViewById(R.id.layout_news);
        }
    }
}
