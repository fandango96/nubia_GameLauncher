package cn.nubia.commonui.actionbar.internal.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.internal.view.SupportMenuItem;
import android.support.v4.util.SimpleArrayMap;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import cn.nubia.commonui.actionbar.internal.view.menu.MenuWrapperFactory;
import cn.nubia.commonui.actionbar.view.ActionMode.Callback;

@TargetApi(11)
public class SupportActionModeWrapper extends ActionMode {
    final Context mContext;
    final cn.nubia.commonui.actionbar.view.ActionMode mWrappedObject;

    public static class CallbackWrapper implements Callback {
        final SimpleArrayMap<cn.nubia.commonui.actionbar.view.ActionMode, SupportActionModeWrapper> mActionModes = new SimpleArrayMap<>();
        final Context mContext;
        final SimpleArrayMap<Menu, Menu> mMenus = new SimpleArrayMap<>();
        final ActionMode.Callback mWrappedCallback;

        public CallbackWrapper(Context context, ActionMode.Callback supportCallback) {
            this.mContext = context;
            this.mWrappedCallback = supportCallback;
        }

        public boolean onCreateActionMode(cn.nubia.commonui.actionbar.view.ActionMode mode, Menu menu) {
            return this.mWrappedCallback.onCreateActionMode(getActionModeWrapper(mode), getMenuWrapper(menu));
        }

        public boolean onPrepareActionMode(cn.nubia.commonui.actionbar.view.ActionMode mode, Menu menu) {
            return this.mWrappedCallback.onPrepareActionMode(getActionModeWrapper(mode), getMenuWrapper(menu));
        }

        public boolean onActionItemClicked(cn.nubia.commonui.actionbar.view.ActionMode mode, MenuItem item) {
            return this.mWrappedCallback.onActionItemClicked(getActionModeWrapper(mode), MenuWrapperFactory.wrapSupportMenuItem(this.mContext, (SupportMenuItem) item));
        }

        public void onDestroyActionMode(cn.nubia.commonui.actionbar.view.ActionMode mode) {
            this.mWrappedCallback.onDestroyActionMode(getActionModeWrapper(mode));
        }

        private Menu getMenuWrapper(Menu menu) {
            Menu wrapper = (Menu) this.mMenus.get(menu);
            if (wrapper != null) {
                return wrapper;
            }
            Menu wrapper2 = MenuWrapperFactory.wrapSupportMenu(this.mContext, (SupportMenu) menu);
            this.mMenus.put(menu, wrapper2);
            return wrapper2;
        }

        private ActionMode getActionModeWrapper(cn.nubia.commonui.actionbar.view.ActionMode mode) {
            SupportActionModeWrapper wrapper = (SupportActionModeWrapper) this.mActionModes.get(mode);
            if (wrapper != null) {
                return wrapper;
            }
            SupportActionModeWrapper wrapper2 = new SupportActionModeWrapper(this.mContext, mode);
            this.mActionModes.put(mode, wrapper2);
            return wrapper2;
        }
    }

    public SupportActionModeWrapper(Context context, cn.nubia.commonui.actionbar.view.ActionMode supportActionMode) {
        this.mContext = context;
        this.mWrappedObject = supportActionMode;
    }

    public Object getTag() {
        return this.mWrappedObject.getTag();
    }

    public void setTag(Object tag) {
        this.mWrappedObject.setTag(tag);
    }

    public void setTitle(CharSequence title) {
        this.mWrappedObject.setTitle(title);
    }

    public void setSubtitle(CharSequence subtitle) {
        this.mWrappedObject.setSubtitle(subtitle);
    }

    public void invalidate() {
        this.mWrappedObject.invalidate();
    }

    public void finish() {
        this.mWrappedObject.finish();
    }

    public Menu getMenu() {
        return MenuWrapperFactory.wrapSupportMenu(this.mContext, (SupportMenu) this.mWrappedObject.getMenu());
    }

    public CharSequence getTitle() {
        return this.mWrappedObject.getTitle();
    }

    public void setTitle(int resId) {
        this.mWrappedObject.setTitle(resId);
    }

    public CharSequence getSubtitle() {
        return this.mWrappedObject.getSubtitle();
    }

    public void setSubtitle(int resId) {
        this.mWrappedObject.setSubtitle(resId);
    }

    public View getCustomView() {
        return this.mWrappedObject.getCustomView();
    }

    public void setCustomView(View view) {
        this.mWrappedObject.setCustomView(view);
    }

    public MenuInflater getMenuInflater() {
        return this.mWrappedObject.getMenuInflater();
    }

    public boolean getTitleOptionalHint() {
        return this.mWrappedObject.getTitleOptionalHint();
    }

    public void setTitleOptionalHint(boolean titleOptional) {
        this.mWrappedObject.setTitleOptionalHint(titleOptional);
    }

    public boolean isTitleOptional() {
        return this.mWrappedObject.isTitleOptional();
    }
}
