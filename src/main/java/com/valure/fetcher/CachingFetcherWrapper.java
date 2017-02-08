package com.valure.fetcher;

import com.valure.fetcher.exception.FetcherException;

public class CachingFetcherWrapper<T> implements Fetcher<T> {

    private final Fetcher<T> fetcher;
    private volatile T prevObj;
    private volatile FetcherException prevException;

    public CachingFetcherWrapper(final Fetcher<T> fetcher) {
        this.fetcher = fetcher;
        this.prevObj = null;
        this.prevException = null;
    }

    @Override
    public synchronized T fetch() throws FetcherException {

        try {

            if (this.prevException != null) {
                throw this.prevException;
            }
            else if (this.prevObj != null) {
                return this.prevObj;
            }

            final T value = this.fetcher.fetch();

            if (value == null) {
                throw new FetcherException(new NullPointerException("Value from fetcher was null"));
            }
            else {
                this.prevObj = value; // store the previously retrieved value
                                      // for faster retrieval
                return value;
            }

        }
        catch (final Exception e) {
            this.prevException = new FetcherException(e);
            throw this.prevException;
        }

    }

    protected boolean clearPreviousException() {
        this.prevException = null;
        return (this.prevException == null);
    }

    protected boolean clearCachedObject() {
        this.prevObj = null;
        // System.out.println("clearing cache");
        return (this.prevObj == null);
    }

}
