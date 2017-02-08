package com.fetcher.response;

import com.fetcher.response.source.Source;

public interface FetcherResponse<T> {

    public Source source();

    public T value();

}
