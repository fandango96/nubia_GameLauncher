package cn.nubia.commonui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import cn.nubia.commonui.R;
import cn.nubia.commonui.ReflectUtils;

public class MorePopupRelativeLayout extends RelativeLayout {
    private int mDistance;
    private double mDownPointX = 0.0d;
    private double mDownPointY = 0.0d;
    private int mHistoryPointCount = 0;
    private NubiaMorePopup mNubiaMorePopup;
    private PopupWindow mPopup;
    private boolean mTag;

    public MorePopupRelativeLayout(Context context) {
        super(context);
        initDistance(context);
    }

    public MorePopupRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDistance(context);
    }

    public void setPopupWindow(PopupWindow pop) {
        this.mPopup = pop;
    }

    public void setNubiaMorePopup(NubiaMorePopup nubiaPop) {
        this.mNubiaMorePopup = nubiaPop;
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (event.getPointerCount() == 3) {
            Object capture = ReflectUtils.invoke("android.os.SystemProperties", "getBoolean", true, true, new Object[]{"persist.sys.gesture.capture", Boolean.valueOf(false)}, String.class, Boolean.TYPE);
            if (capture == null ? false : ((Boolean) capture).booleanValue()) {
                return true;
            }
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        boolean inScope;
        switch (ev.getActionMasked()) {
            case 0:
                this.mDownPointX = (double) ev.getRawX();
                this.mDownPointY = (double) ev.getRawY();
                return true;
            case 1:
                if (Math.abs(((double) ev.getRawX()) - this.mDownPointX) >= ((double) this.mDistance) || Math.abs(((double) ev.getRawY()) - this.mDownPointY) >= ((double) this.mDistance)) {
                    inScope = false;
                } else {
                    inScope = true;
                }
                this.mHistoryPointCount++;
                if (this.mHistoryPointCount > 1) {
                    this.mHistoryPointCount = 0;
                    break;
                } else {
                    if (!(this.mPopup == null || this.mNubiaMorePopup == null || !inScope)) {
                        this.mNubiaMorePopup.startExitAnimation(this.mPopup);
                    }
                    this.mHistoryPointCount = 0;
                    return true;
                }
            case 6:
                this.mHistoryPointCount++;
                return true;
        }
        return false;
    }

    private void initDistance(Context context) {
        this.mDistance = context.getResources().getDimensionPixelSize(R.dimen.nubia_more_popup_distance);
    }
}
