/**
 * @author cadams2
 * @since Feb 7, 2017
 */
package com.rentworthy.fetcher.concurrent;

import java.util.concurrent.Callable;

import com.rentworthy.fetcher.Fetcher;
import com.rentworthy.fetcher.exception.FetcherException;

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
        final T val = this.fetcher.fetch();
        System.out.println("callable returned");
        return val;
    }

}
