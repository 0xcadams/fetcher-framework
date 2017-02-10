package com.rentworthy.fetcher.caching.concurrent.multi.expiring;

import com.rentworthy.fetcher.caching.concurrent.NonBlockingConcurrentFetcher;
import com.rentworthy.fetcher.caching.concurrent.multi.CachingNonBlockingConcurrentFetcher;
import com.rentworthy.fetcher.exception.FetcherException;
import com.rentworthy.fetcher.response.FetcherResponse;

public class ExpiringCachingConcurrentFetcher<T> extends CachingNonBlockingConcurrentFetcher<T> {

    private static final double DEFAULT_MAX_CACHE_TIME = Double.MAX_VALUE;
    private volatile long lastClearTime;
    private final double maxCacheTimeMs;

    public ExpiringCachingConcurrentFetcher(final NonBlockingConcurrentFetcher<T> fetcher) {
        this(fetcher, ExpiringCachingConcurrentFetcher.DEFAULT_MAX_CACHE_TIME);
    }

    public ExpiringCachingConcurrentFetcher(final NonBlockingConcurrentFetcher<T> fetcher,
                                                   final int maxCacheTimeMs) {
        this(fetcher, (double) maxCacheTimeMs);
    }

    public ExpiringCachingConcurrentFetcher(final NonBlockingConcurrentFetcher<T> fetcher,
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
