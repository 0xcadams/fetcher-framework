package com.fetcher;

import com.fetcher.exception.FetcherException;
import com.fetcher.exception.FetcherNotReadyException;

public class CachingFetcherWrapper<T> implements Fetcher<T> {

    private final Object lock = new Object();
    private final Fetcher<T> fetcher;
    private T prevObj;
    private volatile FetcherException prevException;

    public CachingFetcherWrapper(final Fetcher<T> fetcher) {
        this.fetcher = fetcher;
        this.prevObj = null;
        this.prevException = null;
    }

    @Override
    public T fetch() throws FetcherException {

        try {

            synchronized (this.lock) {
                if ((this.prevException != null) && (!this.prevException.getCause().getClass().equals(FetcherNotReadyException.class))) {
                    throw this.prevException;
                } else if (this.prevObj != null) {
                    return this.prevObj;
                }
            }

            final T value = this.fetcher.fetch();

            if (value == null) {
                throw new FetcherException(new NullPointerException("Value from fetcher was null"));
            } else {
                synchronized (this.lock) {
                    this.prevObj = value; // store the previously retrieved value for faster retrieval
                }
                return value;
            }

        } catch (final Exception e) {

            synchronized (this.lock) {
                this.prevException = new FetcherException(e);
                throw this.prevException;
            }

        }

    }

    protected boolean clearCachedObject() {
        synchronized (this.lock) {
            this.prevObj = null;
            return (this.prevObj == null);
        }
    }

}
