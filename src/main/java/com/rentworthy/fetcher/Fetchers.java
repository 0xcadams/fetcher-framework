package com.rentworthy.fetcher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import com.rentworthy.fetcher.exception.FetcherErrorCallback;

public final class Fetchers {

    private final static Fetcher<ExecutorService> EXECUTOR_SERVICE_FETCHER = new ExecutorServiceCachingFetcher();

    @SafeVarargs
    public final static <T> Fetcher<T> getMultiConcurrentFetcher(final Fetcher<T>... fetchers) {

        final List<NonBlockingConcurrentFetcher<T>> fetchersWrapped = new ArrayList<NonBlockingConcurrentFetcher<T>>(fetchers.length);

        for (final Fetcher<T> fetcher : fetchers) {
            fetchersWrapped.add(
                new NonBlockingConcurrentFetcher<T>(fetcher, getExecutorServiceFetcher()));
        }

        return new MultiFetcherValue<T>(new MultiConcurrentFetcher<T>(fetchersWrapped));

    }

    @SafeVarargs
    public final static <T> Fetcher<T> getExpiringMultiConcurrentFetcher(
            Fetcher<ExecutorService> executorServiceFetcher, final long maxTimeMs,
            final Fetcher<T>... fetchers) {

        final List<NonBlockingConcurrentFetcher<T>> fetchersWrapped = new ArrayList<NonBlockingConcurrentFetcher<T>>(fetchers.length);

        for (final Fetcher<T> fetcher : fetchers) {
            fetchersWrapped.add(
                new NonBlockingConcurrentFetcher<T>(fetcher, executorServiceFetcher));
        }

        return new MultiFetcherValue<T>(new ExpiringMultiConcurrentFetcher<T>(maxTimeMs,
                                                                              fetchersWrapped));

    }

    @SafeVarargs
    public final static <T> Fetcher<T> getExpiringMultiConcurrentFetcher(final long maxTimeMs,
            final Fetcher<T>... fetchers) {
        return getExpiringMultiConcurrentFetcher(getExecutorServiceFetcher(), maxTimeMs, fetchers);
    }

    @SafeVarargs
    public final static <T> Fetcher<T> getWaterfallFetcher(final Fetcher<T>... fetchers) {

        final List<CachingFetcher<T>> fetchersWrapped = new ArrayList<CachingFetcher<T>>(fetchers.length);

        for (final Fetcher<T> fetcher : fetchers) {
            fetchersWrapped.add(new CachingFetcher<T>(fetcher));
        }

        return new MultiFetcherValue<T>(new WaterfallCachingFetcher<T>(fetchersWrapped));

    }

    @SafeVarargs
    public final static <T> Fetcher<T> getWaterfallFetcher(final FetcherErrorCallback callback,
            final Fetcher<T>... fetchers) {

        final List<CachingFetcher<T>> fetchersWrapped = new ArrayList<CachingFetcher<T>>(fetchers.length);

        for (final Fetcher<T> fetcher : fetchers) {
            fetchersWrapped.add(new CachingFetcher<T>(fetcher));
        }

        return new MultiFetcherValue<T>(new WaterfallCachingFetcher<T>(callback, fetchersWrapped));

    }

    public final static <T> Fetcher<T> getBlockingConcurrentFetcher(
            Fetcher<ExecutorService> executorServiceFetcher, final Fetcher<T> fetcher) {
        return new BlockingConcurrentFetcher<T>(fetcher, executorServiceFetcher);
    }

    public final static <T> Fetcher<T> getNonBlockingConcurrentFetcher(
            Fetcher<ExecutorService> executorServiceFetcher, final Fetcher<T> fetcher) {
        return new NonBlockingConcurrentFetcher<T>(fetcher, executorServiceFetcher);
    }

    public final static <T> Fetcher<T> getBlockingConcurrentFetcher(final Fetcher<T> fetcher) {
        return getBlockingConcurrentFetcher(getExecutorServiceFetcher(), fetcher);
    }

    public final static <T> Fetcher<T> getNonBlockingConcurrentFetcher(final Fetcher<T> fetcher) {
        return getNonBlockingConcurrentFetcher(getExecutorServiceFetcher(), fetcher);
    }

    public final static <T> Fetcher<T> getCachingFetcher(final Fetcher<T> fetcher) {
        return new CachingFetcher<T>(fetcher);
    }

    public final static <T> Fetcher<T> getExpiringCachingFetcher(final Fetcher<T> fetcher,
            final int maxCacheTime) {
        return new ExpiringCachingFetcher<T>(fetcher, maxCacheTime);
    }

    public final static Fetcher<ExecutorService> getExecutorServiceFetcher() {
        return EXECUTOR_SERVICE_FETCHER;
    }

}
