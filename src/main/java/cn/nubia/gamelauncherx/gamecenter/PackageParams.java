package cn.nubia.gamelauncherx.gamecenter;

import cn.nubia.gamelauncherx.util.HTTPUtils;
import java.util.ArrayList;
import java.util.HashMap;

public class PackageParams {
    private static final String KEY_PACKAGE_NAME = "PackageName";
    private static final String KEY_PACKAGE_NAMES = "PackageNames";
    private static final String KEY_SIGN = "Sign";
    private static final String KEY_TIME = "Time";
    public static final int TYPE_PACKAGE_LIST_PARAMS = 200;
    public static final int TYPE_PACKAGE_PARAMS = 100;
    private String result;

    public static class Builder {
        private String mPackageName;
        private String mPackageNameList;
        private String mSign;
        private long mTime = -1;
        private int mType = -1;

        public Builder(int type) {
            this.mType = type;
        }

        public Builder setPackageName(String pName) {
            this.mPackageName = pName;
            return this;
        }

        public Builder setTime(long time) {
            this.mTime = time;
            return this;
        }

        public Builder setSign(String sign) {
            this.mSign = sign;
            return this;
        }

        public Builder setPackageList(ArrayList<String> list) {
            this.mPackageNameList = getListString(list);
            return this;
        }

        public String getPackageNameList() {
            return this.mPackageNameList;
        }

        public String toString() {
            checkInfos();
            String result = "";
            if (this.mType == 100) {
                return "?" + getString(PackageParams.KEY_PACKAGE_NAME, this.mPackageName) + "&" + getString(PackageParams.KEY_TIME, String.valueOf(this.mTime)) + "&" + getString(PackageParams.KEY_SIGN, this.mSign);
            }
            if (this.mType == 200) {
                return "?" + getString(PackageParams.KEY_PACKAGE_NAMES, this.mPackageNameList) + "&" + getString(PackageParams.KEY_TIME, String.valueOf(this.mTime)) + "&" + getString(PackageParams.KEY_SIGN, this.mSign);
            }
            return result;
        }

        private String getListString(ArrayList<String> list) {
            StringBuilder text = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                text.append("{\"PackageName\"");
                text.append(":");
                text.append("\"" + ((String) list.get(i)) + "\"}");
                if (i < list.size() - 1) {
                    text.append(",");
                }
            }
            text.append("]");
            return text.toString();
        }

        private String getString(String key, String value) {
            return key + "=" + value;
        }

        private void checkInfos() {
            if (this.mType == -1) {
                throw new RuntimeException("PackageParams type error!!");
            } else if (this.mType == 100) {
                if (this.mPackageName.isEmpty() || this.mSign.isEmpty() || this.mTime == -1) {
                    throw new RuntimeException("PackageParams error!!");
                }
            } else if (this.mType != 200) {
            } else {
                if (this.mPackageNameList.isEmpty() || this.mSign.isEmpty() || this.mTime == -1) {
                    throw new RuntimeException("PackageParams list error!!");
                }
            }
        }
    }

    public PackageParams(String pName) {
        this.result = createParams(pName);
    }

    public PackageParams(ArrayList<String> pNameList) {
        this.result = creatListParams(pNameList);
    }

    public String getParams() {
        return this.result;
    }

    private String createParams(String pName) {
        HashMap<String, String> map = new HashMap<>();
        long time = System.currentTimeMillis() / 1000;
        map.put(KEY_PACKAGE_NAME, pName);
        map.put(KEY_TIME, String.valueOf(time));
        map.put(KEY_SIGN, HTTPUtils.getDigestSign(HTTPUtils.sortParms(map)));
        return new Builder(100).setPackageName(pName).setTime(time).setSign((String) map.get(KEY_SIGN)).toString();
    }

    private String creatListParams(ArrayList<String> pNameList) {
        Builder builder = new Builder(200).setPackageList(pNameList);
        HashMap<String, String> map = new HashMap<>();
        long time = System.currentTimeMillis() / 1000;
        map.put(KEY_PACKAGE_NAMES, builder.getPackageNameList());
        map.put(KEY_TIME, String.valueOf(time));
        map.put(KEY_SIGN, HTTPUtils.getDigestSign(HTTPUtils.sortParms(map)));
        return builder.setTime(time).setSign((String) map.get(KEY_SIGN)).toString();
    }
}
