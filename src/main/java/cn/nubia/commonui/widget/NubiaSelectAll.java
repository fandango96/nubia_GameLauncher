package cn.nubia.commonui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.CompoundButton;
import cn.nubia.commonui.R;

public class NubiaSelectAll extends CompoundButton {
    public NubiaSelectAll(Context context) {
        this(context, null);
    }

    public NubiaSelectAll(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.nubiaSelectAllStyle);
    }

    public NubiaSelectAll(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public NubiaSelectAll(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(NubiaSelectAll.class.getName());
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(NubiaSelectAll.class.getName());
    }
}
