package cn.nubia.gamelauncherx.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import cn.nubia.gamelauncherx.gamehandle.GameHandleConstant;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothUtils {
    public static List<BluetoothDevice> getSystemConnectedDevices() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        List<BluetoothDevice> deviceList = new ArrayList<>();
        if (adapter != null) {
            try {
                Method method = BluetoothAdapter.class.getDeclaredMethod("getConnectionState", null);
                method.setAccessible(true);
                if (((Integer) method.invoke(adapter, null)).intValue() == 2) {
                    for (BluetoothDevice device : adapter.getBondedDevices()) {
                        Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod(GameHandleConstant.EXTRA_CONNECTED, null);
                        isConnectedMethod.setAccessible(true);
                        if (((Boolean) isConnectedMethod.invoke(device, null)).booleanValue()) {
                            deviceList.add(device);
                        }
                    }
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e2) {
                e2.printStackTrace();
            } catch (IllegalAccessException e3) {
                e3.printStackTrace();
            }
        }
        return deviceList;
    }

    public static boolean isBond(String address) {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            return false;
        }
        Set<BluetoothDevice> bondedDevices = adapter.getBondedDevices();
        if (bondedDevices == null) {
            return false;
        }
        for (BluetoothDevice device : bondedDevices) {
            if (device.getAddress().equals(address)) {
                return true;
            }
        }
        return false;
    }

    public static boolean removeBond(BluetoothDevice device) {
        try {
            Method method = BluetoothDevice.class.getDeclaredMethod("removeBond", null);
            method.setAccessible(true);
            return ((Boolean) method.invoke(device, null)).booleanValue();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e2) {
            e2.printStackTrace();
        } catch (IllegalAccessException e3) {
            e3.printStackTrace();
        }
        return false;
    }
}
