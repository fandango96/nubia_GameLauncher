package cn.nubia.commonui.actionbar.internal.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff.Mode;
import android.support.annotation.Nullable;
import android.support.v4.view.TintableBackgroundView;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

public class TintAutoCompleteTextView extends AutoCompleteTextView implements TintableBackgroundView {
    private static final int[] TINT_ATTRS = {16842964, 16843126};
    private TintInfo mBackgroundTint;
    private TintManager mTintManager;

    public TintAutoCompleteTextView(Context context) {
        this(context, null);
    }

    public TintAutoCompleteTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 16842859);
    }

    public TintAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(TintContextWrapper.wrap(context), attrs, defStyleAttr);
        if (TintManager.SHOULD_BE_USED) {
            TintTypedArray a = TintTypedArray.obtainStyledAttributes(getContext(), attrs, TINT_ATTRS, defStyleAttr, 0);
            this.mTintManager = a.getTintManager();
            if (a.hasValue(0)) {
                setSupportBackgroundTintList(this.mTintManager.getColorStateList(a.getResourceId(0, -1)));
            }
            if (a.hasValue(1)) {
                setDropDownBackgroundDrawable(a.getDrawable(1));
            }
            a.recycle();
        }
    }

    public void setDropDownBackgroundResource(int id) {
        setDropDownBackgroundDrawable(this.mTintManager.getDrawable(id));
    }

    public void setSupportBackgroundTintList(@Nullable ColorStateList tint) {
        if (this.mBackgroundTint == null) {
            this.mBackgroundTint = new TintInfo();
        }
        this.mBackgroundTint.mTintList = tint;
        applySupportBackgroundTint();
    }

    @Nullable
    public ColorStateList getSupportBackgroundTintList() {
        if (this.mBackgroundTint != null) {
            return this.mBackgroundTint.mTintList;
        }
        return null;
    }

    public void setSupportBackgroundTintMode(@Nullable Mode tintMode) {
        if (this.mBackgroundTint == null) {
            this.mBackgroundTint = new TintInfo();
        }
        this.mBackgroundTint.mTintMode = tintMode;
        applySupportBackgroundTint();
    }

    @Nullable
    public Mode getSupportBackgroundTintMode() {
        if (this.mBackgroundTint != null) {
            return this.mBackgroundTint.mTintMode;
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public void drawableStateChanged() {
        super.drawableStateChanged();
        applySupportBackgroundTint();
    }

    private void applySupportBackgroundTint() {
        if (getBackground() != null && this.mBackgroundTint != null) {
            TintManager.tintViewBackground(this, this.mBackgroundTint);
        }
    }
}
