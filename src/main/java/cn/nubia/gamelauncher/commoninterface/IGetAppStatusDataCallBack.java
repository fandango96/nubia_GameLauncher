package cn.nubia.gamelauncher.commoninterface;

import cn.nubia.gamelauncher.bean.AppListItemBean;
import java.util.ArrayList;

public interface IGetAppStatusDataCallBack {
    void onLoadAddAppListDone(ArrayList<AppListItemBean> arrayList, int i);
}
