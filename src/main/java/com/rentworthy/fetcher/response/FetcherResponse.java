package com.rentworthy.fetcher.response;

import com.rentworthy.fetcher.response.source.Source;
import com.rentworthy.fetcher.response.source.UnlimitedSource;

public interface FetcherResponse<T> {

    public static <S> FetcherResponse<S> getFetcherResponse(final int rankFinal, final S value) {

        return new FetcherResponse<S>() {

            @Override
            public Source source() {
                return UnlimitedSource.valueOf(rankFinal);
            }

            @Override
            public S value() {
                return value;
            }

        };

    }

    public Source source();

    public T value();

}
