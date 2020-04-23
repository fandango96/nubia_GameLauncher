package cn.nubia.gamelauncher.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import cn.nubia.gamelauncher.service.TimerService.Callbacks;
import java.lang.ref.WeakReference;

public class TimerServiceStatus {
    private static TimerServiceStatus mInstance;
    private ServiceConnection cnn = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            TimerServiceStatus.this.mTimerService = ((ServiceBinder) service).getService();
            if (TimerServiceStatus.this.mCallbacks != null && TimerServiceStatus.this.mCallbacks.get() != null) {
                TimerServiceStatus.this.mTimerService.setCallbacks((Callbacks) TimerServiceStatus.this.mCallbacks.get());
                ((Callbacks) TimerServiceStatus.this.mCallbacks.get()).updateTimerView(TimerServiceStatus.this.mTimerService.getTime());
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            TimerServiceStatus.this.mTimerService = null;
        }
    };
    private boolean isServiceStarted = false;
    /* access modifiers changed from: private */
    public WeakReference<Callbacks> mCallbacks;
    /* access modifiers changed from: private */
    public TimerService mTimerService;

    public static TimerServiceStatus getInstance() {
        if (mInstance == null) {
            mInstance = new TimerServiceStatus();
        }
        return mInstance;
    }

    public void setServiceStatus(Context context, boolean isStared) {
        if (isStared) {
            this.isServiceStarted = context.getApplicationContext().bindService(new Intent(context, TimerService.class), this.cnn, 1);
        } else {
            this.isServiceStarted = false;
        }
    }

    public boolean isServiceStarted() {
        return this.isServiceStarted;
    }

    public TimerService getTimerService(Context context) {
        if (this.mTimerService == null) {
            Context appContext = context.getApplicationContext();
            appContext.bindService(new Intent(appContext, TimerService.class), this.cnn, 1);
        }
        return this.mTimerService;
    }

    public void setActivity(Callbacks callbacks) {
        if (this.mTimerService != null) {
            this.mTimerService.setCallbacks(callbacks);
        }
    }

    public void setCallbacks(Callbacks callbacks) {
        this.mCallbacks = new WeakReference<>(callbacks);
        if (this.mTimerService != null && this.mCallbacks != null && this.mCallbacks.get() != null) {
            this.mTimerService.setCallbacks((Callbacks) this.mCallbacks.get());
        }
    }

    public void stopService(Context context) {
        Context appContext = context.getApplicationContext();
        if (this.isServiceStarted) {
            appContext.unbindService(this.cnn);
        }
        appContext.stopService(new Intent(appContext, TimerService.class));
    }

    public void startService(Context context) {
        Context appContext = context.getApplicationContext();
        appContext.startService(new Intent(appContext, TimerService.class));
    }
}
