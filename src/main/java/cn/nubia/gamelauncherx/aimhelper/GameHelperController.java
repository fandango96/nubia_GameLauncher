package cn.nubia.gamelauncherx.aimhelper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Handler;
import android.provider.Settings;
import android.provider.Settings.Global;
import android.text.TextUtils;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import cn.nubia.gamelauncherx.R;
import cn.nubia.gamelauncherx.util.ReflectUtilities;
import cn.nubia.gamelauncherx.util.ToastUtil;
import java.util.Iterator;
import java.util.Set;

public class GameHelperController implements TopActivityMonitor.TopActivityMonitorCallback
{
    public static final String ACTION_CLOSE_AIM_HELPER = "cn.nubia.gamelauncher.action.delay_close_aim_helper_for_package";
    private static final int ALARM_REQUEST_CODE_DELAY_CLOSE_AIM_HELPER = 1;
    private static final long CLOSE_AIM_HELPER_DELAY_MS = 20000;
    private static final String SETTING_KEY_FLOATING_WINDOW = "game_mode_floating_window_show";
    private static final String SETTING_KEY_PIP_PKG = "pip_pkg";
    private static final String SETTING_KEY_QQ_ICON = "nubia_pip_icon_com.tencent.mobileqq";
    private static final String SETTING_KEY_WX_ICON = "nubia_pip_icon_com.tencent.mm";
    private static final String TAG = "GameHelperController";
    private static int[] aimResArray = {R.mipmap.center1, R.mipmap.center2, R.mipmap.center3, R.mipmap.center4, R.mipmap.center5};
    /* access modifiers changed from: private */
    public boolean isOtherAppFloatingWindowShow = false;
    /* access modifiers changed from: private */
    public boolean isQQPipIconShowing = false;
    private boolean isShowingQuasiCenter = false;
    /* access modifiers changed from: private */
    public boolean isWeixinPipIconShowing = false;
    /* access modifiers changed from: private */
    public Context mContext;
    private FloatingWindowValueChangeObserver mFloatingWindowValueChangeObserver;
    private String mForegroundGameActivity;
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler();
    private boolean mIsGameMode = false;
    /* access modifiers changed from: private */
    public String mPipPkg = null;
    private PipPkgObserver mPipPkgObserver;
    private QQPipIconOvserver mQQPipIconObserver;
    private AimSettingFloatingWindow mSettingFloatingWindow;
    private TopActivityMonitor mTopActivityMonitor;
    private ImageView mView;
    private WeixinPipIconOvserver mWxPipIconObserver;
    private WindowManager windowManager;

    class FloatingWindowValueChangeObserver extends ContentObserver {
        public FloatingWindowValueChangeObserver(Handler handler) {
            super(handler);
        }

        public void onChange(boolean selfChange) {
            boolean z = false;
            super.onChange(selfChange);
            int value = Global.getInt(GameHelperController.this.mContext.getContentResolver(), GameHelperController.SETTING_KEY_FLOATING_WINDOW, 0);
            LogUtil.d(GameHelperController.TAG, "SettingValueChangeObserver onChange value = " + value);
            GameHelperController gameHelperController = GameHelperController.this;
            if (value != 0) {
                z = true;
            }
            gameHelperController.isOtherAppFloatingWindowShow = z;
            if (!GameHelperController.this.isOtherAppFloatingWindowShow) {
                LogUtil.d(GameHelperController.TAG, "delay refresh aim center");
                GameHelperController.this.mHandler.postDelayed(new Runnable() {
                    public void run() {
                        GameHelperController.this.refreshAimCenter();
                    }
                }, 100);
                return;
            }
            GameHelperController.this.refreshAimCenter();
        }
    }

    class PipPkgObserver extends ContentObserver {
        public PipPkgObserver(Handler handler) {
            super(handler);
        }

        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            String pkg = Global.getString(GameHelperController.this.mContext.getContentResolver(), GameHelperController.SETTING_KEY_PIP_PKG);
            LogUtil.d(GameHelperController.TAG, "pip_pkg=" + pkg);
            GameHelperController.this.mPipPkg = pkg;
            GameHelperController.this.refreshAimCenter();
            if (pkg != null && !"null".equals(pkg)) {
                GameHelperController.this.hideChoice();
            }
        }
    }

    class QQPipIconOvserver extends ContentObserver {
        public QQPipIconOvserver(Handler handler) {
            super(handler);
        }

        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            String value = Global.getString(GameHelperController.this.mContext.getContentResolver(), GameHelperController.SETTING_KEY_QQ_ICON);
            LogUtil.d(GameHelperController.TAG, "nubia_pip_icon_com.tencent.mobileqq = " + value);
            GameHelperController.this.isQQPipIconShowing = "true".equals(value);
            GameHelperController.this.refreshAimCenter();
        }
    }

    class WeixinPipIconOvserver extends ContentObserver {
        public WeixinPipIconOvserver(Handler handler) {
            super(handler);
        }

        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            String value = Global.getString(GameHelperController.this.mContext.getContentResolver(), GameHelperController.SETTING_KEY_WX_ICON);
            LogUtil.d(GameHelperController.TAG, "nubia_pip_icon_com.tencent.mm = " + value);
            GameHelperController.this.isWeixinPipIconShowing = "true".equals(value);
            GameHelperController.this.refreshAimCenter();
        }
    }

    private class WriteSettingTask extends AsyncTask<Integer, Void, Void> {
        private WriteSettingTask() {
        }

        /* access modifiers changed from: protected */
        public Void doInBackground(Integer... values) {
            try {
                Global.putInt(GameHelperController.this.mContext.getContentResolver(), "gamelauncher_helper", values[0].intValue());
                LogUtil.d(GameHelperController.TAG, "write setting gamelauncher_helper " + values[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void onCreate(Context context) {
        boolean z;
        boolean z2 = false;
        LogUtil.d(TAG, "GameHelperController onCreate");
        this.mContext = context;
        this.windowManager = (WindowManager) this.mContext.getSystemService(Context.WINDOW_SERVICE);
        int gameKeys = ReflectUtilities.getGameModeDBValue(this.mContext);
        if ((gameKeys & 1) != 0) {
            z = true;
        } else {
            z = false;
        }
        this.mIsGameMode = z;
        LogUtil.d(TAG, String.format("gameKeys:%d isGameMode:%b", new Object[]{Integer.valueOf(gameKeys), Boolean.valueOf(this.mIsGameMode)}));
        ContentResolver contentResolver = this.mContext.getContentResolver();
        if (Global.getInt(contentResolver, SETTING_KEY_FLOATING_WINDOW, 0) != 0) {
            z2 = true;
        }
        this.isOtherAppFloatingWindowShow = z2;
        this.mFloatingWindowValueChangeObserver = new FloatingWindowValueChangeObserver(this.mHandler);
        contentResolver.registerContentObserver(Global.getUriFor(SETTING_KEY_FLOATING_WINDOW), true, this.mFloatingWindowValueChangeObserver);
        String pipPkg = Global.getString(this.mContext.getContentResolver(), SETTING_KEY_PIP_PKG);
        LogUtil.d(TAG, "pip_pkg=" + pipPkg);
        this.mPipPkg = pipPkg;
        this.mPipPkgObserver = new PipPkgObserver(this.mHandler);
        contentResolver.registerContentObserver(Global.getUriFor(SETTING_KEY_PIP_PKG), true, this.mPipPkgObserver);
        this.mQQPipIconObserver = new QQPipIconOvserver(this.mHandler);
        this.isQQPipIconShowing = "true".equals(Global.getString(contentResolver, SETTING_KEY_QQ_ICON));
        contentResolver.registerContentObserver(Global.getUriFor(SETTING_KEY_QQ_ICON), true, this.mQQPipIconObserver);
        this.mWxPipIconObserver = new WeixinPipIconOvserver(this.mHandler);
        this.isWeixinPipIconShowing = "true".equals(Global.getString(contentResolver, SETTING_KEY_WX_ICON));
        contentResolver.registerContentObserver(Global.getUriFor(SETTING_KEY_WX_ICON), true, this.mWxPipIconObserver);
        this.mForegroundGameActivity = null;
        this.mTopActivityMonitor = new TopActivityMonitor(this);
        this.mTopActivityMonitor.start();
        this.mSettingFloatingWindow = new AimSettingFloatingWindow(context, this);
    }

    public void onDestroy() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mFloatingWindowValueChangeObserver);
        this.mContext.getContentResolver().unregisterContentObserver(this.mPipPkgObserver);
        this.mContext.getContentResolver().unregisterContentObserver(this.mQQPipIconObserver);
        this.mContext.getContentResolver().unregisterContentObserver(this.mWxPipIconObserver);
        if (this.mTopActivityMonitor != null) {
            this.mTopActivityMonitor.stop();
        }
        hideChoice();
        refreshAimCenter();
        LogUtil.d(TAG, "GameHelperController onDestroy");
    }

    public void handleStart() {
        int gameKeys = ReflectUtilities.getGameModeDBValue(this.mContext);
        this.mIsGameMode = (gameKeys & 1) != 0;
        LogUtil.d(TAG, String.format("handleStart gameKeys:%d isGameMode:%b", new Object[]{Integer.valueOf(gameKeys), Boolean.valueOf(this.mIsGameMode)}));
        if (enableOpenSetting()) {
            if (VERSION.SDK_INT >= 23) {
                LogUtil.d(TAG, "canDrawOverlays " + Settings.canDrawOverlays(this.mContext));
            }
            if (VERSION.SDK_INT < 23 || Settings.canDrawOverlays(this.mContext)) {
                showChoice();
                return;
            }
            Intent intent = new Intent();
            intent.setAction("android.settings.action.MANAGE_OVERLAY_PERMISSION");
            intent.setData(Uri.parse("package:" + this.mContext.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.mContext.startActivity(intent);
        }
    }

    public void handleGameModeChange(boolean isGameMode) {
        LogUtil.d(TAG, "handleGameModeChange isGameMode=" + isGameMode);
        this.mIsGameMode = isGameMode;
        refreshAimCenter();
        if (!isGameMode) {
            hideChoice();
            Global.putInt(this.mContext.getContentResolver(), SETTING_KEY_FLOATING_WINDOW, 0);
        }
    }

    public void handleDelayCloseAimHelperAlarm(String packageName) {
        AimConfigs.getInstance(this.mContext).setOn(packageName, false);
        LogUtil.d(TAG, "handleDelayCloseAimHelperAlarm close aim helper for package:" + packageName);
    }

    private void showChoice() {
        LogUtil.d(TAG, "showChoice");
        if (!(!this.isShowingQuasiCenter || this.windowManager == null || this.mView == null)) {
            this.isShowingQuasiCenter = false;
            this.windowManager.removeViewImmediate(this.mView);
        }
        this.mSettingFloatingWindow.show();
        refreshAimCenter();
    }

    /* access modifiers changed from: private */
    public void hideChoice() {
        this.mSettingFloatingWindow.hide();
    }

    private void createQuasiCenter() {
        if (this.mView == null) {
            this.mView = new ImageView(this.mContext);
        }
    }

    private LayoutParams makeQuasiCenterLayoutParams() {
        LayoutParams layoutParams = new LayoutParams();
        if (VERSION.SDK_INT >= 26) {
            layoutParams.type = 2038;
        } else {
            layoutParams.type = 2002;
        }
        layoutParams.format = 1;
        layoutParams.flags = 67108888;
        layoutParams.width = -2;
        layoutParams.height = -2;
        layoutParams.gravity = 17;
        return layoutParams;
    }

    public void onActivityChange(Set<String> activityStack) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (String cn2 : activityStack) {
            sb.append(cn2);
        }
        sb.append("]");
        String foregroundGameActivity = null;
        Iterator it = activityStack.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            String activity = (String) it.next();
            if (GameWhiteList.isSupportGame(activity.split("/")[0])) {
                foregroundGameActivity = activity;
                break;
            }
        }
        sb.append("   foreground=" + foregroundGameActivity);
        LogUtil.d(TAG, "activityStack=" + sb.toString());
        if (!TextUtils.equals(this.mForegroundGameActivity, foregroundGameActivity)) {
            if (foregroundGameActivity == null) {
                hideChoice();
                delayCloseAimHelperForPackage(getTopApplication());
            } else {
                changeSwitchState(AimConfigs.getInstance(this.mContext).isOn(foregroundGameActivity.split("/")[0]));
                if (TextUtils.equals(ActivityUtils.getPackageFromComponent(this.mForegroundGameActivity), ActivityUtils.getPackageFromComponent(foregroundGameActivity))) {
                    cancelDeleteCloseAimHelperForPackage(ActivityUtils.getPackageFromComponent(foregroundGameActivity));
                }
            }
            LogUtil.d(TAG, "topActivityChange " + this.mForegroundGameActivity + " ---> " + foregroundGameActivity);
            this.mForegroundGameActivity = foregroundGameActivity;
            refreshAimCenter();
            if (this.mSettingFloatingWindow.isShowing() && !GameWhiteList.isGameActivity(this.mForegroundGameActivity)) {
                this.mSettingFloatingWindow.hide();
            }
        }
    }

    private void delayCloseAimHelperForPackage(String packageName) {
        AimConfigs configs = AimConfigs.getInstance(this.mContext);
        if (!configs.isAuto(packageName) && configs.isOn(packageName)) {
            configs.setOn(packageName, false);
            LogUtil.d(TAG, "delayCloseAimHelperForPackage " + packageName);
        }
    }

    private void cancelDeleteCloseAimHelperForPackage(String packageName) {
        AlarmManager alarmManager = (AlarmManager) this.mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ACTION_CLOSE_AIM_HELPER + packageName);
        intent.setComponent(new ComponentName(this.mContext, AimService.class));
        intent.putExtra("package", packageName);
        alarmManager.cancel(PendingIntent.getService(this.mContext, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT));
        LogUtil.d(TAG, "cancelDeleteCloseAimHelperForPackage " + packageName);
    }

    public void onAppStop(String packageName) {
        if (GameWhiteList.isSupportGame(packageName)) {
            AimConfigs configs = AimConfigs.getInstance(this.mContext);
            LogUtil.d(TAG, "onAppStop pkg:" + packageName + " isOn:" + configs.isOn(packageName) + " isAuto:" + configs.isAuto(packageName));
            if (!configs.isAuto(packageName) && configs.isOn(packageName)) {
                configs.setOn(packageName, false);
                refreshAimCenter();
            }
            if (this.mForegroundGameActivity != null && this.mForegroundGameActivity.startsWith(packageName)) {
                this.mForegroundGameActivity = null;
                refreshAimCenter();
            }
        }
    }

    private void changeSwitchState(boolean open) {
        int i = 1;
        WriteSettingTask writeSettingTask = new WriteSettingTask();
        Integer[] numArr = new Integer[1];
        if (!open) {
            i = 0;
        }
        numArr[0] = Integer.valueOf(i);
        writeSettingTask.execute(numArr);
    }

    public void refreshAimCenter() {
        AimConfigs configs = AimConfigs.getInstance(this.mContext);
        String topPackage = getTopApplication();
        boolean hasPipWindow = true;
        if (TextUtils.isEmpty(this.mPipPkg) || "null".equals(this.mPipPkg)) {
            hasPipWindow = false;
        } else if (this.mPipPkg.equals("com.tencent.mm") && this.isWeixinPipIconShowing) {
            hasPipWindow = false;
        } else if (this.mPipPkg.equals("com.tencent.mobileqq") && this.isQQPipIconShowing) {
            hasPipWindow = false;
        }
        LogUtil.d(TAG, "mPipPkg=" + this.mPipPkg + " " + SETTING_KEY_QQ_ICON + "=" + this.isQQPipIconShowing + "  " + SETTING_KEY_WX_ICON + "=" + this.isWeixinPipIconShowing);
        boolean needShow = this.mIsGameMode && !hasPipWindow && configs.isOn(topPackage) && GameWhiteList.isGameActivity(this.mForegroundGameActivity) && (!this.isOtherAppFloatingWindowShow || this.mSettingFloatingWindow.isShowing());
        LogUtil.d(TAG, String.format("refreshAimCenter needShow:%b isGameMode:%b hasPipWindow:%b isOn:%b isOtherAppFloatingWindowShow:%b", new Object[]{Boolean.valueOf(needShow), Boolean.valueOf(this.mIsGameMode), Boolean.valueOf(hasPipWindow), Boolean.valueOf(configs.isOn(topPackage)), Boolean.valueOf(this.isOtherAppFloatingWindowShow)}));
        if (needShow) {
            int style = configs.getStyle(topPackage);
            int color = configs.getColor(topPackage);
            int scale = configs.getSize(topPackage);
            if (scale > 100 || scale < 40) {
                scale = 100;
            }
            float scalef = ((float) scale) / 100.0f;
            LogUtil.d(TAG, String.format("style=%d, color=%d, scale=%d", new Object[]{Integer.valueOf(style), Integer.valueOf(color), Integer.valueOf(scale)}));
            createQuasiCenter();
            if (style < 1 || style > 5) {
                style = 1;
            }
            this.mView.setImageResource(aimResArray[style - 1]);
            this.mView.setColorFilter(color);
            this.mView.setScaleX(scalef);
            this.mView.setScaleY(scalef);
            this.mView.setScaleType(ScaleType.CENTER);
            if (VERSION.SDK_INT >= 23 && Settings.canDrawOverlays(this.mContext) && !this.isShowingQuasiCenter) {
                LogUtil.d(TAG, "add aim view to window");
                this.windowManager.addView(this.mView, makeQuasiCenterLayoutParams());
                this.isShowingQuasiCenter = true;
                setControlCenterButtonState(true);
            }
        } else if (this.mView != null && this.isShowingQuasiCenter) {
            LogUtil.d(TAG, "remove aim view from window");
            this.windowManager.removeViewImmediate(this.mView);
            this.isShowingQuasiCenter = false;
            setControlCenterButtonState(false);
        }
    }

    private boolean enableOpenSetting() {
        boolean isOtherAppShowFloatingWindow;
        if (Global.getInt(this.mContext.getContentResolver(), SETTING_KEY_FLOATING_WINDOW, 0) != 0) {
            isOtherAppShowFloatingWindow = true;
        } else {
            isOtherAppShowFloatingWindow = false;
        }
        if (isOtherAppShowFloatingWindow) {
            ToastUtil.showGamemodeToast(this.mContext.getString(R.string.please_close_current_settings_window));
            LogUtil.d(TAG, "enableOpenSetting false isOtherAppShowFloatingWindow");
            return false;
        }
        String topPkg = ActivityUtils.getCurrentTopPkg();
        if (!GameWhiteList.isSupportGame(topPkg)) {
            ToastUtil.showGamemodeToast(this.mContext.getString(R.string.toast_unsupport_app));
            LogUtil.d(TAG, "enableOpenSetting false topPkg=" + topPkg);
            return false;
        } else if (this.mForegroundGameActivity == null) {
            ToastUtil.showGamemodeToast(this.mContext.getString(R.string.toast_unsupport_app));
            LogUtil.d(TAG, "enableOpenSetting false mForegroundGameActivity is null");
            return false;
        } else {
            int rotation = this.windowManager.getDefaultDisplay().getRotation();
            if (rotation == 1 || rotation == 3) {
                LogUtil.d(TAG, "enableOpenSetting isGameMode:" + this.mIsGameMode + " isOtherAppShowFloatingWindow:" + isOtherAppShowFloatingWindow + " mForegroundGameActivity=" + this.mForegroundGameActivity);
                return true;
            }
            ToastUtil.showGamemodeToast(this.mContext.getString(R.string.only_support_portrait_screen));
            LogUtil.d(TAG, "enableOpenSetting false is not portrait screen rotation=" + rotation);
            return false;
        }
    }

    public String getTopApplication() {
        if (!TextUtils.isEmpty(this.mForegroundGameActivity)) {
            return this.mForegroundGameActivity.split("/")[0];
        }
        return null;
    }

    private void setControlCenterButtonState(boolean highLight) {
        int i = 1;
        WriteSettingTask writeSettingTask = new WriteSettingTask();
        Integer[] numArr = new Integer[1];
        if (!highLight) {
            i = 0;
        }
        numArr[0] = Integer.valueOf(i);
        writeSettingTask.execute(numArr);
    }

    public boolean isGameMode() {
        return this.mIsGameMode;
    }

    public void onAppListloaded() {
        this.mTopActivityMonitor.notifyTopChange();
    }
}
