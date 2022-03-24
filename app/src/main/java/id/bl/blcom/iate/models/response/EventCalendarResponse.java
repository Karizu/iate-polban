package id.bl.blcom.iate.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EventCalendarResponse {

    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("total")
    @Expose
    private int total;
    @SerializedName("data")
    @Expose
    private List<EventDataResponse> data;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<EventDataResponse> getData() {
        return data;
    }

    public void setData(List<EventDataResponse> data) {
        this.data = data;
    }
}
