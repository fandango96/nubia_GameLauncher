package cn.nubia.commonui.widget;

import android.content.Context;
import android.graphics.Outline;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.ImageView;
import cn.nubia.commonui.R;

public class FloatingActionButton extends ImageView {
    public FloatingActionButton(Context context) {
        this(context, null);
    }

    public FloatingActionButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
            public boolean onPreDraw() {
                FloatingActionButton.this.getViewTreeObserver().removeOnPreDrawListener(this);
                FloatingActionButton.this.setShader();
                return true;
            }
        });
    }

    /* access modifiers changed from: private */
    public void setShader() {
        setTranslationZ((float) getResources().getDimensionPixelSize(R.dimen.fab_shader));
        setOutlineProvider(new ViewOutlineProvider() {
            public void getOutline(View view, Outline outline) {
                outline.setOval(0, 0, view.getWidth(), view.getHeight());
            }
        });
    }
}
