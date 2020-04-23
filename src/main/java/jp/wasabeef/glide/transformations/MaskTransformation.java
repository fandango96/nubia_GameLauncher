package jp.wasabeef.glide.transformations;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import jp.wasabeef.glide.transformations.internal.Utils;

public class MaskTransformation implements Transformation<Bitmap> {
    private static Paint sMaskingPaint = new Paint();
    private BitmapPool mBitmapPool;
    private Context mContext;
    private int mMaskId;

    static {
        sMaskingPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
    }

    public MaskTransformation(Context context, int maskId) {
        this(context, Glide.get(context).getBitmapPool(), maskId);
    }

    public MaskTransformation(Context context, BitmapPool pool, int maskId) {
        this.mBitmapPool = pool;
        this.mContext = context.getApplicationContext();
        this.mMaskId = maskId;
    }

    public Resource<Bitmap> transform(Resource<Bitmap> resource, int outWidth, int outHeight) {
        Bitmap source = (Bitmap) resource.get();
        int width = source.getWidth();
        int height = source.getHeight();
        Bitmap result = this.mBitmapPool.get(width, height, Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        }
        Drawable mask = Utils.getMaskDrawable(this.mContext, this.mMaskId);
        Canvas canvas = new Canvas(result);
        mask.setBounds(0, 0, width, height);
        mask.draw(canvas);
        canvas.drawBitmap(source, 0.0f, 0.0f, sMaskingPaint);
        return BitmapResource.obtain(result, this.mBitmapPool);
    }

    public String getId() {
        return "MaskTransformation(maskId=" + this.mContext.getResources().getResourceEntryName(this.mMaskId) + ")";
    }
}
