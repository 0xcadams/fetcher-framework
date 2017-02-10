/**
 * @author cadams2
 * @since Feb 7, 2017
 */
package com.rentworthy.fetcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.rentworthy.fetcher.exception.FetcherErrorCallback;
import com.rentworthy.fetcher.exception.FetcherException;
import com.rentworthy.fetcher.exception.FetcherNotReadyException;
import com.rentworthy.fetcher.response.FetcherResponse;
import com.rentworthy.fetcher.response.FetcherResponseFactory;

class MultiConcurrentFetcher<T> implements MultiFetcher<T> {

    private final static FetcherErrorCallback DEFAULT_ERROR_CALLBACK = e -> {
    };
    private final static FetcherErrorCallback DEFAULT_TIMEOUT_CALLBACK = e -> {
    }; // default do nothing on timeout

    private final static double DEFAULT_MAX_TIME_MS = 10 * 1000;

    private final FetcherErrorCallback errorCallback;
    private final FetcherErrorCallback timeoutCallback;

    private final double maxTimeMs;

    private final List<NonBlockingConcurrentFetcher<T>> fetchers;

    @SafeVarargs
    public MultiConcurrentFetcher(final NonBlockingConcurrentFetcher<T>... fetchers) {
        this(MultiConcurrentFetcher.DEFAULT_ERROR_CALLBACK,
             MultiConcurrentFetcher.DEFAULT_TIMEOUT_CALLBACK,
             MultiConcurrentFetcher.DEFAULT_MAX_TIME_MS,
             fetchers);
    }

    @SafeVarargs
    public MultiConcurrentFetcher(final int maxTimeMs,
                                  final NonBlockingConcurrentFetcher<T>... fetchers) {
        this(MultiConcurrentFetcher.DEFAULT_ERROR_CALLBACK,
             MultiConcurrentFetcher.DEFAULT_TIMEOUT_CALLBACK,
             maxTimeMs,
             fetchers);
    }

    @SafeVarargs
    public MultiConcurrentFetcher(final FetcherErrorCallback errorCallback,
                                  final FetcherErrorCallback timeoutCallback,
                                  final double maxTimeMs,
                                  final NonBlockingConcurrentFetcher<T>... fetchers) {
        this(errorCallback, timeoutCallback, maxTimeMs, Arrays.asList(fetchers));
    }

    public MultiConcurrentFetcher(final List<NonBlockingConcurrentFetcher<T>> fetchers) {
        this(MultiConcurrentFetcher.DEFAULT_ERROR_CALLBACK,
             MultiConcurrentFetcher.DEFAULT_TIMEOUT_CALLBACK,
             MultiConcurrentFetcher.DEFAULT_MAX_TIME_MS,
             fetchers);
    }

    public MultiConcurrentFetcher(final FetcherErrorCallback errorCallback,
                                  final FetcherErrorCallback timeoutCallback,
                                  final double maxTimeMs,
                                  final List<NonBlockingConcurrentFetcher<T>> fetchers) {

        this.fetchers = new ArrayList<>();

        for (final NonBlockingConcurrentFetcher<T> fetcher : fetchers) {
            this.fetchers.add(fetcher);
        }

        this.errorCallback = errorCallback;
        this.timeoutCallback = timeoutCallback;
        this.maxTimeMs = maxTimeMs;

    }

    @Override
    public synchronized FetcherResponse<T> fetch() throws FetcherException {

        if (this.fetchers.size() <= 0) {
            throw new FetcherException("Number of fetchers was zero!");
        }

        final long startTime = System.currentTimeMillis();

        while ((System.currentTimeMillis() - startTime) < this.maxTimeMs) {

            for (int i = 0; i < this.fetchers.size(); i++) { // loop through all
                                                             // fetchers

                final Fetcher<T> fetcher = this.fetchers.get(i);

                try {
                    return FetcherResponseFactory.getFetcherResponse(i + 1, fetcher.fetch());
                }
                catch (final FetcherException e) {
                    if (!e.getCause().getClass().equals(FetcherNotReadyException.class)) {
                        this.errorCallback.onError(e);
                    }
                    else {
                        this.timeoutCallback.onError(e);
                    }
                }

            }

        }

        return FetcherResponseFactory.getFetcherResponse(this.fetchers.size(),
            this.fetchers.get(this.fetchers.size() - 1).fetch());

    }

    // public boolean clearCachedObject() {
    //
    // boolean cleared = true;
    //
    // for (ConcurrentFetcherWrapper<T> fetcherWrapper : fetchers) {
    // cleared = cleared && fetcherWrapper.clearCachedObject();
    // }
    //
    // return cleared;
    //
    // }

    public boolean clearFuture() {

        boolean cleared = true;

        for (final AbstractCachingConcurrentFetcher<T> fetcherWrapper : this.fetchers) {
            cleared = cleared && fetcherWrapper.clearFuture();
            ;
        }

        return cleared;

    }

}