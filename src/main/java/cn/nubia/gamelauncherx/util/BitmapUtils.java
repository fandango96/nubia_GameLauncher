package cn.nubia.gamelauncherx.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.core.view.ViewCompat;

import cn.nubia.gamelauncherx.GameLauncherApplication;
import cn.nubia.gamelauncherx.R;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BitmapUtils {
    private static final int COLOR_GRADIENT_END = Color.parseColor("#FF27F5F2");
    private static final int COLOR_GRADIENT_MIDDLE = Color.parseColor("#FF215DFF");
    private static final int COLOR_GRADIENT_START = Color.parseColor("#FFF72525");

    public static Bitmap createBitmapWithProcess(Bitmap src, float rate) {
        if (src == null || src.isRecycled()) {
            return null;
        }
        if (rate > 0.0f) {
            rate /= 100.0f;
        }
        Bitmap maskBitmap = BitmapFactory.decodeResource(GameLauncherApplication.CONTEXT.getResources(), R.mipmap.download_mask_icon).copy(Config.ARGB_8888, true);
        int width = maskBitmap.getWidth();
        int height = maskBitmap.getHeight();
        Bitmap srcScale = Bitmap.createScaledBitmap(src, width, height, false);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(srcScale, 0.0f, 0.0f, null);
        Rect rect = new Rect();
        rect.set(0, (int) (((float) height) * rate), width, height);
        canvas.drawBitmap(maskBitmap, rect, rect, null);
        return adapterBottomIcon(bitmap);
    }

    private static Bitmap adapterBottomIcon(Bitmap topBitmap) {
        Bitmap bottom = BitmapFactory.decodeResource(GameLauncherApplication.CONTEXT.getResources(), R.mipmap.download_icon_bottom).copy(Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bottom);
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_ATOP));
        canvas.drawBitmap(topBitmap, 0.0f, 0.0f, paint);
        return bottom;
    }

    public static byte[] flattenBitmap(Bitmap bitmap) {
        byte[] bArr;
        ByteArrayOutputStream out = new ByteArrayOutputStream(bitmap.getWidth() * bitmap.getHeight() * 4);
        try {
            bitmap.compress(CompressFormat.PNG, 100, out);
            out.flush();
            bArr = out.toByteArray();
        } catch (IOException e) {
            Log.i("BitmapUtils", "flattenBitmap ", e);
            bArr = null;
        } finally {
            try {
                out.close();
            } catch (IOException e2) {
            }
        }
        return bArr;
    }

    public static Bitmap getParallelogramBitmap(Bitmap bitmap, int moveAlongX, Rect rect) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int offsetWidth = (width - rect.width()) / 2;
        int offsetHeight = (bitmap.getHeight() - rect.height()) / 2;
        Bitmap result = Bitmap.createBitmap(rect.width(), rect.height(), Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawARGB(0, 0, 0, 0);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(ViewCompat.MEASURED_STATE_MASK);
        Path path = new Path();
        path.moveTo((float) moveAlongX, 0.0f);
        path.lineTo(0.0f, (float) rect.height());
        path.lineTo((float) (rect.width() - moveAlongX), (float) rect.height());
        path.lineTo((float) (rect.width() + 0), 0.0f);
        path.close();
        canvas.drawPath(path, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, (float) (-offsetWidth), (float) (-offsetHeight), paint);
        return result;
    }

    public static Bitmap getZoomImage(Bitmap orgBitmap, double newWidth, double newHeight, boolean isZoomBySameRatio) {
        float scale;
        if (orgBitmap == null) {
            return null;
        }
        if (orgBitmap.isRecycled()) {
            return null;
        }
        if (newWidth <= 0.0d || newHeight <= 0.0d) {
            return null;
        }
        float width = (float) orgBitmap.getWidth();
        float height = (float) orgBitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        if (isZoomBySameRatio) {
            if (scaleWidth > scaleHeight) {
                scale = scaleWidth;
            } else {
                scale = scaleHeight;
            }
            matrix.postScale(scale, scale);
        } else {
            matrix.postScale(scaleWidth, scaleHeight);
        }
        return Bitmap.createBitmap(orgBitmap, 0, 0, (int) width, (int) height, matrix, true);
    }

    public static Drawable convertBitmapToDrawable(Bitmap bitmap) {
        return new BitmapDrawable(bitmap);
    }

    public static Bitmap getRoundScaleBitmapByShader(Bitmap bitmap, int outWidth, int outHeight, int radius, int boarder) {
        if (bitmap == null) {
            return null;
        }
        float widthScale = (((float) outWidth) * 1.0f) / ((float) bitmap.getWidth());
        float heightScale = (((float) outHeight) * 1.0f) / ((float) bitmap.getHeight());
        Matrix matrix = new Matrix();
        matrix.setScale(widthScale, heightScale);
        Bitmap desBitmap = Bitmap.createBitmap(outWidth, outHeight, Config.ARGB_8888);
        Canvas canvas = new Canvas(desBitmap);
        Paint paint = new Paint(1);
        BitmapShader bitmapShader = new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);
        bitmapShader.setLocalMatrix(matrix);
        paint.setShader(bitmapShader);
        RectF rect = new RectF((float) boarder, (float) boarder, (float) (outWidth - boarder), (float) (outHeight - boarder));
        canvas.drawRoundRect(rect, (float) radius, (float) radius, paint);
        canvas.drawRoundRect(rect, (float) radius, (float) radius, getRoundPaint(boarder));
        return desBitmap;
    }

    public static Bitmap getRoundCropBitmapByShader(Bitmap bitmap, int outWidth, int outHeight, int radius, int boarder, int TranslateY) {
        float scale;
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float widthScale = (((float) outWidth) * 1.0f) / ((float) width);
        float heightScale = (((float) outHeight) * 1.0f) / ((float) height);
        if (widthScale > heightScale) {
            scale = widthScale;
        } else {
            scale = heightScale;
        }
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        if (((float) height) * scale > ((float) outHeight)) {
            matrix.setTranslate(0.0f, (((float) height) * scale) - ((float) outHeight) > ((float) TranslateY) ? (float) (-TranslateY) : 0.0f);
        }
        Bitmap desBitmap = Bitmap.createBitmap(outWidth, outHeight, Config.ARGB_8888);
        Canvas canvas = new Canvas(desBitmap);
        Paint paint = new Paint(1);
        BitmapShader bitmapShader = new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);
        bitmapShader.setLocalMatrix(matrix);
        paint.setShader(bitmapShader);
        RectF rect = new RectF((float) boarder, (float) boarder, (float) (outWidth - boarder), (float) (outHeight - boarder));
        canvas.drawRoundRect(rect, (float) radius, (float) radius, paint);
        canvas.drawRoundRect(rect, (float) radius, (float) radius, getRoundPaint(boarder));
        return desBitmap;
    }

    private static Paint getRoundPaint(int strokeWidth) {
        Shader shader = new LinearGradient(188.0f, -127.0f, 916.0f, 697.0f, new int[]{COLOR_GRADIENT_START, COLOR_GRADIENT_MIDDLE, COLOR_GRADIENT_END}, new float[]{0.0f, 0.7f, 1.0f}, TileMode.CLAMP);
        Paint boarderPaint = new Paint(1);
        boarderPaint.setStyle(Style.STROKE);
        boarderPaint.setStrokeWidth((float) strokeWidth);
        boarderPaint.setShader(shader);
        return boarderPaint;
    }
}
