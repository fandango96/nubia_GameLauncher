package cn.nubia.gamelauncher.bean;

import android.graphics.Bitmap;

public class AppListItemBean {
    private String componentName;
    private NeoIconDownloadInfo downloadInfo;
    public Bitmap icon;
    private boolean isDownloadItem;
    private boolean isGame;
    private String mImageUrl;
    private String mUpdateTime;
    public String name;
    public boolean select;

    public AppListItemBean(String name2, String componentName2, String imageUrl) {
        this(null, name2, componentName2, false, imageUrl);
    }

    public AppListItemBean(Bitmap icon2, String name2, String componentName2, boolean select2, String imageUrl) {
        this.isGame = false;
        this.isDownloadItem = false;
        this.icon = icon2;
        this.name = name2;
        this.select = select2;
        this.componentName = componentName2;
        this.mImageUrl = imageUrl;
    }

    public boolean isGame() {
        return this.isGame;
    }

    public void setGame(boolean game) {
        this.isGame = game;
    }

    public String getComponetName() {
        return this.componentName;
    }

    public void setComponentName(String componentName2) {
        this.componentName = componentName2;
    }

    public Bitmap getIcon() {
        return this.icon;
    }

    public void setIcon(Bitmap icon2) {
        this.icon = icon2;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public boolean isSelect() {
        return this.select;
    }

    public void setSelect(boolean select2) {
        this.select = select2;
    }

    public String getImageUrl() {
        return this.mImageUrl;
    }

    public void setImageUrl(String mImageUrl2) {
        this.mImageUrl = mImageUrl2;
    }

    public String getUpdateTime() {
        return this.mUpdateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.mUpdateTime = updateTime;
    }

    public boolean isDownloadItem() {
        boolean z = this.downloadInfo != null;
        this.isDownloadItem = z;
        return z;
    }

    public NeoIconDownloadInfo getDownloadInfo() {
        return this.downloadInfo;
    }

    public void setDownloadInfo(NeoIconDownloadInfo downloadInfo2) {
        this.downloadInfo = downloadInfo2;
    }

    public String toString() {
        return "AppListItemBean{icon=" + this.icon + ", name='" + this.name + '\'' + ", select=" + this.select + ", componentName='" + this.componentName + '\'' + ", mImageUrl='" + this.mImageUrl + '\'' + ", mUpdateTime='" + this.mUpdateTime + '\'' + '}';
    }
}
