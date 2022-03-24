package id.bl.blcom.iate.models.response;

public class AreaDataResponse {
    private String id;
    private String name;
    private String createdAt;
    private String upatedAt;
    private String deletedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpatedAt() {
        return upatedAt;
    }

    public void setUpatedAt(String upatedAt) {
        this.upatedAt = upatedAt;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }
}
