package cn.nubia.commonui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.provider.Settings.System;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.KeyEvent.DispatcherState;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.view.animation.PathInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import cn.nubia.commonui.R;
import cn.nubia.commonui.ReflectUtils;
import cn.nubia.commonui.util.UiUtils;

public class NubiaMorePopup {
    private static final long ENTER_TOTAL_DURATION_TIME = 200;
    private static final long EXIT_DELAY_TIME = 50;
    private static final long EXIT_PART_DURATION_TIME = 100;
    private static final long EXIT_TOTAL_DURATION_TIME = 150;
    private static final float NUBIA_MORE_POPUP_HEIGHT_RATE = 0.8f;
    private String MUTI_WINDOW_STATUS;
    private MyAdapter mAdapter;
    /* access modifiers changed from: private */
    public MorePopupRelativeLayout mBackgroundView;
    private View mBottomDivider;
    private ImageView mBottomImageView;
    private LinearLayout mBottomView;
    private ImageView mCancelImageView;
    private MorePopupRelativeLayout mContainer;
    private Context mContext;
    private View mDropDownAnchorView;
    private ListView mDropDownList;
    private AnimatorSet mEntryAnimatorSet;
    private AnimatorSet mExitAnimatorSet;
    final Handler mHandler;
    private boolean mIsOnTop;
    /* access modifiers changed from: private */
    public boolean[] mItemEnabled;
    private CharSequence[] mItems;
    private long mLastClickTime;
    private View mMockStatusBarView;
    private ImageView mMoreImageView;
    private android.view.View.OnClickListener mMorePopupOnClickListener;
    /* access modifiers changed from: private */
    public OnClickListener mOnClickListener;
    private int[] mParams;
    /* access modifiers changed from: private */
    public PopupWindow mPopup;
    private LinearLayout mPopupPanel;
    private Runnable mRunnable;
    private View mTopDivider;
    private ImageView mTopImageView;
    private LinearLayout mTopView;
    private PathInterpolator mTranslateInInterpolator;
    private PathInterpolator mTranslateOutInterpolator;
    private Object mWindowManager;

    private class MyAdapter extends BaseAdapter {
        private CharSequence[] mDataList;
        private LayoutInflater mInflater;

        public MyAdapter(Context context, CharSequence[] data) {
            this.mInflater = LayoutInflater.from(context);
            this.mDataList = data;
        }

        public int getCount() {
            if (this.mDataList == null) {
                return 0;
            }
            return this.mDataList.length;
        }

        public Object getItem(int postion) {
            if (this.mDataList == null) {
                return null;
            }
            return this.mDataList[postion];
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public View getView(int postion, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = this.mInflater.inflate(R.layout.nubia_more_popup_list_item, null);
            }
            initItem((CharSequence) getItem(postion), convertView, postion);
            return convertView;
        }

        private void initItem(CharSequence value, View view, int postion) {
            TextView text = (TextView) view.findViewById(R.id.nubia_more_popup_text);
            text.setText(value);
            if (!NubiaMorePopup.this.mItemEnabled[postion]) {
                view.setEnabled(false);
                text.setEnabled(false);
                return;
            }
            view.setEnabled(true);
            text.setEnabled(true);
        }
    }

    private class NubiaMorePopupViewContainer extends FrameLayout {
        public NubiaMorePopupViewContainer(Context context) {
            super(context);
        }

        public boolean dispatchKeyEvent(KeyEvent event) {
            int keyCode = event.getKeyCode();
            int action = event.getAction();
            if (event.getKeyCode() == 4) {
                if (getKeyDispatcherState() == null) {
                    return super.dispatchKeyEvent(event);
                }
                if (event.getAction() == 0 && event.getRepeatCount() == 0) {
                    DispatcherState state = getKeyDispatcherState();
                    if (state == null) {
                        return true;
                    }
                    state.startTracking(event, this);
                    return true;
                }
                if (event.getAction() == 1) {
                    DispatcherState state2 = getKeyDispatcherState();
                    if (state2 != null && state2.isTracking(event) && !event.isCanceled()) {
                        NubiaMorePopup.this.startExitAnimation(NubiaMorePopup.this.mPopup);
                        return true;
                    }
                }
                return super.dispatchKeyEvent(event);
            } else if (event.getKeyCode() != 82) {
                return super.dispatchKeyEvent(event);
            } else {
                if (event.getAction() == 1) {
                    return onKeyUp(event.getKeyCode(), event);
                }
                return super.dispatchKeyEvent(event);
            }
        }

        public boolean onKeyUp(int keyCode, KeyEvent event) {
            if (NubiaMorePopup.this.isShowing()) {
                NubiaMorePopup.this.startExitAnimation(NubiaMorePopup.this.mPopup);
            }
            return true;
        }
    }

    public interface OnClickListener {
        void onClick(int i);
    }

    public NubiaMorePopup(Context context) {
        this(context, null, 0, 0);
    }

    public NubiaMorePopup(Context context, AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    public NubiaMorePopup(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public NubiaMorePopup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this.mIsOnTop = false;
        this.mParams = new int[2];
        this.mEntryAnimatorSet = new AnimatorSet();
        this.mExitAnimatorSet = new AnimatorSet();
        this.MUTI_WINDOW_STATUS = "ss_multi_window_enabled";
        this.mHandler = new Handler();
        this.mRunnable = new Runnable() {
            public void run() {
                NubiaMorePopup.this.startEntryAnimation();
            }
        };
        this.mMorePopupOnClickListener = new android.view.View.OnClickListener() {
            public void onClick(View v) {
                NubiaMorePopup.this.startExitAnimation(NubiaMorePopup.this.mPopup);
            }
        };
        this.mContext = context;
        this.mLastClickTime = 0;
        this.mPopup = new PopupWindow(context, attrs, defStyleAttr, defStyleRes);
        this.mPopup.setBackgroundDrawable(null);
        this.mPopup.setFocusable(true);
        this.mPopup.setAnimationStyle(0);
        this.mPopup.setWidth(-1);
        this.mPopup.setHeight(-1);
        this.mTranslateInInterpolator = new PathInterpolator(0.37f, 0.28f, 0.1f, 1.0f);
        this.mTranslateOutInterpolator = new PathInterpolator(0.37f, 0.21f, 0.58f, 1.0f);
        this.mTopImageView = buildImageView(context);
        this.mBottomImageView = buildImageView(context);
        if (!isMultiScreenOpen()) {
            ReflectUtils.invoke(this.mPopup, "setLayoutInScreenEnabled", false, false, new Object[]{Boolean.valueOf(true)}, Boolean.TYPE);
        } else {
            ReflectUtils.invoke(this.mPopup, "setLayoutInScreenEnabled", false, false, new Object[]{Boolean.valueOf(false)}, Boolean.TYPE);
        }
        this.mPopup.setWindowLayoutMode(-1, -1);
        this.mWindowManager = ReflectUtils.invoke("android.view.WindowManagerGlobal", "getWindowManagerService", true, true);
    }

    private boolean isMultiScreenOpen() {
        return System.getInt(this.mContext.getContentResolver(), this.MUTI_WINDOW_STATUS, 0) != 0;
    }

    public void setAnchorView(View anchor) {
        this.mDropDownAnchorView = anchor;
    }

    public void setItems(CharSequence[] items, OnClickListener listener) {
        this.mItems = items;
        initItemsState(items);
        this.mOnClickListener = listener;
    }

    public void setItems(int itemsId, OnClickListener listener) {
        setItems(this.mContext.getResources().getTextArray(itemsId), listener);
    }

    public void setItemEnabled(int position, boolean isEnabled) {
        this.mItemEnabled[position] = isEnabled;
    }

    private boolean isPortrait() {
        if (isMultiScreenOpen() || 1 == this.mContext.getResources().getConfiguration().orientation) {
            return true;
        }
        return false;
    }

    public void show() {
        if (!isShowing() && this.mDropDownAnchorView != null) {
            createContentView();
            this.mPopup.showAtLocation(this.mDropDownAnchorView, this.mIsOnTop ? 80 : 48, -1, -1);
            this.mPopup.getContentView().getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    NubiaMorePopup.this.mPopup.getContentView().getViewTreeObserver().removeOnPreDrawListener(this);
                    NubiaMorePopup.this.startEntryAnimation();
                    return true;
                }
            });
        }
    }

    public void show(boolean isToTop) {
        if (!isShowing() && this.mDropDownAnchorView != null) {
            createContentView(isToTop);
            this.mPopup.showAtLocation(this.mDropDownAnchorView, isToTop ? 80 : 48, -1, -1);
            this.mPopup.getContentView().getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    NubiaMorePopup.this.mPopup.getContentView().getViewTreeObserver().removeOnPreDrawListener(this);
                    NubiaMorePopup.this.startEntryAnimation();
                    return true;
                }
            });
        }
    }

    public void dismiss() {
        this.mPopup.dismiss();
        this.mPopup.setContentView(null);
        this.mDropDownList = null;
    }

    private void createContentView() {
        this.mIsOnTop = getPopupWindowInfo(this.mDropDownAnchorView, 0);
        if (this.mDropDownList == null) {
            initView();
            createContainer();
            createPopupWindow(this.mIsOnTop);
        } else {
            updatePopupWindow(this.mIsOnTop);
        }
        this.mContainer.setGravity(this.mIsOnTop ? 80 : 48);
        this.mContainer.setPopupWindow(this.mPopup);
        this.mContainer.setNubiaMorePopup(this);
        this.mBackgroundView.setPopupWindow(this.mPopup);
        this.mBackgroundView.setNubiaMorePopup(this);
    }

    private void createContentView(boolean isToTop) {
        this.mIsOnTop = getPopupWindowInfo(this.mDropDownAnchorView, 0);
        this.mIsOnTop = isToTop;
        if (this.mDropDownList == null) {
            initView();
            createContainer();
            createPopupWindow(this.mIsOnTop);
        } else {
            updatePopupWindow(this.mIsOnTop);
        }
        this.mContainer.setGravity(this.mIsOnTop ? 80 : 48);
        this.mContainer.setPopupWindow(this.mPopup);
        this.mContainer.setNubiaMorePopup(this);
        this.mBackgroundView.setPopupWindow(this.mPopup);
        this.mBackgroundView.setNubiaMorePopup(this);
    }

    private void createListView() {
        this.mAdapter = new MyAdapter(this.mContext, this.mItems);
        this.mDropDownList.setAdapter(this.mAdapter);
        if (this.mOnClickListener != null) {
            this.mDropDownList.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    if (NubiaMorePopup.this.mItemEnabled[position]) {
                        NubiaMorePopup.this.mOnClickListener.onClick(position);
                        NubiaMorePopup.this.mPopup.dismiss();
                    }
                }
            });
        }
    }

    private void createContainer() {
        createTopView(this.mContext);
        createBottomView(this.mContext);
        createListView();
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(this.mContext);
        this.mContainer = (MorePopupRelativeLayout) inflater.inflate(R.layout.nubia_more_popup, null);
        this.mBackgroundView = (MorePopupRelativeLayout) inflater.inflate(R.layout.nubia_more_popup_fade_popup, null);
        this.mPopupPanel = (LinearLayout) this.mContainer.findViewById(R.id.nubia_more_popup_panel);
        this.mTopDivider = this.mContainer.findViewById(R.id.nubia_more_popup_top_divider);
        this.mBottomDivider = this.mContainer.findViewById(R.id.nubia_more_popup_bottom_divider);
        this.mDropDownList = (ListView) this.mContainer.findViewById(R.id.nubia_more_popup_list);
        this.mTopView = (LinearLayout) this.mContainer.findViewById(R.id.nubia_more_popup_top_View);
        this.mBottomView = (LinearLayout) this.mContainer.findViewById(R.id.nubia_more_popup_bottom_View);
        this.mMockStatusBarView = this.mContainer.findViewById(R.id.nubia_more_popup_mock_status_bar);
    }

    private void createPopupWindow(boolean onTop) {
        LayoutParams listParams;
        updatePopupWindow(onTop);
        NubiaMorePopupViewContainer container = new NubiaMorePopupViewContainer(this.mContext);
        LayoutParams backPara = new LayoutParams(-1, -1);
        if (isPortrait()) {
            listParams = new LayoutParams(-1, -1);
        } else {
            listParams = new LayoutParams(this.mContext.getResources().getDimensionPixelSize(R.dimen.nubia_more_popup_listview_width_land), (int) (((float) getScreenHeight()) * NUBIA_MORE_POPUP_HEIGHT_RATE));
            listParams.gravity = 5;
            listParams.rightMargin = this.mContext.getResources().getDimensionPixelSize(R.dimen.nubia_more_popup_right_padding_land);
        }
        container.addView(this.mBackgroundView, backPara);
        container.addView(this.mContainer, listParams);
        this.mPopup.setContentView(container);
    }

    private int getScreenHeight() {
        if (this.mDropDownAnchorView != null) {
            return this.mDropDownAnchorView.getRootView().getHeight();
        }
        DisplayMetrics metric = new DisplayMetrics();
        ((WindowManager) this.mContext.getSystemService("window")).getDefaultDisplay().getMetrics(metric);
        return metric.heightPixels;
    }

    private int getScreenWidth() {
        if (this.mDropDownAnchorView != null) {
            return this.mDropDownAnchorView.getRootView().getWidth();
        }
        DisplayMetrics metric = new DisplayMetrics();
        ((WindowManager) this.mContext.getSystemService("window")).getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }

    private void updatePopupWindow(boolean onTop) {
        if (onTop) {
            this.mTopView.setVisibility(8);
            this.mTopDivider.setVisibility(8);
            this.mBottomDivider.setVisibility(0);
            this.mBottomView.setVisibility(0);
            this.mMoreImageView = this.mBottomImageView;
            if (getScreenHeight() > getInitHeight()) {
                this.mMockStatusBarView.setVisibility(8);
            }
        } else {
            this.mTopView.setVisibility(0);
            this.mTopDivider.setVisibility(0);
            this.mBottomDivider.setVisibility(8);
            this.mBottomView.setVisibility(8);
            this.mMoreImageView = this.mTopImageView;
            int flag = 0;
            if (this.mContext instanceof Activity) {
                flag = ((Activity) this.mContext).getWindow().getAttributes().flags;
            }
            if (UiUtils.isFullScreenWindow(flag) || !UiUtils.isImmersedStatusBar(flag)) {
                this.mMockStatusBarView.setVisibility(8);
            } else {
                this.mMockStatusBarView.setVisibility(0);
                setViewBackgroundColor(this.mMockStatusBarView);
            }
        }
        this.mDropDownList.setSelection(-1);
    }

    private void setViewBackgroundColor(View view) {
        if (!isPortrait()) {
            view.setBackgroundColor(this.mContext.getResources().getColor(R.color.nubia_more_popup_mask_background));
        }
    }

    private void createTopView(Context context) {
        LinearLayout.LayoutParams textPara;
        int realMarginLeft;
        Resources res = context.getResources();
        LinearLayout textView = new LinearLayout(context);
        textView.setGravity(17);
        if (!isPortrait()) {
            textPara = new LinearLayout.LayoutParams(res.getDimensionPixelSize(R.dimen.nubia_action_bar_split_height), res.getDimensionPixelSize(R.dimen.nubia_action_bar_split_height));
            this.mTopView.setGravity(21);
        } else {
            textPara = new LinearLayout.LayoutParams(res.getDimensionPixelSize(R.dimen.nubia_more_popup_top_width), res.getDimensionPixelSize(R.dimen.nubia_action_bar_default_height));
            if (this.mParams[0] > getScreenWidth()) {
                realMarginLeft = (this.mParams[0] + getScreenWidth()) - getAbsScreenHeight();
            } else {
                realMarginLeft = this.mParams[0];
            }
            if (this.mDropDownAnchorView.getLayoutDirection() == 1) {
                textPara.setMarginStart((getScreenWidth() - realMarginLeft) - res.getDimensionPixelSize(R.dimen.nubia_more_popup_top_width));
            } else {
                textPara.leftMargin = realMarginLeft;
            }
        }
        this.mTopView.addView(textView, textPara);
        textView.setOnClickListener(this.mMorePopupOnClickListener);
        LinearLayout.LayoutParams topPara = new LinearLayout.LayoutParams(res.getDimensionPixelSize(R.dimen.nubia_action_bar_menu_size), res.getDimensionPixelSize(R.dimen.nubia_action_bar_menu_size));
        ViewGroup parent = (ViewGroup) this.mTopImageView.getParent();
        if (parent != null) {
            parent.removeAllViews();
        }
        textView.addView(this.mTopImageView, topPara);
    }

    private void createBottomView(Context context) {
        int realMarginLeft;
        Resources res = context.getResources();
        LinearLayout textView = new LinearLayout(context);
        textView.setOrientation(0);
        LinearLayout.LayoutParams textPara = new LinearLayout.LayoutParams(res.getDimensionPixelSize(R.dimen.nubia_action_bar_split_height), res.getDimensionPixelSize(R.dimen.nubia_action_bar_split_height));
        if (this.mParams[0] > getScreenWidth()) {
            realMarginLeft = (this.mParams[0] + getScreenWidth()) - getAbsScreenHeight();
        } else {
            realMarginLeft = this.mParams[0];
        }
        if (this.mDropDownAnchorView.getLayoutDirection() == 1) {
            textPara.setMarginStart((getScreenWidth() - realMarginLeft) - res.getDimensionPixelSize(R.dimen.nubia_action_bar_split_height));
        } else {
            textPara.leftMargin = realMarginLeft;
        }
        this.mBottomView.addView(textView, textPara);
        textView.setGravity(17);
        textView.setClickable(true);
        textView.setOnClickListener(this.mMorePopupOnClickListener);
        LinearLayout.LayoutParams bottomPara = new LinearLayout.LayoutParams(res.getDimensionPixelSize(R.dimen.nubia_action_bar_menu_size), res.getDimensionPixelSize(R.dimen.nubia_action_bar_menu_size));
        ViewGroup parent = (ViewGroup) this.mBottomImageView.getParent();
        if (parent != null) {
            parent.removeAllViews();
        }
        textView.addView(this.mBottomImageView, bottomPara);
    }

    private int getAbsScreenHeight() {
        Point size = new Point();
        ReflectUtils.invoke(this.mWindowManager, "getInitialDisplaySize", false, false, new Object[]{Integer.valueOf(0), size}, Integer.TYPE, Point.class);
        return size.y;
    }

    private ImageView buildImageView(Context context) {
        ImageView img = new ImageView(context);
        img.setBackgroundResource(R.drawable.nubia_more_popup_operation_background);
        return img;
    }

    public void setImageView(Context context, int resid) {
        this.mCancelImageView = new ImageView(context);
        this.mCancelImageView.setBackgroundResource(resid);
        this.mTopImageView = this.mCancelImageView;
        this.mBottomImageView = this.mCancelImageView;
    }

    private boolean getPopupWindowInfo(View anchor, int yOffset) {
        Rect displayFrame = new Rect();
        anchor.getWindowVisibleDisplayFrame(displayFrame);
        int bottomEdge = displayFrame.bottom;
        int topEdge = displayFrame.top;
        int[] anchorPos = new int[2];
        anchor.getLocationOnScreen(anchorPos);
        anchor.getLocationOnScreen(this.mParams);
        int distanceToBottom = (bottomEdge - (anchorPos[1] + anchor.getHeight())) - yOffset;
        if (distanceToBottom < 0) {
            int scrollX = anchor.getScrollX();
            int scrollY = anchor.getScrollY();
            anchor.requestRectangleOnScreen(new Rect(scrollX, scrollY, displayFrame.width(), this.mPopup.getHeight() + scrollY + anchor.getHeight() + yOffset), true);
            anchor.getLocationOnScreen(anchorPos);
        }
        if ((anchorPos[1] - topEdge) + yOffset > distanceToBottom) {
            return true;
        }
        return false;
    }

    public boolean isShowing() {
        return this.mPopup.isShowing();
    }

    private void measureView(View child) {
        int childMeasureHeight;
        ViewGroup.LayoutParams lp = child.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(-1, -2);
        }
        int childMeasureWidth = ViewGroup.getChildMeasureSpec(0, 0, lp.width);
        if (lp.height > 0) {
            childMeasureHeight = MeasureSpec.makeMeasureSpec(lp.height, 1073741824);
        } else {
            childMeasureHeight = MeasureSpec.makeMeasureSpec(0, 0);
        }
        child.measure(childMeasureWidth, childMeasureHeight);
    }

    private int getInitHeight() {
        if (this.mDropDownList == null) {
            return 0;
        }
        measureView(this.mPopupPanel);
        measureView(this.mDropDownList);
        if (this.mItems != null) {
            return this.mPopupPanel.getMeasuredHeight() + (this.mDropDownList.getMeasuredHeight() * (this.mItems.length - 1));
        }
        return 0;
    }

    /* access modifiers changed from: private */
    public void startEntryAnimation() {
        int height;
        Animator totalTranslate;
        if (!this.mEntryAnimatorSet.isStarted()) {
            if (this.mPopupPanel.getHeight() == 0) {
                height = getInitHeight();
            } else {
                height = this.mPopupPanel.getHeight();
            }
            if (this.mIsOnTop) {
                totalTranslate = ObjectAnimator.ofFloat(this.mPopupPanel, "translationY", new float[]{(float) height, 0.0f});
            } else {
                totalTranslate = ObjectAnimator.ofFloat(this.mPopupPanel, "translationY", new float[]{(float) (-height), 0.0f});
            }
            totalTranslate.setDuration(ENTER_TOTAL_DURATION_TIME);
            totalTranslate.setInterpolator(this.mTranslateInInterpolator);
            ValueAnimator backgroudAlpha = ValueAnimator.ofInt(new int[]{0, 153});
            backgroudAlpha.setDuration(ENTER_TOTAL_DURATION_TIME);
            backgroudAlpha.addUpdateListener(new AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator anim) {
                    NubiaMorePopup.this.mBackgroundView.setBackgroundColor(Color.argb(((Integer) anim.getAnimatedValue()).intValue(), 0, 0, 0));
                }
            });
            this.mEntryAnimatorSet.playTogether(new Animator[]{totalTranslate, backgroudAlpha});
            this.mEntryAnimatorSet.start();
        }
    }

    public void startExitAnimation(final PopupWindow popup) {
        Animator totalTranslate;
        if (!this.mExitAnimatorSet.isStarted()) {
            if (this.mIsOnTop) {
                totalTranslate = ObjectAnimator.ofFloat(this.mPopupPanel, "translationY", new float[]{0.0f, (float) this.mPopupPanel.getHeight()});
            } else {
                totalTranslate = ObjectAnimator.ofFloat(this.mPopupPanel, "translationY", new float[]{0.0f, (float) (-this.mPopupPanel.getHeight())});
            }
            totalTranslate.setDuration(EXIT_TOTAL_DURATION_TIME);
            totalTranslate.setInterpolator(this.mTranslateOutInterpolator);
            ValueAnimator backgroudAlpha = ValueAnimator.ofInt(new int[]{153, 0});
            backgroudAlpha.setDuration(EXIT_TOTAL_DURATION_TIME);
            backgroudAlpha.addUpdateListener(new AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator anim) {
                    NubiaMorePopup.this.mBackgroundView.setBackgroundColor(Color.argb(((Integer) anim.getAnimatedValue()).intValue(), 0, 0, 0));
                }
            });
            this.mExitAnimatorSet.playTogether(new Animator[]{totalTranslate, backgroudAlpha});
            this.mExitAnimatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    popup.dismiss();
                }
            });
            this.mExitAnimatorSet.start();
        }
    }

    private void initItemsState(CharSequence[] items) {
        this.mItemEnabled = new boolean[items.length];
        for (int i = 0; i < items.length; i++) {
            this.mItemEnabled[i] = true;
        }
    }
}
