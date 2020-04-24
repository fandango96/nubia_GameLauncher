package cn.nubia.gamelauncherx.recycler;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.PathInterpolator;
import cn.nubia.gamelauncherx.recycler.LooperLayoutManager.PostLayoutListener;

public class ZoomPostLayoutListener implements PostLayoutListener {
    private static final int CALIBRATION_VALUE = -6;
    public static final float SCALE_RATE = 1.0f;
    PathInterpolator mZoomInPathInterpolator = new PathInterpolator(1.0f, 0.78f, 1.0f, 1.31f);
    PathInterpolator mZoomOutPathInterpolator = new PathInterpolator(0.0f, 0.23f, 0.03f, -0.13f);

    public ItemTransformation transformChild(@NonNull View child, float itemPositionToCenterDiff, int orientation, boolean isNeedPathInterpolator, boolean scrolledToTheLeft) {
        float posDiffAbs = Math.abs(itemPositionToCenterDiff);
        float calculationItemPositionOffset = calculationItemPositionOffset(itemPositionToCenterDiff, getPathInterpolation(itemPositionToCenterDiff, isNeedPathInterpolator, scrolledToTheLeft));
        return new ItemTransformation(1.0f, 1.0f, getTranslationX(child, itemPositionToCenterDiff), 0.0f, Math.min(1.0f, posDiffAbs), posDiffAbs < 0.5f ? 1.0f : 0.0f, Math.max(0.35f, (float) Math.pow(0.5d, (double) posDiffAbs)), Math.max(0.36f, (float) Math.pow(0.5400000214576721d, (double) posDiffAbs)), Math.max(0.0f, (float) Math.pow(0.10000000149011612d, (double) posDiffAbs)), 1.0f - Math.min(1.0f, posDiffAbs - 2.0f), posDiffAbs == 0.0f ? 1.0f : 0.0f);
    }

    private float getTranslationX(@NonNull View child, float diff) {
        int width = child.getMeasuredWidth();
        float translateX = 0.0f;
        for (float absDiff = Math.abs(diff) - 1.0f; absDiff > 0.0f; absDiff -= 1.0f) {
            translateX = (float) (((double) translateX) + (((double) width) - Anim3DHelper.getProjectionWidthByOffset(width, absDiff)));
        }
        return Math.signum(diff) * translateX * -1.0f;
    }

    private PathInterpolator getPathInterpolation(float positionToCenterDiff, boolean isNeedPathInterpolator, boolean scrolledToTheLeft) {
        if (!isNeedPathInterpolator) {
            return null;
        }
        return ((positionToCenterDiff > 0.0f ? 1 : (positionToCenterDiff == 0.0f ? 0 : -1)) <= 0 && !scrolledToTheLeft) || ((positionToCenterDiff > 0.0f ? 1 : (positionToCenterDiff == 0.0f ? 0 : -1)) >= 0 && scrolledToTheLeft) ? this.mZoomInPathInterpolator : this.mZoomOutPathInterpolator;
    }

    private float calculationItemPositionOffset(float positionToCenterDiff, PathInterpolator interpolator) {
        if (interpolator == null) {
            return positionToCenterDiff;
        }
        float offset = positionToCenterDiff % 1.0f;
        return (positionToCenterDiff - offset) + (Math.signum(positionToCenterDiff) * interpolator.getInterpolation(Math.abs(offset)));
    }
}
