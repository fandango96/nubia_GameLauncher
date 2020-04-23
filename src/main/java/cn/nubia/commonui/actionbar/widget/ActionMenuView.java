package cn.nubia.commonui.actionbar.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build.VERSION;
import android.support.constraint.solver.widgets.ConstraintAnchor;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewDebug.ExportedProperty;
import android.view.accessibility.AccessibilityEvent;
import cn.nubia.commonui.R;
import cn.nubia.commonui.actionbar.internal.view.menu.ActionMenuItemView;
import cn.nubia.commonui.actionbar.internal.view.menu.MenuBuilder;
import cn.nubia.commonui.actionbar.internal.view.menu.MenuBuilder.ItemInvoker;
import cn.nubia.commonui.actionbar.internal.view.menu.MenuItemImpl;
import cn.nubia.commonui.actionbar.internal.view.menu.MenuPresenter.Callback;
import cn.nubia.commonui.actionbar.internal.view.menu.MenuView;

public class ActionMenuView extends LinearLayoutCompat implements ItemInvoker, MenuView {
    static final int GENERATED_ITEM_PADDING = 4;
    static final int MIN_CELL_SIZE = 56;
    private static final String TAG = "NubiaWidget";
    private Callback mActionMenuPresenterCallback;
    private Context mContext;
    private boolean mFormatItems;
    private int mFormatItemsWidth;
    private int mGeneratedItemPadding;
    private int mMaxItemHeight;
    private MenuBuilder mMenu;
    /* access modifiers changed from: private */
    public MenuBuilder.Callback mMenuBuilderCallback;
    private int mMinCellSize;
    /* access modifiers changed from: private */
    public OnMenuItemClickListener mOnMenuItemClickListener;
    private Context mPopupContext;
    private int mPopupTheme;
    private ActionMenuPresenter mPresenter;
    private boolean mReserveOverflow;

    public interface ActionMenuChildView {
        boolean needsDividerAfter();

        boolean needsDividerBefore();
    }

    private class ActionMenuPresenterCallback implements Callback {
        private ActionMenuPresenterCallback() {
        }

        public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
        }

        public boolean onOpenSubMenu(MenuBuilder subMenu) {
            return false;
        }
    }

    public static class LayoutParams extends cn.nubia.commonui.actionbar.widget.LinearLayoutCompat.LayoutParams {
        @ExportedProperty
        public int cellsUsed;
        @ExportedProperty
        public boolean expandable;
        boolean expanded;
        @ExportedProperty
        public int extraPixels;
        @ExportedProperty
        public boolean isOverflowButton;
        @ExportedProperty
        public boolean preventEdgeOffset;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams other) {
            super(other);
        }

        public LayoutParams(LayoutParams other) {
            super((android.view.ViewGroup.LayoutParams) other);
            this.isOverflowButton = other.isOverflowButton;
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            this.isOverflowButton = false;
        }

        LayoutParams(int width, int height, boolean isOverflowButton2) {
            super(width, height);
            this.isOverflowButton = isOverflowButton2;
        }
    }

    private class MenuBuilderCallback implements MenuBuilder.Callback {
        private MenuBuilderCallback() {
        }

        public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
            return ActionMenuView.this.mOnMenuItemClickListener != null && ActionMenuView.this.mOnMenuItemClickListener.onMenuItemClick(item);
        }

        public void onMenuModeChange(MenuBuilder menu) {
            if (ActionMenuView.this.mMenuBuilderCallback != null) {
                ActionMenuView.this.mMenuBuilderCallback.onMenuModeChange(menu);
            }
        }
    }

    public interface OnMenuItemClickListener {
        boolean onMenuItemClick(MenuItem menuItem);
    }

    public ActionMenuView(Context context) {
        this(context, null);
    }

    public ActionMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        setBaselineAligned(false);
        float density = context.getResources().getDisplayMetrics().density;
        this.mMinCellSize = (int) (56.0f * density);
        this.mGeneratedItemPadding = (int) (4.0f * density);
        this.mPopupContext = context;
        this.mPopupTheme = 0;
        setResetMaxItemHeight();
    }

    public void setPopupTheme(int resId) {
        if (this.mPopupTheme != resId) {
            this.mPopupTheme = resId;
            if (resId == 0) {
                this.mPopupContext = this.mContext;
            } else {
                this.mPopupContext = new ContextThemeWrapper(this.mContext, resId);
            }
        }
    }

    public int getPopupTheme() {
        return this.mPopupTheme;
    }

    public void setPresenter(ActionMenuPresenter presenter) {
        this.mPresenter = presenter;
        this.mPresenter.setMenuView(this);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        if (VERSION.SDK_INT >= 8) {
            super.onConfigurationChanged(newConfig);
        }
        if (this.mPresenter != null) {
            this.mPresenter.updateMenuView(false);
            setResetMaxItemHeight();
            if (this.mPresenter.isOverflowMenuShowing()) {
                this.mPresenter.hideOverflowMenu();
                this.mPresenter.showOverflowMenu();
            }
        }
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener listener) {
        this.mOnMenuItemClickListener = listener;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(this.mMaxItemHeight, Integer.MIN_VALUE));
    }

    private void onMeasureExactFormat(int widthMeasureSpec, int heightMeasureSpec) {
        int cellsAvailable;
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthPadding = getPaddingLeft() + getPaddingRight();
        int heightPadding = getPaddingTop() + getPaddingBottom();
        int itemHeightSpec = getChildMeasureSpec(heightMeasureSpec, heightPadding, -2);
        int widthSize2 = widthSize - widthPadding;
        int cellCount = widthSize2 / this.mMinCellSize;
        int cellSizeRemaining = widthSize2 % this.mMinCellSize;
        if (cellCount == 0) {
            setMeasuredDimension(widthSize2, 0);
            return;
        }
        int cellSize = this.mMinCellSize + (cellSizeRemaining / cellCount);
        int cellsRemaining = cellCount;
        int maxChildHeight = 0;
        int maxCellsUsed = 0;
        int expandableItemCount = 0;
        int visibleItemCount = 0;
        boolean hasOverflow = false;
        long smallestItemsAt = 0;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                boolean isGeneratedItem = child instanceof ActionMenuItemView;
                visibleItemCount++;
                if (isGeneratedItem) {
                    child.setPadding(this.mGeneratedItemPadding, 0, this.mGeneratedItemPadding, 0);
                }
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                lp.expanded = false;
                lp.extraPixels = 0;
                lp.cellsUsed = 0;
                lp.expandable = false;
                lp.leftMargin = 0;
                lp.rightMargin = 0;
                lp.preventEdgeOffset = isGeneratedItem && ((ActionMenuItemView) child).hasText();
                if (lp.isOverflowButton) {
                    cellsAvailable = 1;
                } else {
                    cellsAvailable = cellsRemaining;
                }
                int cellsUsed = measureChildForCells(child, cellSize, cellsAvailable, itemHeightSpec, heightPadding);
                maxCellsUsed = Math.max(maxCellsUsed, cellsUsed);
                if (lp.expandable) {
                    expandableItemCount++;
                }
                if (lp.isOverflowButton) {
                    hasOverflow = true;
                }
                cellsRemaining -= cellsUsed;
                maxChildHeight = Math.max(maxChildHeight, child.getMeasuredHeight());
                if (cellsUsed == 1) {
                    smallestItemsAt |= (long) (1 << i);
                }
            }
        }
        boolean centerSingleExpandedItem = hasOverflow && visibleItemCount == 2;
        boolean needsExpansion = false;
        while (expandableItemCount > 0 && cellsRemaining > 0) {
            int minCells = ConstraintAnchor.ANY_GROUP;
            long minCellsAt = 0;
            int minCellsItemCount = 0;
            for (int i2 = 0; i2 < childCount; i2++) {
                LayoutParams lp2 = (LayoutParams) getChildAt(i2).getLayoutParams();
                if (lp2.expandable) {
                    if (lp2.cellsUsed < minCells) {
                        minCells = lp2.cellsUsed;
                        minCellsAt = (long) (1 << i2);
                        minCellsItemCount = 1;
                    } else if (lp2.cellsUsed == minCells) {
                        minCellsAt |= (long) (1 << i2);
                        minCellsItemCount++;
                    }
                }
            }
            smallestItemsAt |= minCellsAt;
            if (minCellsItemCount > cellsRemaining) {
                break;
            }
            int minCells2 = minCells + 1;
            for (int i3 = 0; i3 < childCount; i3++) {
                View child2 = getChildAt(i3);
                LayoutParams lp3 = (LayoutParams) child2.getLayoutParams();
                if ((((long) (1 << i3)) & minCellsAt) != 0) {
                    if (centerSingleExpandedItem && lp3.preventEdgeOffset && cellsRemaining == 1) {
                        child2.setPadding(this.mGeneratedItemPadding + cellSize, 0, this.mGeneratedItemPadding, 0);
                    }
                    lp3.cellsUsed++;
                    lp3.expanded = true;
                    cellsRemaining--;
                } else if (lp3.cellsUsed == minCells2) {
                    smallestItemsAt |= (long) (1 << i3);
                }
            }
            needsExpansion = true;
        }
        boolean singleItem = !hasOverflow && visibleItemCount == 1;
        if (cellsRemaining > 0 && smallestItemsAt != 0 && (cellsRemaining < visibleItemCount - 1 || singleItem || maxCellsUsed > 1)) {
            float expandCount = (float) Long.bitCount(smallestItemsAt);
            if (!singleItem) {
                if ((1 & smallestItemsAt) != 0 && !((LayoutParams) getChildAt(0).getLayoutParams()).preventEdgeOffset) {
                    expandCount -= 0.5f;
                }
                if ((((long) (1 << (childCount - 1))) & smallestItemsAt) != 0 && !((LayoutParams) getChildAt(childCount - 1).getLayoutParams()).preventEdgeOffset) {
                    expandCount -= 0.5f;
                }
            }
            int extraPixels = expandCount > 0.0f ? (int) (((float) (cellsRemaining * cellSize)) / expandCount) : 0;
            for (int i4 = 0; i4 < childCount; i4++) {
                if ((((long) (1 << i4)) & smallestItemsAt) != 0) {
                    View child3 = getChildAt(i4);
                    LayoutParams lp4 = (LayoutParams) child3.getLayoutParams();
                    if (child3 instanceof ActionMenuItemView) {
                        lp4.extraPixels = extraPixels;
                        lp4.expanded = true;
                        if (i4 == 0 && !lp4.preventEdgeOffset) {
                            lp4.leftMargin = (-extraPixels) / 2;
                        }
                        needsExpansion = true;
                    } else if (lp4.isOverflowButton) {
                        lp4.extraPixels = extraPixels;
                        lp4.expanded = true;
                        lp4.rightMargin = (-extraPixels) / 2;
                        needsExpansion = true;
                    } else {
                        if (i4 != 0) {
                            lp4.leftMargin = extraPixels / 2;
                        }
                        if (i4 != childCount - 1) {
                            lp4.rightMargin = extraPixels / 2;
                        }
                    }
                }
            }
        }
        if (needsExpansion) {
            for (int i5 = 0; i5 < childCount; i5++) {
                View child4 = getChildAt(i5);
                LayoutParams lp5 = (LayoutParams) child4.getLayoutParams();
                if (lp5.expanded) {
                    child4.measure(MeasureSpec.makeMeasureSpec((lp5.cellsUsed * cellSize) + lp5.extraPixels, 1073741824), itemHeightSpec);
                }
            }
        }
        if (heightMode != 1073741824) {
            heightSize = maxChildHeight;
        }
        setMeasuredDimension(widthSize2, heightSize);
    }

    static int measureChildForCells(View child, int cellSize, int cellsRemaining, int parentHeightMeasureSpec, int parentHeightPadding) {
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        int childHeightSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(parentHeightMeasureSpec) - parentHeightPadding, MeasureSpec.getMode(parentHeightMeasureSpec));
        ActionMenuItemView itemView = child instanceof ActionMenuItemView ? (ActionMenuItemView) child : null;
        boolean hasText = itemView != null && itemView.hasText();
        int cellsUsed = 0;
        if (cellsRemaining > 0 && (!hasText || cellsRemaining >= 2)) {
            child.measure(MeasureSpec.makeMeasureSpec(cellSize * cellsRemaining, Integer.MIN_VALUE), childHeightSpec);
            int measuredWidth = child.getMeasuredWidth();
            cellsUsed = measuredWidth / cellSize;
            if (measuredWidth % cellSize != 0) {
                cellsUsed++;
            }
            if (hasText && cellsUsed < 2) {
                cellsUsed = 2;
            }
        }
        lp.expandable = !lp.isOverflowButton && hasText;
        lp.cellsUsed = cellsUsed;
        child.measure(MeasureSpec.makeMeasureSpec(cellsUsed * cellSize, 1073741824), childHeightSpec);
        return cellsUsed;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        dismissPopupMenus();
    }

    public boolean isOverflowReserved() {
        return this.mReserveOverflow;
    }

    public void setOverflowReserved(boolean reserveOverflow) {
        this.mReserveOverflow = reserveOverflow;
    }

    /* access modifiers changed from: protected */
    public LayoutParams generateDefaultLayoutParams() {
        LayoutParams params = new LayoutParams(-2, -2);
        params.gravity = 16;
        return params;
    }

    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /* access modifiers changed from: protected */
    public LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams p) {
        if (p == null) {
            return generateDefaultLayoutParams();
        }
        LayoutParams result = p instanceof LayoutParams ? new LayoutParams((LayoutParams) p) : new LayoutParams(p);
        if (result.gravity > 0) {
            return result;
        }
        result.gravity = 16;
        return result;
    }

    /* access modifiers changed from: protected */
    public boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return p != null && (p instanceof LayoutParams);
    }

    public LayoutParams generateOverflowButtonLayoutParams() {
        LayoutParams result = generateDefaultLayoutParams();
        result.isOverflowButton = true;
        return result;
    }

    public boolean invokeItem(MenuItemImpl item) {
        return this.mMenu.performItemAction(item, 0);
    }

    public int getWindowAnimations() {
        return 0;
    }

    public void initialize(MenuBuilder menu) {
        this.mMenu = menu;
    }

    public Menu getMenu() {
        if (this.mMenu == null) {
            Context context = getContext();
            this.mMenu = new MenuBuilder(context);
            this.mMenu.setCallback(new MenuBuilderCallback());
            this.mPresenter = new ActionMenuPresenter(context);
            this.mPresenter.setReserveOverflow(true);
            this.mPresenter.setCallback(this.mActionMenuPresenterCallback != null ? this.mActionMenuPresenterCallback : new ActionMenuPresenterCallback());
            this.mMenu.addMenuPresenter(this.mPresenter, this.mPopupContext);
            this.mPresenter.setMenuView(this);
        }
        return this.mMenu;
    }

    public void setMenuCallbacks(Callback pcb, MenuBuilder.Callback mcb) {
        this.mActionMenuPresenterCallback = pcb;
        this.mMenuBuilderCallback = mcb;
    }

    public MenuBuilder peekMenu() {
        return this.mMenu;
    }

    public boolean showOverflowMenu() {
        return this.mPresenter != null && this.mPresenter.showOverflowMenu();
    }

    public boolean hideOverflowMenu() {
        return this.mPresenter != null && this.mPresenter.hideOverflowMenu();
    }

    public boolean isOverflowMenuShowing() {
        return this.mPresenter != null && this.mPresenter.isOverflowMenuShowing();
    }

    public boolean isOverflowMenuShowPending() {
        return this.mPresenter != null && this.mPresenter.isOverflowMenuShowPending();
    }

    public void dismissPopupMenus() {
        if (this.mPresenter != null) {
            this.mPresenter.dismissPopupMenus();
        }
    }

    /* access modifiers changed from: protected */
    public boolean hasSupportDividerBeforeChildAt(int childIndex) {
        if (childIndex == 0) {
            return false;
        }
        View childBefore = getChildAt(childIndex - 1);
        View child = getChildAt(childIndex);
        boolean result = false;
        if (childIndex < getChildCount() && (childBefore instanceof ActionMenuChildView)) {
            result = false | ((ActionMenuChildView) childBefore).needsDividerAfter();
        }
        if (childIndex <= 0 || !(child instanceof ActionMenuChildView)) {
            return result;
        }
        return result | ((ActionMenuChildView) child).needsDividerBefore();
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        return false;
    }

    public void setExpandedActionViewsExclusive(boolean exclusive) {
        this.mPresenter.setExpandedActionViewsExclusive(exclusive);
    }

    private void setResetMaxItemHeight() {
        this.mMaxItemHeight = getContext().getResources().getDimensionPixelSize(R.dimen.nubia_action_bar_menu_default_height);
    }
}
