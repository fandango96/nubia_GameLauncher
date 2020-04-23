package cn.nubia.commonui.widget.tab;

import android.graphics.drawable.Drawable;

public abstract class NubiaPagerAdapter extends PagerAdapterTab {
    public abstract long getItemId(int i);

    public abstract CharSequence getPageTitle(int i);

    public Drawable getPageImage(int position) {
        return null;
    }
}
