package id.bl.blcom.iate.services;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
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
import id.bl.blcom.iate.presentation.article.ArticleDetailFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdapterListMenuArticle extends RecyclerView.Adapter<AdapterListMenuArticle.ViewHolder> {
    private List<ArticleModel> articleModels;
    private Context context;

    public AdapterListMenuArticle(List<ArticleModel> articleModels, Context context){
        this.articleModels = articleModels;
        this.context = context;
    }

    @Override
    public AdapterListMenuArticle.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_article, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AdapterListMenuArticle.ViewHolder holder, int position){
        final ArticleModel articleModel = articleModels.get(position);
        final String imgUrl = ApiInterface.BASE_URL_IMAGE + articleModel.getBanner();
        final String title = articleModel.getTitle();
        final String detailArticle = articleModel.getArticleDetail().replace("background-color: ", "");
        final String createAt = dateFormater(articleModel.getCreateAt(), "dd MMMM yyyy", "yyyy-MM-dd HH:mm:ss");
        final String creator = articleModel.getUser().getUsername();

        Glide.with(context).applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ph_banner)).load(imgUrl).into(holder.imageView);
        holder.textViewTitle.setText(articleModel.getTitle());
        holder.textViewArticle.setText(articleModel.getArticleHeadline());
        holder.linearLayout.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            LoadingDialog loadingDialog = new LoadingDialog(context);
            loadingDialog.setCancelable(false);
            loadingDialog.show();

            bundle.putString("pageTitle", "ARTIKEL");
            bundle.putString("imgUrl", imgUrl);
            bundle.putString("title", title);
            bundle.putString("detailArticle", detailArticle);
            bundle.putString("creator", createAt +" - Oleh "+creator);
            bundle.putString("headline", articleModel.getArticleHeadline());
            bundle.putString("source", articleModel.getSource());

            Fragment fragment = new ArticleDetailFragment();
            fragment.setArguments(bundle);
            FragmentManager fragmentManager = articleModel.getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.screen_area, fragment, "article list");
            fragmentTransaction.addToBackStack("article list");

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
        public TextView textViewArticle;
        public FrameLayout linearLayout;

        public ViewHolder(View v){
            super(v);

            imageView = (ImageView) v.findViewById(R.id.article_image_view);
            textViewTitle = (TextView) v.findViewById(R.id.article_title_text_view);
            textViewArticle = (TextView) v.findViewById(R.id.article_detail_text_view);
            linearLayout = (FrameLayout) v.findViewById(R.id.layout_article);
        }
    }
}
