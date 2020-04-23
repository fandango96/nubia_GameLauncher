package jp.wasabeef.glide.transformations.gpu;

import android.content.Context;
import android.graphics.PointF;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import java.util.Arrays;
import jp.co.cyberagent.android.gpuimage.GPUImageVignetteFilter;

public class VignetteFilterTransformation extends GPUFilterTransformation {
    private PointF mCenter;
    private float[] mVignetteColor;
    private float mVignetteEnd;
    private float mVignetteStart;

    public VignetteFilterTransformation(Context context) {
        this(context, Glide.get(context).getBitmapPool());
    }

    public VignetteFilterTransformation(Context context, BitmapPool pool) {
        this(context, pool, new PointF(0.5f, 0.5f), new float[]{0.0f, 0.0f, 0.0f}, 0.0f, 0.75f);
    }

    public VignetteFilterTransformation(Context context, PointF center, float[] color, float start, float end) {
        this(context, Glide.get(context).getBitmapPool(), center, color, start, end);
    }

    public VignetteFilterTransformation(Context context, BitmapPool pool, PointF center, float[] color, float start, float end) {
        super(context, pool, new GPUImageVignetteFilter());
        this.mCenter = center;
        this.mVignetteColor = color;
        this.mVignetteStart = start;
        this.mVignetteEnd = end;
        GPUImageVignetteFilter filter = (GPUImageVignetteFilter) getFilter();
        filter.setVignetteCenter(this.mCenter);
        filter.setVignetteColor(this.mVignetteColor);
        filter.setVignetteStart(this.mVignetteStart);
        filter.setVignetteEnd(this.mVignetteEnd);
    }

    public String getId() {
        return "VignetteFilterTransformation(center=" + this.mCenter.toString() + ",color=" + Arrays.toString(this.mVignetteColor) + ",start=" + this.mVignetteStart + ",end=" + this.mVignetteEnd + ")";
    }
}
