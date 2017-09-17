/**
 * @author cadams2
 * @since Feb 8, 2017
 */
package com.lieuu.fetcher;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.lieuu.fetcher.concurrent.FetcherCallable;
import com.lieuu.fetcher.exception.FetcherException;
import com.lieuu.fetcher.exception.FetcherNotReadyException;

abstract class AbstractConcurrentFetcher<T> implements Fetcher<T> {

    private final ReadWriteLock futureLock;

    private final Fetcher<T> fetcher;
    protected volatile Future<T> future;
    private final long maxWaitNanos;
    private final Fetcher<ExecutorService> executorServiceFetcher;

    protected AbstractConcurrentFetcher(final Fetcher<T> fetcher,
                                        final Fetcher<ExecutorService> executorServiceFetcher,
                                        final long maxWaitNanos) {
        this.futureLock = new ReentrantReadWriteLock();
        this.executorServiceFetcher = executorServiceFetcher;
        this.fetcher = fetcher;
        this.maxWaitNanos = maxWaitNanos;
    }

    @Override
    public T fetch() throws FetcherException {

        this.futureLock.writeLock().lock();

        try {

            if (this.futureIsNull()) {
                this.future = this.executorServiceFetcher.fetch().submit(
                    new FetcherCallable<>(this.fetcher));
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
            catch (final ExecutionException | InterruptedException | NullPointerException e1) {
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
