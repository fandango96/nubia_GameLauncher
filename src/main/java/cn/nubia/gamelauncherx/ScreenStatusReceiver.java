package cn.nubia.gamelauncherx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cn.nubia.gamelauncherx.activity.GameSpaceActivity;
import cn.nubia.gamelauncherx.service.TimerServiceStatus;
import cn.nubia.gamelauncherx.util.CommonUtil;

public class ScreenStatusReceiver extends BroadcastReceiver {
    GameSpaceActivity mActivity = null;

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if ("android.intent.action.SCREEN_OFF".equals(action)) {
            if (!CommonUtil.isInternalVersion()) {
                TimerServiceStatus.getInstance().stopService(context);
            }
            if (this.mActivity != null) {
                this.mActivity.hideVideoViewDelayed(0);
            }
        } else if ("android.intent.action.USER_PRESENT".equals(action) && !CommonUtil.isInternalVersion()) {
            TimerServiceStatus.getInstance().startService(context);
        }
    }

    public void setActivity(GameSpaceActivity activity) {
        this.mActivity = activity;
    }
}
