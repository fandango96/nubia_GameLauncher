package cn.nubia.gamelauncherx.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

public class DrawableLeftTextView extends TextView {
    public DrawableLeftTextView(Context context) {
        super(context);
    }

    public DrawableLeftTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawableLeftTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Drawable[] drawables = getCompoundDrawables();
        float textWidth = 100.0f;
        if (drawables != null) {
            Drawable drawableLeft = drawables[0];
            int padding = getPaddingLeft() + getPaddingRight();
            if (getLayout() != null) {
                textWidth = getLayout().getLineWidth(0);
            } else if (!(getPaint() == null || getText() == null)) {
                textWidth = getPaint().measureText(getText().toString().trim());
            }
            float bodyWidth = textWidth;
            if (drawableLeft == null) {
                canvas.translate(((((float) getWidth()) - bodyWidth) - ((float) padding)) / 2.0f, 0.0f);
            }
            super.onDraw(canvas);
        }
    }
}
