package jp.wasabeef.glide.transformations.gpu;

import android.content.Context;
import android.graphics.Bitmap;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

public class GPUFilterTransformation implements Transformation<Bitmap> {
    private BitmapPool mBitmapPool;
    private Context mContext;
    private GPUImageFilter mFilter;

    public GPUFilterTransformation(Context context, GPUImageFilter filter) {
        this(context, Glide.get(context).getBitmapPool(), filter);
    }

    public GPUFilterTransformation(Context context, BitmapPool pool, GPUImageFilter filter) {
        this.mContext = context.getApplicationContext();
        this.mBitmapPool = pool;
        this.mFilter = filter;
    }

    public Resource<Bitmap> transform(Resource<Bitmap> resource, int outWidth, int outHeight) {
        Bitmap source = (Bitmap) resource.get();
        GPUImage gpuImage = new GPUImage(this.mContext);
        gpuImage.setImage(source);
        gpuImage.setFilter(this.mFilter);
        return BitmapResource.obtain(gpuImage.getBitmapWithFilterApplied(), this.mBitmapPool);
    }

    public String getId() {
        return getClass().getSimpleName();
    }

    public <T> T getFilter() {
        return this.mFilter;
    }
}
