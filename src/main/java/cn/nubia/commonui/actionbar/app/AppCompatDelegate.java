package cn.nubia.commonui.actionbar.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import cn.nubia.commonui.actionbar.app.ActionBarDrawerToggle.Delegate;
import cn.nubia.commonui.actionbar.view.ActionMode;
import cn.nubia.commonui.actionbar.view.ActionMode.Callback;
import cn.nubia.commonui.actionbar.widget.Toolbar;

public abstract class AppCompatDelegate {
    static final String TAG = "NubiaWidget";
    private static boolean sCompatVectorFromResourcesEnabled = false;

    public abstract void addContentView(View view, LayoutParams layoutParams);

    public abstract View createView(View view, String str, @NonNull Context context, @NonNull AttributeSet attributeSet);

    public abstract int getContextDisplayMode();

    public abstract Delegate getDrawerToggleDelegate();

    public abstract MenuInflater getMenuInflater();

    public abstract ActionBar getSupportActionBar();

    public abstract void installViewFactory();

    public abstract void invalidateOptionsMenu();

    public abstract void onConfigurationChanged(Configuration configuration);

    public abstract void onCreate(Bundle bundle);

    public abstract void onDestroy();

    public abstract void onPostCreate(Bundle bundle);

    public abstract void onPostResume();

    public abstract void onStop();

    public abstract boolean requestWindowFeature(int i);

    public abstract void setContentView(int i);

    public abstract void setContentView(View view);

    public abstract void setContentView(View view, LayoutParams layoutParams);

    public abstract void setContextDisplayMode(int i);

    public abstract void setHideSplitView(boolean z);

    public abstract void setSupportActionBar(Toolbar toolbar);

    public abstract void setTitle(CharSequence charSequence);

    public abstract ActionMode startSupportActionMode(Callback callback);

    public static AppCompatDelegate create(Activity activity, AppCompatCallback callback) {
        if (VERSION.SDK_INT >= 11) {
            return new AppCompatDelegateImplV11(activity, activity.getWindow(), callback);
        }
        return new AppCompatDelegateImplV7(activity, activity.getWindow(), callback);
    }

    public static AppCompatDelegate create(Dialog dialog, AppCompatCallback callback) {
        if (VERSION.SDK_INT >= 11) {
            return new AppCompatDelegateImplV11(dialog.getContext(), dialog.getWindow(), callback);
        }
        return new AppCompatDelegateImplV7(dialog.getContext(), dialog.getWindow(), callback);
    }

    AppCompatDelegate() {
    }

    public static void setCompatVectorFromResourcesEnabled(boolean enabled) {
        sCompatVectorFromResourcesEnabled = enabled;
    }

    public static boolean isCompatVectorFromResourcesEnabled() {
        return sCompatVectorFromResourcesEnabled;
    }
}
