package cn.nubia.gamelauncherx.recycler;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import cn.nubia.gamelauncherx.util.LogUtil;

public class BannerRecyclerView extends RecyclerView {
    int mBannerWidth;
    LooperLayoutManager mManager;
    ScrollHelper mScrollHelper;
    ScrollListener mScrollListener;
    int mScrolledX = 0;

    class ScrollListener extends OnScrollListener {
        ScrollListener() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            switch (newState) {
                case 0:
                    BannerRecyclerView.this.mScrolledX = 0;
                    return;
                default:
                    return;
            }
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            BannerRecyclerView.this.mScrolledX += dx;
        }
    }

    public BannerRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public BannerRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BannerRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        if (this.mScrollHelper == null) {
            this.mScrollHelper = new ScrollHelper(context);
        }
        if (this.mScrollListener == null) {
            this.mScrollListener = new ScrollListener();
        }
        addOnScrollListener(this.mScrollListener);
    }

    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        try {
            return super.drawChild(canvas, child, drawingTime);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.w("BannerRecyclerView", "drawChild Exception e = " + e);
            return false;
        }
    }

    public boolean fling(int velocityX, int velocityY) {
        return super.fling(prooFreading(velocityX), velocityY);
    }

    private int prooFreading(int velocity) {
        if (!isCarouselLayoutManager().booleanValue()) {
            return velocity;
        }
        updateBannerWidth();
        int hasScrolledOffset = Math.abs(this.mScrolledX % this.mBannerWidth);
        int dx = ((int) this.mScrollHelper.getSplineFlingDistance(velocity)) + hasScrolledOffset;
        if (dx < getHalfBannerWidth()) {
            return velocity;
        }
        int count = dx / this.mBannerWidth;
        if (((double) (dx % this.mBannerWidth)) > ((double) getHalfBannerWidth())) {
            count++;
        }
        return this.mScrollHelper.getVelocityByDistance((double) (((float) ((this.mBannerWidth * Math.min(count, 18)) - hasScrolledOffset)) * Math.signum((float) velocity)));
    }

    private Boolean isCarouselLayoutManager() {
        if (getLayoutManager() == null || !(getLayoutManager() instanceof LooperLayoutManager)) {
            return Boolean.valueOf(false);
        }
        this.mManager = (LooperLayoutManager) getLayoutManager();
        return Boolean.valueOf(true);
    }

    private void updateBannerWidth() {
        if (this.mBannerWidth <= 0) {
            this.mBannerWidth = this.mManager.getScrollItemSize();
        }
    }

    private int getHalfBannerWidth() {
        return this.mBannerWidth / 2;
    }
}
