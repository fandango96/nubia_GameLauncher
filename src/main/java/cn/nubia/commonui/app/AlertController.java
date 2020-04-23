package cn.nubia.commonui.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.provider.FontsContractCompat.FontRequestCallback;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import cn.nubia.commonui.R;
import cn.nubia.commonui.ReflectUtils;
import cn.nubia.commonui.widget.DialogCenterLinearLayout;
import cn.nubia.commonui.widget.DialogLinearLayout;
import java.lang.ref.WeakReference;

public class AlertController {
    private static final String TAG = "AlertController";
    View animPanel;
    /* access modifiers changed from: private */
    public ListAdapter mAdapter;
    private int mAlertDialogLayout;
    private View mBackAnimView;
    private final OnClickListener mButtonHandler = new OnClickListener() {
        public void onClick(View v) {
            Message m;
            if (v == AlertController.this.mButtonPositive && AlertController.this.mButtonPositiveMessage != null) {
                m = Message.obtain(AlertController.this.mButtonPositiveMessage);
            } else if (v == AlertController.this.mButtonNegative && AlertController.this.mButtonNegativeMessage != null) {
                m = Message.obtain(AlertController.this.mButtonNegativeMessage);
            } else if (v != AlertController.this.mButtonNeutral || AlertController.this.mButtonNeutralMessage == null) {
                m = null;
            } else {
                m = Message.obtain(AlertController.this.mButtonNeutralMessage);
            }
            if (m != null) {
                m.sendToTarget();
            }
            AlertController.this.mHandler.obtainMessage(1, AlertController.this.mDialogInterface).sendToTarget();
        }
    };
    /* access modifiers changed from: private */
    public Button mButtonNegative;
    /* access modifiers changed from: private */
    public Message mButtonNegativeMessage;
    private CharSequence mButtonNegativeText;
    private int mButtonNegativeTextColor;
    /* access modifiers changed from: private */
    public Button mButtonNeutral;
    /* access modifiers changed from: private */
    public Message mButtonNeutralMessage;
    private CharSequence mButtonNeutralText;
    private int mButtonNeutralTextColor;
    private int mButtonPanelLayoutHint = 0;
    private int mButtonPanelSideLayout;
    /* access modifiers changed from: private */
    public Button mButtonPositive;
    /* access modifiers changed from: private */
    public Message mButtonPositiveMessage;
    private CharSequence mButtonPositiveText;
    private int mButtonPositiveTextColor;
    /* access modifiers changed from: private */
    public int mCheckedItem = -1;
    private View mContentView;
    /* access modifiers changed from: private */
    public final Context mContext;
    private View mCustomTitleView;
    /* access modifiers changed from: private */
    public final DialogInterface mDialogInterface;
    private boolean mForceInverseBackground;
    /* access modifiers changed from: private */
    public final Handler mHandler;
    private Drawable mIcon;
    private int mIconId = 0;
    private ImageView mIconView;
    private boolean mIsContentDividerVisible;
    /* access modifiers changed from: private */
    public boolean mIsMultiChoiceDialog;
    /* access modifiers changed from: private */
    public boolean mIsSingleChoiceDialog;
    /* access modifiers changed from: private */
    public int mListItemLayout;
    /* access modifiers changed from: private */
    public int mListLayout;
    /* access modifiers changed from: private */
    public ListView mListView;
    private float mMaxHeightRatio = -1.0f;
    private CharSequence mMessage;
    private TextView mMessageView;
    /* access modifiers changed from: private */
    public int mMultiChoiceItemLayout;
    private int mNubiaLeftButtonBgID;
    private int mNubiaMiddleButtonBgID;
    private int mNubiaRightButtonBgID;
    private int mParentPanelBottom;
    private int mParentPanelLeft;
    private int mParentPanelRight;
    private int mParentPanelTop;
    private ScrollView mScrollView;
    /* access modifiers changed from: private */
    public int mSingleChoiceItemLayout;
    private CharSequence mTitle;
    private TextView mTitleChoiceView;
    private TextView mTitleView;
    private float mTranslatePadding;
    private View mView;
    private int mViewLayoutResId;
    private int mViewSpacingBottom;
    private int mViewSpacingLeft;
    private int mViewSpacingRight;
    private boolean mViewSpacingSpecified = false;
    private int mViewSpacingTop;
    private final Window mWindow;

    public static class AlertParams {
        public ListAdapter mAdapter;
        public boolean mCancelable;
        public int mCheckedItem = -1;
        public boolean[] mCheckedItems;
        public final Context mContext;
        public Cursor mCursor;
        public View mCustomTitleView;
        public boolean mForceInverseBackground;
        public Drawable mIcon;
        public int mIconAttrId = 0;
        public int mIconId = 0;
        public final LayoutInflater mInflater;
        public String mIsCheckedColumn;
        public boolean mIsContentDividerVisibleParams = false;
        public boolean mIsMultiChoice;
        public boolean mIsSingleChoice;
        public CharSequence[] mItems;
        public String mLabelColumn;
        public float mMaxHeightRatio = -1.0f;
        public CharSequence mMessage;
        public DialogInterface.OnClickListener mNegativeButtonListener;
        public CharSequence mNegativeButtonText;
        public DialogInterface.OnClickListener mNeutralButtonListener;
        public CharSequence mNeutralButtonText;
        public OnCancelListener mOnCancelListener;
        public OnMultiChoiceClickListener mOnCheckboxClickListener;
        public DialogInterface.OnClickListener mOnClickListener;
        public OnDismissListener mOnDismissListener;
        public OnItemSelectedListener mOnItemSelectedListener;
        public OnKeyListener mOnKeyListener;
        public OnPrepareListViewListener mOnPrepareListViewListener;
        public DialogInterface.OnClickListener mPositiveButtonListener;
        public CharSequence mPositiveButtonText;
        public boolean mRecycleOnMeasure = true;
        public CharSequence mTitle;
        public View mView;
        public int mViewLayoutResId;
        public int mViewSpacingBottom;
        public int mViewSpacingLeft;
        public int mViewSpacingRight;
        public boolean mViewSpacingSpecified = false;
        public int mViewSpacingTop;

        public interface OnPrepareListViewListener {
            void onPrepareListView(ListView listView);
        }

        public AlertParams(Context context) {
            this.mContext = context;
            this.mCancelable = true;
            this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        }

        public void apply(AlertController dialog) {
            if (this.mCustomTitleView != null) {
                dialog.setCustomTitle(this.mCustomTitleView);
            } else {
                if (this.mTitle != null) {
                    dialog.setTitle(this.mTitle);
                }
                if (this.mIcon != null) {
                    dialog.setIcon(this.mIcon);
                }
                if (this.mIconId != 0) {
                    dialog.setIcon(this.mIconId);
                }
                if (this.mIconAttrId != 0) {
                    dialog.setIcon(dialog.getIconAttributeResId(this.mIconAttrId));
                }
            }
            dialog.setContentDivider(this.mIsContentDividerVisibleParams);
            if (this.mMaxHeightRatio >= 0.0f && this.mMaxHeightRatio <= 1.0f) {
                dialog.setMaxHeightRatio(this.mMaxHeightRatio);
            }
            if (this.mMessage != null) {
                dialog.setMessage(this.mMessage);
            }
            if (this.mPositiveButtonText != null) {
                dialog.setButton(-1, this.mPositiveButtonText, this.mPositiveButtonListener, null);
            }
            if (this.mNegativeButtonText != null) {
                dialog.setButton(-2, this.mNegativeButtonText, this.mNegativeButtonListener, null);
            }
            if (this.mNeutralButtonText != null) {
                dialog.setButton(-3, this.mNeutralButtonText, this.mNeutralButtonListener, null);
            }
            if (this.mForceInverseBackground) {
                dialog.setInverseBackgroundForced(true);
            }
            if (!(this.mItems == null && this.mCursor == null && this.mAdapter == null)) {
                createListView(dialog);
            }
            if (this.mView != null) {
                if (this.mViewSpacingSpecified) {
                    dialog.setView(this.mView, this.mViewSpacingLeft, this.mViewSpacingTop, this.mViewSpacingRight, this.mViewSpacingBottom);
                    return;
                }
                dialog.setView(this.mView);
            } else if (this.mViewLayoutResId != 0) {
                dialog.setView(this.mViewLayoutResId);
            }
        }

        /* JADX WARNING: type inference failed for: r0v0, types: [android.widget.SimpleCursorAdapter] */
        /* JADX WARNING: type inference failed for: r0v1, types: [cn.nubia.commonui.app.AlertController$CheckedItemAdapter] */
        /* JADX WARNING: type inference failed for: r0v2, types: [android.widget.ListAdapter] */
        /* JADX WARNING: type inference failed for: r0v3, types: [android.widget.ListAdapter] */
        /* JADX WARNING: type inference failed for: r0v5, types: [cn.nubia.commonui.app.AlertController$AlertParams$1] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Unknown variable types count: 4 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void createListView(final cn.nubia.commonui.app.AlertController r11) {
            /*
                r10 = this;
                r9 = 1
                r5 = 0
                android.view.LayoutInflater r1 = r10.mInflater
                int r3 = r11.mListLayout
                r4 = 0
                android.view.View r6 = r1.inflate(r3, r4)
                cn.nubia.commonui.app.AlertController$RecycleListView r6 = (cn.nubia.commonui.app.AlertController.RecycleListView) r6
                boolean r1 = r10.mIsSingleChoice
                r11.mIsSingleChoiceDialog = r1
                boolean r1 = r10.mIsMultiChoice
                r11.mIsMultiChoiceDialog = r1
                java.lang.String r1 = "AlertController"
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r4 = "createListView: mIsSingleChoice = "
                java.lang.StringBuilder r3 = r3.append(r4)
                boolean r4 = r10.mIsSingleChoice
                java.lang.StringBuilder r3 = r3.append(r4)
                java.lang.String r4 = ", mIsMultiChoice = "
                java.lang.StringBuilder r3 = r3.append(r4)
                boolean r4 = r10.mIsMultiChoice
                java.lang.StringBuilder r3 = r3.append(r4)
                java.lang.String r3 = r3.toString()
                android.util.Log.i(r1, r3)
                boolean r1 = r10.mIsMultiChoice
                if (r1 == 0) goto L_0x0099
                android.database.Cursor r1 = r10.mCursor
                if (r1 != 0) goto L_0x008c
                cn.nubia.commonui.app.AlertController$AlertParams$1 r0 = new cn.nubia.commonui.app.AlertController$AlertParams$1
                android.content.Context r2 = r10.mContext
                int r3 = r11.mMultiChoiceItemLayout
                int r4 = cn.nubia.commonui.R.id.text1
                java.lang.CharSequence[] r5 = r10.mItems
                r1 = r10
                r0.<init>(r2, r3, r4, r5, r6)
            L_0x0057:
                cn.nubia.commonui.app.AlertController$AlertParams$OnPrepareListViewListener r1 = r10.mOnPrepareListViewListener
                if (r1 == 0) goto L_0x0060
                cn.nubia.commonui.app.AlertController$AlertParams$OnPrepareListViewListener r1 = r10.mOnPrepareListViewListener
                r1.onPrepareListView(r6)
            L_0x0060:
                r11.mAdapter = r0
                int r1 = r10.mCheckedItem
                r11.mCheckedItem = r1
                android.content.DialogInterface$OnClickListener r1 = r10.mOnClickListener
                if (r1 == 0) goto L_0x00d4
                cn.nubia.commonui.app.AlertController$AlertParams$3 r1 = new cn.nubia.commonui.app.AlertController$AlertParams$3
                r1.<init>(r11)
                r6.setOnItemClickListener(r1)
            L_0x0074:
                android.widget.AdapterView$OnItemSelectedListener r1 = r10.mOnItemSelectedListener
                if (r1 == 0) goto L_0x007d
                android.widget.AdapterView$OnItemSelectedListener r1 = r10.mOnItemSelectedListener
                r6.setOnItemSelectedListener(r1)
            L_0x007d:
                boolean r1 = r10.mIsSingleChoice
                if (r1 == 0) goto L_0x00e1
                r6.setChoiceMode(r9)
            L_0x0084:
                boolean r1 = r10.mRecycleOnMeasure
                r6.mRecycleOnMeasure = r1
                r11.mListView = r6
                return
            L_0x008c:
                cn.nubia.commonui.app.AlertController$AlertParams$2 r0 = new cn.nubia.commonui.app.AlertController$AlertParams$2
                android.content.Context r3 = r10.mContext
                android.database.Cursor r4 = r10.mCursor
                r1 = r0
                r2 = r10
                r7 = r11
                r1.<init>(r3, r4, r5, r6, r7)
                goto L_0x0057
            L_0x0099:
                boolean r1 = r10.mIsSingleChoice
                if (r1 == 0) goto L_0x00ac
                int r2 = r11.mSingleChoiceItemLayout
            L_0x00a1:
                android.database.Cursor r1 = r10.mCursor
                if (r1 != 0) goto L_0x00bd
                android.widget.ListAdapter r1 = r10.mAdapter
                if (r1 == 0) goto L_0x00b1
                android.widget.ListAdapter r0 = r10.mAdapter
            L_0x00ab:
                goto L_0x0057
            L_0x00ac:
                int r2 = r11.mListItemLayout
                goto L_0x00a1
            L_0x00b1:
                cn.nubia.commonui.app.AlertController$CheckedItemAdapter r0 = new cn.nubia.commonui.app.AlertController$CheckedItemAdapter
                android.content.Context r1 = r10.mContext
                int r3 = cn.nubia.commonui.R.id.text1
                java.lang.CharSequence[] r4 = r10.mItems
                r0.<init>(r1, r2, r3, r4)
                goto L_0x00ab
            L_0x00bd:
                android.widget.SimpleCursorAdapter r0 = new android.widget.SimpleCursorAdapter
                android.content.Context r1 = r10.mContext
                android.database.Cursor r3 = r10.mCursor
                java.lang.String[] r4 = new java.lang.String[r9]
                java.lang.String r7 = r10.mLabelColumn
                r4[r5] = r7
                int[] r7 = new int[r9]
                int r8 = cn.nubia.commonui.R.id.text1
                r7[r5] = r8
                r5 = r7
                r0.<init>(r1, r2, r3, r4, r5)
                goto L_0x0057
            L_0x00d4:
                android.content.DialogInterface$OnMultiChoiceClickListener r1 = r10.mOnCheckboxClickListener
                if (r1 == 0) goto L_0x0074
                cn.nubia.commonui.app.AlertController$AlertParams$4 r1 = new cn.nubia.commonui.app.AlertController$AlertParams$4
                r1.<init>(r6, r11)
                r6.setOnItemClickListener(r1)
                goto L_0x0074
            L_0x00e1:
                boolean r1 = r10.mIsMultiChoice
                if (r1 == 0) goto L_0x0084
                r1 = 2
                r6.setChoiceMode(r1)
                goto L_0x0084
            */
            throw new UnsupportedOperationException("Method not decompiled: cn.nubia.commonui.app.AlertController.AlertParams.createListView(cn.nubia.commonui.app.AlertController):void");
        }
    }

    private static final class ButtonHandler extends Handler {
        private static final int MSG_DISMISS_DIALOG = 1;
        private WeakReference<DialogInterface> mDialog;

        public ButtonHandler(DialogInterface dialog) {
            this.mDialog = new WeakReference<>(dialog);
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FontRequestCallback.FAIL_REASON_FONT_LOAD_ERROR /*-3*/:
                case -2:
                case -1:
                    ((DialogInterface.OnClickListener) msg.obj).onClick((DialogInterface) this.mDialog.get(), msg.what);
                    return;
                case 1:
                    ((DialogInterface) msg.obj).dismiss();
                    return;
                default:
                    return;
            }
        }
    }

    private static class CheckedItemAdapter extends ArrayAdapter<CharSequence> {
        public CheckedItemAdapter(Context context, int resource, int textViewResourceId, CharSequence[] objects) {
            super(context, resource, textViewResourceId, objects);
        }

        public boolean hasStableIds() {
            return true;
        }

        public long getItemId(int position) {
            return (long) position;
        }
    }

    public static class RecycleListView extends ListView {
        boolean mRecycleOnMeasure = true;

        public RecycleListView(Context context) {
            super(context);
        }

        public RecycleListView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public RecycleListView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        public RecycleListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        /* access modifiers changed from: protected */
        public boolean recycleOnMeasure() {
            return this.mRecycleOnMeasure;
        }
    }

    /* access modifiers changed from: 0000 */
    public void setAlertDialogLayout(int redirectLayout) {
        this.mAlertDialogLayout = redirectLayout;
    }

    public AlertController(Context context, DialogInterface di, Window window) {
        this.mContext = context;
        this.mDialogInterface = di;
        this.mWindow = window;
        this.mHandler = new ButtonHandler(di);
        resetNubiaAlertDialogLayout();
        setNubiaButtonBackground(R.drawable.nubia_btn_default_material, R.drawable.nubia_btn_default_material, R.drawable.nubia_btn_default_material);
    }

    static boolean canTextInput(View v) {
        if (v.onCheckIsTextEditor()) {
            return true;
        }
        if (!(v instanceof ViewGroup)) {
            return false;
        }
        ViewGroup vg = (ViewGroup) v;
        int i = vg.getChildCount();
        while (i > 0) {
            i--;
            if (canTextInput(vg.getChildAt(i))) {
                return true;
            }
        }
        return false;
    }

    public void installContent() {
        this.mWindow.requestFeature(1);
        this.mWindow.setContentView(selectContentView());
        setupView();
        setupDecor();
    }

    private int selectContentView() {
        if (this.mButtonPanelSideLayout == 0) {
            return this.mAlertDialogLayout;
        }
        if (this.mButtonPanelLayoutHint == 1) {
            return this.mButtonPanelSideLayout;
        }
        return this.mAlertDialogLayout;
    }

    public void setTitle(CharSequence title) {
        this.mTitle = title;
        if (this.mTitleView != null) {
            this.mTitleView.setText(title);
        }
        if (this.mTitleChoiceView != null) {
            this.mTitleChoiceView.setText(title);
        }
    }

    public void setCustomTitle(View customTitleView) {
        this.mCustomTitleView = customTitleView;
    }

    public void setMessage(CharSequence message) {
        this.mMessage = message;
        if (this.mMessageView != null) {
            this.mMessageView.setText(message);
        }
    }

    public void setView(int layoutResId) {
        this.mView = null;
        this.mViewLayoutResId = layoutResId;
        this.mViewSpacingSpecified = false;
    }

    public void setView(View view) {
        this.mView = view;
        this.mViewLayoutResId = 0;
        this.mViewSpacingSpecified = false;
    }

    public void setView(View view, int viewSpacingLeft, int viewSpacingTop, int viewSpacingRight, int viewSpacingBottom) {
        this.mView = view;
        this.mViewLayoutResId = 0;
        this.mViewSpacingSpecified = true;
        this.mViewSpacingLeft = viewSpacingLeft;
        this.mViewSpacingTop = viewSpacingTop;
        this.mViewSpacingRight = viewSpacingRight;
        this.mViewSpacingBottom = viewSpacingBottom;
    }

    public void setButtonPanelLayoutHint(int layoutHint) {
        this.mButtonPanelLayoutHint = layoutHint;
    }

    public void setButton(int whichButton, CharSequence text, DialogInterface.OnClickListener listener, Message msg) {
        if (msg == null && listener != null) {
            msg = this.mHandler.obtainMessage(whichButton, listener);
        }
        switch (whichButton) {
            case FontRequestCallback.FAIL_REASON_FONT_LOAD_ERROR /*-3*/:
                this.mButtonNeutralText = text;
                this.mButtonNeutralMessage = msg;
                return;
            case -2:
                this.mButtonNegativeText = text;
                this.mButtonNegativeMessage = msg;
                return;
            case -1:
                this.mButtonPositiveText = text;
                this.mButtonPositiveMessage = msg;
                return;
            default:
                throw new IllegalArgumentException("Button does not exist");
        }
    }

    public void setButtonTextColor(int whichButton, int color) {
        switch (whichButton) {
            case FontRequestCallback.FAIL_REASON_FONT_LOAD_ERROR /*-3*/:
                this.mButtonNeutralTextColor = color;
                return;
            case -2:
                this.mButtonNegativeTextColor = color;
                return;
            case -1:
                this.mButtonPositiveTextColor = color;
                return;
            default:
                throw new IllegalArgumentException("Button does not exist");
        }
    }

    public void setMaxHeightRatio(float ratio) {
        this.mMaxHeightRatio = ratio;
    }

    public void setParentPanelMargin(int left, int top, int right, int bottom) {
        this.mParentPanelLeft = left;
        this.mParentPanelTop = top;
        this.mParentPanelRight = right;
        this.mParentPanelBottom = bottom;
    }

    public void setIcon(int resId) {
        this.mIcon = null;
        this.mIconId = resId;
        if (this.mIconView == null) {
            return;
        }
        if (resId != 0) {
            this.mIconView.setImageResource(this.mIconId);
        } else {
            this.mIconView.setVisibility(8);
        }
    }

    public void setIcon(Drawable icon) {
        this.mIcon = icon;
        this.mIconId = 0;
        if (this.mIconView == null) {
            return;
        }
        if (icon != null) {
            this.mIconView.setImageDrawable(icon);
        } else {
            this.mIconView.setVisibility(8);
        }
    }

    public int getIconAttributeResId(int attrId) {
        TypedValue out = new TypedValue();
        this.mContext.getTheme().resolveAttribute(attrId, out, true);
        return out.resourceId;
    }

    public void setInverseBackgroundForced(boolean forceInverseBackground) {
        this.mForceInverseBackground = forceInverseBackground;
    }

    public ListView getListView() {
        return this.mListView;
    }

    public Button getButton(int whichButton) {
        switch (whichButton) {
            case FontRequestCallback.FAIL_REASON_FONT_LOAD_ERROR /*-3*/:
                return this.mButtonNeutral;
            case -2:
                return this.mButtonNegative;
            case -1:
                return this.mButtonPositive;
            default:
                return null;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return this.mScrollView != null && this.mScrollView.executeKeyEvent(event);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return this.mScrollView != null && this.mScrollView.executeKeyEvent(event);
    }

    private void setupDecor() {
        View decor = this.mWindow.getDecorView();
        final View parent = this.mWindow.findViewById(R.id.nubia_parentPanel);
        if (!(parent == null || decor == null)) {
            decor.setOnApplyWindowInsetsListener(new OnApplyWindowInsetsListener() {
                public WindowInsets onApplyWindowInsets(View view, WindowInsets insets) {
                    if (insets.isRound()) {
                        int roundOffset = AlertController.this.mContext.getResources().getDimensionPixelOffset(R.dimen.nubia_alert_dialog_round_padding);
                        parent.setPadding(roundOffset, roundOffset, roundOffset, roundOffset);
                    }
                    return insets.consumeSystemWindowInsets();
                }
            });
            decor.setFitsSystemWindows(true);
            decor.requestApplyInsets();
            parent.setPadding(this.mParentPanelLeft, this.mParentPanelTop, this.mParentPanelRight, this.mParentPanelBottom);
        }
        if (this.mMaxHeightRatio >= 0.0f && this.mMaxHeightRatio <= 1.0f) {
            if (parent instanceof DialogLinearLayout) {
                ((DialogLinearLayout) parent).setMaxHeightRatio(this.mMaxHeightRatio);
            } else if (parent instanceof DialogCenterLinearLayout) {
                ((DialogCenterLinearLayout) parent).setMaxHeightRatio(this.mMaxHeightRatio);
            }
        }
    }

    private void setupView() {
        this.animPanel = this.mWindow.findViewById(R.id.nubia_animPanel);
        ViewGroup contentPanel = (ViewGroup) this.mWindow.findViewById(R.id.contentPanel);
        setupContent(contentPanel);
        View buttonPanel = this.mWindow.findViewById(R.id.buttonPanel);
        boolean hasButtons = setupButtons();
        ViewGroup topPanel = (ViewGroup) this.mWindow.findViewById(R.id.topPanel);
        boolean hasTitle = setupTitle(topPanel);
        FrameLayout customPanel = (FrameLayout) this.mWindow.findViewById(R.id.customPanel);
        boolean hasCustomView = setupCustomView();
        adjustPadding(hasButtons, hasCustomView, topPanel, contentPanel);
        setBackground(topPanel, contentPanel, customPanel, buttonPanel, hasTitle, hasCustomView, hasButtons);
        setDivider(contentPanel, customPanel);
    }

    public void setContentDivider(boolean visible) {
        this.mIsContentDividerVisible = visible;
    }

    private void setDivider(ViewGroup contentPanel, ViewGroup customPanel) {
        if (contentPanel.getHeight() == 0 || customPanel.getHeight() == 0) {
            View nubiaDivider = this.mWindow.findViewById(R.id.nubia_title_content_divider);
            if (nubiaDivider != null) {
                nubiaDivider.setVisibility(this.mIsContentDividerVisible ? 0 : 8);
            }
        }
    }

    private void adjustPadding(boolean hasButtons, boolean hasCustomView, ViewGroup topPanel, ViewGroup contentPanel) {
        int i = 0;
        if (!hasButtons) {
            View nubiaButtonPanel = this.mWindow.findViewById(R.id.nubia_button_bg);
            if (nubiaButtonPanel != null) {
                nubiaButtonPanel.setVisibility(8);
            }
        }
        if (this.mCustomTitleView == null) {
            if (!(!TextUtils.isEmpty(this.mTitle))) {
                if (isCenterAlertDialog() && !TextUtils.isEmpty(this.mMessage)) {
                    LayoutParams lp = new LayoutParams(-1, -2);
                    lp.setMargins(0, this.mContext.getResources().getDimensionPixelOffset(R.dimen.nubia_center_alert_dialog_message_vertical_margin_bottom), 0, 0);
                    contentPanel.setLayoutParams(lp);
                } else if (!(isCenterAlertDialog() || contentPanel == null || contentPanel.getVisibility() == 8)) {
                    LayoutParams lp2 = new LayoutParams(-1, -2);
                    lp2.setMargins(0, this.mContext.getResources().getDimensionPixelOffset(R.dimen.nubia_alert_dialog_context_notitle_margin_top), 0, 0);
                    contentPanel.setLayoutParams(lp2);
                }
                View nubiaDivider = this.mWindow.findViewById(R.id.nubia_title_content_divider);
                if (nubiaDivider != null) {
                    if (!this.mIsContentDividerVisible) {
                        i = 8;
                    }
                    nubiaDivider.setVisibility(i);
                }
            } else if (isCenterAlertDialog() && contentPanel != null && contentPanel.getVisibility() == 8 && !hasCustomView) {
                LayoutParams lp3 = new LayoutParams(-1, -2);
                int verMargin = this.mContext.getResources().getDimensionPixelOffset(R.dimen.nubia_center_alert_dialog_message_vertical_margin_bottom);
                lp3.setMargins(0, verMargin, 0, verMargin);
                topPanel.setLayoutParams(lp3);
            }
        }
    }

    private boolean isCenterAlertDialog() {
        return this.mAlertDialogLayout == R.layout.nubia_alert_dialog_holo_center;
    }

    private boolean setupCustomView() {
        View customView;
        boolean hasCustomView = false;
        FrameLayout customPanel = (FrameLayout) this.mWindow.findViewById(R.id.customPanel);
        if (this.mView != null) {
            customView = this.mView;
        } else if (this.mViewLayoutResId != 0) {
            customView = LayoutInflater.from(this.mContext).inflate(this.mViewLayoutResId, customPanel, false);
        } else {
            customView = null;
        }
        if (customView != null) {
            hasCustomView = true;
        }
        if (!hasCustomView || !canTextInput(customView)) {
            this.mWindow.setFlags(131072, 131072);
        }
        if (hasCustomView) {
            FrameLayout custom = (FrameLayout) this.mWindow.findViewById(R.id.custom);
            custom.addView(customView, new ViewGroup.LayoutParams(-1, -1));
            if (this.mViewSpacingSpecified) {
                custom.setPadding(this.mViewSpacingLeft, this.mViewSpacingTop, this.mViewSpacingRight, this.mViewSpacingBottom);
            }
            if (this.mListView != null) {
                ((LayoutParams) customPanel.getLayoutParams()).weight = 0.0f;
            }
        } else {
            customPanel.setVisibility(8);
        }
        return hasCustomView;
    }

    private boolean setupTitle(ViewGroup topPanel) {
        boolean hasTextTitle;
        if (this.mCustomTitleView != null) {
            topPanel.addView(this.mCustomTitleView, 0, new ViewGroup.LayoutParams(-1, -2));
            this.mWindow.findViewById(R.id.title_template).setVisibility(8);
            return true;
        }
        this.mIconView = (ImageView) this.mWindow.findViewById(R.id.icon);
        if (!TextUtils.isEmpty(this.mTitle)) {
            hasTextTitle = true;
        } else {
            hasTextTitle = false;
        }
        if (hasTextTitle) {
            this.mTitleView = (TextView) this.mWindow.findViewById(R.id.alertTitle);
            if (this.mTitleView != null) {
                this.mTitleView.setText(this.mTitle);
            }
            this.mTitleChoiceView = (TextView) this.mWindow.findViewById(R.id.alertChoiceTitle);
            if (this.mTitleChoiceView != null) {
                this.mTitleChoiceView.setText(this.mTitle);
            }
            if (this.mAdapter != null || this.mIsSingleChoiceDialog || this.mIsMultiChoiceDialog) {
                if (this.mTitleView != null) {
                    this.mTitleView.setVisibility(8);
                }
                if (this.mTitleChoiceView != null) {
                    this.mTitleChoiceView.setVisibility(0);
                }
                LayoutParams lp = new LayoutParams(-1, -2);
                int verMarginTop = this.mContext.getResources().getDimensionPixelOffset(R.dimen.nubia_alert_dialog_title_choice_vertical_margin_top);
                int verMarginBottom = this.mContext.getResources().getDimensionPixelOffset(R.dimen.nubia_alert_dialog_title_choice_vertical_margin_bottom);
                Log.i(TAG, "setupTitle: verMarginTop = " + verMarginTop + ", verMarginBottom = " + verMarginBottom);
                lp.setMargins(0, verMarginTop, 0, verMarginBottom);
                topPanel.setLayoutParams(lp);
            } else {
                if (this.mTitleChoiceView != null) {
                    this.mTitleChoiceView.setVisibility(8);
                }
                if (this.mTitleView != null) {
                    this.mTitleView.setVisibility(0);
                }
            }
            if (this.mIconId != 0) {
                this.mIconView.setImageResource(this.mIconId);
                return true;
            } else if (this.mIcon != null) {
                this.mIconView.setImageDrawable(this.mIcon);
                return true;
            } else {
                if (this.mTitleView != null && this.mTitleView.getVisibility() == 0) {
                    this.mTitleView.setPadding(this.mIconView.getPaddingLeft(), this.mIconView.getPaddingTop(), this.mIconView.getPaddingRight(), this.mIconView.getPaddingBottom());
                } else if (this.mTitleChoiceView != null && this.mTitleChoiceView.getVisibility() == 0) {
                    this.mTitleChoiceView.setPadding(this.mIconView.getPaddingLeft(), this.mIconView.getPaddingTop(), this.mIconView.getPaddingRight(), this.mIconView.getPaddingBottom());
                }
                this.mIconView.setVisibility(8);
                return true;
            }
        } else {
            this.mWindow.findViewById(R.id.title_template).setVisibility(8);
            this.mIconView.setVisibility(8);
            topPanel.setVisibility(8);
            return false;
        }
    }

    private void setupContent(ViewGroup contentPanel) {
        this.mScrollView = (ScrollView) this.mWindow.findViewById(R.id.scrollView);
        this.mScrollView.setFocusable(false);
        this.mMessageView = (TextView) this.mWindow.findViewById(R.id.message);
        if (this.mMessageView != null) {
            if (this.mMessage != null) {
                this.mMessageView.setText(this.mMessage);
                return;
            }
            this.mMessageView.setVisibility(8);
            this.mScrollView.removeView(this.mMessageView);
            if (this.mListView != null) {
                ViewGroup scrollParent = (ViewGroup) this.mScrollView.getParent();
                int childIndex = scrollParent.indexOfChild(this.mScrollView);
                scrollParent.removeViewAt(childIndex);
                scrollParent.addView(this.mListView, childIndex, new ViewGroup.LayoutParams(-1, -1));
                View nubiaDivider = this.mWindow.findViewById(R.id.nubia_center_title_content_divider);
                if (nubiaDivider != null) {
                    nubiaDivider.setVisibility(0);
                    return;
                }
                return;
            }
            contentPanel.setVisibility(8);
        }
    }

    private static void manageScrollIndicators(View v, View upIndicator, View downIndicator) {
        int i = 0;
        if (upIndicator != null) {
            upIndicator.setVisibility(v.canScrollVertically(-1) ? 0 : 4);
        }
        if (downIndicator != null) {
            if (!v.canScrollVertically(1)) {
                i = 4;
            }
            downIndicator.setVisibility(i);
        }
    }

    private boolean setupButtons() {
        boolean hasButtons;
        int whichButtons = 0;
        this.mButtonPositive = (Button) this.mWindow.findViewById(R.id.button1);
        this.mButtonPositive.setOnClickListener(this.mButtonHandler);
        if (TextUtils.isEmpty(this.mButtonPositiveText)) {
            this.mButtonPositive.setVisibility(8);
        } else {
            this.mButtonPositive.setText(this.mButtonPositiveText);
            if (this.mButtonPositiveTextColor != 0) {
                this.mButtonPositive.setTextColor(this.mButtonPositiveTextColor);
            }
            this.mButtonPositive.setVisibility(0);
            whichButtons = 0 | 1;
        }
        this.mButtonNegative = (Button) this.mWindow.findViewById(R.id.button2);
        this.mButtonNegative.setOnClickListener(this.mButtonHandler);
        if (TextUtils.isEmpty(this.mButtonNegativeText)) {
            this.mButtonNegative.setVisibility(8);
        } else {
            this.mButtonNegative.setText(this.mButtonNegativeText);
            if (this.mButtonNegativeTextColor != 0) {
                this.mButtonNegative.setTextColor(this.mButtonNegativeTextColor);
            }
            this.mButtonNegative.setVisibility(0);
            whichButtons |= 2;
        }
        this.mButtonNeutral = (Button) this.mWindow.findViewById(R.id.button3);
        this.mButtonNeutral.setOnClickListener(this.mButtonHandler);
        if (TextUtils.isEmpty(this.mButtonNeutralText)) {
            this.mButtonNeutral.setVisibility(8);
        } else {
            this.mButtonNeutral.setText(this.mButtonNeutralText);
            if (this.mButtonNeutralTextColor != 0) {
                this.mButtonNeutral.setTextColor(this.mButtonNeutralTextColor);
            }
            this.mButtonNeutral.setVisibility(0);
            whichButtons |= 4;
        }
        resetDialogButtonStyle();
        if (whichButtons == 1) {
            centerButton(this.mButtonPositive);
        } else if (whichButtons == 2) {
            centerButton(this.mButtonNegative);
        } else if (whichButtons == 4) {
            centerButton(this.mButtonNeutral);
        } else if (whichButtons == 3) {
            layoutButtons(this.mButtonNegative, this.mButtonPositive);
        } else if (whichButtons == 7) {
        }
        if (whichButtons != 0) {
            hasButtons = true;
        } else {
            hasButtons = false;
        }
        View buttonPanel = this.mWindow.findViewById(R.id.buttonPanel);
        if (!hasButtons) {
            buttonPanel.setVisibility(8);
            ReflectUtils.invoke(this.mWindow, "setCloseOnTouchOutsideIfNotSet", false, false, new Object[]{Boolean.valueOf(true)}, Boolean.TYPE);
        }
        return hasButtons;
    }

    private void centerButton(Button button) {
        int width;
        int margin;
        LayoutParams params = (LayoutParams) button.getLayoutParams();
        params.gravity = 16;
        if (isCenterAlertDialog()) {
            width = this.mContext.getResources().getDimensionPixelSize(R.dimen.nubia_center_alert_dialog_button_width_1);
            margin = this.mContext.getResources().getDimensionPixelSize(R.dimen.nubia_center_alert_dialog_button_horizontal_margin);
        } else {
            width = this.mContext.getResources().getDimensionPixelSize(R.dimen.nubia_alert_dialog_button_width_1);
            margin = this.mContext.getResources().getDimensionPixelSize(R.dimen.nubia_alert_dialog_button_horizontal_margin_1);
        }
        params.width = width;
        params.setMargins(margin, 0, margin, 0);
        button.setLayoutParams(params);
    }

    private void layoutButtons(Button buttonLeft, Button buttonRight) {
        int width;
        LayoutParams paramsLeft = (LayoutParams) buttonLeft.getLayoutParams();
        LayoutParams paramsRight = (LayoutParams) buttonRight.getLayoutParams();
        paramsLeft.gravity = 16;
        paramsRight.gravity = 16;
        if (isCenterAlertDialog()) {
            width = this.mContext.getResources().getDimensionPixelSize(R.dimen.nubia_center_alert_dialog_button_width_2);
            int margin = this.mContext.getResources().getDimensionPixelSize(R.dimen.nubia_center_alert_dialog_button_horizontal_margin);
            paramsLeft.setMargins(margin, 0, 0, 0);
            paramsRight.setMargins(0, 0, margin, 0);
        } else {
            width = this.mContext.getResources().getDimensionPixelSize(R.dimen.nubia_alert_dialog_button_width_2);
            int margin2 = this.mContext.getResources().getDimensionPixelSize(R.dimen.nubia_alert_dialog_button_horizontal_margin_2);
            paramsLeft.setMargins(margin2, 0, margin2 / 2, 0);
            paramsRight.setMargins(margin2 / 2, 0, margin2, 0);
        }
        paramsLeft.width = width;
        paramsRight.width = width;
        buttonLeft.setLayoutParams(paramsLeft);
        buttonRight.setLayoutParams(paramsRight);
    }

    public void setAnimPanelBackground(int colorId) {
        Log.d(TAG, "setAnimPanelBackground: colorId = [0x" + Integer.toHexString(colorId) + "]  animPanel:" + this.animPanel);
        if (this.animPanel != null) {
            this.animPanel.setBackgroundResource(colorId);
        }
    }

    private void setBackground(View topPanel, View contentPanel, View customPanel, View buttonPanel, boolean hasTitle, boolean hasCustomView, boolean hasButtons) {
        int topBright = R.drawable.nubia_dialog_background_transparent;
        int topDark = R.drawable.nubia_dialog_background_transparent;
        int centerBright = R.drawable.nubia_dialog_background_transparent;
        int centerDark = R.drawable.nubia_dialog_background_transparent;
        View[] views = new View[4];
        boolean[] light = new boolean[4];
        View lastView = null;
        boolean lastLight = false;
        int pos = 0;
        if (hasTitle) {
            views[0] = topPanel;
            light[0] = false;
            pos = 0 + 1;
        }
        if (contentPanel.getVisibility() == 8) {
            contentPanel = null;
        }
        views[pos] = contentPanel;
        light[pos] = this.mListView != null;
        int pos2 = pos + 1;
        if (hasCustomView) {
            views[pos2] = customPanel;
            light[pos2] = this.mForceInverseBackground;
            pos2++;
        }
        if (hasButtons) {
            views[pos2] = buttonPanel;
            light[pos2] = true;
        }
        boolean setView = false;
        for (int pos3 = 0; pos3 < views.length; pos3++) {
            View v = views[pos3];
            if (v != null) {
                if (lastView != null) {
                    if (!setView) {
                        lastView.setBackgroundResource(lastLight ? topBright : topDark);
                    } else {
                        lastView.setBackgroundResource(lastLight ? centerBright : centerDark);
                    }
                    setView = true;
                }
                lastView = v;
                lastLight = light[pos3];
            }
        }
        if (lastView != null) {
            if (setView) {
                int bottomBright = R.drawable.nubia_dialog_background_transparent;
                int bottomMedium = R.drawable.nubia_dialog_background_transparent;
                int bottomDark = R.drawable.nubia_dialog_background_transparent;
                if (!lastLight) {
                    bottomMedium = bottomDark;
                } else if (!hasButtons) {
                    bottomMedium = bottomBright;
                }
                lastView.setBackgroundResource(bottomMedium);
            } else {
                int fullBright = R.drawable.nubia_dialog_background_transparent;
                int fullDark = R.drawable.nubia_dialog_background_transparent;
                if (!lastLight) {
                    fullBright = fullDark;
                }
                lastView.setBackgroundResource(fullBright);
            }
        }
        ListView listView = this.mListView;
        if (listView != null && this.mAdapter != null) {
            listView.setAdapter(this.mAdapter);
            listView.setDivider(null);
            int checkedItem = this.mCheckedItem;
            if (checkedItem > -1) {
                listView.setItemChecked(checkedItem, true);
                listView.setSelection(checkedItem);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void resetNubiaAlertDialogLayout() {
        this.mAlertDialogLayout = R.layout.nubia_alert_dialog_holo;
        this.mListLayout = R.layout.nubia_select_dialog_holo;
        this.mMultiChoiceItemLayout = R.layout.nubia_select_dialog_multichoice_holo;
        this.mSingleChoiceItemLayout = R.layout.nubia_select_dialog_singlechoice_holo;
        this.mListItemLayout = R.layout.nubia_select_dialog_item_holo;
    }

    /* access modifiers changed from: 0000 */
    public void setNubiaButtonBackground(int left, int mid, int right) {
        this.mNubiaLeftButtonBgID = left;
        this.mNubiaMiddleButtonBgID = mid;
        this.mNubiaRightButtonBgID = right;
    }

    private void resetDialogButtonStyle() {
        if (!TextUtils.isEmpty(this.mButtonNegativeText) && !TextUtils.isEmpty(this.mButtonPositiveText) && !TextUtils.isEmpty(this.mButtonNeutralText)) {
            this.mButtonPositive.setBackgroundResource(this.mNubiaRightButtonBgID);
            this.mButtonNegative.setBackgroundResource(this.mNubiaLeftButtonBgID);
            this.mButtonNeutral.setBackgroundResource(this.mNubiaMiddleButtonBgID);
        } else if (!TextUtils.isEmpty(this.mButtonNegativeText) && !TextUtils.isEmpty(this.mButtonPositiveText)) {
            this.mButtonPositive.setBackgroundResource(this.mNubiaRightButtonBgID);
            this.mButtonNegative.setBackgroundResource(this.mNubiaLeftButtonBgID);
        }
    }
}
