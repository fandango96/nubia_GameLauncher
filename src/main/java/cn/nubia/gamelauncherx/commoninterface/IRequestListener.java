package cn.nubia.gamelauncherx.commoninterface;

import cn.nubia.gamelauncherx.bean.ResponseBean;

public interface IRequestListener {
    void responseError(String str);

    void responseInfo(ResponseBean responseBean);
}
