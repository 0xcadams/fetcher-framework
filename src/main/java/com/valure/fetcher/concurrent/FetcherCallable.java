/**
 * @author cadams2
 * @since Feb 7, 2017
 */
package com.valure.fetcher.concurrent;

import java.util.concurrent.Callable;

import com.valure.fetcher.Fetcher;
import com.valure.fetcher.exception.FetcherException;

public class FetcherCallable<T> implements Callable<T> {

    private final Fetcher<T> fetcher;

    /**
     * @param fetcher
     */
    public FetcherCallable(final Fetcher<T> fetcher) {
        this.fetcher = fetcher;
    }

    /*
     * (non-Javadoc)
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public T call() throws FetcherException {
        return this.fetcher.fetch();
    }

}
