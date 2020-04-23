package cn.nubia.gamelauncher.gamehandle;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.util.Log;
import cn.nubia.gamelauncher.util.BluetoothUtils;
import cn.nubia.gamelauncher.util.LogUtil;
import java.util.ArrayList;
import java.util.List;

@TargetApi(21)
public class ScanHelper {
    private static final String TAG = "ScanHelper";
    private static final long WAIT_BLUETOOTH_OPEN_DELAY = 500;
    private boolean isScanning = false;
    /* access modifiers changed from: private */
    public BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    /* access modifiers changed from: private */
    public GameHandleScanCallback mGameHandleScanCallback;
    private Runnable mGetSystemConnectedDevices = new Runnable() {
        public void run() {
            List<BluetoothDevice> devices = BluetoothUtils.getSystemConnectedDevices();
            if (ScanHelper.this.mGameHandleScanCallback != null && !devices.isEmpty()) {
                ScanHelper.this.mGameHandleScanCallback.onBatchScanResults(devices);
            }
        }
    };
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler(Looper.getMainLooper());
    private ScanCallback mLeScanCallback = new ScanCallback() {
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            LogUtil.d(ScanHelper.TAG, "onScanResult: " + result.toString());
            BluetoothDevice gameHandleDevice = ScanHelper.this.filterScanResult(result);
            if (gameHandleDevice != null && ScanHelper.this.mGameHandleScanCallback != null) {
                ScanHelper.this.mGameHandleScanCallback.onScanResult(gameHandleDevice);
            }
        }

        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            LogUtil.d(ScanHelper.TAG, "onBatchScanResults: " + results.size());
            List<BluetoothDevice> devices = new ArrayList<>();
            BluetoothDevice device = null;
            for (ScanResult result : results) {
                device = ScanHelper.this.filterScanResult(result);
                if (device != null) {
                    devices.add(device);
                }
            }
            if (!devices.isEmpty() && ScanHelper.this.mGameHandleScanCallback != null) {
                if (devices.size() > 1) {
                    ScanHelper.this.mGameHandleScanCallback.onBatchScanResults(devices);
                } else {
                    ScanHelper.this.mGameHandleScanCallback.onScanResult(device);
                }
            }
        }

        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.w(ScanHelper.TAG, "onScanFailed error: " + errorCode);
            if (ScanHelper.this.mGameHandleScanCallback != null) {
                ScanHelper.this.mGameHandleScanCallback.onScanFailed(errorCode);
            }
            ScanHelper.this.stopScan();
        }
    };
    private Runnable mScanTask = new Runnable() {
        public void run() {
            if (ScanHelper.this.mBluetoothAdapter.getBluetoothLeScanner() == null) {
                ScanHelper.this.mHandler.postDelayed(this, ScanHelper.WAIT_BLUETOOTH_OPEN_DELAY);
            } else {
                ScanHelper.this.doScan();
            }
        }
    };

    public boolean scan(GameHandleScanCallback callback) {
        if (this.mBluetoothAdapter == null) {
            LogUtil.w(TAG, "device does not support bluetooth");
            return false;
        } else if (this.mBluetoothAdapter.isEnabled()) {
            LogUtil.i(TAG, "start scan");
            this.mGameHandleScanCallback = callback;
            this.isScanning = true;
            doScan();
            return true;
        } else if (!this.mBluetoothAdapter.enable()) {
            LogUtil.w(TAG, "open bluetooth failed");
            return false;
        } else {
            this.mGameHandleScanCallback = callback;
            this.isScanning = true;
            this.mHandler.postDelayed(this.mScanTask, WAIT_BLUETOOTH_OPEN_DELAY);
            Log.d(TAG, "wait bluetooth open");
            return true;
        }
    }

    /* access modifiers changed from: private */
    public void doScan() {
        BluetoothLeScanner scanner = this.mBluetoothAdapter.getBluetoothLeScanner();
        if (scanner == null) {
            LogUtil.w(TAG, "scanner is null");
            return;
        }
        scanner.startScan(this.mLeScanCallback);
        this.mHandler.post(this.mGetSystemConnectedDevices);
        LogUtil.d(TAG, "start scan");
    }

    public void stopScan() {
        if (this.mBluetoothAdapter == null) {
            LogUtil.w(TAG, "device does not support bluetooth");
            return;
        }
        BluetoothLeScanner scanner = this.mBluetoothAdapter.getBluetoothLeScanner();
        if (scanner != null) {
            scanner.stopScan(this.mLeScanCallback);
        }
        this.mGameHandleScanCallback = null;
        this.isScanning = false;
        this.mHandler.removeCallbacksAndMessages(null);
        LogUtil.i(TAG, "stop scan");
    }

    public boolean isScanning() {
        return this.isScanning;
    }

    /* access modifiers changed from: private */
    public BluetoothDevice filterScanResult(ScanResult result) {
        LogUtil.d(TAG, "filterScanResult:" + result.getDevice().getName());
        BluetoothDevice device = result.getDevice();
        String address = device.getAddress();
        String name = device.getName();
        List<ParcelUuid> uuids = result.getScanRecord().getServiceUuids();
        if (uuids != null) {
            for (ParcelUuid uuid : uuids) {
                LogUtil.d(TAG, "filterScanResult service uuid " + uuid.toString());
                if (uuid.toString() != null) {
                    return device;
                }
            }
        }
        return null;
    }
}
