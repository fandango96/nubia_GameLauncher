package cn.nubia.commonui.actionbar.internal.widget;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;

class TintContextWrapper extends ContextWrapper {
    private final TintManager mTintManager;

    public static Context wrap(Context context) {
        if (!(context instanceof TintContextWrapper)) {
            return new TintContextWrapper(context);
        }
        return context;
    }

    TintContextWrapper(Context base) {
        super(base);
        this.mTintManager = new TintManager(base);
    }

    public Resources getResources() {
        return this.mTintManager.getResources();
    }

    /* access modifiers changed from: 0000 */
    public final TintManager getTintManager() {
        return this.mTintManager;
    }
}
