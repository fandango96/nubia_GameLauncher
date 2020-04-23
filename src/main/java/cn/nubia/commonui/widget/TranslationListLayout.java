package cn.nubia.commonui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import cn.nubia.commonui.R;

public class TranslationListLayout extends FrameLayout {
    private static final int BOTTOM = 1;
    private static final float DEFAULT_MARGIN_TOP = 65.33f;
    private static final float DEFAULT_PAUSED_TRANSLATION = 150.0f;
    private static final int DEFAULT_TEXT_COLOR = 1275068416;
    private static final float DEFAULT_TEXT_SIZE = 12.0f;
    private static final int INVALID_POINTER = -1;
    /* access modifiers changed from: private */
    public static boolean ON_BOTTOM = false;
    private static final int STATE_DRAG = 2;
    private static final int STATE_FLING = 4;
    private static final int STATE_IDLE = 1;
    private static final String TAG = "NubiaWidget";
    private static final int TOP = 2;
    private static final float TOUCH_SLOP_RATIO = 0.3f;
    private boolean OPTS_INPUT;
    private int mActivePointerId;
    private boolean mCanDraggedDown;
    private View mCaptureView;
    private TextView mHeaderTextView;
    private boolean mIsBeingDragged;
    private float mLastMotionY;
    private float mMaximumVelocity;
    private float mMinimumVelocity;
    /* access modifiers changed from: private */
    public int mPositionState;
    /* access modifiers changed from: private */
    public int mState;
    private float mTextTranslation;
    /* access modifiers changed from: private */
    public float mThresholdTranslation;
    private int mTouchMoveNumber;
    private int mTouchSlop;
    /* access modifiers changed from: private */
    public float mTranslation;
    private VelocityTracker mVelocityTracker;
    private boolean mViewCanScrolled;

    public TranslationListLayout(Context context) {
        this(context, null);
    }

    public TranslationListLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TranslationListLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.OPTS_INPUT = true;
        this.mTouchMoveNumber = 0;
        this.mActivePointerId = -1;
        this.mIsBeingDragged = false;
        this.mTranslation = 0.0f;
        this.mState = 1;
        this.mPositionState = 2;
        this.mCanDraggedDown = true;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        float density = getResources().getDisplayMetrics().density;
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.TranslationListLayout);
        String draggedStr = array.getString(R.styleable.TranslationListLayout_draggedText);
        if (draggedStr == null || draggedStr.equals("")) {
            draggedStr = getContext().getString(R.string.nubia_translation_list_dragged_text);
        }
        float textSize = array.getDimension(R.styleable.TranslationListLayout_textSize, DEFAULT_TEXT_SIZE * density);
        int textColor = array.getColor(R.styleable.TranslationListLayout_textColor, DEFAULT_TEXT_COLOR);
        int marginTop = array.getDimensionPixelSize(R.styleable.TranslationListLayout_hintMarginTop, (int) (DEFAULT_MARGIN_TOP * density));
        this.mTextTranslation = (1.5f * textSize) + ((float) marginTop);
        this.mThresholdTranslation = (float) array.getDimensionPixelSize(R.styleable.TranslationListLayout_pausedDistance, (int) (DEFAULT_PAUSED_TRANSLATION * density));
        this.mHeaderTextView = new TextView(getContext());
        this.mHeaderTextView.setTextColor(textColor);
        this.mHeaderTextView.setTextSize(0, textSize);
        this.mHeaderTextView.setText(draggedStr);
        LayoutParams layoutParams = new LayoutParams(-2, -2, 1);
        layoutParams.setMargins(0, marginTop, 0, 0);
        addView((View) this.mHeaderTextView, (ViewGroup.LayoutParams) layoutParams);
        array.recycle();
        this.mTranslation = this.mThresholdTranslation;
        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        this.mTouchSlop = configuration.getScaledTouchSlop();
        this.mMinimumVelocity = (float) configuration.getScaledMinimumFlingVelocity();
        this.mMaximumVelocity = (float) configuration.getScaledMaximumFlingVelocity();
        if (ON_BOTTOM) {
            setDefaultPullDown(true);
        } else {
            setDefaultPullDown(false);
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() == 2) {
            this.mCaptureView = getChildAt(1);
            if (ON_BOTTOM) {
                this.mCaptureView.setTranslationY(this.mThresholdTranslation);
            } else {
                this.mCaptureView.setTranslationY(0.0f);
            }
        }
        if (getChildCount() > 2) {
            throw new IllegalStateException("TranslationListLayout can host only one direct child");
        }
    }

    public void setDefaultPullDown(boolean isPullDown) {
        if (isPullDown) {
            this.mHeaderTextView.setAlpha(1.0f);
            this.mHeaderTextView.setTranslationY(0.0f);
            if (this.mCaptureView != null) {
                this.mCaptureView.setTranslationY(this.mThresholdTranslation);
            }
            this.mTranslation = this.mThresholdTranslation;
            this.mPositionState = 1;
            ON_BOTTOM = true;
        } else {
            this.mHeaderTextView.setAlpha(0.0f);
            this.mHeaderTextView.setTranslationY(-this.mTextTranslation);
            if (this.mCaptureView != null) {
                this.mCaptureView.setTranslationY(0.0f);
            }
            this.mTranslation = 0.0f;
            this.mPositionState = 2;
            ON_BOTTOM = false;
        }
        invalidate();
    }

    public void setCanDraggedDownView(boolean canDrag) {
        this.mCanDraggedDown = canDrag;
        if (this.mCanDraggedDown) {
            this.mHeaderTextView.setVisibility(0);
            setDefaultPullDown(true);
            return;
        }
        this.mHeaderTextView.setVisibility(8);
        this.mCaptureView.setTranslationY(0.0f);
        this.mTranslation = 0.0f;
        invalidate();
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float realTouchSlop;
        if (!this.mCanDraggedDown) {
            return super.onInterceptTouchEvent(ev);
        }
        if ((ev.getAction() == 2 && this.mIsBeingDragged) || this.mState == 4) {
            return true;
        }
        switch (ev.getActionMasked()) {
            case 0:
                int y = (int) ev.getY();
                this.mIsBeingDragged = false;
                this.mLastMotionY = (float) y;
                this.mActivePointerId = ev.getPointerId(0);
                initOrResetVelocityTracker();
                this.mVelocityTracker.addMovement(ev);
                if (this.OPTS_INPUT) {
                    this.mTouchMoveNumber = 0;
                    break;
                }
                break;
            case 1:
            case 3:
                this.mIsBeingDragged = false;
                this.mActivePointerId = -1;
                this.mState = 1;
                recycleVelocityTracker();
                if (this.OPTS_INPUT) {
                    this.mTouchMoveNumber = 0;
                    break;
                }
                break;
            case 2:
                if (this.OPTS_INPUT) {
                    this.mTouchMoveNumber++;
                }
                int activePointerId = this.mActivePointerId;
                if (activePointerId != -1) {
                    int pointerIndex = ev.findPointerIndex(activePointerId);
                    if (pointerIndex != -1) {
                        initVelocityTrackerIfNotExists();
                        this.mVelocityTracker.addMovement(ev);
                        float yy = ev.getY(pointerIndex);
                        float yDiff = yy - this.mLastMotionY;
                        if (!this.OPTS_INPUT) {
                            realTouchSlop = (float) this.mTouchSlop;
                        } else if (this.mTouchMoveNumber == 1) {
                            realTouchSlop = (float) ((int) (TOUCH_SLOP_RATIO * ((float) this.mTouchSlop)));
                        } else {
                            realTouchSlop = (float) this.mTouchSlop;
                        }
                        if (Math.abs(yDiff) > realTouchSlop && this.mPositionState == 1) {
                            this.mIsBeingDragged = true;
                            this.mState = 2;
                            this.mLastMotionY = yy;
                            ViewParent parent = getParent();
                            if (parent != null) {
                                parent.requestDisallowInterceptTouchEvent(true);
                            }
                        }
                        if (yDiff > ((float) this.mTouchSlop) && this.mPositionState == 2 && canDragCaptureView()) {
                            this.mIsBeingDragged = true;
                            this.mLastMotionY = yy;
                            ViewParent parent2 = getParent();
                            if (parent2 != null) {
                                parent2.requestDisallowInterceptTouchEvent(true);
                                break;
                            }
                        }
                    }
                }
                break;
            case 5:
                if (this.OPTS_INPUT) {
                    this.mTouchMoveNumber = 0;
                    break;
                }
                break;
            case 6:
                onSecondaryPointerUp(ev);
                if (this.OPTS_INPUT) {
                    this.mTouchMoveNumber = 0;
                    break;
                }
                break;
        }
        return this.mIsBeingDragged;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!this.mCanDraggedDown) {
            return super.onTouchEvent(event);
        }
        if (this.mState == 4) {
            return true;
        }
        initVelocityTrackerIfNotExists();
        this.mVelocityTracker.addMovement(event);
        switch (event.getActionMasked()) {
            case 0:
                if (getChildCount() < 2) {
                    return false;
                }
                if (this.mIsBeingDragged) {
                    this.mState = 2;
                }
                this.mLastMotionY = event.getY();
                this.mActivePointerId = event.getPointerId(0);
                return true;
            case 1:
            case 3:
                if (this.mIsBeingDragged) {
                    this.mState = 4;
                    VelocityTracker velocityTracker = this.mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000, this.mMaximumVelocity);
                    int initialVelocity = (int) velocityTracker.getYVelocity(this.mActivePointerId);
                    if (((float) Math.abs(initialVelocity)) < 4.0f * this.mMinimumVelocity) {
                        doBackAnimation();
                    } else {
                        doBackAnimation(initialVelocity);
                    }
                    this.mActivePointerId = -1;
                    this.mIsBeingDragged = false;
                }
                recycleVelocityTracker();
                return true;
            case 2:
                int activePointerIndex = event.findPointerIndex(this.mActivePointerId);
                if (activePointerIndex == -1) {
                    Log.e(TAG, "Invalid pointerId=" + this.mActivePointerId + " in onTouchEvent");
                    return true;
                }
                int y = (int) event.getY(activePointerIndex);
                float deltaY = ((float) y) - this.mLastMotionY;
                if (this.mPositionState == 1 && !this.mIsBeingDragged && Math.abs(deltaY) > ((float) this.mTouchSlop)) {
                    ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                    this.mIsBeingDragged = true;
                    this.mState = 2;
                    deltaY = deltaY > 0.0f ? deltaY - ((float) this.mTouchSlop) : deltaY + ((float) this.mTouchSlop);
                }
                if (!this.mIsBeingDragged && deltaY > ((float) this.mTouchSlop) && this.mPositionState == 2 && canDragCaptureView()) {
                    ViewParent parent2 = getParent();
                    if (parent2 != null) {
                        parent2.requestDisallowInterceptTouchEvent(true);
                    }
                    this.mIsBeingDragged = true;
                    this.mState = 2;
                    if (deltaY > 0.0f) {
                        deltaY -= (float) this.mTouchSlop;
                    } else {
                        deltaY += (float) this.mTouchSlop;
                    }
                }
                if (!this.mIsBeingDragged) {
                    return true;
                }
                this.mLastMotionY = (float) y;
                setCaptureViewTranslation(deltaY);
                return true;
            case 5:
                int index = event.getActionIndex();
                this.mLastMotionY = event.getY(index);
                this.mActivePointerId = event.getPointerId(index);
                return true;
            case 6:
                onSecondaryPointerUp(event);
                return true;
            default:
                return true;
        }
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        int pointerIndex = (ev.getAction() & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8;
        if (ev.getPointerId(pointerIndex) == this.mActivePointerId) {
            int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            this.mLastMotionY = (float) ((int) ev.getY(newPointerIndex));
            this.mActivePointerId = ev.getPointerId(newPointerIndex);
            if (this.mVelocityTracker != null) {
                this.mVelocityTracker.clear();
            }
        }
    }

    private void setCaptureViewTranslation(float deltaY) {
        this.mTranslation += deltaY;
        if (this.mTranslation > this.mThresholdTranslation) {
            this.mTranslation = this.mThresholdTranslation;
        }
        if (this.mTranslation < 0.0f) {
            this.mTranslation = 0.0f;
        }
        float ratio = this.mTranslation / this.mThresholdTranslation;
        this.mCaptureView.setTranslationY(this.mTranslation);
        this.mHeaderTextView.setTranslationY((-this.mTextTranslation) * (1.0f - ratio));
        this.mHeaderTextView.setAlpha(ratio);
    }

    private void doBackAnimation() {
        if (this.mPositionState == 2) {
            if (this.mTranslation >= this.mThresholdTranslation * 0.25f) {
                createToBottomAnimator(0).start();
            } else {
                createToTopAnimator(0).start();
            }
        } else if (this.mTranslation >= this.mThresholdTranslation * 0.75f) {
            createToBottomAnimator(0).start();
        } else {
            createToTopAnimator(0).start();
        }
    }

    private void doBackAnimation(int velocity) {
        if (this.mPositionState == 2) {
            if (velocity > 0) {
                createToBottomAnimator(velocity).start();
            } else if (this.mTranslation > 0.9f * this.mThresholdTranslation) {
                createToBottomAnimator(velocity).start();
            } else {
                createToTopAnimator(velocity).start();
            }
        } else if (velocity < 0) {
            createToTopAnimator(velocity).start();
        } else {
            createToBottomAnimator(velocity).start();
        }
    }

    private Animator createToBottomAnimator(int velocity) {
        int duration = ((int) (100.0f * (Math.abs(this.mThresholdTranslation - this.mTranslation) / this.mThresholdTranslation))) + 50;
        if (velocity != 0) {
            if (((float) Math.abs(velocity)) > this.mMaximumVelocity / 2.0f) {
                duration = 50;
            } else {
                duration = ((int) (((float) (duration - 50)) * (1.0f - (((float) (Math.abs(velocity) * 2)) / this.mMaximumVelocity)))) + 50;
            }
        }
        AnimatorSet aSet = new AnimatorSet();
        ObjectAnimator animator = ObjectAnimator.ofFloat(this.mCaptureView, "translationY", new float[]{this.mThresholdTranslation});
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration((long) duration);
        animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                TranslationListLayout.this.mTranslation = TranslationListLayout.this.mThresholdTranslation;
                TranslationListLayout.this.mPositionState = 1;
                TranslationListLayout.ON_BOTTOM = true;
                TranslationListLayout.this.mState = 1;
            }
        });
        ObjectAnimator textTranAnim = ObjectAnimator.ofFloat(this.mHeaderTextView, "translationY", new float[]{0.0f});
        textTranAnim.setInterpolator(new LinearInterpolator());
        textTranAnim.setDuration((long) duration);
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(this.mHeaderTextView, "alpha", new float[]{1.0f});
        alphaAnim.setInterpolator(new LinearInterpolator());
        alphaAnim.setDuration((long) duration);
        aSet.playTogether(new Animator[]{animator, textTranAnim, alphaAnim});
        return aSet;
    }

    private Animator createToTopAnimator(int velocity) {
        int duration = ((int) (100.0f * (this.mTranslation / this.mThresholdTranslation))) + 50;
        if (velocity != 0) {
            if (((float) Math.abs(velocity)) > this.mMaximumVelocity / 2.0f) {
                duration = 50;
            } else {
                duration = ((int) (((float) (duration - 50)) * (1.0f - (((float) (Math.abs(velocity) * 2)) / this.mMaximumVelocity)))) + 50;
            }
        }
        AnimatorSet aSet = new AnimatorSet();
        ObjectAnimator animator = ObjectAnimator.ofFloat(this.mCaptureView, "translationY", new float[]{0.0f});
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration((long) duration);
        animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                TranslationListLayout.this.mTranslation = 0.0f;
                TranslationListLayout.this.mPositionState = 2;
                TranslationListLayout.this.mState = 1;
                TranslationListLayout.ON_BOTTOM = false;
            }
        });
        ObjectAnimator textTranAnim = ObjectAnimator.ofFloat(this.mHeaderTextView, "translationY", new float[]{-this.mTextTranslation});
        textTranAnim.setInterpolator(new LinearInterpolator());
        textTranAnim.setDuration((long) duration);
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(this.mHeaderTextView, "alpha", new float[]{0.0f});
        alphaAnim.setInterpolator(new LinearInterpolator());
        alphaAnim.setDuration((long) duration);
        aSet.playTogether(new Animator[]{animator, textTranAnim, alphaAnim});
        return aSet;
    }

    private boolean canDragCaptureView() {
        boolean z;
        if (this.mCaptureView instanceof AbsListView) {
            if (((AbsListView) this.mCaptureView).getFirstVisiblePosition() == 0 && ((AbsListView) this.mCaptureView).getChildAt(0) != null && ((AbsListView) this.mCaptureView).getChildAt(0).getTop() == 0) {
                z = true;
            } else {
                z = false;
            }
            return z;
        } else if (this.mCaptureView instanceof ScrollView) {
            if (this.mCaptureView.getScrollY() != 0) {
                return false;
            }
            return true;
        } else if (!(this.mCaptureView instanceof RecyclerView)) {
            this.mViewCanScrolled = false;
            if (isFindScrollerViewGroup(this.mCaptureView)) {
                return true;
            }
            if (this.mViewCanScrolled) {
                return false;
            }
            if (this.mCaptureView.getScrollY() != 0) {
                return false;
            }
            return true;
        } else if (this.mCaptureView.canScrollVertically(-1)) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isFindScrollerViewGroup(View view) {
        if (view instanceof ViewGroup) {
            int count = ((ViewGroup) view).getChildCount();
            for (int i = 0; i < count; i++) {
                View childView = ((ViewGroup) view).getChildAt(i);
                if (!(childView.getVisibility() == 8 || childView.getVisibility() == 4)) {
                    if (childView instanceof AbsListView) {
                        this.mViewCanScrolled = true;
                        return ((AbsListView) childView).getFirstVisiblePosition() == 0 && ((AbsListView) childView).getChildAt(0) != null && ((AbsListView) childView).getChildAt(0).getTop() == 0;
                    } else if (childView instanceof ScrollView) {
                        this.mViewCanScrolled = true;
                        if (childView.getScrollY() != 0) {
                            return false;
                        }
                        return true;
                    } else if (childView instanceof RecyclerView) {
                        this.mViewCanScrolled = true;
                        if (childView.canScrollVertically(-1)) {
                            return false;
                        }
                        return true;
                    } else if (isFindScrollerViewGroup(childView)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void addView(View child) {
        if (getChildCount() > 1) {
            throw new IllegalStateException("TranslationListLayout can host only one direct child");
        }
        super.addView(child);
        initCaptureView();
    }

    public void addView(View child, int index) {
        if (getChildCount() > 1) {
            throw new IllegalStateException("TranslationListLayout can host only one direct child");
        }
        super.addView(child, index);
        initCaptureView();
    }

    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (getChildCount() > 1) {
            throw new IllegalStateException("TranslationListLayout can host only one direct child");
        }
        super.addView(child, index, params);
        initCaptureView();
    }

    public void addView(View child, ViewGroup.LayoutParams params) {
        if (getChildCount() > 1) {
            throw new IllegalStateException("TranslationListLayout can host only one direct child");
        }
        super.addView(child, params);
        initCaptureView();
    }

    public void addView(View child, int width, int height) {
        if (getChildCount() > 1) {
            throw new IllegalStateException("TranslationListLayout can host only one direct child");
        }
        super.addView(child, width, height);
        initCaptureView();
    }

    private void initCaptureView() {
        if (getChildCount() == 2) {
            this.mCaptureView = getChildAt(1);
            if (ON_BOTTOM) {
                this.mCaptureView.setTranslationY(this.mThresholdTranslation);
            } else {
                this.mCaptureView.setTranslationY(0.0f);
            }
        }
    }

    private void initOrResetVelocityTracker() {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        } else {
            this.mVelocityTracker.clear();
        }
    }

    private void initVelocityTrackerIfNotExists() {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    /* access modifiers changed from: protected */
    public void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == 0 && this.mCanDraggedDown) {
            if (!canDragCaptureView()) {
                setDefaultPullDown(false);
            } else if (ON_BOTTOM) {
                setDefaultPullDown(true);
            } else {
                setDefaultPullDown(false);
            }
        }
    }
}
