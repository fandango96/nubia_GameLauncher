package cn.nubia.commonui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.support.constraint.solver.widgets.ConstraintAnchor;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;
import cn.nubia.commonui.R;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class WheelView extends View {
    private static final int SELECTOR_MAX_FLING_VELOCITY_ADJUSTMENT = 4;
    private static final int SELECTOR_MIDDLE_ITEM_INDEX = 4;
    private static final int SELECTOR_WHEEL_ITEM_COUNT = 9;
    private static final int SNAP_SCROLL_DURATION = 300;
    private static final TwoDigitFormatter sTwoDigitFormatter = new TwoDigitFormatter();
    private final int EDGE_ALPHA;
    private final int ENHANCED_ALPHA;
    private final int MIDDLE_ALPHA;
    private final int mAdjustDrawPos;
    private final Scroller mAdjustScroller;
    private int mCurrentAlpha;
    private float mCurrentLocationX;
    private float mCurrentLocationY;
    private int mCurrentScrollOffset;
    private String[] mDisplayedValues;
    private Scroller mFlingScroller;
    private Formatter mFormatter;
    private GestureDetector mGestureDetector;
    private int mInitialScrollOffset;
    private long mLastDownEventTime;
    private float mLastDownEventY;
    private float mLastDownOrMoveEventY;
    private Paint mMaskPaint;
    private int mMaxValue;
    private int mMaximumFlingVelocity;
    private int mMiddleBottom;
    private int mMiddleBottomEnd;
    private int mMiddleTop;
    private int mMiddleTopEnd;
    private float mMiddleY;
    private int mMinValue;
    private int mMinimumFlingVelocity;
    private OnScrollListener mOnScrollListener;
    private OnValueChangeListener mOnValueChangeListener;
    private int mPreviousScrollerY;
    private int mScrollState;
    private Paint mSelectedWheelPaint;
    private int mSelectorElementHeight;
    private final SparseArray<String> mSelectorIndexToStringCache;
    private final int[] mSelectorIndices;
    private int mSelectorTextGapHeight;
    private int mSelectorTextGapHeightNotWrap;
    private Rect mTextBound;
    private Paint mTextPaint;
    private Rect mTextShowRect;
    private int mTextSize;
    private float[] mTextsLocation;
    private float[] mTextsScaleX;
    private float[] mTextsSize;
    private int mTouchSlop;
    private int mValue;
    private VelocityTracker mVelocityTracker;
    private boolean mWrapSelectorWheel;

    public interface Formatter {
        String format(int i);
    }

    public interface OnScrollListener {
        public static final int SCROLL_STATE_FLING = 2;
        public static final int SCROLL_STATE_IDLE = 0;
        public static final int SCROLL_STATE_TOUCH_SCROLL = 1;

        public @interface ScrollState {
        }

        void onScrollStateChange(WheelView wheelView, @ScrollState int i);
    }

    public interface OnValueChangeListener {
        void onValueChange(WheelView wheelView, int i, int i2);
    }

    private static class TwoDigitFormatter implements Formatter {
        final Object[] mArgs = new Object[1];
        final StringBuilder mBuilder = new StringBuilder();
        java.util.Formatter mFmt;
        char mZeroDigit;

        TwoDigitFormatter() {
            init(Locale.getDefault());
        }

        private void init(Locale locale) {
            this.mFmt = createFormatter(locale);
            this.mZeroDigit = getZeroDigit(locale);
        }

        public String format(int value) {
            Locale currentLocale = Locale.getDefault();
            if (this.mZeroDigit != getZeroDigit(currentLocale)) {
                init(currentLocale);
            }
            this.mArgs[0] = Integer.valueOf(value);
            this.mBuilder.delete(0, this.mBuilder.length());
            this.mFmt.format("%02d", this.mArgs);
            return this.mFmt.toString();
        }

        private static char getZeroDigit(Locale locale) {
            return new DecimalFormatSymbols(locale).getZeroDigit();
        }

        private java.util.Formatter createFormatter(Locale locale) {
            return new java.util.Formatter(this.mBuilder, locale);
        }
    }

    public WheelView(Context context) {
        this(context, null);
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mSelectedWheelPaint = new Paint();
        this.ENHANCED_ALPHA = 225;
        this.EDGE_ALPHA = 0;
        this.MIDDLE_ALPHA = 150;
        this.mTextBound = new Rect();
        this.mTextShowRect = new Rect();
        this.mMaskPaint = new Paint();
        this.mSelectorIndices = new int[9];
        this.mSelectorIndexToStringCache = new SparseArray<>();
        this.mInitialScrollOffset = Integer.MIN_VALUE;
        this.mScrollState = 0;
        this.mTextsScaleX = new float[9];
        this.mTextsSize = new float[9];
        this.mTextsLocation = new float[9];
        this.mFlingScroller = new Scroller(getContext(), null, true);
        this.mAdjustScroller = new Scroller(getContext(), new DecelerateInterpolator(8.0f));
        this.mTextPaint = new Paint();
        this.mTextPaint.setColor(-16776961);
        int selectedWheelPaintColor = getContext().getResources().getColor(R.color.nubia_wheelview_text_color);
        this.mMaskPaint.setColor(getContext().getResources().getColor(R.color.nubia_wheelview_middle_zone_color));
        this.mSelectedWheelPaint.setColor(selectedWheelPaintColor);
        this.mSelectedWheelPaint.setAntiAlias(true);
        this.mSelectedWheelPaint.setTextAlign(Align.LEFT);
        this.mTextSize = getResources().getDimensionPixelSize(R.dimen.nubia_wheel_text_size);
        this.mAdjustDrawPos = getContext().getResources().getDimensionPixelOffset(R.dimen.nubia_wheel_adjust_pos);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        this.mTouchSlop = configuration.getScaledTouchSlop();
        this.mMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
        this.mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity() / 4;
    }

    public void computeScroll() {
        Scroller scroller = this.mFlingScroller;
        if (scroller.isFinished()) {
            scroller = this.mAdjustScroller;
            if (scroller.isFinished()) {
                return;
            }
        }
        scroller.computeScrollOffset();
        int currentScrollerY = scroller.getCurrY();
        if (this.mPreviousScrollerY == 0) {
            this.mPreviousScrollerY = scroller.getStartY();
        }
        scrollBy(0, currentScrollerY - this.mPreviousScrollerY);
        this.mPreviousScrollerY = currentScrollerY;
        if (scroller.isFinished()) {
            onScrollerFinished(scroller);
        } else {
            invalidate();
        }
    }

    public void scrollBy(int x, int y) {
        int[] selectorIndices = this.mSelectorIndices;
        if (!this.mWrapSelectorWheel && y > 0 && selectorIndices[4] <= this.mMinValue) {
            this.mCurrentScrollOffset = this.mInitialScrollOffset;
        } else if (this.mWrapSelectorWheel || y >= 0 || selectorIndices[4] < this.mMaxValue) {
            if (!this.mWrapSelectorWheel && (selectorIndices[4] <= this.mMinValue || selectorIndices[4] >= this.mMaxValue)) {
                this.mSelectorTextGapHeight = this.mSelectorTextGapHeightNotWrap;
            }
            this.mCurrentScrollOffset += y;
            while (this.mCurrentScrollOffset - this.mInitialScrollOffset > this.mSelectorTextGapHeight) {
                this.mCurrentScrollOffset -= this.mSelectorElementHeight;
                decrementSelectorIndices(selectorIndices);
                setValueInternal(selectorIndices[4], true);
                if (!this.mWrapSelectorWheel && selectorIndices[4] <= this.mMinValue) {
                    this.mCurrentScrollOffset = this.mInitialScrollOffset;
                }
            }
            while (this.mCurrentScrollOffset - this.mInitialScrollOffset < (-this.mSelectorTextGapHeight)) {
                this.mCurrentScrollOffset += this.mSelectorElementHeight;
                incrementSelectorIndices(selectorIndices);
                setValueInternal(selectorIndices[4], true);
                if (!this.mWrapSelectorWheel && selectorIndices[4] >= this.mMaxValue) {
                    this.mCurrentScrollOffset = this.mInitialScrollOffset;
                }
            }
            getDrawTextAttri();
        } else {
            this.mCurrentScrollOffset = this.mInitialScrollOffset;
        }
    }

    private void onScrollerFinished(Scroller scroller) {
        if (scroller == this.mFlingScroller) {
            ensureScrollWheelAdjusted();
            onScrollStateChange(0);
        }
    }

    private void onScrollStateChange(int scrollState) {
        if (this.mScrollState != scrollState) {
            this.mScrollState = scrollState;
            if (this.mOnScrollListener != null) {
                this.mOnScrollListener.onScrollStateChange(this, scrollState);
            }
        }
    }

    public void setWrapSelectorWheel(boolean wrapSelectorWheel) {
        boolean wrappingAllowed = this.mMaxValue - this.mMinValue >= this.mSelectorIndices.length;
        if ((!wrapSelectorWheel || wrappingAllowed) && wrapSelectorWheel != this.mWrapSelectorWheel) {
            this.mWrapSelectorWheel = wrapSelectorWheel;
        }
    }

    private int getWrappedSelectorIndex(int selectorIndex) {
        if (selectorIndex > this.mMaxValue) {
            return (this.mMinValue + ((selectorIndex - this.mMaxValue) % (this.mMaxValue - this.mMinValue))) - 1;
        }
        if (selectorIndex < this.mMinValue) {
            return (this.mMaxValue - ((this.mMinValue - selectorIndex) % (this.mMaxValue - this.mMinValue))) + 1;
        }
        return selectorIndex;
    }

    public void setFormatter(Formatter formatter) {
        if (formatter != this.mFormatter) {
            this.mFormatter = formatter;
            initializeSelectorWheelIndices();
        }
    }

    private String formatNumber(int value) {
        if (this.mFormatter != null) {
            return this.mFormatter.format(value);
        }
        return Integer.toString(value);
    }

    private void ensureCachedScrollSelectorValue(int selectorIndex) {
        String scrollSelectorValue;
        SparseArray<String> cache = this.mSelectorIndexToStringCache;
        if (((String) cache.get(selectorIndex)) == null) {
            if (selectorIndex < this.mMinValue || selectorIndex > this.mMaxValue) {
                scrollSelectorValue = "";
            } else if (this.mDisplayedValues != null) {
                scrollSelectorValue = this.mDisplayedValues[selectorIndex - this.mMinValue];
            } else {
                scrollSelectorValue = formatNumber(selectorIndex);
            }
            cache.put(selectorIndex, scrollSelectorValue);
        }
    }

    private void initializeSelectorWheelIndices() {
        this.mSelectorIndexToStringCache.clear();
        int[] selectorIndices = this.mSelectorIndices;
        int current = getValue();
        for (int i = 0; i < this.mSelectorIndices.length; i++) {
            int selectorIndex = current + (i - 4);
            if (this.mWrapSelectorWheel) {
                selectorIndex = getWrappedSelectorIndex(selectorIndex);
            }
            selectorIndices[i] = selectorIndex;
            ensureCachedScrollSelectorValue(selectorIndices[i]);
        }
    }

    public void setMinValue(int value) {
        if (this.mMinValue != value) {
            if (value < 0) {
                throw new IllegalArgumentException("minValue must be >= 0");
            }
            this.mMinValue = value;
            if (this.mMinValue > this.mValue) {
                this.mValue = this.mMinValue;
            }
            setWrapSelectorWheel(this.mMaxValue - this.mMinValue > this.mSelectorIndices.length);
            initializeSelectorWheelIndices();
            invalidate();
        }
    }

    public void setMaxValue(int value) {
        if (this.mMaxValue != value) {
            if (value < 0) {
                throw new IllegalArgumentException("minValue must be >= 0");
            }
            this.mMaxValue = value;
            if (this.mMaxValue < this.mValue) {
                this.mValue = this.mMaxValue;
            }
            setWrapSelectorWheel(this.mMaxValue - this.mMinValue > this.mSelectorIndices.length);
            initializeSelectorWheelIndices();
            invalidate();
        }
    }

    public int getValue() {
        return this.mValue;
    }

    public int getMinValue() {
        return this.mMinValue;
    }

    public int getMaxValue() {
        return this.mMaxValue;
    }

    private boolean ensureScrollWheelAdjusted() {
        int deltaY = this.mInitialScrollOffset - this.mCurrentScrollOffset;
        if (deltaY == 0) {
            return false;
        }
        this.mPreviousScrollerY = 0;
        if (Math.abs(deltaY) > this.mSelectorElementHeight / 2) {
            deltaY += deltaY > 0 ? -this.mSelectorElementHeight : this.mSelectorElementHeight;
        }
        this.mAdjustScroller.startScroll(0, 0, 0, deltaY, SNAP_SCROLL_DURATION);
        invalidate();
        return true;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(event);
        switch (event.getAction() & 255) {
            case 0:
                float y = event.getY();
                this.mLastDownEventY = y;
                this.mLastDownOrMoveEventY = y;
                this.mLastDownEventTime = event.getEventTime();
                getParent().requestDisallowInterceptTouchEvent(true);
                if (this.mFlingScroller.isFinished()) {
                    if (!this.mAdjustScroller.isFinished()) {
                        this.mFlingScroller.forceFinished(true);
                        this.mAdjustScroller.forceFinished(true);
                        break;
                    }
                } else {
                    this.mFlingScroller.forceFinished(true);
                    this.mAdjustScroller.forceFinished(true);
                    onScrollStateChange(0);
                    break;
                }
                break;
            case 1:
                VelocityTracker velocityTracker = this.mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, (float) this.mMaximumFlingVelocity);
                int initialVelocity = (int) velocityTracker.getYVelocity();
                if (Math.abs(initialVelocity) > this.mMinimumFlingVelocity) {
                    fling(initialVelocity);
                } else {
                    ensureScrollWheelAdjusted();
                    onScrollStateChange(0);
                }
                this.mVelocityTracker.recycle();
                this.mVelocityTracker = null;
                break;
            case 2:
                float currentMoveY = event.getY();
                if (this.mScrollState == 1) {
                    scrollBy(0, (int) (currentMoveY - this.mLastDownOrMoveEventY));
                    invalidate();
                } else if (((int) Math.abs(currentMoveY - this.mLastDownEventY)) > this.mTouchSlop) {
                    onScrollStateChange(1);
                }
                this.mLastDownOrMoveEventY = currentMoveY;
                break;
        }
        return true;
    }

    private boolean moveToFinalScrollerPosition(Scroller scroller) {
        scroller.forceFinished(true);
        int amountToScroll = scroller.getFinalY() - scroller.getCurrY();
        int overshootAdjustment = this.mInitialScrollOffset - ((this.mCurrentScrollOffset + amountToScroll) % this.mSelectorElementHeight);
        if (overshootAdjustment == 0) {
            return false;
        }
        if (Math.abs(overshootAdjustment) > this.mSelectorElementHeight / 2) {
            if (overshootAdjustment > 0) {
                overshootAdjustment -= this.mSelectorElementHeight;
            } else {
                overshootAdjustment += this.mSelectorElementHeight;
            }
        }
        scrollBy(0, amountToScroll + overshootAdjustment);
        return true;
    }

    private void changeValueByOne(boolean increment) {
        if (!moveToFinalScrollerPosition(this.mFlingScroller)) {
            moveToFinalScrollerPosition(this.mAdjustScroller);
        }
        this.mPreviousScrollerY = 0;
        if (increment) {
            this.mFlingScroller.startScroll(0, 0, 0, -this.mSelectorElementHeight, SNAP_SCROLL_DURATION);
        } else {
            this.mFlingScroller.startScroll(0, 0, 0, this.mSelectorElementHeight, SNAP_SCROLL_DURATION);
        }
        invalidate();
    }

    private void incrementSelectorIndices(int[] selectorIndices) {
        for (int i = 0; i < selectorIndices.length - 1; i++) {
            selectorIndices[i] = selectorIndices[i + 1];
        }
        int nextScrollSelectorIndex = selectorIndices[selectorIndices.length - 2] + 1;
        if (this.mWrapSelectorWheel && nextScrollSelectorIndex > this.mMaxValue) {
            nextScrollSelectorIndex = this.mMinValue;
        }
        selectorIndices[selectorIndices.length - 1] = nextScrollSelectorIndex;
        ensureCachedScrollSelectorValue(nextScrollSelectorIndex);
    }

    private void decrementSelectorIndices(int[] selectorIndices) {
        for (int i = selectorIndices.length - 1; i > 0; i--) {
            selectorIndices[i] = selectorIndices[i - 1];
        }
        int nextScrollSelectorIndex = selectorIndices[1] - 1;
        if (this.mWrapSelectorWheel && nextScrollSelectorIndex < this.mMinValue) {
            nextScrollSelectorIndex = this.mMaxValue;
        }
        selectorIndices[0] = nextScrollSelectorIndex;
        ensureCachedScrollSelectorValue(nextScrollSelectorIndex);
    }

    private void notifyChange(int previous, int current) {
        if (this.mOnValueChangeListener != null) {
            this.mOnValueChangeListener.onValueChange(this, previous, this.mValue);
        }
    }

    public void setValue(int value) {
        setValueInternal(value, false);
    }

    private void setValueInternal(int current, boolean notifyChange) {
        int current2;
        if (this.mValue != current) {
            if (this.mWrapSelectorWheel) {
                current2 = getWrappedSelectorIndex(current);
            } else {
                current2 = Math.min(Math.max(current, this.mMinValue), this.mMaxValue);
            }
            int previous = this.mValue;
            this.mValue = current2;
            if (notifyChange) {
                notifyChange(previous, current2);
            }
            initializeSelectorWheelIndices();
            invalidate();
        }
    }

    private void fling(int velocityY) {
        this.mPreviousScrollerY = 0;
        if (velocityY > 0) {
            this.mFlingScroller.fling(0, 0, 0, velocityY, 0, 0, 0, ConstraintAnchor.ANY_GROUP);
        } else {
            this.mFlingScroller.fling(0, ConstraintAnchor.ANY_GROUP, 0, velocityY, 0, 0, 0, ConstraintAnchor.ANY_GROUP);
        }
        invalidate();
    }

    public void getDrawTextAttri() {
        float mStartRadian = (float) (1.5707963267948966d - ((double) (4.0f * 0.3926991f)));
        float mMappingViewHeight = (float) (this.mSelectorElementHeight * 8);
        float mHalfViewHeight = (float) (this.mSelectorElementHeight * 4);
        int[] selectorIndices = this.mSelectorIndices;
        float y = (float) this.mCurrentScrollOffset;
        for (int i = 0; i < selectorIndices.length; i++) {
            float mRadianY = mStartRadian + (((8.0f * 0.3926991f) * (y - ((float) this.mInitialScrollOffset))) / mMappingViewHeight);
            this.mTextsSize[i] = (float) (((double) this.mTextSize) * Math.sin((double) mRadianY));
            if (this.mTextsSize[i] < 0.0f) {
                this.mTextsSize[i] = 0.0f;
            }
            this.mTextsLocation[i] = (float) (((((double) (((float) this.mInitialScrollOffset) + mHalfViewHeight)) + (Math.sin(((double) mRadianY) - 1.5707963267948966d) * ((double) mHalfViewHeight))) + ((double) (this.mTextsSize[i] / 2.0f))) - ((double) this.mAdjustDrawPos));
            this.mTextsScaleX[i] = ((float) (0.8d + (0.2d * Math.sin((double) mRadianY)))) * (((float) this.mTextSize) / this.mTextsSize[i]);
            y += (float) this.mSelectorElementHeight;
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int[] selectorIndices = this.mSelectorIndices;
        for (int i = 0; i < selectorIndices.length; i++) {
            int selectorIndex = selectorIndices[i];
            this.mSelectedWheelPaint.setTextSize((float) this.mTextSize);
            this.mCurrentLocationY = this.mTextsLocation[i];
            this.mCurrentAlpha = 150 - ((int) ((300.0f * Math.abs(this.mCurrentLocationY - this.mMiddleY)) / ((float) getHeight())));
            this.mSelectedWheelPaint.setAlpha(this.mCurrentAlpha);
            String scrollSelectorValue = (String) this.mSelectorIndexToStringCache.get(selectorIndex);
            this.mSelectedWheelPaint.getTextBounds(scrollSelectorValue, 0, scrollSelectorValue.length(), this.mTextBound);
            canvas.save();
            canvas.scale(1.0f, 1.0f / this.mTextsScaleX[i]);
            this.mCurrentLocationX = (float) (((getRight() - getLeft()) / 2) - (this.mTextBound.width() / 2));
            this.mCurrentLocationY = this.mTextsLocation[i] * this.mTextsScaleX[i];
            this.mTextShowRect.left = (int) (this.mCurrentLocationX + ((float) this.mTextBound.left));
            this.mTextShowRect.top = (int) (this.mCurrentLocationY + (((float) this.mTextBound.top) * this.mTextsScaleX[i]));
            this.mTextShowRect.right = (int) (this.mCurrentLocationX + ((float) this.mTextBound.right));
            this.mTextShowRect.bottom = (int) (this.mCurrentLocationY + (((float) this.mTextBound.bottom) * this.mTextsScaleX[i]));
            canvas.drawText(scrollSelectorValue, this.mCurrentLocationX, this.mCurrentLocationY, this.mSelectedWheelPaint);
            if (((float) this.mTextShowRect.bottom) > ((float) this.mMiddleTop) * this.mTextsScaleX[i] && ((float) this.mTextShowRect.top) < ((float) this.mMiddleBottom) * this.mTextsScaleX[i]) {
                canvas.clipRect(0.0f, (((float) this.mMiddleTop) * this.mTextsScaleX[i]) + 1.0f, (float) getWidth(), ((float) this.mMiddleBottom) * this.mTextsScaleX[i]);
                canvas.drawRect(this.mTextShowRect, this.mMaskPaint);
                this.mSelectedWheelPaint.setAlpha(225);
                this.mSelectedWheelPaint.setTextSize((float) (this.mTextSize + 3));
                canvas.drawText(scrollSelectorValue, this.mCurrentLocationX, this.mCurrentLocationY, this.mSelectedWheelPaint);
            }
            canvas.restore();
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(getContext().getResources().getDimensionPixelSize(R.dimen.nubia_wheel_hight), 1073741824));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            initializeSelectorWheel();
            initializeFadingEdges();
        }
    }

    private void initializeSelectorWheel() {
        initializeSelectorWheelIndices();
        int[] selectorIndices = this.mSelectorIndices;
        float textGapCount = (float) selectorIndices.length;
        this.mSelectorTextGapHeight = (int) ((((float) ((getBottom() - getTop()) - (selectorIndices.length * this.mTextSize))) / textGapCount) + 0.5f);
        this.mSelectorTextGapHeightNotWrap = (int) ((((float) ((getBottom() - getTop()) - (this.mTextSize * 5))) / 5.0f) + 0.5f);
        this.mSelectorElementHeight = this.mTextSize + this.mSelectorTextGapHeight;
        this.mInitialScrollOffset = (int) (((float) (getBottom() - getTop())) / (2.0f * textGapCount));
        this.mCurrentScrollOffset = this.mInitialScrollOffset;
        this.mMiddleTop = ((getBottom() - getTop()) / 2) - this.mSelectorElementHeight;
        this.mMiddleBottom = ((getBottom() - getTop()) / 2) + this.mSelectorElementHeight;
        getDrawTextAttri();
        this.mMiddleY = (float) ((this.mMiddleTop + this.mMiddleBottom) / 2);
    }

    public int getMiddleTop() {
        return this.mMiddleTop;
    }

    public int getMiddleBottom() {
        return this.mMiddleBottom;
    }

    private void initializeFadingEdges() {
        setVerticalFadingEdgeEnabled(true);
        setFadingEdgeLength(((getBottom() - getTop()) - this.mTextSize) / 2);
    }

    public void setOnValueChangedListener(OnValueChangeListener onValueChangedListener) {
        this.mOnValueChangeListener = onValueChangedListener;
    }

    public void setDisplayedValues(String[] displayedValues) {
        if (this.mDisplayedValues != displayedValues) {
            this.mDisplayedValues = displayedValues;
            initializeSelectorWheelIndices();
        }
    }

    public String[] getDisplayedValues() {
        return this.mDisplayedValues;
    }

    public static final Formatter getTwoDigitFormatter() {
        return sTwoDigitFormatter;
    }
}
