package com.bumptech.glide;

import android.content.Context;
import android.os.ParcelFileDescriptor;
import com.bumptech.glide.load.model.ImageVideoModelLoader;
import com.bumptech.glide.load.model.ImageVideoWrapper;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gifbitmap.GifBitmapWrapper;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;
import com.bumptech.glide.manager.Lifecycle;
import com.bumptech.glide.manager.RequestTracker;
import com.bumptech.glide.provider.FixedLoadProvider;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import java.io.File;
import java.io.InputStream;

public class DrawableTypeRequest<ModelType> extends DrawableRequestBuilder<ModelType> implements DownloadOptions {
    private final ModelLoader<ModelType, ParcelFileDescriptor> fileDescriptorModelLoader;
    private final OptionsApplier optionsApplier;
    private final ModelLoader<ModelType, InputStream> streamModelLoader;

    private static <A, Z, R> FixedLoadProvider<A, ImageVideoWrapper, Z, R> buildProvider(Glide glide, ModelLoader<A, InputStream> streamModelLoader2, ModelLoader<A, ParcelFileDescriptor> fileDescriptorModelLoader2, Class<Z> resourceClass, Class<R> transcodedClass, ResourceTranscoder<Z, R> transcoder) {
        if (streamModelLoader2 == null && fileDescriptorModelLoader2 == null) {
            return null;
        }
        if (transcoder == null) {
            transcoder = glide.buildTranscoder(resourceClass, transcodedClass);
        }
        return new FixedLoadProvider<>(new ImageVideoModelLoader<>(streamModelLoader2, fileDescriptorModelLoader2), transcoder, glide.buildDataProvider(ImageVideoWrapper.class, resourceClass));
    }

    DrawableTypeRequest(Class<ModelType> modelClass, ModelLoader<ModelType, InputStream> streamModelLoader2, ModelLoader<ModelType, ParcelFileDescriptor> fileDescriptorModelLoader2, Context context, Glide glide, RequestTracker requestTracker, Lifecycle lifecycle, OptionsApplier optionsApplier2) {
        FixedLoadProvider buildProvider = buildProvider(glide, streamModelLoader2, fileDescriptorModelLoader2, GifBitmapWrapper.class, GlideDrawable.class, null);
        super(context, modelClass, buildProvider, glide, requestTracker, lifecycle);
        this.streamModelLoader = streamModelLoader2;
        this.fileDescriptorModelLoader = fileDescriptorModelLoader2;
        this.optionsApplier = optionsApplier2;
    }

    public BitmapTypeRequest<ModelType> asBitmap() {
        return (BitmapTypeRequest) this.optionsApplier.apply(new BitmapTypeRequest(this, this.streamModelLoader, this.fileDescriptorModelLoader, this.optionsApplier));
    }

    public GifTypeRequest<ModelType> asGif() {
        return (GifTypeRequest) this.optionsApplier.apply(new GifTypeRequest(this, this.streamModelLoader, this.optionsApplier));
    }

    public <Y extends Target<File>> Y downloadOnly(Y target) {
        return getDownloadOnlyRequest().downloadOnly(target);
    }

    public FutureTarget<File> downloadOnly(int width, int height) {
        return getDownloadOnlyRequest().downloadOnly(width, height);
    }

    private GenericTranscodeRequest<ModelType, InputStream, File> getDownloadOnlyRequest() {
        return (GenericTranscodeRequest) this.optionsApplier.apply(new GenericTranscodeRequest(File.class, this, this.streamModelLoader, InputStream.class, File.class, this.optionsApplier));
    }
}
