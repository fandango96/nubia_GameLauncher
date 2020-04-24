package cn.nubia.gamelauncherx.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import cn.nubia.gamelauncherx.util.GameKeysConstant;
import cn.nubia.gamelauncherx.util.NubiaTrackManager;

public class BuryReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null || intent.getAction().equals("cn.nubia.owlsystem.firstbootdayaction")) {
            SharedPreferences mSharePref = context.getSharedPreferences(GameKeysConstant.IS_FIRST_DIALOG_NAME, 0);
            String mListOrCard = mSharePref.getString(GameKeysConstant.LIST_OR_CARD, "Card");
            int mAppNumber = mSharePref.getInt(GameKeysConstant.APPS_NUMBER, 0);
            Bundle cv = new Bundle();
            cv.putString("package_name", "cn.nubia.gamelauncher");
            cv.putString("event_name", "gamespace_view_switching_status");
            cv.putString("action_type", "option game_number");
            cv.putString("action_value", mListOrCard + " " + mAppNumber);
            cv.putInt("report_interval", 1);
            NubiaTrackManager.getInstance().sendEvent("cn.nubia.gamelauncher", cv);
            String mLedStatus = mSharePref.getString(GameKeysConstant.LED_STATUS, "开");
            Bundle cv2 = new Bundle();
            cv2.putString("package_name", "cn.nubia.gamelauncher");
            cv2.putString("event_name", "gamespace_athletic_atmosphere_light");
            cv2.putString("action_type", "switch_on");
            cv2.putString("action_value", mLedStatus);
            cv2.putInt("report_interval", 1);
            NubiaTrackManager.getInstance().sendEvent("cn.nubia.gamelauncher", cv2);
            String mFanStatus = mSharePref.getString(GameKeysConstant.FAN_STATUS, "关");
            Bundle cv3 = new Bundle();
            cv3.putString("package_name", "cn.nubia.gamelauncher");
            cv3.putString("event_name", "gamespace_cooling_fan_switch");
            cv3.putString("action_type", "switch_status");
            cv3.putString("action_value", mFanStatus);
            cv3.putInt("report_interval", 1);
            NubiaTrackManager.getInstance().sendEvent("cn.nubia.gamelauncher", cv3);
        }
    }
}
