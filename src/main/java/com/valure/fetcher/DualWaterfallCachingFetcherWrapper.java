package com.valure.fetcher;

import com.valure.fetcher.exception.FetcherErrorCallback;
import com.valure.fetcher.exception.FetcherException;
import com.valure.fetcher.response.DualFetcherResponse;
import com.valure.fetcher.response.DualSource;

public class DualWaterfallCachingFetcherWrapper<T> implements Fetcher<DualFetcherResponse<T>> {

    private final static FetcherErrorCallback DEFAULT_ERROR_CALLBACK = e -> e.printStackTrace();

    private final FetcherErrorCallback errorCallback;

    private final CachingFetcherWrapper<T> fetcherOne;
    private final CachingFetcherWrapper<T> fetcherTwo;

    public DualWaterfallCachingFetcherWrapper(final CachingFetcherWrapper<T> fetcherOne,
                                              final CachingFetcherWrapper<T> fetcherTwo) {
        this.fetcherOne = fetcherOne;
        this.fetcherTwo = fetcherTwo;
        this.errorCallback = DualWaterfallCachingFetcherWrapper.DEFAULT_ERROR_CALLBACK;
    }

    public DualWaterfallCachingFetcherWrapper(final CachingFetcherWrapper<T> fetcherOne,
                                              final CachingFetcherWrapper<T> fetcherTwo,
                                              final FetcherErrorCallback errorCallback) {
        this.fetcherOne = fetcherOne;
        this.fetcherTwo = fetcherTwo;
        this.errorCallback = errorCallback;
    }

    @Override
    public synchronized DualFetcherResponse<T> fetch() throws FetcherException {

        try {
            return new DualFetcherResponse<T>(DualSource.PRIMARY, this.fetcherOne.fetch());
        }
        catch (final FetcherException e) {
            this.errorCallback.onError(e);
            return new DualFetcherResponse<T>(DualSource.PRIMARY, this.fetcherTwo.fetch());
        }

    }

}
