package cn.nubia.commonui.widget;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.nubia.commonui.R;

public abstract class NubiaDialogActivity extends Activity {
    private TextView mCancel;
    private ViewGroup mContentView;
    private int mLandThemeId = R.style.Theme_Nubia_Dialog_DialogActivity;
    /* access modifiers changed from: private */
    public OnButtonClickListener mListener;
    private TextView mOk;
    private int mPortThemeId = R.style.Theme_Nubia_NoActionBar_DialogActivity;
    private ScreenInfo mScreenInfo;
    private int mStatusBarHeight;

    public interface OnButtonClickListener {
        void OnNegativeButtonClick();

        void OnPositiveButtonClick();
    }

    private class ScreenInfo {
        private boolean mIsLandscape;
        private int mScreenHeight;
        private int mScreenWidth;

        public ScreenInfo(Context context) {
            DisplayMetrics metric = new DisplayMetrics();
            ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getMetrics(metric);
            this.mIsLandscape = NubiaDialogActivity.this.getResources().getConfiguration().orientation == 2;
            if (NubiaDialogActivity.this.getRequestedOrientation() == 1) {
                this.mIsLandscape = false;
            }
            this.mScreenWidth = this.mIsLandscape ? metric.heightPixels : metric.widthPixels;
            this.mScreenHeight = this.mIsLandscape ? metric.widthPixels : metric.heightPixels;
        }

        /* access modifiers changed from: private */
        public boolean isLandscape() {
            return this.mIsLandscape;
        }

        /* access modifiers changed from: private */
        public int getScreenWidth() {
            return this.mScreenWidth;
        }

        private int getScreenHeight() {
            return this.mScreenHeight;
        }
    }

    public void setOnButtonClickListener(OnButtonClickListener listener) {
        this.mListener = listener;
    }

    public void setThemes(int landThemeId, int portThemeId) {
        this.mLandThemeId = landThemeId;
        this.mPortThemeId = portThemeId;
    }

    public void setNegativeButtonEnabled(Boolean isDisabled) {
        if (this.mOk == null) {
            this.mOk = (TextView) findViewById(R.id.nubia_dialog_activity_ok);
            this.mOk.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (NubiaDialogActivity.this.mListener != null) {
                        NubiaDialogActivity.this.mListener.OnNegativeButtonClick();
                    }
                }
            });
        }
        this.mOk.setEnabled(isDisabled.booleanValue());
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDialogActivityTheme();
        initDialogView();
        setStatusBarHeight();
        prepareContentChange();
    }

    private void setStatusBarHeight() {
        int statusResId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (statusResId > 0) {
            this.mStatusBarHeight = getResources().getDimensionPixelSize(statusResId);
        }
    }

    private void setDialogActivityTheme() {
        boolean isLand = getResources().getConfiguration().orientation == 2;
        if (getRequestedOrientation() == 1) {
            setTheme(this.mPortThemeId);
        } else if (isLand) {
            setTheme(this.mLandThemeId);
        } else {
            setTheme(this.mPortThemeId);
        }
    }

    private void prepareContentChange() {
        resetScreenInfo(this);
        adjustLayout();
    }

    public void setTheme(int resid) {
        super.setTheme(resid);
    }

    public void setContentView(int layoutResID) {
        setContentInnerView(layoutResID, null, null);
    }

    public void setContentView(View view) {
        setContentInnerView(0, view, null);
    }

    public void setContentView(View view, LayoutParams params) {
        setContentInnerView(0, view, params);
    }

    public void addContentView(View view, LayoutParams params) {
    }

    private void initDialogView() {
        super.setContentView(R.layout.nubia_dialog_activity_layout);
        this.mContentView = (ViewGroup) findViewById(R.id.nubia_dialog_activity_content);
        this.mCancel = (TextView) findViewById(R.id.nubia_dialog_activity_cancel);
        this.mCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (NubiaDialogActivity.this.mListener != null) {
                    NubiaDialogActivity.this.mListener.OnPositiveButtonClick();
                }
            }
        });
        this.mOk = (TextView) findViewById(R.id.nubia_dialog_activity_ok);
        this.mOk.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (NubiaDialogActivity.this.mListener != null) {
                    NubiaDialogActivity.this.mListener.OnNegativeButtonClick();
                }
            }
        });
    }

    private void setContentInnerView(int layoutResID, View view, LayoutParams params) {
        if (this.mContentView != null) {
            if (layoutResID != 0 && view == null) {
                view = getLayoutInflater().inflate(layoutResID, null);
            }
            this.mContentView.removeAllViews();
            if (params != null) {
                this.mContentView.addView(view, params);
            } else {
                this.mContentView.addView(view);
            }
        }
    }

    public ActionBar getActionBar() {
        return null;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void finish() {
        super.finish();
        super.overridePendingTransition(R.anim.nubia_dialog_activity_closed_anim_in, R.anim.nubia_dialog_activity_closed_anim_out);
    }

    public void setTitle(CharSequence title) {
        TextView titleView = (TextView) findViewById(R.id.nubia_dialog_activity_title);
        if (titleView != null) {
            titleView.setText(title);
        }
    }

    private void adjustLayout() {
        int i = -1;
        LinearLayout panel = (LinearLayout) findViewById(R.id.nubia_dialog_activity_panel);
        LinearLayout.LayoutParams params = null;
        if (panel != null) {
            params = (LinearLayout.LayoutParams) panel.getLayoutParams();
        }
        boolean bIsLandscape = this.mScreenInfo.isLandscape();
        if (params != null) {
            params.height = bIsLandscape ? this.mScreenInfo.getScreenWidth() - this.mStatusBarHeight : -1;
            if (bIsLandscape) {
                i = this.mScreenInfo.getScreenWidth();
            }
            params.width = i;
        }
        panel.requestLayout();
    }

    private void resetScreenInfo(Context context) {
        this.mScreenInfo = new ScreenInfo(context);
    }
}
