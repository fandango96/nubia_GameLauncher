package cn.nubia.commonui.widget;

import android.content.Context;
import android.preference.PreferenceGroup;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import cn.nubia.commonui.R;

public class NubiaPreferenceCategory extends PreferenceGroup {
    private LayoutInflater mInflater;
    private LinearLayout mRightParent;
    private OnClickListener mRightParentListener;
    private int mRightWidget;
    private View mRightWidgetView;

    public NubiaPreferenceCategory(Context context) {
        this(context, null);
    }

    public NubiaPreferenceCategory(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NubiaPreferenceCategory(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mRightWidget = -1;
        this.mRightWidgetView = null;
        this.mRightParent = null;
        this.mInflater = null;
        Context context2 = getContext();
        getContext();
        this.mInflater = (LayoutInflater) context2.getSystemService("layout_inflater");
        setSelectable(false);
        setLayoutResource(R.layout.nubia_preferencecategory);
    }

    /* access modifiers changed from: protected */
    public void onBindView(View view) {
        super.onBindView(view);
        this.mRightParent = (LinearLayout) view.findViewById(R.id.nubia_preference_category_right_layout);
        if (this.mRightParent != null) {
            bindRightWidget();
            this.mRightParent.setOnClickListener(this.mRightParentListener);
            view.findViewById(R.id.line).setVisibility(8);
            view.findViewById(R.id.top_divider).setVisibility(0);
        }
    }

    public void setRightLayout(int rigthWidget, OnClickListener listener) {
        this.mRightWidget = rigthWidget;
        this.mRightParentListener = listener;
    }

    public void setRightView(View view, OnClickListener listener) {
        this.mRightWidgetView = view;
        this.mRightParentListener = listener;
    }

    private void bindRightWidget() {
        if (this.mRightWidgetView != null) {
            ViewGroup parent = (ViewGroup) this.mRightWidgetView.getParent();
            if (parent != null) {
                parent.removeAllViews();
            }
            this.mRightParent.addView(this.mRightWidgetView);
        } else if (this.mRightWidget >= 0) {
            this.mInflater.inflate(this.mRightWidget, this.mRightParent, true);
        }
    }
}
