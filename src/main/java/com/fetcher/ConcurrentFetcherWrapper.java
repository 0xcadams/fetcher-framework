/**
 * @author  cadams2
 * @since   Feb 8, 2017
 */
package com.fetcher;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.fetcher.concurrent.ExecutorServiceCachingFetcher;
import com.fetcher.concurrent.FetcherCallable;
import com.fetcher.exception.FetcherException;
import com.fetcher.exception.FetcherNotReadyException;

public class ConcurrentFetcherWrapper<T> implements Fetcher<T> {

    private final static Fetcher<ExecutorService> EXECUTOR_SERVICE_FETCHER = new ExecutorServiceCachingFetcher();
    private final static int DEFAULT_WAIT_NANOS = 3 * 1000 * 1000; // default to
                                                                   // 3 ms wait

    private final Fetcher<T> fetcher;
    private final int maxWaitNanos;

    public ConcurrentFetcherWrapper(final Fetcher<T> fetcher) {
        this.fetcher = fetcher;
        this.maxWaitNanos = ConcurrentFetcherWrapper.DEFAULT_WAIT_NANOS;
    }

    public ConcurrentFetcherWrapper(final Fetcher<T> fetcher, final int maxWaitNanos) {
        this.fetcher = fetcher;
        this.maxWaitNanos = maxWaitNanos;
    }

    protected volatile Future<T> future = null;

    @Override
    public synchronized T fetch() throws FetcherException {

        if (this.future == null) {
            this.future = ConcurrentFetcherWrapper.EXECUTOR_SERVICE_FETCHER.fetch().submit(new FetcherCallable<T>(this.fetcher));
        }

        try {
            return this.future.get(this.maxWaitNanos, TimeUnit.NANOSECONDS);
        } catch (InterruptedException | TimeoutException e1) {
            throw new FetcherNotReadyException(e1);
        } catch (ExecutionException e1) {
            throw new FetcherException(e1);
        } catch (final NullPointerException e1) {
            throw new FetcherException(e1);
        }

    }

    protected synchronized void clearFuture() {
        this.future = null;
    }

}
