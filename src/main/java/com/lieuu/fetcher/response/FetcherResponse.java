package com.lieuu.fetcher.response;

import com.lieuu.fetcher.response.source.Source;

public interface FetcherResponse<T> {

    public Source source();

    public T value();

}
