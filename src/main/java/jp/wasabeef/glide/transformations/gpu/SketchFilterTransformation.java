package jp.wasabeef.glide.transformations.gpu;

import android.content.Context;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import jp.co.cyberagent.android.gpuimage.GPUImageSketchFilter;

public class SketchFilterTransformation extends GPUFilterTransformation {
    public SketchFilterTransformation(Context context) {
        this(context, Glide.get(context).getBitmapPool());
    }

    public SketchFilterTransformation(Context context, BitmapPool pool) {
        super(context, pool, new GPUImageSketchFilter());
    }

    public String getId() {
        return "SketchFilterTransformation()";
    }
}
