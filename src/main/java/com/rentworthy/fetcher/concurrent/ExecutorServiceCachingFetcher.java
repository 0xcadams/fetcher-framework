package com.rentworthy.fetcher.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.rentworthy.fetcher.caching.CachingFetcher;

public class ExecutorServiceCachingFetcher extends CachingFetcher<ExecutorService> {

    public ExecutorServiceCachingFetcher() {
        super(() -> Executors.newCachedThreadPool());
    }

}
