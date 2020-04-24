package cn.nubia.gamelauncherx.gamehandle;

import android.bluetooth.BluetoothDevice;
import java.util.List;

public interface GameHandleScanCallback {
    void onBatchScanResults(List<BluetoothDevice> list);

    void onScanFailed(int i);

    void onScanResult(BluetoothDevice bluetoothDevice);
}
