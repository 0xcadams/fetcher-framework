package com.rentworthy.fetcher.caching.concurrent.multi.expiring;

import java.util.Arrays;
import java.util.List;

import com.rentworthy.fetcher.caching.concurrent.NonBlockingConcurrentFetcher;
import com.rentworthy.fetcher.caching.concurrent.multi.MultiConcurrentFetcher;
import com.rentworthy.fetcher.exception.FetcherException;
import com.rentworthy.fetcher.response.FetcherResponse;

public class ExpiringMultiConcurrentFetcher<T> extends MultiConcurrentFetcher<T> {

    private static final double DEFAULT_MAX_CACHE_TIME = Double.MAX_VALUE;
    private volatile long lastClearTime;
    private final double maxCacheTimeMs;

    @SafeVarargs
    public ExpiringMultiConcurrentFetcher(final NonBlockingConcurrentFetcher<T>... fetchers) {
        this(ExpiringMultiConcurrentFetcher.DEFAULT_MAX_CACHE_TIME, fetchers);
    }

    @SafeVarargs
    public ExpiringMultiConcurrentFetcher(final int maxCacheTimeMs,
                                          final NonBlockingConcurrentFetcher<T>... fetchers) {
        this((double) maxCacheTimeMs, fetchers);
    }

    @SafeVarargs
    public ExpiringMultiConcurrentFetcher(final double maxCacheTimeMs,
                                          final NonBlockingConcurrentFetcher<T>... fetchers) {
        this(maxCacheTimeMs, Arrays.asList(fetchers));
    }

    public ExpiringMultiConcurrentFetcher(final List<NonBlockingConcurrentFetcher<T>> fetchers) {
        this(DEFAULT_MAX_CACHE_TIME, fetchers);
    }

    public ExpiringMultiConcurrentFetcher(final double maxCacheTimeMs,
                                          final List<NonBlockingConcurrentFetcher<T>> fetchers) {
        super(fetchers);
        this.maxCacheTimeMs = maxCacheTimeMs;
        this.lastClearTime = System.currentTimeMillis();
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
