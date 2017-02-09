package com.rentworthy.fetcher.caching.concurrent.multi.expiring;

import com.rentworthy.fetcher.caching.concurrent.NonBlockingConcurrentFetcherWrapper;
import com.rentworthy.fetcher.caching.concurrent.multi.CachingNonBlockingConcurrentFetcherWrapper;
import com.rentworthy.fetcher.exception.FetcherException;
import com.rentworthy.fetcher.response.FetcherResponse;

public class ExpiringCachingConcurrentFetcherWrapper<T> extends CachingNonBlockingConcurrentFetcherWrapper<T> {

    private static final double DEFAULT_MAX_CACHE_TIME = Double.MAX_VALUE;
    private volatile long lastClearTime;
    private final double maxCacheTimeMs;

    public ExpiringCachingConcurrentFetcherWrapper(final NonBlockingConcurrentFetcherWrapper<T> fetcher) {
        this(fetcher, ExpiringCachingConcurrentFetcherWrapper.DEFAULT_MAX_CACHE_TIME);
    }

    public ExpiringCachingConcurrentFetcherWrapper(final NonBlockingConcurrentFetcherWrapper<T> fetcher,
                                                   final int maxCacheTimeMs) {
        this(fetcher, (double) maxCacheTimeMs);
    }

    public ExpiringCachingConcurrentFetcherWrapper(final NonBlockingConcurrentFetcherWrapper<T> fetcher,
                                                   final double maxCacheTimeMs) {
        super(fetcher);
        this.lastClearTime = System.currentTimeMillis();
        this.maxCacheTimeMs = maxCacheTimeMs;
    }

    @Override
    public synchronized FetcherResponse<T> fetch() throws FetcherException {

        if ((System.currentTimeMillis() - this.lastClearTime) >= this.maxCacheTimeMs) {
            this.clearFuture();
            this.lastClearTime = System.currentTimeMillis();
        }

        return super.fetch();

    }

}
