package cn.nubia.commonui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.os.ResultReceiver;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.CollapsibleActionView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import cn.nubia.commonui.R;
import cn.nubia.commonui.ReflectUtils;
import java.lang.reflect.Method;
import java.util.WeakHashMap;

public class NubiaSearchView extends LinearLayout implements CollapsibleActionView {
    private static final boolean DBG = false;
    private static final String LOG_TAG = "NubiaSearchView";
    public static final int MAX = 99;
    private final View mCancelArea;
    private final ImageView mCancelDivider;
    /* access modifiers changed from: private */
    public OnClickListener mCancelListener;
    private final TextView mCancelTextView;
    private boolean mClearingFocus;
    /* access modifiers changed from: private */
    public final ImageView mCloseButton;
    private int mCollapsedImeOptions;
    private boolean mExpandedInActionView;
    private boolean mIconified;
    private boolean mIconifiedByDefault;
    private int mMaxWidth;
    private CharSequence mOldQueryText;
    private final OnClickListener mOnClickListener;
    private OnCloseListener mOnCloseListener;
    private final OnEditorActionListener mOnEditorActionListener;
    private OnQueryTextListener mOnQueryChangeListener;
    /* access modifiers changed from: private */
    public OnFocusChangeListener mOnQueryTextFocusChangeListener;
    private OnClickListener mOnSearchClickListener;
    private final WeakHashMap<String, ConstantState> mOutsideDrawablesCache;
    private CharSequence mQueryHint;
    private boolean mQueryRefinement;
    private final EditText mQueryTextView;
    /* access modifiers changed from: private */
    public final ImageView mSearchButton;
    private final View mSearchEditFrame;
    private final ImageView mSearchHintIcon;
    private final int mSearchIconResId;
    private final View mSearchPlate;
    private Runnable mShowImeRunnable;
    private final View mSubmitArea;
    /* access modifiers changed from: private */
    public final ImageView mSubmitButton;
    private boolean mSubmitButtonEnabled;
    private TextWatcher mTextWatcher;
    private final TextView mTotalTextView;
    private Runnable mUpdateDrawableStateRunnable;
    private CharSequence mUserQuery;

    public interface OnCloseListener {
        boolean onClose();
    }

    public interface OnQueryTextListener {
        boolean onQueryTextChange(String str);

        boolean onQueryTextSubmit(String str);
    }

    /* access modifiers changed from: private */
    public void showSoftInputUnchecked() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService("input_method");
        if (imm != null) {
            try {
                Method method = Class.forName("android.view.inputmethod.InputMethodManager").getMethod("showSoftInputUnchecked", new Class[0]);
                if (method != null) {
                    method.setAccessible(true);
                    method.invoke(imm, new Object[]{Integer.valueOf(0), null});
                    method.setAccessible(false);
                }
            } catch (ClassNotFoundException ce) {
                ce.printStackTrace();
            } catch (NoSuchMethodError e) {
                e.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public NubiaSearchView(Context context) {
        this(context, null);
    }

    public NubiaSearchView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.nubiaSearchViewStyle);
    }

    public NubiaSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public NubiaSearchView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mIconifiedByDefault = false;
        this.mShowImeRunnable = new Runnable() {
            public void run() {
                NubiaSearchView.this.showSoftInputUnchecked();
                InputMethodManager imm = (InputMethodManager) NubiaSearchView.this.getContext().getSystemService("input_method");
                if (imm != null) {
                    Rect r = new Rect();
                    NubiaSearchView.this.getLocalVisibleRect(r);
                    if (r.left != r.right) {
                        ReflectUtils.invoke(imm, "showSoftInputUnchecked", false, false, new Object[]{Integer.valueOf(0), null}, Integer.TYPE, ResultReceiver.class);
                    }
                }
            }
        };
        this.mUpdateDrawableStateRunnable = new Runnable() {
            public void run() {
                NubiaSearchView.this.updateFocusedState();
            }
        };
        this.mOutsideDrawablesCache = new WeakHashMap<>();
        this.mOnClickListener = new OnClickListener() {
            public void onClick(View v) {
                if (v == NubiaSearchView.this.mSearchButton) {
                    NubiaSearchView.this.onSearchClicked();
                } else if (v == NubiaSearchView.this.mSubmitButton) {
                    NubiaSearchView.this.onSubmitQuery();
                } else if (v == NubiaSearchView.this.mCloseButton) {
                    NubiaSearchView.this.onCloseClicked();
                }
            }
        };
        this.mOnEditorActionListener = new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                NubiaSearchView.this.onSubmitQuery();
                return true;
            }
        };
        this.mTextWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int before, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int after) {
                NubiaSearchView.this.onTextChanged(s);
            }

            public void afterTextChanged(Editable s) {
            }
        };
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NubiaSearchView, defStyleAttr, defStyleRes);
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(a.getResourceId(R.styleable.NubiaSearchView_layout, R.layout.nubia_search_view), this, true);
        this.mQueryTextView = (EditText) findViewById(R.id.nubia_search_src_text);
        this.mSearchEditFrame = findViewById(R.id.nubia_search_edit_frame);
        this.mSearchPlate = findViewById(R.id.nubia_search_plate);
        this.mSubmitArea = findViewById(R.id.nubia_submit_area);
        this.mCancelArea = findViewById(R.id.nubia_cancel_area);
        this.mCancelTextView = (TextView) findViewById(R.id.nubia_search_cancel_text);
        this.mCancelTextView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (NubiaSearchView.this.mCancelListener != null) {
                    NubiaSearchView.this.mCancelListener.onClick(v);
                }
            }
        });
        this.mCancelDivider = (ImageView) findViewById(R.id.nubia_search_cancel_divider);
        this.mSearchButton = (ImageView) findViewById(R.id.nubia_search_button);
        this.mSubmitButton = (ImageView) findViewById(R.id.nubia_search_go_btn);
        this.mCloseButton = (ImageView) findViewById(R.id.nubia_search_close_btn);
        this.mSearchHintIcon = (ImageView) findViewById(R.id.nubia_search_mag_icon);
        this.mTotalTextView = (TextView) findViewById(R.id.nubia_total);
        this.mSearchEditFrame.setBackground(a.getDrawable(R.styleable.NubiaSearchView_queryBackground));
        this.mSubmitArea.setBackground(a.getDrawable(R.styleable.NubiaSearchView_submitBackground));
        this.mSearchIconResId = a.getResourceId(R.styleable.NubiaSearchView_searchIcon, 0);
        this.mSearchButton.setImageResource(this.mSearchIconResId);
        this.mSubmitButton.setImageDrawable(a.getDrawable(R.styleable.NubiaSearchView_goIcon));
        this.mCloseButton.setImageDrawable(a.getDrawable(R.styleable.NubiaSearchView_closeIcon));
        this.mSearchHintIcon.setImageDrawable(a.getDrawable(R.styleable.NubiaSearchView_searchIcon));
        this.mSearchButton.setOnClickListener(this.mOnClickListener);
        this.mCloseButton.setOnClickListener(this.mOnClickListener);
        this.mSubmitButton.setOnClickListener(this.mOnClickListener);
        this.mQueryTextView.addTextChangedListener(this.mTextWatcher);
        this.mQueryTextView.setOnEditorActionListener(this.mOnEditorActionListener);
        this.mQueryTextView.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (NubiaSearchView.this.mOnQueryTextFocusChangeListener != null) {
                    NubiaSearchView.this.mOnQueryTextFocusChangeListener.onFocusChange(NubiaSearchView.this, hasFocus);
                }
            }
        });
        setIconifiedByDefault(a.getBoolean(R.styleable.NubiaSearchView_iconifiedByDefault, true));
        int maxWidth = a.getDimensionPixelSize(R.styleable.NubiaSearchView_maxWidth, -1);
        if (maxWidth != -1) {
            setMaxWidth(maxWidth);
        }
        CharSequence queryHint = a.getText(R.styleable.NubiaSearchView_queryHint);
        if (!TextUtils.isEmpty(queryHint)) {
            setQueryHint(queryHint);
        }
        int imeOptions = a.getInt(R.styleable.NubiaSearchView_imeOptions, -1);
        if (imeOptions != -1) {
            setImeOptions(imeOptions);
        }
        int inputType = a.getInt(R.styleable.NubiaSearchView_inputType, -1);
        if (inputType != -1) {
            setInputType(inputType);
        }
        setFocusable(a.getBoolean(R.styleable.NubiaSearchView_focusable, true));
        a.recycle();
        updateViewsVisibility(false);
        updateQueryHint();
    }

    public View getSearchHintIcon() {
        return this.mSearchHintIcon;
    }

    public View getSearchEditFrame() {
        return this.mSearchEditFrame;
    }

    public EditText getQueryTextView() {
        return this.mQueryTextView;
    }

    public void setImeOptions(int imeOptions) {
        this.mQueryTextView.setImeOptions(imeOptions);
    }

    public int getImeOptions() {
        return this.mQueryTextView.getImeOptions();
    }

    public void setInputType(int inputType) {
        this.mQueryTextView.setInputType(inputType);
    }

    public int getInputType() {
        return this.mQueryTextView.getInputType();
    }

    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        if (this.mClearingFocus) {
            return false;
        }
        if (!isFocusable()) {
            return false;
        }
        if (isIconified()) {
            return super.requestFocus(direction, previouslyFocusedRect);
        }
        boolean result = this.mQueryTextView.requestFocus(direction, previouslyFocusedRect);
        if (!result) {
            return result;
        }
        updateViewsVisibility(false);
        return result;
    }

    public void clearFocus() {
        this.mClearingFocus = true;
        setImeVisibility(false);
        super.clearFocus();
        this.mQueryTextView.clearFocus();
        this.mClearingFocus = false;
    }

    public void setOnQueryTextListener(OnQueryTextListener listener) {
        this.mOnQueryChangeListener = listener;
    }

    public void setOnCloseListener(OnCloseListener listener) {
        this.mOnCloseListener = listener;
    }

    public void setOnQueryTextFocusChangeListener(OnFocusChangeListener listener) {
        this.mOnQueryTextFocusChangeListener = listener;
    }

    public void setOnSearchClickListener(OnClickListener listener) {
        this.mOnSearchClickListener = listener;
    }

    public CharSequence getQuery() {
        return this.mQueryTextView.getText();
    }

    public void setQuery(CharSequence query, boolean submit) {
        this.mQueryTextView.setText(query);
        if (query != null) {
            this.mQueryTextView.setSelection(this.mQueryTextView.length());
            this.mUserQuery = query;
        }
        if (submit && !TextUtils.isEmpty(query)) {
            onSubmitQuery();
        }
    }

    public void setQueryHint(CharSequence hint) {
        this.mQueryHint = hint;
        updateQueryHint();
    }

    public CharSequence getQueryHint() {
        if (this.mQueryHint != null) {
            return this.mQueryHint;
        }
        return null;
    }

    public void setIconifiedByDefault(boolean iconified) {
        if (this.mIconifiedByDefault != iconified) {
            this.mIconifiedByDefault = iconified;
            updateViewsVisibility(iconified);
            updateQueryHint();
        }
    }

    public boolean isIconfiedByDefault() {
        return this.mIconifiedByDefault;
    }

    public void setIconified(boolean iconify) {
        if (iconify) {
            onCloseClicked();
        } else {
            onSearchClicked();
        }
    }

    public boolean isIconified() {
        return this.mIconified;
    }

    public void setSubmitButtonEnabled(boolean enabled) {
        this.mSubmitButtonEnabled = enabled;
        updateViewsVisibility(isIconified());
    }

    public boolean isSubmitButtonEnabled() {
        return this.mSubmitButtonEnabled;
    }

    public void setQueryRefinementEnabled(boolean enable) {
        this.mQueryRefinement = enable;
    }

    public boolean isQueryRefinementEnabled() {
        return this.mQueryRefinement;
    }

    public void setMaxWidth(int maxpixels) {
        this.mMaxWidth = maxpixels;
        requestLayout();
    }

    public int getMaxWidth() {
        return this.mMaxWidth;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isIconified()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        switch (widthMode) {
            case Integer.MIN_VALUE:
                if (this.mMaxWidth <= 0) {
                    width = Math.min(getPreferredWidth(), width);
                    break;
                } else {
                    width = Math.min(this.mMaxWidth, width);
                    break;
                }
            case 0:
                if (this.mMaxWidth <= 0) {
                    width = getPreferredWidth();
                    break;
                } else {
                    width = this.mMaxWidth;
                    break;
                }
            case 1073741824:
                if (this.mMaxWidth > 0) {
                    width = Math.min(this.mMaxWidth, width);
                    break;
                }
                break;
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(width, 1073741824), heightMeasureSpec);
    }

    private int getPreferredWidth() {
        return getContext().getResources().getDimensionPixelSize(R.dimen.nubia_search_view_preferred_width);
    }

    private void updateViewsVisibility(boolean collapsed) {
        int visCollapsed;
        boolean hasText;
        int i = 8;
        this.mIconified = collapsed;
        if (collapsed) {
            visCollapsed = 0;
        } else {
            visCollapsed = 8;
        }
        if (!TextUtils.isEmpty(this.mQueryTextView.getText())) {
            hasText = true;
        } else {
            hasText = false;
        }
        this.mSearchButton.setVisibility(visCollapsed);
        updateSubmitButton(hasText);
        View view = this.mSearchEditFrame;
        if (!collapsed) {
            i = 0;
        }
        view.setVisibility(i);
        updateCloseButton();
        updateSubmitArea();
    }

    private boolean isSubmitAreaEnabled() {
        return this.mSubmitButtonEnabled && !isIconified();
    }

    private void updateSubmitButton(boolean hasText) {
        int visibility = 8;
        if (this.mSubmitButtonEnabled && isSubmitAreaEnabled() && hasFocus() && hasText) {
            visibility = 0;
        }
        this.mSubmitButton.setVisibility(visibility);
    }

    private void updateSubmitArea() {
        int visibility = 8;
        if (isSubmitAreaEnabled() && this.mSubmitButton.getVisibility() == 0) {
            visibility = 0;
        }
        this.mSubmitArea.setVisibility(visibility);
    }

    private void updateCloseButton() {
        boolean hasText;
        int i = 0;
        if (!TextUtils.isEmpty(this.mQueryTextView.getText())) {
            hasText = true;
        } else {
            hasText = false;
        }
        ImageView imageView = this.mCloseButton;
        if (!hasText) {
            i = 8;
        }
        imageView.setVisibility(i);
        if (!hasText) {
            updateTotalTextView(-1);
        }
        this.mCloseButton.getDrawable().setState(hasText ? ENABLED_STATE_SET : EMPTY_STATE_SET);
    }

    private void postUpdateFocusedState() {
        post(this.mUpdateDrawableStateRunnable);
    }

    /* access modifiers changed from: private */
    public void updateFocusedState() {
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        removeCallbacks(this.mUpdateDrawableStateRunnable);
        super.onDetachedFromWindow();
    }

    private void setImeVisibility(boolean visible) {
        if (visible) {
            post(this.mShowImeRunnable);
            return;
        }
        removeCallbacks(this.mShowImeRunnable);
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService("input_method");
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindowToken(), 0);
        }
    }

    /* access modifiers changed from: private */
    public void onSubmitQuery() {
        CharSequence query = this.mQueryTextView.getText();
        if (query != null && TextUtils.getTrimmedLength(query) > 0) {
            if (this.mOnQueryChangeListener == null || !this.mOnQueryChangeListener.onQueryTextSubmit(query.toString())) {
                setImeVisibility(false);
            }
        }
    }

    private void updateQueryHint() {
        if (this.mQueryHint != null) {
            this.mQueryTextView.setHint(this.mQueryHint);
        } else {
            this.mQueryTextView.setHint("");
        }
    }

    /* access modifiers changed from: private */
    public void onTextChanged(CharSequence newText) {
        CharSequence text = this.mQueryTextView.getText();
        this.mUserQuery = text;
        updateSubmitButton(!TextUtils.isEmpty(text));
        updateCloseButton();
        updateSubmitArea();
        if (this.mOnQueryChangeListener != null && !TextUtils.equals(newText, this.mOldQueryText)) {
            this.mOnQueryChangeListener.onQueryTextChange(newText.toString());
        }
        this.mOldQueryText = newText.toString();
    }

    public void setTotalTextHint(int total) {
        updateTotalTextView(total);
    }

    private void updateTotalTextView(int total) {
        if (total > 99) {
            this.mTotalTextView.setText(getContext().getString(R.string.nubia_total_text, new Object[]{String.valueOf(99) + "+"}));
            this.mTotalTextView.setVisibility(0);
        } else if (total >= 0) {
            this.mTotalTextView.setText(getContext().getString(R.string.nubia_total_text, new Object[]{String.valueOf(total)}));
            this.mTotalTextView.setVisibility(0);
        }
        if (this.mCloseButton.getVisibility() != 0) {
            this.mTotalTextView.setVisibility(8);
        }
    }

    /* access modifiers changed from: private */
    public void onCloseClicked() {
        if (!TextUtils.isEmpty(this.mQueryTextView.getText())) {
            this.mQueryTextView.setText("");
            updateTotalTextView(-1);
            this.mQueryTextView.requestFocus();
            setImeVisibility(true);
        } else if (!this.mIconifiedByDefault) {
        } else {
            if (this.mOnCloseListener == null || !this.mOnCloseListener.onClose()) {
                clearFocus();
                updateViewsVisibility(true);
            }
        }
    }

    /* access modifiers changed from: private */
    public void onSearchClicked() {
        updateViewsVisibility(false);
        this.mQueryTextView.requestFocus();
        setImeVisibility(true);
        if (this.mOnSearchClickListener != null) {
            this.mOnSearchClickListener.onClick(this);
        }
    }

    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        postUpdateFocusedState();
    }

    public void onActionViewCollapsed() {
        setQuery("", false);
        clearFocus();
        updateViewsVisibility(true);
        this.mQueryTextView.setImeOptions(this.mCollapsedImeOptions);
        this.mExpandedInActionView = false;
    }

    public void onActionViewExpanded() {
        if (!this.mExpandedInActionView) {
            this.mExpandedInActionView = true;
            this.mCollapsedImeOptions = this.mQueryTextView.getImeOptions();
            this.mQueryTextView.setImeOptions(this.mCollapsedImeOptions | 33554432);
            this.mQueryTextView.setText("");
            setIconified(false);
        }
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(NubiaSearchView.class.getName());
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(NubiaSearchView.class.getName());
    }

    private void setQuery(CharSequence query) {
        int length;
        this.mQueryTextView.setText(query);
        EditText editText = this.mQueryTextView;
        if (TextUtils.isEmpty(query)) {
            length = 0;
        } else {
            length = query.length();
        }
        editText.setSelection(length);
    }

    static boolean isLandscapeMode(Context context) {
        return context.getResources().getConfiguration().orientation == 2;
    }

    private void updateQueryTextAppearence(boolean hasText) {
        this.mQueryTextView.setTextSize(2, 16.0f);
    }

    public void setCancelBtnVisibility(boolean visibility) {
        this.mCancelArea.setVisibility(visibility ? 0 : 8);
    }

    public void setCancelListener(OnClickListener cancelListener) {
        this.mCancelListener = cancelListener;
    }

    public void setCancelTextViewColor(int color) {
        if (this.mCancelTextView != null) {
            this.mCancelTextView.setTextColor(getResources().getColorStateList(color));
        }
    }

    public void setCancelDividerBackgroundResource(int resid) {
        this.mCancelDivider.setBackgroundResource(resid);
    }

    public void setCancelDividerBackground(Drawable drawable) {
        this.mCancelDivider.setBackground(drawable);
    }
}
