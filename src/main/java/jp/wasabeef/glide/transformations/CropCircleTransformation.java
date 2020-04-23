package jp.wasabeef.glide.transformations;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;

public class CropCircleTransformation implements Transformation<Bitmap> {
    private BitmapPool mBitmapPool;

    public CropCircleTransformation(Context context) {
        this(Glide.get(context).getBitmapPool());
    }

    public CropCircleTransformation(BitmapPool pool) {
        this.mBitmapPool = pool;
    }

    public Resource<Bitmap> transform(Resource<Bitmap> resource, int outWidth, int outHeight) {
        Bitmap source = (Bitmap) resource.get();
        int size = Math.min(source.getWidth(), source.getHeight());
        int width = (source.getWidth() - size) / 2;
        int height = (source.getHeight() - size) / 2;
        Bitmap bitmap = this.mBitmapPool.get(size, size, Config.ARGB_8888);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(size, size, Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(source, TileMode.CLAMP, TileMode.CLAMP);
        if (!(width == 0 && height == 0)) {
            Matrix matrix = new Matrix();
            matrix.setTranslate((float) (-width), (float) (-height));
            shader.setLocalMatrix(matrix);
        }
        paint.setShader(shader);
        paint.setAntiAlias(true);
        float r = ((float) size) / 2.0f;
        canvas.drawCircle(r, r, r, paint);
        return BitmapResource.obtain(bitmap, this.mBitmapPool);
    }

    public String getId() {
        return "CropCircleTransformation()";
    }
}
