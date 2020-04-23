package jp.wasabeef.glide.transformations.gpu;

import android.content.Context;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import jp.co.cyberagent.android.gpuimage.GPUImageToonFilter;

public class ToonFilterTransformation extends GPUFilterTransformation {
    private float mQuantizationLevels;
    private float mThreshold;

    public ToonFilterTransformation(Context context) {
        this(context, Glide.get(context).getBitmapPool());
    }

    public ToonFilterTransformation(Context context, BitmapPool pool) {
        this(context, pool, 0.2f, 10.0f);
    }

    public ToonFilterTransformation(Context context, float threshold, float quantizationLevels) {
        this(context, Glide.get(context).getBitmapPool(), threshold, quantizationLevels);
    }

    public ToonFilterTransformation(Context context, BitmapPool pool, float threshold, float quantizationLevels) {
        super(context, pool, new GPUImageToonFilter());
        this.mThreshold = threshold;
        this.mQuantizationLevels = quantizationLevels;
        GPUImageToonFilter filter = (GPUImageToonFilter) getFilter();
        filter.setThreshold(this.mThreshold);
        filter.setQuantizationLevels(this.mQuantizationLevels);
    }

    public String getId() {
        return "ToonFilterTransformation(threshold=" + this.mThreshold + ",quantizationLevels=" + this.mQuantizationLevels + ")";
    }
}
