/**
 * @author cadams2
 * @since Feb 9, 2017
 */
package com.lieuu.fetcher;

import java.util.concurrent.ExecutorService;

final class NonBlockingConcurrentFetcher<T> extends AbstractConcurrentFetcher<T> {

    private final static long DEFAULT_WAIT_NANOS = 1 * 1000 * 1000; // 1ms

    /**
     * @param fetcher
     * @param executorServiceFetcher 
     */
    public NonBlockingConcurrentFetcher(final Fetcher<T> fetcher, Fetcher<ExecutorService> executorServiceFetcher) {
        super(fetcher, executorServiceFetcher, NonBlockingConcurrentFetcher.DEFAULT_WAIT_NANOS);
    }

}
