package cn.nubia.gamelauncherx.recycler;

import android.view.animation.PathInterpolator;

public class Anim3DHelper {
    public static float CAMERA_LOCATION_TO_PIXEL = 72.0f;
    public static float CAMERA_LOCATION_Z = -8.0f;
    public static final int CARD_ROUND_RECT_RADIUS = 34;
    public static PathInterpolator DEFAULT_ANIM_PATH_INTERPOLATOR = new PathInterpolator(0.3f, 0.0f, 0.3f, 1.0f);
    public static final int MAX_DEGREES = 90;
    public static final int MAX_SCROLL_COUNT = 18;
    public static final float OFFSET_SCALE_RATE = 0.89f;
    public static PathInterpolator PATH_INTERPOLATOR_CARD_ENTER = new PathInterpolator(0.3f, 0.1f, 0.3f, 1.0f);
    public static PathInterpolator PATH_INTERPOLATOR_CARD_REBOUND = new PathInterpolator(0.3f, 0.13f, 0.58f, 1.0f);
    public static final float RECYCLE_FLING_SPEED_RATIO = 0.7f;
    public static final int ROTATE_DEGREES_OFFSET_ONE = 33;
    public static final float SCALE_MUTATION_RATE = 2.5f;
    public static final int START_ANIM_DURATION = 1350;
    private static final int TOTAL_ROTATE_DEGREES = 360;
    public static float mDensity = 3.0f;

    public static double getAngleWithScreenByOffset(float offset) {
        float angle = getDegreesByOffset(offset);
        if (angle > 90.0f) {
            angle = 90.0f - (angle % 90.0f);
        }
        return (((double) angle) * 3.141592653589793d) / 180.0d;
    }

    public static float getDegreesByOffset(float offset) {
        return ((33.0f * offset) + 360.0f) % 360.0f;
    }

    public static float transformOffsetWithMutation(float offset, float mutation) {
        return Math.abs(offset) > 1.0f ? Math.signum(offset) * (Math.abs(offset) + ((Math.abs(offset) - 1.0f) * mutation)) : offset;
    }

    public static float getCameraLocationZByPixel() {
        return Math.abs(CAMERA_LOCATION_Z * CAMERA_LOCATION_TO_PIXEL * mDensity);
    }

    public static void setDensity(float density) {
        mDensity = density;
    }

    public static float getDensity() {
        return mDensity;
    }

    public static float getScaleRateByOffset(float offset) {
        return (float) Math.pow(0.8899999856948853d, (double) Math.abs(transformOffsetWithMutation(offset, 2.5f)));
    }

    public static double getProjectionWidthByOffset(int width, float offset) {
        float scale = getScaleRateByOffset(offset);
        double angle = getAngleWithScreenByOffset(offset);
        int width2 = (int) (((float) width) * scale);
        return (((double) (getCameraLocationZByPixel() * ((float) width2))) * Math.cos(angle)) / (((double) getCameraLocationZByPixel()) + (((double) width2) * Math.sin(angle)));
    }

    public static float getValueOfInterpolator(PathInterpolator interpolator, long elapsedTime, long duration) {
        if (0 == duration) {
            return 0.0f;
        }
        if (elapsedTime > duration) {
            return 1.0f;
        }
        if (interpolator == null) {
            interpolator = DEFAULT_ANIM_PATH_INTERPOLATOR;
        }
        return interpolator.getInterpolation(((float) elapsedTime) / ((float) duration));
    }
}
