package cn.nubia.commonui.actionbar.internal.widget;

import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;

class TintResources extends ResourcesWrapper {
    private final TintManager mTintManager;

    public TintResources(Resources resources, TintManager tintManager) {
        super(resources);
        this.mTintManager = tintManager;
    }

    public Drawable getDrawable(int id) throws NotFoundException {
        Drawable d = super.getDrawable(id);
        if (d != null) {
            this.mTintManager.tintDrawable(id, d);
        }
        return d;
    }
}
