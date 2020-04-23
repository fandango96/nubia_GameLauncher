package cn.nubia.gamelauncher.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import cn.nubia.gamelauncher.R;
import cn.nubia.gamelauncher.activity.UninstallActivity;

public class AppListTopBarController {
    private Context mContext = null;
    private ImageView mReturnBtn = null;
    private TextView mUninstallText = null;

    public void init(final Activity activity) {
        this.mContext = activity.getApplicationContext();
        this.mReturnBtn = (ImageView) activity.findViewById(R.id.left_icon);
        this.mReturnBtn.setClickable(true);
        this.mReturnBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                activity.finish();
            }
        });
        this.mUninstallText = (TextView) activity.findViewById(R.id.app_select_btn);
        this.mUninstallText.setClickable(true);
        this.mUninstallText.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                AppListTopBarController.this.startUninstallActivity();
            }
        });
    }

    /* access modifiers changed from: private */
    public void startUninstallActivity() {
        this.mContext.startActivity(new Intent(this.mContext, UninstallActivity.class));
    }
}
