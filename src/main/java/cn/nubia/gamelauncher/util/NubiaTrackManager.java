package cn.nubia.gamelauncher.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;

public class NubiaTrackManager {
    private static final String TAG = "NubiaTrackManager";
    private static final long TIEMOUT = 3000;
    static TrackHandler sTrackHandler = null;
    static HandlerThread sTrackThread = null;
    /* access modifiers changed from: private */
    public boolean isConn;
    /* access modifiers changed from: private */
    public ServiceConnection mConn;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public Messenger mService;

    private static class SingleInstance {
        static NubiaTrackManager instance = new NubiaTrackManager();

        private SingleInstance() {
        }
    }

    final class TrackHandler extends Handler {
        static final int TRACK_EVENT_MSG = 1;
        static final int UNBIND_SERVICE_MSG = 2;

        public TrackHandler(Looper looper) {
            super(looper);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:45:0x0082, code lost:
            if (cn.nubia.gamelauncher.util.NubiaTrackManager.access$400(r4.this$0) == null) goto L_?;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:47:?, code lost:
            cn.nubia.gamelauncher.util.NubiaTrackManager.access$400(r4.this$0).unbindService(cn.nubia.gamelauncher.util.NubiaTrackManager.access$500(r4.this$0));
         */
        /* JADX WARNING: Code restructure failed: missing block: B:48:0x0095, code lost:
            r0 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:49:0x0096, code lost:
            r0.printStackTrace();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:56:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:57:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:58:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:59:?, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void handleMessage(android.os.Message r5) {
            /*
                r4 = this;
                int r1 = r5.what
                switch(r1) {
                    case 1: goto L_0x0009;
                    case 2: goto L_0x005f;
                    default: goto L_0x0005;
                }
            L_0x0005:
                super.handleMessage(r5)
            L_0x0008:
                return
            L_0x0009:
                boolean r1 = android.app.ActivityManager.isUserAMonkey()
                if (r1 != 0) goto L_0x0008
                android.os.HandlerThread r2 = cn.nubia.gamelauncher.util.NubiaTrackManager.sTrackThread     // Catch:{ RemoteException -> 0x0030, InterruptedException -> 0x005a }
                monitor-enter(r2)     // Catch:{ RemoteException -> 0x0030, InterruptedException -> 0x005a }
                cn.nubia.gamelauncher.util.NubiaTrackManager r1 = cn.nubia.gamelauncher.util.NubiaTrackManager.this     // Catch:{ all -> 0x002d }
                boolean r1 = r1.isConn     // Catch:{ all -> 0x002d }
                if (r1 == 0) goto L_0x0035
                cn.nubia.gamelauncher.util.NubiaTrackManager r1 = cn.nubia.gamelauncher.util.NubiaTrackManager.this     // Catch:{ all -> 0x002d }
                android.os.Messenger r1 = r1.mService     // Catch:{ all -> 0x002d }
                if (r1 == 0) goto L_0x0035
                cn.nubia.gamelauncher.util.NubiaTrackManager r1 = cn.nubia.gamelauncher.util.NubiaTrackManager.this     // Catch:{ all -> 0x002d }
                android.os.Messenger r1 = r1.mService     // Catch:{ all -> 0x002d }
                r1.send(r5)     // Catch:{ all -> 0x002d }
                monitor-exit(r2)     // Catch:{ all -> 0x002d }
                goto L_0x0008
            L_0x002d:
                r1 = move-exception
                monitor-exit(r2)     // Catch:{ all -> 0x002d }
                throw r1     // Catch:{ RemoteException -> 0x0030, InterruptedException -> 0x005a }
            L_0x0030:
                r0 = move-exception
                r0.printStackTrace()
                goto L_0x0008
            L_0x0035:
                cn.nubia.gamelauncher.util.NubiaTrackManager r1 = cn.nubia.gamelauncher.util.NubiaTrackManager.this     // Catch:{ all -> 0x002d }
                r1.bindServiceInvoked()     // Catch:{ all -> 0x002d }
                android.os.HandlerThread r1 = cn.nubia.gamelauncher.util.NubiaTrackManager.sTrackThread     // Catch:{ all -> 0x002d }
                r1.wait()     // Catch:{ all -> 0x002d }
                cn.nubia.gamelauncher.util.NubiaTrackManager r1 = cn.nubia.gamelauncher.util.NubiaTrackManager.this     // Catch:{ all -> 0x002d }
                boolean r1 = r1.isConn     // Catch:{ all -> 0x002d }
                if (r1 == 0) goto L_0x0058
                cn.nubia.gamelauncher.util.NubiaTrackManager r1 = cn.nubia.gamelauncher.util.NubiaTrackManager.this     // Catch:{ all -> 0x002d }
                android.os.Messenger r1 = r1.mService     // Catch:{ all -> 0x002d }
                if (r1 == 0) goto L_0x0058
                cn.nubia.gamelauncher.util.NubiaTrackManager r1 = cn.nubia.gamelauncher.util.NubiaTrackManager.this     // Catch:{ all -> 0x002d }
                android.os.Messenger r1 = r1.mService     // Catch:{ all -> 0x002d }
                r1.send(r5)     // Catch:{ all -> 0x002d }
            L_0x0058:
                monitor-exit(r2)     // Catch:{ all -> 0x002d }
                goto L_0x0008
            L_0x005a:
                r0 = move-exception
                r0.printStackTrace()
                goto L_0x0008
            L_0x005f:
                android.os.HandlerThread r2 = cn.nubia.gamelauncher.util.NubiaTrackManager.sTrackThread
                monitor-enter(r2)
                cn.nubia.gamelauncher.util.NubiaTrackManager r1 = cn.nubia.gamelauncher.util.NubiaTrackManager.this     // Catch:{ all -> 0x006c }
                boolean r1 = r1.isConn     // Catch:{ all -> 0x006c }
                if (r1 != 0) goto L_0x006f
                monitor-exit(r2)     // Catch:{ all -> 0x006c }
                goto L_0x0008
            L_0x006c:
                r1 = move-exception
                monitor-exit(r2)     // Catch:{ all -> 0x006c }
                throw r1
            L_0x006f:
                cn.nubia.gamelauncher.util.NubiaTrackManager r1 = cn.nubia.gamelauncher.util.NubiaTrackManager.this     // Catch:{ all -> 0x006c }
                r3 = 0
                r1.mService = r3     // Catch:{ all -> 0x006c }
                cn.nubia.gamelauncher.util.NubiaTrackManager r1 = cn.nubia.gamelauncher.util.NubiaTrackManager.this     // Catch:{ all -> 0x006c }
                r3 = 0
                r1.isConn = r3     // Catch:{ all -> 0x006c }
                monitor-exit(r2)     // Catch:{ all -> 0x006c }
                cn.nubia.gamelauncher.util.NubiaTrackManager r1 = cn.nubia.gamelauncher.util.NubiaTrackManager.this
                android.content.Context r1 = r1.mContext
                if (r1 == 0) goto L_0x0008
                cn.nubia.gamelauncher.util.NubiaTrackManager r1 = cn.nubia.gamelauncher.util.NubiaTrackManager.this     // Catch:{ Exception -> 0x0095 }
                android.content.Context r1 = r1.mContext     // Catch:{ Exception -> 0x0095 }
                cn.nubia.gamelauncher.util.NubiaTrackManager r2 = cn.nubia.gamelauncher.util.NubiaTrackManager.this     // Catch:{ Exception -> 0x0095 }
                android.content.ServiceConnection r2 = r2.mConn     // Catch:{ Exception -> 0x0095 }
                r1.unbindService(r2)     // Catch:{ Exception -> 0x0095 }
                goto L_0x0008
            L_0x0095:
                r0 = move-exception
                r0.printStackTrace()
                goto L_0x0008
            */
            throw new UnsupportedOperationException("Method not decompiled: cn.nubia.gamelauncher.util.NubiaTrackManager.TrackHandler.handleMessage(android.os.Message):void");
        }
    }

    private NubiaTrackManager() {
        this.mConn = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder service) {
                synchronized (NubiaTrackManager.sTrackThread) {
                    NubiaTrackManager.this.mService = new Messenger(service);
                    NubiaTrackManager.this.isConn = true;
                    NubiaTrackManager.sTrackThread.notify();
                }
            }

            public void onServiceDisconnected(ComponentName name) {
                synchronized (NubiaTrackManager.sTrackThread) {
                    NubiaTrackManager.this.mService = null;
                    NubiaTrackManager.this.isConn = false;
                    NubiaTrackManager.sTrackThread.notify();
                }
            }
        };
    }

    public static NubiaTrackManager getInstance() {
        return SingleInstance.instance;
    }

    /* access modifiers changed from: private */
    public void bindServiceInvoked() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("cn.nubia.owlsystem", "cn.nubia.applearning.datacollection.DataCollectionService"));
        if (this.mContext != null) {
            this.mContext.bindService(intent, this.mConn, 1);
        }
    }

    public void init(Context context) {
        this.mContext = context;
        if (sTrackHandler == null) {
            sTrackThread = new HandlerThread("NubiaTrackEvent", 10);
            sTrackThread.start();
            sTrackHandler = new TrackHandler(sTrackThread.getLooper());
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
        r4.mContext.unbindService(r4.mConn);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0022, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0023, code lost:
        r0.printStackTrace();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0013, code lost:
        if (r4.mContext == null) goto L_?;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void unbindServiceInvoked() {
        /*
            r4 = this;
            r3 = 0
            android.os.HandlerThread r2 = sTrackThread
            monitor-enter(r2)
            boolean r1 = r4.isConn     // Catch:{ all -> 0x001f }
            if (r1 != 0) goto L_0x000a
            monitor-exit(r2)     // Catch:{ all -> 0x001f }
        L_0x0009:
            return
        L_0x000a:
            r1 = 0
            r4.mService = r1     // Catch:{ all -> 0x001f }
            r1 = 0
            r4.isConn = r1     // Catch:{ all -> 0x001f }
            monitor-exit(r2)     // Catch:{ all -> 0x001f }
            android.content.Context r1 = r4.mContext
            if (r1 == 0) goto L_0x0009
            android.content.Context r1 = r4.mContext     // Catch:{ Exception -> 0x0022 }
            android.content.ServiceConnection r2 = r4.mConn     // Catch:{ Exception -> 0x0022 }
            r1.unbindService(r2)     // Catch:{ Exception -> 0x0022 }
        L_0x001c:
            r4.mContext = r3
            goto L_0x0009
        L_0x001f:
            r1 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x001f }
            throw r1
        L_0x0022:
            r0 = move-exception
            r0.printStackTrace()
            goto L_0x001c
        */
        throw new UnsupportedOperationException("Method not decompiled: cn.nubia.gamelauncher.util.NubiaTrackManager.unbindServiceInvoked():void");
    }

    public void sendEvent(String pkgName, String event) {
        if (sTrackHandler == null) {
            LogUtil.d("sendEvent", "sTrackHandler is null!!");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("pkgName", pkgName);
        bundle.putString("event", event);
        sTrackHandler.removeMessages(2);
        Message msg = sTrackHandler.obtainMessage(1);
        msg.setData(bundle);
        sTrackHandler.sendMessage(msg);
        sTrackHandler.sendMessageDelayed(sTrackHandler.obtainMessage(2), TIEMOUT);
        LogUtil.d("sendEvent", event);
    }

    public void sendEvent(String pkgName, String event, String key, String value) {
        Bundle bundle = new Bundle();
        bundle.putString("pkgName", pkgName);
        bundle.putString("event", event);
        bundle.putString(key, value);
        sTrackHandler.removeMessages(2);
        Message msg = sTrackHandler.obtainMessage(1);
        msg.setData(bundle);
        sTrackHandler.sendMessage(msg);
        sTrackHandler.sendMessageDelayed(sTrackHandler.obtainMessage(2), TIEMOUT);
        LogUtil.d("sendEvent", event + " " + key + " " + value);
    }

    public void sendEvent(String pkgName, String event, String key, boolean value) {
        Bundle bundle = new Bundle();
        bundle.putString("pkgName", pkgName);
        bundle.putString("event", event);
        bundle.putBoolean(key, value);
        sTrackHandler.removeMessages(2);
        Message msg = sTrackHandler.obtainMessage(1);
        msg.setData(bundle);
        sTrackHandler.sendMessage(msg);
        sTrackHandler.sendMessageDelayed(sTrackHandler.obtainMessage(2), TIEMOUT);
        LogUtil.d("sendEvent", event + " " + key + " " + value);
    }

    public void sendEvent(String pkgName, String event, String key, int value) {
        Bundle bundle = new Bundle();
        bundle.putString("pkgName", pkgName);
        bundle.putString("event", event);
        bundle.putInt(key, value);
        sTrackHandler.removeMessages(2);
        Message msg = sTrackHandler.obtainMessage(1);
        msg.setData(bundle);
        sTrackHandler.sendMessage(msg);
        sTrackHandler.sendMessageDelayed(sTrackHandler.obtainMessage(2), TIEMOUT);
        LogUtil.d("sendEvent", event + " " + key + " " + value);
    }

    public void sendEvent(String pkgName, Bundle bundle) {
        bundle.putString("pkgName", pkgName);
        sTrackHandler.removeMessages(2);
        Message msg = sTrackHandler.obtainMessage(1);
        msg.setData(bundle);
        sTrackHandler.sendMessage(msg);
        sTrackHandler.sendMessageDelayed(sTrackHandler.obtainMessage(2), TIEMOUT);
        LogUtil.d("sendEvent", bundle.toString());
    }
}
