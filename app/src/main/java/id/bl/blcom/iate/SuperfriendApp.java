package id.bl.blcom.iate;

import android.app.Application;
import android.content.Context;

import id.bl.blcom.iate.api.ApiInterface;
import id.bl.blcom.iate.models.SuperfriendRealmModule;
import id.bl.blcom.iate.services.GlideImageLoadingService;
import ss.com.bannerslider.Slider;

import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.glide.GlideImageLoader;
import com.rezkyatinnov.kyandroid.Kyandroid;
import com.rezkyatinnov.kyandroid.localdata.KyandroidRealmModule;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

public class SuperfriendApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Kyandroid.init(this,ApiInterface.BASE_URL, ApiInterface.class,"superfriendapp", Context.MODE_PRIVATE
        ,"superfrienddb",1,false, new KyandroidRealmModule(), new SuperfriendRealmModule());

        Slider.init(new GlideImageLoadingService(this));
        BigImageViewer.initialize(GlideImageLoader.with(this));

        EmojiManager.install(new GoogleEmojiProvider());
    }
}
