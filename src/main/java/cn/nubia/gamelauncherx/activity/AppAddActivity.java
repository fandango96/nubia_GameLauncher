package cn.nubia.gamelauncherx.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import cn.nubia.gamelauncherx.R;
import cn.nubia.gamelauncherx.controller.AppListController;
import cn.nubia.gamelauncherx.controller.AppListTopBarController;

public class AppAddActivity extends BaseActivity {
    private AppListController mAddListController = null;
    private AppListTopBarController mAppListTopBarController = null;

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initController();
    }

    private void initController() {
        this.mAppListTopBarController = new AppListTopBarController();
        this.mAppListTopBarController.init(this);
        this.mAddListController = new AppListController();
        this.mAddListController.init(this);
    }

    private void initView() {
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.app_add_layout);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        this.mAddListController.onResume();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        this.mAddListController.onPasue();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        this.mAddListController.onDestory();
    }
}
