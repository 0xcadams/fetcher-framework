/**
 * @author cadams2
 * @since Feb 7, 2017
 */
package com.rentworthy.fetcher;

import java.util.ArrayList;
import java.util.List;

import com.rentworthy.fetcher.exception.FetcherErrorCallback;
import com.rentworthy.fetcher.exception.FetcherException;
import com.rentworthy.fetcher.exception.FetcherNotReadyException;
import com.rentworthy.fetcher.response.FetcherResponse;
import com.rentworthy.fetcher.response.source.Source;
import com.rentworthy.fetcher.response.source.UnlimitedSource;

public class ConcurrentCachingFetcherWrapper<T> implements Fetcher<FetcherResponse<T>> {

    private final static FetcherErrorCallback DEFAULT_ERROR_CALLBACK = e -> {
    };
    private final static FetcherErrorCallback DEFAULT_TIMEOUT_CALLBACK = e -> {
    }; // default do nothing on timeout

    private final static double DEFAULT_MAX_TIME_MS = Double.MAX_VALUE;

    private final FetcherErrorCallback errorCallback;
    private final FetcherErrorCallback timeoutCallback;

    private final double maxTimeMs;

    private final List<ConcurrentFetcherWrapper<T>> fetchers;

    @SafeVarargs
    public ConcurrentCachingFetcherWrapper(final ConcurrentFetcherWrapper<T>... fetchers) {
        this(ConcurrentCachingFetcherWrapper.DEFAULT_ERROR_CALLBACK,
             ConcurrentCachingFetcherWrapper.DEFAULT_TIMEOUT_CALLBACK,
             ConcurrentCachingFetcherWrapper.DEFAULT_MAX_TIME_MS,
             fetchers);
    }

    @SafeVarargs
    public ConcurrentCachingFetcherWrapper(int maxTimeMs,
                                           final ConcurrentFetcherWrapper<T>... fetchers) {
        this(ConcurrentCachingFetcherWrapper.DEFAULT_ERROR_CALLBACK,
             ConcurrentCachingFetcherWrapper.DEFAULT_TIMEOUT_CALLBACK,
             maxTimeMs,
             fetchers);
    }

    @SafeVarargs
    public ConcurrentCachingFetcherWrapper(final FetcherErrorCallback errorCallback,
                                           final FetcherErrorCallback timeoutCallback,
                                           final double maxTimeMs,
                                           final ConcurrentFetcherWrapper<T>... fetchers) {

        this.fetchers = new ArrayList<>();

        for (final ConcurrentFetcherWrapper<T> fetcher : fetchers) {
            this.fetchers.add(fetcher);
        }

        this.errorCallback = errorCallback;
        this.timeoutCallback = timeoutCallback;
        this.maxTimeMs = maxTimeMs;

    }

    @Override
    public synchronized FetcherResponse<T> fetch() throws FetcherException {

        if (this.fetchers.size() == 0) {
            throw new FetcherException("Number of fetchers was zero!");
        }

        if (this.fetchers.size() == 0) {
            throw new FetcherException("Number of fetchers was zero!");
        }

        final long startTime = System.currentTimeMillis();

        while ((System.currentTimeMillis() - startTime) < maxTimeMs) {

            for (int i = 0; i < this.fetchers.size(); i++) { // loop through all
                                                             // fetchers

                final ConcurrentFetcherWrapper<T> fetcher = this.fetchers.get(i);

                try {
                    System.out.println(fetcher.fetch());
                    return this.getFetcherResponse(i + 1, fetcher.fetch());
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

        return this.getFetcherResponse(fetchers.size(), fetchers.get(fetchers.size() - 1).fetch());

    }

    private FetcherResponse<T> getFetcherResponse(final int rankFinal, final T value) {

        return new FetcherResponse<T>() {

            @Override
            public Source source() {
                return UnlimitedSource.valueOf(rankFinal);
            }

            @Override
            public T value() {
                return value;
            }

        };

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

        for (ConcurrentFetcherWrapper<T> fetcherWrapper : fetchers) {
            cleared = cleared && fetcherWrapper.clearFuture();
            ;
        }

        return cleared;

    }

}