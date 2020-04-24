package cn.nubia.gamelauncherx.view;

import android.content.Context;
import android.view.View;

public class TestView extends View {
    public TestView(Context context) {
        super(context);
    }

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        setVisibility(4);
    }
}
