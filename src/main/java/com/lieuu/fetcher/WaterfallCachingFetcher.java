package com.lieuu.fetcher;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.lieuu.fetcher.exception.FetcherErrorCallback;
import com.lieuu.fetcher.exception.FetcherException;
import com.lieuu.fetcher.response.FetcherResponse;

class WaterfallCachingFetcher<T> implements MultiFetcher<T> {

    private final static FetcherErrorCallback DEFAULT_ERROR_CALLBACK = e -> e.printStackTrace();

    private final FetcherErrorCallback errorCallback;
    private final List<CachingFetcher<T>> fetchers;

    @SafeVarargs
    public WaterfallCachingFetcher(final CachingFetcher<T>... fetchers) {
        this(WaterfallCachingFetcher.DEFAULT_ERROR_CALLBACK, fetchers);
    }

    @SafeVarargs
    public WaterfallCachingFetcher(final FetcherErrorCallback errorCallback,
                                   final CachingFetcher<T>... fetchers) {
        this(errorCallback, Arrays.asList(fetchers));
    }

    public WaterfallCachingFetcher(final List<CachingFetcher<T>> fetchers) {
        this(WaterfallCachingFetcher.DEFAULT_ERROR_CALLBACK, fetchers);
    }

    public WaterfallCachingFetcher(final FetcherErrorCallback errorCallback,
                                   final List<CachingFetcher<T>> fetchers) {
        this.fetchers = Collections.unmodifiableList(fetchers);
        this.errorCallback = errorCallback;
    }

    @Override
    public FetcherResponse<T> fetch() throws FetcherException {

        if (this.fetchers.size() == 0) {
            throw new FetcherException("Number of fetchers was zero!");
        }

        for (int i = 0; i < (this.fetchers.size() - 1); i++) {

            final CachingFetcher<T> fetcher = this.fetchers.get(i);

            try {
                return FetcherResponseFactory.getFetcherResponse(i + 1, fetcher.fetch());
            }
            catch (final FetcherException e) {
                this.errorCallback.onError(e);
            }

        }

        return FetcherResponseFactory.getFetcherResponse(this.fetchers.size(),
            this.fetchers.get(this.fetchers.size() - 1).fetch());

    }

}
