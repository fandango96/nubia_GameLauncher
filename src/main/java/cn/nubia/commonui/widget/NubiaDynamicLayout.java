package cn.nubia.commonui.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import cn.nubia.commonui.R;

public class NubiaDynamicLayout extends RelativeLayout {
    private Button mButton;
    private ProgressBar mProgressBar;
    private int mTextColor;

    public NubiaDynamicLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mTextColor = -1;
        initView(context);
    }

    public NubiaDynamicLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public NubiaDynamicLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NubiaDynamicLayout(Context context) {
        this(context, null);
    }

    private void initView(Context context) {
        View.inflate(context, R.layout.nubia_dynamic_layout, this);
        this.mButton = (Button) findViewById(R.id.download_button);
        this.mProgressBar = (ProgressBar) findViewById(R.id.nubia_progressBar);
    }

    public int getProgress() {
        return this.mProgressBar.getProgress();
    }

    public void setProgress(int progress) {
        this.mProgressBar.setProgress(progress);
    }

    public void setText(String text) {
        this.mButton.setText(text);
        if (getProgress() >= getMax()) {
            this.mButton.setTextColor(this.mTextColor);
        }
    }

    private void setColor(int color) {
        this.mTextColor = color;
    }

    public int getMax() {
        return this.mProgressBar.getMax();
    }

    public void setMax(int max) {
        this.mProgressBar.setMax(max);
    }

    public void setProgressTintList(int color) {
        this.mProgressBar.setProgressTintList(ColorStateList.valueOf(color));
    }

    public void setBackground(Drawable background) {
        this.mButton.setBackground(background);
    }

    public void setBackgroundResource(int background) {
        this.mButton.setBackgroundResource(background);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }
}
