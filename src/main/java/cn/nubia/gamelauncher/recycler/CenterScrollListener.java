package cn.nubia.gamelauncher.recycler;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import cn.nubia.gamelauncher.recycler.LooperLayoutManager.ScrollState;
import cn.nubia.gamelauncher.util.LogUtil;

public class CenterScrollListener extends OnScrollListener {
    private boolean mAutoSet = true;

    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (!(layoutManager instanceof LooperLayoutManager)) {
            this.mAutoSet = true;
            return;
        }
        LooperLayoutManager lm = (LooperLayoutManager) layoutManager;
        switchSmoothScrollStateIfNeed(newState, lm);
        smoothScrollTheCenterItemToMiddleIfNeed(recyclerView, newState, lm);
        if (1 == newState || 2 == newState) {
            this.mAutoSet = false;
        }
    }

    private void smoothScrollTheCenterItemToMiddleIfNeed(RecyclerView recyclerView, int newState, LooperLayoutManager lm) {
        if (!this.mAutoSet && newState == 0) {
            int scrollNeeded = lm.getOffsetCenterView();
            LogUtil.d("scroll", "smoothScrollTheCenterItemToMiddleIfNeed() scrollNeeded = " + scrollNeeded);
            if (lm.getOrientation() == 0) {
                recyclerView.smoothScrollBy(scrollNeeded, 0, Anim3DHelper.PATH_INTERPOLATOR_CARD_REBOUND);
                if (scrollNeeded == 0) {
                    lm.scrollStateChanged(false, ScrollState.IDLE);
                } else {
                    lm.scrollStateChanged(false, ScrollState.REBOUNDING);
                }
            } else {
                recyclerView.smoothScrollBy(0, scrollNeeded);
            }
            this.mAutoSet = true;
        }
    }

    private void switchSmoothScrollStateIfNeed(int newState, LooperLayoutManager manager) {
        if (this.mAutoSet && (1 == newState || 2 == newState)) {
            manager.scrollStateChanged(true, ScrollState.SCROLLING);
        } else if (this.mAutoSet && newState == 0) {
            manager.scrollStateChanged(false, ScrollState.IDLE);
        }
    }
}
