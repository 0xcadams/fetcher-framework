package com.rentworthy.fetcher;

import com.rentworthy.fetcher.exception.FetcherErrorCallback;
import com.rentworthy.fetcher.exception.FetcherException;
import com.rentworthy.fetcher.response.TripleFetcherResponse;
import com.rentworthy.fetcher.response.source.TripleSource;

public class TripleWaterfallCachingFetcherWrapper<T> implements Fetcher<TripleFetcherResponse<T>> {

    private final static FetcherErrorCallback DEFAULT_ERROR_CALLBACK = e -> e.printStackTrace();

    private final FetcherErrorCallback errorCallback;

    private final CachingFetcherWrapper<T> fetcherOne;
    private final CachingFetcherWrapper<T> fetcherTwo;
    private final CachingFetcherWrapper<T> fetcherThree;

    public TripleWaterfallCachingFetcherWrapper(final CachingFetcherWrapper<T> fetcherOne,
                                                final CachingFetcherWrapper<T> fetcherTwo,
                                                final CachingFetcherWrapper<T> fetcherThree) {
        this.fetcherOne = fetcherOne;
        this.fetcherTwo = fetcherTwo;
        this.fetcherThree = fetcherThree;
        this.errorCallback = TripleWaterfallCachingFetcherWrapper.DEFAULT_ERROR_CALLBACK;
    }

    public TripleWaterfallCachingFetcherWrapper(final CachingFetcherWrapper<T> fetcherOne,
                                                final CachingFetcherWrapper<T> fetcherTwo,
                                                final CachingFetcherWrapper<T> fetcherThree,
                                                final FetcherErrorCallback errorCallback) {
        this.fetcherOne = fetcherOne;
        this.fetcherTwo = fetcherTwo;
        this.fetcherThree = fetcherThree;
        this.errorCallback = errorCallback;
    }

    @Override
    public synchronized TripleFetcherResponse<T> fetch() throws FetcherException {

        try {
            return new TripleFetcherResponse<T>(TripleSource.PRIMARY, this.fetcherOne.fetch());
        }
        catch (final FetcherException e) {

            try {
                this.errorCallback.onError(e);
                return new TripleFetcherResponse<T>(TripleSource.SECONDARY,
                                                    this.fetcherTwo.fetch());
            }
            catch (final FetcherException e2) {
                this.errorCallback.onError(e2);
                return new TripleFetcherResponse<T>(TripleSource.TERTIARY,
                                                    this.fetcherThree.fetch());
            }

        }

    }

}
