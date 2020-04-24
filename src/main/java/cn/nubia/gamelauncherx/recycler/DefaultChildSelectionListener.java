package cn.nubia.gamelauncherx.recycler;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class DefaultChildSelectionListener extends LooperSelectionListener {
    @NonNull
    private final OnCenterItemClickListener mOnCenterItemClickListener;

    public interface OnCenterItemClickListener {
        void onBackItemClicked(@NonNull View view);

        void onCenterItemClicked(@NonNull RecyclerView recyclerView, @NonNull LooperLayoutManager looperLayoutManager, @NonNull View view);
    }

    protected DefaultChildSelectionListener(@NonNull OnCenterItemClickListener onCenterItemClickListener, @NonNull RecyclerView recyclerView, @NonNull LooperLayoutManager carouselLayoutManager) {
        super(recyclerView, carouselLayoutManager);
        this.mOnCenterItemClickListener = onCenterItemClickListener;
    }

    /* access modifiers changed from: protected */
    public void onCenterItemClicked(@NonNull RecyclerView recyclerView, @NonNull LooperLayoutManager carouselLayoutManager, @NonNull View v) {
        this.mOnCenterItemClickListener.onCenterItemClicked(recyclerView, carouselLayoutManager, v);
    }

    /* access modifiers changed from: protected */
    public void onBackItemClicked(@NonNull RecyclerView recyclerView, @NonNull LooperLayoutManager carouselLayoutManager, @NonNull View v) {
        BannerViewHolder holder = carouselLayoutManager.getViewHolderByPosition(carouselLayoutManager.getCenterItemPosition());
        if (holder != null && holder.mModifyAtmosphere != null && !holder.mModifyAtmosphere.isShown()) {
            recyclerView.smoothScrollToPosition(carouselLayoutManager.getPosition(v));
            this.mOnCenterItemClickListener.onBackItemClicked(v);
        }
    }

    public static DefaultChildSelectionListener initCenterItemListener(@NonNull OnCenterItemClickListener onCenterItemClickListener, @NonNull RecyclerView recyclerView, @NonNull LooperLayoutManager carouselLayoutManager) {
        return new DefaultChildSelectionListener(onCenterItemClickListener, recyclerView, carouselLayoutManager);
    }
}
