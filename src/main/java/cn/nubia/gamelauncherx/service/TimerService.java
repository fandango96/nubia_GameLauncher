package cn.nubia.gamelauncherx.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.Nullable;

import cn.nubia.gamelauncherx.util.TimerServiceUtil;
import java.util.Date;

public class TimerService extends Service {
    private static final int ACTION_WRITE_DATA = 100;
    private static final String DATE_KEY = "date";
    private static final String SHARED_PERFERENCES_FILES = "game_launcher_timer";
    private static final String TIMER_KEY = "timer";
    private static final int VALUE_FOUR_HOURS = 240;
    private static final int VALUE_THREE_HOURS = 180;
    private static final int VALUE_TWO_HOURS = 120;
    private Callbacks mCallbacks;
    /* access modifiers changed from: private */
    public Date mDate;
    /* access modifiers changed from: private */
    public int mTimer = 0;
    private WorkHandler mWorkHander;
    private HandlerThread mWorkThread;

    public interface Callbacks {
        void notifyTimeRange(Status status);

        void updateTimerView(int i);
    }

    class ServiceBinder extends Binder {
        ServiceBinder() {
        }

        public TimerService getService() {
            return TimerService.this;
        }
    }

    public enum Status {
        NOMAL,
        MILD,
        MODERATE,
        SEVERE
    }

    class WorkHandler extends Handler {
        public WorkHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100:
                    TimerServiceUtil.writeDataTosharedPrefs(TimerService.this, TimerService.this.mDate.getTime());
                    TimerServiceUtil.writeTimerTosharedPrefs(TimerService.this, TimerService.this.mTimer);
                    return;
                default:
                    return;
            }
        }
    }

    public void onCreate() {
        super.onCreate();
        this.mWorkThread = new HandlerThread("GTimer-worker");
        this.mWorkThread.start();
        this.mWorkHander = new WorkHandler(this.mWorkThread.getLooper());
        initDatas();
        TimerServiceStatus.getInstance().setServiceStatus(this, true);
    }

    public void onDestroy() {
        super.onDestroy();
        TimerServiceStatus.getInstance().setServiceStatus(this, false);
        this.mWorkHander.removeMessages(100);
        this.mWorkHander = null;
        this.mWorkThread.quitSafely();
    }

    @Nullable
    public IBinder onBind(Intent intent) {
        return new ServiceBinder();
    }

    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    public void holdDatas() {
        this.mWorkHander.sendEmptyMessage(100);
    }

    public void updateTimer() {
        if (TimerServiceUtil.isUpdateDate(this)) {
            resetDatas();
            updateViews(this.mTimer);
            return;
        }
        this.mTimer++;
        updateViews(this.mTimer);
    }

    public void setCallbacks(Callbacks callbacks) {
        this.mCallbacks = callbacks;
    }

    public int getTime() {
        return this.mTimer;
    }

    private void initDatas() {
        this.mDate = TimerServiceUtil.readDataTosharedPrefs(this);
        this.mTimer = TimerServiceUtil.readTimerTosharedPrefs(this);
        if (this.mDate == null || this.mTimer == -1) {
            resetDatas();
        }
    }

    private boolean resetDatas() {
        this.mTimer = 0;
        this.mDate = new Date();
        holdDatas();
        setTimeOut4HValue();
        return true;
    }

    public void setTimeOut4HValue() {
        Editor editor = getSharedPreferences("data", 0).edit();
        editor.putBoolean(TimerServiceUtil.TIME_OUT_4H, true);
        editor.apply();
    }

    private void updateViews(int time) {
        holdDatas();
        if (this.mCallbacks != null) {
            this.mCallbacks.updateTimerView(this.mTimer);
            calculateTimeRange(time);
        }
    }

    private void calculateTimeRange(int time) {
        if (time > VALUE_FOUR_HOURS) {
            this.mCallbacks.notifyTimeRange(Status.SEVERE);
        } else if (time < VALUE_TWO_HOURS) {
            this.mCallbacks.notifyTimeRange(Status.NOMAL);
        } else if (time > VALUE_TWO_HOURS && time < VALUE_THREE_HOURS) {
            this.mCallbacks.notifyTimeRange(Status.MILD);
        } else if (time > VALUE_THREE_HOURS && time < VALUE_FOUR_HOURS) {
            this.mCallbacks.notifyTimeRange(Status.MODERATE);
        }
    }
}
