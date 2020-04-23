package jp.wasabeef.glide.transformations;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.RectF;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;

public class CropTransformation implements Transformation<Bitmap> {
    private BitmapPool mBitmapPool;
    private CropType mCropType;
    private int mHeight;
    private int mWidth;

    public enum CropType {
        TOP,
        CENTER,
        BOTTOM
    }

    public CropTransformation(Context context) {
        this(Glide.get(context).getBitmapPool());
    }

    public CropTransformation(BitmapPool pool) {
        this(pool, 0, 0);
    }

    public CropTransformation(Context context, int width, int height) {
        this(Glide.get(context).getBitmapPool(), width, height);
    }

    public CropTransformation(BitmapPool pool, int width, int height) {
        this(pool, width, height, CropType.CENTER);
    }

    public CropTransformation(Context context, int width, int height, CropType cropType) {
        this(Glide.get(context).getBitmapPool(), width, height, cropType);
    }

    public CropTransformation(BitmapPool pool, int width, int height, CropType cropType) {
        this.mCropType = CropType.CENTER;
        this.mBitmapPool = pool;
        this.mWidth = width;
        this.mHeight = height;
        this.mCropType = cropType;
    }

    public Resource<Bitmap> transform(Resource<Bitmap> resource, int outWidth, int outHeight) {
        Bitmap source = (Bitmap) resource.get();
        this.mWidth = this.mWidth == 0 ? source.getWidth() : this.mWidth;
        this.mHeight = this.mHeight == 0 ? source.getHeight() : this.mHeight;
        Config config = source.getConfig() != null ? source.getConfig() : Config.ARGB_8888;
        Bitmap bitmap = this.mBitmapPool.get(this.mWidth, this.mHeight, config);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(this.mWidth, this.mHeight, config);
        }
        float scale = Math.max(((float) this.mWidth) / ((float) source.getWidth()), ((float) this.mHeight) / ((float) source.getHeight()));
        float scaledWidth = scale * ((float) source.getWidth());
        float scaledHeight = scale * ((float) source.getHeight());
        float left = (((float) this.mWidth) - scaledWidth) / 2.0f;
        float top = getTop(scaledHeight);
        new Canvas(bitmap).drawBitmap(source, null, new RectF(left, top, left + scaledWidth, top + scaledHeight), null);
        return BitmapResource.obtain(bitmap, this.mBitmapPool);
    }

    public String getId() {
        return "CropTransformation(width=" + this.mWidth + ", height=" + this.mHeight + ", cropType=" + this.mCropType + ")";
    }

    private float getTop(float scaledHeight) {
        switch (this.mCropType) {
            case CENTER:
                return (((float) this.mHeight) - scaledHeight) / 2.0f;
            case BOTTOM:
                return ((float) this.mHeight) - scaledHeight;
            default:
                return 0.0f;
        }
    }
}
