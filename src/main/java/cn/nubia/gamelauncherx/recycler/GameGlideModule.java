package cn.nubia.gamelauncherx.recycler;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.load.engine.cache.DiskCache.Factory;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.GlideModule;

import cn.nubia.gamelauncherx.util.LogUtil;

public class GameGlideModule implements GlideModule {
    public void applyOptions(Context context, GlideBuilder glideBuilder) {
        glideBuilder.setMemoryCache(new LruResourceCache(((int) Runtime.getRuntime().maxMemory()) / 8));
        glideBuilder.setDiskCache((Factory) new InternalCacheDiskCacheFactory(context, "glide_cache", 209715200));
        LogUtil.d("GameGlideModule", "-------->applyOptions()");
    }

    @Override
    public void registerComponents(@NonNull final Context context, @NonNull final Glide glide,
            @NonNull final Registry registry)
    {
        // Do nothing
    }
}
