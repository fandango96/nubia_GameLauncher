package cn.nubia.commonui.actionbar.internal.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import cn.nubia.commonui.actionbar.internal.widget.FitWindowsViewGroup.OnFitSystemWindowsListener;

public class FitWindowsLinearLayout extends LinearLayout implements FitWindowsViewGroup {
    private OnFitSystemWindowsListener mListener;

    public FitWindowsLinearLayout(Context context) {
        super(context);
    }

    public FitWindowsLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnFitSystemWindowsListener(OnFitSystemWindowsListener listener) {
        this.mListener = listener;
    }

    /* access modifiers changed from: protected */
    public boolean fitSystemWindows(Rect insets) {
        if (this.mListener != null) {
            this.mListener.onFitSystemWindows(insets);
        }
        return super.fitSystemWindows(insets);
    }
}
