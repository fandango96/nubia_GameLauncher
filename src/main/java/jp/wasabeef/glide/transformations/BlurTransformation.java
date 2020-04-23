package jp.wasabeef.glide.transformations;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build.VERSION;
import android.renderscript.RSRuntimeException;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import jp.wasabeef.glide.transformations.internal.FastBlur;
import jp.wasabeef.glide.transformations.internal.RSBlur;

public class BlurTransformation implements Transformation<Bitmap> {
    private static int DEFAULT_DOWN_SAMPLING = 1;
    private static int MAX_RADIUS = 25;
    private BitmapPool mBitmapPool;
    private Context mContext;
    private int mRadius;
    private int mSampling;

    public BlurTransformation(Context context) {
        this(context, Glide.get(context).getBitmapPool(), MAX_RADIUS, DEFAULT_DOWN_SAMPLING);
    }

    public BlurTransformation(Context context, BitmapPool pool) {
        this(context, pool, MAX_RADIUS, DEFAULT_DOWN_SAMPLING);
    }

    public BlurTransformation(Context context, BitmapPool pool, int radius) {
        this(context, pool, radius, DEFAULT_DOWN_SAMPLING);
    }

    public BlurTransformation(Context context, int radius) {
        this(context, Glide.get(context).getBitmapPool(), radius, DEFAULT_DOWN_SAMPLING);
    }

    public BlurTransformation(Context context, int radius, int sampling) {
        this(context, Glide.get(context).getBitmapPool(), radius, sampling);
    }

    public BlurTransformation(Context context, BitmapPool pool, int radius, int sampling) {
        this.mContext = context.getApplicationContext();
        this.mBitmapPool = pool;
        this.mRadius = radius;
        this.mSampling = sampling;
    }

    public Resource<Bitmap> transform(Resource<Bitmap> resource, int outWidth, int outHeight) {
        Bitmap bitmap;
        Bitmap source = (Bitmap) resource.get();
        int width = source.getWidth();
        int scaledWidth = width / this.mSampling;
        int scaledHeight = source.getHeight() / this.mSampling;
        Bitmap bitmap2 = this.mBitmapPool.get(scaledWidth, scaledHeight, Config.ARGB_8888);
        if (bitmap2 == null) {
            bitmap2 = Bitmap.createBitmap(scaledWidth, scaledHeight, Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap2);
        canvas.scale(1.0f / ((float) this.mSampling), 1.0f / ((float) this.mSampling));
        Paint paint = new Paint();
        paint.setFlags(2);
        canvas.drawBitmap(source, 0.0f, 0.0f, paint);
        if (VERSION.SDK_INT >= 18) {
            try {
                bitmap = RSBlur.blur(this.mContext, bitmap2, this.mRadius);
            } catch (RSRuntimeException e) {
                bitmap = FastBlur.blur(bitmap2, this.mRadius, true);
            }
        } else {
            bitmap = FastBlur.blur(bitmap2, this.mRadius, true);
        }
        return BitmapResource.obtain(bitmap, this.mBitmapPool);
    }

    public String getId() {
        return "BlurTransformation(radius=" + this.mRadius + ", sampling=" + this.mSampling + ")";
    }
}
