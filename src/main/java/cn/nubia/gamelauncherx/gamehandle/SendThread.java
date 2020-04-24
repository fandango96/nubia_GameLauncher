package cn.nubia.gamelauncherx.gamehandle;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;

public class SendThread extends HandlerThread implements Callback {
    private static final int MSG_SEND = 1;
    private static final int MSG_SET_CHARACTERISTIC = 2;
    private BluetoothClient mBluetoothClient;
    private Handler mHandler;

    public SendThread(BluetoothClient bluetoothClient) {
        super("SendThread");
        this.mBluetoothClient = bluetoothClient;
    }

    /* access modifiers changed from: protected */
    public void onLooperPrepared() {
        super.onLooperPrepared();
        this.mHandler = new Handler(getLooper(), this);
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 1:
                this.mBluetoothClient.sendCommand((byte[]) msg.obj);
                break;
            case 2:
                this.mBluetoothClient.setCharacteristic();
                break;
        }
        return true;
    }

    public void sendCommand(byte[] data) {
        if (this.mHandler != null) {
            this.mHandler.sendMessage(Message.obtain(this.mHandler, 1, data));
        }
    }

    public void setCharacteristic() {
        if (this.mHandler != null) {
            this.mHandler.sendEmptyMessage(2);
        }
    }
}
