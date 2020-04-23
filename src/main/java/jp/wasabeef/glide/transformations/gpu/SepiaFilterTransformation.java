package jp.wasabeef.glide.transformations.gpu;

import android.content.Context;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import jp.co.cyberagent.android.gpuimage.GPUImageSepiaFilter;

public class SepiaFilterTransformation extends GPUFilterTransformation {
    private float mIntensity;

    public SepiaFilterTransformation(Context context) {
        this(context, Glide.get(context).getBitmapPool());
    }

    public SepiaFilterTransformation(Context context, BitmapPool pool) {
        this(context, pool, 1.0f);
    }

    public SepiaFilterTransformation(Context context, float intensity) {
        this(context, Glide.get(context).getBitmapPool(), intensity);
    }

    public SepiaFilterTransformation(Context context, BitmapPool pool, float intensity) {
        super(context, pool, new GPUImageSepiaFilter());
        this.mIntensity = intensity;
        ((GPUImageSepiaFilter) getFilter()).setIntensity(this.mIntensity);
    }

    public String getId() {
        return "SepiaFilterTransformation(intensity=" + this.mIntensity + ")";
    }
}
