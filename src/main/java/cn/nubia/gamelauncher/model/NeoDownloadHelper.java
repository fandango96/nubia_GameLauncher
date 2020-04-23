package cn.nubia.gamelauncher.model;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import cn.nubia.gamelauncher.GameLauncherApplication;
import cn.nubia.gamelauncher.bean.AppListItemBean;
import cn.nubia.gamelauncher.bean.GameItemBean;
import cn.nubia.gamelauncher.bean.NeoIconDownloadInfo;
import cn.nubia.gamelauncher.bean.ResponseBean;
import cn.nubia.gamelauncher.commoninterface.ConstantVariable;
import cn.nubia.gamelauncher.commoninterface.INeoGameChangeListener;
import cn.nubia.gamelauncher.commoninterface.IRequestListener;
import cn.nubia.gamelauncher.gamecenter.BusinessRequestorImp;
import cn.nubia.gamelauncher.util.WorkThread;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class NeoDownloadHelper {
    public static final int CHANGE_ALL = -200;
    public static final String EXTPROVIDEROPERATION = "action";
    public static final String EXTPROVIDEROPERATION_DELETE = "delete";
    public static final String EXTPROVIDEROPERATION_INSERT = "insert";
    public static final String EXTPROVIDEROPERATION_UPDATE = "update";
    BusinessRequestorImp mBusinessRequestorImp = null;
    private INeoGameChangeListener mChangeListener = null;
    /* access modifiers changed from: private */
    public HashMap<Integer, NeoIconDownloadInfo> mNeoIconDownloadInfos = new HashMap<>();
    private NeoIconDownloadObserver mNeoIconObserver = null;
    /* access modifiers changed from: private */
    public ArrayList<NeoIconDownloadInfo> mPendingVerifyList = new ArrayList<>();

    private class NeoIconDownloadObserver extends ContentObserver {
        public NeoIconDownloadObserver(Handler handler) {
            super(handler);
        }

        public void onChange(boolean selfChange, Uri uri) {
            String action = uri.getQueryParameter(NeoDownloadHelper.EXTPROVIDEROPERATION);
            int app_id = (int) ContentUris.parseId(uri);
            if (NeoDownloadHelper.EXTPROVIDEROPERATION_INSERT.equals(action)) {
                NeoIconDownloadInfo info = NeoDownloadHelper.this.queryByAppId(uri, app_id);
                if (info != null) {
                    NeoDownloadHelper.this.verifyDownloadInfo(info);
                }
            } else if (NeoDownloadHelper.EXTPROVIDEROPERATION_DELETE.equals(action)) {
                if (NeoDownloadHelper.this.mNeoIconDownloadInfos.containsKey(Integer.valueOf(app_id))) {
                    NeoDownloadHelper.this.mNeoIconDownloadInfos.remove(Integer.valueOf(app_id));
                    NeoDownloadHelper.this.notifyChangeListener(NeoDownloadHelper.CHANGE_ALL);
                }
            } else if (NeoDownloadHelper.EXTPROVIDEROPERATION_UPDATE.equals(action)) {
                NeoIconDownloadInfo access$500 = NeoDownloadHelper.this.queryByAppId(uri, app_id);
                NeoDownloadHelper.this.notifyChangeListener(app_id);
            }
        }
    }

    public void setNeoGameChangeListener(INeoGameChangeListener listener) {
        this.mChangeListener = listener;
    }

    public void setBusinessRequestorImp(BusinessRequestorImp businessRequestorImp) {
        this.mBusinessRequestorImp = businessRequestorImp;
    }

    public void init() {
        WorkThread.runOnWorkThread(new Runnable() {
            public void run() {
                NeoDownloadHelper.this.mNeoIconDownloadInfos.clear();
                Cursor c = GameLauncherApplication.CONTEXT.getContentResolver().query(ConstantVariable.NEOExtendDB_URI, null, null, null, null);
                NeoDownloadHelper.this.mPendingVerifyList.clear();
                if (c != null) {
                    try {
                        if (c.moveToNext()) {
                            do {
                                NeoIconDownloadInfo info = new NeoIconDownloadInfo(c);
                                info.updateInfo(c, GameLauncherApplication.CONTEXT);
                                if (NeoDownloadHelper.this.isGameDownload(info)) {
                                    NeoDownloadHelper.this.addNeoIconDownloadInfo(info);
                                } else {
                                    NeoDownloadHelper.this.mPendingVerifyList.add(info);
                                }
                            } while (c.moveToNext());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (c != null) {
                            c.close();
                        }
                    } catch (Throwable th) {
                        if (c != null) {
                            c.close();
                        }
                        throw th;
                    }
                }
                if (c != null) {
                    c.close();
                }
                if (NeoDownloadHelper.this.mPendingVerifyList.size() > 0) {
                    ArrayList<String> packageList = new ArrayList<>();
                    Iterator it = NeoDownloadHelper.this.mPendingVerifyList.iterator();
                    while (it.hasNext()) {
                        packageList.add(((NeoIconDownloadInfo) it.next()).packageName);
                    }
                    NeoDownloadHelper.this.mBusinessRequestorImp.getApplicationsByPackageNames(GameLauncherApplication.CONTEXT, packageList, new IRequestListener() {
                        public void responseInfo(ResponseBean bean) {
                            if (!(bean == null || bean.getGameItemBean() == null || bean.getGameItemBean().size() <= 0)) {
                                Iterator it = bean.getGameItemBean().iterator();
                                while (it.hasNext()) {
                                    GameItemBean gameItemBean = (GameItemBean) it.next();
                                    if (gameItemBean != null && gameItemBean.getAppType() == ConstantVariable.APP_TYPE_GAME) {
                                        NeoDownloadHelper.this.addNeoIconDownloadInfo(NeoDownloadHelper.this.findDownloadInfoInList(gameItemBean.getPackageName(), NeoDownloadHelper.this.mPendingVerifyList));
                                    }
                                }
                            }
                            NeoDownloadHelper.this.mPendingVerifyList.clear();
                            if (NeoDownloadHelper.this.mNeoIconDownloadInfos.size() > 0) {
                                NeoDownloadHelper.this.notifyChangeListener(NeoDownloadHelper.CHANGE_ALL);
                            }
                        }

                        public void responseError(String errorMsg) {
                            NeoDownloadHelper.this.mPendingVerifyList.clear();
                            if (NeoDownloadHelper.this.mNeoIconDownloadInfos.size() > 0) {
                                NeoDownloadHelper.this.notifyChangeListener(NeoDownloadHelper.CHANGE_ALL);
                            }
                        }
                    });
                }
                if (NeoDownloadHelper.this.mNeoIconDownloadInfos.size() > 0 && NeoDownloadHelper.this.mPendingVerifyList.size() == 0) {
                    NeoDownloadHelper.this.notifyChangeListener(NeoDownloadHelper.CHANGE_ALL);
                }
                Log.i("lsm", "NeoDownloadHelper init NeoIconDownloadInfo mNeoIconDownloadInfos == " + NeoDownloadHelper.this.mNeoIconDownloadInfos);
            }
        });
        registerObserver();
    }

    public void end() {
        if (this.mNeoIconObserver != null) {
            unRegisterObserver();
        }
    }

    /* access modifiers changed from: 0000 */
    public NeoIconDownloadInfo findDownloadInfoInList(String packageName, ArrayList<NeoIconDownloadInfo> list) {
        if (list != null && list.size() > 0) {
            Iterator it = list.iterator();
            while (it.hasNext()) {
                NeoIconDownloadInfo info = (NeoIconDownloadInfo) it.next();
                if (info.packageName.equals(packageName)) {
                    return info;
                }
            }
        }
        return null;
    }

    private void registerObserver() {
        try {
            ContentResolver cr = GameLauncherApplication.CONTEXT.getContentResolver();
            Uri uri = ConstantVariable.NEOExtendDB_URI;
            NeoIconDownloadObserver neoIconDownloadObserver = new NeoIconDownloadObserver(WorkThread.getHandler());
            this.mNeoIconObserver = neoIconDownloadObserver;
            cr.registerContentObserver(uri, true, neoIconDownloadObserver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: private */
    public NeoIconDownloadInfo queryByAppId(Uri uri, int appId) {
        NeoIconDownloadInfo info = (NeoIconDownloadInfo) this.mNeoIconDownloadInfos.get(Integer.valueOf(appId));
        Uri uri2 = uri;
        Cursor c = GameLauncherApplication.CONTEXT.getContentResolver().query(uri2, null, "app_id=?", new String[]{"" + appId}, null);
        try {
            if (c.moveToNext()) {
                if (info == null) {
                    info = new NeoIconDownloadInfo(c);
                }
                info.updateInfo(c, GameLauncherApplication.CONTEXT);
            }
            if (c != null) {
                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (c != null) {
                c.close();
            }
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
            throw th;
        }
        return info;
    }

    /* access modifiers changed from: private */
    public void addNeoIconDownloadInfo(NeoIconDownloadInfo info) {
        if (AppAddModel.getInstance().isAppExitsInSystem(info.packageName)) {
            Log.i("lsm", "addNeoIconDownloadInfo isAppExitsInSystem packageName ==  " + info.packageName);
            return;
        }
        this.mNeoIconDownloadInfos.put(Integer.valueOf(info.app_id), info);
        info.appListItemBean = constructAppListItemBean(info);
    }

    public NeoIconDownloadInfo getNeoDownloadInfoByAppId(int appId) {
        return (NeoIconDownloadInfo) this.mNeoIconDownloadInfos.get(Integer.valueOf(appId));
    }

    public AppListItemBean getAppListItemBeanByAppId(int appId) {
        if (this.mNeoIconDownloadInfos.get(Integer.valueOf(appId)) != null) {
            return ((NeoIconDownloadInfo) this.mNeoIconDownloadInfos.get(Integer.valueOf(appId))).appListItemBean;
        }
        return null;
    }

    public ArrayList<AppListItemBean> getNeoDownloadAppList() {
        if (this.mNeoIconDownloadInfos.size() <= 0) {
            return null;
        }
        ArrayList<AppListItemBean> list = new ArrayList<>();
        for (Integer intValue : this.mNeoIconDownloadInfos.keySet()) {
            NeoIconDownloadInfo info = (NeoIconDownloadInfo) this.mNeoIconDownloadInfos.get(Integer.valueOf(intValue.intValue()));
            if (info != null) {
                if (info.appListItemBean == null) {
                    info.appListItemBean = constructAppListItemBean(info);
                }
                list.add(info.appListItemBean);
            }
        }
        return list;
    }

    public void unRegisterObserver() {
        try {
            GameLauncherApplication.CONTEXT.getContentResolver().unregisterContentObserver(this.mNeoIconObserver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: 0000 */
    public void verifyDownloadInfo(final NeoIconDownloadInfo info) {
        if (isGameDownload(info)) {
            addNeoIconDownloadInfo(info);
            notifyChangeListener(CHANGE_ALL);
            return;
        }
        this.mBusinessRequestorImp.getApplicationByPackageName(GameLauncherApplication.CONTEXT, info.packageName, new IRequestListener() {
            public void responseInfo(ResponseBean bean) {
                if (bean != null && bean.getStateCode() == ConstantVariable.STATE_CODE_SUCESS && bean.getGameItemBean() != null && bean.getGameItemBean().size() > 0) {
                    GameItemBean gameItemBean = (GameItemBean) bean.getGameItemBean().get(0);
                    if (gameItemBean != null && gameItemBean.getAppType() == ConstantVariable.APP_TYPE_GAME) {
                        NeoDownloadHelper.this.addNeoIconDownloadInfo(info);
                        NeoDownloadHelper.this.notifyChangeListener(NeoDownloadHelper.CHANGE_ALL);
                    }
                }
            }

            public void responseError(String errorMsg) {
            }
        });
    }

    private AppListItemBean constructAppListItemBean(NeoIconDownloadInfo info) {
        if (info == null) {
            return null;
        }
        AppListItemBean bean = new AppListItemBean(info.icon, info.title, info.packageName + ",Neo", false, null);
        bean.setDownloadInfo(info);
        return bean;
    }

    /* access modifiers changed from: private */
    public void notifyChangeListener(int app_id) {
        if (this.mChangeListener != null) {
            this.mChangeListener.onNeoDownloadGameChange(app_id);
        }
    }

    /* access modifiers changed from: private */
    public boolean isGameDownload(NeoIconDownloadInfo info) {
        if (info != null) {
            String str = info.packageName;
            if (isInLocalGameList(info.packageName) || info.type == NeoIconDownloadInfo.TYPE_GAME_CENTER) {
                return true;
            }
        }
        return false;
    }

    public boolean isInLocalGameList(String packageName) {
        for (String gameName : ConstantVariable.LOCAL_GAME_IMAGE_MAP.keySet()) {
            if (gameName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
}
