package jp.wasabeef.glide.transformations.gpu;

import android.content.Context;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import jp.co.cyberagent.android.gpuimage.GPUImageKuwaharaFilter;

public class KuwaharaFilterTransformation extends GPUFilterTransformation {
    private int mRadius;

    public KuwaharaFilterTransformation(Context context) {
        this(context, Glide.get(context).getBitmapPool());
    }

    public KuwaharaFilterTransformation(Context context, BitmapPool pool) {
        this(context, pool, 25);
    }

    public KuwaharaFilterTransformation(Context context, int radius) {
        this(context, Glide.get(context).getBitmapPool(), radius);
    }

    public KuwaharaFilterTransformation(Context context, BitmapPool pool, int radius) {
        super(context, pool, new GPUImageKuwaharaFilter());
        this.mRadius = radius;
        ((GPUImageKuwaharaFilter) getFilter()).setRadius(this.mRadius);
    }

    public String getId() {
        return "KuwaharaFilterTransformation(radius=" + this.mRadius + ")";
    }
}
