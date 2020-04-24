package cn.nubia.gamelauncherx.util;

import java.security.MessageDigest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class HTTPUtils {
    public static final String FORMAL_DOMAIN = "http://api.appstore.nubia.cn";
    private static final String GET_SOFT_BY_PACKAGE_NAME = "/RedMagic/GetSoftByPackageName";
    private static final String GET_SOFT_LIST_BY_PACKAGE_NAMES = "/RedMagic/GetSoftListByPackageNames";
    public static final String SECURITY_KEY = "GjWj8pc4bYq19NDkQ86fd6MtlW650Tki";
    public static final String TEST_DOMAIN = "http://store-api-test.nubia.cn";

    public static String sortParms(HashMap params) {
        Map<String, Object> treeMap = new TreeMap<>();
        treeMap.putAll(params);
        Set<String> keySet = treeMap.keySet();
        StringBuffer sb = new StringBuffer();
        for (String key : keySet) {
            Object value = treeMap.get(key);
            if (value instanceof List) {
                List<Object> list = (List) value;
                Collections.sort(list);
                for (Object object : list) {
                    sb.append(key).append("=").append(String.valueOf(object));
                }
            } else {
                sb.append(key).append("=").append(treeMap.get(key));
            }
        }
        return sb.toString();
    }

    public static String getDigestSign(String content) {
        return getDigestSign(content, SECURITY_KEY);
    }

    public static String getDigestSign(String content, String key) {
        if (content == null) {
            throw new UnsupportedOperationException();
        }
        try {
            String content2 = content + key;
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(content2.getBytes("utf-8"));
            return bytes2Hex(md.digest());
        } catch (Exception e) {
            return null;
        }
    }

    private static String bytes2Hex(byte[] bts) {
        String des = "";
        for (byte b : bts) {
            String tmp = Integer.toHexString(b & 255);
            if (tmp.length() == 1) {
                des = des + "0";
            }
            des = des + tmp;
        }
        return des;
    }

    public static String getSoftByPackageName() {
        return getDomainUrl() + GET_SOFT_BY_PACKAGE_NAME;
    }

    public static String getSoftListByPackageNames() {
        return getDomainUrl() + GET_SOFT_LIST_BY_PACKAGE_NAMES;
    }

    public static String getDomainUrl() {
        return FORMAL_DOMAIN;
    }
}
