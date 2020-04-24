package cn.nubia.gamelauncherx.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.widget.ImageView;
import cn.nubia.gamelauncherx.R;

@SuppressLint({"AppCompatCustomView"})
public class SimpleEditImageView extends ImageView {
    private static final int EDGE_EXTEND = 240;
    private static final int MODE_DRAG = 1;
    private static final int MODE_NONE = 0;
    private static final int MODE_ZOOM = 2;
    private Bitmap mBitmap;
    private int mBitmapHeightOffSet;
    private float mBitmapStartX;
    private float mBitmapStartY;
    private int mBitmapWidthOffSet;
    private float mClippingBoxBottomEdge;
    private float mClippingBoxLeftEdge;
    private float mClippingBoxRightEdge;
    private float mClippingBoxTopEdge;
    private Context mContext;
    private Matrix mCurrentMatrix;
    private boolean mEnableTouch = true;
    private PointF mMiddleF = new PointF();
    private RectF mRectF;
    private Matrix mSavedMatrix;
    private PointF mStartF = new PointF();
    private int mode = 0;
    private float oldDis = 1.0f;

    public SimpleEditImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.mCurrentMatrix = new Matrix();
        this.mSavedMatrix = new Matrix();
        this.mBitmapHeightOffSet = getResources().getDimensionPixelOffset(R.dimen.bitmap_height_offset);
    }

    public boolean getEnable() {
        return this.mEnableTouch;
    }

    public void setEnable(boolean enable) {
        this.mEnableTouch = enable;
    }

    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = Bitmap.createBitmap(bitmap);
        setImageMatrix(null);
        setImageBitmap(this.mBitmap);
        initBitmapPosition(this.mBitmap);
    }

    private void initBitmapPosition(Bitmap bitmap) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        if (this.mContext instanceof Activity) {
            Display display = ((Activity) this.mContext).getWindowManager().getDefaultDisplay();
            dm = new DisplayMetrics();
            try {
                Class.forName("android.view.Display").getMethod("getRealMetrics", new Class[]{DisplayMetrics.class}).invoke(display, new Object[]{dm});
            } catch (Exception e) {
                dm = getResources().getDisplayMetrics();
                e.printStackTrace();
            }
        }
        this.mBitmapStartX = (float) ((dm.widthPixels - bitmap.getWidth()) / 2);
        this.mBitmapStartY = (float) (((dm.heightPixels - bitmap.getHeight()) / 2) - this.mBitmapHeightOffSet);
        int cropBitmapWidth = getResources().getDimensionPixelOffset(R.dimen.crop_bitmap_width);
        int cropBitmapHeight = getResources().getDimensionPixelOffset(R.dimen.crop_bitmap_height);
        float scale = Math.max(((float) (cropBitmapHeight + EDGE_EXTEND)) / ((float) bitmap.getHeight()), ((float) (cropBitmapWidth + EDGE_EXTEND)) / ((float) bitmap.getWidth()));
        this.mCurrentMatrix = new Matrix();
        this.mCurrentMatrix.setTranslate(this.mBitmapStartX, this.mBitmapStartY);
        this.mCurrentMatrix.postScale(scale, scale, (float) (dm.widthPixels / 2), (float) ((dm.heightPixels / 2) - this.mBitmapHeightOffSet));
        this.mClippingBoxLeftEdge = ((float) (dm.widthPixels - cropBitmapWidth)) / 2.0f;
        this.mClippingBoxRightEdge = this.mClippingBoxLeftEdge + ((float) cropBitmapWidth);
        this.mClippingBoxTopEdge = (((float) (dm.heightPixels - cropBitmapHeight)) / 2.0f) - ((float) this.mBitmapHeightOffSet);
        this.mClippingBoxBottomEdge = this.mClippingBoxTopEdge + ((float) cropBitmapHeight);
        setImageMatrix(this.mCurrentMatrix);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.mEnableTouch) {
            switch (event.getAction() & 255) {
                case 0:
                    this.mSavedMatrix.set(this.mCurrentMatrix);
                    this.mStartF.set(event.getX(), event.getY());
                    this.mode = 1;
                    break;
                case 1:
                    this.mode = 0;
                    break;
                case 2:
                    this.mCurrentMatrix.set(this.mSavedMatrix);
                    PointF matrixBitmap = getMatrixBitmapPointF();
                    int width = (int) matrixBitmap.x;
                    int height = (int) matrixBitmap.y;
                    if (this.mode != 1) {
                        if (this.mode == 2 && event.getPointerCount() == 2) {
                            float scale = getZoomScale(calDistance(event), this.oldDis);
                            this.mCurrentMatrix.postScale(scale, scale, this.mMiddleF.x, this.mMiddleF.y);
                            break;
                        }
                    } else {
                        this.mRectF = getRectF();
                        this.mCurrentMatrix.postTranslate(getDisX(event, width), getDisY(event, height));
                        break;
                    }
                case 5:
                    this.oldDis = calDistance(event);
                    if (this.oldDis > 10.0f) {
                        this.mSavedMatrix.set(this.mCurrentMatrix);
                        this.mMiddleF = calMidPoint(event);
                        this.mode = 2;
                        break;
                    }
                    break;
                case 6:
                    this.mSavedMatrix.set(this.mCurrentMatrix);
                    if (event.getActionIndex() == 0) {
                        this.mStartF.set(event.getX(1), event.getY(1));
                    } else if (event.getActionIndex() == 1) {
                        this.mStartF.set(event.getX(0), event.getY(0));
                    }
                    this.mode = 1;
                    break;
            }
            setImageMatrix(this.mCurrentMatrix);
        }
        return true;
    }

    private float getDisY(MotionEvent event, int height) {
        float dy = event.getY() - this.mStartF.y;
        float top = this.mRectF.top;
        float bottom = this.mRectF.top + ((float) height);
        if (dy > 0.0f && top + dy > this.mClippingBoxTopEdge) {
            return this.mClippingBoxTopEdge - top;
        }
        if (dy >= 0.0f || bottom + dy >= this.mClippingBoxBottomEdge) {
            return dy;
        }
        return this.mClippingBoxBottomEdge - bottom;
    }

    private float getDisX(MotionEvent event, int width) {
        float dx = event.getX() - this.mStartF.x;
        float left = this.mRectF.left;
        float right = this.mRectF.left + ((float) width);
        if (dx > 0.0f && left + dx > this.mClippingBoxLeftEdge) {
            return this.mClippingBoxLeftEdge - left;
        }
        if (dx >= 0.0f || right + dx >= this.mClippingBoxRightEdge) {
            return dx;
        }
        return this.mClippingBoxRightEdge - right;
    }

    private float getZoomScale(float newDis, float oldDis2) {
        this.mRectF = getRectF();
        PointF matrixBitmap = getMatrixBitmapPointF();
        float scale = newDis / oldDis2;
        float disHeightMin = Math.max(this.mMiddleF.y - this.mClippingBoxTopEdge, this.mClippingBoxBottomEdge - this.mMiddleF.y);
        float mBitmapHeight = matrixBitmap.y / 2.0f;
        if (scale < disHeightMin / mBitmapHeight) {
            scale = disHeightMin / mBitmapHeight;
        }
        float disWidthMin = Math.max(this.mMiddleF.x - this.mClippingBoxLeftEdge, this.mClippingBoxRightEdge - this.mMiddleF.x);
        float mBitmapWidth = matrixBitmap.x / 2.0f;
        if (scale < disWidthMin / mBitmapWidth) {
            scale = disWidthMin / mBitmapWidth;
        }
        if (matrixBitmap.x <= ((float) getResources().getDisplayMetrics().widthPixels) || scale <= 1.0f) {
            return scale;
        }
        return 1.0f;
    }

    public Bitmap getMatrixBitmap() {
        return Bitmap.createBitmap(this.mBitmap, 0, 0, this.mBitmap.getWidth(), this.mBitmap.getHeight(), this.mCurrentMatrix, true);
    }

    public Matrix getCurrentMatrix() {
        return this.mCurrentMatrix;
    }

    public PointF getMatrixBitmapPointF() {
        float[] dst = new float[8];
        this.mCurrentMatrix.mapPoints(dst, new float[]{0.0f, 0.0f, (float) this.mBitmap.getWidth(), 0.0f, 0.0f, (float) this.mBitmap.getHeight(), (float) this.mBitmap.getWidth(), (float) this.mBitmap.getHeight()});
        Path path = new Path();
        path.moveTo(dst[0], dst[1]);
        path.lineTo(dst[2], dst[3]);
        path.lineTo(dst[6], dst[7]);
        path.lineTo(dst[4], dst[5]);
        path.close();
        RectF r = new RectF();
        path.computeBounds(r, true);
        return new PointF(r.width(), r.height());
    }

    private float calDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt((double) ((x * x) + (y * y)));
    }

    private PointF calMidPoint(MotionEvent event) {
        this.mRectF = getRectF();
        PointF matrixBitmap = getMatrixBitmapPointF();
        return new PointF(this.mRectF.left + (matrixBitmap.x / 2.0f), this.mRectF.top + (matrixBitmap.y / 2.0f));
    }

    private RectF getRectF() {
        RectF rectF = new RectF();
        this.mCurrentMatrix.mapRect(rectF);
        return rectF;
    }

    private float calRotation(MotionEvent event) {
        return (float) Math.toDegrees(Math.atan2((double) (event.getY(0) - event.getY(1)), (double) (event.getX(0) - event.getX(1))));
    }
}
