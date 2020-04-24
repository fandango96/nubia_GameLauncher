package cn.nubia.gamelauncherx.aimhelper;

import android.content.Context;
import android.os.Build.VERSION;
import android.provider.Settings.Global;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import cn.nubia.gamelauncherx.R;

public class AimSettingFloatingWindow {
    private static final String TAG = AimSettingFloatingWindow.class.getSimpleName();
    private boolean isShowing = false;
    private ImageButton mBtnClose;
    private View mContentView;
    private Context mContext;
    private GameHelperController mGameHelperController;
    private RadioButton mRbOff;
    private RadioButton mRbOn;
    private RadioGroup mRgOnOff;
    private AimSettingView mSettingView;
    private WindowManager mWindowManager;

    public AimSettingFloatingWindow(Context context, GameHelperController gameHelperController) {
        this.mContext = context;
        this.mWindowManager = (WindowManager) context.getSystemService("window");
        this.mGameHelperController = gameHelperController;
    }

    private void initViews() {
        this.mContentView = LayoutInflater.from(this.mContext).inflate(R.layout.aim_setting_layout, null);
        this.mRgOnOff = (RadioGroup) this.mContentView.findViewById(R.id.radiogroup_on_off);
        this.mRbOn = (RadioButton) this.mContentView.findViewById(R.id.rb_on);
        this.mRbOff = (RadioButton) this.mContentView.findViewById(R.id.rb_off);
        this.mRgOnOff.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                AimSettingFloatingWindow.this.onSwitchOpen(checkedId == R.id.rb_on);
            }
        });
        this.mBtnClose = (ImageButton) this.mContentView.findViewById(R.id.btn_close);
        this.mBtnClose.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                AimSettingFloatingWindow.this.hide();
            }
        });
        this.mSettingView = (AimSettingView) this.mContentView.findViewById(R.id.aim_setting_view);
        this.mSettingView.setGameHelperController(this.mGameHelperController);
    }

    private LayoutParams makeParams(boolean expand) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        if (VERSION.SDK_INT >= 26) {
            layoutParams.type = 2038;
        } else {
            layoutParams.type = 2002;
        }
        layoutParams.format = 1;
        layoutParams.flags = 67108906;
        layoutParams.width = this.mContext.getResources().getDimensionPixelSize(R.dimen.aim_setting_layout_width);
        layoutParams.height = this.mContext.getResources().getDimensionPixelSize(expand ? R.dimen.aim_setting_layout_expand_height : R.dimen.aim_setting_layout_narrow_height);
        layoutParams.gravity = 53;
        layoutParams.dimAmount = 0.66f;
        layoutParams.y = this.mContext.getResources().getDimensionPixelSize(R.dimen.top_margin);
        layoutParams.x = this.mContext.getResources().getDimensionPixelSize(R.dimen.right_margin);
        return layoutParams;
    }

    public void show() {
        LogUtil.d(TAG, "show isShowing:" + this.isShowing);
        if (!this.isShowing) {
            initViews();
            boolean isOn = AimConfigs.getInstance(this.mContext).isOn(this.mGameHelperController.getTopApplication());
            LogUtil.d(TAG, "show isOn " + isOn);
            this.mRgOnOff.setOnCheckedChangeListener(null);
            if (isOn) {
                this.mRbOn.setChecked(true);
            } else {
                this.mRbOff.setChecked(true);
            }
            this.mRgOnOff.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    AimSettingFloatingWindow.this.onSwitchOpen(checkedId == R.id.rb_on);
                }
            });
            this.mContentView.setBackgroundResource(isOn ? R.mipmap.window_on_bg : R.mipmap.window_off_bg);
            this.mSettingView.setVisibility(isOn ? 0 : 8);
            this.mSettingView.refreshUI();
            this.mWindowManager.addView(this.mContentView, makeParams(isOn));
            this.isShowing = true;
            if (this.mGameHelperController.isGameMode()) {
                Global.putInt(this.mContext.getContentResolver(), "game_mode_floating_window_show", 1);
            }
        }
    }

    public void hide() {
        if (this.isShowing) {
            this.mWindowManager.removeViewImmediate(this.mContentView);
            this.isShowing = false;
            if (this.mGameHelperController.isGameMode()) {
                Global.putInt(this.mContext.getContentResolver(), "game_mode_floating_window_show", 0);
            }
        }
        LogUtil.d(TAG, "hide choice view");
    }

    public boolean isShowing() {
        return this.isShowing;
    }

    /* access modifiers changed from: private */
    public void onSwitchOpen(boolean open) {
        AimConfigs.getInstance(this.mContext).setOn(this.mGameHelperController.getTopApplication(), open);
        this.mSettingView.setVisibility(open ? 0 : 8);
        if (open) {
            this.mRbOn.setChecked(true);
        } else {
            this.mRbOff.setChecked(true);
        }
        this.mContentView.setBackgroundResource(open ? R.mipmap.window_on_bg : R.mipmap.window_off_bg);
        this.mWindowManager.updateViewLayout(this.mContentView, makeParams(open));
        if (open) {
            this.mSettingView.refreshUI();
        }
        this.mGameHelperController.refreshAimCenter();
    }
}
