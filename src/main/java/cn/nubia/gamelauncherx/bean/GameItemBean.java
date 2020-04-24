package cn.nubia.gamelauncherx.bean;

import cn.nubia.gamelauncherx.util.LogUtil;
import org.json.JSONObject;

public class GameItemBean {
    private int mAppType;
    private String mPackageName;
    private int mSoftId;
    private String mSoftName;
    private String mUrl;

    public GameItemBean(JSONObject jsonObject) {
        try {
            this.mPackageName = jsonObject.getString("PackageName");
            this.mSoftId = jsonObject.getInt("SoftId");
            this.mAppType = jsonObject.getInt("AppType");
            this.mSoftName = jsonObject.getString("SoftName");
            this.mUrl = jsonObject.getString("Url");
        } catch (Exception e) {
            LogUtil.e("GameItemBean", "GameItemBean init Error!! " + e.fillInStackTrace());
        }
    }

    public GameItemBean(String mPackageName2, int mAppType2, String mUrl2) {
        this.mPackageName = mPackageName2;
        this.mAppType = mAppType2;
        this.mUrl = mUrl2;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public void setPackageName(String packageName) {
        this.mPackageName = packageName;
    }

    public int getSoftId() {
        return this.mSoftId;
    }

    public void setSoftId(int softId) {
        this.mSoftId = softId;
    }

    public int getAppType() {
        return this.mAppType;
    }

    public void setAppType(int appType) {
        this.mAppType = appType;
    }

    public String getSoftName() {
        return this.mSoftName;
    }

    public void setSoftName(String softName) {
        this.mSoftName = softName;
    }

    public String getUrl() {
        return this.mUrl;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public String toString() {
        return "GameItemBean{PackageName='" + this.mPackageName + '\'' + ", SoftId=" + this.mSoftId + ", AppType=" + this.mAppType + ", SoftName='" + this.mSoftName + '\'' + ", Url='" + this.mUrl + '\'' + '}';
    }
}
