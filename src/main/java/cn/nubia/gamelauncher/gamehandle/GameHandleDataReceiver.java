package cn.nubia.gamelauncher.gamehandle;

public interface GameHandleDataReceiver {
    void onBatteryChange(String str, int i);

    void onCalibrateResult(String str, boolean z);

    void onReceiveData(String str, byte[] bArr);
}
