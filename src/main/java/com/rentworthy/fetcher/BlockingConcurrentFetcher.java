/**
 * @author cadams2
 * @since Feb 9, 2017
 */
package com.rentworthy.fetcher;

import java.util.concurrent.ExecutorService;

final class BlockingConcurrentFetcher<T> extends AbstractConcurrentFetcher<T> {

    private final static long DEFAULT_WAIT_NANOS = Long.MAX_VALUE; // MAX_VALUE

    /**
     * @param fetcher
     */
    public BlockingConcurrentFetcher(final Fetcher<T> fetcher, final Fetcher<ExecutorService> executorServiceFetcher) {
        super(fetcher, executorServiceFetcher, BlockingConcurrentFetcher.DEFAULT_WAIT_NANOS);
    }

}
