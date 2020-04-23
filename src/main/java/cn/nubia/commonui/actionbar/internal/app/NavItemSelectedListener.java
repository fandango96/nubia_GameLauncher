package cn.nubia.commonui.actionbar.internal.app;

import android.view.View;
import cn.nubia.commonui.actionbar.app.ActionBar.OnNavigationListener;
import cn.nubia.commonui.actionbar.internal.widget.AdapterViewCompat;
import cn.nubia.commonui.actionbar.internal.widget.AdapterViewCompat.OnItemSelectedListener;

class NavItemSelectedListener implements OnItemSelectedListener {
    private final OnNavigationListener mListener;

    public NavItemSelectedListener(OnNavigationListener listener) {
        this.mListener = listener;
    }

    public void onItemSelected(AdapterViewCompat<?> adapterViewCompat, View view, int position, long id) {
        if (this.mListener != null) {
            this.mListener.onNavigationItemSelected(position, id);
        }
    }

    public void onNothingSelected(AdapterViewCompat<?> adapterViewCompat) {
    }
}
