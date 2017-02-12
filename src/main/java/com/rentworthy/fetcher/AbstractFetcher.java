package com.rentworthy.fetcher;

import com.rentworthy.fetcher.exception.FetcherException;

public abstract class AbstractFetcher<T> implements Fetcher<T> {

    private final Fetcher<T> fetcher;

    public AbstractFetcher(final Fetcher<T> fetcher) {
        this.fetcher = fetcher;
    }

    @Override
    public T fetch() throws FetcherException {
        return this.fetcher.fetch();
    }

}
