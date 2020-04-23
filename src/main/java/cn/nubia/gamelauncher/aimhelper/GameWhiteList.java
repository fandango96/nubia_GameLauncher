package cn.nubia.gamelauncher.aimhelper;

import android.text.TextUtils;
import cn.nubia.gamelauncher.bean.AppListItemBean;
import cn.nubia.gamelauncher.util.CommonUtil;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameWhiteList {
    private static Set<String> ACTIVITIES = null;
    private static Set<String> ADAPTER_PACKAGES = new HashSet();
    private static Set<String> PACKAGES = new HashSet();
    private static final String TAG = "GameWhiteList";

    static {
        ACTIVITIES = new HashSet();
        PACKAGES.addAll(Arrays.asList(new String[]{"com.tencent.af", "com.tencent.tmgp.cf", "com.tencent.tmgp.pubgmhd", "com.tencent.ig"}));
        ADAPTER_PACKAGES.addAll(Arrays.asList(new String[]{"com.tencent.af", "com.tencent.tmgp.cf", "com.tencent.tmgp.pubgmhd", "com.tencent.ig"}));
        ACTIVITIES = new HashSet(Arrays.asList(new String[]{"com.tencent.af/com.tencent.af.AFActivity", "com.tencent.tmgp.cf/com.tencent.tmgp.cf.AFMainActivity", "com.tencent.tmgp.pubgmhd/com.epicgames.ue4.GameActivity", "com.tencent.ig/com.epicgames.ue4.GameActivity"}));
    }

    public static boolean isSupportGame(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        return PACKAGES.contains(packageName);
    }

    public static boolean isGameActivity(String activityName) {
        return true;
    }

    static void syncPackages(List<AppListItemBean> appsInGameLauncher) {
        PACKAGES.clear();
        PACKAGES.addAll(ADAPTER_PACKAGES);
        if (appsInGameLauncher != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("syncPackages=[");
            for (AppListItemBean bean : appsInGameLauncher) {
                sb.append(bean.getComponetName());
                PACKAGES.add(CommonUtil.convertPackageName(bean.getComponetName()));
            }
            sb.append("]");
            LogUtil.d(TAG, sb.toString());
        }
    }
}
