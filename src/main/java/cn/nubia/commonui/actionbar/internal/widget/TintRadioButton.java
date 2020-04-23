package cn.nubia.commonui.actionbar.internal.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioButton;

public class TintRadioButton extends RadioButton {
    private static final int[] TINT_ATTRS = {16843015};
    private TintManager mTintManager;

    public TintRadioButton(Context context) {
        this(context, null);
    }

    public TintRadioButton(Context context, AttributeSet attrs) {
        this(context, attrs, 16842878);
    }

    public TintRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (TintManager.SHOULD_BE_USED) {
            TintTypedArray a = TintTypedArray.obtainStyledAttributes(getContext(), attrs, TINT_ATTRS, defStyleAttr, 0);
            setButtonDrawable(a.getDrawable(0));
            a.recycle();
            this.mTintManager = a.getTintManager();
        }
    }

    public void setButtonDrawable(int resid) {
        if (this.mTintManager != null) {
            setButtonDrawable(this.mTintManager.getDrawable(resid));
        } else {
            super.setButtonDrawable(resid);
        }
    }
}
