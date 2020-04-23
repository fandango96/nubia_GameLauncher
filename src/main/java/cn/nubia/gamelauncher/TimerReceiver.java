package cn.nubia.gamelauncher;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import cn.nubia.gamelauncher.service.TimerService;
import cn.nubia.gamelauncher.service.TimerServiceStatus;

public class TimerReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (ActivityManager.isUserAMonkey()) {
            try {
                if (GameLauncherApplication.receiver != null) {
                    context.unregisterReceiver(GameLauncherApplication.receiver);
                    GameLauncherApplication.receiver = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i("GameSpace", "ActivityManager.isUserAMonkey == true unregisterReceiver time tick");
            return;
        }
        if ("android.intent.action.TIME_TICK".equals(intent.getAction()) && TimerServiceStatus.getInstance().isServiceStarted()) {
            TimerService timerService = TimerServiceStatus.getInstance().getTimerService(context);
            if (timerService != null) {
                timerService.updateTimer();
            }
        }
    }
}
