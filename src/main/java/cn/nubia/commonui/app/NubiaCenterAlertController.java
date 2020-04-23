package cn.nubia.commonui.app;

import android.content.Context;
import android.content.DialogInterface;
import android.view.Window;
import cn.nubia.commonui.R;

public class NubiaCenterAlertController extends AlertController {
    public NubiaCenterAlertController(Context context, DialogInterface di, Window window) {
        super(context, di, window);
        super.resetNubiaAlertDialogLayout();
        super.setNubiaButtonBackground(R.drawable.nubia_center_alert_dialog_left_btn_default_material, R.drawable.nubia_center_alert_dialog_mid_btn_default_material, R.drawable.nubia_center_alert_dialog_right_btn_default_material);
        resetNubiaAlertDialogLayout();
    }

    /* access modifiers changed from: 0000 */
    public void resetNubiaAlertDialogLayout() {
        if (R.layout.nubia_alert_dialog_holo != 0) {
            setAlertDialogLayout(R.layout.nubia_alert_dialog_holo_center);
        }
    }
}
