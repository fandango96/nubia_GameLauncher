package jp.wasabeef.glide.transformations.gpu;

import android.content.Context;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import jp.co.cyberagent.android.gpuimage.GPUImageBrightnessFilter;

public class BrightnessFilterTransformation extends GPUFilterTransformation {
    private float mBrightness;

    public BrightnessFilterTransformation(Context context) {
        this(context, Glide.get(context).getBitmapPool());
    }

    public BrightnessFilterTransformation(Context context, BitmapPool pool) {
        this(context, pool, 0.0f);
    }

    public BrightnessFilterTransformation(Context context, float brightness) {
        this(context, Glide.get(context).getBitmapPool(), brightness);
    }

    public BrightnessFilterTransformation(Context context, BitmapPool pool, float brightness) {
        super(context, pool, new GPUImageBrightnessFilter());
        this.mBrightness = brightness;
        ((GPUImageBrightnessFilter) getFilter()).setBrightness(this.mBrightness);
    }

    public String getId() {
        return "BrightnessFilterTransformation(brightness=" + this.mBrightness + ")";
    }
}
