package cn.nubia.commonui.actionbar.internal.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.widget.RatingBar;

public class TintRatingBar extends RatingBar {
    private static final int[] TINT_ATTRS = {16843067, 16843068};
    private Bitmap mSampleTile;

    public TintRatingBar(Context context) {
        this(context, null);
    }

    public TintRatingBar(Context context, AttributeSet attrs) {
        this(context, attrs, 16842876);
    }

    public TintRatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (TintManager.SHOULD_BE_USED) {
            TintTypedArray a = TintTypedArray.obtainStyledAttributes(getContext(), attrs, TINT_ATTRS, defStyleAttr, 0);
            Drawable drawable = a.getDrawable(0);
            if (drawable != null) {
                setIndeterminateDrawable(tileifyIndeterminate(drawable));
            }
            Drawable drawable2 = a.getDrawable(1);
            if (drawable2 != null) {
                setProgressDrawable(tileify(drawable2, false));
            }
            a.recycle();
        }
    }

    /* JADX WARNING: type inference failed for: r10v7, types: [android.graphics.drawable.ClipDrawable] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.graphics.drawable.Drawable tileify(android.graphics.drawable.Drawable r14, boolean r15) {
        /*
            r13 = this;
            r11 = 1
            boolean r10 = r14 instanceof android.support.v4.graphics.drawable.DrawableWrapper
            if (r10 == 0) goto L_0x001a
            r10 = r14
            android.support.v4.graphics.drawable.DrawableWrapper r10 = (android.support.v4.graphics.drawable.DrawableWrapper) r10
            android.graphics.drawable.Drawable r5 = r10.getWrappedDrawable()
            if (r5 == 0) goto L_0x0018
            android.graphics.drawable.Drawable r5 = r13.tileify(r5, r15)
            r10 = r14
            android.support.v4.graphics.drawable.DrawableWrapper r10 = (android.support.v4.graphics.drawable.DrawableWrapper) r10
            r10.setWrappedDrawable(r5)
        L_0x0018:
            r6 = r14
        L_0x0019:
            return r6
        L_0x001a:
            boolean r10 = r14 instanceof android.graphics.drawable.LayerDrawable
            if (r10 == 0) goto L_0x005a
            r1 = r14
            android.graphics.drawable.LayerDrawable r1 = (android.graphics.drawable.LayerDrawable) r1
            int r0 = r1.getNumberOfLayers()
            android.graphics.drawable.Drawable[] r7 = new android.graphics.drawable.Drawable[r0]
            r3 = 0
        L_0x0028:
            if (r3 >= r0) goto L_0x0048
            int r4 = r1.getId(r3)
            android.graphics.drawable.Drawable r12 = r1.getDrawable(r3)
            r10 = 16908301(0x102000d, float:2.3877265E-38)
            if (r4 == r10) goto L_0x003c
            r10 = 16908303(0x102000f, float:2.387727E-38)
            if (r4 != r10) goto L_0x0046
        L_0x003c:
            r10 = r11
        L_0x003d:
            android.graphics.drawable.Drawable r10 = r13.tileify(r12, r10)
            r7[r3] = r10
            int r3 = r3 + 1
            goto L_0x0028
        L_0x0046:
            r10 = 0
            goto L_0x003d
        L_0x0048:
            android.graphics.drawable.LayerDrawable r6 = new android.graphics.drawable.LayerDrawable
            r6.<init>(r7)
            r3 = 0
        L_0x004e:
            if (r3 >= r0) goto L_0x0019
            int r10 = r1.getId(r3)
            r6.setId(r3, r10)
            int r3 = r3 + 1
            goto L_0x004e
        L_0x005a:
            boolean r10 = r14 instanceof android.graphics.drawable.BitmapDrawable
            if (r10 == 0) goto L_0x0018
            android.graphics.drawable.BitmapDrawable r14 = (android.graphics.drawable.BitmapDrawable) r14
            android.graphics.Bitmap r9 = r14.getBitmap()
            android.graphics.Bitmap r10 = r13.mSampleTile
            if (r10 != 0) goto L_0x006a
            r13.mSampleTile = r9
        L_0x006a:
            android.graphics.drawable.ShapeDrawable r8 = new android.graphics.drawable.ShapeDrawable
            android.graphics.drawable.shapes.Shape r10 = r13.getDrawableShape()
            r8.<init>(r10)
            android.graphics.BitmapShader r2 = new android.graphics.BitmapShader
            android.graphics.Shader$TileMode r10 = android.graphics.Shader.TileMode.REPEAT
            android.graphics.Shader$TileMode r12 = android.graphics.Shader.TileMode.CLAMP
            r2.<init>(r9, r10, r12)
            android.graphics.Paint r10 = r8.getPaint()
            r10.setShader(r2)
            if (r15 == 0) goto L_0x008c
            android.graphics.drawable.ClipDrawable r10 = new android.graphics.drawable.ClipDrawable
            r12 = 3
            r10.<init>(r8, r12, r11)
            r8 = r10
        L_0x008c:
            r6 = r8
            goto L_0x0019
        */
        throw new UnsupportedOperationException("Method not decompiled: cn.nubia.commonui.actionbar.internal.widget.TintRatingBar.tileify(android.graphics.drawable.Drawable, boolean):android.graphics.drawable.Drawable");
    }

    private Drawable tileifyIndeterminate(Drawable drawable) {
        if (!(drawable instanceof AnimationDrawable)) {
            return drawable;
        }
        AnimationDrawable background = (AnimationDrawable) drawable;
        int N = background.getNumberOfFrames();
        AnimationDrawable newBg = new AnimationDrawable();
        newBg.setOneShot(background.isOneShot());
        for (int i = 0; i < N; i++) {
            Drawable frame = tileify(background.getFrame(i), true);
            frame.setLevel(10000);
            newBg.addFrame(frame, background.getDuration(i));
        }
        newBg.setLevel(10000);
        return newBg;
    }

    private Shape getDrawableShape() {
        return new RoundRectShape(new float[]{5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f}, null, null);
    }

    /* access modifiers changed from: protected */
    public synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.mSampleTile != null) {
            setMeasuredDimension(ViewCompat.resolveSizeAndState(this.mSampleTile.getWidth() * getNumStars(), widthMeasureSpec, 0), getMeasuredHeight());
        }
    }
}
