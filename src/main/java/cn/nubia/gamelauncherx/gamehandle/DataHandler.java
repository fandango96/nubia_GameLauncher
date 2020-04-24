package cn.nubia.gamelauncherx.gamehandle;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import cn.nubia.gamelauncherx.util.LogUtil;
import java.util.ArrayList;
import java.util.List;

public class DataHandler {
    private static final int MSG_AUTH_SUCCESS = 1;
    private static final int MSG_CALIBRATE_RESULT = 3;
    private static final int MSG_GET_BATTERY = 2;
    private static final int MSG_MAP_OPTION = 4;
    private static final int MSG_OPT_MODE_CHANGE = 5;
    private static final String TAG = "DataHandler";
    /* access modifiers changed from: private */
    public BluetoothClient mClient;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public List<GameHandleDataReceiver> mDataReceiver = new ArrayList();
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2:
                    for (GameHandleDataReceiver receiver : DataHandler.this.mDataReceiver) {
                        LogUtil.d(DataHandler.TAG, "MSG_GET_BATTERY : " + msg.arg1);
                        receiver.onBatteryChange(DataHandler.this.mClient.getCurrentDevice(), msg.arg1);
                    }
                    return;
                case 3:
                    for (GameHandleDataReceiver receiver2 : DataHandler.this.mDataReceiver) {
                        LogUtil.d(DataHandler.TAG, "MSG_CALIBRATE_RESULT " + msg.arg1);
                        receiver2.onCalibrateResult(DataHandler.this.mClient.getCurrentDevice(), msg.arg1 == 1);
                    }
                    return;
                case 4:
                    LogUtil.i(DataHandler.TAG, "send broadcast: cn.nubia.intent.action.HAND_SHANK_SCREEN_MAP_OPTION");
                    String address = DataHandler.this.mClient.getCurrentDevice();
                    String addressBig = null;
                    if (address != null) {
                        String[] addreBE = address.split(":");
                        addressBig = addreBE[5] + ":" + addreBE[4] + ":" + addreBE[3] + ":" + addreBE[2] + ":" + addreBE[1] + ":" + addreBE[0];
                    }
                    Intent intent = new Intent(GameHandleConstant.ACTION_HAND_SHANK_SCREEN_MAP_OPTION);
                    intent.putExtra(GameHandleConstant.IS_REDMAGIC_HANDLE_CLIENT, true);
                    intent.putExtra(GameHandleConstant.ACTION_HAND_ADDRESS, addressBig);
                    DataHandler.this.mContext.sendBroadcast(intent);
                    return;
                default:
                    return;
            }
        }
    };

    public DataHandler(Context context, BluetoothClient client) {
        this.mContext = context;
        this.mClient = client;
    }

    public void addDataReceiver(GameHandleDataReceiver receiver) {
        if (receiver != null && !this.mDataReceiver.contains(receiver)) {
            this.mDataReceiver.add(receiver);
        }
    }

    public void removeDataReceiver(GameHandleDataReceiver receiver) {
        if (receiver != null && this.mDataReceiver.contains(receiver)) {
            this.mDataReceiver.remove(receiver);
        }
    }

    /* access modifiers changed from: protected */
    public void handleReceiveData(byte[] data) {
        byte head = data[0];
        if (head == 1) {
            this.mHandler.sendMessage(Message.obtain(this.mHandler, 1));
        } else if (head == 2) {
            this.mHandler.sendMessage(Message.obtain(this.mHandler, 2, data[1], 0));
        } else if (head == 3) {
            this.mHandler.sendMessage(Message.obtain(this.mHandler, 3, data[1], 0));
        } else if (head == 4) {
            if (data[1] == 1) {
                LogUtil.d(TAG, "左手模式");
            } else if (data[1] == 2) {
                LogUtil.d(TAG, "右手模式");
            }
            this.mHandler.sendMessage(Message.obtain(this.mHandler, 5, data[1], 0));
        } else if (head == 5) {
            this.mHandler.sendEmptyMessage(4);
        }
    }
}
