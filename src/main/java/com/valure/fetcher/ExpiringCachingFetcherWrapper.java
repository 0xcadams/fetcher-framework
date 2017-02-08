package com.valure.fetcher;

import com.valure.fetcher.exception.FetcherException;

public class ExpiringCachingFetcherWrapper<T> extends CachingFetcherWrapper<T> {

    private static final double DEFAULT_MAX_CACHE_TIME = Double.MAX_VALUE;
    private volatile long lastClearTime;
    private final double maxCacheTime;

    public ExpiringCachingFetcherWrapper(final Fetcher<T> fetcher) {
        this(fetcher, ExpiringCachingFetcherWrapper.DEFAULT_MAX_CACHE_TIME);
    }

    public ExpiringCachingFetcherWrapper(final Fetcher<T> fetcher, final int maxCacheTime) {
        this(fetcher, (double) maxCacheTime);
    }

    public ExpiringCachingFetcherWrapper(final Fetcher<T> fetcher, final double maxCacheTime) {
        super(fetcher);
        this.lastClearTime = System.currentTimeMillis();
        this.maxCacheTime = maxCacheTime;
    }

    @Override
    public synchronized T fetch() throws FetcherException {

        if ((System.currentTimeMillis() - this.lastClearTime) >= this.maxCacheTime) {
            this.clearCachedObject();
            this.lastClearTime = System.currentTimeMillis();
        }

        return super.fetch();

    }

}
