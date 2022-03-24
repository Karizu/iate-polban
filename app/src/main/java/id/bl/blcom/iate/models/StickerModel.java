package id.bl.blcom.iate.models;

import android.graphics.drawable.Drawable;
import androidx.fragment.app.FragmentManager;

public class StickerModel {
    public int imgSticker;
    public Drawable imgStickerDraw;
    public String descSticker;
    public FragmentManager fragmentManager;

    public int getImgSticker() {
        return imgSticker;
    }

    public void setImgSticker(int imgSticker) {
        this.imgSticker = imgSticker;
    }

    public Drawable getImgStickerDraw() {
        return imgStickerDraw;
    }

    public void setImgStickerDraw(Drawable imgStickerDraw) {
        this.imgStickerDraw = imgStickerDraw;
    }

    public String getDescSticker() {
        return descSticker;
    }

    public void setDescSticker(String descSticker) {
        this.descSticker = descSticker;
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }
}
