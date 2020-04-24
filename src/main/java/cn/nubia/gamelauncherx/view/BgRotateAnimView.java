package cn.nubia.gamelauncherx.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.support.graphics.drawable.PathInterpolatorCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.animation.PathInterpolator;
import cn.nubia.gamelauncherx.R;
import cn.nubia.gamelauncherx.bean.CircleBean;
import cn.nubia.gamelauncherx.recycler.Anim3DHelper;
import cn.nubia.gamelauncherx.recycler.BannerManager;
import cn.nubia.gamelauncherx.util.LogUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class BgRotateAnimView extends SurfaceView implements Callback, BannerManager.Callback {
    public static final String TAG = "BgRotate";
    private final int ANIM_DURATION;
    private final float ARC_ROTATE_VELOCITY;
    private final float CIRCLE_ROTATE_VELOCITY;
    private final float DIAMETER_CIRCLE_1;
    private final float DIAMETER_CIRCLE_2;
    private final float DIAMETER_CIRCLE_3;
    private final float DIAMETER_CIRCLE_4;
    private final float DIAMETER_CIRCLE_5;
    private final float DIAMETER_CIRCLE_6;
    private final float DIAMETER_PENDULUM;
    private final int FRAM_DURATION;
    private final float LIFT_DISTANCE;
    private final float MAX_ARC_ROTATE_VELOCITY;
    private final float MIN_ARC_ROTATE_VELOCITY;
    final int MIN_TIME_CONSUM_LIFT;
    final int MIN_TIME_INTERVAL_BETWEEN_LIFT;
    final int RANDOM_TIME_CONSUM_RANGES;
    final int RANDOM_TIME_INTERVAL_RANGES;
    private final float ROTATE_X;
    private final float SCALE_Z;
    private final float SMALL_CIRCLE_TRANSLATE_Y;
    final int START_ANIM_RISE_DURATION;
    private final int STROKE_ARC;
    private final int STROKE_CIRCLE;
    private final int STROKE_DEFAULT;
    private final int STROKE_PENDULUM;
    private final float TRANSLATE_Y;
    /* access modifiers changed from: private */
    public int mAlpha;
    private Bitmap mBg;
    private Camera mCamera;
    /* access modifiers changed from: private */
    public Canvas mCanvas;
    float mDensity;
    /* access modifiers changed from: private */
    public SurfaceHolder mHolder;
    private boolean mIsPaused;
    private boolean mIsStart;
    private LiftHelper mLiftHelper;
    private ArrayList<CircleBean> mList;
    private Matrix mMatrix;
    private Paint mPaint;
    PathInterpolator mPathInterpolator;
    /* access modifiers changed from: private */
    public int mRotateEvenlyDirection;
    private float mRotateX;
    private boolean mRotating;
    private float mScaleZ;
    /* access modifiers changed from: private */
    public boolean mScrollDirectionChanged;
    private final OnScrollListener mScrollListener;
    private long mStartTime;

    private class DrawRunnable implements Runnable {
        private DrawRunnable() {
        }

        public void run() {
            long startTime = System.currentTimeMillis();
            try {
                BgRotateAnimView.this.mCanvas = BgRotateAnimView.this.getHolder().lockCanvas();
                if (BgRotateAnimView.this.mCanvas != null) {
                    BgRotateAnimView.this.mCanvas.drawColor(0, Mode.CLEAR);
                    BgRotateAnimView.this.doFrame(BgRotateAnimView.this.mCanvas);
                }
                try {
                    if (BgRotateAnimView.this.mCanvas != null) {
                        BgRotateAnimView.this.mHolder.unlockCanvasAndPost(BgRotateAnimView.this.mCanvas);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
                try {
                    if (BgRotateAnimView.this.mCanvas != null) {
                        BgRotateAnimView.this.mHolder.unlockCanvasAndPost(BgRotateAnimView.this.mCanvas);
                    }
                } catch (Exception e3) {
                    e3.printStackTrace();
                }
            } catch (Throwable th) {
                try {
                    if (BgRotateAnimView.this.mCanvas != null) {
                        BgRotateAnimView.this.mHolder.unlockCanvasAndPost(BgRotateAnimView.this.mCanvas);
                    }
                } catch (Exception e4) {
                    e4.printStackTrace();
                }
                throw th;
            }
            postDelayedNextFrame(startTime);
        }

        private void postDelayedNextFrame(long startTime) {
            long elapsed = System.currentTimeMillis() - startTime;
            if (elapsed >= 16) {
                BannerManager.getInstance().postBgRotateRunnableDelayed(this, 0);
            } else {
                BannerManager.getInstance().postBgRotateRunnableDelayed(this, 16 - elapsed);
            }
        }
    }

    class LiftHelper {
        final int LIFT_IDLE = 0;
        final int LIFT_LAND = 2;
        final int LIFT_RISE = 1;
        Runnable landRunnable = new Runnable() {
            public void run() {
                if (LiftHelper.this.getState() == 1) {
                    LiftHelper.this.setState(2);
                    LiftHelper.this.mRiseDistance = 42.0f;
                    LiftHelper.this.mLiftStartTime = System.currentTimeMillis();
                    LiftHelper.this.mLiftDuration *= 2;
                }
            }
        };
        long mLiftDuration;
        long mLiftStartTime;
        int mLiftState = 0;
        Random mRandom = new Random();
        float mRiseDistance = 42.0f;
        Runnable riseRunnable = new Runnable() {
            public void run() {
                if (LiftHelper.this.getState() == 0) {
                    LiftHelper.this.setState(1);
                    LiftHelper.this.mRiseDistance = 0.0f;
                    LiftHelper.this.mLiftStartTime = System.currentTimeMillis();
                    LiftHelper.this.mLiftDuration = (long) (LiftHelper.this.mRandom.nextInt(PathInterpolatorCompat.MAX_NUM_POINTS) + 800);
                }
            }
        };

        LiftHelper() {
        }

        public void setState(int state) {
            this.mLiftState = state;
        }

        public int getState() {
            return this.mLiftState;
        }

        private int getLiftElapsedTime() {
            return (int) (System.currentTimeMillis() - this.mLiftStartTime);
        }

        public void updateRiseLiftDistance() {
            if (getState() != 0) {
                float rate = Anim3DHelper.getValueOfInterpolator(null, (long) getLiftElapsedTime(), this.mLiftDuration);
                if (getState() == 2) {
                    rate = 1.0f - rate;
                }
                this.mRiseDistance = 42.0f * rate;
                startLandDelay(50);
                if (0.0f == this.mRiseDistance) {
                    startLift();
                }
            }
        }

        public void startLift() {
            BannerManager.getInstance().postBgRotateRunnableDelayed(this.riseRunnable, (long) (this.mRandom.nextInt(12000) + 200));
            setState(0);
        }

        public void resetRiseDistance() {
            this.mRiseDistance = 0.0f;
        }

        public void startLandDelay(long delay) {
            if (this.mRiseDistance >= 42.0f) {
                BannerManager.getInstance().postBgRotateRunnableDelayed(this.landRunnable, delay);
            }
        }

        public float getRiseLiftDistance() {
            return this.mRiseDistance;
        }

        public void setLiftDuration(int duration) {
            this.mLiftDuration = (long) duration;
        }
    }

    public BgRotateAnimView(Context context) {
        this(context, null);
    }

    public BgRotateAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mAlpha = 0;
        this.ROTATE_X = 83.84f;
        this.TRANSLATE_Y = 295.0f;
        this.SMALL_CIRCLE_TRANSLATE_Y = 315.0f;
        this.SCALE_Z = 25.0f;
        this.mRotateX = 83.84f;
        this.mScaleZ = 25.0f;
        this.mList = new ArrayList<>();
        this.mIsStart = false;
        this.mRotating = false;
        this.mIsPaused = false;
        this.mDensity = 3.0f;
        this.DIAMETER_CIRCLE_1 = 1457.0f;
        this.DIAMETER_CIRCLE_2 = 1425.0f;
        this.DIAMETER_CIRCLE_3 = 553.0f;
        this.DIAMETER_CIRCLE_4 = 308.0f;
        this.DIAMETER_CIRCLE_5 = 195.0f;
        this.DIAMETER_PENDULUM = 215.0f;
        this.DIAMETER_CIRCLE_6 = 1000.0f;
        this.STROKE_CIRCLE = 8;
        this.STROKE_ARC = 26;
        this.STROKE_DEFAULT = 9;
        this.STROKE_PENDULUM = 42;
        this.ANIM_DURATION = Anim3DHelper.START_ANIM_DURATION;
        this.FRAM_DURATION = 16;
        this.MIN_ARC_ROTATE_VELOCITY = 2.0f;
        this.MAX_ARC_ROTATE_VELOCITY = 8.0f;
        this.ARC_ROTATE_VELOCITY = -2.0f;
        this.CIRCLE_ROTATE_VELOCITY = 1.8f;
        this.START_ANIM_RISE_DURATION = 1000;
        this.MIN_TIME_INTERVAL_BETWEEN_LIFT = 200;
        this.RANDOM_TIME_INTERVAL_RANGES = 12000;
        this.MIN_TIME_CONSUM_LIFT = 800;
        this.RANDOM_TIME_CONSUM_RANGES = PathInterpolatorCompat.MAX_NUM_POINTS;
        this.LIFT_DISTANCE = 42.0f;
        this.mRotateEvenlyDirection = -1;
        this.mScrollDirectionChanged = false;
        this.mPathInterpolator = Anim3DHelper.DEFAULT_ANIM_PATH_INTERPOLATOR;
        this.mScrollListener = new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case 0:
                        BgRotateAnimView.this.mScrollDirectionChanged = false;
                        return;
                    default:
                        return;
                }
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (BgRotateAnimView.this.mScrollDirectionChanged) {
                    BgRotateAnimView.this.mRotateEvenlyDirection = -((int) Math.signum((float) dx));
                    BgRotateAnimView.this.updateArcVelocity((float) ((-dx) / 40));
                } else if (!BgRotateAnimView.this.mScrollDirectionChanged) {
                    BgRotateAnimView.this.updateArcVelocity();
                }
            }
        };
        init();
        this.mDensity = context.getResources().getDisplayMetrics().density;
    }

    private void init() {
        this.mHolder = getHolder();
        this.mHolder.addCallback(this);
        this.mHolder.setFormat(-2);
        setZOrderOnTop(false);
        this.mMatrix = new Matrix();
        this.mCamera = new Camera();
        this.mPaint = new Paint();
        this.mLiftHelper = new LiftHelper();
        this.mBg = BitmapFactory.decodeResource(getResources(), R.mipmap.bg);
        initCircleList();
    }

    private void initCircleList() {
        int colorDefault = getResources().getColor(R.color.color_halo_default, null);
        int colorAlpha = getResources().getColor(R.color.color_halo_alpha, null);
        this.mList.clear();
        this.mList.add(new CircleBean(728.5f, 0.0f, 1000, 0, 30.0f, this.mPathInterpolator, null, colorDefault, 8, 295.0f, 0.0f, false, 0, null, false));
        this.mList.add(new CircleBean(712.5f, -2.0f, 1000, 0, 30.0f, this.mPathInterpolator, new float[]{260.0f, 5700.0f}, colorAlpha, 34, 295.0f, 0.0f, false, 0, null, false));
        this.mList.add(new CircleBean(500.0f, 0.0f, 1000, 0, 30.0f, this.mPathInterpolator, new float[]{800.0f, 4000.0f}, colorAlpha, 26, 315.0f, 100.0f, true, 2400, null, false));
        this.mList.add(new CircleBean(500.0f, 2.0f, 1000, 0, 30.0f, this.mPathInterpolator, new float[]{200.0f, 4000.0f}, colorAlpha, 26, 315.0f, 200.0f, false, 0, null, false));
        this.mList.add(new CircleBean(276.5f, 0.0f, 1000, 150, 0.0f, this.mPathInterpolator, null, colorDefault, 9, 315.0f, 0.0f, false, 0, null, false));
        float[] arrayCircle = {20.0f, 40.0f};
        this.mList.add(new CircleBean(154.0f, 1.8f, 1000, ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION, 0.0f, this.mPathInterpolator, arrayCircle, colorDefault, 9, 315.0f, 0.0f, false, 0, null, false));
        this.mList.add(new CircleBean(97.5f, 0.0f, 1000, 350, 42.0f, this.mPathInterpolator, null, -1, 11, 315.0f, 0.0f, false, 0, null, true));
        this.mList.add(new CircleBean(107.5f, 0.0f, 1000, 350, 42.0f, this.mPathInterpolator, new float[]{15.0f, 4000.0f}, -1, 42, 315.0f, 0.0f, true, 1400, null, true));
        this.mList.add(new CircleBean(107.5f, 0.0f, 1000, 350, 42.0f, this.mPathInterpolator, new float[]{15.0f, 4000.0f}, -1, 42, 315.0f, 0.0f, true, 5400, null, true));
    }

    public void doAlphaAnimator() {
        ValueAnimator animator = ValueAnimator.ofInt(new int[]{0, 255});
        animator.setDuration(600);
        animator.setInterpolator(Anim3DHelper.DEFAULT_ANIM_PATH_INTERPOLATOR);
        animator.start();
        animator.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                BgRotateAnimView.this.mAlpha = ((Integer) valueAnimator.getAnimatedValue()).intValue();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                onStartAnimEnd();
            }

            private void onStartAnimEnd() {
                BgRotateAnimView.this.mAlpha = 255;
            }

            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                onStartAnimEnd();
            }
        });
    }

    public void surfaceCreated(SurfaceHolder holder) {
        LogUtil.d(TAG, "surfaceCreated()");
        if (this.mIsPaused) {
            startDrawThread();
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        LogUtil.d(TAG, "surfaceDestroyed()");
        finish();
    }

    public void startDrawThread() {
        BannerManager.getInstance().postBgRotateRunnableDelayed(new DrawRunnable(), 0);
        if (!this.mIsPaused) {
            doFirstStartThing();
        } else {
            this.mLiftHelper.startLift();
        }
    }

    private void doFirstStartThing() {
        setVisibility(0);
        doAlphaAnimator();
        startAnim();
        BannerManager.getInstance().postBgRotateRunnableDelayed(new Runnable() {
            public void run() {
                BgRotateAnimView.this.onStartAnimationEnd();
            }
        }, 1350);
    }

    /* access modifiers changed from: private */
    public void doFrame(Canvas canvas) {
        drawBg(canvas);
        drawCircle(canvas);
    }

    public void startAnim() {
        this.mIsStart = true;
        this.mStartTime = System.currentTimeMillis();
        LiftHelper liftHelper = this.mLiftHelper;
        this.mLiftHelper.getClass();
        liftHelper.setState(1);
    }

    public void onStartAnimationEnd() {
        this.mRotating = true;
        this.mBg = BitmapFactory.decodeResource(getResources(), R.mipmap.bg_rotate);
        this.mLiftHelper.setLiftDuration(1000);
        this.mLiftHelper.startLandDelay(0);
    }

    public void onFrameAnimationEnd() {
        if (!this.mRotating) {
            this.mRotating = true;
            this.mBg = BitmapFactory.decodeResource(getResources(), R.mipmap.bg_rotate);
        }
        this.mIsStart = false;
        this.mIsPaused = true;
        this.mLiftHelper.resetRiseDistance();
    }

    public int getElapsedTime() {
        return (int) (System.currentTimeMillis() - this.mStartTime);
    }

    public void finish() {
        BannerManager.getInstance().clearBgRotateHandler();
        onFrameAnimationEnd();
    }

    public boolean isStart() {
        return this.mIsStart;
    }

    public boolean isFinish() {
        return !this.mRotating && !this.mIsStart;
    }

    private void drawBg(Canvas canvas) {
        canvas.drawBitmap(this.mBg, this.mMatrix, this.mPaint);
    }

    private void drawCircle(Canvas canvas) {
        if (this.mRotating) {
            this.mLiftHelper.updateRiseLiftDistance();
        }
        Iterator it = this.mList.iterator();
        while (it.hasNext()) {
            drawCircle((CircleBean) it.next(), canvas);
        }
    }

    private void drawCircle(CircleBean bean, Canvas canvas) {
        if (bean != null) {
            float radius = bean.getRadius();
            float translate = this.mRotating ? bean.getTranslate() : bean.getTranslateByDuration(getElapsedTime());
            if (this.mRotating && bean.isLiftCircle()) {
                translate -= this.mLiftHelper.getRiseLiftDistance();
            }
            if (radius <= 0.0f) {
                return;
            }
            if (!this.mRotating || 2.0f * radius != 1457.0f) {
                Matrix matrix = new Matrix();
                this.mCamera.save();
                canvas.save();
                this.mCamera.translate(0.0f, 0.0f, this.mScaleZ);
                this.mCamera.rotateX(this.mRotateX);
                float r = bean.getRotationAngle();
                if (bean.isRotateCircle()) {
                    r = (bean.getRotateVelocity() + r) % 360.0f;
                    bean.setRotationAngle(r);
                }
                this.mCamera.rotateZ(r);
                this.mCamera.getMatrix(matrix);
                this.mCamera.restore();
                float[] values = new float[9];
                matrix.getValues(values);
                values[6] = values[6] / this.mDensity;
                values[7] = values[7] / this.mDensity;
                matrix.setValues(values);
                matrix.preTranslate((float) ((-canvas.getWidth()) / 2), ((float) ((-canvas.getHeight()) / 2)) - translate);
                matrix.postTranslate((float) (canvas.getWidth() / 2), ((float) (canvas.getHeight() / 2)) + translate);
                canvas.concat(matrix);
                bean.getPaint().setAlpha(this.mAlpha);
                canvas.drawCircle((float) (canvas.getWidth() / 2), ((float) (canvas.getHeight() / 2)) + translate, radius, bean.getPaint());
                canvas.restore();
            }
        }
    }

    public void updateArcVelocity(float velocity) {
        if (this.mList != null && this.mList.size() > 0 && 0.0f != Math.signum(velocity)) {
            float velocity2 = Math.signum(velocity) * Math.max(Math.abs(velocity), 2.0f);
            ((CircleBean) this.mList.get(1)).setRotateVelocity(Math.signum(velocity2) * Math.min(Math.abs(velocity2), 8.0f));
        }
    }

    public void updateArcVelocity() {
        if (this.mList != null && this.mList.size() > 0) {
            ((CircleBean) this.mList.get(1)).setRotateVelocity(Math.abs(-2.0f) * ((float) this.mRotateEvenlyDirection));
        }
    }

    public void scrollDirectionChanged() {
        this.mScrollDirectionChanged = true;
    }

    public void listenerRecycleScrollState() {
        BannerManager.getInstance().addCallback(this);
        BannerManager.getInstance().addBannerScrollListener(this.mScrollListener);
    }
}
