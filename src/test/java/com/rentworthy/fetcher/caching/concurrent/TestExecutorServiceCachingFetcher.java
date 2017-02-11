package com.rentworthy.fetcher.caching.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.rentworthy.fetcher.Fetcher;
import com.rentworthy.fetcher.Fetchers;
import com.rentworthy.fetcher.exception.FetcherException;

public class TestExecutorServiceCachingFetcher implements Fetcher<ExecutorService> {

    private static final Fetcher<ExecutorService> FETCHER = Fetchers.getCachingFetcher(
        () -> Executors.newCachedThreadPool());

    @Override
    public ExecutorService fetch() throws FetcherException {
        return TestExecutorServiceCachingFetcher.FETCHER.fetch();
    }

}
