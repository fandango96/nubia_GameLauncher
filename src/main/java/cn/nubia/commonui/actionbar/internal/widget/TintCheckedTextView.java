package cn.nubia.commonui.actionbar.internal.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckedTextView;

public class TintCheckedTextView extends CheckedTextView {
    private static final int[] TINT_ATTRS = {16843016};
    private TintManager mTintManager;

    public TintCheckedTextView(Context context) {
        this(context, null);
    }

    public TintCheckedTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 16843720);
    }

    public TintCheckedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (TintManager.SHOULD_BE_USED) {
            TintTypedArray a = TintTypedArray.obtainStyledAttributes(getContext(), attrs, TINT_ATTRS, defStyleAttr, 0);
            setCheckMarkDrawable(a.getDrawable(0));
            a.recycle();
            this.mTintManager = a.getTintManager();
        }
    }

    public void setCheckMarkDrawable(int resid) {
        if (this.mTintManager != null) {
            setCheckMarkDrawable(this.mTintManager.getDrawable(resid));
        } else {
            super.setCheckMarkDrawable(resid);
        }
    }
}
