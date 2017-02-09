package com.rentworthy.fetcher.response;

import com.rentworthy.fetcher.response.source.Source;

public interface FetcherResponse<T> {

    public Source source();

    public T value();

}
