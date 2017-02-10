package com.rentworthy.fetcher;

import java.util.ArrayList;
import java.util.List;

import com.rentworthy.fetcher.exception.FetcherErrorCallback;

public class FetcherFactory {

    @SafeVarargs
    public static <T> Fetcher<T> getMultiConcurrentFetcher(final Fetcher<T>... fetchers) {

        final List<NonBlockingConcurrentFetcher<T>> fetchersWrapped = new ArrayList<NonBlockingConcurrentFetcher<T>>(fetchers.length);

        for (final Fetcher<T> fetcher : fetchers) {
            fetchersWrapped.add(new NonBlockingConcurrentFetcher<T>(fetcher));
        }

        return new MultiFetcherValue<T>(new MultiConcurrentFetcher<T>(fetchersWrapped));

    }

    @SafeVarargs
    public static <T> Fetcher<T> getExpiringMultiConcurrentFetcher(final long maxTimeMs,
            final Fetcher<T>... fetchers) {

        final List<NonBlockingConcurrentFetcher<T>> fetchersWrapped = new ArrayList<NonBlockingConcurrentFetcher<T>>(fetchers.length);

        for (final Fetcher<T> fetcher : fetchers) {
            fetchersWrapped.add(new NonBlockingConcurrentFetcher<T>(fetcher));
        }

        return new MultiFetcherValue<T>(new ExpiringMultiConcurrentFetcher<T>(maxTimeMs,
                                                                              fetchersWrapped));

    }

    @SafeVarargs
    public static <T> Fetcher<T> getWaterfallFetcher(final Fetcher<T>... fetchers) {

        final List<CachingFetcher<T>> fetchersWrapped = new ArrayList<CachingFetcher<T>>(fetchers.length);

        for (final Fetcher<T> fetcher : fetchers) {
            fetchersWrapped.add(new CachingFetcher<T>(fetcher));
        }

        return new MultiFetcherValue<T>(new WaterfallCachingFetcher<T>(fetchersWrapped));

    }

    @SafeVarargs
    public static <T> Fetcher<T> getWaterfallFetcher(final FetcherErrorCallback callback,
            final Fetcher<T>... fetchers) {

        final List<CachingFetcher<T>> fetchersWrapped = new ArrayList<CachingFetcher<T>>(fetchers.length);

        for (final Fetcher<T> fetcher : fetchers) {
            fetchersWrapped.add(new CachingFetcher<T>(fetcher));
        }

        return new MultiFetcherValue<T>(new WaterfallCachingFetcher<T>(callback, fetchersWrapped));

    }

    public static <T> Fetcher<T> getBlockingConcurrentFetcher(final Fetcher<T> fetcher) {
        return new BlockingConcurrentFetcher<T>(fetcher);
    }

    public static <T> Fetcher<T> getNonBlockingConcurrentFetcher(final Fetcher<T> fetcher) {
        return new NonBlockingConcurrentFetcher<T>(fetcher);
    }

    public static <T> Fetcher<T> getCachingFetcher(final Fetcher<T> fetcher) {
        return new CachingFetcher<T>(fetcher);
    }

    public static <T> Fetcher<T> getExpiringCachingFetcher(final Fetcher<T> fetcher,
            final int maxCacheTime) {
        return new ExpiringCachingFetcher<T>(fetcher, maxCacheTime);
    }

}
