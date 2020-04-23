package cn.nubia.commonui.widget.tab;

import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;

public abstract class NubiaFragmentPagerAdapter extends FragmentPagerAdapterTab {
    public abstract long getItemId(int i);

    public abstract CharSequence getPageTitle(int i);

    public NubiaFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public Drawable getPageImage(int position) {
        return null;
    }
}
