package cn.nubia.gamelauncherx.bean;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import cn.nubia.gamelauncherx.R;
import cn.nubia.gamelauncherx.commoninterface.NeoGameDBColumns;
import cn.nubia.gamelauncherx.util.BitmapUtils;

public class NeoIconDownloadInfo {
    public static int TYPE_APP_CENTER = 1;
    public static int TYPE_GAME_CENTER = 2;
    public AppListItemBean appListItemBean;
    public int app_id;
    private byte[] data;
    private boolean hasUpdateIcon;
    public Bitmap icon;
    private int id;
    private boolean isNeedUpdateIcon = true;
    public int mission_id;
    public String packageName;
    public int process;
    public Bitmap processIcon;
    public String status;
    public String title;
    public int type;

    public NeoIconDownloadInfo(Cursor c) {
        this.id = c.getInt(c.getColumnIndex(NeoGameDBColumns._ID));
        this.app_id = c.getInt(c.getColumnIndex(NeoGameDBColumns.APP_ID));
        this.mission_id = c.getInt(c.getColumnIndex(NeoGameDBColumns.MISSION_ID));
        this.title = c.getString(c.getColumnIndex(NeoGameDBColumns.TITLE));
        this.title = this.title != null ? this.title : "";
        this.packageName = c.getString(c.getColumnIndex(NeoGameDBColumns.PACKAGENAME));
        this.type = c.getInt(c.getColumnIndex(NeoGameDBColumns.TYPE));
        this.process = c.getInt(c.getColumnIndex(NeoGameDBColumns.PROCESS));
        this.status = c.getString(c.getColumnIndex("status"));
    }

    public void updateInfo(Cursor c, Context context) {
        int oldProcess = this.process;
        this.process = c.getInt(c.getColumnIndex(NeoGameDBColumns.PROCESS));
        if (this.process < oldProcess) {
            this.process = oldProcess;
        }
        this.status = c.getString(c.getColumnIndex("status"));
        this.data = c.getBlob(c.getColumnIndex(NeoGameDBColumns.ICON));
        updateIcon(context);
    }

    private void updateIcon(Context context) {
        if (this.data == null && this.icon == null) {
            this.icon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.default_neoicon);
            this.data = BitmapUtils.flattenBitmap(this.icon);
            Log.i("lsm", "updateIcon data == null && icon == null");
        } else if (!this.hasUpdateIcon && this.data != null) {
            this.hasUpdateIcon = true;
            this.icon = BitmapFactory.decodeByteArray(this.data, 0, this.data.length);
            if (this.icon == null) {
                this.icon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.default_neoicon);
                this.data = BitmapUtils.flattenBitmap(this.icon);
                Log.i("lsm", "updateIcon show default data == null && icon == null");
            }
        }
        if (this.process >= 99) {
            Log.i("lsm", "updateIcon process >= 99  info = " + this);
        }
        this.icon = BitmapUtils.createBitmapWithProcess(this.icon, 100.0f);
        this.processIcon = BitmapUtils.createBitmapWithProcess(this.icon, (float) this.process);
    }

    public boolean isNeedUpdateIcon() {
        if (!this.hasUpdateIcon || !this.isNeedUpdateIcon) {
            return false;
        }
        this.isNeedUpdateIcon = false;
        return true;
    }

    public String toString() {
        return "id=" + this.id + " ,app_id=" + this.app_id + " ,mission_id=" + this.mission_id + " ,title=" + this.title + " ,packageName=" + this.packageName + " ,process=" + this.process + " ,status=" + this.status + " ,hasUpdateIcon=" + this.hasUpdateIcon + " ,type=" + this.type;
    }
}
