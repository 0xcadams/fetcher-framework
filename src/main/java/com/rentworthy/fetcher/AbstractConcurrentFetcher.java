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
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.rentworthy.fetcher.concurrent.FetcherCallable;
import com.rentworthy.fetcher.exception.FetcherException;
import com.rentworthy.fetcher.exception.FetcherNotReadyException;

abstract class AbstractConcurrentFetcher<T> implements Fetcher<T> {

    private final static Fetcher<ExecutorService> EXECUTOR_SERVICE_FETCHER = new ExecutorServiceCachingFetcher();

    private final ReadWriteLock futureLock;

    private final Fetcher<T> fetcher;
    protected volatile Future<T> future;
    private final long maxWaitNanos;

    protected AbstractConcurrentFetcher(final Fetcher<T> fetcher, final long maxWaitNanos) {
        this.futureLock = new ReentrantReadWriteLock();
        this.fetcher = fetcher;
        this.maxWaitNanos = maxWaitNanos;
    }

    @Override
    public T fetch() throws FetcherException {

        this.futureLock.writeLock().lock();

        try {

            if (this.futureIsNull()) {
                this.future = AbstractConcurrentFetcher.EXECUTOR_SERVICE_FETCHER.fetch().submit(
                    new FetcherCallable<T>(this.fetcher));
            }

        }
        finally {
            this.futureLock.writeLock().unlock();
        }

        this.futureLock.readLock().lock();

        try {

            try {
                return this.future.get(this.maxWaitNanos, TimeUnit.NANOSECONDS);
            }
            catch (final TimeoutException e1) {
                throw new FetcherNotReadyException(e1);
            }
            catch (final ExecutionException | InterruptedException e1) {
                throw new FetcherException(e1);
            }
            catch (final NullPointerException e1) {
                throw new FetcherException(e1);
            }

        }
        finally {
            this.futureLock.readLock().unlock();
        }

    }

    private boolean futureIsNull() {

        this.futureLock.readLock().lock();

        try {
            return this.future == null;
        }
        finally {
            this.futureLock.readLock().unlock();
        }

    }

    public void clearFuture() {

        this.futureLock.writeLock().lock();

        try {
            this.future = null;
        }
        finally {
            this.futureLock.writeLock().unlock();
        }

    }

}
