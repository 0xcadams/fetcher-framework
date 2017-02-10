/**
 * @author cadams2
 * @since Feb 9, 2017
 */
package com.rentworthy.fetcher.caching.concurrent;

import com.rentworthy.fetcher.Fetcher;

public class BlockingConcurrentFetcherWrapper<T> extends AbstractCachingConcurrentFetcherWrapper<T> {

    private final static long DEFAULT_WAIT_NANOS = Long.MAX_VALUE; // MAX_VALUE

    /**
     * @param fetcher
     */
    public BlockingConcurrentFetcherWrapper(final Fetcher<T> fetcher) {
        super(fetcher, BlockingConcurrentFetcherWrapper.DEFAULT_WAIT_NANOS);
    }

}
