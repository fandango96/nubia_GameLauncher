package cn.nubia.commonui.blureffect;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

public class BlurEffect {
    public static final int DEFAULT_SCALE = 16;
    public static final int DEFAULT_SIGMA = 100;

    public static native void getFrostedGlassMask(Bitmap bitmap, int i, int i2, int i3, int i4);

    public static native void getGaussianBlurEffect(Bitmap bitmap, Bitmap bitmap2, float f);

    public static native void getSimpleBlurEffect(Bitmap bitmap, Bitmap bitmap2, int i);

    static {
        System.loadLibrary("BlurEffect");
    }

    public static boolean doGaussianBlur(Bitmap bitmap) {
        return doGaussianBlur(bitmap, 16, 100.0f);
    }

    public static boolean doGaussianBlur(Bitmap bitmap, int scale, float sigma) {
        if (bitmap == null || true == bitmap.isRecycled()) {
            return false;
        }
        int width = bitmap.getWidth() / scale;
        int height = bitmap.getHeight() / scale;
        float sigma2 = sigma / ((float) scale);
        Bitmap bitmapSubIn = Bitmap.createScaledBitmap(bitmap, width, height, true);
        Bitmap bitmapSubOut = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        getGaussianBlurEffect(bitmapSubIn, bitmapSubOut, sigma2);
        bitmapSubIn.recycle();
        Canvas canvas = new Canvas(bitmap);
        Rect srcRect = new Rect(0, 0, width, height);
        Rect dstRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Paint paint = new Paint();
        paint.setAntiAlias(false);
        paint.setDither(false);
        paint.setFilterBitmap(true);
        canvas.drawBitmap(bitmapSubOut, srcRect, dstRect, paint);
        bitmapSubOut.recycle();
        return true;
    }

    public static Bitmap doGaussianBlur2(Bitmap bitmap) {
        return doGaussianBlurSelf(bitmap, 16, 100.0f);
    }

    public static Bitmap doGaussianBlur2(Bitmap bitmap, int scale, float sigma) {
        return doGaussianBlurSelf(bitmap, scale, sigma);
    }

    private static Bitmap doGaussianBlurSelf(Bitmap srcBmp, int scale, float sigma) {
        int srcWidth = srcBmp.getWidth();
        int srcHeight = srcBmp.getHeight();
        int width = srcWidth / scale;
        int height = srcHeight / scale;
        float sigma2 = sigma / ((float) scale);
        try {
            Bitmap bitmapSubIn = Bitmap.createScaledBitmap(srcBmp, width, height, true);
            Bitmap bitmapSubOut = Bitmap.createBitmap(width, height, Config.ARGB_8888);
            getGaussianBlurEffect(bitmapSubIn, bitmapSubOut, sigma2);
            bitmapSubIn.recycle();
            return Bitmap.createScaledBitmap(bitmapSubOut, srcWidth, srcHeight, true);
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    public static Bitmap doArcBitmap(Bitmap srcBmp, int arcHeight) {
        Paint paint = new Paint();
        int bmpWidth = srcBmp.getWidth();
        int bmpHeight = srcBmp.getHeight();
        Canvas canvas = new Canvas(srcBmp);
        canvas.drawColor(0);
        paint.setColor(4342338);
        paint.setAntiAlias(true);
        paint.setDither(true);
        float radius = ((((float) (bmpWidth * bmpWidth)) * 0.125f) / ((float) arcHeight)) + ((float) arcHeight);
        float cx = ((float) bmpWidth) * 0.5f;
        float cy = (((float) bmpHeight) + radius) - ((float) arcHeight);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawCircle(cx, cy, radius, paint);
        canvas.setBitmap(null);
        return srcBmp;
    }

    public static Bitmap doBitmapGaussianBlurByDefault(Bitmap wallpaperBmp, Rect clipRect) {
        return doBitmapGaussianBlurInGeneral(wallpaperBmp, clipRect, 16, 100.0f);
    }

    public static Bitmap doBitmapGaussianBlurInGeneral(Bitmap wallpaperBmp, Rect clipRect, int scale, float sigma) {
        Bitmap clipBmp = clipWallPaper(wallpaperBmp, clipRect);
        if (clipBmp == null) {
            return null;
        }
        return doGaussianBlurSelf(clipBmp, scale, sigma);
    }

    public static Bitmap doGaussianBlurByDefault(Context context, Rect clipRect) {
        return doGaussianBlurInGeneral(context, clipRect, 16, 100.0f);
    }

    public static Bitmap doGaussianBlurInGeneral(Context context, Rect clipRect, int scale, float sigma) {
        Bitmap clipBmp = clipWallPaper(((BitmapDrawable) WallpaperManager.getInstance(context).getDrawable()).getBitmap(), clipRect);
        if (clipBmp == null) {
            return null;
        }
        return doGaussianBlurSelf(clipBmp, scale, sigma);
    }

    private static Bitmap clipWallPaper(Bitmap wallPapaerBmp, Rect clipRect) {
        if (wallPapaerBmp == null) {
            return null;
        }
        int rWidth = clipRect.width();
        int rHeight = clipRect.height();
        int dWidth = wallPapaerBmp.getWidth();
        int dHeight = wallPapaerBmp.getHeight();
        if (rWidth == dWidth && rHeight == dHeight) {
            return wallPapaerBmp;
        }
        try {
            Bitmap bitmap = Bitmap.createBitmap(rWidth, rHeight, Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            Rect dstRect = new Rect(0, 0, rWidth, rHeight);
            Rect srcRect = new Rect(clipRect.left, clipRect.top, clipRect.right, clipRect.bottom);
            if (dWidth < rWidth) {
                srcRect.right = dWidth;
                srcRect.left = 0;
            }
            canvas.drawBitmap(wallPapaerBmp, srcRect, dstRect, null);
            canvas.setBitmap(null);
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }
}
