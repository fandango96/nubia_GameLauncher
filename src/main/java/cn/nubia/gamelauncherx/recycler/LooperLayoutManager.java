package cn.nubia.gamelauncherx.recycler;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import cn.nubia.gamelauncherx.R;
import cn.nubia.gamelauncherx.util.LogUtil;
import cn.nubia.gamelauncherx.view.BannerView;
import cn.nubia.gamelauncherx.view.DrawableLeftTextView;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LooperLayoutManager extends RecyclerView.LayoutManager implements RecyclerView.SmoothScroller.ScrollVectorProvider
{
    private static final boolean CIRCLE_LAYOUT = false;
    public static final int HORIZONTAL = 0;
    public static final int INVALID_POSITION = -1;
    public static final int MAX_VISIBLE_ITEMS = 1;
    private static final String TAG = "CLManager";
    public static final int VERTICAL = 1;
    private float REMOVE_INTERPOLATOR_THRESHOLD;
    private final int TEXT_SHOW_ANIM_DURATION;
    private Integer mAdjacentCardSpace;
    private Integer mCardMoveAlongX;
    private int mCenterItemPosition;
    private final boolean mCircleLayout;
    private Context mContext;
    private Integer mDecoratedChildHeight;
    private boolean mDecoratedChildSizeInvalid;
    private Integer mDecoratedChildWidth;
    private Integer mEdgeTransparentAreaWidth;
    private List<String> mExcludeList;
    private boolean mIsNeedPathInterpolator;
    private boolean mIsScrolling;
    private boolean mIsVisibility;
    private int mItemsCount;
    private final LayoutHelper mLayoutHelper;
    private float mMillisecondsPerPixel;
    private final List<OnCenterItemSelectionListener> mOnCenterItemSelectionListeners;
    private final int mOrientation;
    @Nullable
    private CarouselSavedState mPendingCarouselSavedState;
    private int mPendingScrollPosition;
    private float mScrollStartDirection;
    private float mScrollStartPosition;
    ScrollState mScrollState;
    private boolean mStartAnimEnd;
    private PostLayoutListener mViewPostLayout;

    protected static class CarouselSavedState implements Parcelable {
        public static final Creator<CarouselSavedState> CREATOR = new Creator<CarouselSavedState>() {
            public CarouselSavedState createFromParcel(Parcel parcel) {
                return new CarouselSavedState(parcel);
            }

            public CarouselSavedState[] newArray(int i) {
                return new CarouselSavedState[i];
            }
        };
        /* access modifiers changed from: private */
        public int mCenterItemPosition;
        /* access modifiers changed from: private */
        public final Parcelable mSuperState;

        protected CarouselSavedState(@Nullable Parcelable superState) {
            this.mSuperState = superState;
        }

        private CarouselSavedState(@NonNull Parcel in) {
            this.mSuperState = in.readParcelable(Parcelable.class.getClassLoader());
            this.mCenterItemPosition = in.readInt();
        }

        protected CarouselSavedState(@NonNull CarouselSavedState other) {
            this.mSuperState = other.mSuperState;
            this.mCenterItemPosition = other.mCenterItemPosition;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeParcelable(this.mSuperState, i);
            parcel.writeInt(this.mCenterItemPosition);
        }
    }

    private static class LayoutHelper {
        /* access modifiers changed from: private */
        public LayoutOrder[] mLayoutOrder;
        /* access modifiers changed from: private */
        public int mMaxVisibleItems;
        private final List<WeakReference<LayoutOrder>> mReusedItems = new ArrayList();
        /* access modifiers changed from: private */
        public int mScrollOffset;

        LayoutHelper(int maxVisibleItems) {
            this.mMaxVisibleItems = maxVisibleItems;
        }

        /* access modifiers changed from: 0000 */
        public void initLayoutOrder(int layoutCount) {
            if (this.mLayoutOrder == null || this.mLayoutOrder.length != layoutCount) {
                if (this.mLayoutOrder != null) {
                    recycleItems(this.mLayoutOrder);
                }
                this.mLayoutOrder = new LayoutOrder[layoutCount];
                fillLayoutOrder();
            }
        }

        /* access modifiers changed from: 0000 */
        public void setLayoutOrder(int arrayPosition, int itemAdapterPosition, float itemPositionDiff) {
            LayoutOrder item = this.mLayoutOrder[arrayPosition];
            item.mItemAdapterPosition = itemAdapterPosition;
            item.mItemPositionDiff = itemPositionDiff;
        }

        /* access modifiers changed from: 0000 */
        public boolean hasAdapterPosition(int adapterPosition) {
            if (this.mLayoutOrder == null) {
                return false;
            }
            for (LayoutOrder layoutOrder : this.mLayoutOrder) {
                if (layoutOrder.mItemAdapterPosition == adapterPosition) {
                    return true;
                }
            }
            return false;
        }

        private void recycleItems(@NonNull LayoutOrder... layoutOrders) {
            for (LayoutOrder layoutOrder : layoutOrders) {
                this.mReusedItems.add(new WeakReference(layoutOrder));
            }
        }

        private void fillLayoutOrder() {
            int length = this.mLayoutOrder.length;
            for (int i = 0; i < length; i++) {
                if (this.mLayoutOrder[i] == null) {
                    this.mLayoutOrder[i] = createLayoutOrder();
                }
            }
        }

        private LayoutOrder createLayoutOrder() {
            Iterator<WeakReference<LayoutOrder>> iterator = this.mReusedItems.iterator();
            while (iterator.hasNext()) {
                LayoutOrder layoutOrder = (LayoutOrder) ((WeakReference) iterator.next()).get();
                iterator.remove();
                if (layoutOrder != null) {
                    return layoutOrder;
                }
            }
            return new LayoutOrder();
        }
    }

    private static class LayoutOrder {
        /* access modifiers changed from: private */
        public int mItemAdapterPosition;
        /* access modifiers changed from: private */
        public float mItemPositionDiff;
        private float mTranslationX;

        private LayoutOrder() {
        }
    }

    public interface OnCenterItemSelectionListener {
        void onCenterItemChanged(int i);
    }

    public interface PostLayoutListener {
        ItemTransformation transformChild(@NonNull View view, float f, int i, boolean z, boolean z2);
    }

    public enum ScrollState {
        IDLE,
        SCROLLING,
        REBOUNDING
    }

    public float getMillisecondsPerPixel() {
        return this.mMillisecondsPerPixel;
    }

    public void setMillisecondePerPixel(float milliseconds) {
        this.mMillisecondsPerPixel = milliseconds;
    }

    public LooperLayoutManager(Context context, int orientation) {
        this(context, orientation, false);
    }

    public LooperLayoutManager(final Context context, int orientation, boolean circleLayout) {
        this.mScrollState = ScrollState.IDLE;
        this.mEdgeTransparentAreaWidth = Integer.valueOf(0);
        this.mCardMoveAlongX = Integer.valueOf(0);
        this.mAdjacentCardSpace = Integer.valueOf(0);
        this.mLayoutHelper = new LayoutHelper(1);
        this.mOnCenterItemSelectionListeners = new ArrayList();
        this.mCenterItemPosition = -1;
        this.mScrollStartPosition = -1.0f;
        this.mScrollStartDirection = 0.0f;
        this.mIsNeedPathInterpolator = true;
        this.mIsScrolling = false;
        this.mStartAnimEnd = true;
        this.mIsVisibility = false;
        this.TEXT_SHOW_ANIM_DURATION = 285;
        this.mMillisecondsPerPixel = 0.045f;
        this.REMOVE_INTERPOLATOR_THRESHOLD = 0.2f;
        if (orientation == 0 || 1 == orientation) {
            this.mOrientation = orientation;
            this.mCircleLayout = circleLayout;
            this.mPendingScrollPosition = -1;
            this.mContext = context;
            this.mExcludeList = new ArrayList<String>() {
                {
                    add(context.getResources().getString(R.string.add_game));
                    add(context.getResources().getString(R.string.classic_masterpiece));
                    add(context.getResources().getString(R.string.minority_boutique));
                }
            };
            return;
        }
        throw new IllegalArgumentException("orientation should be HORIZONTAL or VERTICAL");
    }

    public void setPostLayoutListener(@Nullable PostLayoutListener postLayoutListener) {
        this.mViewPostLayout = postLayoutListener;
        requestLayout();
    }

    @CallSuper
    public void setMaxVisibleItems(int maxVisibleItems) {
        if (maxVisibleItems <= 0) {
            throw new IllegalArgumentException("maxVisibleItems can't be less then 1");
        }
        this.mLayoutHelper.mMaxVisibleItems = maxVisibleItems;
        requestLayout();
    }

    public int getMaxVisibleItems() {
        return this.mLayoutHelper.mMaxVisibleItems;
    }

    public int getAllVisibleItemsCount() {
        return ((getMaxVisibleItems() + 1) * 2) + 1;
    }

    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(-2, -2);
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    public boolean canScrollHorizontally() {
        if (!isRecyclerviewCanScroll() && getChildCount() != 0 && this.mOrientation == 0) {
            return true;
        }
        return false;
    }

    public boolean canScrollVertically() {
        boolean z = true;
        if (isRecyclerviewCanScroll()) {
            return false;
        }
        if (getChildCount() == 0 || 1 != this.mOrientation) {
            z = false;
        }
        return z;
    }

    private boolean isRecyclerviewCanScroll() {
        int centerPosition = getCenterItemPosition();
        if (centerPosition == -1) {
            centerPosition = this.mPendingScrollPosition;
        }
        BannerViewHolder holder = getViewHolderByPosition(centerPosition);
        if (holder == null || !holder.mModifyAtmosphere.isShown()) {
            return false;
        }
        return true;
    }

    public int getCenterItemPosition() {
        return this.mCenterItemPosition;
    }

    public void addOnItemSelectionListener(@NonNull OnCenterItemSelectionListener onCenterItemSelectionListener) {
        this.mOnCenterItemSelectionListeners.add(onCenterItemSelectionListener);
    }

    public void removeOnItemSelectionListener(@NonNull OnCenterItemSelectionListener onCenterItemSelectionListener) {
        this.mOnCenterItemSelectionListeners.remove(onCenterItemSelectionListener);
    }

    public void scrollToPosition(int position) {
        if (position < 0) {
            throw new IllegalArgumentException("position can't be less then 0. position is : " + position);
        }
        this.mPendingScrollPosition = position;
        requestLayout();
    }

    public void smoothScrollToPosition(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.State state, int position) {
        LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
            public int calculateDyToMakeVisible(View view, int snapPreference) {
                if (!LooperLayoutManager.this.canScrollVertically()) {
                    return 0;
                }
                return LooperLayoutManager.this.getOffsetForCurrentView(view);
            }

            public int calculateDxToMakeVisible(View view, int snapPreference) {
                if (!LooperLayoutManager.this.canScrollHorizontally()) {
                    return 0;
                }
                return LooperLayoutManager.this.getOffsetForCurrentView(view);
            }

            /* access modifiers changed from: protected */
            public float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return displayMetrics.density * LooperLayoutManager.this.getMillisecondsPerPixel();
            }
        };
        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }

    @Nullable
    public PointF computeScrollVectorForPosition(int targetPosition) {
        if (getChildCount() == 0) {
            return null;
        }
        int direction = (int) (-Math.signum(getScrollDirection(targetPosition)));
        if (this.mOrientation == 0) {
            return new PointF((float) direction, 0.0f);
        }
        return new PointF(0.0f, (float) direction);
    }

    private float getScrollDirection(int targetPosition) {
        float currentScrollPosition = makeScrollPositionInRange0ToCount(getCurrentScrollPosition(), this.mItemsCount);
        if (!this.mCircleLayout) {
            return currentScrollPosition - ((float) targetPosition);
        }
        float t1 = currentScrollPosition - ((float) targetPosition);
        float t2 = Math.abs(t1) - ((float) this.mItemsCount);
        if (Math.abs(t1) > Math.abs(t2)) {
            return Math.signum(t1) * t2;
        }
        return t1;
    }

    public int scrollVerticallyBy(int dy, @NonNull RecyclerView.Recycler recycler, @NonNull RecyclerView.State state) {
        if (this.mOrientation == 0) {
            return 0;
        }
        return scrollBy(dy, recycler, state);
    }

    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (1 == this.mOrientation) {
            return 0;
        }
        return scrollBy(dx, recycler, state);
    }

    /* access modifiers changed from: protected */
    @CallSuper
    public int scrollBy(int diff, @NonNull RecyclerView.Recycler recycler, @NonNull RecyclerView.State state) {
        int resultScroll = 0;
        if (!(this.mDecoratedChildWidth == null || this.mDecoratedChildHeight == null || getChildCount() == 0 || diff == 0)) {
            if (this.mCircleLayout) {
                resultScroll = diff;
                LayoutHelper layoutHelper = this.mLayoutHelper;
                layoutHelper.mScrollOffset = layoutHelper.mScrollOffset + resultScroll;
                int maxOffset = getScrollItemSize() * this.mItemsCount;
                while (this.mLayoutHelper.mScrollOffset < 0) {
                    LayoutHelper layoutHelper2 = this.mLayoutHelper;
                    layoutHelper2.mScrollOffset = layoutHelper2.mScrollOffset + maxOffset;
                }
                while (this.mLayoutHelper.mScrollOffset > maxOffset) {
                    LayoutHelper layoutHelper3 = this.mLayoutHelper;
                    layoutHelper3.mScrollOffset = layoutHelper3.mScrollOffset - maxOffset;
                }
                LayoutHelper layoutHelper4 = this.mLayoutHelper;
                layoutHelper4.mScrollOffset = layoutHelper4.mScrollOffset - resultScroll;
            } else {
                int maxOffset2 = getMaxScrollOffset();
                if (this.mLayoutHelper.mScrollOffset + diff < 0) {
                    resultScroll = -this.mLayoutHelper.mScrollOffset;
                } else if (this.mLayoutHelper.mScrollOffset + diff > maxOffset2) {
                    resultScroll = maxOffset2 - this.mLayoutHelper.mScrollOffset;
                } else {
                    resultScroll = diff;
                }
            }
            if (resultScroll != 0) {
                LayoutHelper layoutHelper5 = this.mLayoutHelper;
                layoutHelper5.mScrollOffset = layoutHelper5.mScrollOffset + resultScroll;
                fillData(recycler, state);
            }
        }
        return resultScroll;
    }

    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        this.mDecoratedChildSizeInvalid = true;
        super.onMeasure(recycler, state, widthSpec, heightSpec);
    }

    public void onAdapterChanged(RecyclerView.Adapter oldAdapter, RecyclerView.Adapter newAdapter) {
        super.onAdapterChanged(oldAdapter, newAdapter);
        removeAllViews();
    }

    @CallSuper
    public void onLayoutChildren(@NonNull RecyclerView.Recycler recycler, @NonNull RecyclerView.State state) {
        if (state.getItemCount() == 0) {
            removeAndRecycleAllViews(recycler);
            selectItemCenterPosition(-1);
            return;
        }
        if (this.mDecoratedChildWidth == null || this.mDecoratedChildSizeInvalid) {
            View view = recycler.getViewForPosition(0);
            addView(view);
            measureChildWithMargins(view, 0, 0);
            int decoratedChildWidth = getDecoratedMeasuredWidth(view);
            int decoratedChildHeight = getDecoratedMeasuredHeight(view);
            removeAndRecycleView(view, recycler);
            if (this.mDecoratedChildWidth != null && (!(this.mDecoratedChildWidth.intValue() == decoratedChildWidth && this.mDecoratedChildHeight.intValue() == decoratedChildHeight) && -1 == this.mPendingScrollPosition && this.mPendingCarouselSavedState == null)) {
                this.mPendingScrollPosition = this.mCenterItemPosition;
            }
            this.mDecoratedChildWidth = Integer.valueOf(decoratedChildWidth);
            this.mDecoratedChildHeight = Integer.valueOf(decoratedChildHeight);
            this.mDecoratedChildSizeInvalid = false;
        }
        if (-1 != this.mPendingScrollPosition) {
            int itemsCount = state.getItemCount();
            this.mPendingScrollPosition = itemsCount == 0 ? -1 : Math.max(0, Math.min(itemsCount - 1, this.mPendingScrollPosition));
        }
        if (-1 != this.mPendingScrollPosition) {
            this.mLayoutHelper.mScrollOffset = calculateScrollForSelectingPosition(this.mPendingScrollPosition, state);
            this.mPendingScrollPosition = -1;
            this.mPendingCarouselSavedState = null;
        } else if (this.mPendingCarouselSavedState != null) {
            this.mLayoutHelper.mScrollOffset = calculateScrollForSelectingPosition(this.mPendingCarouselSavedState.mCenterItemPosition, state);
            this.mPendingCarouselSavedState = null;
        } else if (state.didStructureChange() && -1 != this.mCenterItemPosition) {
            this.mLayoutHelper.mScrollOffset = calculateScrollForSelectingPosition(this.mCenterItemPosition, state);
        }
        fillData(recycler, state);
    }

    private int calculateScrollForSelectingPosition(int itemPosition, RecyclerView.State state) {
        return (1 == this.mOrientation ? this.mDecoratedChildHeight : this.mDecoratedChildWidth).intValue() * (itemPosition < state.getItemCount() ? itemPosition : state.getItemCount() - 1);
    }

    private void fillData(@NonNull RecyclerView.Recycler recycler, @NonNull RecyclerView.State state) {
        float currentScrollPosition = getCurrentScrollPosition();
        generateLayoutOrder(currentScrollPosition, state);
        detachAndScrapAttachedViews(recycler);
        recyclerOldViews(recycler);
        fillDataHorizontal(recycler, getWidthNoPadding(), getHeightNoPadding());
        recycler.clear();
        detectOnItemSelectionChanged(currentScrollPosition, state);
    }

    private void detectOnItemSelectionChanged(float currentScrollPosition, RecyclerView.State state) {
        final int centerItem = Math.round(makeScrollPositionInRange0ToCount(currentScrollPosition, state.getItemCount()));
        if (this.mCenterItemPosition != centerItem) {
            if (this.mCenterItemPosition == -1) {
                this.mCenterItemPosition = centerItem;
                doAnimator(false);
            }
            this.mCenterItemPosition = centerItem;
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    LooperLayoutManager.this.selectItemCenterPosition(centerItem);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void selectItemCenterPosition(int centerItem) {
        for (OnCenterItemSelectionListener onCenterItemSelectionListener : this.mOnCenterItemSelectionListeners) {
            onCenterItemSelectionListener.onCenterItemChanged(centerItem);
        }
    }

    private void fillDataHorizontal(RecyclerView.Recycler recycler, int width, int height) {
        int top = (height - this.mDecoratedChildHeight.intValue()) / 2;
        int bottom = top + this.mDecoratedChildHeight.intValue();
        int centerViewStart = (width - this.mDecoratedChildWidth.intValue()) / 2;
        for (int i = this.mLayoutHelper.mLayoutOrder.length - 1; i >= 0; i--) {
            LayoutOrder layoutOrder = this.mLayoutHelper.mLayoutOrder[i];
            int start = centerViewStart + getCardOffsetByPositionDiffAndChildWidth(layoutOrder.mItemPositionDiff);
            fillChildItem(start, top, start + this.mDecoratedChildWidth.intValue(), bottom, layoutOrder, recycler, i);
        }
    }

    private void fillChildItem(int start, int top, int end, int bottom, @NonNull LayoutOrder layoutOrder, @NonNull RecyclerView.Recycler recycler, int i) {
        View view = bindChild(layoutOrder.mItemAdapterPosition, recycler);
        ViewCompat.setElevation(view, (float) i);
        ItemTransformation transformation = null;
        if (this.mViewPostLayout != null) {
            setTheInitialDirectionOfThisScrollIfNeed();
            if (view instanceof BannerView) {
                ((BannerView) view).updateRotate(layoutOrder.mItemPositionDiff);
            }
            transformation = this.mViewPostLayout.transformChild(view, layoutOrder.mItemPositionDiff, this.mOrientation, isNeedPathInterpolator(), getCurrentScrollPosition() > this.mScrollStartPosition);
        }
        if (transformation == null) {
            view.layout(start, top, end, bottom);
            return;
        }
        view.layout(Math.round(((float) start) + transformation.mTranslationX), Math.round(((float) top) + transformation.mTranslationY), Math.round(((float) end) + transformation.mTranslationX), Math.round(((float) bottom) + transformation.mTranslationY));
        view.setTag(transformation);
        if (isStartAnimEnd()) {
            view.setAlpha(transformation.mCardAlpha);
            view.setTranslationY(0.0f);
        }
        BannerViewHolder holder = (BannerViewHolder) ((RecyclerView) view.getParent()).findViewHolderForLayoutPosition(layoutOrder.mItemAdapterPosition);
        if (holder != null) {
            holder.mMaskView.setAlpha(transformation.mViewMaskAlpha);
            if (transformation.mTextAlpha <= 0.0f) {
                holder.mShadowView.setAlpha(0.0f);
                holder.mGameNameView.setAlpha(0.0f);
                holder.mTitleBg.setAlpha(0.0f);
                holder.mMoreOptions.setAlpha(0.0f);
            }
            if (this.mExcludeList.contains(holder.mGameNameView.getText().toString()) || holder.mStateText.getVisibility() == View.VISIBLE) {
                holder.mMoreOptions.setAlpha(0.0f);
            }
        }
    }

    private boolean isScrolling() {
        return this.mScrollState == ScrollState.SCROLLING;
    }

    private boolean isNeedPathInterpolator() {
        boolean z = true;
        if (!this.mIsNeedPathInterpolator) {
            return false;
        }
        if (0.0f > this.mScrollStartPosition) {
            return true;
        }
        if (Math.abs(getCurrentScrollPosition() - this.mScrollStartPosition) > this.REMOVE_INTERPOLATOR_THRESHOLD) {
            z = false;
        }
        this.mIsNeedPathInterpolator = z;
        if (isThisScrollDirectionChanged()) {
            this.mIsNeedPathInterpolator = false;
        }
        return this.mIsNeedPathInterpolator;
    }

    public void scrollStateChanged(boolean isScrolling, ScrollState scrollState) {
        this.mIsScrolling = isScrolling;
        if (scrollState != this.mScrollState) {
            if (scrollState == ScrollState.SCROLLING) {
                setTextVisibility(false);
                doAnimator(true);
            } else if (isScrolling()) {
                doAnimator(false);
            }
            this.mScrollState = scrollState;
        }
        if (!this.mIsScrolling) {
            this.mScrollStartDirection = 0.0f;
            this.mScrollStartPosition = getCurrentScrollPosition();
            this.mIsNeedPathInterpolator = true;
        } else if (0.0f > this.mScrollStartPosition) {
            this.mScrollStartPosition = (float) getCenterItemPosition();
        }
    }

    private void setTheInitialDirectionOfThisScrollIfNeed() {
        if (this.mIsNeedPathInterpolator && 0.0f == this.mScrollStartDirection && this.mIsScrolling) {
            this.mScrollStartDirection = getCurrentScrollPosition() - this.mScrollStartPosition;
        }
    }

    private boolean isThisScrollDirectionChanged() {
        if (0.0f != this.mScrollStartDirection && this.mScrollStartDirection * (getCurrentScrollPosition() - this.mScrollStartPosition) <= 0.0f) {
            return true;
        }
        return false;
    }

    public void doAnimator(boolean isHide) {
        int centerPosition = getCenterItemPosition();
        if (centerPosition >= 0) {
            LogUtil.w(TAG, "doAnimator(" + isHide + ") center : " + centerPosition + ", mItemsCount = " + this.mItemsCount);
            setTextVisibility(!isHide);
            for (int i = -(getAllVisibleItemsCount() / 2); i < getAllVisibleItemsCount() / 2; i++) {
                int position = centerPosition + i;
                if (position >= this.mItemsCount) {
                    position -= this.mItemsCount;
                } else if (position < 0) {
                    position += this.mItemsCount;
                }
                startItemAnimatorByPosition(position, isHide);
            }
        }
    }

    private void startItemAnimatorByPosition(int position, boolean isHide) {
        float textAlpha;
        int i;
        float f;
        float f2;
        int i2 = 0;
        float f3 = 0.0f;
        BannerViewHolder holder = getViewHolderByPosition(position);
        View view = findViewByPosition(position);
        if (view == null) {
            LogUtil.w(TAG, "startItemAnimatorByPosition() error because not found view by position : " + position);
            return;
        }
        ItemTransformation itemTransformation = (ItemTransformation) view.getTag();
        if (position == getCenterItemPosition()) {
            textAlpha = 1.0f;
        } else {
            textAlpha = 0.0f;
        }
        float moreOptionsAlpha = getOptionsAlphaByPosition(position, holder);
        if (holder != null) {
            ImageView imageView = holder.mMoreOptions;
            String str = "alpha";
            if (isHide) {
                moreOptionsAlpha = 0.0f;
            }
            if (isHide) {
                i = 0;
            } else {
                i = 285;
            }
            startAnimator(imageView, str, moreOptionsAlpha, i);
            DrawableLeftTextView drawableLeftTextView = holder.mGameNameView;
            String str2 = "alpha";
            if (isHide) {
                f = 0.0f;
            } else {
                f = textAlpha;
            }
            startAnimator(drawableLeftTextView, str2, f, isHide ? 0 : 285);
            ImageView imageView2 = holder.mTitleBg;
            String str3 = "alpha";
            if (isHide) {
                f2 = 0.0f;
            } else {
                f2 = textAlpha;
            }
            startAnimator(imageView2, str3, f2, isHide ? 0 : 285);
            ImageView imageView3 = holder.mShadowView;
            String str4 = "alpha";
            if (!isHide) {
                f3 = textAlpha;
            }
            if (!isHide) {
                i2 = 285;
            }
            startAnimator(imageView3, str4, f3, i2);
            return;
        }
        LogUtil.w(TAG, "---------->startItemAnimator() but position : " + position + ", holder = " + holder);
    }

    private void setTextVisibility(boolean visibility) {
        this.mIsVisibility = visibility;
    }

    private boolean isTextVisibility() {
        return this.mIsVisibility && isStartAnimEnd();
    }

    public void setTextVisibility() {
        if (this.mScrollState != ScrollState.SCROLLING) {
            setTextVisibility(true);
            doAnimator(false);
        }
    }

    private float getOptionsAlphaByPosition(int position, BannerViewHolder holder) {
        if (holder != null && position == getCenterItemPosition() && holder.mStateText.getVisibility() != View.VISIBLE && !this.mExcludeList.contains(holder.mGameNameView.getText().toString())) {
            return 1.0f;
        }
        return 0.0f;
    }

    private void startAnimator(View view, String propertyName, float value, int duration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, propertyName, new float[]{value});
        animator.setDuration((long) duration);
        animator.setInterpolator(Anim3DHelper.PATH_INTERPOLATOR_CARD_REBOUND);
        animator.start();
    }

    public BannerViewHolder getViewHolderByPosition(int position) {
        View view = findViewByPosition(position);
        if (view == null || view.getParent() == null || !(view.getParent() instanceof RecyclerView)) {
            return null;
        }
        RecyclerView recyclerView = (RecyclerView) view.getParent();
        BannerViewHolder holder = (BannerViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
        if (holder == null) {
            return (BannerViewHolder) recyclerView.findContainingViewHolder(view);
        }
        return holder;
    }

    public void doStartAnim() {
        View view = findViewByPosition(getCenterItemPosition());
        if (view != null) {
            view.setAlpha(0.0f);
        }
    }

    public View findCenterItemView() {
        return findViewByPosition(getCenterItemPosition());
    }

    public void setStartAnimEnd(boolean isEnd) {
        this.mStartAnimEnd = isEnd;
    }

    public boolean isStartAnimEnd() {
        return this.mStartAnimEnd;
    }

    public void setAdjacentCardSpace(int cardSpace) {
        this.mAdjacentCardSpace = Integer.valueOf(cardSpace);
    }

    public int getAdjacentCardSpace() {
        return this.mAdjacentCardSpace.intValue();
    }

    public void setCardMoveAlongX(int cardMoveAlongX) {
        this.mCardMoveAlongX = Integer.valueOf(cardMoveAlongX);
    }

    public int getCardMoveAlongX() {
        return this.mCardMoveAlongX.intValue();
    }

    public void setEdgeTransparentAreaWidth(int edgeTransparentAreaWidth) {
        this.mEdgeTransparentAreaWidth = Integer.valueOf(edgeTransparentAreaWidth);
    }

    public int getEdgeTransparentAreaWidth() {
        return this.mEdgeTransparentAreaWidth.intValue();
    }

    private int getVisibleCardOffset() {
        return (int) (((float) (getEdgeTransparentAreaWidth() - 40)) * 1.0f);
    }

    private int getVisibleCardMinStart() {
        return getVisibleCardOffset() * -1;
    }

    private int getVisibleCardMinEnd() {
        return getVisibleCardMinStart() + this.mDecoratedChildWidth.intValue();
    }

    private int getVisibleCardMaxStart() {
        return getVisibleCardMaxEnd() - this.mDecoratedChildWidth.intValue();
    }

    private int getVisibleCardMaxEnd() {
        return getWidthNoPadding() + getVisibleCardOffset();
    }

    private float getCurrentScrollPosition() {
        return (float) getDoubleCurrentScrollPosition();
    }

    private double getDoubleCurrentScrollPosition() {
        if (getMaxScrollOffset() == 0) {
            return 0.0d;
        }
        return ((double) this.mLayoutHelper.mScrollOffset) / ((double) getScrollItemSize());
    }

    private int getMaxScrollOffset() {
        return getScrollItemSize() * (this.mItemsCount - 1);
    }

    private void generateLayoutOrder(float currentScrollPosition, @NonNull RecyclerView.State state) {
        this.mItemsCount = state.getItemCount();
        float absCurrentScrollPosition = makeScrollPositionInRange0ToCount(currentScrollPosition, this.mItemsCount);
        double absDoubleCurrentScrollPosition = makeScrollPositionInRange0ToCount(getDoubleCurrentScrollPosition(), this.mItemsCount);
        int centerItem = Math.round(absCurrentScrollPosition);
        if (!this.mCircleLayout || 1 >= this.mItemsCount) {
            int firstVisible = Math.max((centerItem - this.mLayoutHelper.mMaxVisibleItems) - 1, 0);
            int lastVisible = Math.min(this.mLayoutHelper.mMaxVisibleItems + centerItem + 1, this.mItemsCount - 1);
            int layoutCount = (lastVisible - firstVisible) + 1;
            this.mLayoutHelper.initLayoutOrder(layoutCount);
            for (int i = firstVisible; i <= lastVisible; i++) {
                if (i == centerItem) {
                    this.mLayoutHelper.setLayoutOrder(layoutCount - 1, i, (float) (((double) i) - absDoubleCurrentScrollPosition));
                } else if (i < centerItem) {
                    this.mLayoutHelper.setLayoutOrder(i - firstVisible, i, (float) (((double) i) - absDoubleCurrentScrollPosition));
                } else {
                    this.mLayoutHelper.setLayoutOrder((layoutCount - (i - centerItem)) - 1, i, (float) (((double) i) - absDoubleCurrentScrollPosition));
                }
            }
            return;
        }
        int layoutCount2 = Math.min((this.mLayoutHelper.mMaxVisibleItems * 2) + 3, this.mItemsCount);
        this.mLayoutHelper.initLayoutOrder(layoutCount2);
        int countLayoutHalf = layoutCount2 / 2;
        for (int i2 = 1; i2 <= countLayoutHalf; i2++) {
            this.mLayoutHelper.setLayoutOrder(countLayoutHalf - i2, Math.round((absCurrentScrollPosition - ((float) i2)) + ((float) this.mItemsCount)) % this.mItemsCount, (((float) centerItem) - absCurrentScrollPosition) - ((float) i2));
        }
        for (int i3 = layoutCount2 - 1; i3 >= countLayoutHalf + 1; i3--) {
            this.mLayoutHelper.setLayoutOrder(i3 - 1, Math.round((absCurrentScrollPosition - ((float) i3)) + ((float) layoutCount2)) % this.mItemsCount, ((((float) centerItem) - absCurrentScrollPosition) + ((float) layoutCount2)) - ((float) i3));
        }
        this.mLayoutHelper.setLayoutOrder(layoutCount2 - 1, centerItem, ((float) centerItem) - absCurrentScrollPosition);
    }

    public int getWidthNoPadding() {
        return (getWidth() - getPaddingStart()) - getPaddingEnd();
    }

    public int getHeightNoPadding() {
        return (getHeight() - getPaddingEnd()) - getPaddingStart();
    }

    private View bindChild(int position, @NonNull RecyclerView.Recycler recycler) {
        View view = recycler.getViewForPosition(position);
        addView(view);
        measureChildWithMargins(view, 0, 0);
        return view;
    }

    private void recyclerOldViews(RecyclerView.Recycler recycler) {
        Iterator it = new ArrayList(recycler.getScrapList()).iterator();
        while (it.hasNext()) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) it.next();
            int adapterPosition = viewHolder.getAdapterPosition();
            boolean found = false;
            LayoutOrder[] access$400 = this.mLayoutHelper.mLayoutOrder;
            int length = access$400.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                } else if (access$400[i].mItemAdapterPosition == adapterPosition) {
                    found = true;
                    break;
                } else {
                    i++;
                }
            }
            if (!found) {
                recycler.recycleView(viewHolder.itemView);
            }
        }
    }

    /* access modifiers changed from: protected */
    public int getCardWidth() {
        return (this.mDecoratedChildWidth.intValue() - getEdgeTransparentAreaWidth()) - getCardMoveAlongX();
    }

    /* access modifiers changed from: protected */
    public int getSumOfCardWidthAndAdjacentCardSpace() {
        return getCardWidth() + getAdjacentCardSpace();
    }

    /* access modifiers changed from: protected */
    public int getCardOffsetByPositionDiffAndChildWidth(float itemPositionDiff) {
        return Math.round(((float) getSumOfCardWidthAndAdjacentCardSpace()) * itemPositionDiff);
    }

    public int getScrollItemSize() {
        if (1 == this.mOrientation) {
            return this.mDecoratedChildHeight.intValue();
        }
        return this.mDecoratedChildWidth.intValue();
    }

    public Parcelable onSaveInstanceState() {
        if (this.mPendingCarouselSavedState != null) {
            return new CarouselSavedState(this.mPendingCarouselSavedState);
        }
        CarouselSavedState savedState = new CarouselSavedState(super.onSaveInstanceState());
        savedState.mCenterItemPosition = this.mCenterItemPosition;
        return savedState;
    }

    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof CarouselSavedState) {
            this.mPendingCarouselSavedState = (CarouselSavedState) state;
            super.onRestoreInstanceState(this.mPendingCarouselSavedState.mSuperState);
            return;
        }
        super.onRestoreInstanceState(state);
    }

    /* access modifiers changed from: protected */
    public int getOffsetCenterView() {
        return (Math.round(getCurrentScrollPosition()) * getScrollItemSize()) - this.mLayoutHelper.mScrollOffset;
    }

    /* access modifiers changed from: protected */
    public int getOffsetForCurrentView(@NonNull View view) {
        int distance = Math.round(((float) getScrollItemSize()) * getScrollDirection(getPosition(view)));
        if (this.mCircleLayout) {
        }
        return distance;
    }

    private static float makeScrollPositionInRange0ToCount(float currentScrollPosition, int count) {
        float absCurrentScrollPosition = currentScrollPosition;
        while (0.0f > absCurrentScrollPosition) {
            absCurrentScrollPosition += (float) count;
        }
        while (Math.round(absCurrentScrollPosition) >= count) {
            absCurrentScrollPosition -= (float) count;
        }
        return absCurrentScrollPosition;
    }

    private static double makeScrollPositionInRange0ToCount(double currentScrollPosition, int count) {
        double absCurrentScrollPosition = currentScrollPosition;
        while (0.0d > absCurrentScrollPosition) {
            absCurrentScrollPosition += (double) count;
        }
        while (Math.round(absCurrentScrollPosition) >= ((long) count)) {
            absCurrentScrollPosition -= (double) count;
        }
        return absCurrentScrollPosition;
    }
}
