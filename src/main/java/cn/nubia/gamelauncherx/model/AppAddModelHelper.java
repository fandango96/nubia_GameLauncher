package cn.nubia.gamelauncherx.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import cn.nubia.gamelauncherx.GameLauncherApplication;
import cn.nubia.gamelauncherx.bean.AppListItemBean;
import cn.nubia.gamelauncherx.bean.GameItemBean;
import cn.nubia.gamelauncherx.commoninterface.ConstantVariable;
import cn.nubia.gamelauncherx.commoninterface.NeoGameDBColumns;
import cn.nubia.gamelauncherx.db.AppAddProvider;
import cn.nubia.gamelauncherx.util.CommonUtil;
import cn.nubia.gamelauncherx.util.WorkThread;
import java.util.ArrayList;
import java.util.Iterator;

public class AppAddModelHelper {
    private int mMaxIdInAppAddTable = 1;
    private int mMaxIdInUserRemoveTable = 1;

    public void initMaxIdInTable() {
        WorkThread.runOnWorkThread(new Runnable() {
            public void run() {
                ContentResolver resolver = GameLauncherApplication.CONTEXT.getContentResolver();
                Cursor cursorAppAdd = null;
                Cursor cursorUserRemove = null;
                try {
                    Cursor cursorAppAdd2 = resolver.query(ConstantVariable.APPADD_URI, null, "max(_id)", null, null);
                    if (cursorAppAdd2 != null && cursorAppAdd2.moveToNext()) {
                        AppAddModelHelper.this.setMaxIdInAppAddTable(cursorAppAdd2.getInt(0));
                    }
                    Cursor cursorUserRemove2 = resolver.query(ConstantVariable.USER_ROMOVE_URI, null, "max(_id)", null, null);
                    if (cursorUserRemove2 != null && cursorUserRemove2.moveToNext()) {
                        AppAddModelHelper.this.setMaxIdInUserRemoveTable(cursorUserRemove2.getInt(0));
                    }
                    if (cursorAppAdd2 != null) {
                        cursorAppAdd2.close();
                    }
                    if (cursorUserRemove2 != null) {
                        cursorUserRemove2.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (cursorAppAdd != null) {
                        cursorAppAdd.close();
                    }
                    if (cursorUserRemove != null) {
                        cursorUserRemove.close();
                    }
                } catch (Throwable th) {
                    if (cursorAppAdd != null) {
                        cursorAppAdd.close();
                    }
                    if (cursorUserRemove != null) {
                        cursorUserRemove.close();
                    }
                    throw th;
                }
                Log.i("lsm", "initMaxIdInTable getMaxIdInAppAddTable == " + AppAddModelHelper.this.getMaxIdInAppAddTable() + " getMaxIdInUserRemoveTable ==  " + AppAddModelHelper.this.getMaxIdInUserRemoveTable());
            }
        });
    }

    public void insertAppToAppAddDB(final ArrayList<AppListItemBean> list) {
        final ContentResolver resolver = GameLauncherApplication.CONTEXT.getContentResolver();
        WorkThread.runOnWorkThread(new Runnable() {
            public void run() {
                for (int i = 0; i < list.size(); i++) {
                    AppListItemBean bean = (AppListItemBean) list.get(i);
                    if (!AppAddModelHelper.this.componentExistInAppAddDB(bean.getComponetName(), resolver)) {
                        if (i == list.size() - 1) {
                            resolver.insert(ConstantVariable.APPADD_URI, AppAddModelHelper.this.covertToAppAddContentValues(bean));
                        } else {
                            resolver.insert(ConstantVariable.APPADD_URI_NO_NOTIFY, AppAddModelHelper.this.covertToAppAddContentValues(bean));
                        }
                        if (bean.isGame()) {
                            AppAddModelHelper.this.notifyLauncherNotification(AppAddModelHelper.this.getMaxIdInAppAddTable(), bean.getName());
                        }
                    }
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void notifyLauncherNotification(int id, String appName) {
        Bundle bundle = new Bundle();
        bundle.putInt(NeoGameDBColumns._ID, id);
        bundle.putString("appName", appName);
        try {
            GameLauncherApplication.CONTEXT.getContentResolver().call(Uri.parse("content://cn.nubia.launcher.settings/favorites"), "gamelauncher", "notification", bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteAppItemBeanInAppAddDB(final ArrayList<AppListItemBean> list) {
        final ContentResolver resolver = GameLauncherApplication.CONTEXT.getContentResolver();
        WorkThread.runOnWorkThread(new Runnable() {
            public void run() {
                for (int i = 0; i < list.size(); i++) {
                    AppListItemBean bean = (AppListItemBean) list.get(i);
                    if (AppAddModelHelper.this.componentExistInAppAddDB(bean.getComponetName(), resolver)) {
                        if (i == list.size() - 1) {
                            resolver.delete(ConstantVariable.APPADD_URI, "component=?", new String[]{bean.getComponetName()});
                        } else {
                            resolver.delete(ConstantVariable.APPADD_URI_NO_NOTIFY, "component=?", new String[]{bean.getComponetName()});
                        }
                    }
                }
            }
        });
    }

    public void updateAppItemBeanInAppAddDB(final AppListItemBean bean) {
        if (bean != null) {
            final ContentResolver resolver = GameLauncherApplication.CONTEXT.getContentResolver();
            WorkThread.runOnWorkThread(new Runnable() {
                public void run() {
                    if (AppAddModelHelper.this.componentExistInAppAddDB(bean.getComponetName(), resolver)) {
                        resolver.update(ConstantVariable.APPADD_URI, AppAddModelHelper.this.covertToAppAddContentValues(bean), "component=?", new String[]{bean.getComponetName()});
                    }
                }
            });
        }
    }

    public void insertAppToUserRemoveDB(final ArrayList<AppListItemBean> list) {
        final ContentResolver resolver = GameLauncherApplication.CONTEXT.getContentResolver();
        WorkThread.runOnWorkThread(new Runnable() {
            public void run() {
                Iterator it = list.iterator();
                while (it.hasNext()) {
                    AppListItemBean bean = (AppListItemBean) it.next();
                    if (!AppAddModelHelper.this.componentExistInUserRemoveDB(bean.getComponetName(), resolver)) {
                        resolver.insert(ConstantVariable.USER_ROMOVE_URI_NO_NOTIFY, AppAddModelHelper.this.covertToUserRemoveContentValues(bean));
                    }
                }
            }
        });
    }

    public void deleteAppItemBeanInUserRemoveDB(final ArrayList<AppListItemBean> list) {
        final ContentResolver resolver = GameLauncherApplication.CONTEXT.getContentResolver();
        WorkThread.runOnWorkThread(new Runnable() {
            public void run() {
                Iterator it = list.iterator();
                while (it.hasNext()) {
                    AppListItemBean bean = (AppListItemBean) it.next();
                    if (AppAddModelHelper.this.componentExistInUserRemoveDB(bean.getComponetName(), resolver)) {
                        resolver.delete(ConstantVariable.USER_ROMOVE_URI_NO_NOTIFY, "component=?", new String[]{bean.getComponetName()});
                    }
                }
            }
        });
    }

    /* access modifiers changed from: 0000 */
    public boolean componentExistInAppAddDB(String componentName, ContentResolver resolver) {
        Cursor cursor = null;
        try {
            ContentResolver contentResolver = resolver;
            Cursor cursor2 = contentResolver.query(ConstantVariable.APPADD_URI, new String[]{AppAddProvider.APPADD_ID, AppAddProvider.APPADD_COMPONENT}, "component=?", new String[]{componentName}, null);
            if (cursor2 == null || !cursor2.moveToFirst()) {
                if (cursor2 != null) {
                    cursor2.close();
                }
                return false;
            }
            if (cursor2 != null) {
                cursor2.close();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (cursor != null) {
                cursor.close();
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
        return false;
    }

    public boolean componentExistInUserRemoveDB(String componentName, ContentResolver resolver) {
        Cursor cursor = null;
        try {
            ContentResolver contentResolver = resolver;
            Cursor cursor2 = contentResolver.query(ConstantVariable.USER_ROMOVE_URI, new String[]{AppAddProvider.APPADD_ID, AppAddProvider.APPADD_COMPONENT}, "component=?", new String[]{componentName}, null);
            if (cursor2 == null || !cursor2.moveToFirst()) {
                if (cursor2 != null) {
                    cursor2.close();
                }
                return false;
            }
            if (cursor2 != null) {
                cursor2.close();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (cursor != null) {
                cursor.close();
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
        return false;
    }

    public ArrayList<String> getWillVerifyPackageList(ArrayList<AppListItemBean> list) {
        if (list == null || list.size() <= 0) {
            return null;
        }
        ArrayList<String> packList = new ArrayList<>();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            packList.add(CommonUtil.convertPackageName(((AppListItemBean) it.next()).getComponetName()));
        }
        return packList;
    }

    public boolean isInLocalGameList(String packageName) {
        for (String gameName : ConstantVariable.LOCAL_GAME_IMAGE_MAP.keySet()) {
            if (gameName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    public boolean isInSystemAppList(String component) {
        Iterator it = ConstantVariable.SYSTEM_APP_LIST.iterator();
        while (it.hasNext()) {
            if (((String) it.next()).equals(component)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<AppListItemBean> getListBeanByGameItemBean(GameItemBean gameItemBean, ArrayList<AppListItemBean> list) {
        if (list == null || list.size() <= 0) {
            return null;
        }
        ArrayList<AppListItemBean> listItemBeans = new ArrayList<>();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            AppListItemBean bean = (AppListItemBean) it.next();
            if (CommonUtil.convertPackageName(bean.getComponetName()).equals(gameItemBean.getPackageName())) {
                bean.setImageUrl(gameItemBean.getUrl());
                bean.setGame(gameItemBean.getAppType() == ConstantVariable.APP_TYPE_GAME);
                listItemBeans.add(bean);
            }
        }
        return listItemBeans;
    }

    public ArrayList<AppListItemBean> getListBeanByPackName(String packageName, ArrayList<AppListItemBean> list) {
        if (list == null || list.size() <= 0) {
            return null;
        }
        ArrayList<AppListItemBean> listItemBeans = new ArrayList<>();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            AppListItemBean bean = (AppListItemBean) it.next();
            if (CommonUtil.convertPackageName(bean.getComponetName()).equals(packageName)) {
                listItemBeans.add(bean);
            }
        }
        return listItemBeans;
    }

    private int generateNewAppAddId() {
        this.mMaxIdInAppAddTable++;
        return this.mMaxIdInAppAddTable;
    }

    public void setMaxIdInAppAddTable(int id) {
        this.mMaxIdInAppAddTable = id;
    }

    public int getMaxIdInAppAddTable() {
        return this.mMaxIdInAppAddTable;
    }

    private int generateNewUserRemoveId() {
        this.mMaxIdInUserRemoveTable++;
        return this.mMaxIdInUserRemoveTable;
    }

    public int getMaxIdInUserRemoveTable() {
        return this.mMaxIdInUserRemoveTable;
    }

    public void setMaxIdInUserRemoveTable(int mMaxIdInUserRemoveTable2) {
        this.mMaxIdInUserRemoveTable = mMaxIdInUserRemoveTable2;
    }

    public ContentValues covertToAppAddContentValues(AppListItemBean bean) {
        ContentValues values = new ContentValues();
        values.put(AppAddProvider.APPADD_ID, Integer.valueOf(generateNewAppAddId()));
        if (bean.select) {
            values.put(AppAddProvider.APPADD_ISADD, Integer.valueOf(1));
        } else {
            values.put(AppAddProvider.APPADD_ISADD, Integer.valueOf(0));
        }
        if (bean.isGame()) {
            values.put(AppAddProvider.APPADD_ISGAME, Integer.valueOf(1));
        } else {
            values.put(AppAddProvider.APPADD_ISGAME, Integer.valueOf(0));
        }
        values.put(AppAddProvider.APPADD_NAME, bean.getName());
        values.put(AppAddProvider.APPADD_COMPONENT, bean.getComponetName());
        values.put(AppAddProvider.APPADD_IMAGE_URL, bean.getImageUrl());
        return values;
    }

    public ContentValues covertToUserRemoveContentValues(AppListItemBean bean) {
        ContentValues values = new ContentValues();
        values.put(AppAddProvider.APPADD_ID, Integer.valueOf(generateNewUserRemoveId()));
        values.put(AppAddProvider.APPADD_COMPONENT, bean.getComponetName());
        return values;
    }

    public ArrayList<AppListItemBean> convertToAppAddList(ArrayList<AppListItemBean> list) {
        if (list != null && list.size() > 0) {
            Iterator it = list.iterator();
            while (it.hasNext()) {
                AppListItemBean bean = (AppListItemBean) it.next();
                String packageName = CommonUtil.convertPackageName(bean.getComponetName());
                if (isInLocalGameList(packageName) && TextUtils.isEmpty(bean.getImageUrl())) {
                    bean.setImageUrl((String) ConstantVariable.LOCAL_GAME_IMAGE_MAP.get(packageName));
                }
                bean.setSelect(true);
            }
        }
        return list;
    }

    public void removeAppListItemBeanInRemoveDB(ArrayList<AppListItemBean> list) {
        if (list != null && list.size() > 0) {
            Iterator<AppListItemBean> iterator = list.iterator();
            while (iterator.hasNext()) {
                if (componentExistInUserRemoveDB(((AppListItemBean) iterator.next()).getComponetName(), GameLauncherApplication.CONTEXT.getContentResolver())) {
                    iterator.remove();
                }
            }
        }
    }
}
