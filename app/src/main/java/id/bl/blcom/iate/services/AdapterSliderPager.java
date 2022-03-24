package id.bl.blcom.iate.services;

import java.util.List;

import id.bl.blcom.iate.R;
import id.bl.blcom.iate.api.ApiInterface;
import id.bl.blcom.iate.models.response.GeneralDataResponse;
import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class AdapterSliderPager extends SliderAdapter {

    List<GeneralDataResponse> mBanner;

    public AdapterSliderPager(List<GeneralDataResponse> banner) {
        mBanner = banner;
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder imageSlideViewHolder) {
        imageSlideViewHolder.bindImageSlide(ApiInterface.BASE_URL_IMAGE +  mBanner.get(position).getBanner(), R.drawable.ph_banner, R.drawable.ph_banner);
    }

    @Override
    public int getItemCount() {
        return mBanner.size();
    }
}

