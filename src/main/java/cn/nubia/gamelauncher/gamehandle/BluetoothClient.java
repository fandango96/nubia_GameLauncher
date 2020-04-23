package cn.nubia.gamelauncher.gamehandle;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import cn.nubia.gamelauncher.util.BluetoothUtils;
import cn.nubia.gamelauncher.util.ByteUtil;
import cn.nubia.gamelauncher.util.LogUtil;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;

@TargetApi(21)
public class BluetoothClient extends BroadcastReceiver {
    public static final int STATE_CONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_DISCONNECTED = 3;
    public static final int STATE_WAIT_BOND = 4;
    private static final String TAG = "BluetoothClient";
    private boolean mAutoConnectWhenBond = false;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothAddress;
    private BluetoothGatt mBluetoothGatt;
    /* access modifiers changed from: private */
    public BluetoothGattCharacteristic mCharacteristic;
    private int mConnectState = 3;
    private Context mContext;
    /* access modifiers changed from: private */
    public DataHandler mDataHandler;
    private GameHandleListener mGameHandleListener;
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            LogUtil.i(BluetoothClient.TAG, "onConnectionStateChange status " + status + " newState:" + newState);
            switch (newState) {
                case 0:
                    BluetoothClient.this.setConnectState(3);
                    return;
                case 2:
                    BluetoothClient.this.setConnectState(0);
                    gatt.discoverServices();
                    return;
                default:
                    return;
            }
        }

        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            LogUtil.d(BluetoothClient.TAG, "onServicesDiscovered");
            if (status != 0) {
                LogUtil.w(BluetoothClient.TAG, String.format("discover services failed, status: %d", new Object[]{Integer.valueOf(status)}));
                BluetoothClient.this.mCharacteristic = null;
                return;
            }
            BluetoothGattService service = gatt.getService(UUID.fromString(GameHandleConstant.HANDLE_SERVICE_UUID));
            if (service == null) {
                LogUtil.i(BluetoothClient.TAG, String.format("can not find service by uuid:%s", new Object[]{GameHandleConstant.HANDLE_SERVICE_UUID}));
                BluetoothClient.this.mCharacteristic = null;
                return;
            }
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(GameHandleConstant.HANDLE_CHARACTERISTIC_UUID));
            if (characteristic == null) {
                LogUtil.i(BluetoothClient.TAG, String.format("can not find characteristic by uuid: %s", new Object[]{GameHandleConstant.HANDLE_SERVICE_UUID}));
                BluetoothClient.this.mCharacteristic = null;
                return;
            }
            BluetoothClient.this.mCharacteristic = characteristic;
            LogUtil.i(BluetoothClient.TAG, "find service and characteristic");
            BluetoothClient.this.mSendThread.setCharacteristic();
        }

        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (status == 0) {
                Log.d(BluetoothClient.TAG, "读取成功 " + ByteUtil.byteArray2hexString(BluetoothClient.this.mCharacteristic.getValue()));
            }
        }

        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (status == 0) {
                LogUtil.d(BluetoothClient.TAG, "写入成功 " + ByteUtil.byteArray2hexString(BluetoothClient.this.mCharacteristic.getValue()));
            }
        }

        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            byte[] data = characteristic.getValue();
            LogUtil.d(BluetoothClient.TAG, "收到数据 " + ByteUtil.byteArray2hexString(data));
            BluetoothClient.this.mDataHandler.handleReceiveData(data);
        }

        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            LogUtil.d(BluetoothClient.TAG, "onDescriptorWrite " + ByteUtil.byteArray2hexString(descriptor.getValue()));
            if (status == 0 && descriptor.getUuid().equals(UUID.fromString(GameHandleConstant.CLIENT_CHARACTERISTIC_CONFIG))) {
                BluetoothClient.this.auth();
                BluetoothClient.this.mHandler.postDelayed(new Runnable() {
                    public void run() {
                        BluetoothClient.this.queryBatteryLevel();
                    }
                }, 200);
                BluetoothClient.this.mHandler.postDelayed(new Runnable() {
                    public void run() {
                        BluetoothClient.this.changeMode(true);
                    }
                }, 400);
            }
        }

        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }
    };
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler();
    /* access modifiers changed from: private */
    public SendThread mSendThread;

    public BluetoothClient(GameHandleService service, String address) {
        this.mContext = service.getApplicationContext();
        this.mGameHandleListener = service;
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        LogUtil.d(TAG, " BluetoothClient  address == " + address);
        if (address == null || address == "") {
            this.mBluetoothAddress = "address";
        } else {
            this.mBluetoothAddress = address;
        }
        this.mSendThread = new SendThread(this);
        this.mSendThread.start();
        this.mDataHandler = new DataHandler(this.mContext, this);
        LogUtil.d(TAG, "constructor() currentDevice:" + this.mBluetoothAddress);
    }

    public void addDataReceiver(GameHandleDataReceiver receiver) {
        this.mDataHandler.addDataReceiver(receiver);
    }

    public void removeDataReceiver(GameHandleDataReceiver receiver) {
        this.mDataHandler.removeDataReceiver(receiver);
    }

    public String getCurrentDevice() {
        return this.mBluetoothAddress;
    }

    public int getConnectState() {
        return this.mConnectState;
    }

    /* access modifiers changed from: private */
    public void setConnectState(int state) {
        if (this.mConnectState != state) {
            int oldState = this.mConnectState;
            this.mConnectState = state;
            this.mGameHandleListener.onConnectionStateChange(this.mBluetoothAddress, oldState, state);
        }
    }

    public boolean isConnected() {
        return this.mConnectState == 0;
    }

    public boolean connect() {
        Log.d(TAG, "start connect to " + this.mBluetoothAddress);
        if (this.mBluetoothAdapter == null) {
            LogUtil.i(TAG, "Device not support bluetooth");
            return false;
        } else if (!BluetoothAdapter.checkBluetoothAddress(this.mBluetoothAddress)) {
            LogUtil.i(TAG, "Invalidate bluetooth address:" + this.mBluetoothAddress);
            return false;
        } else if (!this.mBluetoothAdapter.isEnabled()) {
            LogUtil.d(TAG, "Bluetooth disable, connect failed");
            return false;
        } else {
            disconnect();
            if (!BluetoothUtils.isBond(this.mBluetoothAddress)) {
                BluetoothDevice device = this.mBluetoothAdapter.getRemoteDevice(this.mBluetoothAddress);
                if (device == null) {
                    LogUtil.i(TAG, "Device not found, Unable to connect");
                    return false;
                } else if (device.createBond()) {
                    LogUtil.i(TAG, "Device not bond, start request bond");
                    this.mAutoConnectWhenBond = true;
                    setConnectState(4);
                    return true;
                } else {
                    LogUtil.i(TAG, "request bond failed");
                    return false;
                }
            } else {
                BluetoothDevice device2 = this.mBluetoothAdapter.getRemoteDevice(this.mBluetoothAddress);
                if (device2 == null) {
                    LogUtil.i(TAG, "Device not found, Unable to connect");
                    return false;
                }
                LogUtil.i(TAG, "try to crate a new connection " + this.mBluetoothAddress);
                this.mBluetoothGatt = device2.connectGatt(this.mContext, false, this.mGattCallback);
                setConnectState(1);
                return true;
            }
        }
    }

    public void disconnect() {
        if (this.mBluetoothGatt != null) {
            this.mBluetoothGatt.disconnect();
            this.mBluetoothGatt.close();
            this.mBluetoothGatt = null;
        }
        this.mCharacteristic = null;
        setConnectState(3);
    }

    public void auth() {
        Random random = new Random();
        byte[] command = new byte[17];
        for (int i = 1; i < 17; i++) {
            command[i] = (byte) random.nextInt(256);
        }
        command[0] = 1;
        this.mSendThread.sendCommand(command);
    }

    public void queryBatteryLevel() {
        this.mSendThread.sendCommand(new byte[]{2});
    }

    public void calibrate() {
        this.mSendThread.sendCommand(new byte[]{3});
    }

    public void changeMode(boolean leftMode) {
        this.mSendThread.sendCommand(leftMode ? new byte[]{4, 1} : new byte[]{4, 2});
    }

    public void queryMode() {
        this.mSendThread.sendCommand(new byte[]{4, 3});
    }

    /* access modifiers changed from: protected */
    public void sendCommand(byte[] command) {
        LogUtil.d(TAG, "send  " + ByteUtil.byteArray2hexString(command));
        if (this.mCharacteristic != null && this.mBluetoothGatt != null) {
            int index = 0;
            while (true) {
                int index2 = index;
                index = index2 + 1;
                if (index2 < 4) {
                    this.mCharacteristic.setValue(command);
                    this.mCharacteristic.setWriteType(2);
                    boolean writeRet = this.mBluetoothGatt.writeCharacteristic(this.mCharacteristic);
                    LogUtil.d(TAG, "write " + ByteUtil.byteArray2hexString(command) + " result:" + writeRet);
                    if (!writeRet) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void setCharacteristic() {
        if (this.mCharacteristic != null && this.mBluetoothGatt != null) {
            try {
                LogUtil.d(TAG, "setCharacteristicNotification return " + this.mBluetoothGatt.setCharacteristicNotification(this.mCharacteristic, true));
                BluetoothGattDescriptor descriptor = this.mCharacteristic.getDescriptor(UUID.fromString(GameHandleConstant.CLIENT_CHARACTERISTIC_CONFIG));
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                this.mBluetoothGatt.writeDescriptor(descriptor);
            } catch (Exception e) {
                Log.d("kong", e.toString());
                e.printStackTrace();
            }
        }
    }

    public void connectIfDisconnected() {
        if (this.mConnectState == 3 && this.mBluetoothAddress != null) {
            connect();
        }
    }

    public boolean unBond() {
        Log.d(TAG, "unBond address:" + this.mBluetoothAddress);
        disconnect();
        Iterator it = this.mBluetoothAdapter.getBondedDevices().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            BluetoothDevice device = (BluetoothDevice) it.next();
            if (device.getAddress().equals(this.mBluetoothAddress)) {
                if (!BluetoothUtils.removeBond(device)) {
                    Log.d(TAG, "remove bond failed");
                }
            }
        }
        return true;
    }

    public void onReceive(Context context, Intent intent) {
        if ("android.bluetooth.device.action.BOND_STATE_CHANGED".equals(intent.getAction())) {
            BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
            int newState = intent.getIntExtra("android.bluetooth.device.extra.BOND_STATE", 10);
            LogUtil.d(TAG, String.format("address:%s bondState:%d currentDevice:%s mAutoConnectWhenBond:%b", new Object[]{device.getAddress(), Integer.valueOf(newState), this.mBluetoothAddress, Boolean.valueOf(this.mAutoConnectWhenBond)}));
            if (newState == 12 && this.mConnectState == 4 && this.mAutoConnectWhenBond && device.getAddress().equals(this.mBluetoothAddress)) {
                LogUtil.d(TAG, "蓝牙配对成功, 开始连接");
                connect();
            }
        }
    }

    public void close() {
        disconnect();
        this.mHandler.removeCallbacksAndMessages(null);
        this.mSendThread.quit();
    }
}
