package com.lieuu.fetcher;

import java.lang.ref.SoftReference;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.lieuu.fetcher.exception.FetcherException;
import com.lieuu.fetcher.exception.FetcherNotReadyException;

class CachingFetcher<T> implements Fetcher<T> {

    private final ReadWriteLock objLock;
    private final ReadWriteLock exceptionLock;

    private final Fetcher<T> fetcher;
    private SoftReference<T> prevObj;
    private SoftReference<FetcherException> prevException;

    public CachingFetcher(final Fetcher<T> fetcher) {
        this.objLock = new ReentrantReadWriteLock();
        this.exceptionLock = new ReentrantReadWriteLock();
        this.fetcher = fetcher;
        this.prevObj = new SoftReference<>(null);
        this.prevException = new SoftReference<>(null);
    }

    @Override
    public T fetch() throws FetcherException {

        try {

            this.exceptionLock.readLock().lock();

            try {
                final FetcherException exceptionCurrent = this.prevException.get();

                if ((exceptionCurrent != null)
                    && !(exceptionCurrent.getCause() instanceof FetcherNotReadyException)) {
                    throw exceptionCurrent;
                }
            }
            finally {
                this.exceptionLock.readLock().unlock();
            }

            this.objLock.readLock().lock();

            try {
                final T objectCurrent = this.prevObj.get();

                if (objectCurrent != null) {
                    return objectCurrent;
                }
            }
            finally {
                this.objLock.readLock().unlock();
            }

            final T value = this.fetcher.fetch();

            if (value == null) {
                throw new FetcherException(new NullPointerException("Value from fetcher was null"));
            }
            else {
                this.setPrevObj(value); // store the previously retrieved value
                                        // for faster retrieval
                return value;
            }

        }
        catch (final Exception e) {

            this.setPrevException(e);

            this.exceptionLock.readLock().lock();

            try {
                throw this.prevException.get();
            }
            finally {
                this.exceptionLock.readLock().unlock();
            }

        }

    }

    private void setPrevException(final Throwable e) {

        this.exceptionLock.writeLock().lock();

        try {
            this.prevException = new SoftReference<>(new FetcherException(e));
        }
        finally {
            this.exceptionLock.writeLock().unlock();
        }

    }

    private void setPrevObj(final T obj) {

        this.objLock.writeLock().lock();

        try {
            this.prevObj = new SoftReference<>(obj);
        }
        finally {
            this.objLock.writeLock().unlock();
        }

    }

    protected void clearCachedObject() {
        this.setPrevObj(null);
    }

    protected void clearCachedException() {
        this.setPrevException(null);
    }

}
