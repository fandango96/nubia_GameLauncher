package cn.nubia.commonui.app;

import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import cn.nubia.commonui.R;
import cn.nubia.commonui.ReflectUtils;

public class Dialog extends android.app.Dialog {
    private double downPointX;
    private double downPointY;
    private boolean mCanceledOnOutside;
    private int mDistance;
    private int mHistoryPointerCount;

    public Dialog(Context context) {
        this(context, 0, true);
    }

    protected Dialog(Context context, int theme, boolean createContextThemeWrapper) {
        this(context, theme);
        this.mDistance = context.getResources().getDimensionPixelSize(R.dimen.nubia_more_popup_distance);
    }

    public Dialog(Context context, int theme) {
        super(context, theme);
        this.mHistoryPointerCount = 0;
        this.downPointX = 0.0d;
        this.downPointY = 0.0d;
    }

    protected Dialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mHistoryPointerCount = 0;
        this.downPointX = 0.0d;
        this.downPointY = 0.0d;
    }

    public boolean onTouchEvent(MotionEvent event) {
        return nubiaTouchEvent(event);
    }

    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
        this.mCanceledOnOutside = cancel;
    }

    private boolean nubiaTouchEvent(MotionEvent event) {
        boolean inScope;
        switch (event.getActionMasked()) {
            case 0:
                this.downPointX = (double) event.getRawX();
                this.downPointY = (double) event.getRawY();
                break;
            case 1:
                if (Math.abs(((double) event.getRawX()) - this.downPointX) >= ((double) this.mDistance) || Math.abs(((double) event.getRawY()) - this.downPointY) >= ((double) this.mDistance)) {
                    inScope = false;
                } else {
                    inScope = true;
                }
                this.mHistoryPointerCount++;
                if (this.mHistoryPointerCount <= 1) {
                    Object mResult = ReflectUtils.getValueByName(this, "mCancelable");
                    boolean mFlag = mResult == null ? true : ((Boolean) mResult).booleanValue();
                    if (inScope && mFlag && isShowing() && shouldCloseOnTouchOfNubia(getContext(), event)) {
                        this.mHistoryPointerCount = 0;
                        cancel();
                        return true;
                    }
                }
                this.mHistoryPointerCount = 0;
                break;
            case 6:
                this.mHistoryPointerCount++;
                break;
        }
        return false;
    }

    private boolean shouldCloseOnTouchOfNubia(Context context, MotionEvent event) {
        if (!this.mCanceledOnOutside || event.getAction() != 1 || getWindow().getDecorView() == null || !isOutOfBounds(context, event)) {
            return false;
        }
        return true;
    }

    private boolean isOutOfBounds(Context context, MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int slop = ViewConfiguration.get(context).getScaledWindowTouchSlop();
        View decorView = getWindow().getDecorView();
        return x < (-slop) || y < (-slop) || x > decorView.getWidth() + slop || y > decorView.getHeight() + slop;
    }
}
