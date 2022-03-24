package id.bl.blcom.iate.presentation.article;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Objects;

import id.bl.blcom.iate.R;
import id.bl.blcom.iate.presentation.ImagePreviewActivity;
import id.bl.blcom.iate.presentation.NavigationDrawerActivity;

public class ArticleDetailFragment extends Fragment {
    private String id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_article_detail, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateData();
        ((NavigationDrawerActivity)getActivity()).showBackMenu(true);

        RelativeLayout imgNotification = getActivity().findViewById(R.id.img_notification_toolbar);
        imgNotification.setVisibility(View.GONE);

        ImageView imgShare = getActivity().findViewById(R.id.img_share_toolbar);
        imgShare.setVisibility(View.VISIBLE);
        imgShare.setOnClickListener(view1 -> {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, getArguments().getString("title")+
            "\n"+ getArguments().getString("headline")+ "\n" +getArguments().getString("source"));
            startActivity(Intent.createChooser(sharingIntent, "Share using:"));
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void populateData(){
        String pageTitle = requireArguments().getString("pageTitle");
        String imgUrl = getArguments().getString("imgUrl");
        String title = getArguments().getString("title");
        String creator = getArguments().getString("creator");
        String detailArticle = getArguments().getString("detailArticle");

        TextView txtPageTitle = requireActivity().findViewById(R.id.article_detail_text_artikel);
        ImageView imageView = getActivity().findViewById(R.id.img_article_detail);
        TextView txtTitle = getActivity().findViewById(R.id.article_detail_title);
        WebView txtDetail = getActivity().findViewById(R.id.article_detail_text);
        TextView txtCreator = getActivity().findViewById(R.id.creator);

        txtPageTitle.setText(pageTitle);
        txtCreator.setText(creator);
        Glide.with(this).applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ph_banner)).load(imgUrl).into(imageView);
        txtTitle.setText(title);

        imageView.setOnClickListener(view -> {
            Intent myIntent = new Intent(getActivity(), ImagePreviewActivity.class);
            myIntent.putExtra("imgUrl", imgUrl);
            myIntent.putExtra("type", 1);
            startActivity(myIntent);
        });

        String content = "<html><head>"
                + "<style type=\"text/css\"> body{color: #483434} p{color: white; font-size: 12px;}"
                + "</style></head>"
                + "<body>"
                + detailArticle
                + "</body></html>";

        txtDetail.getSettings().setJavaScriptEnabled(true);
        txtDetail.setBackgroundColor(Color.TRANSPARENT);
        String base64version = Base64.encodeToString(content.getBytes(), Base64.DEFAULT);
        txtDetail.loadData(base64version, "text/html; charset=UTF-8", "base64");
    }

    private void setId(String id){
        this.id = id;
    }
}
