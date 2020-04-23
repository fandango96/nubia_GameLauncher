package jp.wasabeef.glide.transformations;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;

public class ColorFilterTransformation implements Transformation<Bitmap> {
    private BitmapPool mBitmapPool;
    private int mColor;

    public ColorFilterTransformation(Context context, int color) {
        this(Glide.get(context).getBitmapPool(), color);
    }

    public ColorFilterTransformation(BitmapPool pool, int color) {
        this.mBitmapPool = pool;
        this.mColor = color;
    }

    public Resource<Bitmap> transform(Resource<Bitmap> resource, int outWidth, int outHeight) {
        Bitmap source = (Bitmap) resource.get();
        int width = source.getWidth();
        int height = source.getHeight();
        Config config = source.getConfig() != null ? source.getConfig() : Config.ARGB_8888;
        Bitmap bitmap = this.mBitmapPool.get(width, height, config);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(width, height, config);
        }
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColorFilter(new PorterDuffColorFilter(this.mColor, Mode.SRC_ATOP));
        canvas.drawBitmap(source, 0.0f, 0.0f, paint);
        return BitmapResource.obtain(bitmap, this.mBitmapPool);
    }

    public String getId() {
        return "ColorFilterTransformation(color=" + this.mColor + ")";
    }
}
