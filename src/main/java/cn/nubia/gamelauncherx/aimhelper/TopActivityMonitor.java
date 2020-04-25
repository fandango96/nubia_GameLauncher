package cn.nubia.gamelauncherx.aimhelper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class TopActivityMonitor extends Handler {
    private static final String CLASS_NAME = "android.app.NubiaSysState";
    private static final String METHOD_REGISTER = "registerReceiverHandler";
    private static final String METHOD_UNREGISTER = "unregisterReceiver";
    private static final int SYS_STATE_ACT_PAUSE = 2002;
    private static final int SYS_STATE_ACT_RESUME = 2001;
    private static final int SYS_STATE_ACT_RESUMED = 2005;
    private static final int SYS_STATE_ACT_STOP = 2003;
    private static final int SYS_STATE_ACT_TOP = 2000;
    private static final int SYS_STATE_APP_START = 2100;
    private static final int SYS_STATE_APP_STOP = 2101;
    private static final int SYS_STATE_KEYGUARD = 2102;
    private static final int SYS_STATE_RESUME_APP_DIED = 2004;
    private static final String TAG = TopActivityMonitor.class.getSimpleName();
    private Set<String> activityStack = null;
    private TopActivityMonitorCallback mCallback;
    private Object mNubiaSysState;

    interface TopActivityMonitorCallback {
        void onActivityChange(Set<String> set);

        void onAppStop(String str);
    }

    public TopActivityMonitor(TopActivityMonitorCallback callback) {
        this.mCallback = callback;
        LogUtil.d(TAG, "topPkg" + ActivityUtils.getCurrentTopPkg());
    }

    public void start() {
        registerCallback();
    }

    public void stop() {
        unregisterCallback();
    }

    private void createSysStateObj() {
        try {
            if (this.mNubiaSysState == null) {
                Class<?> clz = Class.forName(CLASS_NAME);
                if (this.mNubiaSysState == null) {
                    this.mNubiaSysState = clz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (InstantiationException e3) {
            e3.printStackTrace();
        } catch (NoSuchMethodException e4) {
            e4.printStackTrace();
        } catch (InvocationTargetException e5) {
            e5.printStackTrace();
        }
    }

    private void registerCallback() {
        createSysStateObj();
        try {
            Method registerReceiverHandlerMethod = Class.forName(CLASS_NAME).getDeclaredMethod(METHOD_REGISTER, new Class[]{Handler.class});
            registerReceiverHandlerMethod.setAccessible(true);
            registerReceiverHandlerMethod.invoke(this.mNubiaSysState, new Object[]{this});
            LogUtil.d(TAG, "registerReceiverHandler success");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (NoSuchMethodException e3) {
            e3.printStackTrace();
        } catch (InvocationTargetException e4) {
            e4.printStackTrace();
        }
    }

    private void unregisterCallback() {
        try {
            LogUtil.d(TAG, "unregisterCallback mNubiaSysState =" + this.mNubiaSysState);
            if (this.mNubiaSysState != null) {
                Method registerReceiverHandlerMethod = Class.forName(CLASS_NAME).getDeclaredMethod(METHOD_UNREGISTER, new Class[0]);
                registerReceiverHandlerMethod.setAccessible(true);
                registerReceiverHandlerMethod.invoke(this.mNubiaSysState, new Object[0]);
            }
            LogUtil.d(TAG, "unregisterReceiver success");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (NoSuchMethodException e3) {
            e3.printStackTrace();
        } catch (InvocationTargetException e4) {
            e4.printStackTrace();
        }
    }

    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        StringBuffer sb = new StringBuffer();
        Bundle bundle = msg.getData();
        String pkgName = bundle.getString("packageName");
        sb.append(pkgName);
        String actName = bundle.getString("activityName");
        sb.append("/").append(actName);
        switch (msg.what) {
            case SYS_STATE_ACT_TOP /*2000*/:
                sb.append(" activity top ");
                handleTopChange(bundle);
                return;
            case SYS_STATE_ACT_RESUME /*2001*/:
                sb.append(" activity resume");
                onActivityResume(pkgName, actName);
                return;
            case SYS_STATE_ACT_PAUSE /*2002*/:
                sb.append(" activity pause");
                onActivityPause(pkgName, actName);
                return;
            case SYS_STATE_ACT_STOP /*2003*/:
                sb.append(" activity stop");
                onActivityStop(pkgName, actName);
                return;
            case SYS_STATE_RESUME_APP_DIED /*2004*/:
                sb.append(" resume ap died");
                return;
            case SYS_STATE_ACT_RESUMED /*2005*/:
                sb.append(" activity resumed");
                onActivityResumed(pkgName, actName);
                return;
            case SYS_STATE_APP_START /*2100*/:
                sb.append(" app start");
                return;
            case SYS_STATE_APP_STOP /*2101*/:
                sb.append(" app stop");
                return;
            case SYS_STATE_KEYGUARD /*2102*/:
                sb.append(" keyguard");
                return;
            default:
                sb.append("unhandle msg " + msg.what);
                return;
        }
    }

    private void onActivityResumed(String pkgName, String actName) {
        LogUtil.d(TAG, "--------------------------------------------------------------------------");
        LogUtil.d(TAG, "resumed " + pkgName + "/" + actName);
    }

    private void onActivityResume(String packageName, String activityName) {
        LogUtil.d(TAG, "--------------------------------------------------------------------------");
        LogUtil.d(TAG, "resume " + packageName + "/" + activityName);
        if (this.activityStack != null) {
            if ("cn.nubia.gamelauncher".equals(packageName) && "cn.nubia.gamelauncher.activity.GameSpaceActivity".equals(activityName)) {
                this.activityStack.clear();
            }
            this.activityStack.add(packageName + "/" + activityName);
            if (this.mCallback != null) {
                this.mCallback.onActivityChange(this.activityStack);
            }
        }
    }

    private void onActivityPause(String packageName, String activityName) {
        LogUtil.d(TAG, "--------------------------------------------------------------------------");
        LogUtil.d(TAG, "pause " + packageName + "/" + activityName);
        if (this.activityStack != null) {
            this.activityStack.remove(packageName + "/" + activityName);
            if (this.mCallback != null) {
                this.mCallback.onActivityChange(this.activityStack);
            }
        }
    }

    private void onActivityStop(String pkgName, String actName) {
        LogUtil.d(TAG, "--------------------------------------------------------------------------");
        LogUtil.d(TAG, "stop " + pkgName + "/" + actName);
        if (this.activityStack != null) {
            this.activityStack.remove(pkgName + "/" + actName);
            if (this.mCallback != null) {
                this.mCallback.onActivityChange(this.activityStack);
            }
        }
    }

    private void handleTopChange(Bundle bundle) {
        if (this.activityStack == null) {
            LogUtil.d(TAG, "handleTopChange");
            this.activityStack = new HashSet();
            String pkg = bundle.getString("packageName");
            String act = bundle.getString("activityName");
            if (!TextUtils.isEmpty(pkg) && !TextUtils.isEmpty(act)) {
                this.activityStack.add(pkg + "/" + act);
                LogUtil.d(TAG, "component=" + pkg + "/" + act);
            }
            int i = 0;
            while (true) {
                if (i >= 100 || !bundle.containsKey("stackId" + i) || !TextUtils.isEmpty(null)) {
                    break;
                }
                String packageName = bundle.getString("packageName" + i);
                if ("com.tencent.mm".equals(packageName) || "com.tencent.mobileqq".equals(packageName) || "cn.nubia.browser".equals(packageName)) {
                    i++;
                } else {
                    LogUtil.d(TAG, "stackId" + i + "=" + null + "/" + null);
                    String activityName = bundle.getString("activityName" + i);
                    if (!TextUtils.isEmpty(packageName) && !TextUtils.isEmpty(activityName)) {
                        this.activityStack.add(packageName + "/" + activityName);
                    }
                }
            }
            if (this.mCallback != null) {
                this.mCallback.onActivityChange(this.activityStack);
            }
        }
    }

    private void onAppStop(String packageName) {
        LogUtil.d(TAG, "onAppStop " + packageName);
        if (this.activityStack != null && !TextUtils.isEmpty(packageName)) {
            Iterator<String> iterator = this.activityStack.iterator();
            while (iterator.hasNext()) {
                String value = (String) iterator.next();
                if (value != null && value.startsWith(packageName + "/")) {
                    iterator.remove();
                }
            }
        }
        if (this.mCallback != null) {
            this.mCallback.onActivityChange(this.activityStack);
        }
    }

    public void notifyTopChange() {
        if (this.mCallback != null && this.activityStack != null) {
            this.mCallback.onActivityChange(this.activityStack);
        }
    }
}
