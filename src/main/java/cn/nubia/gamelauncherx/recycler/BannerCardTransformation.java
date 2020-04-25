package cn.nubia.gamelauncherx.recycler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;

import androidx.annotation.NonNull;

import cn.nubia.gamelauncherx.R;
import cn.nubia.gamelauncherx.util.BitmapUtils;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

public class BannerCardTransformation extends BitmapTransformation {
    Context mContext;
    Rect mCroppedRect;

    public BannerCardTransformation(Context context) {
        this.mContext = context;
    }

    /* access modifiers changed from: protected */
    public Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return BitmapUtils.getRoundCropBitmapByShader(toTransform, getCardWidth(), getCardHeight(), 34, 6, getCropTranslateY());
    }

    public String getId() {
        return "BannerCardTransformation";
    }

    public int getMoveAlongX() {
        return this.mContext.getResources().getDimensionPixelSize(R.dimen.game_card_move_along_x);
    }

    public int getCropTranslateY() {
        return this.mContext.getResources().getDimensionPixelSize(R.dimen.game_card_crop_translate_y);
    }

    private int getCardWidth() {
        return this.mContext.getResources().getDimensionPixelSize(R.dimen.game_card_width);
    }

    private int getCardHeight() {
        return this.mContext.getResources().getDimensionPixelSize(R.dimen.game_card_height);
    }

    private Rect getCroppedRect() {
        if (this.mCroppedRect == null) {
            this.mCroppedRect = new Rect(0, 0, getCardWidth(), getCardHeight());
        }
        return this.mCroppedRect;
    }

    @Override
    public void updateDiskCacheKey(@NonNull final MessageDigest messageDigest)
    {
        // Do nothing
    }
}
