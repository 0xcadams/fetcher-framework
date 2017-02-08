/**
 * @author cadams2
 * @since Feb 7, 2017
 */
package com.valure.fetcher;

import java.util.concurrent.atomic.AtomicInteger;

import com.valure.fetcher.exception.FetcherErrorCallback;
import com.valure.fetcher.exception.FetcherException;
import com.valure.fetcher.exception.FetcherNotReadyException;
import com.valure.fetcher.response.DualFetcherResponse;
import com.valure.fetcher.response.DualSource;

public class DualConcurrentCachingFetcherWrapper<T> implements Fetcher<DualFetcherResponse<T>> {

    private final static FetcherErrorCallback DEFAULT_ERROR_CALLBACK = e -> e.printStackTrace();
    private final static FetcherErrorCallback DEFAULT_TIMEOUT_CALLBACK = e -> {
    }; // default do nothing on timeout

    private final static int DEFAULT_MAX_ATTEMPTS = Integer.MAX_VALUE;

    private final AtomicInteger countAttempts = new AtomicInteger(0);

    private final FetcherErrorCallback errorCallback;
    private final FetcherErrorCallback timeoutCallback;

    private final ConcurrentCachingFetcherWrapper<T> fetcherSecondary;
    private final ConcurrentCachingFetcherWrapper<T> fetcherPrimary;

    private final int maxAttempts;

    /**
     * @param fetcherPrimary
     * @param fetcherSecondary
     */
    public DualConcurrentCachingFetcherWrapper(final ConcurrentCachingFetcherWrapper<T> fetcherPrimary, final ConcurrentCachingFetcherWrapper<T> fetcherSecondary) {
        this(fetcherPrimary, fetcherSecondary, DualConcurrentCachingFetcherWrapper.DEFAULT_ERROR_CALLBACK, DualConcurrentCachingFetcherWrapper.DEFAULT_TIMEOUT_CALLBACK,
                DualConcurrentCachingFetcherWrapper.DEFAULT_MAX_ATTEMPTS);
    }

    /**
     * @param fetcherPrimary
     * @param fetcherSecondary
     * @param errorCallback
     * @param timeoutCallback
     */
    public DualConcurrentCachingFetcherWrapper(final ConcurrentCachingFetcherWrapper<T> fetcherPrimary, final ConcurrentCachingFetcherWrapper<T> fetcherSecondary,
            final FetcherErrorCallback errorCallback, final FetcherErrorCallback timeoutCallback, final int maxAttempts) {
        this.fetcherPrimary = fetcherPrimary;
        this.fetcherSecondary = fetcherSecondary;
        this.errorCallback = errorCallback;
        this.timeoutCallback = timeoutCallback;
        this.maxAttempts = maxAttempts;
    }

    @Override
    public synchronized DualFetcherResponse<T> fetch() throws FetcherNotReadyException {

        try {
            return new DualFetcherResponse<T>(DualSource.PRIMARY, this.fetcherPrimary.fetch());
        } catch (final FetcherException e) {
            if (e.getCause().getClass().equals(FetcherNotReadyException.class)) {
                this.fetcherPrimary.clearPreviousException();
            } else {
                this.errorCallback.onError(e);
            }
        }

        try {
            return new DualFetcherResponse<T>(DualSource.SECONDARY, this.fetcherSecondary.fetch());
        } catch (final FetcherException e) {
            if (e.getCause().getClass().equals(FetcherNotReadyException.class)) {
                this.fetcherSecondary.clearPreviousException();
            } else {
                this.errorCallback.onError(e);
            }
        }

        if (this.countAttempts.getAndIncrement() >= maxAttempts) {
            timeoutCallback.onError(new FetcherNotReadyException("Max attempts reached for SubstitutingCachingFetcherWrapper"));
        }

        return this.fetch();

    }

}