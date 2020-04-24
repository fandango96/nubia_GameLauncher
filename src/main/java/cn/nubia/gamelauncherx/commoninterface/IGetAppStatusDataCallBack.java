package cn.nubia.gamelauncherx.commoninterface;

import cn.nubia.gamelauncherx.bean.AppListItemBean;
import java.util.ArrayList;

public interface IGetAppStatusDataCallBack {
    void onLoadAddAppListDone(ArrayList<AppListItemBean> arrayList, int i);
}
