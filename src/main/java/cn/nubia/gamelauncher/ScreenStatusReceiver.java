package cn.nubia.gamelauncher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cn.nubia.gamelauncher.activity.GameSpaceActivity;
import cn.nubia.gamelauncher.service.TimerServiceStatus;
import cn.nubia.gamelauncher.util.CommonUtil;

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
