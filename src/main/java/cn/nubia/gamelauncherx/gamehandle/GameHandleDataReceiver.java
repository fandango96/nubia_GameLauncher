package cn.nubia.gamelauncherx.gamehandle;

public interface GameHandleDataReceiver {
    void onBatteryChange(String str, int i);

    void onCalibrateResult(String str, boolean z);

    void onReceiveData(String str, byte[] bArr);
}
