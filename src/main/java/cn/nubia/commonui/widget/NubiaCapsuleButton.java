package cn.nubia.commonui.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import cn.nubia.commonui.R;

public class NubiaCapsuleButton extends LinearLayout {
    public static final int LEFT_BUTTON = 1;
    private static final int MAX_CAPSULE_NUMS = 4;
    public static final int MID1_BUTTON = 2;
    public static final int MID2_BUTTON = 3;
    private static final int MIN_CAPSULE_NUMS = 2;
    public static final int RIGHT_BUTTON = 4;
    private Drawable mBackgroundLeft;
    private Drawable mBackgroundMid1;
    private Drawable mBackgroundMid2;
    private Drawable mBackgroundRight;
    private int mCapsuleNums;
    private String[] mItems;
    /* access modifiers changed from: private */
    public Button mLeftButton;
    /* access modifiers changed from: private */
    public OnCapsuleClickListener mLeftButtonListener;
    private DisplayMetrics mMetrics;
    /* access modifiers changed from: private */
    public Button mMid1Button;
    /* access modifiers changed from: private */
    public OnCapsuleClickListener mMid1ButtonListener;
    /* access modifiers changed from: private */
    public Button mMid2Button;
    /* access modifiers changed from: private */
    public OnCapsuleClickListener mMid2ButtonListener;
    private int mNormalTextColor;
    private final OnClickListener mOnClickListener;
    /* access modifiers changed from: private */
    public Button mRightButton;
    /* access modifiers changed from: private */
    public OnCapsuleClickListener mRightButtonListener;
    private int mSelectedTextColor;
    private ColorStateList mTextColor;
    private WindowManager mWindowManager;

    public interface OnCapsuleClickListener {
        void onCapsuleClick(int i);
    }

    public NubiaCapsuleButton(Context context) {
        this(context, null);
    }

    public NubiaCapsuleButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.capsuleButtonStyle);
    }

    public NubiaCapsuleButton(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, 0);
    }

    public NubiaCapsuleButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mCapsuleNums = 4;
        this.mOnClickListener = new OnClickListener() {
            public void onClick(View view) {
                NubiaCapsuleButton.this.clearSelected();
                if (view == NubiaCapsuleButton.this.mLeftButton && NubiaCapsuleButton.this.mLeftButtonListener != null) {
                    NubiaCapsuleButton.this.mLeftButton.setSelected(true);
                    NubiaCapsuleButton.this.mLeftButtonListener.onCapsuleClick(1);
                } else if (view == NubiaCapsuleButton.this.mMid1Button && NubiaCapsuleButton.this.mMid1ButtonListener != null) {
                    NubiaCapsuleButton.this.mMid1Button.setSelected(true);
                    NubiaCapsuleButton.this.mMid1ButtonListener.onCapsuleClick(2);
                } else if (view == NubiaCapsuleButton.this.mMid2Button && NubiaCapsuleButton.this.mMid2ButtonListener != null) {
                    NubiaCapsuleButton.this.mMid2Button.setSelected(true);
                    NubiaCapsuleButton.this.mMid2ButtonListener.onCapsuleClick(3);
                } else if (view == NubiaCapsuleButton.this.mRightButton && NubiaCapsuleButton.this.mRightButtonListener != null) {
                    NubiaCapsuleButton.this.mRightButton.setSelected(true);
                    NubiaCapsuleButton.this.mRightButtonListener.onCapsuleClick(4);
                }
            }
        };
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CapsuleButton, defStyleAttr, 0);
        this.mBackgroundLeft = typedArray.getDrawable(R.styleable.CapsuleButton_capsuleBackgroundLeft);
        this.mBackgroundMid1 = typedArray.getDrawable(R.styleable.CapsuleButton_capsuleBackgroundMid);
        this.mBackgroundMid2 = typedArray.getDrawable(R.styleable.CapsuleButton_capsuleBackgroundMid);
        this.mBackgroundRight = typedArray.getDrawable(R.styleable.CapsuleButton_capsuleBackgroundRight);
        this.mSelectedTextColor = typedArray.getColor(R.styleable.CapsuleButton_capsuleSelectedTextColor, R.color.nubia_primary_text_default_material_light);
        this.mNormalTextColor = typedArray.getColor(R.styleable.CapsuleButton_capsuleNormalTextColor, R.color.nubia_secondary_text_default_material_light);
        typedArray.recycle();
        this.mTextColor = new ColorStateList(new int[][]{new int[]{16842913}, new int[]{0}}, new int[]{this.mSelectedTextColor, this.mNormalTextColor});
        this.mMetrics = new DisplayMetrics();
        this.mWindowManager = (WindowManager) context.getSystemService("window");
        initView();
    }

    private void initView() {
        View buttonContent = LayoutInflater.from(getContext()).inflate(R.layout.nubia_capsule_button, this, true);
        this.mLeftButton = (Button) buttonContent.findViewById(R.id.nubia_button_left);
        this.mMid1Button = (Button) buttonContent.findViewById(R.id.nubia_button_mid1);
        this.mMid2Button = (Button) buttonContent.findViewById(R.id.nubia_button_mid2);
        this.mRightButton = (Button) buttonContent.findViewById(R.id.nubia_button_right);
        this.mLeftButton.setOnClickListener(this.mOnClickListener);
        this.mMid1Button.setOnClickListener(this.mOnClickListener);
        this.mMid2Button.setOnClickListener(this.mOnClickListener);
        this.mRightButton.setOnClickListener(this.mOnClickListener);
        setInitTextColor();
        setInitBackground();
        clearSelected();
        this.mLeftButton.setSelected(true);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.mWindowManager.getDefaultDisplay().getMetrics(this.mMetrics);
        int ratio = this.mMetrics.densityDpi / 160;
        int maxButtonWidth = Math.max(Math.max(this.mLeftButton.getMeasuredWidth(), this.mRightButton.getMeasuredWidth()), Math.max(this.mMid1Button.getMeasuredWidth(), this.mMid2Button.getMeasuredWidth()));
        int needButtonWidth = (this.mMetrics.widthPixels - (ratio * 28)) / this.mCapsuleNums;
        if (maxButtonWidth > needButtonWidth) {
            maxButtonWidth = needButtonWidth;
        }
        LayoutParams params = new LayoutParams(maxButtonWidth, -2);
        this.mLeftButton.setLayoutParams(params);
        this.mMid1Button.setLayoutParams(params);
        this.mMid2Button.setLayoutParams(params);
        this.mRightButton.setLayoutParams(params);
    }

    public void setCapsuleNums(int nums) {
        if (nums < 5 && nums > 1) {
            this.mCapsuleNums = nums;
            if (nums < 3) {
                this.mMid1Button.setVisibility(8);
                this.mMid2Button.setVisibility(8);
            } else if (nums < 4) {
                this.mMid2Button.setVisibility(8);
            }
        }
    }

    public void setOnClickListener(OnCapsuleClickListener listener) {
        this.mLeftButtonListener = listener;
        this.mMid1ButtonListener = listener;
        this.mMid2ButtonListener = listener;
        this.mRightButtonListener = listener;
    }

    public void setItemTitles(int itemsId) {
        this.mItems = getResources().getStringArray(itemsId);
        setItemTitles(this.mItems);
    }

    public void setItemTitles(String[] items) {
        this.mItems = items;
        int length = this.mItems.length;
        if (length > 1) {
            this.mLeftButton.setText(this.mItems[0]);
            this.mRightButton.setText(this.mItems[length - 1]);
        }
        if (length > 2) {
            this.mMid1Button.setText(this.mItems[1]);
        }
        if (length > 3) {
            this.mMid2Button.setText(this.mItems[2]);
        }
    }

    /* access modifiers changed from: private */
    public void clearSelected() {
        this.mLeftButton.setSelected(false);
        this.mMid1Button.setSelected(false);
        this.mMid2Button.setSelected(false);
        this.mRightButton.setSelected(false);
    }

    public void setInitSelected(int index) {
        clearSelected();
        if (index <= this.mCapsuleNums) {
            switch (index) {
                case 1:
                    this.mLeftButton.setSelected(true);
                    return;
                case 2:
                    if (2 == this.mCapsuleNums) {
                        this.mRightButton.setSelected(true);
                        return;
                    } else {
                        this.mMid1Button.setSelected(true);
                        return;
                    }
                case 3:
                    if (3 == this.mCapsuleNums) {
                        this.mRightButton.setSelected(true);
                        return;
                    } else {
                        this.mMid2Button.setSelected(true);
                        return;
                    }
                case 4:
                    this.mRightButton.setSelected(true);
                    return;
                default:
                    return;
            }
        }
    }

    private void setInitTextColor() {
        if (this.mTextColor != null) {
            this.mLeftButton.setTextColor(this.mTextColor);
            this.mMid1Button.setTextColor(this.mTextColor);
            this.mMid2Button.setTextColor(this.mTextColor);
            this.mRightButton.setTextColor(this.mTextColor);
        }
    }

    private void setInitBackground() {
        if (this.mBackgroundLeft != null && this.mBackgroundMid1 != null && this.mBackgroundRight != null) {
            this.mLeftButton.setBackground(this.mBackgroundLeft);
            this.mMid1Button.setBackground(this.mBackgroundMid1);
            this.mMid2Button.setBackground(this.mBackgroundMid2);
            this.mRightButton.setBackground(this.mBackgroundRight);
        }
    }
}
