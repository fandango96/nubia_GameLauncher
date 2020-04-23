package cn.nubia.gamelauncher.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import cn.nubia.gamelauncher.util.WorkThread;

public class TestDataReceiver extends BroadcastReceiver {
    public static String TEST_ACTION = "cn.nubia.gamelauncher.datatest";

    public void onReceive(Context context, Intent intent) {
        if (TEST_ACTION.equals(intent.getAction())) {
            WorkThread.runOnWorkThread(new Runnable() {
                public void run() {
                    Log.i("lsm", "TestDataReceiver exportAppDbFile");
                    new CopyTestDataUtil().exportAppDbFile();
                }
            });
        }
    }
}
