/**
 * @author cadams2
 * @since Feb 8, 2017
 */
package com.rentworthy.fetcher;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.rentworthy.fetcher.concurrent.FetcherCallable;
import com.rentworthy.fetcher.exception.FetcherException;
import com.rentworthy.fetcher.exception.FetcherNotReadyException;

abstract class AbstractConcurrentFetcher<T> extends CachingFetcher<T> {

    private final static Fetcher<ExecutorService> EXECUTOR_SERVICE_FETCHER = new ExecutorServiceCachingFetcher();

    protected final volatile Future<T> future;
    private final long maxWaitNanos;

    protected AbstractConcurrentFetcher(final Fetcher<T> fetcher, final long maxWaitNanos) {

        super(new Fetcher<T>() {

            @Override
            public synchronized T fetch() throws FetcherException {

                if (future == null) {
                    future = AbstractConcurrentFetcher.EXECUTOR_SERVICE_FETCHER.fetch().submit(new FetcherCallable<T>(this.fetcher));
                }

                try {
                    return future.get(this.maxWaitNanos, TimeUnit.NANOSECONDS);
                } catch (InterruptedException | TimeoutException e1) {
                    throw new FetcherNotReadyException(e1);
                } catch (final ExecutionException e1) {
                    throw new FetcherException(e1);
                } catch (final NullPointerException e1) {
                    throw new FetcherException(e1);
                }

            }
        });

        this.maxWaitNanos = maxWaitNanos;
        
    }


    public synchronized boolean clearFuture() {
        this.future = null;
        return this.future == null;
    }

}
