package id.bl.blcom.iate.models.response;

import id.bl.blcom.iate.models.User;

public class GeneralDataResponse {
    private String id;
    private String creator;
    private String title;
    private String headline;
    private String banner;
    private String content;
    private int status;
    private int is_banner;
    private int is_filtered;
    private int start_age;
    private int end_age;
    private User user;
    private String interest;
    private String works;
    private String created_at;
    private String updated_at;
    private String deleted_at;
    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int isStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int isIs_banner() {
        return is_banner;
    }

    public void setIs_banner(int is_banner) {
        this.is_banner = is_banner;
    }

    public int isIs_filtered() {
        return is_filtered;
    }

    public void setIs_filtered(int is_filtered) {
        this.is_filtered = is_filtered;
    }

    public int isStart_age() {
        return start_age;
    }

    public void setStart_age(int start_age) {
        this.start_age = start_age;
    }

    public int isEnd_age() {
        return end_age;
    }

    public void setEnd_age(int end_age) {
        this.end_age = end_age;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getWorks() {
        return works;
    }

    public void setWorks(String works) {
        this.works = works;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
