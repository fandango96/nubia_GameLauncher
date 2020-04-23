package cn.nubia.commonui.actionbar.internal.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;
import cn.nubia.commonui.R;
import cn.nubia.commonui.actionbar.internal.text.AllCapsTransformationMethod;

public class CompatTextView extends TextView {
    public CompatTextView(Context context) {
        this(context, null);
    }

    public CompatTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CompatTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CompatTextView, defStyle, 0);
        int ap = a.getResourceId(R.styleable.CompatTextView_android_textAppearance, -1);
        a.recycle();
        if (ap != -1) {
            TypedArray appearance = context.obtainStyledAttributes(ap, R.styleable.TextAppearance);
            if (appearance.hasValue(R.styleable.TextAppearance_textAllCaps)) {
                setAllCaps(appearance.getBoolean(R.styleable.TextAppearance_textAllCaps, false));
            }
            appearance.recycle();
        }
        TypedArray a2 = context.obtainStyledAttributes(attrs, R.styleable.CompatTextView, defStyle, 0);
        if (a2.hasValue(R.styleable.CompatTextView_textAllCaps)) {
            setAllCaps(a2.getBoolean(R.styleable.CompatTextView_textAllCaps, false));
        }
        a2.recycle();
    }

    public void setAllCaps(boolean allCaps) {
        setTransformationMethod(allCaps ? new AllCapsTransformationMethod(getContext()) : null);
    }

    public void setTextAppearance(Context context, int resid) {
        super.setTextAppearance(context, resid);
        TypedArray appearance = context.obtainStyledAttributes(resid, R.styleable.TextAppearance);
        if (appearance.hasValue(R.styleable.TextAppearance_textAllCaps)) {
            setAllCaps(appearance.getBoolean(R.styleable.TextAppearance_textAllCaps, false));
        }
        appearance.recycle();
    }
}
