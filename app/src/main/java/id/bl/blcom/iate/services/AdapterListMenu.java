package id.bl.blcom.iate.services;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import id.bl.blcom.iate.R;
import id.bl.blcom.iate.models.ArticleModel;
import id.bl.blcom.iate.presentation.article.ArticleDetailFragment;

import java.util.List;

public class AdapterListMenu extends RecyclerView.Adapter<AdapterListMenu.ViewHolder> {
    private List<ArticleModel> articleModels;
    private Context context;

    public AdapterListMenu(List<ArticleModel> articleModels, Context context){
        this.articleModels = articleModels;
        this.context = context;
    }

    @Override
    public AdapterListMenu.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_article, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AdapterListMenu.ViewHolder holder, int position){
        final ArticleModel articleModel = articleModels.get(position);
        int imageResource = context.getResources().getIdentifier("@drawable/logo_sidebar",
                null,
                context.getPackageName());
        holder.imageView.setImageResource(imageResource);
        holder.textViewTitle.setText(articleModel.getTitle());
        holder.textViewArticle.setText(articleModel.getArticleDetail());
        holder.imageView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Fragment fragment = new ArticleDetailFragment();
                FragmentManager fragmentManager = articleModel.getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.replace(R.id.screen_area, fragment, "article list");
                fragmentTransaction.addToBackStack("article list");

                fragmentTransaction.commit();
            }
        });
    }

    @Override
    public int getItemCount(){ return articleModels.size();}

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textViewTitle;
        public TextView textViewArticle;

        public ViewHolder(View v){
            super(v);

            imageView = (ImageView) v.findViewById(R.id.article_image_view);
            textViewTitle = (TextView) v.findViewById(R.id.article_title_text_view);
            textViewArticle = (TextView) v.findViewById(R.id.article_detail_text_view);
        }
    }
}
