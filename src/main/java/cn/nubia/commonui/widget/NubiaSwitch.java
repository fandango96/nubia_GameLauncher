package cn.nubia.commonui.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.PathInterpolator;
import android.widget.CompoundButton;
import cn.nubia.commonui.R;
import cn.nubia.commonui.util.AllCapsTransformationMethod;
import cn.nubia.commonui.util.FloatProperty;
import cn.nubia.commonui.util.Insets;
import cn.nubia.commonui.util.TransformationMethod2;

public class NubiaSwitch extends CompoundButton {
    private static final int[] CHECKED_STATE_SET = {16842912};
    private static final int MONOSPACE = 3;
    private static final int SANS = 1;
    private static final int SERIF = 2;
    private static final int THUMB_ANIMATION_DURATION = 150;
    private static final FloatProperty<NubiaSwitch> THUMB_POS = new FloatProperty<NubiaSwitch>("thumbPos") {
        public Float get(NubiaSwitch object) {
            return Float.valueOf(object.mThumbPosition);
        }

        public void setValue(NubiaSwitch object, float value) {
            object.setThumbPosition(value);
        }
    };
    private static final int TOUCH_MODE_DOWN = 1;
    private static final int TOUCH_MODE_DRAGGING = 2;
    private static final int TOUCH_MODE_IDLE = 0;
    private boolean mDragSwitch;
    private Drawable mDragThumbDrawable;
    private Drawable mInitThumbDrawable;
    private int mMinFlingVelocity;
    private Layout mOffLayout;
    private Layout mOnLayout;
    private ObjectAnimator mPositionAnimator;
    private final Rect mRectParameters;
    private boolean mShowText;
    private boolean mSplitTrack;
    private int mSwitchBottom;
    private int mSwitchHeight;
    private int mSwitchLeft;
    private int mSwitchMinHeight;
    private int mSwitchMinWidth;
    private int mSwitchPadding;
    private int mSwitchRight;
    private int mSwitchTop;
    private TransformationMethod2 mSwitchTransformationMethod;
    private int mSwitchWidth;
    private ColorStateList mTextColors;
    private CharSequence mTextOff;
    private CharSequence mTextOn;
    private TextPaint mTextPaint;
    private Drawable mThumbDrawable;
    private int mThumbOffsetTrack;
    /* access modifiers changed from: private */
    public float mThumbPosition;
    private int mThumbTextPadding;
    private int mThumbWidth;
    private int mTouchMode;
    private int mTouchSlop;
    private float mTouchX;
    private float mTouchY;
    private Drawable mTrackDrawable;
    private VelocityTracker mVelocityTracker;

    public NubiaSwitch(Context context) {
        this(context, null);
    }

    public NubiaSwitch(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.nubiaSwitchStyle);
    }

    public NubiaSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public NubiaSwitch(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mVelocityTracker = VelocityTracker.obtain();
        this.mDragSwitch = false;
        this.mRectParameters = new Rect();
        this.mTextPaint = new TextPaint(1);
        Resources res = getResources();
        this.mTextPaint.density = res.getDisplayMetrics().density;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NubiaSwitch, defStyleAttr, defStyleRes);
        this.mInitThumbDrawable = a.getDrawable(R.styleable.NubiaSwitch_thumb);
        this.mThumbDrawable = a.getDrawable(R.styleable.NubiaSwitch_thumb);
        if (this.mThumbDrawable != null) {
            this.mThumbDrawable.setCallback(this);
        }
        this.mDragThumbDrawable = res.getDrawable(R.drawable.nubia_switch_thumb_material);
        if (this.mDragThumbDrawable != null) {
            this.mDragThumbDrawable.setCallback(this);
        }
        this.mTrackDrawable = a.getDrawable(R.styleable.NubiaSwitch_track);
        if (this.mTrackDrawable != null) {
            this.mTrackDrawable.setCallback(this);
        }
        this.mThumbOffsetTrack = res.getDimensionPixelSize(R.dimen.nubia_switch_thumb_offset_track);
        this.mTextOn = a.getText(R.styleable.NubiaSwitch_textOn);
        this.mTextOff = a.getText(R.styleable.NubiaSwitch_textOff);
        this.mShowText = a.getBoolean(R.styleable.NubiaSwitch_showText, false);
        this.mThumbTextPadding = a.getDimensionPixelSize(R.styleable.NubiaSwitch_thumbTextPadding, 0);
        this.mSwitchMinWidth = a.getDimensionPixelSize(R.styleable.NubiaSwitch_switchMinWidth, 0);
        this.mSwitchMinHeight = a.getDimensionPixelSize(R.styleable.NubiaSwitch_switchMinHight, 0);
        this.mSwitchPadding = a.getDimensionPixelSize(R.styleable.NubiaSwitch_switchPadding, 0);
        this.mSplitTrack = a.getBoolean(R.styleable.NubiaSwitch_splitTrack, false);
        int appearance = a.getResourceId(R.styleable.NubiaSwitch_switchTextAppearance, 0);
        if (appearance != 0) {
            setSwitchTextAppearance(context, appearance);
        }
        a.recycle();
        ViewConfiguration config = ViewConfiguration.get(context);
        this.mTouchSlop = config.getScaledTouchSlop();
        this.mMinFlingVelocity = config.getScaledMinimumFlingVelocity();
        refreshDrawableState();
        setChecked(isChecked());
    }

    public void setSwitchTextAppearance(Context context, int resid) {
        ColorStateList colors = context.getResources().getColorStateList(R.color.nubia_primary_text_default_material_light);
        if (colors != null) {
            this.mTextColors = colors;
        } else {
            this.mTextColors = getTextColors();
        }
        int ts = context.getResources().getDimensionPixelSize(R.dimen.nubia_switch_text_size);
        if (!(ts == 0 || ((float) ts) == this.mTextPaint.getTextSize())) {
            this.mTextPaint.setTextSize((float) ts);
            requestLayout();
        }
        setSwitchTypefaceByIndex(-1, 0);
        this.mSwitchTransformationMethod = new AllCapsTransformationMethod(getContext());
        this.mSwitchTransformationMethod.setLengthChangesAllowed(true);
    }

    private void setSwitchTypefaceByIndex(int typefaceIndex, int styleIndex) {
        Typeface tf = null;
        switch (typefaceIndex) {
            case 1:
                tf = Typeface.SANS_SERIF;
                break;
            case 2:
                tf = Typeface.SERIF;
                break;
            case 3:
                tf = Typeface.MONOSPACE;
                break;
        }
        setSwitchTypeface(tf, styleIndex);
    }

    public void setSwitchTypeface(Typeface tf, int style) {
        Typeface tf2;
        int typefaceStyle;
        float f;
        boolean z = false;
        if (style > 0) {
            if (tf == null) {
                tf2 = Typeface.defaultFromStyle(style);
            } else {
                tf2 = Typeface.create(tf, style);
            }
            setSwitchTypeface(tf2);
            if (tf2 != null) {
                typefaceStyle = tf2.getStyle();
            } else {
                typefaceStyle = 0;
            }
            int need = style & (typefaceStyle ^ -1);
            TextPaint textPaint = this.mTextPaint;
            if ((need & 1) != 0) {
                z = true;
            }
            textPaint.setFakeBoldText(z);
            TextPaint textPaint2 = this.mTextPaint;
            if ((need & 2) != 0) {
                f = -0.25f;
            } else {
                f = 0.0f;
            }
            textPaint2.setTextSkewX(f);
            return;
        }
        this.mTextPaint.setFakeBoldText(false);
        this.mTextPaint.setTextSkewX(0.0f);
        setSwitchTypeface(tf);
    }

    public void setSwitchTypeface(Typeface tf) {
        if (this.mTextPaint.getTypeface() != tf) {
            this.mTextPaint.setTypeface(tf);
            requestLayout();
            invalidate();
        }
    }

    public void setSwitchPadding(int pixels) {
        this.mSwitchPadding = pixels;
        requestLayout();
    }

    public int getSwitchPadding() {
        return this.mSwitchPadding;
    }

    public void setSwitchMinWidth(int pixels) {
        this.mSwitchMinWidth = pixels;
        requestLayout();
    }

    public int getSwitchMinWidth() {
        return this.mSwitchMinWidth;
    }

    public void setThumbTextPadding(int pixels) {
        this.mThumbTextPadding = pixels;
        requestLayout();
    }

    public int getThumbTextPadding() {
        return this.mThumbTextPadding;
    }

    public void setTrackDrawable(Drawable track) {
        if (this.mTrackDrawable != null) {
            this.mTrackDrawable.setCallback(null);
        }
        this.mTrackDrawable = track;
        if (track != null) {
            track.setCallback(this);
        }
        requestLayout();
    }

    public void setTrackResource(int resId) {
        setTrackDrawable(getContext().getResources().getDrawable(resId));
    }

    public Drawable getTrackDrawable() {
        return this.mTrackDrawable;
    }

    public void setThumbDrawable(Drawable thumb) {
        if (this.mThumbDrawable != null) {
            this.mThumbDrawable.setCallback(null);
        }
        this.mThumbDrawable = thumb;
        if (thumb != null) {
            thumb.setCallback(this);
        }
        requestLayout();
    }

    public void setThumbResource(int resId) {
        setThumbDrawable(getContext().getResources().getDrawable(resId));
    }

    public Drawable getThumbDrawable() {
        return this.mThumbDrawable;
    }

    public void setSplitTrack(boolean splitTrack) {
        this.mSplitTrack = splitTrack;
        invalidate();
    }

    public boolean getSplitTrack() {
        return this.mSplitTrack;
    }

    public CharSequence getTextOn() {
        return this.mTextOn;
    }

    public void setTextOn(CharSequence textOn) {
        this.mTextOn = textOn;
        requestLayout();
    }

    public CharSequence getTextOff() {
        return this.mTextOff;
    }

    public void setTextOff(CharSequence textOff) {
        this.mTextOff = textOff;
        requestLayout();
    }

    public void setShowText(boolean showText) {
        if (this.mShowText != showText) {
            this.mShowText = showText;
            requestLayout();
        }
    }

    public boolean getShowText() {
        return this.mShowText;
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int thumbWidth;
        int thumbHeight;
        int maxTextWidth;
        int trackHeight;
        if (this.mShowText) {
            if (this.mOnLayout == null) {
                this.mOnLayout = makeLayout(this.mTextOn);
            }
            if (this.mOffLayout == null) {
                this.mOffLayout = makeLayout(this.mTextOff);
            }
        }
        Rect padding = this.mRectParameters;
        if (this.mThumbDrawable != null) {
            this.mThumbDrawable.getPadding(padding);
            thumbWidth = (this.mThumbDrawable.getIntrinsicWidth() - padding.left) - padding.right;
            thumbHeight = this.mThumbDrawable.getIntrinsicHeight();
        } else {
            thumbWidth = 0;
            thumbHeight = 0;
        }
        if (this.mShowText) {
            maxTextWidth = Math.max(this.mOnLayout.getWidth(), this.mOffLayout.getWidth()) + (this.mThumbTextPadding * 2);
        } else {
            maxTextWidth = 0;
        }
        this.mThumbWidth = Math.max(maxTextWidth, thumbWidth);
        if (this.mTrackDrawable != null) {
            this.mTrackDrawable.getPadding(padding);
            trackHeight = this.mTrackDrawable.getIntrinsicHeight();
        } else {
            padding.setEmpty();
            trackHeight = 0;
        }
        int paddingLeft = padding.left;
        int paddingRight = padding.right;
        if (this.mThumbDrawable != null) {
            Insets inset = Insets.NONE;
            paddingLeft = Math.max(paddingLeft, inset.left);
            paddingRight = Math.max(paddingRight, inset.right);
        }
        int switchWidth = Math.max(this.mSwitchMinWidth, this.mThumbWidth + paddingLeft + paddingRight);
        int switchHeight = Math.max(trackHeight, thumbHeight);
        this.mSwitchWidth = switchWidth;
        this.mSwitchHeight = switchHeight;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredHeight = getMeasuredHeight();
        if (measuredHeight < switchHeight && switchHeight < this.mSwitchMinHeight) {
            setMeasuredDimension(getPaddingStart() + switchWidth + getPaddingEnd(), this.mSwitchMinHeight);
        } else if (measuredHeight < switchHeight) {
            setMeasuredDimension(getPaddingStart() + switchWidth + getPaddingEnd(), getPaddingTop() + switchHeight + getPaddingBottom());
        }
    }

    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        super.onPopulateAccessibilityEvent(event);
        CharSequence text = isChecked() ? this.mTextOn : this.mTextOff;
        if (text != null) {
            event.getText().add(text);
        }
    }

    private Layout makeLayout(CharSequence text) {
        CharSequence transformed;
        if (this.mSwitchTransformationMethod != null) {
            transformed = this.mSwitchTransformationMethod.getTransformation(text, this);
        } else {
            transformed = text;
        }
        if (TextUtils.isEmpty(transformed)) {
            transformed = getContext().getResources().getString(R.string.nubia_capital_on);
        }
        return new StaticLayout(transformed, this.mTextPaint, (int) Math.ceil((double) Layout.getDesiredWidth(transformed, this.mTextPaint)), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
    }

    private boolean hitThumb(float x, float y) {
        int thumbOffset = getThumbOffset();
        this.mThumbDrawable.getPadding(this.mRectParameters);
        int thumbLeft = (this.mSwitchLeft + thumbOffset) - this.mTouchSlop;
        return x > ((float) thumbLeft) && x < ((float) ((((this.mThumbWidth + thumbLeft) + this.mRectParameters.left) + this.mRectParameters.right) + this.mTouchSlop)) && y > ((float) (this.mSwitchTop - this.mTouchSlop)) && y < ((float) (this.mSwitchBottom + this.mTouchSlop));
    }

    public int constrain(int amount, int low, int high) {
        if (amount < low) {
            return low;
        }
        return amount > high ? high : amount;
    }

    public long constrain(long amount, long low, long high) {
        if (amount < low) {
            return low;
        }
        return amount > high ? high : amount;
    }

    public float constrain(float amount, float low, float high) {
        if (amount < low) {
            return low;
        }
        return amount > high ? high : amount;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        this.mVelocityTracker.addMovement(ev);
        switch (ev.getActionMasked()) {
            case 0:
                float x = ev.getX();
                float y = ev.getY();
                if (isEnabled() && hitThumb(x, y)) {
                    this.mTouchMode = 1;
                    this.mTouchX = x;
                    this.mTouchY = y;
                    break;
                }
            case 1:
            case 3:
                if (this.mTouchMode != 2) {
                    this.mTouchMode = 0;
                    this.mVelocityTracker.clear();
                    break;
                } else {
                    stopDrag(ev);
                    super.onTouchEvent(ev);
                    return true;
                }
            case 2:
                switch (this.mTouchMode) {
                    case 1:
                        float x2 = ev.getX();
                        float y2 = ev.getY();
                        if (Math.abs(x2 - this.mTouchX) > ((float) this.mTouchSlop) || Math.abs(y2 - this.mTouchY) > ((float) this.mTouchSlop)) {
                            this.mTouchMode = 2;
                            getParent().requestDisallowInterceptTouchEvent(true);
                            this.mTouchX = x2;
                            this.mTouchY = y2;
                            return true;
                        }
                    case 2:
                        float x3 = ev.getX();
                        int thumbScrollRange = getThumbScrollRange();
                        float thumbScrollOffset = x3 - this.mTouchX;
                        float dPos = thumbScrollRange != 0 ? thumbScrollOffset / ((float) thumbScrollRange) : thumbScrollOffset > 0.0f ? 1.0f : -1.0f;
                        float newPos = constrain(this.mThumbPosition + dPos, 0.0f, 1.0f);
                        if (newPos != this.mThumbPosition) {
                            this.mTouchX = x3;
                            setThumbPosition(newPos);
                        }
                        return true;
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void cancelSuperTouch(MotionEvent ev) {
        MotionEvent cancel = MotionEvent.obtain(ev);
        cancel.setAction(3);
        super.onTouchEvent(cancel);
        cancel.recycle();
    }

    private void stopDrag(MotionEvent ev) {
        boolean commitChange;
        boolean newState = true;
        this.mTouchMode = 0;
        if (ev.getAction() != 1 || !isEnabled()) {
            commitChange = false;
        } else {
            commitChange = true;
        }
        if (commitChange) {
            this.mVelocityTracker.computeCurrentVelocity(1000);
            float xvel = this.mVelocityTracker.getXVelocity();
            if (Math.abs(xvel) <= ((float) this.mMinFlingVelocity)) {
                newState = getTargetCheckedState();
            } else if (xvel <= 0.0f) {
                newState = false;
            }
        } else {
            newState = isChecked();
        }
        setThumbDrawable(this.mDragThumbDrawable);
        setChecked(newState);
        cancelSuperTouch(ev);
    }

    private void animateThumbToCheckedState(boolean newCheckedState) {
        this.mPositionAnimator = ObjectAnimator.ofFloat(this, THUMB_POS, new float[]{newCheckedState ? 1.0f : 0.0f});
        this.mPositionAnimator.setDuration(150);
        this.mPositionAnimator.setInterpolator(new PathInterpolator(0.16f, 0.1f, 0.15f, 1.0f));
        this.mPositionAnimator.setAutoCancel(true);
        this.mPositionAnimator.start();
    }

    private void cancelPositionAnimator() {
        if (this.mPositionAnimator != null) {
            this.mPositionAnimator.cancel();
        }
    }

    private boolean getTargetCheckedState() {
        return this.mThumbPosition > 0.5f;
    }

    /* access modifiers changed from: private */
    public void setThumbPosition(float position) {
        this.mThumbPosition = position;
        invalidate();
    }

    public void toggle() {
        setChecked(!isChecked());
    }

    public void setChecked(boolean checked) {
        super.setChecked(checked);
        boolean checked2 = isChecked();
        if (!isAttachedToWindow() || !isLaidOut()) {
            cancelPositionAnimator();
            setThumbPosition(checked2 ? 1.0f : 0.0f);
            return;
        }
        animateThumbToCheckedState(checked2);
    }

    private boolean isLayoutRtlNubia() {
        return getLayoutDirection() == 1;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int switchRight;
        int switchLeft;
        int topBottomPadding;
        int switchBottom;
        int switchTop;
        super.onLayout(changed, left, top, right, bottom);
        int opticalInsetLeft = 0;
        int opticalInsetRight = 0;
        if (this.mThumbDrawable != null) {
            Rect trackPadding = this.mRectParameters;
            if (this.mTrackDrawable != null) {
                this.mTrackDrawable.getPadding(trackPadding);
            } else {
                trackPadding.setEmpty();
            }
            Insets insets = Insets.NONE;
            opticalInsetLeft = Math.max(0, insets.left - trackPadding.left);
            opticalInsetRight = Math.max(0, insets.right - trackPadding.right);
        }
        if (isLayoutRtlNubia()) {
            switchLeft = getPaddingLeft() + opticalInsetLeft;
            switchRight = ((this.mSwitchWidth + switchLeft) - opticalInsetLeft) - opticalInsetRight;
        } else {
            switchRight = (getWidth() - getPaddingRight()) - opticalInsetRight;
            switchLeft = (switchRight - this.mSwitchWidth) + opticalInsetLeft + opticalInsetRight;
        }
        if (this.mTrackDrawable == null) {
            topBottomPadding = 0;
        } else {
            topBottomPadding = (getHeight() - this.mTrackDrawable.getIntrinsicHeight()) / 2;
        }
        switch (getGravity() & 112) {
            case 16:
                switchTop = ((((getPaddingTop() + getHeight()) - getPaddingBottom()) / 2) - (this.mSwitchHeight / 2)) + topBottomPadding;
                switchBottom = switchTop + this.mSwitchHeight;
                break;
            case 80:
                switchBottom = (getHeight() - getPaddingBottom()) - topBottomPadding;
                switchTop = switchBottom - this.mSwitchHeight;
                break;
            default:
                switchTop = getPaddingTop() + topBottomPadding;
                switchBottom = switchTop + this.mSwitchHeight;
                break;
        }
        this.mSwitchLeft = switchLeft;
        this.mSwitchTop = switchTop;
        this.mSwitchBottom = switchBottom;
        this.mSwitchRight = switchRight;
    }

    public void draw(Canvas c) {
        Insets thumbInsets;
        Rect padding = this.mRectParameters;
        int switchLeft = this.mSwitchLeft;
        int switchTop = this.mSwitchTop;
        int switchRight = this.mSwitchRight;
        int switchBottom = this.mSwitchBottom;
        int thumbInitialLeft = this.mThumbOffsetTrack + switchLeft + getThumbOffset();
        if (this.mThumbDrawable != null) {
            thumbInsets = Insets.NONE;
        } else {
            thumbInsets = Insets.NONE;
        }
        if (this.mTrackDrawable != null) {
            this.mTrackDrawable.getPadding(padding);
            thumbInitialLeft += padding.left;
            int trackLeft = switchLeft;
            int trackTop = switchTop;
            int trackRight = switchRight;
            int trackBottom = switchBottom;
            if (thumbInsets != Insets.NONE) {
                if (thumbInsets.left > padding.left) {
                }
                if (thumbInsets.top > padding.top) {
                    trackTop += thumbInsets.top - padding.top;
                }
                if (thumbInsets.right > padding.right) {
                }
                if (thumbInsets.bottom > padding.bottom) {
                    trackBottom -= thumbInsets.bottom - padding.bottom;
                }
            }
            this.mTrackDrawable.setBounds(trackLeft, trackTop, trackRight, trackBottom);
        }
        if (this.mThumbDrawable != null) {
            this.mThumbDrawable.getPadding(padding);
            int thumbLeft = thumbInitialLeft - padding.left;
            int thumbRight = this.mThumbWidth + thumbInitialLeft + padding.right;
            this.mThumbDrawable.setBounds(thumbLeft, switchTop, thumbRight, switchBottom);
            Drawable background = getBackground();
            if (background != null) {
                background.setHotspotBounds(thumbLeft, switchTop, thumbRight, switchBottom);
            }
        }
        super.draw(c);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Layout switchText;
        int cX;
        super.onDraw(canvas);
        Rect padding = this.mRectParameters;
        Drawable trackDrawable = this.mTrackDrawable;
        if (trackDrawable != null) {
            trackDrawable.getPadding(padding);
        } else {
            padding.setEmpty();
        }
        int switchTop = this.mSwitchTop;
        int switchInnerTop = switchTop + padding.top;
        int switchInnerBottom = this.mSwitchBottom - padding.bottom;
        Drawable thumbDrawable = this.mThumbDrawable;
        if (trackDrawable != null) {
            if (!this.mSplitTrack || thumbDrawable == null) {
                trackDrawable.draw(canvas);
            } else {
                Insets insets = Insets.NONE;
                thumbDrawable.copyBounds(padding);
                padding.left += insets.left;
                padding.right -= insets.right;
                int saveCount = canvas.save();
                canvas.clipRect(padding, Op.DIFFERENCE);
                trackDrawable.draw(canvas);
                canvas.restoreToCount(saveCount);
            }
        }
        int saveCount2 = canvas.save();
        if (thumbDrawable != null) {
            thumbDrawable.draw(canvas);
        }
        if (getTargetCheckedState()) {
            switchText = this.mOnLayout;
        } else {
            switchText = this.mOffLayout;
        }
        if (switchText != null) {
            int[] drawableState = getDrawableState();
            if (this.mTextColors != null) {
                this.mTextPaint.setColor(this.mTextColors.getColorForState(drawableState, 0));
            }
            this.mTextPaint.drawableState = drawableState;
            if (thumbDrawable != null) {
                Rect bounds = thumbDrawable.getBounds();
                cX = bounds.left + bounds.right;
            } else {
                cX = getWidth();
            }
            canvas.translate((float) ((cX / 2) - (switchText.getWidth() / 2)), (float) (((switchInnerTop + switchInnerBottom) / 2) - (switchText.getHeight() / 2)));
            switchText.draw(canvas);
        }
        canvas.restoreToCount(saveCount2);
    }

    public int getCompoundPaddingLeft() {
        int padding = super.getCompoundPaddingLeft() + this.mSwitchWidth;
        if (!TextUtils.isEmpty(getText())) {
            return padding + this.mSwitchPadding;
        }
        return padding;
    }

    public int getCompoundPaddingRight() {
        int padding = super.getCompoundPaddingRight() + this.mSwitchWidth;
        if (!TextUtils.isEmpty(getText())) {
            return padding + this.mSwitchPadding;
        }
        return padding;
    }

    private int getThumbOffset() {
        return (int) ((((float) getThumbScrollRange()) * this.mThumbPosition) + 0.5f);
    }

    private int getThumbScrollRange() {
        Insets insets;
        if (this.mTrackDrawable == null) {
            return 0;
        }
        Rect padding = this.mRectParameters;
        this.mTrackDrawable.getPadding(padding);
        if (this.mThumbDrawable != null) {
            insets = Insets.NONE;
        } else {
            insets = Insets.NONE;
        }
        return ((((((this.mSwitchWidth - this.mThumbWidth) - padding.left) - padding.right) - insets.left) - insets.right) - this.mThumbOffsetTrack) - this.mThumbOffsetTrack;
    }

    /* access modifiers changed from: protected */
    public int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    /* access modifiers changed from: protected */
    public void drawableStateChanged() {
        super.drawableStateChanged();
        int[] myDrawableState = getDrawableState();
        if (this.mThumbDrawable != null) {
            this.mThumbDrawable.setState(myDrawableState);
        }
        if (this.mTrackDrawable != null) {
            this.mTrackDrawable.setState(myDrawableState);
        }
        invalidate();
    }

    /* access modifiers changed from: protected */
    public boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || who == this.mThumbDrawable || who == this.mTrackDrawable;
    }

    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (this.mThumbDrawable != null) {
            this.mThumbDrawable.jumpToCurrentState();
        }
        if (this.mTrackDrawable != null) {
            this.mTrackDrawable.jumpToCurrentState();
        }
        if (this.mPositionAnimator != null && this.mPositionAnimator.isRunning()) {
            this.mPositionAnimator.end();
            this.mPositionAnimator = null;
        }
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(NubiaSwitch.class.getName());
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(NubiaSwitch.class.getName());
        CharSequence switchText = isChecked() ? this.mTextOn : this.mTextOff;
        if (!TextUtils.isEmpty(switchText)) {
            CharSequence oldText = info.getText();
            if (TextUtils.isEmpty(oldText)) {
                info.setText(switchText);
                return;
            }
            StringBuilder newText = new StringBuilder();
            newText.append(oldText).append(' ').append(switchText);
            info.setText(newText);
        }
    }
}
