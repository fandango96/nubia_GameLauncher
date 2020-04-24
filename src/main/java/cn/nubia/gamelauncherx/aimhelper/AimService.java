package cn.nubia.gamelauncherx.aimhelper;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.IBinder;
import cn.nubia.gamelauncherx.bean.AppListItemBean;
import cn.nubia.gamelauncherx.commoninterface.ConstantVariable;
import cn.nubia.gamelauncherx.commoninterface.IGetAppStatusDataCallBack;
import cn.nubia.gamelauncherx.gamehandle.GameHandleConstant;
import cn.nubia.gamelauncherx.model.AppAddModel;
import java.util.ArrayList;
import java.util.List;

public class AimService extends Service implements IGetAppStatusDataCallBack {
    public static final String ACTION_GAMEMODE_CHANGE = "cn.nubia.gamelauncher.action.GAMEMODE_CHANGE";
    private static final String ACTION_START_AIM_HELPER = "cn.nubia.gamelauncher.action.START_HELPER";
    /* access modifiers changed from: private */
    public static final String TAG = AimService.class.getSimpleName();
    private ContentObserver mAppDbObserver;
    private GameHelperController mGameHelperController = new GameHelperController();
    private int onCreateCount = 0;

    private class AppDbObserver extends ContentObserver {
        public AppDbObserver(Handler handler) {
            super(handler);
        }

        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            LogUtil.d(AimService.TAG, "AppDbObserver onChange");
            AimService.this.syncGameList();
        }
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        String str = TAG;
        StringBuilder append = new StringBuilder().append("AimService onCreate ");
        int i = this.onCreateCount + 1;
        this.onCreateCount = i;
        LogUtil.d(str, append.append(i).toString());
        super.onCreate();
        AppAddModel.getInstance().resisterGetAppStatusDataCallBack(this);
        syncGameList();
        this.mAppDbObserver = new AppDbObserver(new Handler());
        getContentResolver().registerContentObserver(ConstantVariable.APPADD_URI, false, this.mAppDbObserver);
        this.mGameHelperController.onCreate(this);
    }

    /* access modifiers changed from: private */
    @TargetApi(11)
    public void syncGameList() {
        List<AppListItemBean> beanList = AppAddModel.getInstance().getAppAddedList();
        if (beanList != null) {
            GameWhiteList.syncPackages(beanList);
        } else {
            LogUtil.i(TAG, "syncGameList empty");
        }
    }

    public void onDestroy() {
        String str = TAG;
        StringBuilder append = new StringBuilder().append("AimService onDestroy ");
        int i = this.onCreateCount - 1;
        this.onCreateCount = i;
        LogUtil.d(str, append.append(i).toString());
        AppAddModel.getInstance().unResisterGetAppStatusDataCallBack(this);
        getContentResolver().unregisterContentObserver(this.mAppDbObserver);
        this.mGameHelperController.onDestroy();
        super.onDestroy();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent != null ? intent.getAction() : "";
        LogUtil.d(TAG, "onStartCommand action " + action);
        if (action == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        if (ACTION_START_AIM_HELPER.equals(action)) {
            this.mGameHelperController.handleStart();
        } else if (ACTION_GAMEMODE_CHANGE.equals(action)) {
            boolean isGameMode = intent.getBooleanExtra(GameHandleConstant.GAME_MODE_EXTRA_ISRUNNING, false);
            this.mGameHelperController.handleGameModeChange(isGameMode);
            if (!isGameMode) {
                stopSelf();
            }
        } else if (action.startsWith(GameHelperController.ACTION_CLOSE_AIM_HELPER)) {
            this.mGameHelperController.handleDelayCloseAimHelperAlarm(intent.getStringExtra("package"));
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void onLoadAddAppListDone(ArrayList<AppListItemBean> list, int hasAddCount) {
        LogUtil.d(TAG, "onLoadAddAppListDone");
        if (list != null) {
            GameWhiteList.syncPackages(list);
            this.mGameHelperController.onAppListloaded();
        }
    }
}
