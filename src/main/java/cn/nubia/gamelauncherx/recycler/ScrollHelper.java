package cn.nubia.gamelauncherx.recycler;

import android.content.Context;
import android.view.ViewConfiguration;

public class ScrollHelper {
    private static float DECELERATION_RATE = ((float) (Math.log(0.78d) / Math.log(0.9d)));
    private static final float INFLEXION = 0.35f;
    private final String TAG = "scroll";
    Context mContext;
    private float mFlingFriction = ViewConfiguration.getScrollFriction();
    private float mPhysicalCoeff = -1.0f;

    public ScrollHelper(Context context) {
        this.mContext = context;
        initPhysicalCoeffIfNeed();
    }

    private void initPhysicalCoeffIfNeed() {
        if (0.0f > this.mPhysicalCoeff) {
            this.mPhysicalCoeff = 386.0878f * this.mContext.getResources().getDisplayMetrics().density * 160.0f * 0.84f;
        }
    }

    private double getSplineDeceleration(int velocity) {
        return Math.log((double) ((INFLEXION * ((float) Math.abs(velocity))) / (this.mFlingFriction * this.mPhysicalCoeff)));
    }

    public double getSplineFlingDistance(int velocity) {
        return ((double) (this.mFlingFriction * this.mPhysicalCoeff)) * Math.exp((((double) DECELERATION_RATE) / (((double) DECELERATION_RATE) - 1.0d)) * getSplineDeceleration(velocity));
    }

    public int getSplineFlingDuration(int velocity) {
        return (int) (1000.0d * Math.exp(getSplineDeceleration(velocity) / (((double) DECELERATION_RATE) - 1.0d)));
    }

    public int getVelocityByDistance(double distance) {
        return (int) (Math.ceil(Math.abs((Math.exp(Math.log(Math.abs(distance) / ((double) (this.mFlingFriction * this.mPhysicalCoeff))) / (((double) DECELERATION_RATE) / (((double) DECELERATION_RATE) - 1.0d))) * ((double) (this.mFlingFriction * this.mPhysicalCoeff))) / 0.3499999940395355d)) * Math.signum(distance));
    }
}
