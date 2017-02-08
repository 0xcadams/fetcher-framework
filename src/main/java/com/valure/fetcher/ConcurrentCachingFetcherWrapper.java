/**
 * @author cadams2
 * @since Feb 7, 2017
 */
package com.valure.fetcher;

public class ConcurrentCachingFetcherWrapper<T> extends CachingFetcherWrapper<T> {

    protected final ConcurrentFetcherWrapper<T> concurrentFetcherWrapper;

    /**
     * @param fetcher
     * @param maxWaitNanos
     */
    public ConcurrentCachingFetcherWrapper(final ConcurrentFetcherWrapper<T> fetcher) {
        super(fetcher);
        this.concurrentFetcherWrapper = fetcher;
    }

}
