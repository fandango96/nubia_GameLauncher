package cn.nubia.gamelauncherx.model;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.LauncherApps.Callback;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import cn.nubia.gamelauncherx.GameLauncherApplication;
import cn.nubia.gamelauncherx.bean.AppListItemBean;
import cn.nubia.gamelauncherx.bean.GameItemBean;
import cn.nubia.gamelauncherx.bean.ResponseBean;
import cn.nubia.gamelauncherx.commoninterface.ConstantVariable;
import cn.nubia.gamelauncherx.commoninterface.IGetAppStatusDataCallBack;
import cn.nubia.gamelauncherx.commoninterface.IGetPackageIsAutoAddGame;
import cn.nubia.gamelauncherx.commoninterface.INeoGameChangeListener;
import cn.nubia.gamelauncherx.commoninterface.IOnAppAddedListener;
import cn.nubia.gamelauncherx.commoninterface.IRequestListener;
import cn.nubia.gamelauncherx.db.AppAddProvider;
import cn.nubia.gamelauncherx.gamecenter.BusinessRequestorImp;
import cn.nubia.gamelauncherx.recycler.BannerManager;
import cn.nubia.gamelauncherx.util.CommonUtil;
import cn.nubia.gamelauncherx.util.PinYinSortUtil;
import cn.nubia.gamelauncherx.util.WorkThread;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AppAddModel implements IOnAppAddedListener, INeoGameChangeListener {
    private static final String CUSTOM_IMAGE_DIR_NAME = "custom_image";
    private static AppAddModel sAppAddModel = null;
    AppAddModelHelper mAppAddModelHelper;
    /* access modifiers changed from: private */
    public ArrayList<AppListItemBean> mAppAddedList;
    /* access modifiers changed from: private */
    public ArrayList<AppListItemBean> mAppNotAddedList;
    BusinessRequestorImp mBusinessRequestorImp;
    /* access modifiers changed from: private */
    public ArrayList<IGetAppStatusDataCallBack> mCallbackList;
    /* access modifiers changed from: private */
    public Context mContex;
    LauncherApps mLauncherApps;
    /* access modifiers changed from: private */
    public boolean mLoadAllAppListDone;
    private Handler mMainHandler;
    /* access modifiers changed from: private */
    public volatile boolean mNeedCallback;
    private ArrayList<AppListItemBean> mNeoDownloadAppItemList;
    NeoDownloadHelper mNeoDownloadHelper;
    PackageChangedCallback mPackageChangedCallback;

    static class PackageChangedCallback extends Callback {
        PackageChangedCallback() {
        }

        public void onPackageRemoved(String packageName, UserHandle user) {
            Log.i("lsm", "onPackageRemoved packageName == " + packageName);
            AppAddModel.getInstance().doRemoveBusinessByPackName(packageName);
        }

        public void onPackageAdded(String packageName, UserHandle user) {
            Log.i("lsm", "onPackageAdded packageName == " + packageName);
            AppAddModel.getInstance().doPackageAddBusinessByPackName(packageName);
        }

        public void onPackageChanged(String packageName, UserHandle user) {
            Log.i("lsm", "onPackageChanged packageName == " + packageName);
            AppAddModel.getInstance().doPackageUpdateBusinessByPackName(packageName);
        }

        public void onPackagesAvailable(String[] packageNames, UserHandle user, boolean replacing) {
        }

        public void onPackagesUnavailable(String[] packageNames, UserHandle user, boolean replacing) {
        }
    }

    private AppAddModel() {
        this.mAppAddedList = null;
        this.mAppNotAddedList = null;
        this.mNeoDownloadAppItemList = null;
        this.mCallbackList = new ArrayList<>();
        this.mLoadAllAppListDone = false;
        this.mNeedCallback = false;
        this.mMainHandler = null;
        this.mLauncherApps = null;
        this.mPackageChangedCallback = null;
        this.mBusinessRequestorImp = null;
        this.mAppAddModelHelper = null;
        this.mNeoDownloadHelper = null;
        this.mMainHandler = new Handler(Looper.getMainLooper());
        this.mBusinessRequestorImp = new BusinessRequestorImp();
        this.mAppAddModelHelper = new AppAddModelHelper();
        this.mNeoDownloadHelper = new NeoDownloadHelper();
        this.mNeoDownloadHelper.setBusinessRequestorImp(this.mBusinessRequestorImp);
    }

    public static AppAddModel getInstance() {
        if (sAppAddModel == null) {
            synchronized (AppAddModel.class) {
                sAppAddModel = new AppAddModel();
            }
        }
        return sAppAddModel;
    }

    public void init(Context context) {
        this.mContex = context;
        this.mAppAddModelHelper.initMaxIdInTable();
        initLauncherApps();
        loadAllAppList();
        this.mNeoDownloadHelper.init();
        this.mNeoDownloadHelper.setNeoGameChangeListener(this);
        this.mCallbackList.clear();
    }

    /* access modifiers changed from: 0000 */
    public void initLauncherApps() {
        WorkThread.runOnWorkThread(new Runnable() {
            public void run() {
                try {
                    AppAddModel.this.mLauncherApps = (LauncherApps) AppAddModel.this.mContex.getSystemService("launcherapps");
                    LauncherApps launcherApps = AppAddModel.this.mLauncherApps;
                    AppAddModel appAddModel = AppAddModel.this;
                    PackageChangedCallback packageChangedCallback = new PackageChangedCallback();
                    appAddModel.mPackageChangedCallback = packageChangedCallback;
                    launcherApps.registerCallback(packageChangedCallback);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void resisterGetAppStatusDataCallBack(IGetAppStatusDataCallBack callBack) {
        this.mCallbackList.add(callBack);
    }

    public void unResisterGetAppStatusDataCallBack(IGetAppStatusDataCallBack callBack) {
        this.mCallbackList.remove(callBack);
    }

    public void loadAllAppList() {
        WorkThread.runOnWorkThread(new Runnable() {
            public void run() {
                AppAddModel.this.mAppAddedList = AppAddModel.this.getAddedAppListFromDB();
                AppAddModel.this.getNotAddedAppListFromSys();
                AppAddModel.this.mLoadAllAppListDone = true;
                if (AppAddModel.this.mNeedCallback) {
                    AppAddModel.this.notifyChangedData();
                } else {
                    AppAddModel.this.sortListData();
                }
                AppAddModel.this.verifyNotAddGameApp();
            }
        });
    }

    /* access modifiers changed from: private */
    public ArrayList<AppListItemBean> getAddedAppListFromDB() {
        ArrayList<AppListItemBean> listItemBeans = new ArrayList<>();
        PackageManager pm = GameLauncherApplication.CONTEXT.getPackageManager();
        Cursor cursor = GameLauncherApplication.CONTEXT.getContentResolver().query(ConstantVariable.APPADD_URI, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    String imageUrl = cursor.getString(cursor.getColumnIndex(AppAddProvider.APPADD_IMAGE_URL));
                    boolean isAdd = cursor.getInt(cursor.getColumnIndex(AppAddProvider.APPADD_ISADD)) == 1;
                    boolean isGame = cursor.getInt(cursor.getColumnIndex(AppAddProvider.APPADD_ISGAME)) == 1;
                    String component = cursor.getString(cursor.getColumnIndex(AppAddProvider.APPADD_COMPONENT));
                    String name = cursor.getString(cursor.getColumnIndex(AppAddProvider.APPADD_NAME));
                    Bitmap icon = null;
                    boolean isEnable = false;
                    boolean change = true;
                    try {
                        String newName = pm.getApplicationInfo(CommonUtil.convertPackageName(component), 0).loadLabel(pm).toString();
                        if (!name.equals(newName)) {
                            name = newName;
                            change = true;
                        }
                        icon = CommonUtil.drawableToBitmap(pm.getActivityIcon(CommonUtil.createComponentName(component)));
                        isEnable = this.mLauncherApps.isActivityEnabled(CommonUtil.createComponentName(component), Process.myUserHandle()) && isLauncherMainActivity(CommonUtil.createComponentName(component));
                    } catch (Exception e) {
                        Log.i("lsm", "getAddedAppListFromDB ", e);
                    }
                    AppListItemBean bean = new AppListItemBean(icon, name, component, isAdd, imageUrl);
                    bean.setGame(isGame);
                    updateLocalGameItemBeanIfNeed(bean);
                    if (isEnable) {
                        listItemBeans.add(bean);
                        if (change) {
                            updateAppItemBeanInAppAddDB(bean);
                        }
                    } else {
                        ArrayList<AppListItemBean> list = new ArrayList<>();
                        list.add(bean);
                        this.mAppAddModelHelper.deleteAppItemBeanInAppAddDB(list);
                        Log.i("lsm", "getAddedAppListFromDB  component =  " + component + " isEnable == " + isEnable + " deleteAppItemBeanInDB list   ==   " + list);
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                    if (cursor != null) {
                        cursor.close();
                    }
                } catch (Throwable th) {
                    if (cursor != null) {
                        cursor.close();
                    }
                    throw th;
                }
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        updateGameBeanImageUrl(listItemBeans);
        Log.i("lsm", "getAddedAppListFromDB listItemBeans  ==   " + listItemBeans);
        return listItemBeans;
    }

    private boolean isLauncherMainActivity(ComponentName cn2) {
        if (cn2 != null) {
            List<LauncherActivityInfo> apps = this.mLauncherApps.getActivityList(cn2.getPackageName(), Process.myUserHandle());
            for (int i = 0; i < apps.size(); i++) {
                if (cn2.equals(((LauncherActivityInfo) apps.get(i)).getComponentName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void updateGameBeanImageUrl(final ArrayList<AppListItemBean> listItemBeans) {
        WorkThread.runOnWorkThread(new Runnable() {
            public void run() {
                if (listItemBeans != null && listItemBeans.size() > 0) {
                    AppAddModel.this.mBusinessRequestorImp.getApplicationsByPackageNames(GameLauncherApplication.CONTEXT, AppAddModel.this.mAppAddModelHelper.getWillVerifyPackageList(listItemBeans), new IRequestListener() {
                        @TargetApi(8)
                        public void responseInfo(ResponseBean bean) {
                            Log.i("lsm", "updateGameBeanImageUrl responseInfo == " + bean);
                            if (bean != null && bean.getStateCode() == ConstantVariable.STATE_CODE_SUCESS) {
                                ArrayList<GameItemBean> list = bean.getGameItemBean();
                                Log.i("lsm", "updateGameBeanImageUrl GameItemBean  list == " + list);
                                if (list != null && list.size() > 0) {
                                    new ArrayList();
                                    Iterator it = list.iterator();
                                    while (it.hasNext()) {
                                        GameItemBean gameItemBean = (GameItemBean) it.next();
                                        if (gameItemBean.getAppType() == ConstantVariable.APP_TYPE_GAME && gameItemBean.getPackageName() != null && !TextUtils.isEmpty(gameItemBean.getUrl())) {
                                            AppListItemBean findBean = null;
                                            Iterator it2 = listItemBeans.iterator();
                                            while (true) {
                                                if (!it2.hasNext()) {
                                                    break;
                                                }
                                                AppListItemBean appBean = (AppListItemBean) it2.next();
                                                if (gameItemBean.getPackageName().equals(CommonUtil.convertPackageName(appBean.getComponetName()))) {
                                                    findBean = appBean;
                                                    break;
                                                }
                                            }
                                            if (findBean != null && !findBean.getImageUrl().equals(gameItemBean.getUrl()) && !findBean.getImageUrl().contains(AppAddModel.this.mContex.getExternalFilesDir(AppAddModel.CUSTOM_IMAGE_DIR_NAME).getAbsolutePath())) {
                                                findBean.setImageUrl(gameItemBean.getUrl());
                                                Log.i("lsm", "updateGameBeanImageUrl update find bean ==" + findBean);
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        public void responseError(String errorMsg) {
                            Log.i("lsm", "updateGameBeanImageUrl  responseError  updateGameBeanImageUrl");
                        }
                    });
                }
            }
        });
    }

    @TargetApi(8)
    private void updateLocalGameItemBeanIfNeed(AppListItemBean bean) {
        String imageUrl = bean.getImageUrl();
        String packageName = CommonUtil.convertPackageName(bean.getComponetName());
        if (this.mAppAddModelHelper.isInLocalGameList(packageName)) {
            String localImageUrl = (String) ConstantVariable.LOCAL_GAME_IMAGE_MAP.get(packageName);
            if (!isLocalUrlAndDBUrlTheSameValue(imageUrl, localImageUrl)) {
                Log.i("lsm", "updateLocalGameItemBeanIfNeed() update local game url localImageUrl = " + localImageUrl + ", imageUrl = " + imageUrl);
                if (imageUrl == null || !imageUrl.contains(this.mContex.getExternalFilesDir(CUSTOM_IMAGE_DIR_NAME).getAbsolutePath())) {
                    bean.setImageUrl(localImageUrl);
                } else {
                    bean.setImageUrl(imageUrl);
                }
                this.mAppAddModelHelper.updateAppItemBeanInAppAddDB(bean);
            }
        }
    }

    public void updateAppItemBeanInAppAddDB(AppListItemBean bean) {
        this.mAppAddModelHelper.updateAppItemBeanInAppAddDB(bean);
    }

    private boolean isLocalUrlAndDBUrlTheSameValue(String DBUrl, String localUrl) {
        if (localUrl == null) {
            return DBUrl == null;
        }
        return localUrl.equals(DBUrl);
    }

    public ArrayList<AppListItemBean> getAppAddedList() {
        if (this.mLoadAllAppListDone) {
            return this.mAppAddedList;
        }
        this.mNeedCallback = true;
        Log.i("lsm", "getAppAddedList null NeedCallback");
        return null;
    }

    public ArrayList<AppListItemBean> getAppNotAddList() {
        if (this.mLoadAllAppListDone) {
            return this.mAppNotAddedList;
        }
        this.mNeedCallback = true;
        Log.i("lsm", "getAppNotAddList null NeedCallback ");
        return null;
    }

    /* access modifiers changed from: private */
    public ArrayList<AppListItemBean> getNotAddedAppListFromSys() {
        PackageManager packageManager = this.mContex.getPackageManager();
        Intent mainIntent = new Intent("android.intent.action.MAIN", null);
        mainIntent.addCategory("android.intent.category.LAUNCHER");
        return convertToListViewItemBean(packageManager.queryIntentActivities(mainIntent, 0), packageManager);
    }

    private ArrayList<AppListItemBean> convertToListViewItemBean(List<ResolveInfo> list, PackageManager pm) {
        if (this.mAppNotAddedList == null) {
            this.mAppNotAddedList = new ArrayList<>();
        }
        if (list == null) {
            return null;
        }
        for (ResolveInfo info : list) {
            String componentName = info.activityInfo.packageName + "," + info.activityInfo.name;
            if (!this.mAppAddModelHelper.isInSystemAppList(info.activityInfo.packageName) && !isExistInAppAddedList(componentName)) {
                this.mAppNotAddedList.add(new AppListItemBean(CommonUtil.drawableToBitmap(info.loadIcon(pm)), info.loadLabel(pm).toString(), componentName, false, ""));
            }
        }
        Log.i("lsm", "convertToListViewItemBean == " + this.mAppNotAddedList);
        return this.mAppNotAddedList;
    }

    public boolean isAppExitsInSystem(String packageName) {
        if (this.mAppAddedList != null && this.mAppAddedList.size() > 0) {
            Iterator it = this.mAppAddedList.iterator();
            while (it.hasNext()) {
                if (CommonUtil.convertPackageName(((AppListItemBean) it.next()).getComponetName()).equals(packageName)) {
                    return true;
                }
            }
        }
        if (this.mAppNotAddedList != null && this.mAppNotAddedList.size() > 0) {
            Iterator it2 = this.mAppNotAddedList.iterator();
            while (it2.hasNext()) {
                if (CommonUtil.convertPackageName(((AppListItemBean) it2.next()).getComponetName()).equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    public boolean isExistInAppAddedList(String component) {
        if (this.mAppAddedList != null && this.mAppAddedList.size() > 0) {
            Iterator it = this.mAppAddedList.iterator();
            while (it.hasNext()) {
                if (((AppListItemBean) it.next()).getComponetName().equals(component)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void doRemoveBusinessByPackName(final String packageName) {
        WorkThread.runOnWorkThread(new Runnable() {
            public void run() {
                ArrayList<AppListItemBean> addListRemoveApps = AppAddModel.this.mAppAddModelHelper.getListBeanByPackName(packageName, AppAddModel.this.mAppAddedList);
                if (addListRemoveApps != null && addListRemoveApps.size() > 0) {
                    AppAddModel.this.mAppAddedList.removeAll(addListRemoveApps);
                    Log.i("lsm", " mAppAddedList == " + AppAddModel.this.mAppAddedList);
                    AppAddModel.this.mAppAddModelHelper.deleteAppItemBeanInAppAddDB(addListRemoveApps);
                    AppAddModel.this.mAppAddModelHelper.deleteAppItemBeanInUserRemoveDB(addListRemoveApps);
                }
                ArrayList<AppListItemBean> notAddListRemoveApps = AppAddModel.this.mAppAddModelHelper.getListBeanByPackName(packageName, AppAddModel.this.mAppNotAddedList);
                if (notAddListRemoveApps != null && notAddListRemoveApps.size() > 0) {
                    AppAddModel.this.mAppNotAddedList.removeAll(notAddListRemoveApps);
                    AppAddModel.this.mAppAddModelHelper.deleteAppItemBeanInAppAddDB(notAddListRemoveApps);
                    AppAddModel.this.mAppAddModelHelper.deleteAppItemBeanInUserRemoveDB(notAddListRemoveApps);
                }
                AppAddModel.this.notifyChangedData();
            }
        });
    }

    /* access modifiers changed from: private */
    public void notifyChangedData() {
        final ArrayList<AppListItemBean> list = new ArrayList<>();
        sortListData();
        list.addAll(this.mAppAddedList);
        list.addAll(this.mAppNotAddedList);
        this.mMainHandler.post(new Runnable() {
            public void run() {
                if (AppAddModel.this.mCallbackList != null && AppAddModel.this.mCallbackList.size() > 0) {
                    Iterator it = AppAddModel.this.mCallbackList.iterator();
                    while (it.hasNext()) {
                        ((IGetAppStatusDataCallBack) it.next()).onLoadAddAppListDone(list, AppAddModel.this.mAppAddedList.size());
                    }
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void sortListData() {
        PinYinSortUtil.sortByPinYinFirstChar(this.mAppAddedList);
        PinYinSortUtil.sortByPinYinFirstChar(this.mAppNotAddedList);
    }

    /* access modifiers changed from: private */
    public void verifyNotAddGameApp() {
        Log.i("lsm", "verifyGameApp  begin");
        WorkThread.runOnWorkThread(new Runnable() {
            public void run() {
                if (AppAddModel.this.mAppNotAddedList != null && AppAddModel.this.mAppNotAddedList.size() > 0) {
                    AppAddModel.this.mBusinessRequestorImp.getApplicationsByPackageNames(GameLauncherApplication.CONTEXT, AppAddModel.this.mAppAddModelHelper.getWillVerifyPackageList(AppAddModel.this.mAppNotAddedList), new IRequestListener() {
                        public void responseInfo(ResponseBean bean) {
                            Log.i("lsm", "verifyNotAddGameApp responseInfo == " + bean);
                            if (bean != null && bean.getStateCode() == ConstantVariable.STATE_CODE_SUCESS) {
                                ArrayList<GameItemBean> list = bean.getGameItemBean();
                                if (list != null && list.size() > 0) {
                                    ArrayList<AppListItemBean> findGameList = new ArrayList<>();
                                    Iterator it = list.iterator();
                                    while (it.hasNext()) {
                                        GameItemBean gameItemBean = (GameItemBean) it.next();
                                        if (gameItemBean.getAppType() == ConstantVariable.APP_TYPE_GAME || AppAddModel.this.mAppAddModelHelper.isInLocalGameList(gameItemBean.getPackageName())) {
                                            findGameList.addAll(AppAddModel.this.mAppAddModelHelper.getListBeanByGameItemBean(gameItemBean, AppAddModel.this.mAppNotAddedList));
                                        }
                                    }
                                    AppAddModel.this.mAppAddModelHelper.convertToAppAddList(findGameList);
                                    AppAddModel.this.mAppAddModelHelper.removeAppListItemBeanInRemoveDB(findGameList);
                                    AppAddModel.this.mAppAddModelHelper.insertAppToAppAddDB(findGameList);
                                    AppAddModel.this.mAppAddedList.addAll(findGameList);
                                    AppAddModel.this.mAppNotAddedList.removeAll(findGameList);
                                    AppAddModel.this.notifyChangedData();
                                }
                            }
                        }

                        public void responseError(String errorMsg) {
                            Log.i("lsm", "verifyGameApp  responseError begin doAddLocalToListByPackName");
                            AppAddModel.this.doAddLocalToListByPackName();
                        }
                    });
                }
            }
        });
    }

    public void isAutoAddGame(String packageName, final IGetPackageIsAutoAddGame listener) {
        Log.i("lsm", "isAutoAddGame packageName " + packageName);
        if (!this.mAppAddModelHelper.isInSystemAppList(packageName)) {
            if (!this.mAppAddModelHelper.isInLocalGameList(packageName) || listener == null) {
                this.mBusinessRequestorImp.getApplicationByPackageName(GameLauncherApplication.CONTEXT, packageName, new IRequestListener() {
                    public void responseInfo(ResponseBean bean) {
                        Log.i("lsm", "responseInfo == " + bean);
                        if (bean != null && bean.getStateCode() == ConstantVariable.STATE_CODE_SUCESS && bean.getGameItemBean() != null && bean.getGameItemBean().size() > 0) {
                            GameItemBean gameItemBean = (GameItemBean) bean.getGameItemBean().get(0);
                            if (!(gameItemBean == null || gameItemBean.getAppType() != ConstantVariable.APP_TYPE_GAME || listener == null)) {
                                listener.onGetPackageIsAutoAddGame(true, false, (GameItemBean) bean.getGameItemBean().get(0));
                                return;
                            }
                        }
                        if (listener != null) {
                            listener.onGetPackageIsAutoAddGame(false, false, null);
                        }
                    }

                    public void responseError(String errorMsg) {
                        if (listener != null) {
                            listener.onGetPackageIsAutoAddGame(false, true, null);
                            Log.i("lsm", "isAutoAddGame responseError errorMsg = " + errorMsg);
                        }
                    }
                });
            } else {
                listener.onGetPackageIsAutoAddGame(true, false, new GameItemBean(packageName, ConstantVariable.APP_TYPE_GAME, (String) ConstantVariable.LOCAL_GAME_IMAGE_MAP.get(packageName)));
            }
        }
    }

    public void doAddLocalToListByPackName() {
        WorkThread.runOnWorkThread(new Runnable() {
            public void run() {
                if (AppAddModel.this.mAppNotAddedList != null && !AppAddModel.this.mAppNotAddedList.isEmpty()) {
                    ArrayList<AppListItemBean> willAddList = new ArrayList<>();
                    Iterator it = AppAddModel.this.mAppNotAddedList.iterator();
                    while (it.hasNext()) {
                        AppListItemBean bean = (AppListItemBean) it.next();
                        String packageName = CommonUtil.convertPackageName(bean.getComponetName());
                        if (AppAddModel.this.mAppAddModelHelper.isInLocalGameList(packageName)) {
                            bean.setImageUrl((String) ConstantVariable.LOCAL_GAME_IMAGE_MAP.get(packageName));
                            bean.setGame(true);
                            if (!AppAddModel.this.mAppAddModelHelper.componentExistInUserRemoveDB(bean.getComponetName(), GameLauncherApplication.CONTEXT.getContentResolver())) {
                                willAddList.add(bean);
                            }
                        }
                    }
                    Log.i("lsm", "doAddLocalToListByPackName willAddList == " + willAddList);
                    AppAddModel.this.mAppAddModelHelper.insertAppToAppAddDB(willAddList);
                    AppAddModel.this.mAppAddModelHelper.deleteAppItemBeanInUserRemoveDB(willAddList);
                    AppAddModel.this.mAppNotAddedList.removeAll(willAddList);
                    AppAddModel.this.mAppAddedList.addAll(willAddList);
                }
            }
        });
    }

    public void doPackageAddBusinessByPackName(final String packageName) {
        WorkThread.runOnWorkThread(new Runnable() {
            public void run() {
                AppAddModel.this.isAutoAddGame(packageName, new IGetPackageIsAutoAddGame() {
                    public void onGetPackageIsAutoAddGame(boolean isAutoAddGame, boolean isNetworkError, GameItemBean gameItemBean) {
                        Log.i("lsm", "onGetPackageIsAutoAddGame isAutoAddGame== " + isAutoAddGame + " isNetworkError = " + isNetworkError + "  gameItemBean == " + gameItemBean);
                        if (AppAddModel.this.mLauncherApps != null) {
                            List<LauncherActivityInfo> list = AppAddModel.this.mLauncherApps.getActivityList(packageName, Process.myUserHandle());
                            if (list != null && list.size() > 0) {
                                ArrayList<AppListItemBean> insertList = new ArrayList<>();
                                for (LauncherActivityInfo info : list) {
                                    Drawable drawable = info.getIcon(0);
                                    ComponentName componentName = info.getComponentName();
                                    AppListItemBean bean = new AppListItemBean(CommonUtil.drawableToBitmap(drawable), info.getLabel().toString(), componentName.getPackageName() + "," + componentName.getClassName(), true, "");
                                    if (isAutoAddGame) {
                                        if (gameItemBean != null) {
                                            bean.setImageUrl(gameItemBean.getUrl());
                                        }
                                        bean.setGame(true);
                                        if (!AppAddModel.this.isExistInAppAddedList(bean.getComponetName())) {
                                            AppAddModel.this.mAppAddedList.add(bean);
                                        }
                                        insertList.add(bean);
                                    } else {
                                        bean.setGame(false);
                                        bean.setSelect(false);
                                        if (!AppAddModel.this.isExistInAppNotAddedList(bean.getComponetName())) {
                                            AppAddModel.this.mAppNotAddedList.add(bean);
                                        }
                                    }
                                }
                                AppAddModel.this.mAppAddModelHelper.insertAppToAppAddDB(insertList);
                                AppAddModel.this.sortListData();
                            }
                        }
                    }
                });
            }
        });
    }

    /* access modifiers changed from: 0000 */
    public void doPackageUpdateBusinessByPackName(final String packageName) {
        WorkThread.runOnWorkThread(new Runnable() {
            public void run() {
                if (AppAddModel.this.mLauncherApps != null) {
                    List<LauncherActivityInfo> list = AppAddModel.this.mLauncherApps.getActivityList(packageName, Process.myUserHandle());
                    if (list != null && list.size() > 0) {
                        for (LauncherActivityInfo info : list) {
                            Drawable drawable = info.getIcon(0);
                            String name = info.getLabel().toString();
                            ComponentName componentName = info.getComponentName();
                            AppListItemBean findBean = null;
                            Iterator it = AppAddModel.this.mAppAddedList.iterator();
                            while (true) {
                                if (!it.hasNext()) {
                                    break;
                                }
                                AppListItemBean bean = (AppListItemBean) it.next();
                                if (CommonUtil.convertPackageName(bean.getComponetName()).equals(packageName)) {
                                    bean.icon = CommonUtil.drawableToBitmap(drawable);
                                    bean.setName(name);
                                    findBean = bean;
                                    break;
                                }
                            }
                            if (findBean == null) {
                                Iterator it2 = AppAddModel.this.mAppNotAddedList.iterator();
                                while (true) {
                                    if (!it2.hasNext()) {
                                        break;
                                    }
                                    AppListItemBean bean2 = (AppListItemBean) it2.next();
                                    if (CommonUtil.convertPackageName(bean2.getComponetName()).equals(packageName)) {
                                        bean2.icon = CommonUtil.drawableToBitmap(drawable);
                                        bean2.setName(name);
                                        bean2.setComponentName(componentName.getPackageName() + "," + componentName.getClassName());
                                        findBean = bean2;
                                        break;
                                    }
                                }
                            }
                            Log.i("lsm", "doPackageUpdateBusinessByPackName  findBean ==" + findBean);
                            if (findBean != null) {
                                AppAddModel.this.updateAppItemBeanInAppAddDB(findBean);
                            }
                        }
                    }
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public boolean isExistInAppNotAddedList(String component) {
        if (this.mAppNotAddedList != null && this.mAppNotAddedList.size() > 0) {
            Iterator it = this.mAppNotAddedList.iterator();
            while (it.hasNext()) {
                if (((AppListItemBean) it.next()).getComponetName().equals(component)) {
                    return true;
                }
            }
        }
        return false;
    }

    private AppListItemBean getBeanInNotAddList(String component) {
        if (this.mAppNotAddedList != null && this.mAppNotAddedList.size() > 0) {
            Iterator it = this.mAppNotAddedList.iterator();
            while (it.hasNext()) {
                AppListItemBean bean = (AppListItemBean) it.next();
                if (bean.getComponetName().equals(component)) {
                    return bean;
                }
            }
        }
        return null;
    }

    private AppListItemBean getBeanInAddedList(String component) {
        if (this.mAppAddedList != null && this.mAppAddedList.size() > 0) {
            Iterator it = this.mAppAddedList.iterator();
            while (it.hasNext()) {
                AppListItemBean bean = (AppListItemBean) it.next();
                if (bean.getComponetName().equals(component)) {
                    return bean;
                }
            }
        }
        return null;
    }

    public void onAppAddedCallback(String component, boolean isChecked) {
        Log.i("lsm", "onAppAddedCallback component == " + component + "  isChecked == " + isChecked);
        if (isChecked) {
            AppListItemBean bean = getBeanInNotAddList(component);
            this.mAppAddedList.add(bean);
            this.mAppNotAddedList.remove(bean);
            if (bean != null) {
                bean.setSelect(true);
            }
            ArrayList<AppListItemBean> list = new ArrayList<>();
            list.add(bean);
            this.mAppAddModelHelper.insertAppToAppAddDB(list);
            this.mAppAddModelHelper.deleteAppItemBeanInUserRemoveDB(list);
        } else {
            AppListItemBean bean2 = getBeanInAddedList(component);
            this.mAppAddedList.remove(bean2);
            this.mAppNotAddedList.add(bean2);
            if (bean2 != null) {
                bean2.setSelect(false);
            }
            ArrayList<AppListItemBean> list2 = new ArrayList<>();
            list2.add(bean2);
            this.mAppAddModelHelper.deleteAppItemBeanInAppAddDB(list2);
            this.mAppAddModelHelper.insertAppToUserRemoveDB(list2);
        }
        sortListData();
    }

    public void onNeoDownloadGameChange(int appId) {
        if (appId == -200) {
            this.mNeoDownloadAppItemList = this.mNeoDownloadHelper.getNeoDownloadAppList();
            this.mMainHandler.post(new Runnable() {
                public void run() {
                    BannerManager.getInstance().refreshGameRecycler(true);
                }
            });
            return;
        }
        final AppListItemBean changeItemBean = this.mNeoDownloadHelper.getAppListItemBeanByAppId(appId);
        if (changeItemBean != null) {
            this.mMainHandler.post(new Runnable() {
                public void run() {
                    BannerManager.getInstance().doChangeNeoDownloadApp(changeItemBean);
                }
            });
        }
    }

    public ArrayList<AppListItemBean> getNeoDownloadAppItemList() {
        return this.mNeoDownloadAppItemList;
    }

    public void end() {
        this.mCallbackList.clear();
        if (this.mLauncherApps != null) {
            try {
                this.mLauncherApps.unregisterCallback(this.mPackageChangedCallback);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public NeoDownloadHelper getNeoDownloadHelper() {
        return this.mNeoDownloadHelper;
    }
}
