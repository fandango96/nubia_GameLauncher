package cn.nubia.gamelauncher.gamehandle;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings.Global;
import cn.nubia.gamelauncher.aimhelper.AimService;
import cn.nubia.gamelauncher.util.BuildDeviceUtil;
import cn.nubia.gamelauncher.util.CommonUtil;
import cn.nubia.gamelauncher.util.LogUtil;

public class BluetoothReceiver extends BroadcastReceiver {
    private static final String TAG = "BluetoothReceiver";

    public void onReceive(Context context, Intent intent) {
        try {
            intent.setComponent(new ComponentName(context, GameHandleService.class));
            if (!CommonUtil.isInternalVersion() || !CommonUtil.isRedMagicPhone()) {
                context.startService(intent);
                try {
                    if (BuildDeviceUtil.isAndroidQ() && !CommonUtil.isInternalVersion() && CommonUtil.isRedMagicPhone()) {
                        if (intent.getBooleanExtra(GameHandleConstant.GAME_MODE_EXTRA_ISRUNNING, false)) {
                            intent.setAction(AimService.ACTION_GAMEMODE_CHANGE);
                            intent.setComponent(new ComponentName(context, AimService.class));
                            context.startService(intent);
                            return;
                        }
                        Global.putInt(context.getContentResolver(), "game_mode_floating_window_show", 0);
                    }
                } catch (Exception e) {
                    LogUtil.w(TAG, "start MyService error, " + e.getMessage());
                }
            } else {
                context.startForegroundService(intent);
                if (BuildDeviceUtil.isAndroidQ()) {
                }
            }
        } catch (Exception e2) {
            LogUtil.d(TAG, e2.toString());
        }
    }
}
