package cn.nubia.commonui.actionbar.internal.widget;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.SpinnerAdapter;
import cn.nubia.commonui.R;
import cn.nubia.commonui.actionbar.widget.ListPopupWindow;
import cn.nubia.commonui.actionbar.widget.ListPopupWindow.ForwardingListener;

class SpinnerCompat extends AbsSpinnerCompat implements OnClickListener {
    private static final int MAX_ITEMS_MEASURED = 15;
    public static final int MODE_DIALOG = 0;
    public static final int MODE_DROPDOWN = 1;
    private static final int MODE_THEME = -1;
    private static final String TAG = "NubiaWidget";
    private boolean mDisableChildrenWhenDisabled;
    int mDropDownWidth;
    private ForwardingListener mForwardingListener;
    private int mGravity;
    /* access modifiers changed from: private */
    public SpinnerPopup mPopup;
    private DropDownAdapter mTempAdapter;
    /* access modifiers changed from: private */
    public Rect mTempRect;
    private final TintManager mTintManager;

    private class DialogPopup implements SpinnerPopup, OnClickListener {
        private ListAdapter mListAdapter;
        private AlertDialog mPopup;
        private CharSequence mPrompt;

        private DialogPopup() {
        }

        public void dismiss() {
            if (this.mPopup != null) {
                this.mPopup.dismiss();
                this.mPopup = null;
            }
        }

        public boolean isShowing() {
            if (this.mPopup != null) {
                return this.mPopup.isShowing();
            }
            return false;
        }

        public void setAdapter(ListAdapter adapter) {
            this.mListAdapter = adapter;
        }

        public void setPromptText(CharSequence hintText) {
            this.mPrompt = hintText;
        }

        public CharSequence getHintText() {
            return this.mPrompt;
        }

        public void show() {
            if (this.mListAdapter != null) {
                Builder builder = new Builder(SpinnerCompat.this.getContext());
                if (this.mPrompt != null) {
                    builder.setTitle(this.mPrompt);
                }
                this.mPopup = builder.setSingleChoiceItems(this.mListAdapter, SpinnerCompat.this.getSelectedItemPosition(), this).create();
                this.mPopup.show();
            }
        }

        public void onClick(DialogInterface dialog, int which) {
            SpinnerCompat.this.setSelection(which);
            if (SpinnerCompat.this.mOnItemClickListener != null) {
                SpinnerCompat.this.performItemClick(null, which, this.mListAdapter.getItemId(which));
            }
            dismiss();
        }

        public void setBackgroundDrawable(Drawable bg) {
            Log.e(SpinnerCompat.TAG, "Cannot set popup background for MODE_DIALOG, ignoring");
        }

        public void setVerticalOffset(int px) {
            Log.e(SpinnerCompat.TAG, "Cannot set vertical offset for MODE_DIALOG, ignoring");
        }

        public void setHorizontalOffset(int px) {
            Log.e(SpinnerCompat.TAG, "Cannot set horizontal offset for MODE_DIALOG, ignoring");
        }

        public Drawable getBackground() {
            return null;
        }

        public int getVerticalOffset() {
            return 0;
        }

        public int getHorizontalOffset() {
            return 0;
        }
    }

    private static class DropDownAdapter implements ListAdapter, SpinnerAdapter {
        private SpinnerAdapter mAdapter;
        private ListAdapter mListAdapter;

        public DropDownAdapter(SpinnerAdapter adapter) {
            this.mAdapter = adapter;
            if (adapter instanceof ListAdapter) {
                this.mListAdapter = (ListAdapter) adapter;
            }
        }

        public int getCount() {
            if (this.mAdapter == null) {
                return 0;
            }
            return this.mAdapter.getCount();
        }

        public Object getItem(int position) {
            if (this.mAdapter == null) {
                return null;
            }
            return this.mAdapter.getItem(position);
        }

        public long getItemId(int position) {
            if (this.mAdapter == null) {
                return -1;
            }
            return this.mAdapter.getItemId(position);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            return getDropDownView(position, convertView, parent);
        }

        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if (this.mAdapter == null) {
                return null;
            }
            return this.mAdapter.getDropDownView(position, convertView, parent);
        }

        public boolean hasStableIds() {
            return this.mAdapter != null && this.mAdapter.hasStableIds();
        }

        public void registerDataSetObserver(DataSetObserver observer) {
            if (this.mAdapter != null) {
                this.mAdapter.registerDataSetObserver(observer);
            }
        }

        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (this.mAdapter != null) {
                this.mAdapter.unregisterDataSetObserver(observer);
            }
        }

        public boolean areAllItemsEnabled() {
            ListAdapter adapter = this.mListAdapter;
            if (adapter != null) {
                return adapter.areAllItemsEnabled();
            }
            return true;
        }

        public boolean isEnabled(int position) {
            ListAdapter adapter = this.mListAdapter;
            if (adapter != null) {
                return adapter.isEnabled(position);
            }
            return true;
        }

        public int getItemViewType(int position) {
            return 0;
        }

        public int getViewTypeCount() {
            return 1;
        }

        public boolean isEmpty() {
            return getCount() == 0;
        }
    }

    private class DropdownPopup extends ListPopupWindow implements SpinnerPopup {
        /* access modifiers changed from: private */
        public ListAdapter mAdapter;
        private CharSequence mHintText;

        public DropdownPopup(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            setAnchorView(SpinnerCompat.this);
            setModal(true);
            setPromptPosition(0);
            setOnItemClickListener(new OnItemClickListener(SpinnerCompat.this) {
                public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
                    SpinnerCompat.this.setSelection(position);
                    if (SpinnerCompat.this.mOnItemClickListener != null) {
                        SpinnerCompat.this.performItemClick(v, position, DropdownPopup.this.mAdapter.getItemId(position));
                    }
                    DropdownPopup.this.dismiss();
                }
            });
        }

        public void setAdapter(ListAdapter adapter) {
            super.setAdapter(adapter);
            this.mAdapter = adapter;
        }

        public CharSequence getHintText() {
            return this.mHintText;
        }

        public void setPromptText(CharSequence hintText) {
            this.mHintText = hintText;
        }

        /* access modifiers changed from: 0000 */
        public void computeContentWidth() {
            int hOffset;
            Drawable background = getBackground();
            int hOffset2 = 0;
            if (background != null) {
                background.getPadding(SpinnerCompat.this.mTempRect);
                if (ViewUtils.isLayoutRtl(SpinnerCompat.this)) {
                    hOffset2 = SpinnerCompat.this.mTempRect.right;
                } else {
                    hOffset2 = -SpinnerCompat.this.mTempRect.left;
                }
            } else {
                Rect access$400 = SpinnerCompat.this.mTempRect;
                SpinnerCompat.this.mTempRect.right = 0;
                access$400.left = 0;
            }
            int spinnerPaddingLeft = SpinnerCompat.this.getPaddingLeft();
            int spinnerPaddingRight = SpinnerCompat.this.getPaddingRight();
            int spinnerWidth = SpinnerCompat.this.getWidth();
            if (SpinnerCompat.this.mDropDownWidth == -2) {
                int contentWidth = SpinnerCompat.this.measureContentWidth((SpinnerAdapter) this.mAdapter, getBackground());
                int contentWidthLimit = (SpinnerCompat.this.getContext().getResources().getDisplayMetrics().widthPixels - SpinnerCompat.this.mTempRect.left) - SpinnerCompat.this.mTempRect.right;
                if (contentWidth > contentWidthLimit) {
                    contentWidth = contentWidthLimit;
                }
                setContentWidth(Math.max(contentWidth, (spinnerWidth - spinnerPaddingLeft) - spinnerPaddingRight));
            } else if (SpinnerCompat.this.mDropDownWidth == -1) {
                setContentWidth((spinnerWidth - spinnerPaddingLeft) - spinnerPaddingRight);
            } else {
                setContentWidth(SpinnerCompat.this.mDropDownWidth);
            }
            if (ViewUtils.isLayoutRtl(SpinnerCompat.this)) {
                hOffset = hOffset2 + ((spinnerWidth - spinnerPaddingRight) - getWidth());
            } else {
                hOffset = hOffset2 + spinnerPaddingLeft;
            }
            setHorizontalOffset(hOffset);
        }

        public void show(int textDirection, int textAlignment) {
            boolean wasShowing = isShowing();
            computeContentWidth();
            setInputMethodMode(2);
            super.show();
            getListView().setChoiceMode(1);
            setSelection(SpinnerCompat.this.getSelectedItemPosition());
            if (!wasShowing) {
                ViewTreeObserver vto = SpinnerCompat.this.getViewTreeObserver();
                if (vto != null) {
                    final OnGlobalLayoutListener layoutListener = new OnGlobalLayoutListener() {
                        public void onGlobalLayout() {
                            DropdownPopup.this.computeContentWidth();
                            DropdownPopup.super.show();
                        }
                    };
                    vto.addOnGlobalLayoutListener(layoutListener);
                    setOnDismissListener(new OnDismissListener() {
                        public void onDismiss() {
                            ViewTreeObserver vto = SpinnerCompat.this.getViewTreeObserver();
                            if (vto != null) {
                                vto.removeGlobalOnLayoutListener(layoutListener);
                            }
                        }
                    });
                }
            }
        }
    }

    static class SavedState extends SavedState {
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        boolean showDropdown;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.showDropdown = in.readByte() != 0;
        }

        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeByte((byte) (this.showDropdown ? 1 : 0));
        }
    }

    private interface SpinnerPopup {
        void dismiss();

        Drawable getBackground();

        CharSequence getHintText();

        int getHorizontalOffset();

        int getVerticalOffset();

        boolean isShowing();

        void setAdapter(ListAdapter listAdapter);

        void setBackgroundDrawable(Drawable drawable);

        void setHorizontalOffset(int i);

        void setPromptText(CharSequence charSequence);

        void setVerticalOffset(int i);

        void show();
    }

    SpinnerCompat(Context context) {
        this(context, (AttributeSet) null);
    }

    SpinnerCompat(Context context, int mode) {
        this(context, null, R.attr.spinnerStyle, mode);
    }

    SpinnerCompat(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.spinnerStyle);
    }

    SpinnerCompat(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, -1);
    }

    SpinnerCompat(Context context, AttributeSet attrs, int defStyle, int mode) {
        super(context, attrs, defStyle);
        this.mTempRect = new Rect();
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs, R.styleable.Spinner, defStyle, 0);
        if (a.hasValue(R.styleable.Spinner_android_background)) {
            setBackgroundDrawable(a.getDrawable(R.styleable.Spinner_android_background));
        }
        if (mode == -1) {
            mode = a.getInt(R.styleable.Spinner_spinnerMode, 0);
        }
        switch (mode) {
            case 0:
                this.mPopup = new DialogPopup();
                break;
            case 1:
                final DropdownPopup popup = new DropdownPopup(context, attrs, defStyle);
                this.mDropDownWidth = a.getLayoutDimension(R.styleable.Spinner_android_dropDownWidth, -2);
                popup.setBackgroundDrawable(a.getDrawable(R.styleable.Spinner_android_popupBackground));
                this.mPopup = popup;
                this.mForwardingListener = new ForwardingListener(this) {
                    public ListPopupWindow getPopup() {
                        return popup;
                    }

                    public boolean onForwardingStarted() {
                        if (!SpinnerCompat.this.mPopup.isShowing()) {
                            SpinnerCompat.this.mPopup.show();
                        }
                        return true;
                    }
                };
                break;
        }
        this.mGravity = a.getInt(R.styleable.Spinner_android_gravity, 17);
        this.mPopup.setPromptText(a.getString(R.styleable.Spinner_prompt));
        this.mDisableChildrenWhenDisabled = a.getBoolean(R.styleable.Spinner_disableChildrenWhenDisabled, false);
        a.recycle();
        if (this.mTempAdapter != null) {
            this.mPopup.setAdapter(this.mTempAdapter);
            this.mTempAdapter = null;
        }
        this.mTintManager = a.getTintManager();
    }

    public void setPopupBackgroundDrawable(Drawable background) {
        if (!(this.mPopup instanceof DropdownPopup)) {
            Log.e(TAG, "setPopupBackgroundDrawable: incompatible spinner mode; ignoring...");
        } else {
            ((DropdownPopup) this.mPopup).setBackgroundDrawable(background);
        }
    }

    public void setPopupBackgroundResource(int resId) {
        setPopupBackgroundDrawable(this.mTintManager.getDrawable(resId));
    }

    public Drawable getPopupBackground() {
        return this.mPopup.getBackground();
    }

    public void setDropDownVerticalOffset(int pixels) {
        this.mPopup.setVerticalOffset(pixels);
    }

    public int getDropDownVerticalOffset() {
        return this.mPopup.getVerticalOffset();
    }

    public void setDropDownHorizontalOffset(int pixels) {
        this.mPopup.setHorizontalOffset(pixels);
    }

    public int getDropDownHorizontalOffset() {
        return this.mPopup.getHorizontalOffset();
    }

    public void setDropDownWidth(int pixels) {
        if (!(this.mPopup instanceof DropdownPopup)) {
            Log.e(TAG, "Cannot set dropdown width for MODE_DIALOG, ignoring");
        } else {
            this.mDropDownWidth = pixels;
        }
    }

    public int getDropDownWidth() {
        return this.mDropDownWidth;
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (this.mDisableChildrenWhenDisabled) {
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                getChildAt(i).setEnabled(enabled);
            }
        }
    }

    public void setGravity(int gravity) {
        if (this.mGravity != gravity) {
            if ((gravity & 7) == 0) {
                gravity |= GravityCompat.START;
            }
            this.mGravity = gravity;
            requestLayout();
        }
    }

    public void setAdapter(SpinnerAdapter adapter) {
        super.setAdapter(adapter);
        this.mRecycler.clear();
        if (getContext().getApplicationInfo().targetSdkVersion >= 21 && adapter != null && adapter.getViewTypeCount() != 1) {
            throw new IllegalArgumentException("Spinner adapter view type count must be 1");
        } else if (this.mPopup != null) {
            this.mPopup.setAdapter(new DropDownAdapter(adapter));
        } else {
            this.mTempAdapter = new DropDownAdapter(adapter);
        }
    }

    public int getBaseline() {
        View child = null;
        if (getChildCount() > 0) {
            child = getChildAt(0);
        } else if (this.mAdapter != null && this.mAdapter.getCount() > 0) {
            child = makeView(0, false);
            this.mRecycler.put(0, child);
        }
        if (child == null) {
            return -1;
        }
        int childBaseline = child.getBaseline();
        if (childBaseline >= 0) {
            return child.getTop() + childBaseline;
        }
        return -1;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mPopup != null && this.mPopup.isShowing()) {
            this.mPopup.dismiss();
        }
    }

    public void setOnItemClickListener(AdapterViewCompat.OnItemClickListener l) {
        throw new RuntimeException("setOnItemClickListener cannot be used with a spinner.");
    }

    /* access modifiers changed from: 0000 */
    public void setOnItemClickListenerInt(AdapterViewCompat.OnItemClickListener l) {
        super.setOnItemClickListener(l);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.mForwardingListener == null || !this.mForwardingListener.onTouch(this, event)) {
            return super.onTouchEvent(event);
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.mPopup != null && MeasureSpec.getMode(widthMeasureSpec) == Integer.MIN_VALUE) {
            setMeasuredDimension(Math.min(Math.max(getMeasuredWidth(), measureContentWidth(getAdapter(), getBackground())), MeasureSpec.getSize(widthMeasureSpec)), getMeasuredHeight());
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        this.mInLayout = true;
        layout(0, false);
        this.mInLayout = false;
    }

    /* access modifiers changed from: 0000 */
    public void layout(int delta, boolean animate) {
        int childrenLeft = this.mSpinnerPadding.left;
        int childrenWidth = ((getRight() - getLeft()) - this.mSpinnerPadding.left) - this.mSpinnerPadding.right;
        if (this.mDataChanged) {
            handleDataChanged();
        }
        if (this.mItemCount == 0) {
            resetList();
            return;
        }
        if (this.mNextSelectedPosition >= 0) {
            setSelectedPositionInt(this.mNextSelectedPosition);
        }
        recycleAllViews();
        removeAllViewsInLayout();
        this.mFirstPosition = this.mSelectedPosition;
        if (this.mAdapter != null) {
            View sel = makeView(this.mSelectedPosition, true);
            int width = sel.getMeasuredWidth();
            int selectedOffset = childrenLeft;
            switch (GravityCompat.getAbsoluteGravity(this.mGravity, ViewCompat.getLayoutDirection(this)) & 7) {
                case 1:
                    selectedOffset = ((childrenWidth / 2) + childrenLeft) - (width / 2);
                    break;
                case 5:
                    selectedOffset = (childrenLeft + childrenWidth) - width;
                    break;
            }
            sel.offsetLeftAndRight(selectedOffset);
        }
        this.mRecycler.clear();
        invalidate();
        checkSelectionChanged();
        this.mDataChanged = false;
        this.mNeedSync = false;
        setNextSelectedPositionInt(this.mSelectedPosition);
    }

    private View makeView(int position, boolean addChild) {
        if (!this.mDataChanged) {
            View child = this.mRecycler.get(position);
            if (child != null) {
                setUpChild(child, addChild);
                return child;
            }
        }
        View child2 = this.mAdapter.getView(position, null, this);
        setUpChild(child2, addChild);
        return child2;
    }

    private void setUpChild(View child, boolean addChild) {
        LayoutParams lp = child.getLayoutParams();
        if (lp == null) {
            lp = generateDefaultLayoutParams();
        }
        if (addChild) {
            addViewInLayout(child, 0, lp);
        }
        child.setSelected(hasFocus());
        if (this.mDisableChildrenWhenDisabled) {
            child.setEnabled(isEnabled());
        }
        child.measure(ViewGroup.getChildMeasureSpec(this.mWidthMeasureSpec, this.mSpinnerPadding.left + this.mSpinnerPadding.right, lp.width), ViewGroup.getChildMeasureSpec(this.mHeightMeasureSpec, this.mSpinnerPadding.top + this.mSpinnerPadding.bottom, lp.height));
        int childTop = this.mSpinnerPadding.top + ((((getMeasuredHeight() - this.mSpinnerPadding.bottom) - this.mSpinnerPadding.top) - child.getMeasuredHeight()) / 2);
        child.layout(0, childTop, 0 + child.getMeasuredWidth(), childTop + child.getMeasuredHeight());
    }

    public boolean performClick() {
        boolean handled = super.performClick();
        if (!handled) {
            handled = true;
            if (!this.mPopup.isShowing()) {
                this.mPopup.show();
            }
        }
        return handled;
    }

    public void onClick(DialogInterface dialog, int which) {
        setSelection(which);
        dialog.dismiss();
    }

    public void setPrompt(CharSequence prompt) {
        this.mPopup.setPromptText(prompt);
    }

    public void setPromptId(int promptId) {
        setPrompt(getContext().getText(promptId));
    }

    public CharSequence getPrompt() {
        return this.mPopup.getHintText();
    }

    /* access modifiers changed from: 0000 */
    public int measureContentWidth(SpinnerAdapter adapter, Drawable background) {
        if (adapter == null) {
            return 0;
        }
        int width = 0;
        View itemView = null;
        int itemType = 0;
        int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, 0);
        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, 0);
        int start = Math.max(0, getSelectedItemPosition());
        int end = Math.min(adapter.getCount(), start + 15);
        for (int i = Math.max(0, start - (15 - (end - start))); i < end; i++) {
            int positionType = adapter.getItemViewType(i);
            if (positionType != itemType) {
                itemType = positionType;
                itemView = null;
            }
            itemView = adapter.getView(i, itemView, this);
            if (itemView.getLayoutParams() == null) {
                itemView.setLayoutParams(new LayoutParams(-2, -2));
            }
            itemView.measure(widthMeasureSpec, heightMeasureSpec);
            width = Math.max(width, itemView.getMeasuredWidth());
        }
        if (background == null) {
            return width;
        }
        background.getPadding(this.mTempRect);
        return width + this.mTempRect.left + this.mTempRect.right;
    }

    public Parcelable onSaveInstanceState() {
        SavedState ss = new SavedState(super.onSaveInstanceState());
        ss.showDropdown = this.mPopup != null && this.mPopup.isShowing();
        return ss;
    }

    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        if (ss.showDropdown) {
            ViewTreeObserver vto = getViewTreeObserver();
            if (vto != null) {
                vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (!SpinnerCompat.this.mPopup.isShowing()) {
                            SpinnerCompat.this.mPopup.show();
                        }
                        ViewTreeObserver vto = SpinnerCompat.this.getViewTreeObserver();
                        if (vto != null) {
                            vto.removeGlobalOnLayoutListener(this);
                        }
                    }
                });
            }
        }
    }
}
