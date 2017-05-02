package com.lieuu.fetcher;

import com.lieuu.fetcher.exception.FetcherException;

class ExpiringCachingFetcher<T> extends CachingFetcher<T> {

    private final Object lock = new Object();

    private volatile long lastClearTime;
    private final double maxCacheTime;

    public ExpiringCachingFetcher(final Fetcher<T> fetcher, final int maxCacheTime) {
        this(fetcher, (double) maxCacheTime);
    }

    public ExpiringCachingFetcher(final Fetcher<T> fetcher, final double maxCacheTime) {
        super(fetcher);
        this.lastClearTime = System.currentTimeMillis();
        this.maxCacheTime = maxCacheTime;
    }

    @Override
    public T fetch() throws FetcherException {

        synchronized (this.lock) {

            if ((System.currentTimeMillis() - this.lastClearTime) >= this.maxCacheTime) {
                this.clearCachedObject();
                this.lastClearTime = System.currentTimeMillis();
            }

            return super.fetch();

        }

    }

}
