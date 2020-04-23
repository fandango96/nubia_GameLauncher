package cn.nubia.commonui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.view.WindowManager;
import android.widget.LinearLayout;
import cn.nubia.commonui.R;
import cn.nubia.commonui.ReflectUtils;

public class DialogCenterLinearLayout extends LinearLayout {
    private float mMaxHeight;
    private float mMaxHeightRatio;
    private float mSplitMaxHeight;
    private Object mWindowManager;

    public DialogCenterLinearLayout(Context context) {
        this(context, null);
    }

    public DialogCenterLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DialogCenterLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mMaxHeightRatio = 0.8f;
        this.mWindowManager = ReflectUtils.invoke("android.view.WindowManagerGlobal", "getWindowManagerService", true, true);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.nubiaDialogLinearLayout);
        if (ta != null) {
            this.mMaxHeightRatio = ta.getFloat(R.styleable.nubiaDialogLinearLayout_maxHeightRatio, this.mMaxHeightRatio);
            ta.recycle();
        }
        this.mSplitMaxHeight = (float) context.getResources().getDimensionPixelSize(R.dimen.nubia_center_dialog_min_height);
    }

    public void setMaxHeightRatio(float ratio) {
        if (ratio <= 1.0f && ratio >= 0.0f) {
            this.mMaxHeightRatio = ratio;
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sceenHeight = getScreenHeight(getContext());
        int absScreenWidth = getAbsScreenWidth();
        float calHeight = this.mMaxHeightRatio * ((float) sceenHeight);
        if (calHeight < this.mSplitMaxHeight) {
            calHeight = this.mSplitMaxHeight;
        }
        this.mMaxHeight = calHeight;
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (widthSize > absScreenWidth) {
            widthSize = absScreenWidth - (getDensity() * 46);
        }
        if (((float) heightSize) > this.mMaxHeight) {
            heightSize = (int) this.mMaxHeight;
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(widthSize, widthMode), MeasureSpec.makeMeasureSpec(heightSize, heightMode));
    }

    private boolean isLandScreen() {
        return getResources().getConfiguration().orientation == 2;
    }

    private int getScreenHeight(Context context) {
        return ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getHeight();
    }

    private int getScreenWidth(Context context) {
        return ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getWidth();
    }

    private int getAbsScreenWidth() {
        Point size = new Point();
        getDisplay().getSize(size);
        return size.x;
    }

    private int getDensity() {
        return getResources().getDisplayMetrics().densityDpi / 160;
    }
}
