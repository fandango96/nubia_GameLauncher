package cn.nubia.commonui.app;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import cn.nubia.commonui.R;
import cn.nubia.commonui.app.AlertController.AlertParams;

public abstract class AlertActivity extends Activity implements DialogInterface {
    protected AlertController mAlert;
    protected AlertParams mAlertParams;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mAlert = new AlertController(this, this, getWindow());
        this.mAlertParams = new AlertParams(this);
    }

    public void cancel() {
        finish();
    }

    public void onContentChanged() {
        super.onContentChanged();
        resetShowWindowAttributes(getWindow());
    }

    private void resetShowWindowAttributes(Window window) {
        LayoutParams params = window.getAttributes();
        params.width = -1;
        params.height = -2;
        params.gravity = 81;
        window.setAttributes(params);
    }

    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.nubia_dialog_exit);
    }

    public void dismiss() {
        if (!isFinishing()) {
            finish();
        }
    }

    /* access modifiers changed from: protected */
    public void setupAlert() {
        this.mAlertParams.apply(this.mAlert);
        this.mAlert.installContent();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (this.mAlert.onKeyDown(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (this.mAlert.onKeyUp(keyCode, event)) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}
