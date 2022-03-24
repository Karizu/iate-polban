package id.bl.blcom.iate.models;

import androidx.fragment.app.FragmentManager;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ArticleModel {

    @SerializedName("id")
    @Expose
    private String id;

    private String banner;
    private String title;
    private String articleHeadline;
    private String articleDetail;
    private String createAt;
    private String creator;
    private User user;
    private String source;

    private FragmentManager fragmentManager;

    public ArticleModel(String id,
                        String banner,
                        String title,
                        String articleHeadline,
                        String articleDetail,
                        String createdAt,
                        String creator,
                        User user,
                        FragmentManager fragmentManager,
                        String source){
        this.id = id;
        this.banner = banner;
        this.title = title;
        this.articleHeadline = articleHeadline;
        this.articleDetail = articleDetail;
        this.fragmentManager = fragmentManager;
        this.createAt = createdAt;
        this.creator = creator;
        this.user = user;
        this.source = source;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
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

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}

