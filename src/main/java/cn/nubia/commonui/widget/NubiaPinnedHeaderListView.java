package cn.nubia.commonui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

public class NubiaPinnedHeaderListView extends ListView {
    private static final int MAX_ALPHA = 255;
    private NubiaPinnedHeaderAdapter mAdapter;
    private View mHeaderView;
    private int mHeaderViewHeight;
    private boolean mHeaderViewVisible;
    private int mHeaderViewWidth;

    public interface NubiaPinnedHeaderAdapter {
        public static final int PINNED_HEADER_GONE = 0;
        public static final int PINNED_HEADER_PUSHED_UP = 2;
        public static final int PINNED_HEADER_VISIBLE = 1;

        void configurePinnedHeader(View view, int i, int i2);

        int getPinnedHeaderState(int i);
    }

    public NubiaPinnedHeaderListView(Context context) {
        super(context);
    }

    public NubiaPinnedHeaderListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NubiaPinnedHeaderListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (this.mHeaderView != null) {
            this.mHeaderView.layout(0, 0, this.mHeaderViewWidth, this.mHeaderViewHeight);
            configureHeaderView(getFirstVisiblePosition());
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.mHeaderView != null) {
            measureChild(this.mHeaderView, widthMeasureSpec, heightMeasureSpec);
            this.mHeaderViewWidth = this.mHeaderView.getMeasuredWidth();
            this.mHeaderViewHeight = this.mHeaderView.getMeasuredHeight();
        }
    }

    public void setPinnedHeaderView(View view) {
        this.mHeaderView = view;
        if (this.mHeaderView != null) {
            setFadingEdgeLength(0);
        }
        requestLayout();
    }

    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        this.mAdapter = (NubiaPinnedHeaderAdapter) adapter;
    }

    public void configureHeaderView(int position) {
        int y;
        int alpha;
        if (this.mHeaderView != null) {
            switch (this.mAdapter.getPinnedHeaderState(position)) {
                case 0:
                    this.mHeaderViewVisible = false;
                    return;
                case 1:
                    this.mAdapter.configurePinnedHeader(this.mHeaderView, position, 255);
                    if (this.mHeaderView.getTop() != 0) {
                        this.mHeaderView.layout(0, 0, this.mHeaderViewWidth, this.mHeaderViewHeight);
                    }
                    this.mHeaderViewVisible = true;
                    return;
                case 2:
                    int bottom = getChildAt(0).getBottom();
                    int headerHeight = this.mHeaderView.getHeight();
                    if (bottom < headerHeight) {
                        y = bottom - headerHeight;
                        alpha = ((headerHeight + y) * 255) / headerHeight;
                    } else {
                        y = 0;
                        alpha = 255;
                    }
                    this.mAdapter.configurePinnedHeader(this.mHeaderView, position, alpha);
                    if (this.mHeaderView.getTop() != y) {
                        this.mHeaderView.layout(0, y, this.mHeaderViewWidth, this.mHeaderViewHeight + y);
                    }
                    this.mHeaderViewVisible = true;
                    return;
                default:
                    return;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.mHeaderViewVisible) {
            drawChild(canvas, this.mHeaderView, getDrawingTime());
        }
    }
}
