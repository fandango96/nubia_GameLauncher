package cn.nubia.commonui.actionbar.internal.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.support.v4.view.TintableBackgroundView;
import android.util.AttributeSet;
import android.widget.ListPopupWindow;
import android.widget.Spinner;
import java.lang.reflect.Field;

public class TintSpinner extends Spinner implements TintableBackgroundView {
    private static final int[] TINT_ATTRS = {16842964, 16843126};
    private TintInfo mBackgroundTint;

    public TintSpinner(Context context) {
        this(context, null);
    }

    public TintSpinner(Context context, AttributeSet attrs) {
        this(context, attrs, 16842881);
    }

    public TintSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (TintManager.SHOULD_BE_USED) {
            TintTypedArray a = TintTypedArray.obtainStyledAttributes(getContext(), attrs, TINT_ATTRS, defStyleAttr, 0);
            if (a.hasValue(0)) {
                setSupportBackgroundTintList(a.getTintManager().getColorStateList(a.getResourceId(0, -1)));
            }
            if (a.hasValue(1)) {
                Drawable popupBackground = a.getDrawable(1);
                if (VERSION.SDK_INT >= 16) {
                    setPopupBackgroundDrawable(popupBackground);
                } else if (VERSION.SDK_INT >= 11) {
                    setPopupBackgroundDrawableV11(this, popupBackground);
                }
            }
            a.recycle();
        }
    }

    @TargetApi(11)
    private static void setPopupBackgroundDrawableV11(Spinner view, Drawable background) {
        try {
            Field popupField = Spinner.class.getDeclaredField("mPopup");
            popupField.setAccessible(true);
            Object popup = popupField.get(view);
            if (popup instanceof ListPopupWindow) {
                ((ListPopupWindow) popup).setBackgroundDrawable(background);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        }
    }

    public void setSupportBackgroundTintList(@Nullable ColorStateList tint) {
        if (this.mBackgroundTint == null && tint != null) {
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
