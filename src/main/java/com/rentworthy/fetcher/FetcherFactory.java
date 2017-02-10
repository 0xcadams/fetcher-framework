package com.rentworthy.fetcher;

import java.util.ArrayList;
import java.util.List;

import com.rentworthy.fetcher.caching.concurrent.NonBlockingConcurrentFetcher;
import com.rentworthy.fetcher.caching.concurrent.multi.CachingNonBlockingConcurrentFetcher;

public class FetcherFactory {

    @SafeVarargs
    public static <T> Fetcher<T> getCachingNonBlockingConcurrentFetcher(
            final Fetcher<T>... fetchers) {

        final List<NonBlockingConcurrentFetcher<T>> fetchersWrapped = new ArrayList<NonBlockingConcurrentFetcher<T>>(fetchers.length);

        for (final Fetcher<T> fetcher : fetchers) {
            fetchersWrapped.add(new NonBlockingConcurrentFetcher<T>(fetcher));
        }

        return new MultiFetcherValue<T>(new CachingNonBlockingConcurrentFetcher<T>(fetchersWrapped));

    }

}
