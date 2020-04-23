package jp.wasabeef.glide.transformations;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;

public class GrayscaleTransformation implements Transformation<Bitmap> {
    private BitmapPool mBitmapPool;

    public GrayscaleTransformation(Context context) {
        this(Glide.get(context).getBitmapPool());
    }

    public GrayscaleTransformation(BitmapPool pool) {
        this.mBitmapPool = pool;
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
        ColorMatrix saturation = new ColorMatrix();
        saturation.setSaturation(0.0f);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(saturation));
        canvas.drawBitmap(source, 0.0f, 0.0f, paint);
        return BitmapResource.obtain(bitmap, this.mBitmapPool);
    }

    public String getId() {
        return "GrayscaleTransformation()";
    }
}
