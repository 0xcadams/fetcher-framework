package com.rentworthy.fetcher.caching.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.rentworthy.fetcher.Fetcher;
import com.rentworthy.fetcher.FetcherFactory;
import com.rentworthy.fetcher.exception.FetcherException;

public class TestExecutorServiceCachingFetcher implements Fetcher<ExecutorService> {

    private static final Fetcher<ExecutorService> FETCHER = FetcherFactory.getCachingFetcher(
        () -> Executors.newCachedThreadPool());

    @Override
    public ExecutorService fetch() throws FetcherException {
        return FETCHER.fetch();
    }

}
