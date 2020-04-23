package cn.nubia.commonui.actionbar.internal.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.constraint.solver.widgets.ConstraintAnchor;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.nubia.commonui.R;
import cn.nubia.commonui.actionbar.internal.view.ViewPropertyAnimatorCompatSet;
import cn.nubia.commonui.actionbar.internal.view.menu.MenuBuilder;
import cn.nubia.commonui.actionbar.view.ActionMode;
import cn.nubia.commonui.actionbar.widget.ActionMenuPresenter;
import cn.nubia.commonui.actionbar.widget.ActionMenuView;

public class ActionBarContextView extends AbsActionBarView implements ViewPropertyAnimatorListener {
    private static final int ANIMATE_IDLE = 0;
    private static final int ANIMATE_IN = 1;
    private static final int ANIMATE_OUT = 2;
    private static final String TAG = "NubiaWidget";
    private boolean mAnimateInOnLayout;
    private int mAnimationMode;
    private View mClose;
    private int mCloseItemLayout;
    private int mCloseVisibility;
    private ViewPropertyAnimatorCompatSet mCurrentAnimation;
    private View mCustomView;
    private boolean mHideCloseButton;
    private LinearLayout mLinearContainer;
    private View mNubiaBottomClose;
    private int mNubiaBottomCloseLayout;
    private View mNubiaBottomSelect;
    private int mNubiaBottomSelectLayout;
    private Drawable mSplitBackground;
    private CharSequence mSubtitle;
    private int mSubtitleStyleRes;
    private TextView mSubtitleView;
    private CharSequence mTitle;
    private LinearLayout mTitleLayout;
    private boolean mTitleOptional;
    private int mTitleStyleRes;
    private TextView mTitleView;

    public /* bridge */ /* synthetic */ void animateToVisibility(int i) {
        super.animateToVisibility(i);
    }

    public /* bridge */ /* synthetic */ boolean canShowOverflowMenu() {
        return super.canShowOverflowMenu();
    }

    public /* bridge */ /* synthetic */ void dismissPopupMenus() {
        super.dismissPopupMenus();
    }

    public /* bridge */ /* synthetic */ int getAnimatedVisibility() {
        return super.getAnimatedVisibility();
    }

    public /* bridge */ /* synthetic */ int getContentHeight() {
        return super.getContentHeight();
    }

    public /* bridge */ /* synthetic */ boolean isOverflowMenuShowPending() {
        return super.isOverflowMenuShowPending();
    }

    public /* bridge */ /* synthetic */ boolean isOverflowReserved() {
        return super.isOverflowReserved();
    }

    public /* bridge */ /* synthetic */ void postShowOverflowMenu() {
        super.postShowOverflowMenu();
    }

    public /* bridge */ /* synthetic */ void setSplitView(ViewGroup viewGroup) {
        super.setSplitView(viewGroup);
    }

    public /* bridge */ /* synthetic */ void setSplitWhenNarrow(boolean z) {
        super.setSplitWhenNarrow(z);
    }

    public ActionBarContextView(Context context) {
        this(context, null);
    }

    public ActionBarContextView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.actionModeStyle);
    }

    public ActionBarContextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mCloseVisibility = -1;
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs, R.styleable.ActionMode, defStyle, 0);
        setBackgroundDrawable(a.getDrawable(R.styleable.ActionMode_background));
        this.mTitleStyleRes = a.getResourceId(R.styleable.ActionMode_titleTextStyle, 0);
        this.mSubtitleStyleRes = a.getResourceId(R.styleable.ActionMode_subtitleTextStyle, 0);
        this.mContentHeight = a.getLayoutDimension(R.styleable.ActionMode_height, 0);
        this.mSplitBackground = a.getDrawable(R.styleable.ActionMode_backgroundSplit);
        this.mCloseItemLayout = a.getResourceId(R.styleable.ActionMode_closeItemLayout, R.layout.abc_action_mode_close_item_material);
        this.mNubiaBottomCloseLayout = a.getResourceId(R.styleable.ActionMode_nubiaBottomCloseItem, -1);
        this.mNubiaBottomSelectLayout = a.getResourceId(R.styleable.ActionMode_nubiaBottomSelectItem, -1);
        this.mLinearContainer = new LinearLayout(getContext());
        this.mHideCloseButton = a.getBoolean(R.styleable.ActionMode_hideCloseItem, false);
        a.recycle();
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mActionMenuPresenter != null) {
            this.mActionMenuPresenter.hideOverflowMenu();
            this.mActionMenuPresenter.hideSubMenus();
        }
    }

    public void setSplitToolbar(boolean split) {
        if (this.mSplitActionBar != split) {
            if (this.mActionMenuPresenter != null) {
                LayoutParams layoutParams = new LayoutParams(-2, -1);
                if (!split) {
                    this.mMenuView = (ActionMenuView) this.mActionMenuPresenter.getMenuView(this);
                    this.mMenuView.setBackgroundDrawable(null);
                    ViewGroup oldParent = (ViewGroup) this.mMenuView.getParent();
                    if (oldParent != null) {
                        oldParent.removeView(this.mMenuView);
                    }
                    addView(this.mMenuView, layoutParams);
                } else {
                    this.mActionMenuPresenter.setWidthLimit(getContext().getResources().getDisplayMetrics().widthPixels, true);
                    this.mActionMenuPresenter.setItemLimit(ConstraintAnchor.ANY_GROUP);
                    layoutParams.width = -1;
                    layoutParams.height = this.mContentHeight;
                    this.mMenuView = (ActionMenuView) this.mActionMenuPresenter.getMenuView(this);
                    this.mMenuView.setBackgroundDrawable(this.mSplitBackground);
                    ViewGroup oldParent2 = (ViewGroup) this.mMenuView.getParent();
                    if (oldParent2 != null) {
                        oldParent2.removeView(this.mMenuView);
                    }
                    this.mSplitView.addView(this.mMenuView, layoutParams);
                }
            }
            super.setSplitToolbar(split);
        }
    }

    public void setContentHeight(int height) {
        this.mContentHeight = height;
    }

    public void setCustomView(View view) {
        if (this.mCustomView != null) {
            removeView(this.mCustomView);
        }
        this.mCustomView = view;
        if (this.mTitleLayout != null) {
            removeView(this.mTitleLayout);
            this.mTitleLayout = null;
        }
        if (view != null) {
            addView(view);
        }
        requestLayout();
    }

    public void setCloseMenuVisibility(int visibility) {
        if (this.mClose != null) {
            this.mClose.setVisibility(visibility);
        } else {
            this.mCloseVisibility = visibility;
        }
    }

    public void setTitle(CharSequence title) {
        this.mTitle = title;
        initTitle();
    }

    public void setSubtitle(CharSequence subtitle) {
        this.mSubtitle = subtitle;
        initTitle();
    }

    public CharSequence getTitle() {
        return this.mTitle;
    }

    public CharSequence getSubtitle() {
        return this.mSubtitle;
    }

    private void initTitle() {
        boolean hasTitle;
        boolean hasSubtitle;
        int i;
        int i2 = 8;
        if (this.mTitleLayout == null) {
            LayoutInflater.from(getContext()).inflate(R.layout.abc_action_bar_title_item, this);
            this.mTitleLayout = (LinearLayout) getChildAt(getChildCount() - 1);
            this.mTitleView = (TextView) this.mTitleLayout.findViewById(R.id.action_bar_title);
            this.mSubtitleView = (TextView) this.mTitleLayout.findViewById(R.id.action_bar_subtitle);
            if (this.mTitleStyleRes != 0) {
                this.mTitleView.setTextAppearance(getContext(), this.mTitleStyleRes);
            }
            if (this.mSubtitleStyleRes != 0) {
                this.mSubtitleView.setTextAppearance(getContext(), this.mSubtitleStyleRes);
            }
        }
        this.mTitleView.setText(this.mTitle);
        this.mSubtitleView.setText(this.mSubtitle);
        if (!TextUtils.isEmpty(this.mTitle)) {
            hasTitle = true;
        } else {
            hasTitle = false;
        }
        if (!TextUtils.isEmpty(this.mSubtitle)) {
            hasSubtitle = true;
        } else {
            hasSubtitle = false;
        }
        TextView textView = this.mSubtitleView;
        if (hasSubtitle) {
            i = 0;
        } else {
            i = 8;
        }
        textView.setVisibility(i);
        LinearLayout linearLayout = this.mTitleLayout;
        if (hasTitle || hasSubtitle) {
            i2 = 0;
        }
        linearLayout.setVisibility(i2);
        if (this.mTitleLayout.getParent() == null) {
            addView(this.mTitleLayout);
        }
    }

    public boolean setBottomSelectListener(OnClickListener listener) {
        if (this.mNubiaBottomSelect == null) {
            return false;
        }
        this.mNubiaBottomSelect.setOnClickListener(listener);
        return true;
    }

    public CheckedTextView getSelectCheckedTextView() {
        if (this.mNubiaBottomSelect != null) {
            return (CheckedTextView) this.mNubiaBottomSelect.findViewById(R.id.nubia_bottom_action_mode_select_button);
        }
        return null;
    }

    public View getSelectZoneView() {
        return this.mNubiaBottomSelect;
    }

    public boolean setBottomItemIcon(Drawable closeDrawable, Drawable selectDrawable) {
        if (this.mNubiaBottomClose == null || this.mNubiaBottomSelect == null) {
            return false;
        }
        TextView closeTV = (TextView) this.mNubiaBottomClose.findViewById(R.id.nubia_bottom_action_mode_close_button);
        CheckedTextView selectTV = getSelectCheckedTextView();
        if (!(closeTV == null || closeDrawable == null)) {
            closeTV.setBackground(closeDrawable);
        }
        if (!(selectTV == null || selectDrawable == null)) {
            selectTV.setBackground(selectDrawable);
        }
        return true;
    }

    public void initForMode(final ActionMode mode) {
        if (this.mClose == null) {
            this.mClose = LayoutInflater.from(getContext()).inflate(this.mCloseItemLayout, this, false);
        }
        this.mClose.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mode.finish();
            }
        });
        if (this.mCloseVisibility != -1 && this.mCloseVisibility == 8) {
            this.mClose.setVisibility(8);
        }
        MenuBuilder menu = (MenuBuilder) mode.getMenu();
        if (this.mActionMenuPresenter != null) {
            this.mActionMenuPresenter.dismissPopupMenus();
        }
        this.mActionMenuPresenter = new ActionMenuPresenter(getContext());
        this.mActionMenuPresenter.setReserveOverflow(true);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -1);
        layoutParams.gravity = 17;
        if (!this.mSplitActionBar) {
            menu.addMenuPresenter(this.mActionMenuPresenter, this.mPopupContext);
            this.mMenuView = (ActionMenuView) this.mActionMenuPresenter.getMenuView(this);
            this.mMenuView.setBackgroundDrawable(null);
            if (!this.mHideCloseButton) {
                addView(this.mClose);
            }
            addView(this.mMenuView, layoutParams);
        } else {
            this.mActionMenuPresenter.setWidthLimit(getContext().getResources().getDisplayMetrics().widthPixels, true);
            this.mActionMenuPresenter.setItemLimit(ConstraintAnchor.ANY_GROUP);
            layoutParams.width = -1;
            menu.addMenuPresenter(this.mActionMenuPresenter, this.mPopupContext);
            this.mMenuView = (ActionMenuView) this.mActionMenuPresenter.getMenuView(this);
            if (this.mSplitActionBar && this.mNubiaBottomClose == null && this.mNubiaBottomCloseLayout != -1) {
                this.mNubiaBottomClose = LayoutInflater.from(getContext()).inflate(this.mNubiaBottomCloseLayout, this, false);
                this.mNubiaBottomClose.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        mode.finish();
                    }
                });
            }
            if (this.mSplitActionBar && this.mNubiaBottomSelect == null && this.mNubiaBottomSelectLayout != -1) {
                this.mNubiaBottomSelect = LayoutInflater.from(getContext()).inflate(this.mNubiaBottomSelectLayout, this, false);
            }
            int closeWidth = getResources().getDimensionPixelSize(R.dimen.nubia_action_mode_close_button_default_width);
            this.mLinearContainer.setGravity(17);
            this.mLinearContainer.addView(this.mNubiaBottomClose, new LinearLayout.LayoutParams(closeWidth, -1));
            this.mLinearContainer.addView(this.mMenuView, new LinearLayout.LayoutParams(-2, -1));
            this.mLinearContainer.addView(this.mNubiaBottomSelect, new LinearLayout.LayoutParams(closeWidth, -1));
            this.mLinearContainer.setBackgroundDrawable(null);
            this.mSplitView.addView(this.mLinearContainer, layoutParams);
        }
        this.mAnimateInOnLayout = true;
    }

    public void closeMode() {
        if (this.mAnimationMode != 2) {
            if (this.mClose == null) {
                killMode();
                return;
            }
            finishAnimation();
            this.mAnimationMode = 2;
            this.mCurrentAnimation = makeOutAnimation();
            this.mCurrentAnimation.start();
        }
    }

    private void finishAnimation() {
        ViewPropertyAnimatorCompatSet a = this.mCurrentAnimation;
        if (a != null) {
            this.mCurrentAnimation = null;
            a.cancel();
        }
    }

    public void killMode() {
        finishAnimation();
        removeAllViews();
        if (this.mSplitView != null) {
            this.mLinearContainer.removeAllViews();
            this.mSplitView.removeView(this.mLinearContainer);
        }
        this.mClose = null;
        this.mNubiaBottomClose = null;
        this.mNubiaBottomSelect = null;
        this.mCustomView = null;
        this.mMenuView = null;
        this.mAnimateInOnLayout = false;
    }

    public boolean showOverflowMenu() {
        if (this.mActionMenuPresenter != null) {
            return this.mActionMenuPresenter.showOverflowMenu();
        }
        return false;
    }

    public boolean hideOverflowMenu() {
        if (this.mActionMenuPresenter != null) {
            return this.mActionMenuPresenter.hideOverflowMenu();
        }
        return false;
    }

    public boolean isOverflowMenuShowing() {
        if (this.mActionMenuPresenter != null) {
            return this.mActionMenuPresenter.isOverflowMenuShowing();
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(-1, -2);
    }

    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxHeight;
        int customWidth;
        int customHeight;
        if (MeasureSpec.getMode(widthMeasureSpec) != 1073741824) {
            throw new IllegalStateException(getClass().getSimpleName() + " can only be used with android:layout_width=\"match_parent\" (or fill_parent)");
        } else if (MeasureSpec.getMode(heightMeasureSpec) == 0) {
            throw new IllegalStateException(getClass().getSimpleName() + " can only be used with android:layout_height=\"wrap_content\"");
        } else {
            int contentWidth = MeasureSpec.getSize(widthMeasureSpec);
            if (this.mContentHeight > 0) {
                maxHeight = this.mContentHeight;
            } else {
                maxHeight = MeasureSpec.getSize(heightMeasureSpec);
            }
            int verticalPadding = getPaddingTop() + getPaddingBottom();
            int availableWidth = (contentWidth - getPaddingLeft()) - getPaddingRight();
            int height = maxHeight - verticalPadding;
            int childSpecHeight = MeasureSpec.makeMeasureSpec(height, Integer.MIN_VALUE);
            if (this.mClose != null) {
                MarginLayoutParams lp = (MarginLayoutParams) this.mClose.getLayoutParams();
                availableWidth = measureChildView(this.mClose, availableWidth, childSpecHeight, 0) - (lp.leftMargin + lp.rightMargin);
            }
            if (this.mMenuView != null && this.mMenuView.getParent() == this) {
                availableWidth = measureChildView(this.mMenuView, availableWidth, childSpecHeight, 0);
            }
            if (this.mTitleLayout != null && this.mCustomView == null) {
                if (this.mTitleOptional) {
                    this.mTitleLayout.measure(MeasureSpec.makeMeasureSpec(0, 0), childSpecHeight);
                    int titleWidth = this.mTitleLayout.getMeasuredWidth();
                    boolean titleFits = titleWidth <= availableWidth;
                    if (titleFits) {
                        availableWidth -= titleWidth;
                    }
                    this.mTitleLayout.setVisibility(titleFits ? 0 : 8);
                } else {
                    availableWidth = measureChildView(this.mTitleLayout, availableWidth, childSpecHeight, 0);
                }
            }
            if (this.mCustomView != null) {
                LayoutParams lp2 = this.mCustomView.getLayoutParams();
                int customWidthMode = lp2.width != -2 ? 1073741824 : Integer.MIN_VALUE;
                if (lp2.width >= 0) {
                    customWidth = Math.min(lp2.width, availableWidth);
                } else {
                    customWidth = availableWidth;
                }
                int customHeightMode = lp2.height != -2 ? 1073741824 : Integer.MIN_VALUE;
                if (lp2.height >= 0) {
                    customHeight = Math.min(lp2.height, height);
                } else {
                    customHeight = height;
                }
                this.mCustomView.measure(MeasureSpec.makeMeasureSpec(customWidth, customWidthMode), MeasureSpec.makeMeasureSpec(customHeight, customHeightMode));
            }
            if (this.mContentHeight <= 0) {
                int measuredHeight = 0;
                int count = getChildCount();
                for (int i = 0; i < count; i++) {
                    int paddedViewHeight = getChildAt(i).getMeasuredHeight() + verticalPadding;
                    if (paddedViewHeight > measuredHeight) {
                        measuredHeight = paddedViewHeight;
                    }
                }
                setMeasuredDimension(contentWidth, measuredHeight);
                return;
            }
            setMeasuredDimension(contentWidth, maxHeight);
        }
    }

    private ViewPropertyAnimatorCompatSet makeInAnimation() {
        ViewPropertyAnimatorCompatSet set = new ViewPropertyAnimatorCompatSet();
        if (this.mClose != null) {
            float f = (float) ((-this.mClose.getWidth()) - ((MarginLayoutParams) this.mClose.getLayoutParams()).leftMargin);
            ViewCompat.setTranslationX(this.mClose, 0.0f);
            ViewCompat.setScaleY(this.mClose, 0.0f);
            ViewPropertyAnimatorCompat closeIn = ViewCompat.animate(this.mClose).scaleY(1.0f);
            closeIn.setDuration(1);
            set.play(closeIn);
        }
        if (this.mMenuView != null) {
            int count = this.mMenuView.getChildCount();
            if (count > 0) {
                int i = count - 1;
                int j = 0;
                while (i >= 0) {
                    View child = this.mMenuView.getChildAt(i);
                    ViewCompat.setTranslationY(child, (float) this.mMenuView.getMeasuredHeight());
                    ViewPropertyAnimatorCompat translationY = ViewCompat.animate(child).translationY(0.0f);
                    translationY.setDuration(300);
                    set.play(translationY);
                    ViewCompat.setAlpha(child, 0.0f);
                    ViewPropertyAnimatorCompat alpha = ViewCompat.animate(child).alpha(1.0f);
                    alpha.setDuration(300);
                    set.play(alpha);
                    i--;
                    j++;
                }
            }
        }
        return set;
    }

    private ViewPropertyAnimatorCompatSet makeOutAnimation() {
        ViewPropertyAnimatorCompatSet set = new ViewPropertyAnimatorCompatSet();
        if (this.mClose != null) {
            ViewCompat.setScaleY(this.mClose, 1.0f);
            ViewPropertyAnimatorCompat closeOut = ViewCompat.animate(this.mClose).scaleY(0.0f);
            closeOut.setListener(this);
            closeOut.setDuration(1);
            set.play(closeOut);
        }
        if (this.mNubiaBottomClose != null) {
            ViewCompat.setScaleY(this.mNubiaBottomClose, 1.0f);
            ViewPropertyAnimatorCompat nubiaCloseOut = ViewCompat.animate(this.mNubiaBottomClose).scaleY(0.0f);
            nubiaCloseOut.setListener(this);
            nubiaCloseOut.setDuration(1);
            set.play(nubiaCloseOut);
        }
        if (this.mNubiaBottomSelect != null) {
            ViewCompat.setScaleY(this.mNubiaBottomSelect, 1.0f);
            ViewPropertyAnimatorCompat nubiaSelectOut = ViewCompat.animate(this.mNubiaBottomSelect).scaleY(0.0f);
            nubiaSelectOut.setListener(this);
            nubiaSelectOut.setDuration(1);
            set.play(nubiaSelectOut);
        }
        if (this.mMenuView != null && this.mMenuView.getChildCount() > 0) {
            for (int i = 0; i < 0; i++) {
                View child = this.mMenuView.getChildAt(i);
                ViewCompat.setScaleY(child, 1.0f);
                ViewPropertyAnimatorCompat a = ViewCompat.animate(child).scaleY(0.0f);
                a.setDuration(300);
                set.play(a);
            }
        }
        return set;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        boolean isLayoutRtl = ViewUtils.isLayoutRtl(this);
        int x = isLayoutRtl ? (r - l) - getPaddingRight() : getPaddingLeft();
        int y = getPaddingTop();
        int contentHeight = ((b - t) - getPaddingTop()) - getPaddingBottom();
        if (!(this.mClose == null || this.mClose.getVisibility() == 8)) {
            MarginLayoutParams lp = (MarginLayoutParams) this.mClose.getLayoutParams();
            int startMargin = isLayoutRtl ? lp.rightMargin : lp.leftMargin;
            int endMargin = isLayoutRtl ? lp.leftMargin : lp.rightMargin;
            int x2 = next(x, startMargin, isLayoutRtl);
            x = next(x2 + positionChild(this.mClose, x2, y, contentHeight, isLayoutRtl), endMargin, isLayoutRtl);
            if (this.mAnimateInOnLayout) {
                this.mAnimationMode = 1;
                this.mCurrentAnimation = makeInAnimation();
                this.mCurrentAnimation.start();
                this.mAnimateInOnLayout = false;
            }
        }
        if (!(this.mTitleLayout == null || this.mCustomView != null || this.mTitleLayout.getVisibility() == 8)) {
            x += positionChild(this.mTitleLayout, x, y, contentHeight, isLayoutRtl);
        }
        if (this.mCustomView != null) {
            int x3 = x + positionChild(this.mCustomView, x, y, contentHeight, isLayoutRtl);
        }
        int x4 = isLayoutRtl ? getPaddingLeft() : (r - l) - getPaddingRight();
        if (this.mLinearContainer != null) {
            int x5 = x4 + positionChild(this.mLinearContainer, x4, y, getActionMenuHight(), !isLayoutRtl);
        }
    }

    public void onAnimationStart(View view) {
    }

    public void onAnimationEnd(View view) {
        if (this.mAnimationMode == 2) {
            killMode();
        }
        this.mAnimationMode = 0;
    }

    public void onAnimationCancel(View view) {
    }

    public boolean shouldDelayChildPressedState() {
        return false;
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        if (VERSION.SDK_INT < 14) {
            return;
        }
        if (event.getEventType() == 32) {
            event.setSource(this);
            event.setClassName(getClass().getName());
            event.setPackageName(getContext().getPackageName());
            event.setContentDescription(this.mTitle);
            return;
        }
        super.onInitializeAccessibilityEvent(event);
    }

    public void setTitleOptional(boolean titleOptional) {
        if (titleOptional != this.mTitleOptional) {
            requestLayout();
        }
        this.mTitleOptional = titleOptional;
    }

    public boolean isTitleOptional() {
        return this.mTitleOptional;
    }

    public int getActionMenuHight() {
        return getContext().getResources().getDimensionPixelSize(R.dimen.nubia_action_bar_menu_default_height);
    }
}
