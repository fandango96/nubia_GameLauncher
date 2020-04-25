package cn.nubia.gamelauncherx.aimhelper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.nubia.gamelauncherx.R;

public class SelectableImageView extends FrameLayout {
    private Drawable bg;
    private boolean isSelected;
    private ImageView mIvBg;
    private ImageView mIvSrc;
    private TextView mTv;
    private String text;

    public SelectableImageView(@NonNull Context context) {
        this(context, null, 0);
    }

    public SelectableImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectableImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SelectableImageView);
        this.bg = a.getDrawable(R.styleable.SelectableImageView_bg);
        this.isSelected = a.getBoolean(R.styleable.SelectableImageView_selected, false);
        this.text = a.getString(R.styleable.SelectableImageView_text);
        a.recycle();
        init(context);
    }

    private void init(Context context) {
        View rootView = inflate(context, R.layout.selectable_imageview_layout, this);
        this.mIvBg = (ImageView) rootView.findViewById(R.id.iv_bg);
        this.mIvSrc = (ImageView) rootView.findViewById(R.id.iv_select);
        this.mTv = (TextView) rootView.findViewById(R.id.tv_text);
        if (this.bg != null) {
            this.mIvBg.setImageDrawable(this.bg);
        }
        this.mIvSrc.setVisibility(this.isSelected ? View.VISIBLE : View.GONE);
        if (!TextUtils.isEmpty(this.text)) {
            this.mTv.setText(this.text);
            this.mTv.setVisibility(View.VISIBLE);
            return;
        }
        this.mTv.setVisibility(View.GONE);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
    }

    public void setSelect(boolean isSelected2) {
        this.isSelected = isSelected2;
        this.mIvSrc.setVisibility(isSelected2 ? View.VISIBLE : View.INVISIBLE);
        postInvalidate();
    }
}
