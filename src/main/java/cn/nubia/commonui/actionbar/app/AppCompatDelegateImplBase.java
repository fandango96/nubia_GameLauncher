package cn.nubia.commonui.actionbar.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.view.Window.Callback;
import cn.nubia.commonui.R;
import cn.nubia.commonui.actionbar.app.ActionBarDrawerToggle.Delegate;
import cn.nubia.commonui.actionbar.internal.view.SupportMenuInflater;
import cn.nubia.commonui.actionbar.internal.view.WindowCallbackWrapper;
import cn.nubia.commonui.actionbar.internal.view.menu.MenuBuilder;
import cn.nubia.commonui.actionbar.internal.widget.TintTypedArray;
import cn.nubia.commonui.actionbar.view.ActionMode;

abstract class AppCompatDelegateImplBase extends AppCompatDelegate {
    private ActionBar mActionBar;
    final AppCompatCallback mAppCompatCallback;
    final Context mContext;
    boolean mHasActionBar;
    private boolean mIsDestroyed;
    boolean mIsFloating;
    private MenuInflater mMenuInflater;
    final Callback mOriginalWindowCallback = this.mWindow.getCallback();
    boolean mOverlayActionBar;
    boolean mOverlayActionMode;
    private CharSequence mTitle;
    final Window mWindow;
    boolean mWindowNoTitle;

    private class ActionBarDrawableToggleImpl implements Delegate {
        private ActionBarDrawableToggleImpl() {
        }

        public Drawable getThemeUpIndicator() {
            TintTypedArray a = TintTypedArray.obtainStyledAttributes(getActionBarThemedContext(), null, new int[]{R.attr.homeAsUpIndicator});
            Drawable result = a.getDrawable(0);
            a.recycle();
            return result;
        }

        public Context getActionBarThemedContext() {
            return AppCompatDelegateImplBase.this.getActionBarThemedContext();
        }

        public boolean isNavigationVisible() {
            ActionBar ab = AppCompatDelegateImplBase.this.getSupportActionBar();
            return (ab == null || (ab.getDisplayOptions() & 4) == 0) ? false : true;
        }

        public void setActionBarUpIndicator(Drawable upDrawable, int contentDescRes) {
            ActionBar ab = AppCompatDelegateImplBase.this.getSupportActionBar();
            if (ab != null) {
                ab.setHomeAsUpIndicator(upDrawable);
                ab.setHomeActionContentDescription(contentDescRes);
            }
        }

        public void setActionBarDescription(int contentDescRes) {
            ActionBar ab = AppCompatDelegateImplBase.this.getSupportActionBar();
            if (ab != null) {
                ab.setHomeActionContentDescription(contentDescRes);
            }
        }
    }

    private class AppCompatWindowCallback extends WindowCallbackWrapper {
        AppCompatWindowCallback(Callback callback) {
            super(callback);
        }

        public boolean dispatchKeyEvent(KeyEvent event) {
            if (AppCompatDelegateImplBase.this.dispatchKeyEvent(event)) {
                return true;
            }
            return super.dispatchKeyEvent(event);
        }

        public boolean onCreatePanelMenu(int featureId, Menu menu) {
            if (featureId != 0 || (menu instanceof MenuBuilder)) {
                return super.onCreatePanelMenu(featureId, menu);
            }
            return false;
        }

        public boolean onPreparePanel(int featureId, View view, Menu menu) {
            if (featureId == 0 && !(menu instanceof MenuBuilder)) {
                return false;
            }
            if (featureId != 0 || !bypassPrepareOptionsPanelIfNeeded()) {
                return super.onPreparePanel(featureId, view, menu);
            }
            if (AppCompatDelegateImplBase.this.mOriginalWindowCallback instanceof Activity) {
                return ((Activity) AppCompatDelegateImplBase.this.mOriginalWindowCallback).onPrepareOptionsMenu(menu);
            }
            if (AppCompatDelegateImplBase.this.mOriginalWindowCallback instanceof Dialog) {
                return ((Dialog) AppCompatDelegateImplBase.this.mOriginalWindowCallback).onPrepareOptionsMenu(menu);
            }
            return false;
        }

        public boolean onMenuOpened(int featureId, Menu menu) {
            if (AppCompatDelegateImplBase.this.onMenuOpened(featureId, menu)) {
                return true;
            }
            return super.onMenuOpened(featureId, menu);
        }

        public boolean dispatchKeyShortcutEvent(KeyEvent event) {
            if (AppCompatDelegateImplBase.this.onKeyShortcut(event.getKeyCode(), event)) {
                return true;
            }
            return super.dispatchKeyShortcutEvent(event);
        }

        public void onContentChanged() {
        }

        public void onPanelClosed(int featureId, Menu menu) {
            if (!AppCompatDelegateImplBase.this.onPanelClosed(featureId, menu)) {
                super.onPanelClosed(featureId, menu);
            }
        }

        private boolean bypassPrepareOptionsPanelIfNeeded() {
            if ((VERSION.SDK_INT >= 16 || !(AppCompatDelegateImplBase.this.mOriginalWindowCallback instanceof Activity)) && !(AppCompatDelegateImplBase.this.mOriginalWindowCallback instanceof Dialog)) {
                return false;
            }
            return true;
        }
    }

    /* access modifiers changed from: 0000 */
    public abstract ActionBar createSupportActionBar();

    /* access modifiers changed from: 0000 */
    public abstract boolean dispatchKeyEvent(KeyEvent keyEvent);

    /* access modifiers changed from: 0000 */
    public abstract boolean onKeyShortcut(int i, KeyEvent keyEvent);

    /* access modifiers changed from: 0000 */
    public abstract boolean onMenuOpened(int i, Menu menu);

    /* access modifiers changed from: 0000 */
    public abstract boolean onPanelClosed(int i, Menu menu);

    /* access modifiers changed from: 0000 */
    public abstract void onTitleChanged(CharSequence charSequence);

    /* access modifiers changed from: 0000 */
    public abstract ActionMode startSupportActionModeFromWindow(ActionMode.Callback callback);

    AppCompatDelegateImplBase(Context context, Window window, AppCompatCallback callback) {
        this.mContext = context;
        this.mWindow = window;
        this.mAppCompatCallback = callback;
        if (this.mOriginalWindowCallback instanceof AppCompatWindowCallback) {
            throw new IllegalStateException("AppCompat has already installed itself into the Window");
        }
        this.mWindow.setCallback(new AppCompatWindowCallback(this.mOriginalWindowCallback));
    }

    public ActionBar getSupportActionBar() {
        if (this.mHasActionBar && this.mActionBar == null) {
            this.mActionBar = createSupportActionBar();
        }
        return this.mActionBar;
    }

    /* access modifiers changed from: 0000 */
    public final ActionBar peekSupportActionBar() {
        return this.mActionBar;
    }

    /* access modifiers changed from: 0000 */
    public final void setSupportActionBar(ActionBar actionBar) {
        this.mActionBar = actionBar;
    }

    public MenuInflater getMenuInflater() {
        if (this.mMenuInflater == null) {
            this.mMenuInflater = new SupportMenuInflater(getActionBarThemedContext());
        }
        return this.mMenuInflater;
    }

    public void onCreate(Bundle savedInstanceState) {
        TypedArray a = this.mContext.obtainStyledAttributes(R.styleable.Theme);
        if (!a.hasValue(R.styleable.Theme_windowActionBar)) {
            a.recycle();
            throw new IllegalStateException("You need to use a Theme.AppCompat theme (or descendant) with this activity.");
        }
        if (a.getBoolean(R.styleable.Theme_windowActionBar, false)) {
            this.mHasActionBar = true;
        }
        if (a.getBoolean(R.styleable.Theme_windowActionBarOverlay, false)) {
            this.mOverlayActionBar = true;
        }
        if (a.getBoolean(R.styleable.Theme_windowActionModeOverlay, false)) {
            this.mOverlayActionMode = true;
        }
        this.mIsFloating = a.getBoolean(R.styleable.Theme_android_windowIsFloating, false);
        this.mWindowNoTitle = a.getBoolean(R.styleable.Theme_windowNoTitle, false);
        a.recycle();
    }

    public final Delegate getDrawerToggleDelegate() {
        return new ActionBarDrawableToggleImpl();
    }

    /* access modifiers changed from: 0000 */
    public final Context getActionBarThemedContext() {
        Context context = null;
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            context = ab.getThemedContext();
        }
        if (context == null) {
            return this.mContext;
        }
        return context;
    }

    public final void onDestroy() {
        this.mIsDestroyed = true;
    }

    /* access modifiers changed from: 0000 */
    public final boolean isDestroyed() {
        return this.mIsDestroyed;
    }

    /* access modifiers changed from: 0000 */
    public final Callback getWindowCallback() {
        return this.mWindow.getCallback();
    }

    public final void setTitle(CharSequence title) {
        this.mTitle = title;
        onTitleChanged(title);
    }

    /* access modifiers changed from: 0000 */
    public final CharSequence getTitle() {
        if (this.mOriginalWindowCallback instanceof Activity) {
            return ((Activity) this.mOriginalWindowCallback).getTitle();
        }
        return this.mTitle;
    }
}
