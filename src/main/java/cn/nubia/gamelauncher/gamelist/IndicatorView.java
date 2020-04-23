package cn.nubia.gamelauncher.gamelist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import cn.nubia.gamelauncher.R;

public class IndicatorView extends View {
    private int currentIndicator;
    private Handler handler;
    private int indicatorCount;
    private int indicatorWidth;
    private Bitmap mLightBitmap;
    private Bitmap mNormalBitmap;
    private int marginWidth;

    public IndicatorView(Context context) {
        super(context);
        this.indicatorCount = 0;
        this.currentIndicator = 0;
        this.indicatorWidth = 0;
        this.marginWidth = 0;
        this.handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 18) {
                    IndicatorView.this.invalidate();
                }
            }
        };
    }

    public IndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.indicatorCount = 0;
        this.currentIndicator = 0;
        this.indicatorWidth = 0;
        this.marginWidth = 0;
        this.handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 18) {
                    IndicatorView.this.invalidate();
                }
            }
        };
        this.mLightBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.indicator_light);
        this.mNormalBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.indicator_normal);
        this.indicatorWidth = this.mNormalBitmap.getWidth();
        this.marginWidth = (int) getResources().getDimension(R.dimen.indicator_margin_width);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        int totalWidth = (this.indicatorWidth * this.indicatorCount) + (this.marginWidth * (this.indicatorCount - 1));
        if (this.indicatorCount > 1) {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            int top = (viewHeight - this.indicatorWidth) / 2;
            for (int i = 0; i < this.indicatorCount; i++) {
                int left = ((viewWidth - totalWidth) / 2) + ((this.indicatorWidth + this.marginWidth) * i);
                if (i == this.currentIndicator) {
                    canvas.drawBitmap(this.mLightBitmap, (float) left, (float) top, paint);
                } else {
                    canvas.drawBitmap(this.mNormalBitmap, (float) left, (float) top, paint);
                }
            }
        }
    }

    public void setIndicatorCount(int indicatorCount2) {
        this.indicatorCount = indicatorCount2;
    }

    public void setCurrentIndicator(int currentIndicator2) {
        this.currentIndicator = currentIndicator2;
        this.handler.sendEmptyMessage(18);
    }
}
