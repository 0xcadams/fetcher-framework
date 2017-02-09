package com.rentworthy.fetcher;

import com.rentworthy.fetcher.exception.FetcherException;

public interface Fetcher<T> {

    /**
     * Fetches an object of generic type T and throws FetcherException if it
     * encounters an error.
     *
     * @return t of type T
     * @throws FetcherException
     */
    public T fetch() throws FetcherException;

}
