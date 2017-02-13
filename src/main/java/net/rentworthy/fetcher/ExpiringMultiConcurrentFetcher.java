package net.rentworthy.fetcher;

import java.util.Arrays;
import java.util.List;

import net.rentworthy.fetcher.exception.FetcherException;
import net.rentworthy.fetcher.response.FetcherResponse;

class ExpiringMultiConcurrentFetcher<T> extends BlockingMultiConcurrentFetcher<T> {

    private volatile long lastClearTime;
    private final double maxCacheTimeMs;

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
