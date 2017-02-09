/**
 * @author  cadams2
 * @since   Feb 9, 2017
 */
package com.rentworthy.fetcher.caching.concurrent;

import com.rentworthy.fetcher.Fetcher;

public class NonBlockingConcurrentFetcherWrapper<T> extends AbstractCachingConcurrentFetcherWrapper<T> {
    
    private final static long DEFAULT_WAIT_NANOS = 1 * 1000 * 1000; // 1ms

    /**
     * @param fetcher
     */
    public NonBlockingConcurrentFetcherWrapper(Fetcher<T> fetcher) {
        super(fetcher, DEFAULT_WAIT_NANOS);
    }

}
