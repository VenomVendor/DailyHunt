package com.venomvendor.dailyhunt.core;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.module.GlideModule;
import com.venomvendor.dailyhunt.util.AppUtils;

import java.io.File;

public class GlideConfiguration implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // Apply options to the builder here.
        DiskCache.Factory diskCacheFactory = new ExternalGlideCacheDiskCacheFactory(context);
        builder.setDiskCache(diskCacheFactory);
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        // register ModelLoaders here.
    }

    private class ExternalGlideCacheDiskCacheFactory extends DiskLruCacheFactory {

        public ExternalGlideCacheDiskCacheFactory(Context context) {
            this(context, DiskCache.Factory.DEFAULT_DISK_CACHE_SIZE);
        }

        public ExternalGlideCacheDiskCacheFactory(Context context, int diskCacheSize) {
            this(context, DiskCache.Factory.DEFAULT_DISK_CACHE_DIR, diskCacheSize);
        }

        public ExternalGlideCacheDiskCacheFactory(final Context context, final String diskCacheName, int diskCacheSize) {
            super(new CacheDirectoryGetter() {
                @Override
                public File getCacheDirectory() {
                    File cacheDirectory = AppUtils.getStorageLocation();
                    if (cacheDirectory == null) {
                        return null;
                    }
                    if (diskCacheName != null) {
                        return new File(cacheDirectory, diskCacheName);
                    }
                    return cacheDirectory;
                }
            }, diskCacheSize);
        }
    }
}
