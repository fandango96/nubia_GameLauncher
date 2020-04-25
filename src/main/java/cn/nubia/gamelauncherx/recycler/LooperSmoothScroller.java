package cn.nubia.gamelauncherx.recycler;

import android.graphics.PointF;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LooperSmoothScroller {
    public LooperSmoothScroller(@NonNull RecyclerView.State state, int position) {
        if (position < 0) {
            throw new IllegalArgumentException("position can't be less then 0. position is : " + position);
        } else if (position >= state.getItemCount()) {
            throw new IllegalArgumentException("position can't be great then adapter items count. position is : " + position);
        }
    }

    public PointF computeScrollVectorForPosition(int targetPosition, @NonNull LooperLayoutManager carouselLayoutManager) {
        return carouselLayoutManager.computeScrollVectorForPosition(targetPosition);
    }

    public int calculateDyToMakeVisible(View view, @NonNull LooperLayoutManager carouselLayoutManager) {
        if (!carouselLayoutManager.canScrollVertically()) {
            return 0;
        }
        return carouselLayoutManager.getOffsetForCurrentView(view);
    }

    public int calculateDxToMakeVisible(View view, @NonNull LooperLayoutManager carouselLayoutManager) {
        if (!carouselLayoutManager.canScrollHorizontally()) {
            return 0;
        }
        return carouselLayoutManager.getOffsetForCurrentView(view);
    }
}
