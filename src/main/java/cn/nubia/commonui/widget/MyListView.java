package cn.nubia.commonui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.ListView;
import cn.nubia.commonui.R;

public class MyListView extends ListView {
    private float mMaxHeight;

    public MyListView(Context context) {
        this(context, null);
    }

    public MyListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mMaxHeight = (float) getResources().getDimensionPixelSize(R.dimen.nubia_character_list_view_max_height);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec((int) (((float) heightSize) < this.mMaxHeight ? (float) heightSize : this.mMaxHeight), heightMode));
    }
}
