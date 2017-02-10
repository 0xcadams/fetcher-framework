package com.rentworthy.fetcher;

import java.util.ArrayList;
import java.util.List;

import com.rentworthy.fetcher.caching.concurrent.NonBlockingConcurrentFetcher;
import com.rentworthy.fetcher.caching.concurrent.multi.MultiConcurrentFetcher;
import com.rentworthy.fetcher.caching.concurrent.multi.expiring.ExpiringMultiConcurrentFetcher;

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
    public static <T> Fetcher<T> getNeverExpiringMultiConcurrentFetcher(
            final Fetcher<T>... fetchers) {

        final List<NonBlockingConcurrentFetcher<T>> fetchersWrapped = new ArrayList<NonBlockingConcurrentFetcher<T>>(fetchers.length);

        for (final Fetcher<T> fetcher : fetchers) {
            fetchersWrapped.add(new NonBlockingConcurrentFetcher<T>(fetcher));
        }

        return new MultiFetcherValue<T>(new ExpiringMultiConcurrentFetcher<T>(fetchersWrapped));

    }

}
