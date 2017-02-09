package com.rentworthy.fetcher.caching.multi;

import java.util.ArrayList;
import java.util.List;

import com.rentworthy.fetcher.caching.CachingFetcherWrapper;
import com.rentworthy.fetcher.exception.FetcherErrorCallback;
import com.rentworthy.fetcher.exception.FetcherException;
import com.rentworthy.fetcher.response.FetcherResponse;
import com.rentworthy.fetcher.response.MultiFetcher;
import com.rentworthy.fetcher.response.source.Source;
import com.rentworthy.fetcher.response.source.UnlimitedSource;

public class WaterfallCachingFetcherWrapper<T> implements MultiFetcher<T> {

    private final static FetcherErrorCallback DEFAULT_ERROR_CALLBACK = e -> e.printStackTrace();

    private final FetcherErrorCallback errorCallback;

    private final List<CachingFetcherWrapper<T>> fetchers;

    @SafeVarargs
    public WaterfallCachingFetcherWrapper(final CachingFetcherWrapper<T>... fetchers) {
        this(WaterfallCachingFetcherWrapper.DEFAULT_ERROR_CALLBACK, fetchers);
    }

    @SafeVarargs
    public WaterfallCachingFetcherWrapper(final FetcherErrorCallback errorCallback,
                                          final CachingFetcherWrapper<T>... fetchers) {

        this.fetchers = new ArrayList<>();

        for (final CachingFetcherWrapper<T> fetcher : fetchers) {
            this.fetchers.add(fetcher);
        }

        this.errorCallback = errorCallback;

    }

    @Override
    public synchronized FetcherResponse<T> fetch() throws FetcherException {

        if (this.fetchers.size() == 0) {
            throw new FetcherException("Number of fetchers was zero!");
        }

        for (int i = 0; i < (this.fetchers.size() - 1); i++) {

            final CachingFetcherWrapper<T> fetcher = this.fetchers.get(i);

            try {
                return FetcherResponse.getFetcherResponse(i + 1, fetcher.fetch());
            }
            catch (final FetcherException e) {
                this.errorCallback.onError(e);
            }

        }

        return FetcherResponse.getFetcherResponse(this.fetchers.size(),
            this.fetchers.get(this.fetchers.size() - 1).fetch());

    }

    

}
