package cn.nubia.gamelauncherx.gamehandle;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import cn.nubia.gamelauncherx.R;

public class HandShankMoveAreaFloatView extends FrameLayout {
    private static final String TAG = "HandShankMoveAreaFloatView";
    private ImageView fx;
    private Context mContext;

    public HandShankMoveAreaFloatView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public HandShankMoveAreaFloatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public HandShankMoveAreaFloatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        RelativeLayout view = (RelativeLayout) RelativeLayout.inflate(this.mContext, R.layout.handle_rocker, null);
        addView(view);
        this.fx = (ImageView) view.findViewById(R.id.fx);
    }

    private void setFxBackground(float x, float y) {
        if (((double) x) <= -0.01d || ((double) x) >= 0.01d || ((double) y) <= -0.01d || ((double) y) >= 0.01d) {
            this.fx.setPressed(true);
        } else {
            this.fx.setPressed(false);
        }
    }

    public void setJoyStickMoveXY(float x, float y) {
        setFxBackground(x, y);
        int radius = ((getRight() - getLeft()) / 2) - ((this.fx.getRight() - this.fx.getLeft()) / 2);
        int fxW = this.fx.getWidth();
        int fxH = this.fx.getHeight();
        int moveX = (int) (((float) radius) * x);
        int moveY = (int) (((float) radius) * y);
        this.fx.layout(((getWidth() / 2) - (fxW / 2)) + moveX, ((getHeight() / 2) - (fxH / 2)) + moveY, (getWidth() / 2) + (fxW / 2) + moveX, (getHeight() / 2) + (fxH / 2) + moveY);
    }
}
