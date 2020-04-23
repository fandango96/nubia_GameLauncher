package cn.nubia.gamelauncher.recycler;

import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnChildAttachStateChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;

public abstract class LooperSelectionListener {
    /* access modifiers changed from: private */
    @NonNull
    public final LooperLayoutManager mCarouselLayoutManager;
    /* access modifiers changed from: private */
    public int mEventDownY = 0;
    /* access modifiers changed from: private */
    public boolean mMoreOptionsVisible = false;
    /* access modifiers changed from: private */
    public final OnTouchListener mOnTouchListener = new OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            View clickView;
            int clickItemPosition;
            if (LooperSelectionListener.this.mRecyclerView != null && LooperSelectionListener.this.mRecyclerView.getScrollState() != 0) {
                return false;
            }
            if (event.getAction() == 0) {
                LooperSelectionListener.this.mEventDownY = (int) event.getRawY();
            }
            if (event.getAction() != 1) {
                return true;
            }
            if (event.getAction() == 1 && Math.abs(((int) event.getRawY()) - LooperSelectionListener.this.mEventDownY) > 20) {
                return false;
            }
            BannerViewHolder holder = (BannerViewHolder) LooperSelectionListener.this.mRecyclerView.getChildViewHolder(v);
            int position = holder.getAdapterPosition();
            if (LooperSelectionListener.this.isEventInTheActiveArea((View) holder.mCardView, event)) {
                clickView = v;
                clickItemPosition = position;
            } else if (LooperSelectionListener.this.isEventInTheActiveArea(position - 1, event)) {
                clickView = LooperSelectionListener.this.mRecyclerView.findViewHolderForAdapterPosition(position - 1).itemView;
                clickItemPosition = position - 1;
            } else if (!LooperSelectionListener.this.isEventInTheActiveArea(position + 1, event)) {
                return false;
            } else {
                clickView = LooperSelectionListener.this.mRecyclerView.findViewHolderForAdapterPosition(position + 1).itemView;
                clickItemPosition = position + 1;
            }
            doClick(clickItemPosition, clickView, event);
            return false;
        }

        private void doClick(int position, View v, MotionEvent event) {
            int i = 8;
            boolean z = false;
            if (position != LooperSelectionListener.this.mCarouselLayoutManager.getCenterItemPosition()) {
                LooperSelectionListener.this.onBackItemClicked(LooperSelectionListener.this.mRecyclerView, LooperSelectionListener.this.mCarouselLayoutManager, v);
            } else if (LooperSelectionListener.this.isEventInMoreSelectArea(position, event)) {
                BannerViewHolder holder = (BannerViewHolder) LooperSelectionListener.this.mRecyclerView.findViewHolderForAdapterPosition(position);
                if (holder.mMoreOptions.getAlpha() >= 1.0f && holder.mMoreOptions.getVisibility() == 0) {
                    FrameLayout frameLayout = holder.mMoreOptionsList;
                    if (holder.mMoreOptionsList.getVisibility() == 8) {
                        i = 0;
                    }
                    frameLayout.setVisibility(i);
                    LooperSelectionListener looperSelectionListener = LooperSelectionListener.this;
                    if (holder.mMoreOptionsList.getVisibility() == 0) {
                        z = true;
                    }
                    looperSelectionListener.mMoreOptionsVisible = z;
                }
            } else {
                LooperSelectionListener.this.onCenterItemClicked(LooperSelectionListener.this.mRecyclerView, LooperSelectionListener.this.mCarouselLayoutManager, v);
            }
        }
    };
    /* access modifiers changed from: private */
    @NonNull
    public final RecyclerView mRecyclerView;

    /* access modifiers changed from: protected */
    public abstract void onBackItemClicked(@NonNull RecyclerView recyclerView, @NonNull LooperLayoutManager looperLayoutManager, @NonNull View view);

    /* access modifiers changed from: protected */
    public abstract void onCenterItemClicked(@NonNull RecyclerView recyclerView, @NonNull LooperLayoutManager looperLayoutManager, @NonNull View view);

    /* access modifiers changed from: private */
    public boolean isEventInMoreSelectArea(int position, MotionEvent event) {
        BannerViewHolder holder = (BannerViewHolder) this.mRecyclerView.findViewHolderForAdapterPosition(position);
        if (holder == null) {
            return false;
        }
        return isEventInTheActiveArea((View) holder.mMoreOptions, event);
    }

    /* access modifiers changed from: private */
    public boolean isEventInTheActiveArea(int position, MotionEvent event) {
        BannerViewHolder holder = (BannerViewHolder) this.mRecyclerView.findViewHolderForAdapterPosition(position);
        if (holder == null) {
            return false;
        }
        return isEventInTheActiveArea((View) holder.mCardView, event);
    }

    /* access modifiers changed from: private */
    @NonNull
    public boolean isEventInTheActiveArea(View v, MotionEvent event) {
        if (v == null) {
            return false;
        }
        int moveAlongX = (int) (((float) this.mCarouselLayoutManager.getCardMoveAlongX()) * ((View) v.getParent()).getScaleX());
        Rect rect = new Rect();
        v.getGlobalVisibleRect(rect);
        Point p = new Point();
        p.x = (int) event.getRawX();
        p.y = (int) event.getRawY();
        Point centerP = new Point();
        centerP.x = (v.getLeft() + v.getRight()) / 2;
        centerP.y = (v.getTop() + v.getBottom()) / 2;
        Point[] quadrangle = {new Point(), new Point(), new Point(), new Point()};
        quadrangle[0].x = rect.left + moveAlongX;
        quadrangle[0].y = rect.top;
        quadrangle[1].x = rect.right;
        quadrangle[1].y = rect.top;
        quadrangle[2].x = rect.right - moveAlongX;
        quadrangle[2].y = rect.bottom;
        quadrangle[3].x = rect.left;
        quadrangle[3].y = rect.bottom;
        return QuadrangleHelper.isPointInQuadrangle(quadrangle, p);
    }

    protected LooperSelectionListener(@NonNull RecyclerView recyclerView, @NonNull LooperLayoutManager carouselLayoutManager) {
        this.mRecyclerView = recyclerView;
        this.mCarouselLayoutManager = carouselLayoutManager;
        this.mRecyclerView.addOnChildAttachStateChangeListener(new OnChildAttachStateChangeListener() {
            public void onChildViewAttachedToWindow(View view) {
                view.setOnTouchListener(LooperSelectionListener.this.mOnTouchListener);
            }

            public void onChildViewDetachedFromWindow(View view) {
                view.setOnTouchListener(null);
            }
        });
    }
}
