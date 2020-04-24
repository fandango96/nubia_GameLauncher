package cn.nubia.gamelauncherx.util;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;

public class WorkThread {
    private static Handler sHandler;
    private static HandlerThread sWorkThread;

    static {
        sWorkThread = null;
        sHandler = null;
        sWorkThread = new HandlerThread("game-add");
        sWorkThread.start();
        sHandler = new Handler(sWorkThread.getLooper());
    }

    public static void runOnWorkThread(Runnable runnable) {
        if (Process.myTid() == sWorkThread.getThreadId()) {
            runnable.run();
        } else {
            sHandler.post(runnable);
        }
    }

    public static Handler getHandler() {
        return sHandler;
    }
}
