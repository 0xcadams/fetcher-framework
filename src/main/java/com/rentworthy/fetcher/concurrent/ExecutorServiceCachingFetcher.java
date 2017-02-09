package com.rentworthy.fetcher.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.rentworthy.fetcher.caching.CachingFetcherWrapper;

public class ExecutorServiceCachingFetcher extends CachingFetcherWrapper<ExecutorService> {

    public ExecutorServiceCachingFetcher() {
        super(() -> Executors.newCachedThreadPool());
    }

}
