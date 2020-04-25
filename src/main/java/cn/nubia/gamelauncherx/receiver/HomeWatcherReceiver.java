package cn.nubia.gamelauncherx.receiver;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import cn.nubia.gamelauncherx.util.LogUtil;
import java.util.List;

public class HomeWatcherReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(intent.getAction())) {
            if ("homekey".equals(intent.getStringExtra("reason")) && isForeground(context, context.getClass().getName())) {
                LogUtil.d("HomeWatcher", "--->onReceive() homekey !");
            }
        }
    }

    public boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }
        List<RunningTaskInfo> list = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1);
        if (list == null || list.size() <= 0) {
            return false;
        }
        return className.equals(((RunningTaskInfo) list.get(0)).topActivity.getClassName());
    }
}
