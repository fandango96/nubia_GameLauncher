package jp.wasabeef.glide.transformations;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;

public class RoundedCornersTransformation implements Transformation<Bitmap> {
    private BitmapPool mBitmapPool;
    private CornerType mCornerType;
    private int mDiameter;
    private int mMargin;
    private int mRadius;

    public enum CornerType {
        ALL,
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        TOP,
        BOTTOM,
        LEFT,
        RIGHT,
        OTHER_TOP_LEFT,
        OTHER_TOP_RIGHT,
        OTHER_BOTTOM_LEFT,
        OTHER_BOTTOM_RIGHT,
        DIAGONAL_FROM_TOP_LEFT,
        DIAGONAL_FROM_TOP_RIGHT
    }

    public RoundedCornersTransformation(Context context, int radius, int margin) {
        this(context, radius, margin, CornerType.ALL);
    }

    public RoundedCornersTransformation(BitmapPool pool, int radius, int margin) {
        this(pool, radius, margin, CornerType.ALL);
    }

    public RoundedCornersTransformation(Context context, int radius, int margin, CornerType cornerType) {
        this(Glide.get(context).getBitmapPool(), radius, margin, cornerType);
    }

    public RoundedCornersTransformation(BitmapPool pool, int radius, int margin, CornerType cornerType) {
        this.mBitmapPool = pool;
        this.mRadius = radius;
        this.mDiameter = this.mRadius * 2;
        this.mMargin = margin;
        this.mCornerType = cornerType;
    }

    public Resource<Bitmap> transform(Resource<Bitmap> resource, int outWidth, int outHeight) {
        Bitmap source = (Bitmap) resource.get();
        int width = source.getWidth();
        int height = source.getHeight();
        Bitmap bitmap = this.mBitmapPool.get(width, height, Config.ARGB_8888);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, TileMode.CLAMP, TileMode.CLAMP));
        drawRoundRect(canvas, paint, (float) width, (float) height);
        return BitmapResource.obtain(bitmap, this.mBitmapPool);
    }

    private void drawRoundRect(Canvas canvas, Paint paint, float width, float height) {
        float right = width - ((float) this.mMargin);
        float bottom = height - ((float) this.mMargin);
        switch (this.mCornerType) {
            case ALL:
                canvas.drawRoundRect(new RectF((float) this.mMargin, (float) this.mMargin, right, bottom), (float) this.mRadius, (float) this.mRadius, paint);
                return;
            case TOP_LEFT:
                drawTopLeftRoundRect(canvas, paint, right, bottom);
                return;
            case TOP_RIGHT:
                drawTopRightRoundRect(canvas, paint, right, bottom);
                return;
            case BOTTOM_LEFT:
                drawBottomLeftRoundRect(canvas, paint, right, bottom);
                return;
            case BOTTOM_RIGHT:
                drawBottomRightRoundRect(canvas, paint, right, bottom);
                return;
            case TOP:
                drawTopRoundRect(canvas, paint, right, bottom);
                return;
            case BOTTOM:
                drawBottomRoundRect(canvas, paint, right, bottom);
                return;
            case LEFT:
                drawLeftRoundRect(canvas, paint, right, bottom);
                return;
            case RIGHT:
                drawRightRoundRect(canvas, paint, right, bottom);
                return;
            case OTHER_TOP_LEFT:
                drawOtherTopLeftRoundRect(canvas, paint, right, bottom);
                return;
            case OTHER_TOP_RIGHT:
                drawOtherTopRightRoundRect(canvas, paint, right, bottom);
                return;
            case OTHER_BOTTOM_LEFT:
                drawOtherBottomLeftRoundRect(canvas, paint, right, bottom);
                return;
            case OTHER_BOTTOM_RIGHT:
                drawOtherBottomRightRoundRect(canvas, paint, right, bottom);
                return;
            case DIAGONAL_FROM_TOP_LEFT:
                drawDiagonalFromTopLeftRoundRect(canvas, paint, right, bottom);
                return;
            case DIAGONAL_FROM_TOP_RIGHT:
                drawDiagonalFromTopRightRoundRect(canvas, paint, right, bottom);
                return;
            default:
                canvas.drawRoundRect(new RectF((float) this.mMargin, (float) this.mMargin, right, bottom), (float) this.mRadius, (float) this.mRadius, paint);
                return;
        }
    }

    private void drawTopLeftRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF((float) this.mMargin, (float) this.mMargin, (float) (this.mMargin + this.mDiameter), (float) (this.mMargin + this.mDiameter)), (float) this.mRadius, (float) this.mRadius, paint);
        canvas.drawRect(new RectF((float) this.mMargin, (float) (this.mMargin + this.mRadius), (float) (this.mMargin + this.mRadius), bottom), paint);
        canvas.drawRect(new RectF((float) (this.mMargin + this.mRadius), (float) this.mMargin, right, bottom), paint);
    }

    private void drawTopRightRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF(right - ((float) this.mDiameter), (float) this.mMargin, right, (float) (this.mMargin + this.mDiameter)), (float) this.mRadius, (float) this.mRadius, paint);
        canvas.drawRect(new RectF((float) this.mMargin, (float) this.mMargin, right - ((float) this.mRadius), bottom), paint);
        canvas.drawRect(new RectF(right - ((float) this.mRadius), (float) (this.mMargin + this.mRadius), right, bottom), paint);
    }

    private void drawBottomLeftRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF((float) this.mMargin, bottom - ((float) this.mDiameter), (float) (this.mMargin + this.mDiameter), bottom), (float) this.mRadius, (float) this.mRadius, paint);
        canvas.drawRect(new RectF((float) this.mMargin, (float) this.mMargin, (float) (this.mMargin + this.mDiameter), bottom - ((float) this.mRadius)), paint);
        canvas.drawRect(new RectF((float) (this.mMargin + this.mRadius), (float) this.mMargin, right, bottom), paint);
    }

    private void drawBottomRightRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF(right - ((float) this.mDiameter), bottom - ((float) this.mDiameter), right, bottom), (float) this.mRadius, (float) this.mRadius, paint);
        canvas.drawRect(new RectF((float) this.mMargin, (float) this.mMargin, right - ((float) this.mRadius), bottom), paint);
        canvas.drawRect(new RectF(right - ((float) this.mRadius), (float) this.mMargin, right, bottom - ((float) this.mRadius)), paint);
    }

    private void drawTopRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF((float) this.mMargin, (float) this.mMargin, right, (float) (this.mMargin + this.mDiameter)), (float) this.mRadius, (float) this.mRadius, paint);
        canvas.drawRect(new RectF((float) this.mMargin, (float) (this.mMargin + this.mRadius), right, bottom), paint);
    }

    private void drawBottomRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF((float) this.mMargin, bottom - ((float) this.mDiameter), right, bottom), (float) this.mRadius, (float) this.mRadius, paint);
        canvas.drawRect(new RectF((float) this.mMargin, (float) this.mMargin, right, bottom - ((float) this.mRadius)), paint);
    }

    private void drawLeftRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF((float) this.mMargin, (float) this.mMargin, (float) (this.mMargin + this.mDiameter), bottom), (float) this.mRadius, (float) this.mRadius, paint);
        canvas.drawRect(new RectF((float) (this.mMargin + this.mRadius), (float) this.mMargin, right, bottom), paint);
    }

    private void drawRightRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF(right - ((float) this.mDiameter), (float) this.mMargin, right, bottom), (float) this.mRadius, (float) this.mRadius, paint);
        canvas.drawRect(new RectF((float) this.mMargin, (float) this.mMargin, right - ((float) this.mRadius), bottom), paint);
    }

    private void drawOtherTopLeftRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF((float) this.mMargin, bottom - ((float) this.mDiameter), right, bottom), (float) this.mRadius, (float) this.mRadius, paint);
        canvas.drawRoundRect(new RectF(right - ((float) this.mDiameter), (float) this.mMargin, right, bottom), (float) this.mRadius, (float) this.mRadius, paint);
        canvas.drawRect(new RectF((float) this.mMargin, (float) this.mMargin, right - ((float) this.mRadius), bottom - ((float) this.mRadius)), paint);
    }

    private void drawOtherTopRightRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF((float) this.mMargin, (float) this.mMargin, (float) (this.mMargin + this.mDiameter), bottom), (float) this.mRadius, (float) this.mRadius, paint);
        canvas.drawRoundRect(new RectF((float) this.mMargin, bottom - ((float) this.mDiameter), right, bottom), (float) this.mRadius, (float) this.mRadius, paint);
        canvas.drawRect(new RectF((float) (this.mMargin + this.mRadius), (float) this.mMargin, right, bottom - ((float) this.mRadius)), paint);
    }

    private void drawOtherBottomLeftRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF((float) this.mMargin, (float) this.mMargin, right, (float) (this.mMargin + this.mDiameter)), (float) this.mRadius, (float) this.mRadius, paint);
        canvas.drawRoundRect(new RectF(right - ((float) this.mDiameter), (float) this.mMargin, right, bottom), (float) this.mRadius, (float) this.mRadius, paint);
        canvas.drawRect(new RectF((float) this.mMargin, (float) (this.mMargin + this.mRadius), right - ((float) this.mRadius), bottom), paint);
    }

    private void drawOtherBottomRightRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF((float) this.mMargin, (float) this.mMargin, right, (float) (this.mMargin + this.mDiameter)), (float) this.mRadius, (float) this.mRadius, paint);
        canvas.drawRoundRect(new RectF((float) this.mMargin, (float) this.mMargin, (float) (this.mMargin + this.mDiameter), bottom), (float) this.mRadius, (float) this.mRadius, paint);
        canvas.drawRect(new RectF((float) (this.mMargin + this.mRadius), (float) (this.mMargin + this.mRadius), right, bottom), paint);
    }

    private void drawDiagonalFromTopLeftRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF((float) this.mMargin, (float) this.mMargin, (float) (this.mMargin + this.mDiameter), (float) (this.mMargin + this.mDiameter)), (float) this.mRadius, (float) this.mRadius, paint);
        canvas.drawRoundRect(new RectF(right - ((float) this.mDiameter), bottom - ((float) this.mDiameter), right, bottom), (float) this.mRadius, (float) this.mRadius, paint);
        canvas.drawRect(new RectF((float) this.mMargin, (float) (this.mMargin + this.mRadius), right - ((float) this.mDiameter), bottom), paint);
        canvas.drawRect(new RectF((float) (this.mMargin + this.mDiameter), (float) this.mMargin, right, bottom - ((float) this.mRadius)), paint);
    }

    private void drawDiagonalFromTopRightRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF(right - ((float) this.mDiameter), (float) this.mMargin, right, (float) (this.mMargin + this.mDiameter)), (float) this.mRadius, (float) this.mRadius, paint);
        canvas.drawRoundRect(new RectF((float) this.mMargin, bottom - ((float) this.mDiameter), (float) (this.mMargin + this.mDiameter), bottom), (float) this.mRadius, (float) this.mRadius, paint);
        canvas.drawRect(new RectF((float) this.mMargin, (float) this.mMargin, right - ((float) this.mRadius), bottom - ((float) this.mRadius)), paint);
        canvas.drawRect(new RectF((float) (this.mMargin + this.mRadius), (float) (this.mMargin + this.mRadius), right, bottom), paint);
    }

    public String getId() {
        return "RoundedTransformation(radius=" + this.mRadius + ", margin=" + this.mMargin + ", diameter=" + this.mDiameter + ", cornerType=" + this.mCornerType.name() + ")";
    }
}
