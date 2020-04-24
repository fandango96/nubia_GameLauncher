package cn.nubia.gamelauncherx.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;

import cn.nubia.gamelauncherx.gamehandle.GameHandleService;

@SuppressLint("Registered")
public class BaseActivity extends Activity {
    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(512);
        getWindow().addFlags(256);
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavigationBar(hasFocus);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        hideNavigationBar(true);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                BaseActivity.this.hideNavigationBar(true);
                BaseActivity.this.startService(new Intent(BaseActivity.this, GameHandleService.class));
            }
        }, 1000);
    }

    public void hideNavigationBar(boolean hasFocus) {
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(5382);
        }
    }
}
