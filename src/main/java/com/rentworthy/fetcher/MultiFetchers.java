package com.rentworthy.fetcher;

import java.util.ArrayList;
import java.util.List;

import com.rentworthy.fetcher.exception.FetcherErrorCallback;

public final class MultiFetchers {

    @SafeVarargs
    public final static <T> MultiFetcher<T> getMultiConcurrentFetcher(
            final Fetcher<T>... fetchers) {

        final List<NonBlockingConcurrentFetcher<T>> fetchersWrapped = new ArrayList<NonBlockingConcurrentFetcher<T>>(fetchers.length);

        for (final Fetcher<T> fetcher : fetchers) {
            fetchersWrapped.add(new NonBlockingConcurrentFetcher<T>(fetcher));
        }

        return new MultiConcurrentFetcher<T>(fetchersWrapped);

    }

    @SafeVarargs
    public final static <T> MultiFetcher<T> getExpiringMultiConcurrentFetcher(final long maxTimeMs,
            final Fetcher<T>... fetchers) {

        final List<NonBlockingConcurrentFetcher<T>> fetchersWrapped = new ArrayList<NonBlockingConcurrentFetcher<T>>(fetchers.length);

        for (final Fetcher<T> fetcher : fetchers) {
            fetchersWrapped.add(new NonBlockingConcurrentFetcher<T>(fetcher));
        }

        return new ExpiringMultiConcurrentFetcher<T>(maxTimeMs, fetchersWrapped);

    }

    @SafeVarargs
    public final static <T> MultiFetcher<T> getWaterfallFetcher(final Fetcher<T>... fetchers) {

        final List<CachingFetcher<T>> fetchersWrapped = new ArrayList<CachingFetcher<T>>(fetchers.length);

        for (final Fetcher<T> fetcher : fetchers) {
            fetchersWrapped.add(new CachingFetcher<T>(fetcher));
        }

        return new WaterfallCachingFetcher<T>(fetchersWrapped);

    }

    @SafeVarargs
    public final static <T> MultiFetcher<T> getWaterfallFetcher(final FetcherErrorCallback callback,
            final Fetcher<T>... fetchers) {

        final List<CachingFetcher<T>> fetchersWrapped = new ArrayList<CachingFetcher<T>>(fetchers.length);

        for (final Fetcher<T> fetcher : fetchers) {
            fetchersWrapped.add(new CachingFetcher<T>(fetcher));
        }

        return new WaterfallCachingFetcher<T>(callback, fetchersWrapped);

    }

}
