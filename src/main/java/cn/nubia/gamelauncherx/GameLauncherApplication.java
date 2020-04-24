package cn.nubia.gamelauncherx;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import cn.nubia.gamelauncherx.aimhelper.NubiaGameTrackManager;
import cn.nubia.gamelauncherx.commoninterface.ConstantVariable;
import cn.nubia.gamelauncherx.model.AppAddModel;
import cn.nubia.gamelauncherx.service.TimerServiceStatus;
import cn.nubia.gamelauncherx.test.TestDataReceiver;
import cn.nubia.gamelauncherx.util.CommonUtil;
import cn.nubia.gamelauncherx.util.LogUtil;
import cn.nubia.gamelauncherx.util.NubiaTrackManager;

public class GameLauncherApplication extends Application {
    public static Context CONTEXT = null;
    private static final String TAG = "GameLauncherApplication";
    public static ScreenStatusReceiver mScreenStatusReceiver;
    static TimerReceiver receiver;
    TestDataReceiver testDataReceiver;

    public void onCreate() {
        super.onCreate();
        Log.i("lsm", "GameLauncherApplication onCreate begin");
        initHasPermission();
        AppAddModel instance = AppAddModel.getInstance();
        Context applicationContext = getApplicationContext();
        CONTEXT = applicationContext;
        instance.init(applicationContext);
        resiterReceive();
        NubiaTrackManager.getInstance().init(this);
        Log.i("lsm", "GameLauncherApplication onCreate end");
        NubiaGameTrackManager.getInstance();
        NubiaGameTrackManager.init(this);
    }

    /* access modifiers changed from: 0000 */
    public void resiterReceive() {
        new AsyncTask<Void, Void, Void>() {
            /* access modifiers changed from: protected */
            public Void doInBackground(Void... voids) {
                if (!ActivityManager.isUserAMonkey()) {
                    GameLauncherApplication.receiver = new TimerReceiver();
                    IntentFilter filter = new IntentFilter();
                    filter.addAction("android.intent.action.TIME_TICK");
                    GameLauncherApplication.this.registerReceiver(GameLauncherApplication.receiver, filter);
                }
                GameLauncherApplication.this.testDataReceiver = new TestDataReceiver();
                IntentFilter filter1 = new IntentFilter();
                filter1.addAction(TestDataReceiver.TEST_ACTION);
                GameLauncherApplication.this.registerReceiver(GameLauncherApplication.this.testDataReceiver, filter1);
                GameLauncherApplication.mScreenStatusReceiver = new ScreenStatusReceiver();
                IntentFilter screenFilter = new IntentFilter();
                screenFilter.addAction("android.intent.action.SCREEN_ON");
                screenFilter.addAction("android.intent.action.SCREEN_OFF");
                screenFilter.addAction("android.intent.action.USER_PRESENT");
                GameLauncherApplication.this.registerReceiver(GameLauncherApplication.mScreenStatusReceiver, screenFilter);
                return null;
            }
        }.execute(new Void[0]);
    }

    public void onTerminate() {
        super.onTerminate();
        TimerServiceStatus.getInstance().stopService(this);
        if (!ActivityManager.isUserAMonkey()) {
            try {
                if (receiver != null) {
                    unregisterReceiver(receiver);
                    receiver = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (this.testDataReceiver != null) {
            unregisterReceiver(this.testDataReceiver);
            this.testDataReceiver = null;
        }
        if (mScreenStatusReceiver != null) {
            unregisterReceiver(mScreenStatusReceiver);
            mScreenStatusReceiver = null;
        }
        AppAddModel.getInstance().end();
    }

    /* access modifiers changed from: 0000 */
    public void initHasPermission() {
        SharedPreferences sh = getSharedPreferences("data", 0);
        LogUtil.d(TAG, " boolean = " + sh.getBoolean("has_permission", false));
        if (sh.getBoolean("has_permission", false) && !CommonUtil.isInternalVersion()) {
            ConstantVariable.HAS_PERMISSION = true;
        }
    }
}
