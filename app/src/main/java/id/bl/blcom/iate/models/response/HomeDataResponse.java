package id.bl.blcom.iate.models.response;

import java.util.List;

public class HomeDataResponse{
    private List<GeneralDataResponse> news;
    private List<EventDataResponse> event;
    private List<GeneralDataResponse> banner;

    public List<GeneralDataResponse> getNews() {
        return news;
    }

    public void setNews(List<GeneralDataResponse> news) {
        this.news = news;
    }

    public List<EventDataResponse> getEvent() {
        return event;
    }

    public void setEvent(List<EventDataResponse> event) {
        this.event = event;
    }

    public List<GeneralDataResponse> getBanner() {
        return banner;
    }

    public void setBanner(List<GeneralDataResponse> banner) {
        this.banner = banner;
    }
}
