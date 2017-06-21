/**
 * @author cadams2
 * @since Feb 7, 2017
 */
package com.lieuu.fetcher;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.lieuu.fetcher.exception.FetcherErrorCallback;
import com.lieuu.fetcher.exception.FetcherException;
import com.lieuu.fetcher.exception.FetcherNotReadyException;
import com.lieuu.fetcher.exception.FetcherTimeoutCallback;
import com.lieuu.fetcher.response.FetcherResponse;

class BlockingMultiConcurrentFetcher<T> implements MultiFetcher<T> {

    private final static FetcherErrorCallback DEFAULT_ERROR_CALLBACK = e -> {
    };
    private final static FetcherTimeoutCallback DEFAULT_TIMEOUT_CALLBACK = e -> {
    }; // default do nothing on timeout

    private final static double DEFAULT_MAX_TIME_MS = 10 * 1000;

    private final FetcherErrorCallback errorCallback;
    private final FetcherTimeoutCallback timeoutCallback;

    private final double maxTimeMs;

    private final List<NonBlockingConcurrentFetcher<T>> fetchers;

    @SafeVarargs
    public BlockingMultiConcurrentFetcher(final NonBlockingConcurrentFetcher<T>... fetchers) {
        this(BlockingMultiConcurrentFetcher.DEFAULT_ERROR_CALLBACK,
             BlockingMultiConcurrentFetcher.DEFAULT_TIMEOUT_CALLBACK,
             BlockingMultiConcurrentFetcher.DEFAULT_MAX_TIME_MS, fetchers);
    }

    @SafeVarargs
    public BlockingMultiConcurrentFetcher(final int maxTimeMs,
                                          final NonBlockingConcurrentFetcher<T>... fetchers) {
        this(BlockingMultiConcurrentFetcher.DEFAULT_ERROR_CALLBACK,
             BlockingMultiConcurrentFetcher.DEFAULT_TIMEOUT_CALLBACK, maxTimeMs, fetchers);
    }

    @SafeVarargs
    public BlockingMultiConcurrentFetcher(final FetcherErrorCallback errorCallback,
                                          final FetcherTimeoutCallback timeoutCallback,
                                          final double maxTimeMs,
                                          final NonBlockingConcurrentFetcher<T>... fetchers) {
        this(errorCallback, timeoutCallback, maxTimeMs, Arrays.asList(fetchers));
    }

    public BlockingMultiConcurrentFetcher(final List<NonBlockingConcurrentFetcher<T>> fetchers) {
        this(BlockingMultiConcurrentFetcher.DEFAULT_ERROR_CALLBACK,
             BlockingMultiConcurrentFetcher.DEFAULT_TIMEOUT_CALLBACK,
             BlockingMultiConcurrentFetcher.DEFAULT_MAX_TIME_MS, fetchers);
    }

    public BlockingMultiConcurrentFetcher(final FetcherErrorCallback errorCallback,
                                          final FetcherTimeoutCallback timeoutCallback,
                                          final double maxTimeMs,
                                          final List<NonBlockingConcurrentFetcher<T>> fetchers) {

        this.fetchers = Collections.unmodifiableList(fetchers);

        this.errorCallback = errorCallback;
        this.timeoutCallback = timeoutCallback;
        this.maxTimeMs = maxTimeMs;

    }

    @Override
    public FetcherResponse<T> fetch() throws FetcherException {

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
                    if (e.getCause() instanceof FetcherNotReadyException) {
                        this.timeoutCallback.onTimeout((FetcherNotReadyException) e.getCause());
                    }
                    else {
                        this.errorCallback.onError(e);
                    }
                }

            }

        }

        return FetcherResponseFactory.getFetcherResponse(this.fetchers.size(),
            this.fetchers.get(this.fetchers.size() - 1).fetch());

    }

    public void clearFuture() {

        for (final AbstractConcurrentFetcher<T> fetcherWrapper : this.fetchers) {
            fetcherWrapper.clearFuture();
        }

    }

}