package cn.nubia.gamelauncher.bean;

import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.view.animation.PathInterpolator;
import cn.nubia.gamelauncher.recycler.Anim3DHelper;
import java.util.Random;

public class CircleBean {
    final int ANGLE_180 = 180;
    final int ANGLE_270 = 270;
    final int ANGLE_360 = 360;
    final int ANGLE_90 = 90;
    float mAngle;
    private float mDistance = 0.0f;
    float mEndDegree;
    int mEnterAnimDelayedTime = 0;
    int mEnterAnimDuration = 0;
    boolean mFlag = false;
    boolean mIsLift;
    boolean mIsPendulum;
    Paint mPaint;
    PathInterpolator mPathInterpolator;
    int mPendulumDuration;
    PathInterpolator mPendulumInterpolator;
    long mPendulumStartTime;
    float mRadius = 0.0f;
    Random mRandom = new Random();
    float mRotateVelocity = 0.0f;
    private float mRotationAngle = 0.0f;
    float mStartDegree;
    private float mTranslate = 0.0f;

    public CircleBean(float radius, float rotateVelocity, int enterAnimDuration, int enterAnimStartTime, float distance, PathInterpolator interpolator, float[] intervals, int color, int stroke, float translate, float angle, boolean pendulum, int pendulumDuration, PathInterpolator pendulumInterpolator, boolean lift) {
        this.mRotateVelocity = rotateVelocity;
        this.mRadius = radius;
        this.mEnterAnimDuration = enterAnimDuration;
        this.mEnterAnimDelayedTime = enterAnimStartTime;
        this.mPathInterpolator = interpolator;
        this.mPaint = new Paint();
        this.mTranslate = translate;
        this.mRotationAngle = angle;
        this.mDistance = distance;
        this.mIsPendulum = pendulum;
        this.mPendulumDuration = pendulumDuration;
        this.mPendulumInterpolator = pendulumInterpolator;
        if (this.mIsPendulum) {
            this.mEndDegree = this.mRotationAngle;
            makeNextPendulumData();
        }
        if (pendulum && intervals != null) {
            this.mFlag = true;
        }
        this.mIsLift = lift;
        initPaint(color, stroke, intervals);
    }

    private void initPaint(int color, int stroke, float[] intervals) {
        this.mPaint.setColor(color);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setStrokeWidth((float) stroke);
        this.mPaint.setStyle(Style.STROKE);
        if (intervals != null && intervals.length >= 2) {
            this.mPaint.setPathEffect(new DashPathEffect(intervals, 5.0f));
            if (intervals[1] < this.mRadius) {
                this.mPaint.setStrokeCap(Cap.ROUND);
            }
        }
        this.mPaint.setMaskFilter(new BlurMaskFilter(12.0f, Blur.SOLID));
    }

    public void setRotateVelocity(float rotateVelocity) {
        this.mRotateVelocity = rotateVelocity;
    }

    public float getRotateVelocity() {
        return this.mRotateVelocity;
    }

    public boolean isRotateCircle() {
        return 0.0f != this.mRotateVelocity;
    }

    public boolean isLiftCircle() {
        return this.mIsLift;
    }

    public Paint getPaint() {
        return this.mPaint;
    }

    public float getRadius() {
        return this.mRadius;
    }

    public void setRotationAngle(float angle) {
        this.mRotationAngle = angle;
    }

    public float getRotationAngle() {
        if (!this.mIsPendulum) {
            return this.mRotationAngle;
        }
        int duration = getPendulumElapsedTime();
        if (duration < this.mPendulumDuration) {
            return this.mStartDegree + (this.mAngle * Anim3DHelper.getValueOfInterpolator(this.mPendulumInterpolator, (long) duration, (long) this.mPendulumDuration));
        }
        this.mRotationAngle = this.mEndDegree;
        makeNextPendulumData();
        return this.mRotationAngle;
    }

    private void makeNextPendulumData() {
        this.mStartDegree = this.mEndDegree;
        this.mEndDegree = (float) ((this.mStartDegree > 270.0f ? 90 : 360) + this.mRandom.nextInt(180));
        this.mAngle = this.mEndDegree - this.mStartDegree;
        this.mPendulumStartTime = System.currentTimeMillis();
    }

    private int getPendulumElapsedTime() {
        return (int) (System.currentTimeMillis() - this.mPendulumStartTime);
    }

    public float getTranslate() {
        if (this.mIsLift) {
            return this.mTranslate;
        }
        return this.mTranslate - this.mDistance;
    }

    public float getTranslateByDuration(int duration) {
        if (duration <= this.mEnterAnimDelayedTime) {
            return this.mTranslate;
        }
        return this.mTranslate - (this.mDistance * Anim3DHelper.getValueOfInterpolator(this.mPathInterpolator, (long) (duration - this.mEnterAnimDelayedTime), (long) this.mEnterAnimDuration));
    }
}
