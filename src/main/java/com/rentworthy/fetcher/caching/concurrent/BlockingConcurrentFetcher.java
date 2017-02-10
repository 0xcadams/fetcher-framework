/**
 * @author cadams2
 * @since Feb 9, 2017
 */
package com.rentworthy.fetcher.caching.concurrent;

import com.rentworthy.fetcher.Fetcher;

public class BlockingConcurrentFetcher<T> extends AbstractCachingConcurrentFetcher<T> {

    private final static long DEFAULT_WAIT_NANOS = Long.MAX_VALUE; // MAX_VALUE

    /**
     * @param fetcher
     */
    public BlockingConcurrentFetcher(final Fetcher<T> fetcher) {
        super(fetcher, BlockingConcurrentFetcher.DEFAULT_WAIT_NANOS);
    }

}
