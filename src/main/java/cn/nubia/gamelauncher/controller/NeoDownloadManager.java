package cn.nubia.gamelauncher.controller;

import android.content.Intent;
import android.util.Log;
import cn.nubia.gamelauncher.GameLauncherApplication;
import cn.nubia.gamelauncher.bean.NeoIconDownloadInfo;
import cn.nubia.gamelauncher.commoninterface.NeoGameDBColumns;
import cn.nubia.gamelauncher.model.AppAddModel;

public class NeoDownloadManager {
    private static final String ACTION_DELETE_DOWNLOAD = "cn.nubia.neostore.DELETE_DOWNLOAD";
    private static final String ACTION_GAME_DELETE_DOWNLOAD = "cn.nubia.neogamecenter.DELETE_DOWNLOAD";
    private static final String ACTION_GAME_PAUSE_DOWNLOAD = "cn.nubia.neogamecenter.PAUSE_DOWNLOAD";
    private static final String ACTION_GAME_RESUME_DOWNLOAD = "cn.nubia.neogamecenter.RESUME_DOWNLOAD";
    private static final String ACTION_PAUSE_DOWNLOAD = "cn.nubia.neostore.PAUSE_DOWNLOAD";
    private static final String ACTION_RESUME_DOWNLOAD = "cn.nubia.neostore.RESUME_DOWNLOAD";
    private static final String EXTRA_APP_ID = "app_id";
    private static final String GAME_PACKAGENAME = "cn.nubia.neogamecenter";
    private static final String PACKAGENAME = "cn.nubia.neostore";
    private static final NeoDownloadManager ourInstance = new NeoDownloadManager();

    public static NeoDownloadManager getInstance() {
        return ourInstance;
    }

    private NeoDownloadManager() {
    }

    public void doClick(int app_id) {
        NeoIconDownloadInfo info = AppAddModel.getInstance().getNeoDownloadHelper().getNeoDownloadInfoByAppId(app_id);
        Log.i("NeoDownloadManager", "doClick  info = " + info);
        if (info != null) {
            Intent intent = new Intent();
            if (info.type == NeoIconDownloadInfo.TYPE_APP_CENTER) {
                intent.setPackage(PACKAGENAME);
            } else if (info.type == NeoIconDownloadInfo.TYPE_GAME_CENTER) {
                intent.setPackage(GAME_PACKAGENAME);
            }
            intent.putExtra("app_id", app_id);
            if (info.status.equals(NeoGameDBColumns.STATUS_DOWNLOADING)) {
                if (info.type == NeoIconDownloadInfo.TYPE_GAME_CENTER) {
                    intent.setAction(ACTION_GAME_PAUSE_DOWNLOAD);
                } else if (info.type == NeoIconDownloadInfo.TYPE_APP_CENTER) {
                    intent.setAction(ACTION_PAUSE_DOWNLOAD);
                }
            } else if (!info.status.equals(NeoGameDBColumns.STATUS_PAUSE)) {
                return;
            } else {
                if (info.type == NeoIconDownloadInfo.TYPE_GAME_CENTER) {
                    intent.setAction(ACTION_GAME_RESUME_DOWNLOAD);
                } else if (info.type == NeoIconDownloadInfo.TYPE_APP_CENTER) {
                    intent.setAction(ACTION_RESUME_DOWNLOAD);
                }
            }
            try {
                GameLauncherApplication.CONTEXT.startService(intent);
            } catch (Exception e) {
                Log.i("lsm", "onclick,failed startService ", e);
            }
        }
    }
}
