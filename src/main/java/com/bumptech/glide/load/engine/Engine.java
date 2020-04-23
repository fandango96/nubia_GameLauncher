package com.bumptech.glide.load.engine;

import android.os.Looper;
import android.os.MessageQueue.IdleHandler;
import android.util.Log;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskCache.Factory;
import com.bumptech.glide.load.engine.cache.DiskCacheAdapter;
import com.bumptech.glide.load.engine.cache.MemoryCache;
import com.bumptech.glide.load.engine.cache.MemoryCache.ResourceRemovedListener;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;
import com.bumptech.glide.provider.DataLoadProvider;
import com.bumptech.glide.request.ResourceCallback;
import com.bumptech.glide.util.LogTime;
import com.bumptech.glide.util.Util;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class Engine implements EngineJobListener, ResourceRemovedListener, ResourceListener {
    private static final String TAG = "Engine";
    private final Map<Key, WeakReference<EngineResource<?>>> activeResources;
    private final MemoryCache cache;
    private final LazyDiskCacheProvider diskCacheProvider;
    private final EngineJobFactory engineJobFactory;
    private final Map<Key, EngineJob> jobs;
    private final EngineKeyFactory keyFactory;
    private final ResourceRecycler resourceRecycler;
    private ReferenceQueue<EngineResource<?>> resourceReferenceQueue;

    static class EngineJobFactory {
        private final ExecutorService diskCacheService;
        private final EngineJobListener listener;
        private final ExecutorService sourceService;

        public EngineJobFactory(ExecutorService diskCacheService2, ExecutorService sourceService2, EngineJobListener listener2) {
            this.diskCacheService = diskCacheService2;
            this.sourceService = sourceService2;
            this.listener = listener2;
        }

        public EngineJob build(Key key, boolean isMemoryCacheable) {
            return new EngineJob(key, this.diskCacheService, this.sourceService, isMemoryCacheable, this.listener);
        }
    }

    private static class LazyDiskCacheProvider implements DiskCacheProvider {
        private volatile DiskCache diskCache;
        private final Factory factory;

        public LazyDiskCacheProvider(Factory factory2) {
            this.factory = factory2;
        }

        public DiskCache getDiskCache() {
            if (this.diskCache == null) {
                synchronized (this) {
                    if (this.diskCache == null) {
                        this.diskCache = this.factory.build();
                    }
                    if (this.diskCache == null) {
                        this.diskCache = new DiskCacheAdapter();
                    }
                }
            }
            return this.diskCache;
        }
    }

    public static class LoadStatus {
        private final ResourceCallback cb;
        private final EngineJob engineJob;

        public LoadStatus(ResourceCallback cb2, EngineJob engineJob2) {
            this.cb = cb2;
            this.engineJob = engineJob2;
        }

        public void cancel() {
            this.engineJob.removeCallback(this.cb);
        }
    }

    private static class RefQueueIdleHandler implements IdleHandler {
        private final Map<Key, WeakReference<EngineResource<?>>> activeResources;
        private final ReferenceQueue<EngineResource<?>> queue;

        public RefQueueIdleHandler(Map<Key, WeakReference<EngineResource<?>>> activeResources2, ReferenceQueue<EngineResource<?>> queue2) {
            this.activeResources = activeResources2;
            this.queue = queue2;
        }

        public boolean queueIdle() {
            ResourceWeakReference ref = (ResourceWeakReference) this.queue.poll();
            if (ref != null) {
                this.activeResources.remove(ref.key);
            }
            return true;
        }
    }

    private static class ResourceWeakReference extends WeakReference<EngineResource<?>> {
        /* access modifiers changed from: private */
        public final Key key;

        public ResourceWeakReference(Key key2, EngineResource<?> r, ReferenceQueue<? super EngineResource<?>> q) {
            super(r, q);
            this.key = key2;
        }
    }

    public Engine(MemoryCache memoryCache, Factory diskCacheFactory, ExecutorService diskCacheService, ExecutorService sourceService) {
        this(memoryCache, diskCacheFactory, diskCacheService, sourceService, null, null, null, null, null);
    }

    Engine(MemoryCache cache2, Factory diskCacheFactory, ExecutorService diskCacheService, ExecutorService sourceService, Map<Key, EngineJob> jobs2, EngineKeyFactory keyFactory2, Map<Key, WeakReference<EngineResource<?>>> activeResources2, EngineJobFactory engineJobFactory2, ResourceRecycler resourceRecycler2) {
        this.cache = cache2;
        this.diskCacheProvider = new LazyDiskCacheProvider(diskCacheFactory);
        if (activeResources2 == null) {
            activeResources2 = new HashMap<>();
        }
        this.activeResources = activeResources2;
        if (keyFactory2 == null) {
            keyFactory2 = new EngineKeyFactory();
        }
        this.keyFactory = keyFactory2;
        if (jobs2 == null) {
            jobs2 = new HashMap<>();
        }
        this.jobs = jobs2;
        if (engineJobFactory2 == null) {
            engineJobFactory2 = new EngineJobFactory(diskCacheService, sourceService, this);
        }
        this.engineJobFactory = engineJobFactory2;
        if (resourceRecycler2 == null) {
            resourceRecycler2 = new ResourceRecycler();
        }
        this.resourceRecycler = resourceRecycler2;
        cache2.setResourceRemovedListener(this);
    }

    public <T, Z, R> LoadStatus load(Key signature, int width, int height, DataFetcher<T> fetcher, DataLoadProvider<T, Z> loadProvider, Transformation<Z> transformation, ResourceTranscoder<Z, R> transcoder, Priority priority, boolean isMemoryCacheable, DiskCacheStrategy diskCacheStrategy, ResourceCallback cb) {
        Util.assertMainThread();
        long startTime = LogTime.getLogTime();
        EngineKey key = this.keyFactory.buildKey(fetcher.getId(), signature, width, height, loadProvider.getCacheDecoder(), loadProvider.getSourceDecoder(), transformation, loadProvider.getEncoder(), transcoder, loadProvider.getSourceEncoder());
        EngineResource<?> cached = loadFromCache(key, isMemoryCacheable);
        if (cached != null) {
            cb.onResourceReady(cached);
            if (Log.isLoggable(TAG, 2)) {
                logWithTimeAndKey("Loaded resource from cache", startTime, key);
            }
            return null;
        }
        EngineResource<?> active = loadFromActiveResources(key, isMemoryCacheable);
        if (active != null) {
            cb.onResourceReady(active);
            if (Log.isLoggable(TAG, 2)) {
                logWithTimeAndKey("Loaded resource from active resources", startTime, key);
            }
            return null;
        }
        EngineJob current = (EngineJob) this.jobs.get(key);
        if (current != null) {
            current.addCallback(cb);
            if (Log.isLoggable(TAG, 2)) {
                logWithTimeAndKey("Added to existing load", startTime, key);
            }
            return new LoadStatus(cb, current);
        }
        EngineJob engineJob = this.engineJobFactory.build(key, isMemoryCacheable);
        EngineJob engineJob2 = engineJob;
        EngineRunnable engineRunnable = new EngineRunnable(engineJob2, new DecodeJob<>(key, width, height, fetcher, loadProvider, transformation, transcoder, this.diskCacheProvider, diskCacheStrategy, priority), priority);
        this.jobs.put(key, engineJob);
        engineJob.addCallback(cb);
        engineJob.start(engineRunnable);
        if (Log.isLoggable(TAG, 2)) {
            logWithTimeAndKey("Started new load", startTime, key);
        }
        return new LoadStatus(cb, engineJob);
    }

    private static void logWithTimeAndKey(String log, long startTime, Key key) {
        Log.v(TAG, log + " in " + LogTime.getElapsedMillis(startTime) + "ms, key: " + key);
    }

    private EngineResource<?> loadFromActiveResources(Key key, boolean isMemoryCacheable) {
        if (!isMemoryCacheable) {
            return null;
        }
        WeakReference<EngineResource<?>> activeRef = (WeakReference) this.activeResources.get(key);
        if (activeRef == null) {
            return null;
        }
        EngineResource<?> active = (EngineResource) activeRef.get();
        if (active != null) {
            active.acquire();
            return active;
        }
        this.activeResources.remove(key);
        return active;
    }

    private EngineResource<?> loadFromCache(Key key, boolean isMemoryCacheable) {
        if (!isMemoryCacheable) {
            return null;
        }
        EngineResource<?> cached = getEngineResourceFromCache(key);
        if (cached == null) {
            return cached;
        }
        cached.acquire();
        this.activeResources.put(key, new ResourceWeakReference(key, cached, getReferenceQueue()));
        return cached;
    }

    private EngineResource<?> getEngineResourceFromCache(Key key) {
        Resource<?> cached = this.cache.remove(key);
        if (cached == null) {
            return null;
        }
        if (cached instanceof EngineResource) {
            return (EngineResource) cached;
        }
        return new EngineResource(cached, true);
    }

    public void release(Resource resource) {
        Util.assertMainThread();
        if (resource instanceof EngineResource) {
            ((EngineResource) resource).release();
            return;
        }
        throw new IllegalArgumentException("Cannot release anything but an EngineResource");
    }

    public void onEngineJobComplete(Key key, EngineResource<?> resource) {
        Util.assertMainThread();
        if (resource != null) {
            resource.setResourceListener(key, this);
            if (resource.isCacheable()) {
                this.activeResources.put(key, new ResourceWeakReference(key, resource, getReferenceQueue()));
            }
        }
        this.jobs.remove(key);
    }

    public void onEngineJobCancelled(EngineJob engineJob, Key key) {
        Util.assertMainThread();
        if (engineJob.equals((EngineJob) this.jobs.get(key))) {
            this.jobs.remove(key);
        }
    }

    public void onResourceRemoved(Resource<?> resource) {
        Util.assertMainThread();
        this.resourceRecycler.recycle(resource);
    }

    public void onResourceReleased(Key cacheKey, EngineResource resource) {
        Util.assertMainThread();
        this.activeResources.remove(cacheKey);
        if (resource.isCacheable()) {
            this.cache.put(cacheKey, resource);
        } else {
            this.resourceRecycler.recycle(resource);
        }
    }

    public void clearDiskCache() {
        this.diskCacheProvider.getDiskCache().clear();
    }

    private ReferenceQueue<EngineResource<?>> getReferenceQueue() {
        if (this.resourceReferenceQueue == null) {
            this.resourceReferenceQueue = new ReferenceQueue<>();
            Looper.myQueue().addIdleHandler(new RefQueueIdleHandler(this.activeResources, this.resourceReferenceQueue));
        }
        return this.resourceReferenceQueue;
    }
}
