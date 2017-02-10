package com.rentworthy.fetcher;

import com.rentworthy.fetcher.caching.concurrent.NonBlockingConcurrentFetcherWrapper;
import com.rentworthy.fetcher.caching.concurrent.multi.CachingNonBlockingConcurrentFetcherWrapper;

public class FetcherBuilder<T> implements Builder<T> {

    public static long DEFAULT_NON_EXPIRING = -1;

    private boolean concurrent = true;
    private boolean source = false;
    private long expiring = FetcherBuilder.DEFAULT_NON_EXPIRING;

    public FetcherBuilder() {

    }

    public FetcherBuilder<T> nonConcurrent() {
        this.concurrent = false;
        return this;
    }

    public FetcherBuilder<T> source() {
        this.source = true;
        return this;
    }

    public FetcherBuilder<T> expiring(final long expiring) {
        this.expiring = expiring;
        return this;
    }

    @SafeVarargs
    @Override
    public final Fetcher<T> build(final Fetcher<T>... fetcher) {

        if (this.concurrent && !this.source && (this.expiring == -1) && (fetcher.length == 1)) {
            return new MultiFetcherValueWrapper<T>(new CachingNonBlockingConcurrentFetcherWrapper<T>(new NonBlockingConcurrentFetcherWrapper<T>(fetcher[0])));
        }

        return null;

    }

}
