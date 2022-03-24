package id.bl.blcom.iate.models;

import androidx.fragment.app.FragmentManager;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GeneralModel {

    @SerializedName("id")
    @Expose
    private String id;

    private String srcImage;
    private String title;
    private String articleHeadline;
    private String articleDetail;

    private FragmentManager fragmentManager;

    public GeneralModel(String id,
                        String srcImage,
                        String title,
                        String articleHeadline,
                        String articleDetail,
                        FragmentManager fragmentManager){
        this.id = id;
        this.srcImage = srcImage;
        this.title = title;
        this.articleHeadline = articleHeadline;
        this.articleDetail = articleDetail;
        this.fragmentManager = fragmentManager;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSrcImage() {
        return srcImage;
    }

    public void setSrcImage(String srcImage) {
        this.srcImage = srcImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArticleHeadline() {
        return articleHeadline;
    }

    public void setArticleHeadline(String articleHeadline) {
        this.articleHeadline = articleHeadline;
    }

    public String getArticleDetail() {
        return articleDetail;
    }

    public void setArticleDetail(String articleDetail) {
        this.articleDetail = articleDetail;
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }
}

