package cn.nubia.gamelauncherx.gamehandle;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.input.InputManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import cn.nubia.gamelauncherx.R;
import cn.nubia.gamelauncherx.gamehandle.IGameHandleService.Stub;
import cn.nubia.gamelauncherx.util.BluetoothUtils;
import cn.nubia.gamelauncherx.util.CommonUtil;
import cn.nubia.gamelauncherx.util.LogUtil;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class GameHandleService extends Service implements GameHandleListener {
    private static final int INPUT_DEVICE = 4;
    private static final String ISDOUBLEHANDLE = "isConnDoubleHandle";
    private static final String ISFIRSTSTART = "isFirstStart";
    private static final int NOTIFICATION = 1001;
    private static final String NOTIFICATION_CHANNEL_ID = "0x1001";
    private static final String REDMAGICHANDLE_NAME = "GH1001";
    private static final String TAG = "GameHandleService";
    private boolean isConnected = false;
    private boolean isDoubleHandle = false;
    private boolean isFirstStart = true;
    private boolean isGameMode = false;
    private String leftAddress = null;
    private String leftAddress_toGameMode = null;
    /* access modifiers changed from: private */
    public BluetoothClient mBluetoothClientLeft;
    /* access modifiers changed from: private */
    public BluetoothClient mBluetoothClientRight;
    /* access modifiers changed from: private */
    public BluetoothProfile mBluetoothInputDevice;
    private List<GameHandleConnectionStateChangeListener> mConnectionStateChangeListeners = new ArrayList();
    private Handler mHandler = new Handler();
    private boolean mHasBindClient = false;
    private Notification mNotification;
    private String mNotificationChannelName;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtil.d(GameHandleService.TAG, "onReceive action:" + action);
            if ("android.bluetooth.device.action.ACL_CONNECTED".equals(action)) {
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                if (GameHandleService.this.mBluetoothClientLeft != null && device.getAddress().equals(GameHandleService.this.mBluetoothClientLeft.getCurrentDevice())) {
                    GameHandleService.this.mBluetoothClientLeft.connectIfDisconnected();
                } else if (GameHandleService.this.mBluetoothClientRight != null && device.getAddress().equals(GameHandleService.this.mBluetoothClientRight.getCurrentDevice())) {
                    GameHandleService.this.mBluetoothClientRight.connectIfDisconnected();
                }
            }
            if ("android.bluetooth.device.action.BOND_STATE_CHANGED".equals(action)) {
                if (GameHandleService.this.mBluetoothClientLeft != null) {
                    GameHandleService.this.mBluetoothClientLeft.onReceive(GameHandleService.this, intent);
                }
                if (GameHandleService.this.mBluetoothClientRight != null) {
                    GameHandleService.this.mBluetoothClientRight.onReceive(GameHandleService.this, intent);
                }
            }
        }
    };
    private ServiceListener mServiceListener = new ServiceListener() {
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            Log.d("kong", "profile  -- " + profile);
            if (profile == 4) {
                GameHandleService.this.mBluetoothInputDevice = proxy;
                LogUtil.d(GameHandleService.TAG, "连接到profileproxy");
                List<BluetoothDevice> deviceList = proxy.getConnectedDevices();
                Log.d("kong", "deviceList Size ---- " + deviceList.size());
                for (BluetoothDevice device : deviceList) {
                    Log.d(GameHandleService.TAG, "(" + device.getName() + device.getAddress() + ")");
                    if (device.getName() != null && device.getName().length() >= 6 && GameHandleService.REDMAGICHANDLE_NAME.equals(device.getName().substring(0, 6)) && GameHandleService.this.isFirst() && !GameHandleService.this.isConnected(device.getAddress())) {
                        if (!GameHandleService.this.mBluetoothClientLeft.isConnected()) {
                            GameHandleService.this.connect(device.getAddress(), true);
                            Log.d("kong", "leftAddress   ----" + device.getAddress());
                        } else if (!GameHandleService.this.mBluetoothClientRight.isConnected()) {
                            GameHandleService.this.connect(device.getAddress(), false);
                            Log.d("kong", "rightAddress   ------ " + device.getAddress());
                            GameHandleService.this.mSharedPref.edit().putBoolean(GameHandleService.ISFIRSTSTART, false).commit();
                        }
                    }
                }
            }
        }

        public void onServiceDisconnected(int profile) {
            GameHandleService.this.mBluetoothInputDevice = null;
            LogUtil.d(GameHandleService.TAG, "profileproxy连接断开");
        }
    };
    /* access modifiers changed from: private */
    public SharedPreferences mSharedPref;
    private String rightAddress = null;
    private String rightAddress_toGameMode = null;

    public class LocalBinder extends Stub {
        public LocalBinder() {
        }

        public boolean isGameHandleConnected() throws RemoteException {
            return GameHandleService.this.mBluetoothClientLeft.isConnected();
        }

        public boolean connect(String address) throws RemoteException {
            return GameHandleService.this.mBluetoothClientLeft.connect();
        }

        public void disconnect() throws RemoteException {
            GameHandleService.this.mBluetoothClientLeft.disconnect();
        }

        public int getConnectState() throws RemoteException {
            return GameHandleService.this.mBluetoothClientLeft.getConnectState();
        }

        public GameHandleService getService() {
            return GameHandleService.this;
        }
    }

    public IBinder onBind(Intent intent) {
        LogUtil.d(TAG, "onBind connect state " + this.mBluetoothClientLeft.getConnectState() + "  right handle " + this.mBluetoothClientRight.getConnectState());
        this.mHasBindClient = true;
        return new LocalBinder();
    }

    public void onRebind(Intent intent) {
        LogUtil.d(TAG, "onRebind connect state " + this.mBluetoothClientLeft.getConnectState());
        super.onRebind(intent);
    }

    public boolean onUnbind(Intent intent) {
        LogUtil.d(TAG, "onUnbind connect state " + this.mBluetoothClientLeft.getConnectState());
        this.mHasBindClient = false;
        return super.onUnbind(intent);
    }

    public void onConnectionStateChange(final String address, int oldState, final int newState) {
        boolean z = true;
        LogUtil.d(TAG, String.format("onConnectionStateChange %d => %d", new Object[]{Integer.valueOf(oldState), Integer.valueOf(newState)}));
        this.mHandler.post(new Runnable() {
            public void run() {
                GameHandleService.this.notifyConnectionStateToListener(address, newState);
            }
        });
        if (newState == 0 || oldState == 0) {
            this.isConnected = newState == 0;
            isDoubleHandle();
            if ((this.mBluetoothClientLeft == null || !this.mBluetoothClientLeft.isConnected()) && (this.mBluetoothClientRight == null || !this.mBluetoothClientRight.isConnected())) {
                this.isConnected = false;
            } else {
                this.isConnected = true;
            }
            if (!this.isGameMode || !this.isConnected) {
                z = false;
            }
            broadcastConnectionState(z);
        }
    }

    public void onCreate() {
        super.onCreate();
        this.mSharedPref = getSharedPreferences(GameHandleConstant.PREF_FILE_NAME, 0);
        this.leftAddress = this.mSharedPref.getString(GameHandleConstant.LEFT_HANDLE_ADDRESS, null);
        this.mBluetoothClientLeft = new BluetoothClient(this, this.leftAddress);
        this.rightAddress = this.mSharedPref.getString(GameHandleConstant.RIGHT_HANDLE_ADDRESS, null);
        this.mBluetoothClientRight = new BluetoothClient(this, this.rightAddress);
        LogUtil.d(TAG, "[GameHandleService] onCreate One = " + this.leftAddress + " Two = " + this.rightAddress);
        this.isGameMode = this.mSharedPref.getBoolean(GameHandleConstant.KEY_GAME_MODE, false);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.bluetooth.adapter.action.CONNECTION_STATE_CHANGED");
        filter.addAction("android.bluetooth.device.action.ACL_CONNECTED");
        filter.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
        filter.addAction("android.bluetooth.device.action.BOND_STATE_CHANGED");
        registerReceiver(this.mReceiver, filter);
        if (CommonUtil.isInternalVersion() && CommonUtil.isRedMagicPhone()) {
            this.mNotificationChannelName = getString(R.string.handle_service_tips);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(new NotificationChannel(NOTIFICATION_CHANNEL_ID, this.mNotificationChannelName, NotificationManager.IMPORTANCE_DEFAULT));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                this.mNotification = new Builder(this, NOTIFICATION_CHANNEL_ID).setSmallIcon(R.mipmap.ic_launcher).setContentTitle(getString(R.string.app_name)).setContentText(getString(R.string.handle_service_tips)).build();
            }
            startForeground(1001, this.mNotification);
        }
        getSystemConnected();
    }

    public void getSystemConnected() {
        if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            BluetoothAdapter.getDefaultAdapter().getProfileProxy(this, this.mServiceListener, 4);
        }
    }

    public boolean getConnDoubleHandle() {
        return this.mSharedPref.getBoolean(ISDOUBLEHANDLE, true);
    }

    public void setConnDoubleHandle() {
        this.mSharedPref.edit().putBoolean(ISDOUBLEHANDLE, false).commit();
    }

    public boolean isFirst() {
        this.isFirstStart = this.mSharedPref.getBoolean(ISFIRSTSTART, true);
        return this.isFirstStart;
    }

    public String getLeftAddress() {
        return this.mSharedPref.getString(GameHandleConstant.LEFT_HANDLE_ADDRESS, null);
    }

    public String getRightAddress() {
        return this.mSharedPref.getString(GameHandleConstant.RIGHT_HANDLE_ADDRESS, null);
    }

    public void setResetLR(String leftAddreBE, String rightAddreBE) {
        this.mSharedPref.edit().putString(GameHandleConstant.LEFT_HANDLE_ADDRESS, leftAddreBE).apply();
        this.mSharedPref.edit().putString(GameHandleConstant.RIGHT_HANDLE_ADDRESS, rightAddreBE).apply();
    }

    public void refreshClient() {
        this.leftAddress = getLeftAddress();
        connect(this.leftAddress, true);
        this.rightAddress = getRightAddress();
        connect(this.rightAddress, false);
    }

    public void sendCalibrationState() {
        connect(this.leftAddress, true);
        connect(this.rightAddress, false);
        if (isConnected(this.leftAddress) || isConnected(this.rightAddress)) {
            this.isConnected = true;
        } else {
            this.isConnected = false;
        }
        broadcastConnectionState(this.isConnected);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent != null ? intent.getAction() : "null";
        LogUtil.d(TAG, "onStartCommand action: " + action);
        if ("android.bluetooth.adapter.action.STATE_CHANGED".equals(action)) {
            handleBluetoothStateChange();
        } else if (GameHandleConstant.ACTION_GAME_MODE.equals(action)) {
            handleGameModeChange(intent);
        } else {
            handleNullIntent();
        }
        return Service.START_STICKY;
    }

    private void isDoubleHandle() {
        if (!isConnected(this.leftAddress) || !isConnected(this.rightAddress)) {
            this.isDoubleHandle = false;
        } else {
            this.isDoubleHandle = true;
        }
    }

    public void changeMAC() {
        LogUtil.d(TAG, "[GameHandleService] changeMAC one = " + this.leftAddress + " two = " + this.rightAddress);
        if (this.leftAddress != null) {
            String[] left = this.leftAddress.split(":");
            this.leftAddress_toGameMode = left[5] + ":" + left[4] + ":" + left[3] + ":" + left[2] + ":" + left[1] + ":" + left[0];
            if (this.mBluetoothClientLeft != null && !this.mBluetoothClientLeft.isConnected()) {
                this.leftAddress_toGameMode = null;
            }
        }
        if (this.rightAddress != null) {
            String[] right = this.rightAddress.split(":");
            this.rightAddress_toGameMode = right[5] + ":" + right[4] + ":" + right[3] + ":" + right[2] + ":" + right[1] + ":" + right[0];
            if (this.mBluetoothClientRight != null && !this.mBluetoothClientRight.isConnected()) {
                this.rightAddress_toGameMode = null;
            }
        }
    }

    private void broadcastConnectionState(boolean isConnected2) {
        changeMAC();
        int leftDeviceId = getGamePadDeviceId(this, this.leftAddress_toGameMode);
        LogUtil.d(TAG, "leftDeviceId --> " + leftDeviceId + "\n" + "  rightDeviceId ---> " + getGamePadDeviceId(this, this.rightAddress_toGameMode) + "\n" + "    isConnected ===> " + isConnected2 + "\n" + "      leftAddress ---> " + this.mSharedPref.getString(GameHandleConstant.LEFT_HANDLE_ADDRESS, null) + "\n" + "  rightAddress ---> " + this.mSharedPref.getString(GameHandleConstant.RIGHT_HANDLE_ADDRESS, null) + "\n" + "    isDoubleHandle   ===> " + this.isDoubleHandle + "\n" + "  leftAddress_toGameMode  --> " + this.leftAddress_toGameMode + "\n" + "  rightAddress_toGameMode  --> " + this.rightAddress_toGameMode);
        Intent intent = new Intent(GameHandleConstant.ACTION_GAMEHANDLE_CONNECTION_STATE_CHANGE);
        intent.putExtra(GameHandleConstant.EXTRA_CONNECTED, isConnected2);
        intent.putExtra(GameHandleConstant.DOUBLE_HANDLE, this.isDoubleHandle);
        intent.putExtra(GameHandleConstant.LEFT_HANDLE_ADDRESS, this.leftAddress_toGameMode);
        intent.putExtra(GameHandleConstant.RIGHT_HANDLE_ADDRESS, this.rightAddress_toGameMode);
        sendBroadcast(intent);
        LogUtil.d(TAG, "broadcastConnectionState " + isConnected2);
    }

    private void handleGameModeChange(Intent intent) {
        this.isGameMode = intent.getBooleanExtra(GameHandleConstant.GAME_MODE_EXTRA_ISRUNNING, false);
        LogUtil.d(TAG, String.format("handleGameModeChange isGameMode=%b", new Object[]{Boolean.valueOf(this.isGameMode)}));
        this.mSharedPref.edit().putBoolean(GameHandleConstant.KEY_GAME_MODE, this.isGameMode).apply();
        if (!this.isGameMode) {
            isDoubleHandle();
            if ((this.mBluetoothClientLeft == null || !this.mBluetoothClientLeft.isConnected()) && (this.mBluetoothClientRight == null || !this.mBluetoothClientRight.isConnected())) {
                this.isConnected = false;
            } else {
                this.isConnected = true;
            }
            broadcastConnectionState(this.isConnected);
            safeStopSelf();
            return;
        }
        handleNullIntent();
        isDoubleHandle();
    }

    private void handleBluetoothStateChange() {
        boolean enable = BluetoothAdapter.getDefaultAdapter().isEnabled();
        LogUtil.d(TAG, String.format("handleBluetoothStateChange isEnable=%b", new Object[]{Boolean.valueOf(enable)}));
        if (!enable) {
            isDoubleHandle();
            if ((this.mBluetoothClientLeft == null || !this.mBluetoothClientLeft.isConnected()) && (this.mBluetoothClientRight == null || !this.mBluetoothClientRight.isConnected())) {
                this.isConnected = false;
            } else {
                this.isConnected = true;
            }
            broadcastConnectionState(this.isConnected);
            safeStopSelf();
            return;
        }
        isDoubleHandle();
        handleNullIntent();
    }

    private void handleNullIntent() {
        LogUtil.d(TAG, "handleNullIntent");
        if (!this.isGameMode || !BluetoothAdapter.getDefaultAdapter().isEnabled() || (TextUtils.isEmpty(this.mBluetoothClientLeft.getCurrentDevice()) && TextUtils.isEmpty(this.mBluetoothClientRight.getCurrentDevice()))) {
            isDoubleHandle();
            if (this.mBluetoothClientLeft.isConnected() || this.mBluetoothClientRight.isConnected()) {
                this.isConnected = true;
            } else {
                this.isConnected = false;
            }
            broadcastConnectionState(this.isConnected);
        } else if (this.mBluetoothClientLeft.isConnected() || this.mBluetoothClientRight.isConnected()) {
            isDoubleHandle();
            broadcastConnectionState(true);
        } else {
            boolean isLeftConnectToSystem = false;
            boolean isRightConnectToSystem = false;
            for (BluetoothDevice device : BluetoothUtils.getSystemConnectedDevices()) {
                if (this.mBluetoothClientLeft != null && device.getAddress().equals(this.mBluetoothClientLeft.getCurrentDevice())) {
                    isLeftConnectToSystem = true;
                }
                if (this.mBluetoothClientRight != null && device.getAddress().equals(this.mBluetoothClientRight.getCurrentDevice())) {
                    isRightConnectToSystem = true;
                }
            }
            if (isLeftConnectToSystem) {
                LogUtil.d(TAG, "系统已连接手柄，游戏空间尝试自动连接");
                this.mBluetoothClientLeft.connectIfDisconnected();
            }
            if (isRightConnectToSystem) {
                LogUtil.d(TAG, "系统已连接手柄，游戏空间尝试自动连接");
                this.mBluetoothClientRight.connectIfDisconnected();
            }
        }
    }

    private void safeStopSelf() {
        if (!this.mHasBindClient) {
            stopSelf();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy");
        unregisterReceiver(this.mReceiver);
        this.mSharedPref.edit().putBoolean(ISFIRSTSTART, true).commit();
        this.mSharedPref.edit().putBoolean(ISDOUBLEHANDLE, true).commit();
        try {
            if (this.mBluetoothInputDevice != null) {
                Method method = this.mBluetoothInputDevice.getClass().getDeclaredMethod("close", null);
                method.setAccessible(true);
                method.invoke(this.mBluetoothInputDevice, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (this.mBluetoothClientLeft != null) {
            this.mBluetoothClientLeft.close();
            this.mBluetoothClientLeft = null;
        }
        if (this.mBluetoothClientRight != null) {
            this.mBluetoothClientRight.close();
            this.mBluetoothClientRight = null;
        }
    }

    public boolean isConnected(String address) {
        if (this.mBluetoothClientLeft != null && this.mBluetoothClientLeft.getCurrentDevice().equals(address)) {
            return this.mBluetoothClientLeft.isConnected();
        }
        if (this.mBluetoothClientRight == null || !this.mBluetoothClientRight.getCurrentDevice().equals(address)) {
            return false;
        }
        return this.mBluetoothClientRight.isConnected();
    }

    public boolean connect(String address, boolean isLeft) {
        LogUtil.d(TAG, " isLeft = " + isLeft + " connect  address = " + address);
        if (isLeft) {
            if (this.mBluetoothClientLeft != null) {
                this.mBluetoothClientLeft.close();
                this.mBluetoothClientLeft = null;
            }
            this.mBluetoothClientLeft = new BluetoothClient(this, address);
            this.leftAddress = address;
            this.mSharedPref.edit().putString(GameHandleConstant.LEFT_HANDLE_ADDRESS, address).apply();
            return this.mBluetoothClientLeft.connect();
        }
        if (this.mBluetoothClientRight != null) {
            this.mBluetoothClientRight.close();
            this.mBluetoothClientRight = null;
        }
        this.mBluetoothClientRight = new BluetoothClient(this, address);
        this.rightAddress = address;
        this.mSharedPref.edit().putString(GameHandleConstant.RIGHT_HANDLE_ADDRESS, address).apply();
        return this.mBluetoothClientRight.connect();
    }

    public boolean unBond(String address) {
        LogUtil.d(TAG, "[GameHandleService] unBond address = " + address);
        if (this.mBluetoothClientLeft != null && this.mBluetoothClientLeft.getCurrentDevice().equals(address)) {
            this.mBluetoothClientLeft.disconnect();
            this.mSharedPref.edit().putString(GameHandleConstant.LEFT_HANDLE_ADDRESS, null).apply();
            boolean result = this.mBluetoothClientLeft.unBond();
            LogUtil.d(TAG, "[GameHandleService] unBond address  one = " + address);
            return result;
        } else if (this.mBluetoothClientRight == null || !this.mBluetoothClientRight.getCurrentDevice().equals(address)) {
            return true;
        } else {
            this.mBluetoothClientRight.disconnect();
            this.mSharedPref.edit().putString(GameHandleConstant.RIGHT_HANDLE_ADDRESS, null).apply();
            boolean result2 = this.mBluetoothClientRight.unBond();
            LogUtil.d(TAG, "[GameHandleService] unBond address  two = " + address);
            return result2;
        }
    }

    public void addConnectionStateChangeListener(GameHandleConnectionStateChangeListener listener) {
        synchronized (this.mConnectionStateChangeListeners) {
            if (listener != null) {
                if (!this.mConnectionStateChangeListeners.contains(listener)) {
                    this.mConnectionStateChangeListeners.add(listener);
                }
            }
        }
    }

    public void removeConnectionStateChangeListener(GameHandleConnectionStateChangeListener listener) {
        synchronized (this.mConnectionStateChangeListeners) {
            if (listener != null) {
                if (this.mConnectionStateChangeListeners.contains(listener)) {
                    this.mConnectionStateChangeListeners.remove(listener);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void notifyConnectionStateToListener(String address, int state) {
        synchronized (this.mConnectionStateChangeListeners) {
            for (GameHandleConnectionStateChangeListener listener : this.mConnectionStateChangeListeners) {
                listener.onConnectionStateChange(address, state);
            }
        }
    }

    public void addDataReceiver(GameHandleDataReceiver receiver) {
        if (this.mBluetoothClientLeft != null) {
            this.mBluetoothClientLeft.addDataReceiver(receiver);
        }
        if (this.mBluetoothClientRight != null) {
            this.mBluetoothClientRight.addDataReceiver(receiver);
        }
    }

    public void removeDataReceiver(GameHandleDataReceiver receiver) {
        if (this.mBluetoothClientLeft != null) {
            this.mBluetoothClientLeft.removeDataReceiver(receiver);
        }
        if (this.mBluetoothClientRight != null) {
            this.mBluetoothClientRight.removeDataReceiver(receiver);
        }
    }

    public void calibrate(String address) {
        if (this.mBluetoothClientLeft != null && this.mBluetoothClientLeft.getCurrentDevice().equals(address)) {
            this.mBluetoothClientLeft.calibrate();
        } else if (this.mBluetoothClientRight != null && this.mBluetoothClientRight.getCurrentDevice().equals(address)) {
            this.mBluetoothClientRight.calibrate();
        }
    }

    public void getBattery(String address) {
        LogUtil.d(TAG, "[GameHandleService] getBattery address = " + address + "  rightAddress = " + this.rightAddress);
        if (this.mBluetoothClientLeft != null && this.mBluetoothClientLeft.getCurrentDevice().equals(address)) {
            this.mBluetoothClientLeft.queryBatteryLevel();
        } else if (this.mBluetoothClientRight != null && this.mBluetoothClientRight.getCurrentDevice().equals(address)) {
            LogUtil.d(TAG, "[GameHandleService] rightHandle = " + this.rightAddress);
            this.mBluetoothClientRight.queryBatteryLevel();
        }
    }

    public static int getGamePadDeviceId(Context context, String address) {
        int gamepadDeviceId = -1;
        try {
            InputManager inputManager = (InputManager) context.getSystemService(Context.INPUT_SERVICE);
            return ((Integer) InputManager.class.getDeclaredMethod("getGamepadDeviceId", new Class[]{String.class}).invoke(inputManager, new Object[]{address})).intValue();
        } catch (Exception e) {
            e.printStackTrace();
            return gamepadDeviceId;
        }
    }
}
