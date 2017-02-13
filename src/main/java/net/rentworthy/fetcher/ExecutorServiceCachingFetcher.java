package net.rentworthy.fetcher;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ExecutorServiceCachingFetcher extends CachingFetcher<ExecutorService> {

    public ExecutorServiceCachingFetcher() {
        super(() -> Executors.newCachedThreadPool());
    }

}
