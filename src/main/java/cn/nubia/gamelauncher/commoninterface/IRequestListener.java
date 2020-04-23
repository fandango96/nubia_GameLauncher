package cn.nubia.gamelauncher.commoninterface;

import cn.nubia.gamelauncher.bean.ResponseBean;

public interface IRequestListener {
    void responseError(String str);

    void responseInfo(ResponseBean responseBean);
}
