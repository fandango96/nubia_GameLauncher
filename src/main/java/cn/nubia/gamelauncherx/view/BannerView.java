package cn.nubia.gamelauncherx.view;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PaintFlagsDrawFilter;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import cn.nubia.gamelauncherx.recycler.Anim3DHelper;

public class BannerView extends ConstraintLayout {
    private Camera mCamera;
    private Matrix mMatrix;
    private float mOffset;
    private float mRotateDeg;
    private float mScale;

    public BannerView(Context context) {
        this(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mMatrix = new Matrix();
        this.mCamera = new Camera();
        Anim3DHelper.setDensity(context.getResources().getDisplayMetrics().density);
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        for (int i = 0; i < getChildCount(); i++) {
            drawItem(canvas, i, getDrawingTime());
        }
    }

    private void drawItem(Canvas canvas, int screen, long drawingTime) {
        int width = this.mOffset > 0.0f ? 0 : getWidth();
        int height = getHeight() / 2;
        View child = getChildAt(screen);
        if (child != null && child.getVisibility() == 0) {
            Camera camera = this.mCamera;
            Matrix matrix = this.mMatrix;
            canvas.save();
            camera.save();
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, 3));
            camera.rotateY(this.mRotateDeg);
            camera.getMatrix(matrix);
            camera.restore();
            matrix.postScale(this.mScale, this.mScale);
            float[] values = new float[9];
            matrix.getValues(values);
            values[6] = values[6] / Anim3DHelper.getDensity();
            values[7] = values[7] / Anim3DHelper.getDensity();
            matrix.setValues(values);
            matrix.preTranslate((float) (-width), (float) (-height));
            matrix.postTranslate((float) width, (float) height);
            canvas.concat(matrix);
            drawChild(canvas, child, drawingTime);
            canvas.restore();
        }
    }

    public void updateRotate(float offset) {
        this.mRotateDeg = Anim3DHelper.getDegreesByOffset(offset);
        this.mOffset = offset;
        this.mScale = Anim3DHelper.getScaleRateByOffset(offset);
        invalidate();
    }
}
