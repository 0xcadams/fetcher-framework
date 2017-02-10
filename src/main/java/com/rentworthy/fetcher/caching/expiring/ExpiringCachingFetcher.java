package com.rentworthy.fetcher.caching.expiring;

import com.rentworthy.fetcher.Fetcher;
import com.rentworthy.fetcher.caching.CachingFetcher;
import com.rentworthy.fetcher.exception.FetcherException;

public class ExpiringCachingFetcher<T> extends CachingFetcher<T> {

    private static final double DEFAULT_MAX_CACHE_TIME = Double.MAX_VALUE;
    private volatile long lastClearTime;
    private final double maxCacheTime;

    public ExpiringCachingFetcher(final Fetcher<T> fetcher) {
        this(fetcher, ExpiringCachingFetcher.DEFAULT_MAX_CACHE_TIME);
    }

    public ExpiringCachingFetcher(final Fetcher<T> fetcher, final int maxCacheTime) {
        this(fetcher, (double) maxCacheTime);
    }

    public ExpiringCachingFetcher(final Fetcher<T> fetcher, final double maxCacheTime) {
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
