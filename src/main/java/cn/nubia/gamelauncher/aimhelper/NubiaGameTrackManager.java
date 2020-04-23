package cn.nubia.gamelauncher.aimhelper;

import android.content.Context;
import android.provider.Settings.Global;

public class NubiaGameTrackManager {
    private static NubiaGameTrackManager instance = new NubiaGameTrackManager();
    private static Context sContext;

    public static NubiaGameTrackManager getInstance() {
        return instance;
    }

    public static void init(Context context) {
        if (sContext == null) {
            sContext = context;
        }
    }

    public static void updateValue(String packageName) {
        String data;
        int i = 0;
        if (sContext != null) {
            try {
                AimConfigs configs = AimConfigs.getInstance(sContext);
                int colorIndex = 1;
                int currColor = configs.getColor(packageName);
                int[] iArr = AimSettingView.colors;
                int length = iArr.length;
                while (i < length && iArr[i] != currColor) {
                    colorIndex++;
                    i++;
                }
                if (configs.isOn(packageName)) {
                    data = String.format("%d;%d;%d", new Object[]{Integer.valueOf(configs.getStyle(packageName)), Integer.valueOf(colorIndex), Integer.valueOf(configs.getSize(packageName))});
                } else {
                    data = "";
                }
                Global.putString(sContext.getContentResolver(), "zhunxing_helper_" + packageName, data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
