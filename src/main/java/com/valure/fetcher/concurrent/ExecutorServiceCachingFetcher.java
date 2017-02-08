package com.valure.fetcher.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.valure.fetcher.CachingFetcherWrapper;

public class ExecutorServiceCachingFetcher extends CachingFetcherWrapper<ExecutorService> {

    public ExecutorServiceCachingFetcher() {
        super(() -> Executors.newCachedThreadPool());
    }

}
