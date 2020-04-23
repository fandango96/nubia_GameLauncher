package cn.nubia.gamelauncher.recycler;

import android.content.Context;
import cn.nubia.gamelauncher.util.LogUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.DiskCache.Factory;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.GlideModule;

public class GameGlideModule implements GlideModule {
    public void applyOptions(Context context, GlideBuilder glideBuilder) {
        glideBuilder.setMemoryCache(new LruResourceCache(((int) Runtime.getRuntime().maxMemory()) / 8));
        glideBuilder.setDiskCache((Factory) new InternalCacheDiskCacheFactory(context, "glide_cache", 209715200));
        glideBuilder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
        LogUtil.d("GameGlideModule", "-------->applyOptions()");
    }

    public void registerComponents(Context context, Glide glide) {
    }
}
