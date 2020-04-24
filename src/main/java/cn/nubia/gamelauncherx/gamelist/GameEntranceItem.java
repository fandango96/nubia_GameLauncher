package cn.nubia.gamelauncherx.gamelist;

import android.graphics.Bitmap;
import cn.nubia.gamelauncherx.bean.NeoIconDownloadInfo;

public class GameEntranceItem {
    private String comName = "";
    private Bitmap image = null;
    public NeoIconDownloadInfo info;
    private boolean isDownloadItem = false;
    private String name = "";

    public GameEntranceItem(String name2, String comName2, Bitmap image2, NeoIconDownloadInfo info2) {
        boolean z = false;
        this.image = image2;
        this.comName = comName2;
        this.name = name2;
        if (info2 != null) {
            z = true;
        }
        this.isDownloadItem = z;
        this.info = info2;
    }

    public Bitmap getImage() {
        return this.image;
    }

    public String getName() {
        return this.name;
    }

    public String getComponetName() {
        return this.comName;
    }

    public boolean isDownloadItem() {
        return this.isDownloadItem;
    }
}
