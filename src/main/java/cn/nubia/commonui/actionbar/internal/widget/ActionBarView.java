package cn.nubia.commonui.actionbar.internal.widget;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.constraint.solver.widgets.ConstraintAnchor;
import android.support.v4.view.GravityCompat;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window.Callback;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import cn.nubia.commonui.R;
import cn.nubia.commonui.actionbar.app.ActionBar;
import cn.nubia.commonui.actionbar.internal.transition.ActionBarTransition;
import cn.nubia.commonui.actionbar.internal.view.menu.ActionMenuItem;
import cn.nubia.commonui.actionbar.internal.view.menu.MenuBuilder;
import cn.nubia.commonui.actionbar.internal.view.menu.MenuItemImpl;
import cn.nubia.commonui.actionbar.internal.view.menu.MenuPresenter;
import cn.nubia.commonui.actionbar.internal.view.menu.MenuView;
import cn.nubia.commonui.actionbar.internal.view.menu.SubMenuBuilder;
import cn.nubia.commonui.actionbar.internal.widget.AdapterViewCompat.OnItemSelectedListener;
import cn.nubia.commonui.actionbar.view.CollapsibleActionView;
import cn.nubia.commonui.actionbar.widget.ActionMenuPresenter;
import cn.nubia.commonui.actionbar.widget.ActionMenuView;
import cn.nubia.commonui.actionbar.widget.LinearLayoutCompat;

public class ActionBarView extends AbsActionBarView implements DecorToolbar {
    private static final int DEFAULT_CUSTOM_GRAVITY = 8388627;
    public static final int DISPLAY_DEFAULT = 0;
    private static final int DISPLAY_RELAYOUT_MASK = 63;
    private static final String TAG = "NubiaWidget";
    private static final float TITLEVIEW_ALPHA_NORMAL = 0.93f;
    private static final float TITLEVIEW_ALPHA_PRESSED = 0.3f;
    private boolean mActionBarTopShow = true;
    private Context mContext;
    private ActionBarContextView mContextView;
    /* access modifiers changed from: private */
    public View mCustomNavView;
    private int mDefaultUpDescription = R.string.action_bar_up_description;
    /* access modifiers changed from: private */
    public int mDisplayOptions = -1;
    View mExpandedActionView;
    private final OnClickListener mExpandedActionViewUpListener = new OnClickListener() {
        public void onClick(View v) {
            MenuItemImpl item = ActionBarView.this.mExpandedMenuPresenter.mCurrentExpandedItem;
            if (item != null) {
                item.collapseActionView();
            }
        }
    };
    /* access modifiers changed from: private */
    public HomeView mExpandedHomeLayout;
    /* access modifiers changed from: private */
    public ExpandedActionViewMenuPresenter mExpandedMenuPresenter;
    private CharSequence mHomeDescription;
    private int mHomeDescriptionRes;
    /* access modifiers changed from: private */
    public HomeView mHomeLayout;
    /* access modifiers changed from: private */
    public Drawable mIcon;
    private boolean mIncludeTabs;
    private int mIndeterminateProgressStyle;
    private ProgressBar mIndeterminateProgressView;
    private boolean mIsCollapsible;
    private int mItemPadding;
    private LinearLayoutCompat mListNavLayout;
    private Drawable mLogo;
    /* access modifiers changed from: private */
    public ActionMenuItem mLogoNavItem;
    /* access modifiers changed from: private */
    public boolean mMenuPrepared;
    private OnItemSelectedListener mNavItemSelectedListener;
    private int mNavigationMode;
    private MenuBuilder mOptionsMenu;
    private int mProgressBarPadding;
    private int mProgressStyle;
    private ProgressBar mProgressView;
    /* access modifiers changed from: private */
    public SpinnerCompat mSpinner;
    private SpinnerAdapter mSpinnerAdapter;
    private CharSequence mSubtitle;
    private int mSubtitleStyleRes;
    private TextView mSubtitleView;
    /* access modifiers changed from: private */
    public ScrollingTabContainerView mTabScrollView;
    private Runnable mTabSelector;
    private CharSequence mTitle;
    /* access modifiers changed from: private */
    public LinearLayout mTitleLayout;
    private int mTitleStyleRes;
    /* access modifiers changed from: private */
    public TextView mTitleView;
    private final OnClickListener mUpClickListener = new OnClickListener() {
        public void onClick(View v) {
            if (ActionBarView.this.mMenuPrepared) {
                ActionBarView.this.mWindowCallback.onMenuItemSelected(0, ActionBarView.this.mLogoNavItem);
            }
        }
    };
    /* access modifiers changed from: private */
    public ViewGroup mUpGoerFive;
    private boolean mUserTitle;
    /* access modifiers changed from: private */
    public boolean mWasHomeEnabled;
    Callback mWindowCallback;

    private class ExpandedActionViewMenuPresenter implements MenuPresenter {
        MenuItemImpl mCurrentExpandedItem;
        MenuBuilder mMenu;

        private ExpandedActionViewMenuPresenter() {
        }

        public void initForMenu(Context context, MenuBuilder menu) {
            if (!(this.mMenu == null || this.mCurrentExpandedItem == null)) {
                this.mMenu.collapseItemActionView(this.mCurrentExpandedItem);
            }
            this.mMenu = menu;
        }

        public MenuView getMenuView(ViewGroup root) {
            return null;
        }

        public void updateMenuView(boolean cleared) {
            if (this.mCurrentExpandedItem != null) {
                boolean found = false;
                if (this.mMenu != null) {
                    int count = this.mMenu.size();
                    int i = 0;
                    while (true) {
                        if (i >= count) {
                            break;
                        } else if (this.mMenu.getItem(i) == this.mCurrentExpandedItem) {
                            found = true;
                            break;
                        } else {
                            i++;
                        }
                    }
                }
                if (!found) {
                    collapseItemActionView(this.mMenu, this.mCurrentExpandedItem);
                }
            }
        }

        public void setCallback(MenuPresenter.Callback cb) {
        }

        public boolean onSubMenuSelected(SubMenuBuilder subMenu) {
            return false;
        }

        public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
        }

        public boolean flagActionItems() {
            return false;
        }

        public boolean expandItemActionView(MenuBuilder menu, MenuItemImpl item) {
            ActionBarTransition.beginDelayedTransition(ActionBarView.this);
            ActionBarView.this.mExpandedActionView = item.getActionView();
            ActionBarView.this.mExpandedHomeLayout.setIcon(ActionBarView.this.mIcon.getConstantState().newDrawable(ActionBarView.this.getResources()));
            this.mCurrentExpandedItem = item;
            if (ActionBarView.this.mExpandedActionView.getParent() != ActionBarView.this) {
                ActionBarView.this.addView(ActionBarView.this.mExpandedActionView);
            }
            if (ActionBarView.this.mExpandedHomeLayout.getParent() != ActionBarView.this.mUpGoerFive) {
                ActionBarView.this.mUpGoerFive.addView(ActionBarView.this.mExpandedHomeLayout);
            }
            ActionBarView.this.mHomeLayout.setVisibility(8);
            if (ActionBarView.this.mTitleLayout != null) {
                ActionBarView.this.mTitleLayout.setVisibility(8);
            }
            if (ActionBarView.this.mTabScrollView != null) {
                ActionBarView.this.mTabScrollView.setVisibility(8);
            }
            if (ActionBarView.this.mSpinner != null) {
                ActionBarView.this.mSpinner.setVisibility(8);
            }
            if (ActionBarView.this.mCustomNavView != null) {
                ActionBarView.this.mCustomNavView.setVisibility(8);
            }
            ActionBarView.this.setHomeButtonEnabled(false, false);
            ActionBarView.this.requestLayout();
            item.setActionViewExpanded(true);
            if (ActionBarView.this.mExpandedActionView instanceof CollapsibleActionView) {
                ((CollapsibleActionView) ActionBarView.this.mExpandedActionView).onActionViewExpanded();
            }
            return true;
        }

        public boolean collapseItemActionView(MenuBuilder menu, MenuItemImpl item) {
            ActionBarTransition.beginDelayedTransition(ActionBarView.this);
            if (ActionBarView.this.mExpandedActionView instanceof CollapsibleActionView) {
                ((CollapsibleActionView) ActionBarView.this.mExpandedActionView).onActionViewCollapsed();
            }
            ActionBarView.this.removeView(ActionBarView.this.mExpandedActionView);
            ActionBarView.this.mUpGoerFive.removeView(ActionBarView.this.mExpandedHomeLayout);
            ActionBarView.this.mExpandedActionView = null;
            if ((ActionBarView.this.mDisplayOptions & 2) != 0) {
                ActionBarView.this.mHomeLayout.setVisibility(0);
            }
            if ((ActionBarView.this.mDisplayOptions & 8) != 0) {
                if (ActionBarView.this.mTitleLayout == null) {
                    ActionBarView.this.initTitle();
                } else {
                    ActionBarView.this.mTitleLayout.setVisibility(0);
                }
            }
            if (ActionBarView.this.mTabScrollView != null) {
                ActionBarView.this.mTabScrollView.setVisibility(0);
            }
            if (ActionBarView.this.mSpinner != null) {
                ActionBarView.this.mSpinner.setVisibility(0);
            }
            if (ActionBarView.this.mCustomNavView != null) {
                ActionBarView.this.mCustomNavView.setVisibility(0);
            }
            ActionBarView.this.mExpandedHomeLayout.setIcon(null);
            this.mCurrentExpandedItem = null;
            ActionBarView.this.setHomeButtonEnabled(ActionBarView.this.mWasHomeEnabled);
            ActionBarView.this.requestLayout();
            item.setActionViewExpanded(false);
            return true;
        }

        public int getId() {
            return 0;
        }

        public Parcelable onSaveInstanceState() {
            return null;
        }

        public void onRestoreInstanceState(Parcelable state) {
        }
    }

    private static class HomeView extends FrameLayout {
        private static final long DEFAULT_TRANSITION_DURATION = 150;
        private Drawable mDefaultUpIndicator;
        private ImageView mIconView;
        private int mPaddingStart;
        private int mStartOffset;
        private Drawable mUpIndicator;
        private int mUpIndicatorRes;
        private ImageView mUpView;
        private int mUpWidth;

        public HomeView(Context context) {
            this(context, null);
        }

        @SuppressLint({"NewApi"})
        public HomeView(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.mPaddingStart = 0;
            LayoutTransition t = getLayoutTransition();
            if (t != null) {
                t.setDuration(DEFAULT_TRANSITION_DURATION);
            }
            this.mPaddingStart = context.getResources().getDimensionPixelSize(R.dimen.nubia_action_bar_home_title_margin_left);
        }

        public void setShowUp(boolean isUp) {
            this.mUpView.setVisibility(isUp ? 0 : 8);
        }

        public void setShowIcon(boolean showIcon) {
            this.mIconView.setVisibility(showIcon ? 0 : 8);
        }

        public void setIcon(Drawable icon) {
            this.mIconView.setImageDrawable(icon);
        }

        public void setUpIndicator(Drawable d) {
            this.mUpIndicator = d;
            this.mUpIndicatorRes = 0;
            updateUpIndicator();
        }

        public void setDefaultUpIndicator(Drawable d) {
            this.mDefaultUpIndicator = d;
            updateUpIndicator();
        }

        public void setUpIndicator(int resId) {
            this.mUpIndicatorRes = resId;
            this.mUpIndicator = null;
            updateUpIndicator();
        }

        @SuppressLint({"NewApi"})
        private void updateUpIndicator() {
            if (this.mUpIndicator != null) {
                this.mUpView.setImageDrawable(this.mUpIndicator);
            } else if (this.mUpIndicatorRes != 0) {
                this.mUpView.setImageDrawable(getContext().getDrawable(this.mUpIndicatorRes));
            } else {
                this.mUpView.setImageDrawable(this.mDefaultUpIndicator);
            }
        }

        /* access modifiers changed from: protected */
        @SuppressLint({"NewApi"})
        public void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);
            if (this.mUpIndicatorRes != 0) {
                updateUpIndicator();
            }
        }

        public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
            onPopulateAccessibilityEvent(event);
            return true;
        }

        @SuppressLint({"NewApi"})
        public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
            super.onPopulateAccessibilityEvent(event);
            CharSequence cdesc = getContentDescription();
            if (!TextUtils.isEmpty(cdesc)) {
                event.getText().add(cdesc);
            }
        }

        @SuppressLint({"NewApi"})
        public boolean dispatchHoverEvent(MotionEvent event) {
            return onHoverEvent(event);
        }

        /* access modifiers changed from: protected */
        public void onFinishInflate() {
            this.mUpView = (ImageView) findViewById(R.id.up);
            this.mIconView = (ImageView) findViewById(R.id.home);
            this.mDefaultUpIndicator = this.mUpView.getDrawable();
        }

        public int getStartOffset() {
            if (this.mUpView.getVisibility() == 8) {
                return this.mStartOffset;
            }
            return 0;
        }

        public int getUpWidth() {
            return this.mUpWidth;
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width;
            measureChildWithMargins(this.mUpView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            LayoutParams upLp = (LayoutParams) this.mUpView.getLayoutParams();
            int upMargins = upLp.leftMargin + upLp.rightMargin;
            this.mUpWidth = this.mUpView.getMeasuredWidth();
            this.mStartOffset = this.mUpWidth + upMargins;
            if (this.mUpView.getVisibility() == 8) {
                width = this.mPaddingStart;
            } else {
                width = this.mPaddingStart + this.mStartOffset;
            }
            int height = upLp.topMargin + this.mUpView.getMeasuredHeight() + upLp.bottomMargin;
            if (this.mIconView.getVisibility() != 8) {
                measureChildWithMargins(this.mIconView, widthMeasureSpec, width, heightMeasureSpec, 0);
                LayoutParams iconLp = (LayoutParams) this.mIconView.getLayoutParams();
                width += iconLp.leftMargin + this.mIconView.getMeasuredWidth() + iconLp.rightMargin;
                height = Math.max(height, iconLp.topMargin + this.mIconView.getMeasuredHeight() + iconLp.bottomMargin);
            } else if (upMargins < 0) {
                width -= upMargins;
            }
            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            switch (widthMode) {
                case Integer.MIN_VALUE:
                    width = Math.min(width, widthSize);
                    break;
                case 1073741824:
                    width = widthSize;
                    break;
            }
            switch (heightMode) {
                case Integer.MIN_VALUE:
                    height = Math.min(height, heightSize);
                    break;
                case 1073741824:
                    height = heightSize;
                    break;
            }
            setMeasuredDimension(width, height);
        }

        /* access modifiers changed from: protected */
        @SuppressLint({"NewApi"})
        public void onLayout(boolean changed, int l, int t, int r, int b) {
            int iconLeft;
            int iconRight;
            int upRight;
            int upLeft;
            int vCenter = (b - t) / 2;
            boolean isLayoutRtl = ViewUtils.isLayoutRtl(this);
            int width = getWidth();
            int upOffset = 0;
            if (this.mUpView.getVisibility() != 8) {
                LayoutParams upLp = (LayoutParams) this.mUpView.getLayoutParams();
                int upHeight = this.mUpView.getMeasuredHeight();
                int upWidth = this.mUpView.getMeasuredWidth();
                upOffset = upLp.leftMargin + upWidth + upLp.rightMargin;
                int upTop = vCenter - (upHeight / 2);
                int upBottom = upTop + upHeight;
                if (isLayoutRtl) {
                    upRight = width;
                    upLeft = upRight - upWidth;
                    r -= upOffset;
                } else {
                    upRight = upWidth + this.mPaddingStart;
                    upLeft = this.mPaddingStart;
                    l += upOffset;
                }
                if (getLayoutDirection() == 1) {
                    this.mUpView.layout(upLeft - this.mPaddingStart, upTop, upRight - this.mPaddingStart, upBottom);
                } else {
                    this.mUpView.layout(upLeft, upTop, upRight, upBottom);
                }
            }
            LayoutParams iconLp = (LayoutParams) this.mIconView.getLayoutParams();
            int iconHeight = this.mIconView.getMeasuredHeight();
            int iconWidth = this.mIconView.getMeasuredWidth();
            int hCenter = (r - l) / 2;
            int iconTop = Math.max(iconLp.topMargin, vCenter - (iconHeight / 2));
            int iconBottom = iconTop + iconHeight;
            int delta = Math.max(iconLp.getMarginStart(), hCenter - (iconWidth / 2));
            if (isLayoutRtl) {
                iconRight = (width - upOffset) - delta;
                iconLeft = iconRight - iconWidth;
            } else {
                iconLeft = upOffset + delta;
                iconRight = iconLeft + iconWidth;
            }
            this.mIconView.layout(iconLeft, iconTop, iconRight, iconBottom);
        }

        public int getNubiaStartOffset() {
            return 0;
        }

        public void setNubiaShowIcon(boolean showIcon) {
            this.mIconView.setVisibility(8);
        }
    }

    static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        int expandedMenuItemId;
        boolean isOverflowOpen;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.expandedMenuItemId = in.readInt();
            this.isOverflowOpen = in.readInt() != 0;
        }

        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.expandedMenuItemId);
            out.writeInt(this.isOverflowOpen ? 1 : 0);
        }
    }

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

    public /* bridge */ /* synthetic */ boolean hideOverflowMenu() {
        return super.hideOverflowMenu();
    }

    public /* bridge */ /* synthetic */ boolean isOverflowMenuShowPending() {
        return super.isOverflowMenuShowPending();
    }

    public /* bridge */ /* synthetic */ boolean isOverflowMenuShowing() {
        return super.isOverflowMenuShowing();
    }

    public /* bridge */ /* synthetic */ boolean isOverflowReserved() {
        return super.isOverflowReserved();
    }

    public /* bridge */ /* synthetic */ void postShowOverflowMenu() {
        super.postShowOverflowMenu();
    }

    public /* bridge */ /* synthetic */ void setContentHeight(int i) {
        super.setContentHeight(i);
    }

    public /* bridge */ /* synthetic */ void setSplitView(ViewGroup viewGroup) {
        super.setSplitView(viewGroup);
    }

    public /* bridge */ /* synthetic */ void setSplitWhenNarrow(boolean z) {
        super.setSplitWhenNarrow(z);
    }

    public /* bridge */ /* synthetic */ boolean showOverflowMenu() {
        return super.showOverflowMenu();
    }

    @SuppressLint({"NewApi"})
    public ActionBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        setBackgroundResource(0);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ActionBar, R.attr.actionBarStyle, 0);
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        PackageManager packageManager = context.getPackageManager();
        this.mNavigationMode = a.getInt(R.styleable.ActionBar_navigationMode, 0);
        this.mTitle = a.getText(R.styleable.ActionBar_title);
        this.mSubtitle = a.getText(R.styleable.ActionBar_subtitle);
        this.mLogo = a.getDrawable(R.styleable.ActionBar_logo);
        this.mIcon = a.getDrawable(R.styleable.ActionBar_icon);
        LayoutInflater inflater = LayoutInflater.from(context);
        int homeResId = a.getResourceId(R.styleable.ActionBar_homeLayout, R.layout.abc_action_bar_home);
        this.mUpGoerFive = (ViewGroup) inflater.inflate(R.layout.abc_action_bar_up_container, this, false);
        this.mHomeLayout = (HomeView) inflater.inflate(homeResId, this.mUpGoerFive, false);
        this.mUpGoerFive.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent event) {
                switch (event.getAction()) {
                    case 0:
                    case 2:
                        if (ActionBarView.this.mTitleView != null) {
                            if (!ActionBarView.this.isTouchInView(arg0, event.getRawX(), event.getRawY())) {
                                ActionBarView.this.mTitleView.setAlpha(ActionBarView.TITLEVIEW_ALPHA_NORMAL);
                                break;
                            } else {
                                ActionBarView.this.mTitleView.setAlpha(ActionBarView.TITLEVIEW_ALPHA_PRESSED);
                                break;
                            }
                        }
                        break;
                    case 1:
                    case 3:
                        if (ActionBarView.this.mTitleView != null) {
                            ActionBarView.this.mTitleView.setAlpha(ActionBarView.TITLEVIEW_ALPHA_NORMAL);
                            break;
                        }
                        break;
                }
                return false;
            }
        });
        this.mExpandedHomeLayout = (HomeView) inflater.inflate(homeResId, this.mUpGoerFive, false);
        this.mExpandedHomeLayout.setShowUp(true);
        this.mExpandedHomeLayout.setOnClickListener(this.mExpandedActionViewUpListener);
        this.mExpandedHomeLayout.setContentDescription(getResources().getText(this.mDefaultUpDescription));
        Drawable upBackground = this.mUpGoerFive.getBackground();
        if (upBackground != null) {
            this.mExpandedHomeLayout.setBackground(upBackground.getConstantState().newDrawable());
        }
        this.mExpandedHomeLayout.setEnabled(true);
        this.mExpandedHomeLayout.setFocusable(true);
        this.mTitleStyleRes = a.getResourceId(R.styleable.ActionBar_titleTextStyle, 0);
        this.mSubtitleStyleRes = a.getResourceId(R.styleable.ActionBar_subtitleTextStyle, 0);
        this.mProgressStyle = a.getResourceId(R.styleable.ActionBar_progressBarStyle, 0);
        this.mIndeterminateProgressStyle = a.getResourceId(R.styleable.ActionBar_indeterminateProgressStyle, 0);
        this.mProgressBarPadding = a.getDimensionPixelOffset(R.styleable.ActionBar_progressBarPadding, 0);
        this.mItemPadding = a.getDimensionPixelOffset(R.styleable.ActionBar_itemPadding, 0);
        setDisplayOptions(a.getInt(R.styleable.ActionBar_displayOptions, 0));
        int customNavId = a.getResourceId(R.styleable.ActionBar_customNavigationLayout, 0);
        if (customNavId != 0) {
            this.mCustomNavView = inflater.inflate(customNavId, this, false);
            this.mNavigationMode = 0;
            setDisplayOptions(this.mDisplayOptions | 16);
        }
        this.mContentHeight = a.getLayoutDimension(R.styleable.ActionBar_height, 0);
        a.recycle();
        this.mLogoNavItem = new ActionMenuItem(context, 0, 16908332, 0, 0, this.mTitle);
        this.mUpGoerFive.setOnClickListener(this.mUpClickListener);
        this.mUpGoerFive.setClickable(true);
        this.mUpGoerFive.setFocusable(true);
        if (getImportantForAccessibility() == 0) {
            setImportantForAccessibility(1);
        }
    }

    /* access modifiers changed from: private */
    public boolean isTouchInView(View view, float x, float y) {
        if (view == null) {
            return false;
        }
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int right = left + view.getMeasuredWidth();
        int top = location[1];
        int bottom = top + view.getMeasuredHeight();
        if (y < ((float) top) || y > ((float) (bottom + 20)) || x < ((float) left) || x > ((float) right)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    @SuppressLint({"NewApi"})
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.mTitleView = null;
        this.mSubtitleView = null;
        if (this.mTitleLayout != null && this.mTitleLayout.getParent() == this.mUpGoerFive) {
            this.mUpGoerFive.removeView(this.mTitleLayout);
        }
        this.mTitleLayout = null;
        if ((this.mDisplayOptions & 8) != 0) {
            initTitle();
        }
        if (this.mHomeDescriptionRes != 0) {
            setNavigationContentDescription(this.mHomeDescriptionRes);
        }
        if (this.mTabScrollView != null && this.mIncludeTabs) {
            ViewGroup.LayoutParams lp = this.mTabScrollView.getLayoutParams();
            if (lp != null) {
                lp.width = -2;
                lp.height = -1;
            }
            this.mTabScrollView.setAllowCollapse(true);
        }
    }

    public void setWindowCallback(Callback cb) {
        this.mWindowCallback = cb;
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(this.mTabSelector);
        if (this.mActionMenuPresenter != null) {
            this.mActionMenuPresenter.hideOverflowMenu();
            this.mActionMenuPresenter.hideSubMenus();
        }
    }

    @SuppressLint({"NewApi"})
    public void initProgress() {
        this.mProgressView = new ProgressBar(this.mContext, null, 0, this.mProgressStyle);
        this.mProgressView.setId(R.id.progress_horizontal);
        this.mProgressView.setMax(10000);
        this.mProgressView.setVisibility(8);
        addView(this.mProgressView);
    }

    @SuppressLint({"NewApi"})
    public void initIndeterminateProgress() {
        this.mIndeterminateProgressView = new ProgressBar(this.mContext, null, 0, this.mIndeterminateProgressStyle);
        this.mIndeterminateProgressView.setId(R.id.progress_circular);
        this.mIndeterminateProgressView.setVisibility(8);
        addView(this.mIndeterminateProgressView);
    }

    public void setSplitToolbar(boolean splitActionBar) {
        if (this.mSplitActionBar != splitActionBar) {
            if (this.mMenuView != null) {
                ViewGroup oldParent = (ViewGroup) this.mMenuView.getParent();
                if (oldParent != null) {
                    oldParent.removeView(this.mMenuView);
                }
                if (splitActionBar) {
                    if (this.mSplitView != null) {
                        this.mSplitView.addView(this.mMenuView);
                    }
                    this.mMenuView.getLayoutParams().width = -1;
                } else {
                    addView(this.mMenuView);
                    this.mMenuView.getLayoutParams().width = -2;
                }
                this.mMenuView.requestLayout();
            }
            if (this.mSplitView != null) {
                this.mSplitView.setVisibility((!splitActionBar || !this.mActionBarTopShow) ? 8 : 0);
            }
            if (this.mActionMenuPresenter != null) {
                if (!splitActionBar) {
                    this.mActionMenuPresenter.setExpandedActionViewsExclusive(getResources().getBoolean(R.bool.action_bar_expanded_action_views_exclusive));
                } else {
                    this.mActionMenuPresenter.setExpandedActionViewsExclusive(false);
                    this.mActionMenuPresenter.setWidthLimit(getContext().getResources().getDisplayMetrics().widthPixels, true);
                    this.mActionMenuPresenter.setItemLimit(ConstraintAnchor.ANY_GROUP);
                }
            }
            super.setSplitToolbar(splitActionBar);
        }
    }

    public boolean isSplit() {
        return this.mSplitActionBar;
    }

    public boolean canSplit() {
        return true;
    }

    public boolean hasEmbeddedTabs() {
        return this.mIncludeTabs;
    }

    public void setEmbeddedTabView(ScrollingTabContainerView tabs) {
        if (this.mTabScrollView != null) {
            removeView(this.mTabScrollView);
        }
        this.mTabScrollView = tabs;
        this.mIncludeTabs = tabs != null;
        if (this.mIncludeTabs && this.mNavigationMode == 2) {
            addView(this.mTabScrollView);
            ViewGroup.LayoutParams lp = this.mTabScrollView.getLayoutParams();
            lp.width = -2;
            lp.height = -1;
            tabs.setAllowCollapse(true);
        }
    }

    public void setMenuPrepared() {
        this.mMenuPrepared = true;
    }

    public void setMenu(Menu menu, MenuPresenter.Callback cb) {
        ActionMenuView menuView;
        if (menu != this.mOptionsMenu) {
            if (this.mOptionsMenu != null) {
                this.mOptionsMenu.removeMenuPresenter(this.mActionMenuPresenter);
                this.mOptionsMenu.removeMenuPresenter(this.mExpandedMenuPresenter);
            }
            MenuBuilder builder = (MenuBuilder) menu;
            this.mOptionsMenu = builder;
            if (this.mMenuView != null) {
                ViewGroup oldParent = (ViewGroup) this.mMenuView.getParent();
                if (oldParent != null) {
                    oldParent.removeView(this.mMenuView);
                }
            }
            if (this.mActionMenuPresenter == null) {
                this.mActionMenuPresenter = new ActionMenuPresenter(this.mContext);
                this.mActionMenuPresenter.setCallback(cb);
                this.mActionMenuPresenter.setId(R.id.action_menu_presenter);
                this.mExpandedMenuPresenter = new ExpandedActionViewMenuPresenter();
            }
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(-2, -1);
            if (!this.mSplitActionBar) {
                this.mActionMenuPresenter.setExpandedActionViewsExclusive(getResources().getBoolean(R.bool.action_bar_expanded_action_views_exclusive));
                configPresenters(builder);
                menuView = (ActionMenuView) this.mActionMenuPresenter.getMenuView(this);
                ViewGroup oldParent2 = (ViewGroup) menuView.getParent();
                if (!(oldParent2 == null || oldParent2 == this)) {
                    oldParent2.removeView(menuView);
                }
                addView(menuView, layoutParams);
            } else {
                this.mActionMenuPresenter.setExpandedActionViewsExclusive(false);
                this.mActionMenuPresenter.setWidthLimit(getContext().getResources().getDisplayMetrics().widthPixels, true);
                this.mActionMenuPresenter.setItemLimit(ConstraintAnchor.ANY_GROUP);
                layoutParams.width = -1;
                layoutParams.height = -2;
                configPresenters(builder);
                menuView = (ActionMenuView) this.mActionMenuPresenter.getMenuView(this);
                if (this.mSplitView != null) {
                    ViewGroup oldParent3 = (ViewGroup) menuView.getParent();
                    if (!(oldParent3 == null || oldParent3 == this.mSplitView)) {
                        oldParent3.removeView(menuView);
                    }
                    menuView.setVisibility(getAnimatedVisibility());
                    this.mSplitView.addView(menuView, layoutParams);
                } else {
                    menuView.setLayoutParams(layoutParams);
                }
            }
            this.mMenuView = menuView;
        }
    }

    private void configPresenters(MenuBuilder builder) {
        if (builder != null) {
            builder.addMenuPresenter(this.mActionMenuPresenter, this.mPopupContext);
            builder.addMenuPresenter(this.mExpandedMenuPresenter, this.mPopupContext);
            return;
        }
        this.mActionMenuPresenter.initForMenu(this.mPopupContext, null);
        this.mExpandedMenuPresenter.initForMenu(this.mPopupContext, null);
        this.mActionMenuPresenter.updateMenuView(true);
        this.mExpandedMenuPresenter.updateMenuView(true);
    }

    public boolean hasExpandedActionView() {
        return (this.mExpandedMenuPresenter == null || this.mExpandedMenuPresenter.mCurrentExpandedItem == null) ? false : true;
    }

    public void collapseActionView() {
        MenuItemImpl item = this.mExpandedMenuPresenter == null ? null : this.mExpandedMenuPresenter.mCurrentExpandedItem;
        if (item != null) {
            item.collapseActionView();
        }
    }

    public void setCustomView(View view) {
        boolean showCustom = (this.mDisplayOptions & 16) != 0;
        if (showCustom) {
            ActionBarTransition.beginDelayedTransition(this);
        }
        if (this.mCustomNavView != null && showCustom) {
            removeView(this.mCustomNavView);
        }
        this.mCustomNavView = view;
        if (this.mCustomNavView != null && showCustom) {
            addView(this.mCustomNavView);
        }
    }

    public CharSequence getTitle() {
        return this.mTitle;
    }

    public void setTitle(CharSequence title) {
        this.mUserTitle = true;
        setTitleImpl(title);
    }

    public void setWindowTitle(CharSequence title) {
        if (!this.mUserTitle) {
            setTitleImpl(title);
        }
    }

    private void setTitleImpl(CharSequence title) {
        int i = 0;
        ActionBarTransition.beginDelayedTransition(this);
        this.mTitle = title;
        if (this.mTitleView != null) {
            this.mTitleView.setText(title);
            boolean visible = this.mExpandedActionView == null && (this.mDisplayOptions & 8) != 0 && (!TextUtils.isEmpty(this.mTitle) || !TextUtils.isEmpty(this.mSubtitle));
            LinearLayout linearLayout = this.mTitleLayout;
            if (!visible) {
                i = 8;
            }
            linearLayout.setVisibility(i);
        }
        if (this.mLogoNavItem != null) {
            this.mLogoNavItem.setTitle(title);
        }
        updateHomeAccessibility(this.mUpGoerFive.isEnabled());
    }

    public CharSequence getSubtitle() {
        return this.mSubtitle;
    }

    public void setSubtitle(CharSequence subtitle) {
        boolean visible;
        int i = 0;
        ActionBarTransition.beginDelayedTransition(this);
        this.mSubtitle = subtitle;
        if (this.mSubtitleView != null) {
            this.mSubtitleView.setText(subtitle);
            this.mSubtitleView.setVisibility(subtitle != null ? 0 : 8);
            if (this.mExpandedActionView != null || (this.mDisplayOptions & 8) == 0 || (TextUtils.isEmpty(this.mTitle) && TextUtils.isEmpty(this.mSubtitle))) {
                visible = false;
            } else {
                visible = true;
            }
            LinearLayout linearLayout = this.mTitleLayout;
            if (!visible) {
                i = 8;
            }
            linearLayout.setVisibility(i);
        }
        updateHomeAccessibility(this.mUpGoerFive.isEnabled());
    }

    public void setHomeButtonEnabled(boolean enable) {
        setHomeButtonEnabled(enable, true);
    }

    /* access modifiers changed from: private */
    public void setHomeButtonEnabled(boolean enable, boolean recordState) {
        if (recordState) {
            this.mWasHomeEnabled = enable;
        }
        if (this.mExpandedActionView == null) {
            this.mUpGoerFive.setEnabled(enable);
            this.mUpGoerFive.setFocusable(enable);
            updateHomeAccessibility(enable);
        }
    }

    @SuppressLint({"NewApi"})
    private void updateHomeAccessibility(boolean homeEnabled) {
        if (!homeEnabled) {
            this.mUpGoerFive.setContentDescription(null);
            this.mUpGoerFive.setImportantForAccessibility(2);
            return;
        }
        this.mUpGoerFive.setImportantForAccessibility(0);
        this.mUpGoerFive.setContentDescription(buildHomeContentDescription());
    }

    private CharSequence buildHomeContentDescription() {
        CharSequence homeDesc;
        if (this.mHomeDescription != null) {
            homeDesc = this.mHomeDescription;
        } else if ((this.mDisplayOptions & 4) != 0) {
            homeDesc = this.mContext.getResources().getText(this.mDefaultUpDescription);
        } else {
            homeDesc = this.mContext.getResources().getText(R.string.action_bar_home_description);
        }
        CharSequence title = getTitle();
        CharSequence subtitle = getSubtitle();
        if (TextUtils.isEmpty(title)) {
            return homeDesc;
        }
        if (!TextUtils.isEmpty(subtitle)) {
            return getResources().getString(R.string.action_bar_home_subtitle_description_format, new Object[]{title, subtitle, homeDesc});
        }
        return getResources().getString(R.string.action_bar_home_description_format, new Object[]{title, homeDesc});
    }

    public void setDisplayOptions(int options) {
        boolean showHome;
        boolean homeAsUp;
        boolean titleUp;
        boolean setUp;
        int flagsChanged = -1;
        if (this.mDisplayOptions != -1) {
            flagsChanged = options ^ this.mDisplayOptions;
        }
        this.mDisplayOptions = options;
        if ((flagsChanged & 63) != 0) {
            ActionBarTransition.beginDelayedTransition(this);
            if ((flagsChanged & 4) != 0) {
                if ((options & 4) != 0) {
                    setUp = true;
                } else {
                    setUp = false;
                }
                this.mHomeLayout.setShowUp(setUp);
                if (setUp) {
                    setHomeButtonEnabled(true);
                }
            }
            if ((flagsChanged & 1) != 0) {
                this.mHomeLayout.setIcon(this.mLogo != null && (options & 1) != 0 ? this.mLogo : this.mIcon);
            }
            if ((flagsChanged & 8) != 0) {
                if ((options & 8) != 0) {
                    initTitle();
                } else {
                    this.mUpGoerFive.removeView(this.mTitleLayout);
                }
            }
            if ((options & 2) != 0) {
                showHome = true;
            } else {
                showHome = false;
            }
            if ((this.mDisplayOptions & 4) != 0) {
                homeAsUp = true;
            } else {
                homeAsUp = false;
            }
            if (showHome || !homeAsUp) {
                titleUp = false;
            } else {
                titleUp = true;
            }
            this.mHomeLayout.setShowIcon(showHome);
            this.mHomeLayout.setVisibility(((showHome || titleUp) && this.mExpandedActionView == null) ? 0 : 8);
            if (!((flagsChanged & 16) == 0 || this.mCustomNavView == null)) {
                if ((options & 16) != 0) {
                    addView(this.mCustomNavView);
                } else {
                    removeView(this.mCustomNavView);
                }
            }
            if (!(this.mTitleLayout == null || (flagsChanged & 32) == 0)) {
                if ((options & 32) != 0) {
                    this.mTitleView.setSingleLine(false);
                    this.mTitleView.setMaxLines(2);
                } else {
                    this.mTitleView.setMaxLines(1);
                    this.mTitleView.setSingleLine(true);
                }
            }
            requestLayout();
        } else {
            invalidate();
        }
        updateHomeAccessibility(this.mUpGoerFive.isEnabled());
    }

    public void setIcon(Drawable icon) {
        this.mIcon = icon;
        if (icon != null && ((this.mDisplayOptions & 1) == 0 || this.mLogo == null)) {
            this.mHomeLayout.setIcon(icon);
        }
        if (this.mExpandedActionView != null) {
            this.mExpandedHomeLayout.setIcon(this.mIcon.getConstantState().newDrawable(getResources()));
        }
    }

    @SuppressLint({"NewApi"})
    public void setIcon(int resId) {
        setIcon(resId != 0 ? this.mContext.getDrawable(resId) : null);
    }

    public boolean hasIcon() {
        return this.mIcon != null;
    }

    public void setLogo(Drawable logo) {
        this.mLogo = logo;
        if (logo != null && (this.mDisplayOptions & 1) != 0) {
            this.mHomeLayout.setIcon(logo);
        }
    }

    @SuppressLint({"NewApi"})
    public void setLogo(int resId) {
        setLogo(resId != 0 ? this.mContext.getDrawable(resId) : null);
    }

    public boolean hasLogo() {
        return this.mLogo != null;
    }

    public void setNavigationMode(int mode) {
        int oldMode = this.mNavigationMode;
        if (mode != oldMode) {
            ActionBarTransition.beginDelayedTransition(this);
            switch (oldMode) {
                case 1:
                    if (this.mListNavLayout != null) {
                        removeView(this.mListNavLayout);
                        break;
                    }
                    break;
                case 2:
                    if (this.mTabScrollView != null && this.mIncludeTabs) {
                        removeView(this.mTabScrollView);
                        break;
                    }
            }
            switch (mode) {
                case 1:
                    if (this.mSpinner == null) {
                        this.mSpinner = new SpinnerCompat(this.mContext, null, R.attr.actionDropDownStyle);
                        this.mSpinner.setId(R.id.action_bar_spinner);
                        this.mListNavLayout = new LinearLayoutCompat(this.mContext, null, R.attr.actionBarTabBarStyle);
                        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(-2, -1);
                        params.gravity = 17;
                        this.mListNavLayout.addView(this.mSpinner, params);
                    }
                    if (this.mSpinner.getAdapter() != this.mSpinnerAdapter) {
                        this.mSpinner.setAdapter(this.mSpinnerAdapter);
                    }
                    this.mSpinner.setOnItemSelectedListener(this.mNavItemSelectedListener);
                    addView(this.mListNavLayout);
                    break;
                case 2:
                    if (this.mTabScrollView != null && this.mIncludeTabs) {
                        addView(this.mTabScrollView);
                        break;
                    }
            }
            this.mNavigationMode = mode;
            requestLayout();
        }
    }

    public void setDropdownParams(SpinnerAdapter adapter, OnItemSelectedListener l) {
        this.mSpinnerAdapter = adapter;
        this.mNavItemSelectedListener = l;
        if (this.mSpinner != null) {
            this.mSpinner.setAdapter(adapter);
            this.mSpinner.setOnItemSelectedListener(l);
        }
    }

    public int getDropdownItemCount() {
        if (this.mSpinnerAdapter != null) {
            return this.mSpinnerAdapter.getCount();
        }
        return 0;
    }

    public void setDropdownSelectedPosition(int position) {
        this.mSpinner.setSelection(position);
    }

    public int getDropdownSelectedPosition() {
        return this.mSpinner.getSelectedItemPosition();
    }

    public View getCustomView() {
        return this.mCustomNavView;
    }

    public int getNavigationMode() {
        return this.mNavigationMode;
    }

    public int getDisplayOptions() {
        return this.mDisplayOptions;
    }

    public ViewGroup getViewGroup() {
        return this;
    }

    /* access modifiers changed from: protected */
    public ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new ActionBar.LayoutParams((int) DEFAULT_CUSTOM_GRAVITY);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mUpGoerFive.addView(this.mHomeLayout, 0);
        addView(this.mUpGoerFive);
        if (this.mCustomNavView != null && (this.mDisplayOptions & 16) != 0) {
            ViewParent parent = this.mCustomNavView.getParent();
            if (parent != this) {
                if (parent instanceof ViewGroup) {
                    ((ViewGroup) parent).removeView(this.mCustomNavView);
                }
                addView(this.mCustomNavView);
            }
        }
    }

    /* access modifiers changed from: private */
    public void initTitle() {
        if (this.mTitleLayout == null) {
            this.mTitleLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.abc_action_bar_title_item, this, false);
            this.mTitleView = (TextView) this.mTitleLayout.findViewById(R.id.action_bar_title);
            this.mSubtitleView = (TextView) this.mTitleLayout.findViewById(R.id.action_bar_subtitle);
            if (this.mTitleStyleRes != 0) {
                this.mTitleView.setTextAppearance(this.mContext, this.mTitleStyleRes);
            }
            if (this.mTitle != null) {
                this.mTitleView.setText(this.mTitle);
            }
            if (this.mSubtitleStyleRes != 0) {
                this.mSubtitleView.setTextAppearance(this.mContext, this.mSubtitleStyleRes);
            }
            if (this.mSubtitle != null) {
                this.mSubtitleView.setText(this.mSubtitle);
                this.mSubtitleView.setVisibility(0);
            }
        }
        ActionBarTransition.beginDelayedTransition(this);
        this.mUpGoerFive.addView(this.mTitleLayout);
        if (this.mExpandedActionView != null || (TextUtils.isEmpty(this.mTitle) && TextUtils.isEmpty(this.mSubtitle))) {
            this.mTitleLayout.setVisibility(8);
        } else {
            this.mTitleLayout.setVisibility(0);
        }
    }

    public void setContextView(ActionBarContextView view) {
        this.mContextView = view;
    }

    public void setCollapsible(boolean collapsible) {
        this.mIsCollapsible = collapsible;
    }

    public boolean shouldDelayChildPressedState() {
        return false;
    }

    public boolean isTitleTruncated() {
        if (this.mTitleView == null) {
            return false;
        }
        Layout titleLayout = this.mTitleView.getLayout();
        if (titleLayout == null) {
            return false;
        }
        int lineCount = titleLayout.getLineCount();
        for (int i = 0; i < lineCount; i++) {
            if (titleLayout.getEllipsisCount(i) > 0) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxHeight;
        int homeWidthSpec;
        int i;
        int itemPaddingSize;
        int itemPaddingSize2;
        int childCount = getChildCount();
        if (this.mIsCollapsible) {
            int visibleChildren = 0;
            for (int i2 = 0; i2 < childCount; i2++) {
                View child = getChildAt(i2);
                if (!(child.getVisibility() == 8 || ((child == this.mMenuView && this.mMenuView.getChildCount() == 0) || child == this.mUpGoerFive))) {
                    visibleChildren++;
                }
            }
            int upChildCount = this.mUpGoerFive.getChildCount();
            for (int i3 = 0; i3 < upChildCount; i3++) {
                if (this.mUpGoerFive.getChildAt(i3).getVisibility() != 8) {
                    visibleChildren++;
                }
            }
            if (visibleChildren == 0) {
                setMeasuredDimension(0, 0);
                return;
            }
        }
        if (MeasureSpec.getMode(widthMeasureSpec) != 1073741824) {
            throw new IllegalStateException(getClass().getSimpleName() + " can only be used with android:layout_width=\"match_parent\" (or fill_parent)");
        } else if (MeasureSpec.getMode(heightMeasureSpec) != Integer.MIN_VALUE) {
            throw new IllegalStateException(getClass().getSimpleName() + " can only be used with android:layout_height=\"wrap_content\"");
        } else {
            int contentWidth = MeasureSpec.getSize(widthMeasureSpec);
            if (this.mContentHeight >= 0) {
                maxHeight = this.mContentHeight;
            } else {
                maxHeight = MeasureSpec.getSize(heightMeasureSpec);
            }
            int verticalPadding = getPaddingTop() + getPaddingBottom();
            int paddingLeft = getPaddingLeft();
            int paddingRight = getPaddingRight();
            int height = maxHeight - verticalPadding;
            int childSpecHeight = MeasureSpec.makeMeasureSpec(height, Integer.MIN_VALUE);
            int exactHeightSpec = MeasureSpec.makeMeasureSpec(height, 1073741824);
            int availableWidth = (contentWidth - paddingLeft) - paddingRight;
            int leftOfCenter = availableWidth / 2;
            int rightOfCenter = leftOfCenter;
            boolean showTitle = (this.mTitleLayout == null || this.mTitleLayout.getVisibility() == 8 || (this.mDisplayOptions & 8) == 0) ? false : true;
            HomeView homeLayout = this.mExpandedActionView != null ? this.mExpandedHomeLayout : this.mHomeLayout;
            ViewGroup.LayoutParams homeLp = homeLayout.getLayoutParams();
            if (homeLp.width < 0) {
                homeWidthSpec = MeasureSpec.makeMeasureSpec(availableWidth, Integer.MIN_VALUE);
            } else {
                homeWidthSpec = MeasureSpec.makeMeasureSpec(homeLp.width, 1073741824);
            }
            homeLayout.measure(homeWidthSpec, exactHeightSpec);
            int homeWidth = 0;
            if ((homeLayout.getVisibility() != 8 && homeLayout.getParent() == this.mUpGoerFive) || showTitle) {
                homeWidth = homeLayout.getMeasuredWidth();
                int homeOffsetWidth = homeWidth + homeLayout.getStartOffset();
                availableWidth = Math.max(0, availableWidth - homeOffsetWidth);
                leftOfCenter = Math.max(0, availableWidth - homeOffsetWidth);
            }
            if (this.mMenuView != null && this.mMenuView.getParent() == this) {
                availableWidth = measureChildView(this.mMenuView, availableWidth, exactHeightSpec, 0);
                rightOfCenter = Math.max(0, rightOfCenter - this.mMenuView.getMeasuredWidth());
            }
            if (!(this.mIndeterminateProgressView == null || this.mIndeterminateProgressView.getVisibility() == 8)) {
                availableWidth = measureChildView(this.mIndeterminateProgressView, availableWidth, childSpecHeight, 0);
                rightOfCenter = Math.max(0, rightOfCenter - this.mIndeterminateProgressView.getMeasuredWidth());
            }
            if (this.mExpandedActionView == null) {
                switch (this.mNavigationMode) {
                    case 1:
                        if (this.mListNavLayout != null) {
                            if (showTitle) {
                                itemPaddingSize2 = this.mItemPadding * 2;
                            } else {
                                itemPaddingSize2 = this.mItemPadding;
                            }
                            int availableWidth2 = Math.max(0, availableWidth - itemPaddingSize2);
                            int leftOfCenter2 = Math.max(0, leftOfCenter - itemPaddingSize2);
                            this.mListNavLayout.measure(MeasureSpec.makeMeasureSpec(availableWidth2, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(height, 1073741824));
                            int listNavWidth = this.mListNavLayout.getMeasuredWidth();
                            availableWidth = Math.max(0, availableWidth2 - listNavWidth);
                            leftOfCenter = Math.max(0, leftOfCenter2 - listNavWidth);
                            break;
                        }
                        break;
                    case 2:
                        if (this.mTabScrollView != null) {
                            if (showTitle) {
                                itemPaddingSize = this.mItemPadding * 2;
                            } else {
                                itemPaddingSize = this.mItemPadding;
                            }
                            int availableWidth3 = Math.max(0, availableWidth - itemPaddingSize);
                            int leftOfCenter3 = Math.max(0, leftOfCenter - itemPaddingSize);
                            this.mTabScrollView.measure(MeasureSpec.makeMeasureSpec(availableWidth3, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(height, 1073741824));
                            int tabWidth = this.mTabScrollView.getMeasuredWidth();
                            availableWidth = Math.max(0, availableWidth3 - tabWidth);
                            leftOfCenter = Math.max(0, leftOfCenter3 - tabWidth);
                            break;
                        }
                        break;
                }
            }
            View customView = null;
            if (this.mExpandedActionView != null) {
                customView = this.mExpandedActionView;
            } else if (!((this.mDisplayOptions & 16) == 0 || this.mCustomNavView == null)) {
                customView = this.mCustomNavView;
            }
            if (customView != null) {
                ViewGroup.LayoutParams lp = generateLayoutParams(customView.getLayoutParams());
                ActionBar.LayoutParams ablp = lp instanceof ActionBar.LayoutParams ? (ActionBar.LayoutParams) lp : null;
                int horizontalMargin = 0;
                int verticalMargin = 0;
                if (ablp != null) {
                    horizontalMargin = ablp.leftMargin + ablp.rightMargin;
                    verticalMargin = ablp.topMargin + ablp.bottomMargin;
                }
                int customNavHeightMode = this.mContentHeight <= 0 ? Integer.MIN_VALUE : lp.height != -2 ? 1073741824 : Integer.MIN_VALUE;
                if (lp.height >= 0) {
                    height = Math.min(lp.height, height);
                }
                int customNavHeight = Math.max(0, height - verticalMargin);
                int customNavWidthMode = lp.width != -2 ? 1073741824 : Integer.MIN_VALUE;
                if (lp.width >= 0) {
                    i = Math.min(lp.width, availableWidth);
                } else {
                    i = availableWidth;
                }
                int customNavWidth = Math.max(0, i - horizontalMargin);
                if (((ablp != null ? ablp.gravity : DEFAULT_CUSTOM_GRAVITY) & 7) == 1 && lp.width == -1) {
                    customNavWidth = Math.min(leftOfCenter, rightOfCenter) * 2;
                }
                customView.measure(MeasureSpec.makeMeasureSpec(customNavWidth, customNavWidthMode), MeasureSpec.makeMeasureSpec(customNavHeight, customNavHeightMode));
                availableWidth -= customView.getMeasuredWidth() + horizontalMargin;
            }
            int availableWidth4 = measureChildView(this.mUpGoerFive, availableWidth + homeWidth, MeasureSpec.makeMeasureSpec(this.mContentHeight, 1073741824), 0);
            if (this.mTitleLayout != null) {
                int leftOfCenter4 = Math.max(0, leftOfCenter - this.mTitleLayout.getMeasuredWidth());
            }
            if (this.mContentHeight <= 0) {
                int measuredHeight = 0;
                for (int i4 = 0; i4 < childCount; i4++) {
                    int paddedViewHeight = getChildAt(i4).getMeasuredHeight() + verticalPadding;
                    if (paddedViewHeight > measuredHeight) {
                        measuredHeight = paddedViewHeight;
                    }
                }
                setMeasuredDimension(contentWidth, measuredHeight);
            } else {
                setMeasuredDimension(contentWidth, maxHeight);
            }
            if (this.mContextView != null) {
                this.mContextView.setContentHeight(getMeasuredHeight());
            }
            if (this.mProgressView != null && this.mProgressView.getVisibility() != 8) {
                this.mProgressView.measure(MeasureSpec.makeMeasureSpec(contentWidth - (this.mProgressBarPadding * 2), 1073741824), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), Integer.MIN_VALUE));
            }
        }
    }

    /* access modifiers changed from: protected */
    @SuppressLint({"NewApi"})
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        int menuStart;
        int contentHeight = ((b - t) - getPaddingTop()) - getPaddingBottom();
        if (contentHeight > 0) {
            boolean isLayoutRtl = ViewUtils.isLayoutRtl(this);
            int direction = isLayoutRtl ? 1 : -1;
            if (isLayoutRtl) {
                menuStart = getPaddingLeft();
            } else {
                menuStart = (r - l) - getPaddingRight();
            }
            int x = isLayoutRtl ? (r - l) - getPaddingRight() : getPaddingLeft();
            int y = getPaddingTop();
            HomeView homeLayout = this.mExpandedActionView != null ? this.mExpandedHomeLayout : this.mHomeLayout;
            boolean showTitle = (this.mTitleLayout == null || this.mTitleLayout.getVisibility() == 8 || (this.mDisplayOptions & 8) == 0) ? false : true;
            int startOffset = 0;
            if (homeLayout.getParent() == this.mUpGoerFive) {
                if (homeLayout.getVisibility() != 8) {
                    startOffset = 0;
                } else if (showTitle) {
                    startOffset = homeLayout.getUpWidth();
                }
            }
            int x2 = next(x + positionChild(this.mUpGoerFive, next(x, startOffset, isLayoutRtl), y, contentHeight, isLayoutRtl), startOffset, isLayoutRtl);
            if (this.mExpandedActionView == null) {
                switch (this.mNavigationMode) {
                    case 1:
                        if (this.mListNavLayout != null) {
                            if (showTitle) {
                                x2 = next(x2, this.mItemPadding, isLayoutRtl);
                            }
                            x2 = next(x2 + positionChild(this.mListNavLayout, x2, y, contentHeight, isLayoutRtl), this.mItemPadding, isLayoutRtl);
                            break;
                        }
                        break;
                    case 2:
                        if (this.mTabScrollView != null) {
                            if (showTitle) {
                                x2 = next(x2, this.mItemPadding, isLayoutRtl);
                            }
                            x2 = next(x2 + positionChild(this.mTabScrollView, x2, y, contentHeight, isLayoutRtl), this.mItemPadding, isLayoutRtl);
                            break;
                        }
                        break;
                }
            }
            if (this.mMenuView != null && this.mMenuView.getParent() == this) {
                positionChild(this.mMenuView, menuStart, y, contentHeight, !isLayoutRtl);
                positionMenuChild(this.mMenuView, menuStart, y, contentHeight, !isLayoutRtl);
                menuStart += this.mMenuView.getMeasuredWidth() * direction;
            }
            if (!(this.mIndeterminateProgressView == null || this.mIndeterminateProgressView.getVisibility() == 8)) {
                positionChild(this.mIndeterminateProgressView, menuStart, y, contentHeight, !isLayoutRtl);
                menuStart += this.mIndeterminateProgressView.getMeasuredWidth() * direction;
            }
            View customView = null;
            if (this.mExpandedActionView != null) {
                customView = this.mExpandedActionView;
            } else if (!((this.mDisplayOptions & 16) == 0 || this.mCustomNavView == null)) {
                customView = this.mCustomNavView;
            }
            if (customView != null) {
                int layoutDirection = getLayoutDirection();
                ViewGroup.LayoutParams lp = customView.getLayoutParams();
                ActionBar.LayoutParams ablp = lp instanceof ActionBar.LayoutParams ? (ActionBar.LayoutParams) lp : null;
                int gravity = ablp != null ? ablp.gravity : DEFAULT_CUSTOM_GRAVITY;
                int navWidth = customView.getMeasuredWidth();
                int topMargin = 0;
                int bottomMargin = 0;
                if (ablp != null) {
                    x2 = next(x2, ablp.getMarginStart(), isLayoutRtl);
                    menuStart += ablp.getMarginEnd() * direction;
                    topMargin = ablp.topMargin;
                    bottomMargin = ablp.bottomMargin;
                }
                int hgravity = gravity & GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK;
                if (hgravity == 1) {
                    int centeredLeft = (getWidth() - navWidth) / 2;
                    if (isLayoutRtl) {
                        int centeredEnd = centeredLeft;
                        if (centeredLeft + navWidth > x2) {
                            hgravity = 5;
                        } else if (centeredEnd < menuStart) {
                            hgravity = 3;
                        }
                    } else {
                        int centeredEnd2 = centeredLeft + navWidth;
                        if (centeredLeft < x2) {
                            hgravity = 3;
                        } else if (centeredEnd2 > menuStart) {
                            hgravity = 5;
                        }
                    }
                } else if (gravity == 0) {
                    hgravity = GravityCompat.START;
                }
                int xpos = 0;
                switch (Gravity.getAbsoluteGravity(hgravity, layoutDirection)) {
                    case 1:
                        xpos = (getWidth() - navWidth) / 2;
                        break;
                    case 3:
                        if (!isLayoutRtl) {
                            xpos = x2;
                            break;
                        } else {
                            xpos = menuStart;
                            break;
                        }
                    case 5:
                        if (!isLayoutRtl) {
                            xpos = menuStart - navWidth;
                            break;
                        } else {
                            xpos = x2 - navWidth;
                            break;
                        }
                }
                int vgravity = gravity & 112;
                if (gravity == 0) {
                    vgravity = 16;
                }
                int ypos = 0;
                switch (vgravity) {
                    case 16:
                        ypos = (((getHeight() - getPaddingBottom()) - getPaddingTop()) - customView.getMeasuredHeight()) / 2;
                        break;
                    case 48:
                        ypos = getPaddingTop() + topMargin;
                        break;
                    case 80:
                        ypos = ((getHeight() - getPaddingBottom()) - customView.getMeasuredHeight()) - bottomMargin;
                        break;
                }
                int customWidth = customView.getMeasuredWidth();
                customView.layout(xpos, ypos, xpos + customWidth, customView.getMeasuredHeight() + ypos);
                int x3 = next(x2, customWidth, isLayoutRtl);
            }
            if (this.mProgressView != null) {
                this.mProgressView.bringToFront();
                int halfProgressHeight = this.mProgressView.getMeasuredHeight() / 2;
                this.mProgressView.layout(this.mProgressBarPadding, -halfProgressHeight, this.mProgressBarPadding + this.mProgressView.getMeasuredWidth(), halfProgressHeight);
            }
        }
    }

    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new ActionBar.LayoutParams(getContext(), attrs);
    }

    public ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        if (lp == null) {
            return generateDefaultLayoutParams();
        }
        return lp;
    }

    public Parcelable onSaveInstanceState() {
        SavedState state = new SavedState(super.onSaveInstanceState());
        if (!(this.mExpandedMenuPresenter == null || this.mExpandedMenuPresenter.mCurrentExpandedItem == null)) {
            state.expandedMenuItemId = this.mExpandedMenuPresenter.mCurrentExpandedItem.getItemId();
        }
        state.isOverflowOpen = isOverflowMenuShowing();
        return state;
    }

    @SuppressLint({"NewApi"})
    public void onRestoreInstanceState(Parcelable p) {
        SavedState state = (SavedState) p;
        super.onRestoreInstanceState(state.getSuperState());
        if (!(state.expandedMenuItemId == 0 || this.mExpandedMenuPresenter == null || this.mOptionsMenu == null)) {
            MenuItem item = this.mOptionsMenu.findItem(state.expandedMenuItemId);
            if (item != null) {
                item.expandActionView();
            }
        }
        if (state.isOverflowOpen) {
            postShowOverflowMenu();
        }
    }

    public void setNavigationIcon(Drawable indicator) {
        this.mHomeLayout.setUpIndicator(indicator);
    }

    public void setDefaultNavigationIcon(Drawable icon) {
        this.mHomeLayout.setDefaultUpIndicator(icon);
    }

    public void setNavigationIcon(int resId) {
        this.mHomeLayout.setUpIndicator(resId);
    }

    public void setNavigationContentDescription(CharSequence description) {
        this.mHomeDescription = description;
        updateHomeAccessibility(this.mUpGoerFive.isEnabled());
    }

    public void setNavigationContentDescription(int resId) {
        this.mHomeDescriptionRes = resId;
        this.mHomeDescription = resId != 0 ? getResources().getText(resId) : null;
        updateHomeAccessibility(this.mUpGoerFive.isEnabled());
    }

    public void setDefaultNavigationContentDescription(int defaultNavigationContentDescription) {
        if (this.mDefaultUpDescription != defaultNavigationContentDescription) {
            this.mDefaultUpDescription = defaultNavigationContentDescription;
            updateHomeAccessibility(this.mUpGoerFive.isEnabled());
        }
    }

    public void setMenuCallbacks(MenuPresenter.Callback presenterCallback, MenuBuilder.Callback menuBuilderCallback) {
        if (this.mActionMenuPresenter != null) {
            this.mActionMenuPresenter.setCallback(presenterCallback);
        }
        if (this.mOptionsMenu != null) {
            this.mOptionsMenu.setCallback(menuBuilderCallback);
        }
    }

    public Menu getMenu() {
        return this.mOptionsMenu;
    }

    public int getPopupTheme() {
        return 0;
    }

    public void setActionBarShowOrHide(boolean isShow) {
        this.mActionBarTopShow = isShow;
    }
}
