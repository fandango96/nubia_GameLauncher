package cn.nubia.gamelauncherx.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import cn.nubia.gamelauncherx.R;
import cn.nubia.gamelauncherx.controller.UninstallController;

public class UninstallActivity extends BaseActivity {
    private UninstallController mUninstallController = null;

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initController();
    }

    private void initController() {
        this.mUninstallController = new UninstallController();
        this.mUninstallController.init(this);
    }

    private void initView() {
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.uninstall_layout);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        this.mUninstallController.onResume();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        this.mUninstallController.onPasue();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        this.mUninstallController.onDestory();
    }
}
