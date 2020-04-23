package jp.wasabeef.glide.transformations;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;

public class CropSquareTransformation implements Transformation<Bitmap> {
    private BitmapPool mBitmapPool;
    private int mHeight;
    private int mWidth;

    public CropSquareTransformation(Context context) {
        this(Glide.get(context).getBitmapPool());
    }

    public CropSquareTransformation(BitmapPool pool) {
        this.mBitmapPool = pool;
    }

    public Resource<Bitmap> transform(Resource<Bitmap> resource, int outWidth, int outHeight) {
        Bitmap source = (Bitmap) resource.get();
        int size = Math.min(source.getWidth(), source.getHeight());
        this.mWidth = (source.getWidth() - size) / 2;
        this.mHeight = (source.getHeight() - size) / 2;
        Bitmap bitmap = this.mBitmapPool.get(this.mWidth, this.mHeight, source.getConfig() != null ? source.getConfig() : Config.ARGB_8888);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(source, this.mWidth, this.mHeight, size, size);
        }
        return BitmapResource.obtain(bitmap, this.mBitmapPool);
    }

    public String getId() {
        return "CropSquareTransformation(width=" + this.mWidth + ", height=" + this.mHeight + ")";
    }
}
