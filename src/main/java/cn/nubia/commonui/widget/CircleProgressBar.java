package cn.nubia.commonui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import cn.nubia.commonui.R;

public class CircleProgressBar extends View {
    private Paint mCirclePaint;
    private float mCircleRadius;
    private int mCircleSize;
    private float mDrawTextX;
    private float mDrawTextY;
    private int mMax;
    private int mProgress;
    private int mReachedBarColor;
    private Paint mSectorPaint;
    private int mTextColor;
    private Paint mTextPaint;
    private float mTextSize;
    private int mUnreachedBarColor;

    public CircleProgressBar(Context context) {
        this(context, null);
    }

    public CircleProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.circleProgressBarStyle);
    }

    public CircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mProgress = 0;
        this.mMax = 100;
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar, defStyleAttr, 0);
        this.mMax = attributes.getInt(R.styleable.CircleProgressBar_maxProgressNubia, 100);
        this.mCircleRadius = (float) attributes.getDimensionPixelSize(R.styleable.CircleProgressBar_progressCircleRadius, getResources().getDimensionPixelSize(R.dimen.nubia_circle_progress_radius_default));
        this.mReachedBarColor = attributes.getInt(R.styleable.CircleProgressBar_progressReachedColor, getResources().getColor(R.color.nubia_circle_progress_reached_color));
        this.mUnreachedBarColor = attributes.getInt(R.styleable.CircleProgressBar_progressUnreachedColor, getResources().getColor(R.color.nubia_circle_progress_unreached_color));
        this.mTextColor = attributes.getInt(R.styleable.CircleProgressBar_progressTextColor, getResources().getColor(R.color.nubia_circle_progress_reached_color));
        this.mTextSize = (float) attributes.getDimensionPixelSize(R.styleable.CircleProgressBar_progressTextSize, getResources().getDimensionPixelSize(R.dimen.nubia_text_size_extrasmall));
        this.mCircleSize = attributes.getDimensionPixelSize(R.styleable.CircleProgressBar_progressCircleSize, getResources().getDimensionPixelSize(R.dimen.nubia_circle_progress_width_default));
        attributes.recycle();
        initPaint();
    }

    private void initPaint() {
        this.mTextPaint = new Paint(1);
        this.mTextPaint.setTextAlign(Align.CENTER);
        this.mTextPaint.setColor(this.mTextColor);
        this.mTextPaint.setTextSize(this.mTextSize);
        this.mCirclePaint = new Paint(1);
        this.mCirclePaint.setStrokeWidth((float) this.mCircleSize);
        this.mCirclePaint.setColor(this.mUnreachedBarColor);
        this.mCirclePaint.setStyle(Style.STROKE);
        this.mSectorPaint = new Paint(1);
        this.mSectorPaint.setStrokeWidth((float) this.mCircleSize);
        this.mSectorPaint.setStyle(Style.STROKE);
        this.mSectorPaint.setColor(this.mReachedBarColor);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec, true), measure(heightMeasureSpec, false));
    }

    private int measure(int measureSpec, boolean isWidth) {
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int offset = this.mCircleSize;
        if (mode == 1073741824) {
            return size;
        }
        return (((int) this.mCircleRadius) * 2) + offset;
    }

    private void calculateCircleCenter() {
        this.mDrawTextX = (((float) ((getWidth() - getPaddingLeft()) - getPaddingRight())) / 2.0f) + ((float) getPaddingLeft());
        this.mDrawTextY = (((float) ((getHeight() - getPaddingTop()) - getPaddingBottom())) / 2.0f) + ((float) getPaddingTop());
    }

    public void onDraw(Canvas canvas) {
        calculateCircleCenter();
        canvas.drawCircle(this.mDrawTextX, this.mDrawTextY, this.mCircleRadius, this.mCirclePaint);
        canvas.drawText(getProgress() + "%", this.mDrawTextX, this.mDrawTextY + ((float) ((int) (this.mTextSize * 0.4f))), this.mTextPaint);
        Canvas canvas2 = canvas;
        canvas2.drawArc(new RectF((float) ((int) (this.mDrawTextX - this.mCircleRadius)), (float) ((int) (this.mDrawTextY - this.mCircleRadius)), (float) ((int) (this.mDrawTextX + this.mCircleRadius)), (float) ((int) (this.mDrawTextY + this.mCircleRadius))), -90.0f, (float) ((getProgress() * 360) / getMax()), false, this.mSectorPaint);
    }

    public int getMax() {
        return this.mMax;
    }

    public void setMax(int max) {
        this.mMax = max;
    }

    public int getProgress() {
        return this.mProgress;
    }

    public void setProgress(int progress) {
        if (progress < 0) {
            progress = 0;
        } else if (progress > this.mMax) {
            progress = this.mMax;
        }
        if (progress != this.mProgress) {
            this.mProgress = progress;
            invalidate();
        }
    }
}
